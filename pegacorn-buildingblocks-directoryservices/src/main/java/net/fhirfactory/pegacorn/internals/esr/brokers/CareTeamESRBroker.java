/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.esr.brokers;

import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.CareTeamESR;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.ParticipantESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.group.CareTeamsContainingPractitionerRoleGroupESR;
import net.fhirfactory.buildingblocks.esr.models.resources.group.PractitionerRolesInCareTeamGroupESR;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;

@ApplicationScoped
public class CareTeamESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(CareTeamESRBroker.class);

    @Inject
    private CareTeamESRCache careTeamCache;
    
    
    @Inject
    private GroupESRBroker groupBroker;
    
    @Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;
    

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected PegacornESRCache specifyCache() {
        return (careTeamCache);
    }

    //
    // Primary Key Setting
    //
    @Override
    protected void assignSimplifiedID(ExtremelySimplifiedResource resource) {
        getLogger().debug(".assignSimplifiedID(): Entry, resource --> {}", resource);
        if(resource == null){
            getLogger().debug(".assignSimplifiedID(): Entry, resource is null, exiting");
            return;
        }
        resource.assignSimplifiedID(true, getCommonIdentifierTypes().getShortName(), IdentifierESDTUseEnum.USUAL);
    }

    //
    // Create a care team.  This will create many groups.  A single care team group with the care team as the key and the practitioner roles as the members, and create/update a group for every pracactitoner role with the practitionerrole as the key and the care teams as the members.
    //

    public ESRMethodOutcome createCareTeam(CareTeamESR newCareTeam) throws ResourceInvalidSearchException {
    
    	// The new care team might contain practitioner role membership so remove it from here and add it to the group.  The PractitionerRoleESR will get it from the group.
        PractitionerRolesInCareTeamGroupESR practionerRolesInCareTeamSet = new PractitionerRolesInCareTeamGroupESR();
        for (ParticipantESDT participant : newCareTeam.getParticipants()) {
	     
        	// We need to make sure any practitioner role in the care team exists.  This is unlikely to occur but good to check.
        	IdentifierESDT practitionerRoleIdentifier = new IdentifierESDT();
	        practitionerRoleIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
	        practitionerRoleIdentifier.setType("ShortName");
	        practitionerRoleIdentifier.setValue(participant.getSimplifiedID());
        	
        	ESRMethodOutcome outcome = practitionerRoleBroker.searchForDirectoryEntryUsingIdentifier(practitionerRoleIdentifier);
        	
        	 if (outcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY)) {
 	        	PractitionerRoleESR existingPractitionerRole = (PractitionerRoleESR)outcome.getEntry();
        	  	practionerRolesInCareTeamSet.addNewGroupMember(new ParticipantESDT(existingPractitionerRole.getSimplifiedID()));
        	 } else {
 	        	getLogger().warn("Practitioner record not found for simplifiedId: {}", practitionerRoleIdentifier);
 	        }
        }
    	
        newCareTeam.setParticipants(new ArrayList<>());
    	ESRMethodOutcome outcome = this.createDirectoryEntry(newCareTeam);
       
        // Create a care team group with the practitioner roles as the group members.
        practionerRolesInCareTeamSet.setGroupManager(newCareTeam.getSimplifiedID());
        practionerRolesInCareTeamSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITIONER_ROLES_IN_CARE_TEAM_GROUP.getTypeCode());
        practionerRolesInCareTeamSet.setSystemManaged(true);
        practionerRolesInCareTeamSet.getIdentifiers().add(newCareTeam.getIdentifierWithType("ShortName"));
        practionerRolesInCareTeamSet.setDisplayName(SystemManagedGroupTypesEnum.PRACTITIONER_ROLES_IN_CARE_TEAM_GROUP.getGroupPrefix() + newCareTeam.getIdentifierWithType("ShortName").getValue());
      
    	groupBroker.createGroupDE(practionerRolesInCareTeamSet);

        
        // Create/update practitioner role groups with the care teams as the members.
        for (ParticipantESDT participant : practionerRolesInCareTeamSet.getGroupMembership()) {    
	       
        	participant.setSimplifiedID(participant.getSimplifiedID());
   	
	        CareTeamsContainingPractitionerRoleGroupESR careTeamsForPractitionerRoleSet = new CareTeamsContainingPractitionerRoleGroupESR();
	        careTeamsForPractitionerRoleSet.setGroupManager(participant.getSimplifiedID());
	        careTeamsForPractitionerRoleSet.setGroupType(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP.getTypeCode());
	        careTeamsForPractitionerRoleSet.setSystemManaged(true);
	        
	        IdentifierESDT identifier = new IdentifierESDT();
	        identifier.setUse(IdentifierESDTUseEnum.USUAL);
	        identifier.setType("ShortName");
	        identifier.setValue(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP.getGroupPrefix() + participant.getSimplifiedID());
	        
	        careTeamsForPractitionerRoleSet.getIdentifiers().add(identifier);   
	        careTeamsForPractitionerRoleSet.setDisplayName(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP.getGroupPrefix() + participant.getSimplifiedID());
	        careTeamsForPractitionerRoleSet.addNewGroupMember(participant.getSimplifiedID());
	        
	        
	        // Create or update group.
        	ESRMethodOutcome groupOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(identifier, false);
        	
        	if (!groupOutcome.getSearchResult().isEmpty()) {
        		CareTeamsContainingPractitionerRoleGroupESR group = (CareTeamsContainingPractitionerRoleGroupESR)groupOutcome.getEntry();
        		group.addNewGroupMember(newCareTeam.getSimplifiedID());
		        groupBroker.updateGroup(group);
	        } else {
	  	        groupBroker.createGroupDE(careTeamsForPractitionerRoleSet);       		
	    	}
        }
        
        return(outcome);
    }

    
    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
    	getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
    	
        CareTeamESR careTeamESR = (CareTeamESR) entry;
    	
    	ESRMethodOutcome groupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("ShortName"), false);
      
    	if(groupGetOutcome.isSearch()){
          if (!groupGetOutcome.getSearchResult().isEmpty()) {
              getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
              PractitionerRolesInCareTeamGroupESR careTeamGroupESR = (PractitionerRolesInCareTeamGroupESR) groupGetOutcome.getSearchResult().get(0);
              careTeamESR.setParticipants(careTeamGroupESR.getGroupMembership());
          }
      } else {
          if (groupGetOutcome.getEntry() != null) {
              getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
              PractitionerRolesInCareTeamGroupESR careTeamGroupESR = (PractitionerRolesInCareTeamGroupESR) groupGetOutcome.getEntry();
              careTeamESR.setParticipants(careTeamGroupESR.getGroupMembership());
          }
      }
      getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");

    }

    
    /**
	 * Update/create a care team.
	 * 
	 * @param entry
	 * @return
	 * @throws ResourceInvalidSearchException
	 */
	public ESRMethodOutcome updateCareTeam(CareTeamESR entry) throws ResourceInvalidSearchException {
		LOG.info(".updateCareTeam(): Entry");
		
		CareTeamESR existing = (CareTeamESR)careTeamCache.getCacheEntry(entry.getSimplifiedID().toLowerCase());
		
	    if(existing != null) {
	    	ESRMethodOutcome outcome = updateDirectoryEntry(entry);
	    	
	    	// If the care team was updated/created then update the practitioner role records within the care team.
	    	if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL) || outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
	       
	    		ESRMethodOutcome practitionerRolesInCareTeamGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("ShortName"));
	    		
	    		boolean searchCompleted = practitionerRolesInCareTeamGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
	    		boolean searchFoundOneResultOnly = practitionerRolesInCareTeamGroupGetOutcome.getSearchResult().size() == 1;
	    		
	    		if(searchCompleted && searchFoundOneResultOnly && practitionerRolesInCareTeamGroupGetOutcome.isSearchSuccessful()){
	    			getLogger().info(".updateCareTeam(): updating the associated group");
	    		    
	    			PractitionerRolesInCareTeamGroupESR practitionerRolesInCareTeamsGroup = (PractitionerRolesInCareTeamGroupESR)practitionerRolesInCareTeamGroupGetOutcome.getSearchResult().get(0);
	    		    practitionerRolesInCareTeamsGroup.setGroupMembership(entry.getParticipants());
	    		    outcome = groupBroker.updateGroup(practitionerRolesInCareTeamsGroup);
	    		} else {
	    		    getLogger().info(".updateCareTeam(): Update processed failed for the superclass PegacornDirectoryEntry, reason --> {}", outcome.getStatusReason());
	    		}
	    	}
	        
	        return outcome;
	    } else {
	    	return createCareTeam(entry);
	    }
	}
}