package net.fhirfactory.pegacorn.internals.esr.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Entry, practitionerRoleRecordID --> {}, practitionerRecordID -->{}", practitionerRoleRecordID, practitionerRecordID);
        if(practitionerRecordID == null || practitionerRoleRecordID == null){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Exit, either practitionerRoleRecordID or practitionerRecordID are null");
            return;
        }
        addPractitionerIfAbsent(practitionerRecordID);
        addPractitionerRoleIfAbsent(practitionerRoleRecordID);
        if(!practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerRecordID).contains(practitionerRoleRecordID)){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): adding practitionerRole to PractitionerRolesBeingFulfilledByAPractitionerMap --> {}", practitionerRoleRecordID);
            practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerRecordID).add(practitionerRoleRecordID);
        }
        if(!practitionersFulfillingPractitionerRoleMap.get(practitionerRoleRecordID).contains(practitionerRecordID)){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): adding practitioner to PractitionerFulfillingPractitionerRoleMap --> {}", practitionerRecordID);
            practitionersFulfillingPractitionerRoleMap.get(practitionerRoleRecordID).add(practitionerRecordID);
        }
        getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Exit");
    }

    public void removePractitionerRoleFulfilledByPractitioner(String practitionerRoleRecordID, String practitionerRecordID){
        getLogger().info(".removePractitionerRoleFulfilledByPractitioner(): Entry, practitionerRecordID --> {}, practitionerRoleRecordID -->{}", practitionerRecordID, practitionerRoleRecordID);
        if(practitionerRecordID == null || practitionerRoleRecordID == null){
            getLogger().info(".removePractitionerRoleFulfilledByPractitioner(): Exit, either practitionerRoleRecordID or practitionerRecordID are null");
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
        getLogger().info(".removePractitionerRoleFulfilledByPractitioner(): Entry");
    }

    public List<String> getListOfPractitionerRolesFulfilledByPractitioner(String practitionerRecordID){
        getLogger().info(".getListOfPractitionerRolesFulfilledByPractitioner(): Entry, practitionerRecordID --> {}", practitionerRecordID);
        ArrayList<String> practitionerRoleList = new ArrayList<>();
        if(practitionerRecordID == null){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Exit, practitionerRecordID is null");
            return(practitionerRoleList);
        }
        if(this.practitionerRolesBeingFulfilledByAPractitionerMap == null){
            getLogger().error("warning Will Robinson....");
        }
        if(practitionerRolesBeingFulfilledByAPractitionerMap.containsKey(practitionerRecordID)){
            practitionerRoleList.addAll(practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerRecordID));
        }
        getLogger().info(".getListOfPractitionerRolesFulfilledByPractitioner(): Exit");
        return(practitionerRoleList);
    }

    public List<String> getListOfPractitionersFulfillingPractitionerRole(String practitionerRoleRecordID){
        getLogger().info(".getListOfPractitionersFulfillingPractitionerRole(): Entry, practitionerRoleRecordID --> {}", practitionerRoleRecordID);
        ArrayList<String> practitionerList = new ArrayList<>();
        if(practitionerRoleRecordID == null){
            getLogger().info(".getListOfPractitionersFulfillingPractitionerRole(): Exit, practitionerRoleIdentifier is null");
            return(practitionerList);
        }
        if(practitionersFulfillingPractitionerRoleMap.containsKey(practitionerRoleRecordID)){
            practitionerList.addAll(practitionersFulfillingPractitionerRoleMap.get(practitionerRoleRecordID));
        }
        getLogger().info(".getListOfPractitionersFulfillingPractitionerRole(): Exit");
        return(practitionerList);
    }

    public void addPractitionerIfAbsent(String practitionerRecordID){
        getLogger().info(".addPractitioner(): Entry, practitionerRecordID --> {}", practitionerRecordID);
        if(practitionerRecordID == null){
            getLogger().info(".addPractitioner(): Exit, practitionerRecordID is null");
            return;
        }
        if(!practitionerRolesBeingFulfilledByAPractitionerMap.containsKey(practitionerRecordID)){
            ArrayList<String> practitionerRoleList = new ArrayList<>();
            practitionerRolesBeingFulfilledByAPractitionerMap.putIfAbsent(practitionerRecordID, practitionerRoleList);
        }
        getLogger().info(".addPractitioner(): Exit");
    }

    public void addPractitionerRoleIfAbsent(String practitionerRoleRecordID){
        getLogger().info(".addPractitionerRole(): Entry, practitionerRoleRecordID --> {}", practitionerRoleRecordID);
        if(practitionerRoleRecordID == null){
            getLogger().info(".addPractitionerRole(): Exit, practitionerRoleRecordID is null");
            return;
        }
        if(!practitionersFulfillingPractitionerRoleMap.containsKey(practitionerRoleRecordID)){
            ArrayList<String> practitionerList = new ArrayList<>();
            practitionersFulfillingPractitionerRoleMap.putIfAbsent(practitionerRoleRecordID, practitionerList);
        }
        getLogger().info(".addPractitionerRole(): Exit");
    }
}
