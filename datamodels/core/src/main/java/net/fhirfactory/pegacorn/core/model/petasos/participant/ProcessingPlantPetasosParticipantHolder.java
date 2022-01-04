package net.fhirfactory.pegacorn.core.model.petasos.participant;

import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessingPlantPetasosParticipantHolder {

    private PetasosParticipant myProcessingPlantPetasosParticipant;

    //
    // Constructor(s)
    //

    public ProcessingPlantPetasosParticipantHolder(){
        this.myProcessingPlantPetasosParticipant = null;
    }

    //
    // Getters and Setters
    //

    public boolean hasMyProcessingPlantPetasosParticipant(){
        boolean hasValue = this.myProcessingPlantPetasosParticipant != null;
        return(hasValue);
    }

    public PetasosParticipant getMyProcessingPlantPetasosParticipant() {
        return myProcessingPlantPetasosParticipant;
    }

    public void setMyProcessingPlantPetasosParticipant(PetasosParticipant myProcessingPlantPetasosParticipant) {
        this.myProcessingPlantPetasosParticipant = myProcessingPlantPetasosParticipant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "ProcessingPlantPetasosParticipantHolder{" +
                "myProcessingPlantPetasosParticipant=" + myProcessingPlantPetasosParticipant +
                '}';
    }
}
