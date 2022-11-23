package net.fhirfactory.pegacorn.core.model.petasos.participant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;

public class PetasosParticipantRegistrationSet implements Serializable {
    private HashMap<String, PetasosParticipantRegistration> registrationSet;

    //
    // Constructor(s)
    //

    public PetasosParticipantRegistrationSet(){
        this.registrationSet = new HashMap<>();
    }

    //
    // Getters and Setters
    //

    public HashMap<String, PetasosParticipantRegistration> getRegistrationSet() {
        return registrationSet;
    }

    public void setRegistrationSet(HashMap<String, PetasosParticipantRegistration> registrationSet) {
        this.registrationSet = registrationSet;
    }

    @JsonIgnore
    public void addRegistration(PetasosParticipantRegistration registration){
        if(registration != null){
            if(StringUtils.isNotEmpty(registration.getParticipant().getParticipantName())){
                registrationSet.put(registration.getParticipant().getParticipantName(), registration);
            }
        }
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PetasosParticipantRegistrationSet{");
        sb.append("registrationSet=").append(registrationSet);
        sb.append('}');
        return sb.toString();
    }
}
