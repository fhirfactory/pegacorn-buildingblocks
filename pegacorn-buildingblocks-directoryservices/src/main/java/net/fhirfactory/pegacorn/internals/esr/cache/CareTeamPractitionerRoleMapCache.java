package net.fhirfactory.pegacorn.internals.esr.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.ParticipantESDT;

@ApplicationScoped
public class CareTeamPractitionerRoleMapCache {
	private static final Logger LOG = LoggerFactory.getLogger(CareTeamPractitionerRoleMapCache.class);

    private ConcurrentHashMap<String, List<ParticipantESDT>> practitionerRolesInCareTeamMap;
    private ConcurrentHashMap<String, List<String>> careTeamsContainingPractitionerRoleMap;

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
    	
	    ParticipantESDT newPractitionerRole = new ParticipantESDT(practitionerRole.getSimplifiedID());
	    
	    if (!practitionerRoles.contains(newPractitionerRole)) {
	    	practitionerRoles.add(newPractitionerRole);
	    }
   	 	
   	 	
   	 	// Now add the care team to the practitioner role
   	 	addPractitionerRoleIfAbsent(practitionerRole.getSimplifiedID());
		List<String> careTeams = careTeamsContainingPractitionerRoleMap.get(practitionerRole.getSimplifiedID());
				
		if (!careTeams.contains(careTeamRecordId)) {
			careTeams.add(careTeamRecordId);
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
    public List<String>getCareTeamsForPractitionerRole(String practitionerRoleRecordId) {
    	List<String> careTeams = careTeamsContainingPractitionerRoleMap.get(practitionerRoleRecordId);
    	
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
    public void removeCareTeamFromPractitionerRole(String groupManager, String careTeamToRemove) {
		List<String> careTeams = careTeamsContainingPractitionerRoleMap.get(groupManager);
	   	 

		// From the care team from the practitioner role
   	 	List<String>newCareTeamList = new ArrayList<>();
   	 	
   	 	for (String careTeam : careTeams) {
   	 		if (!careTeam.equals(careTeamToRemove)) {
   	 			newCareTeamList.add(careTeam);
   	 		}
   	 	}

   	 	careTeamsContainingPractitionerRoleMap.replace(groupManager, newCareTeamList);
   	 	
   	 	// Now remove the practitioner role from the care team
		List<ParticipantESDT>practitionerRoles = practitionerRolesInCareTeamMap.get(careTeamToRemove);
   	 	
   	 	List<ParticipantESDT>newPractitionerRoleList = new ArrayList<>();
	 	
	 	for (ParticipantESDT practitionerRole : practitionerRoles) {
	 		if (!practitionerRole.getSimplifiedID().equals(groupManager)) {
	 			newPractitionerRoleList.add(practitionerRole);
	 		}
	 	}
	 		
	 	practitionerRolesInCareTeamMap.replace(careTeamToRemove, newPractitionerRoleList);
	}

    
    /**
     * Add a practitioner to a care team.
     * 
     * @param groupManager
     * @param careTeamToAdd
     */
    public void addCareTeamToPractitionerRole(String groupManager, String careTeamToAdd) {
		addPractitionerRoleIfAbsent(groupManager);
		List<String> careTeams = careTeamsContainingPractitionerRoleMap.get(groupManager);
				
		if (!careTeams.contains(careTeamToAdd)) {
			careTeams.add(careTeamToAdd);
		}
		
		
		// Now add the practitioner role to the care team
		addCareTeamIfAbsent(careTeamToAdd);
	    List<ParticipantESDT>practitionerRoles = practitionerRolesInCareTeamMap.get(careTeamToAdd);
	    
	    ParticipantESDT newPractitionerRole = new ParticipantESDT(groupManager);
	    
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
		List<String> careTeams = careTeamsContainingPractitionerRoleMap.get(practitionerRoleToRemove.getSimplifiedID());
		
		List<String>newCareTeamList = new ArrayList<>();
		
		for (String careTeam : careTeams) {
			if (!careTeam.equals(groupManager)) {
				newCareTeamList.add(careTeam);
			}
		}
		
		careTeamsContainingPractitionerRoleMap.replace(practitionerRoleToRemove.getSimplifiedID(), newCareTeamList);
	}
}
