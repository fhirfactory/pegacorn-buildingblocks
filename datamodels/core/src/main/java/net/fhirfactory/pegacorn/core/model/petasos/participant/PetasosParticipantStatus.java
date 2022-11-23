package net.fhirfactory.pegacorn.core.model.petasos.participant;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class PetasosParticipantStatus implements Serializable {
    private String participantName;
    private PetasosParticipantControlStatusEnum controlStatus;
    private PetasosParticipantStatusEnum operationalStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant updateInstant;


    //
    // Constructor(s)
    //

    public PetasosParticipantStatus(){
        this.participantName = null;
        this.controlStatus = null;
        this.operationalStatus = null;
        this.updateInstant = Instant.EPOCH;
    }

    //
    // Getters and Setters
    //

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public PetasosParticipantControlStatusEnum getControlStatus() {
        return controlStatus;
    }

    public void setControlStatus(PetasosParticipantControlStatusEnum controlStatus) {
        this.controlStatus = controlStatus;
    }

    public PetasosParticipantStatusEnum getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(PetasosParticipantStatusEnum operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public Instant getUpdateInstant() {
        return updateInstant;
    }

    public void setUpdateInstant(Instant updateInstant) {
        this.updateInstant = updateInstant;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PetasosParticipantStatus{");
        sb.append("participantName='").append(participantName).append('\'');
        sb.append(", controlStatus=").append(controlStatus);
        sb.append(", operationalStatus=").append(operationalStatus);
        sb.append(", updateInstant=").append(updateInstant);
        sb.append('}');
        return sb.toString();
    }

    //
    // Hash & Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetasosParticipantStatus that = (PetasosParticipantStatus) o;
        return Objects.equals(getParticipantName(), that.getParticipantName()) && getControlStatus() == that.getControlStatus() && getOperationalStatus() == that.getOperationalStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParticipantName());
    }
}
