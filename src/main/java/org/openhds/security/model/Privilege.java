package org.openhds.security.model;

import org.openhds.Description;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Dave Roberge
 */
@Description(description = "A Privilege represents a right granted to access particular service methods.")
@Entity
@Table(name = "privilege")
public class Privilege implements Serializable {

    private static final long serialVersionUID = -5969044695942713833L;

    public enum Grant {
        CREATE_ENTITY,
        VIEW_ENTITY,
        EDIT_ENTITY,
        DELETE_ENTITY,
        ACCESS_CENSUS,
        ACCESS_UPDATE,
        CREATE_USER,
        DELETE_USER
    }

    public Privilege() {
    }

    public Privilege(Grant grant) {
        setGrant(grant);
    }

    @Description(description = "The access right granted by this Privilege.")
    @Id
    @Enumerated(EnumType.STRING)
    private Grant grant;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Privilege)) {
            return false;
        }

        return grant.equals(((Privilege) obj).grant);
    }

    @Override
    public int hashCode() {
        if (grant == null) {
            return super.hashCode();
        }
        return grant.hashCode();
    }

    @Override
    public String toString() {
        return grant.toString();
    }
}
