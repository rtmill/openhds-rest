package org.openhds.service.impl.census;

import org.openhds.domain.model.census.Individual;
import org.openhds.domain.model.census.LocationHierarchy;
import org.openhds.domain.model.census.Relationship;
import org.openhds.domain.model.census.Residency;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.census.RelationshipRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.management.relation.Relation;
import javax.persistence.criteria.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Wolfe on 7/13/2015.
 */
@Service
public class RelationshipService extends AbstractAuditableCollectedService<Relationship, RelationshipRepository> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private LocationHierarchyService locationHierarchyService;

    @Autowired
    public RelationshipService(RelationshipRepository repository) {
        super(repository);
    }

    @Override
    public Relationship makePlaceHolder(String id, String name) {
        Relationship relationship = new Relationship();
        relationship.setUuid(id);
        relationship.setRelationshipType(name);
        relationship.setStartDate(ZonedDateTime.now());
        relationship.setIndividualA(individualService.getUnknownEntity());
        relationship.setIndividualB(individualService.getUnknownEntity());

        initPlaceHolderCollectedFields(relationship);

        return relationship;
    }

    public Relationship recordRelationship(Relationship relationship, String individualAId, String individualBId, String fieldWorkerId){
        relationship.setIndividualA(individualService.findOrMakePlaceHolder(individualAId));
        relationship.setIndividualB(individualService.findOrMakePlaceHolder(individualBId));
        relationship.setCollectedBy(fieldWorkerService.findOrMakePlaceHolder(fieldWorkerId));
        return createOrUpdate(relationship);
    }

    @Override
    public void validate(Relationship relationship, ErrorLog errorLog) {
        super.validate(relationship, errorLog);

      if(relationship.getStartDate().isAfter(relationship.getCollectionDateTime())){
        errorLog.appendError("Relationship cannot have a startDate in the future.");
      }

      if(null != relationship.getEndDate() &&
          relationship.getStartDate().isAfter(relationship.getEndDate())){
        errorLog.appendError("Relationship cannot have a startDate before its endDate.");
      }

      if(!projectCodeService.isValueInCodeGroup(relationship.getRelationshipType(), projectCodeService.RELATIONSHIP_TYPE)) {
        errorLog.appendError("Relationship cannot have a type of: ["+relationship.getRelationshipType()+"].");
      }

      Iterator<Relationship> relationshipIterator = findByIndividualAAndIndividualB(UUID_SORT, relationship.getIndividualA(), relationship.getIndividualB()).iterator();
      Relationship existingRelationship;

      while(relationshipIterator.hasNext()){
        existingRelationship = relationshipIterator.next();

        if(null != relationship.getUuid()
            && !relationship.getUuid().equals(existingRelationship.getUuid())
            && null == existingRelationship.getEndDate()
            && relationship.getRelationshipType().equals(existingRelationship.getRelationshipType())){

          errorLog.appendError("Individual cannot have two Relationships to same person of the same type.");
        }
      }

    }

    // all hierarchies associated with active residencies for either individual
    @Override
    public Set<LocationHierarchy> findEnclosingLocationHierarchies(Relationship entity) {
        Set<LocationHierarchy> locationHierarchies = new HashSet<>();

        for (Residency residency : entity.getIndividualA().collectActiveResidencies(new HashSet<>())) {
            locationHierarchies.addAll(locationHierarchyService.findEnclosingLocationHierarchies(residency.getLocation().getLocationHierarchy()));
        }

        for (Residency residency : entity.getIndividualB().collectActiveResidencies(new HashSet<>())) {
            locationHierarchies.addAll(locationHierarchyService.findEnclosingLocationHierarchies(residency.getLocation().getLocationHierarchy()));
        }

        return locationHierarchies;
    }

    @Override
    public Page<Relationship> findByEnclosingLocationHierarchy(Pageable pageable,
                                                               String locationHierarchyUuid,
                                                               ZonedDateTime modifiedAfter,
                                                               ZonedDateTime modifiedBefore) {
        return locationHierarchyService.findOtherByEnclosingLocationHierarchy(pageable,
            locationHierarchyUuid,
            modifiedAfter,
            modifiedBefore,
            RelationshipService::enclosed,
            repository);
    }

    // relationships where either individual has an active residency at an enclosed location
    private static Specification<Relationship> enclosed(final List<LocationHierarchy> enclosing) {
        return (root, query, cb) -> {
            Predicate individualAPredicate = hasEnclosedResidency(root.join("individualA"),
                    query,
                    cb,
                    enclosing);
            Predicate individualBPredicate = hasEnclosedResidency(root.join("individualB"),
                    query,
                    cb,
                    enclosing);
            return cb.or(individualAPredicate, individualBPredicate);
        };
    }

    private static Predicate hasEnclosedResidency(Join<Relationship, Individual> individualJoin,
                                                  CriteriaQuery query,
                                                  CriteriaBuilder cb,
                                                  final List<LocationHierarchy> enclosing) {

        Join<Individual, Residency> residencyJoin = individualJoin.join("residencies");

        Subquery<Residency> subquery = query.subquery(Residency.class);
        subquery.from(Residency.class);
        subquery.select(residencyJoin);
        subquery.where(cb.and(cb.isNull(residencyJoin.get("endDate")),
                residencyJoin.get("location").get("locationHierarchy").in(enclosing)));

        return cb.exists(subquery);
    }


    public EntityIterator<Relationship> findByIndividualA(Sort sort, Individual individualA) {
      return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndIndividualA(individualA, pageable), sort);
    }

    public EntityIterator<Relationship> findByIndividualB(Sort sort, Individual individualB) {
      return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndIndividualB(individualB, pageable), sort);
    }

    public EntityIterator<Relationship> findByIndividualAAndIndividualB(Sort sort, Individual individualA, Individual individualB) {
      return iteratorFromPageable(pageable -> repository.findByDeletedFalseAndIndividualAAndIndividualB(individualA, individualB, pageable), sort);
    }

}
