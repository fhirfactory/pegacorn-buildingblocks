package net.fhirfactory.pegacorn.core.model.petasos.participant;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessingPlantPetasosParticipantNameHolder {
    private String subsystemParticipantName;

    public String getSubsystemParticipantName() {
        return subsystemParticipantName;
    }

    public void setSubsystemParticipantName(String subsystemParticipantName) {
        this.subsystemParticipantName = subsystemParticipantName;
    }

    @Override
    public String toString() {
        return "ParticipantNameHolder{" +
                "subsystemParticipantName='" + subsystemParticipantName + '\'' +
                '}';
    }
}
