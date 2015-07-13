package org.openhds.domain.model;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A record of an Individual belonging to a SocialGroup for some time interval.")
@Entity
@Table(name = "membership")
public class Membership extends AuditableCollectedEntity {

    @ManyToOne
    @JoinColumn(name = "individual")
    @Description(description="Individual the membership is associated with, identified by external id.")
    private Individual individual;

}
