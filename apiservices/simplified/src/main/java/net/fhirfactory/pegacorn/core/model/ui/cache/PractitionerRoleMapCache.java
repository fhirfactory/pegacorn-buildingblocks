package net.fhirfactory.pegacorn.core.model.ui.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PractitionerRoleMapCache {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleMapCache.class);

    ConcurrentHashMap<String, ArrayList<String>> practitionerRolesBeingFulfilledByAPractitionerMap;
    ConcurrentHashMap<String, ArrayList<String>> practitionersFulfillingPractitionerRoleMap;

    public PractitionerRoleMapCache(){
        this.practitionersFulfillingPractitionerRoleMap = new ConcurrentHashMap<>();
        this.practitionerRolesBeingFulfilledByAPractitionerMap = new ConcurrentHashMap<>();
    }

    protected Logger getLogger(){
        return(LOG);
    }

    public void addPractitionerRoleFulfilledByPractitioner(String practitionerRoleRecordID, String practitionerRecordID){
        getLogger().debug(".addPractitionerRoleFulfilledByPractitioner(): Entry, practitionerRoleRecordID --> {}, practitionerRecordID -->{}", practitionerRoleRecordID, practitionerRecordID);
        if(practitionerRecordID == null || practitionerRoleRecordID == null){
            getLogger().debug(".addPractitionerRoleFulfilledByPractitioner(): Exit, either practitionerRoleRecordID or practitionerRecordID are null");
            return;
        }
        addPractitionerIfAbsent(practitionerRecordID);
        addPractitionerRoleIfAbsent(practitionerRoleRecordID);
        if(!practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerRecordID).contains(practitionerRoleRecordID)){
            getLogger().trace(".addPractitionerRoleFulfilledByPractitioner(): adding practitionerRole to PractitionerRolesBeingFulfilledByAPractitionerMap --> {}", practitionerRoleRecordID);
            practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerRecordID).add(practitionerRoleRecordID);
        }
        if(!practitionersFulfillingPractitionerRoleMap.get(practitionerRoleRecordID).contains(practitionerRecordID)){
            getLogger().trace(".addPractitionerRoleFulfilledByPractitioner(): adding practitioner to PractitionerFulfillingPractitionerRoleMap --> {}", practitionerRecordID);
            practitionersFulfillingPractitionerRoleMap.get(practitionerRoleRecordID).add(practitionerRecordID);
        }
        getLogger().debug(".addPractitionerRoleFulfilledByPractitioner(): Exit");
    }

    public void removePractitionerRoleFulfilledByPractitioner(String practitionerRoleRecordID, String practitionerRecordID){
        getLogger().debug(".removePractitionerRoleFulfilledByPractitioner(): Entry, practitionerRecordID --> {}, practitionerRoleRecordID -->{}", practitionerRecordID, practitionerRoleRecordID);
        if(practitionerRecordID == null || practitionerRoleRecordID == null){
            getLogger().debug(".removePractitionerRoleFulfilledByPractitioner(): Exit, either practitionerRoleRecordID or practitionerRecordID are null");
            return;
        }
        if(this.practitionersFulfillingPractitionerRoleMap.containsKey(practitionerRoleRecordID)){
            practitionersFulfillingPractitionerRoleMap.get(practitionerRoleRecordID).remove(practitionerRecordID);
            if(practitionersFulfillingPractitionerRoleMap.get(practitionerRoleRecordID).isEmpty()){
                practitionersFulfillingPractitionerRoleMap.remove(practitionerRoleRecordID);
            }
        }
        if(this.practitionerRolesBeingFulfilledByAPractitionerMap.containsKey(practitionerRecordID)) {
            practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerRecordID).remove(practitionerRoleRecordID);
            if (practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerRecordID).isEmpty()) {
                practitionerRolesBeingFulfilledByAPractitionerMap.remove(practitionerRecordID);
            }
        }
        getLogger().debug(".removePractitionerRoleFulfilledByPractitioner(): Entry");
    }

    public List<String> getListOfPractitionerRolesFulfilledByPractitioner(String practitionerRecordID){
        getLogger().debug(".getListOfPractitionerRolesFulfilledByPractitioner(): Entry, practitionerRecordID --> {}", practitionerRecordID);
        ArrayList<String> practitionerRoleList = new ArrayList<>();
        if(practitionerRecordID == null){
            getLogger().debug(".addPractitionerRoleFulfilledByPractitioner(): Exit, practitionerRecordID is null");
            return(practitionerRoleList);
        }
        if(this.practitionerRolesBeingFulfilledByAPractitionerMap == null){
            getLogger().warn("warning Will Robinson....");
        }
        if(practitionerRolesBeingFulfilledByAPractitionerMap.containsKey(practitionerRecordID)){
            practitionerRoleList.addAll(practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerRecordID));
        }
        getLogger().debug(".getListOfPractitionerRolesFulfilledByPractitioner(): Exit");
        return(practitionerRoleList);
    }

    public List<String> getListOfPractitionersFulfillingPractitionerRole(String practitionerRoleRecordID){
        getLogger().debug(".getListOfPractitionersFulfillingPractitionerRole(): Entry, practitionerRoleRecordID --> {}", practitionerRoleRecordID);
        ArrayList<String> practitionerList = new ArrayList<>();
        if(practitionerRoleRecordID == null){
            getLogger().debug(".getListOfPractitionersFulfillingPractitionerRole(): Exit, practitionerRoleIdentifier is null");
            return(practitionerList);
        }
        if(practitionersFulfillingPractitionerRoleMap.containsKey(practitionerRoleRecordID)){
            practitionerList.addAll(practitionersFulfillingPractitionerRoleMap.get(practitionerRoleRecordID));
        }
        getLogger().debug(".getListOfPractitionersFulfillingPractitionerRole(): Exit");
        return(practitionerList);
    }

    public void addPractitionerIfAbsent(String practitionerRecordID){
        getLogger().debug(".addPractitioner(): Entry, practitionerRecordID --> {}", practitionerRecordID);
        if(practitionerRecordID == null){
            getLogger().debug(".addPractitioner(): Exit, practitionerRecordID is null");
            return;
        }
        if(!practitionerRolesBeingFulfilledByAPractitionerMap.containsKey(practitionerRecordID)){
            ArrayList<String> practitionerRoleList = new ArrayList<>();
            practitionerRolesBeingFulfilledByAPractitionerMap.putIfAbsent(practitionerRecordID, practitionerRoleList);
        }
        getLogger().debug(".addPractitioner(): Exit");
    }

    public void addPractitionerRoleIfAbsent(String practitionerRoleRecordID){
        getLogger().debug(".addPractitionerRole(): Entry, practitionerRoleRecordID --> {}", practitionerRoleRecordID);
        if(practitionerRoleRecordID == null){
            getLogger().debug(".addPractitionerRole(): Exit, practitionerRoleRecordID is null");
            return;
        }
        if(!practitionersFulfillingPractitionerRoleMap.containsKey(practitionerRoleRecordID)){
            ArrayList<String> practitionerList = new ArrayList<>();
            practitionersFulfillingPractitionerRoleMap.putIfAbsent(practitionerRoleRecordID, practitionerList);
        }
        getLogger().debug(".addPractitionerRole(): Exit");
    }
}
