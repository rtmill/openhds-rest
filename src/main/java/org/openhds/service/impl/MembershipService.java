package org.openhds.service.impl;

import org.openhds.domain.model.Membership;
import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.MembershipRepository;
import org.openhds.service.contract.AbstractAuditableCollectedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/1/2015.
 */
@Component
public class MembershipService extends AbstractAuditableCollectedService<Membership, MembershipRepository> {

    @Autowired
    private IndividualService individualService;

    @Autowired
    private SocialGroupService socialGroupService;

    @Autowired
    private FieldWorkerService fieldWorkerService;

    @Autowired
    public MembershipService(MembershipRepository membershipRepository) {
        super(membershipRepository);
    }

    @Override
    protected Membership makeUnknownEntity() {
        Membership membership = new Membership();

        membership.setCollectedBy(fieldWorkerService.getUnknownEntity());
        membership.setCollectionDateTime(ZonedDateTime.now());

        membership.setSocialGroup(socialGroupService.getUnknownEntity());
        membership.setIndividual(individualService.getUnknownEntity());
        membership.setStartDate(ZonedDateTime.now().minusYears(1));
        membership.setStartType("unknown");
        membership.setRelationshipToGroupHead("unknown");

        return membership;
    }

    @Override
    public void validate(Membership membership, ErrorLog errorLog) {
        super.validate(membership, errorLog);
    }

}
