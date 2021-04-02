package net.fhirfactory.pegacorn.internals.directories.cache;

import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDESet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PractitionerRoleMapCache {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleMapCache.class);

    ConcurrentHashMap<IdentifierDE, IdentifierDESet> practitionerRolesBeingFulfilledByAPractitionerMap;
    ConcurrentHashMap<IdentifierDE, IdentifierDESet> practitionersFulfillingPractitionerRoleMap;

    public PractitionerRoleMapCache(){
        this.practitionersFulfillingPractitionerRoleMap = new ConcurrentHashMap<>();
        this.practitionerRolesBeingFulfilledByAPractitionerMap = new ConcurrentHashMap<>();
    }

    protected Logger getLogger(){
        return(LOG);
    }

    public void addPractitionerRoleFulfilledByPractitioner(IdentifierDE practitionerRoleIdentifier, IdentifierDE practitionerIdentifier){
        getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Entry, practitionerRoleIdentifier --> {}, practitionerIdentifier -->{}", practitionerRoleIdentifier, practitionerIdentifier);
        if(practitionerIdentifier == null || practitionerRoleIdentifier == null){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Exit, either practitionerRoleIdentifier or practitionerIdentifier are null");
            return;
        }
        addPractitionerIfAbsent(practitionerIdentifier);
        addPractitionerRoleIfAbsent(practitionerRoleIdentifier);
        practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerIdentifier).getIdentifiers();
        if(!practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerIdentifier).getIdentifiers().contains(practitionerRoleIdentifier)){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): adding practitionerRole to PractitionerRolesBeingFulfilledByAPractitionerMap --> {}", practitionerRoleIdentifier);
            practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerIdentifier).getIdentifiers().add(practitionerRoleIdentifier);
        }
        if(!practitionersFulfillingPractitionerRoleMap.get(practitionerRoleIdentifier).getIdentifiers().contains(practitionerIdentifier)){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): adding practitioner to PractitionerFulfillingPractitionerRoleMap --> {}", practitionerIdentifier);
            practitionersFulfillingPractitionerRoleMap.get(practitionerRoleIdentifier).getIdentifiers().add(practitionerIdentifier);
        }
        getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Exit");
    }

    public void removePractitionerRoleFulfilledByPractitioner(IdentifierDE practitionerRoleIdentifier, IdentifierDE practitionerIdentifier){
        getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Entry, practitionerIdentifier --> {}, practitionerRoleIdentifier -->{}", practitionerIdentifier, practitionerRoleIdentifier);
        if(practitionerIdentifier == null || practitionerRoleIdentifier == null){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Exit, either practitionerRoleIdentifier or practitionerIdentifier are null");
            return;
        }
        if(this.practitionersFulfillingPractitionerRoleMap.containsKey(practitionerRoleIdentifier)){
            practitionersFulfillingPractitionerRoleMap.get(practitionerRoleIdentifier).getIdentifiers().remove(practitionerIdentifier);
            if(practitionersFulfillingPractitionerRoleMap.get(practitionerRoleIdentifier).getIdentifiers().isEmpty()){
                practitionersFulfillingPractitionerRoleMap.remove(practitionerRoleIdentifier);
            }
        }
        if(this.practitionerRolesBeingFulfilledByAPractitionerMap.containsKey(practitionerIdentifier)) {
            practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerIdentifier).getIdentifiers().remove(practitionerRoleIdentifier);
            if (practitionerRolesBeingFulfilledByAPractitionerMap.get(practitionerIdentifier).getIdentifiers().isEmpty()) {
                practitionerRolesBeingFulfilledByAPractitionerMap.remove(practitionerIdentifier);
            }
        }
        getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Entry");
    }

    public List<IdentifierDE> getListOfPractitionerRolesFulfilledByPractitioner(IdentifierDE practitioner){
        getLogger().info(".getListOfPractitionerRolesFulfilledByPractitioner(): Entry, practitionerIdentifier --> {}", practitioner);
        ArrayList<IdentifierDE> practitionerRoleList = new ArrayList<>();
        if(practitioner == null){
            getLogger().info(".addPractitionerRoleFulfilledByPractitioner(): Exit, practitionerIdentifier is null");
            return(practitionerRoleList);
        }
        if(this.practitionerRolesBeingFulfilledByAPractitionerMap == null){
            getLogger().error("warning Will Robinson....");
        }
        if(practitionerRolesBeingFulfilledByAPractitionerMap.containsKey(practitioner)){
            practitionerRoleList.addAll(practitionerRolesBeingFulfilledByAPractitionerMap.get(practitioner).getIdentifiers());
        }
        getLogger().info(".getListOfPractitionerRolesFulfilledByPractitioner(): Exit");
        return(practitionerRoleList);
    }

    public List<IdentifierDE> getListOfPractitionersFulfillingPractitionerRole(IdentifierDE practitionerRole){
        getLogger().info(".getListOfPractitionersFulfillingPractitionerRole(): Entry, practitionerRoleIdentifier --> {}", practitionerRole);
        ArrayList<IdentifierDE> practitionerList = new ArrayList<>();
        if(practitionerRole == null){
            getLogger().info(".getListOfPractitionersFulfillingPractitionerRole(): Exit, practitionerRoleIdentifier is null");
            return(practitionerList);
        }
        if(practitionersFulfillingPractitionerRoleMap.containsKey(practitionerRole)){
            practitionerList.addAll(practitionersFulfillingPractitionerRoleMap.get(practitionerRole).getIdentifiers());
        }
        getLogger().info(".getListOfPractitionersFulfillingPractitionerRole(): Exit");
        return(practitionerList);
    }

    public void addPractitionerIfAbsent(IdentifierDE practitionerIdentifier){
        getLogger().info(".addPractitioner(): Entry, practitionerIdentifier --> {}", practitionerIdentifier);
        if(practitionerIdentifier == null){
            getLogger().info(".addPractitioner(): Exit, practitionerIdentifier is null");
            return;
        }
        if(!practitionerRolesBeingFulfilledByAPractitionerMap.containsKey(practitionerIdentifier)){
            IdentifierDESet practitionerRoleList = new IdentifierDESet();
            practitionerRolesBeingFulfilledByAPractitionerMap.putIfAbsent(practitionerIdentifier, practitionerRoleList);
        }
        getLogger().info(".addPractitioner(): Exit");
    }

    public void addPractitionerRoleIfAbsent(IdentifierDE practitionerRoleIdentifier){
        getLogger().info(".addPractitionerRole(): Entry, practitionerRoleIdentifier --> {}", practitionerRoleIdentifier);
        if(practitionerRoleIdentifier == null){
            getLogger().info(".addPractitionerRole(): Exit, practitionerRoleIdentifier is null");
            return;
        }
        if(!practitionersFulfillingPractitionerRoleMap.containsKey(practitionerRoleIdentifier)){
            IdentifierDESet practitionerList = new IdentifierDESet();
            practitionersFulfillingPractitionerRoleMap.putIfAbsent(practitionerRoleIdentifier, practitionerList);
        }
        getLogger().info(".addPractitionerRole(): Exit");
    }
}
