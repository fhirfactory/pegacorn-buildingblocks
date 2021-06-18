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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.CommonIdentifierESDTTypes;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.OrganizationESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.OrganisationStructure;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerRoleCareTeamListESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.group.CareTeamsContainingPractitionerRoleGroupESR;
import net.fhirfactory.buildingblocks.esr.models.resources.group.PractitionersFulfillingPractitionerRolesGroupESR;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;

@ApplicationScoped
public class PractitionerRoleESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleESRBroker.class);

    @Inject
    private PractitionerRoleESRCache practitionerRoleCache;
    
    @Inject
    private GroupESRBroker groupBroker;
    
    @Inject
    private PractitionerESRBroker practitionerBroker;
    
    @Inject
    private OrganizationESRBroker organisationBroker;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    protected PegacornESRCache specifyCache(){
        return(practitionerRoleCache);
    }

    //
    // Primary Key Setting
    //
    @Override
    protected void assignSimplifiedID(ExtremelySimplifiedResource resource) {
        getLogger().debug(".assignPrimaryKey(): Entry, resource --> {}", resource);
        if(resource == null){
            getLogger().debug(".assignPrimaryKey(): Entry, resource is null, exiting");
            return;
        }
        resource.assignSimplifiedID(true, getCommonIdentifierTypes().getShortName(), IdentifierESDTUseEnum.OFFICIAL);
    }

    //
    // Create
    //
    public ESRMethodOutcome createPractitionerRole(PractitionerRoleESR directoryEntry) throws ResourceInvalidSearchException{
        getLogger().info(".createPractitionerRole(): Entry, directoryEntry --> {}", directoryEntry);
        
        
        addOrganisationStructure(directoryEntry);
        
        
        ESRMethodOutcome outcome = practitionerRoleCache.addPractitionerRole(directoryEntry);
        PractitionersFulfillingPractitionerRolesGroupESR activePractitionerSet = new PractitionersFulfillingPractitionerRolesGroupESR();
        activePractitionerSet.setGroupManager(directoryEntry.getSimplifiedID());
        activePractitionerSet.setSystemManaged(true);
        activePractitionerSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITIONERS_FULFILLING_PRACTITIONER_ROLE_GROUP.getTypeCode());
        activePractitionerSet.setDisplayName("Practitioners-Fulfilling-PractitionerRole-"+directoryEntry.getIdentifierWithType("ShortName").getValue());
        activePractitionerSet.getIdentifiers().add(directoryEntry.getIdentifierWithType("ShortName"));
        ESRMethodOutcome groupCreateOutcome = groupBroker.createGroupDE(activePractitionerSet);
        
        
        // Create a care teams group
        CareTeamsContainingPractitionerRoleGroupESR careTeamsForPractitionerRoleSet = new CareTeamsContainingPractitionerRoleGroupESR();
        careTeamsForPractitionerRoleSet.setGroupManager(directoryEntry.getSimplifiedID());
        careTeamsForPractitionerRoleSet.setGroupType(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP.getTypeCode());
        careTeamsForPractitionerRoleSet.setSystemManaged(true);
        
        IdentifierESDT identifier = new IdentifierESDT();
        identifier.setUse(IdentifierESDTUseEnum.USUAL);
        identifier.setType("ShortName");
        identifier.setValue(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP.getGroupPrefix() + directoryEntry.getSimplifiedID());
        careTeamsForPractitionerRoleSet.getIdentifiers().add(identifier);   
        careTeamsForPractitionerRoleSet.setDisplayName(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP.getGroupPrefix() + directoryEntry.getSimplifiedID());
        
  	    groupBroker.createGroupDE(careTeamsForPractitionerRoleSet);       		
        

        getLogger().info(".createPractitionerRole(): Exit");
        
        return(outcome);
    }



    /**
     * Add the organisation structure.
     * 
     * @param directoryEntry
     * @throws ResourceInvalidSearchException
     */
    private void addOrganisationStructure(PractitionerRoleESR directoryEntry) throws ResourceInvalidSearchException {      
        if (directoryEntry.getPrimaryOrganizationID() != null) {
            ESRMethodOutcome outcome = organisationBroker.searchForDirectoryEntryUsingLeafValue(directoryEntry.getPrimaryOrganizationID().toLowerCase());
            
            if (outcome.getStatus().equals(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND)) {
                OrganizationESR organisation = (OrganizationESR)outcome.getEntry();
                
                CommonIdentifierESDTTypes identifierTypes = new CommonIdentifierESDTTypes();
                IdentifierESDT shortNameIdentifier = organisation.getIdentifierWithType(identifierTypes.getShortName());
                
                OrganisationStructure structure = new OrganisationStructure();
                
                structure.setIndex(1);
                structure.setValue(shortNameIdentifier.getLeafValue());
                structure.setType(organisation.getOrganizationType().getTypeDisplayValue());
                directoryEntry.getOrganisationStructure().add(structure);
            }
        }
    }
    
    
    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        PractitionerRoleESR practitionerRoleESR = (PractitionerRoleESR) entry;
        
        boolean doEnrich = true;
      
        ESRMethodOutcome practitionerMembershipGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("ShortName"), false);
        if(practitionerMembershipGroupGetOutcome.isSearch()) {
            if (!practitionerMembershipGroupGetOutcome.getSearchResult().isEmpty()) {
            	PractitionersFulfillingPractitionerRolesGroupESR groupESR = (PractitionersFulfillingPractitionerRolesGroupESR) practitionerMembershipGroupGetOutcome.getSearchResult().get(0);
     
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
              
                practitionerRoleESR.getActivePractitionerSet().clear();
                
                for (String practitionerId : groupESR.getGroupMembership()) {
                	doEnrich = false;
                	PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource(practitionerId.toLowerCase(), doEnrich).getEntry();  
                	practitionerRoleESR.addActivePractitioner(practitioner);
                }
            }
        } else {
            if (practitionerMembershipGroupGetOutcome.getEntry() != null) {
            	PractitionersFulfillingPractitionerRolesGroupESR groupESR = (PractitionersFulfillingPractitionerRolesGroupESR) practitionerMembershipGroupGetOutcome.getEntry();
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
              
                practitionerRoleESR.getActivePractitionerSet().clear();
                
                for (String practitionerId : groupESR.getGroupMembership()) {
                	doEnrich = false;
                	PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource(practitionerId.toLowerCase(), doEnrich).getEntry();
                	practitionerRoleESR.addActivePractitioner(practitioner);
                }
            }
        }

        
        IdentifierESDT identifier = new IdentifierESDT();
       	identifier.setValue(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP.getGroupPrefix() + entry.getIdentifierWithType("ShortName").getValue());
       	identifier.setUse(IdentifierESDTUseEnum.USUAL);
       	identifier.setLeafValue(identifier.getValue());
        
       
       	ESRMethodOutcome careTeamsForPractitionerRoleGroup = groupBroker.searchForDirectoryEntryUsingIdentifier(identifier, false);
        if(careTeamsForPractitionerRoleGroup.isSearch()) {
            if (!careTeamsForPractitionerRoleGroup.getSearchResult().isEmpty()) {
            	CareTeamsContainingPractitionerRoleGroupESR groupESR = (CareTeamsContainingPractitionerRoleGroupESR) careTeamsForPractitionerRoleGroup.getSearchResult().get(0);
        	
            	practitionerRoleESR.setCareTeams(groupESR.getGroupMembership());
            }
        } else {
            if (careTeamsForPractitionerRoleGroup.getEntry() != null) {
            	CareTeamsContainingPractitionerRoleGroupESR groupESR = (CareTeamsContainingPractitionerRoleGroupESR) careTeamsForPractitionerRoleGroup.getEntry();
        	
            	practitionerRoleESR.setCareTeams(groupESR.getGroupMembership());
            }
        }
        
        
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //
    public ESRMethodOutcome updatePractitionerRole(PractitionerRoleESR entry) throws ResourceInvalidSearchException {
        LOG.info(".updatePractitionerRole(): Entry");
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        PractitionerRoleESR foundEntry = null;
        if(entry.getSimplifiedID() != null){
            foundEntry = (PractitionerRoleESR) practitionerRoleCache.getCacheEntry(entry.getSimplifiedID());
        } else {
            IdentifierESDT entryIdentifier = entry.getIdentifierWithType("ShortName");
            if(entryIdentifier != null){
                if(entryIdentifier.getUse().equals(IdentifierESDTUseEnum.OFFICIAL)){
                    ESRMethodOutcome practitionerRoleQueryOutcome = practitionerRoleCache.searchCacheForESRUsingIdentifier(entryIdentifier);
                    boolean searchCompleted = practitionerRoleQueryOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                    boolean searchFoundSomething = practitionerRoleQueryOutcome.getSearchResult().size() == 1;
                    if(searchCompleted && searchFoundSomething) {
                        foundEntry = (PractitionerRoleESR) practitionerRoleQueryOutcome.getSearchResult().get(0);
                        outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
                    }
                }
            }
        }
        if(foundEntry == null){
            this.assignSimplifiedID(entry);
            practitionerRoleCache.addPractitionerRole(entry);
            outcome.setId(entry.getSimplifiedID());
            outcome.setEntry(entry);
            outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE);
        }
        
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL) ||outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            getLogger().info(".updatePractitioner(): Entry itself is updated, so updating its associated fulfilledPractitionerRole details");
          
            
            ESRMethodOutcome practitionersGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("ShortName"));
            boolean searchCompleted = practitionersGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            boolean searchFoundSomething = practitionersGroupGetOutcome.getSearchResult().size() == 1;
            
            if(searchCompleted && searchFoundSomething){
                getLogger().info(".updatePractitioner(): updating the associated group");
                PractitionersFulfillingPractitionerRolesGroupESR practitionerRolesGroup = (PractitionersFulfillingPractitionerRolesGroupESR)practitionersGroupGetOutcome.getSearchResult().get(0);
                
                practitionerRolesGroup.setGroupMembership(entry.getActivePractitionerIds());
                groupBroker.updateGroup(practitionerRolesGroup);    
            }

            
            IdentifierESDT identifier = new IdentifierESDT();
	        identifier.setUse(IdentifierESDTUseEnum.USUAL);
	        identifier.setType("ShortName");
	        identifier.setValue(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP.getGroupPrefix() + entry.getSimplifiedID());

            ESRMethodOutcome careTeamssGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(identifier);
            searchCompleted = careTeamssGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            searchFoundSomething = careTeamssGroupGetOutcome.getSearchResult().size() == 1;
        
            if(searchCompleted && searchFoundSomething){
                getLogger().info(".updatePractitioner(): updating the associated group");
                CareTeamsContainingPractitionerRoleGroupESR careTeamRolesGroup = (CareTeamsContainingPractitionerRoleGroupESR)careTeamssGroupGetOutcome.getSearchResult().get(0);
                careTeamRolesGroup.setGroupMembership(entry.getCareTeams());
                groupBroker.updateGroup(careTeamRolesGroup);    
            }
        }
        LOG.info(".updatePractitionerRole(): Exit");
        return(outcome);
    }

    //
    // Delete
    //
    public ESRMethodOutcome deletePractitionerRole(PractitionerRoleESR entry){
        ESRMethodOutcome outcome = new ESRMethodOutcome();

        return(outcome);
    }
    
    
    /**
     * Updates the care teams the practitioner role is in.
     * 
     * @param simplifiedId
     * @param newCareTeams
     * @return
     * @throws ResourceInvalidSearchException
     */
    public ESRMethodOutcome updateCareTeams(String simplifiedId, PractitionerRoleCareTeamListESDT newCareTeamList) throws ResourceInvalidSearchException {
        LOG.info(".getCareTeams(): Entry");
    	
    	PractitionerRoleESR practitionerRole = (PractitionerRoleESR) this.getResource(simplifiedId.toLowerCase()).getEntry();
    	
    	practitionerRole.setCareTeams(newCareTeamList.getCareTeams());
    	updatePractitionerRole(practitionerRole);
    	
    	ESRMethodOutcome outcome = new ESRMethodOutcome();
    	outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
    	
    	return outcome;
    }
    
    
	@Override
	protected void clearAssociations(ExtremelySimplifiedResource entry) {
		PractitionerRoleESR practitonerESR = (PractitionerRoleESR)entry;
		
		practitonerESR.getActivePractitionerSet().clear();
	}
}
