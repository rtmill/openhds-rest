package org.openhds.domain.model.census;

import org.openhds.domain.contract.AuditableCollectedEntity;
import org.openhds.domain.util.Description;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by bsh on 7/13/15.
 */
@Description(description = "A record of an Individual living at a Location for some time interval.")
@Entity
@Table(name = "residency", indexes={@Index(columnList = "lastModifiedDate")})
public class Residency extends AuditableCollectedEntity implements Serializable {

    private static final long serialVersionUID = -8660806978131352923L;

    @NotNull(message = "Residency cannot have a null startdate.")
    @Description(description = "Residency start date.")
    ZonedDateTime startDate;

    @NotNull(message = "Residency cannot have a null startType.")
    @Description(description = "Residency start type.")
    String startType;

    @Description(description = "Residency end date.")
    ZonedDateTime endDate;

    @Description(description = "Residency end type.")
    String endType;

    @NotNull(message = "Residency cannot have a null individual.")
    @ManyToOne
    @Description(description = "Individual who resides at the Location.")
    private Individual individual;

    @NotNull(message = "Residency cannot have a null location.")
    @ManyToOne
    @Description(description = "Location where the Individual resides.")
    private Location location;

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public String getStartType() {
        return startType;
    }

    public void setStartType(String startType) {
        this.startType = startType;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }

    @Override
    public String toString() {
        return "Residency{" +
                "individual=" + individual +
                ", location=" + location +
                ", startDate=" + startDate +
                ", startType='" + startType + '\'' +
                ", endDate=" + endDate +
                ", endType='" + endType + '\'' +
                "} " + super.toString();
    }
}
