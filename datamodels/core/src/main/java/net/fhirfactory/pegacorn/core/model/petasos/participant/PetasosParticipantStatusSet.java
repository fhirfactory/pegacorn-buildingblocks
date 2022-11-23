package net.fhirfactory.pegacorn.core.model.petasos.participant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;

public class PetasosParticipantStatusSet implements Serializable {
    private HashMap<String, PetasosParticipantStatus> statusMap;

    //
    // Constructor(s)
    //

    public PetasosParticipantStatusSet(){
        this.statusMap = new HashMap<>();
    }

    //
    // Getters and Setters
    //

    public HashMap<String, PetasosParticipantStatus> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(HashMap<String, PetasosParticipantStatus> statusMap) {
        this.statusMap = statusMap;
    }

    @JsonIgnore
    public void addStatus(PetasosParticipantStatus status){
        if(status != null){
            statusMap.put(status.getParticipantName(), status);
        }
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PetasosParticipantStatusSet{");
        sb.append("statusMap=").append(statusMap);
        sb.append('}');
        return sb.toString();
    }
}
