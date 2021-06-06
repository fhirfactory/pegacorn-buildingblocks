package net.fhirfactory.pegacorn.internals.esr.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.ParticipantESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.ParticipantRoleCareTeam;

@ApplicationScoped
public class CareTeamPractitionerRoleMapCache {
	private static final Logger LOG = LoggerFactory.getLogger(CareTeamPractitionerRoleMapCache.class);

    private ConcurrentHashMap<String, List<ParticipantESDT>> practitionerRolesInCareTeamMap;
    private ConcurrentHashMap<String, List<ParticipantRoleCareTeam>> careTeamsContainingPractitionerRoleMap;

    public CareTeamPractitionerRoleMapCache(){
        this.practitionerRolesInCareTeamMap = new ConcurrentHashMap<>(); // The practitioners in the care teams
        this.careTeamsContainingPractitionerRoleMap = new ConcurrentHashMap<>(); // The care teams for a practitioner
    }

    protected Logger getLogger(){
        return(LOG);
    }

    
    /**
     * Adds a practitioner role to a care team.
     * 
     * @param careTeamRecordId
     * @param practitionerRoleRecordId
     * @param participantType
     * @throws ResourceInvalidSearchException 
     */
    public void addPractitionerRoleToCareTeam(String careTeamRecordId, ParticipantESDT practitionerRole)  {
    
    	// Add a practitioner role to the care team
    	addCareTeamIfAbsent(careTeamRecordId);
    	List<ParticipantESDT> practitionerRoles = practitionerRolesInCareTeamMap.get(careTeamRecordId);
    	
	    ParticipantESDT newPractitionerRole = new ParticipantESDT(practitionerRole.getSimplifiedID(), practitionerRole.getParticipantType());
	    
	    if (!practitionerRoles.contains(newPractitionerRole)) {
	    	practitionerRoles.add(newPractitionerRole);
	    }
   	 	
   	 	
   	 	// Now add the care team to the practitioner role
   	 	addPractitionerRoleIfAbsent(practitionerRole.getSimplifiedID());
		List<ParticipantRoleCareTeam> careTeams = careTeamsContainingPractitionerRoleMap.get(practitionerRole.getSimplifiedID());
		
		ParticipantRoleCareTeam newCareTeam = new ParticipantRoleCareTeam(careTeamRecordId, practitionerRole.getParticipantType());
		
		if (!careTeams.contains(newCareTeam)) {
			careTeams.add(newCareTeam);
		}
    }

    
    /**
     * Gets the practitioner roles within the care team.
     * 
     * @param careTeamRecordId
     * @return
     * @throws ResourceInvalidSearchException
     */
    public List<ParticipantESDT>getPractitionerRolesWithinCareTeam(String careTeamRecordId) {
    	 List<ParticipantESDT> practitionerRoles = practitionerRolesInCareTeamMap.get(careTeamRecordId);
    	 
    	 if (practitionerRoles == null) {
    		 return new ArrayList<>();
    	 }

    	 return practitionerRoles;
    }
    
    
    /**
     * Gets the care teams for a practitioner role.
     * 
     * @param practitionerRoleRecordId
     * @return
     */
    public List<ParticipantRoleCareTeam>getCareTeamsForPractitionerRole(String practitionerRoleRecordId) {
    	List<ParticipantRoleCareTeam> careTeams = careTeamsContainingPractitionerRoleMap.get(practitionerRoleRecordId);
    	
    	if (careTeams == null) {
    		return new ArrayList<>();
    	}
    	
    	return careTeams;
    }

    
    public void addCareTeamIfAbsent(String careTeamId){
        getLogger().info(".addCareTeamIfAbsent(): Entry, careTeamRecordID --> {}", careTeamId);
        
        if(careTeamId == null){
            getLogger().info(".addCareTeamIfAbsent(): Exit, careTeamRecordID is null");
            return;
        }
        
        if(!practitionerRolesInCareTeamMap.containsKey(careTeamId)){
        	practitionerRolesInCareTeamMap.put(careTeamId, new ArrayList<>());
        }
        
        getLogger().info(".addCareTeamIfAbsent(): Exit");
    }

    
    /**
     * Removes a care team from a practitioner role.
     * 
     * @param groupManager
     * @param careTeamToRemove
     */
    public void removeCareTeamFromPractitionerRole(String groupManager, ParticipantRoleCareTeam careTeamToRemove) {
		List<ParticipantRoleCareTeam> careTeams = careTeamsContainingPractitionerRoleMap.get(groupManager);
	   	 

		// From the care team from the practitioner role
   	 	List<ParticipantRoleCareTeam>newCareTeamList = new ArrayList<>();
   	 	
   	 	for (ParticipantRoleCareTeam careTeam : careTeams) {
   	 		if (!careTeam.getName().equals(careTeamToRemove.getName())) {
   	 			newCareTeamList.add(careTeam);
   	 		}
   	 	}

   	 	careTeamsContainingPractitionerRoleMap.replace(groupManager, newCareTeamList);
   	 	
   	 	// Now remove the practitioner role from the care team
		List<ParticipantESDT>practitionerRoles = practitionerRolesInCareTeamMap.get(careTeamToRemove.getName());
   	 	
   	 	List<ParticipantESDT>newPractitionerRoleList = new ArrayList<>();
	 	
	 	for (ParticipantESDT practitionerRole : practitionerRoles) {
	 		if (!practitionerRole.getSimplifiedID().equals(groupManager)) {
	 			newPractitionerRoleList.add(practitionerRole);
	 		}
	 	}
	 		
	 	practitionerRolesInCareTeamMap.replace(careTeamToRemove.getName(), newPractitionerRoleList);
	}

    
    /**
     * Add a practitioner to a care team.
     * 
     * @param groupManager
     * @param careTeamToAdd
     */
    public void addCareTeamToPractitionerRole(String groupManager, ParticipantRoleCareTeam careTeamToAdd) {
		addPractitionerRoleIfAbsent(groupManager);
		List<ParticipantRoleCareTeam> careTeams = careTeamsContainingPractitionerRoleMap.get(groupManager);
		
		ParticipantRoleCareTeam newCareTeam = new ParticipantRoleCareTeam(careTeamToAdd.getName(), careTeamToAdd.getRole());
		
		if (!careTeams.contains(newCareTeam)) {
			careTeams.add(newCareTeam);
		}
		
		
		// Now add the practitioner role to the care team
		addCareTeamIfAbsent(careTeamToAdd.getName());
	    List<ParticipantESDT>practitionerRoles = practitionerRolesInCareTeamMap.get(careTeamToAdd.getName());
	    
	    ParticipantESDT newPractitionerRole = new ParticipantESDT(groupManager, careTeamToAdd.getRole());
	    
	    if (!practitionerRoles.contains(newPractitionerRole)) {
	    	practitionerRoles.add(newPractitionerRole);
	    }
	    
	}

    
    public void addPractitionerRoleIfAbsent(String practitionerRoleId) {
		 getLogger().info(".addCareTeamIfAbsent(): Entry, careTeamRecordID --> {}", practitionerRoleId);
	        
	        if(practitionerRoleId == null){
	            getLogger().info(".addCareTeamIfAbsent(): Exit, careTeamRecordID is null");
	            return;
	        }
	        
	        if(!careTeamsContainingPractitionerRoleMap.containsKey(practitionerRoleId)){
	        	careTeamsContainingPractitionerRoleMap.put(practitionerRoleId, new ArrayList<>());
	        }
	        
	        getLogger().info(".addCareTeamIfAbsent(): Exit");
		
	}
	
	
	/**
	 * Remove a practitioner role from a care team
	 * 
	 * @param groupManager
	 * @param practitionerRoleToRemove
	 */
	public void removePractitionerRoleFromCareTeam(String groupManager, ParticipantESDT practitionerRoleToRemove) {
		List<ParticipantESDT> practitionerRoles = practitionerRolesInCareTeamMap.get(groupManager);
	   	 
		// Remove the participant from the care team
   	 	List<ParticipantESDT>newPractitionerRoleList = new ArrayList<>();
   	 	
   	 	for (ParticipantESDT practitionerRole : practitionerRoles) {
   	 		if (!practitionerRole.getSimplifiedID().equals(practitionerRoleToRemove.getSimplifiedID())) {
   	 			newPractitionerRoleList.add(practitionerRole);
   	 		}
   	 	}	
   	 	
   	 	practitionerRolesInCareTeamMap.replace(groupManager, newPractitionerRoleList);

   	 	// Now remove the care team from the practitioner.
		List<ParticipantRoleCareTeam> careTeams = careTeamsContainingPractitionerRoleMap.get(practitionerRoleToRemove.getSimplifiedID());
		
		List<ParticipantRoleCareTeam>newCareTeamList = new ArrayList<>();
		
		for (ParticipantRoleCareTeam careTeam : careTeams) {
			if (!careTeam.getName().equals(groupManager)) {
				newCareTeamList.add(careTeam);
			}
		}
		
		careTeamsContainingPractitionerRoleMap.replace(practitionerRoleToRemove.getSimplifiedID(), newCareTeamList);
	}
}
