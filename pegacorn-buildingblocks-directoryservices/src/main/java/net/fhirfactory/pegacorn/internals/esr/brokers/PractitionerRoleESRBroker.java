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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.CareTeamESR;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.GroupESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.ParticipantESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerRoleCareTeam;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerRoleCareTeamListESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.SystemManagedGroupTypesEnum;
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
    private CareTeamESRBroker careTeamBroker;

    @Inject
    private GroupESRBroker groupBroker;

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
    public ESRMethodOutcome createPractitionerRole(PractitionerRoleESR directoryEntry){
        getLogger().info(".createPractitionerRole(): Entry, directoryEntry --> {}", directoryEntry);
        ESRMethodOutcome outcome = practitionerRoleCache.addPractitionerRole(directoryEntry);
        GroupESR activePractitionerSet = new GroupESR();
        activePractitionerSet.setGroupManager(directoryEntry.getSimplifiedID());
        activePractitionerSet.setSystemManaged(true);
        activePractitionerSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITIONEROLE_MAP_PRACTITIONER_GROUP.getTypeCode());
        activePractitionerSet.setDisplayName("Practitioners-Fulfilling-PractitionerRole-"+directoryEntry.getIdentifierWithType("ShortName").getValue());
        activePractitionerSet.getIdentifiers().add(directoryEntry.getIdentifierWithType("ShortName"));
        ESRMethodOutcome groupCreateOutcome = groupBroker.createGroupDE(activePractitionerSet);
        getLogger().info(".createPractitionerRole(): Exit");
        return(outcome);
    }


    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        PractitionerRoleESR practitionerRoleESR = (PractitionerRoleESR) entry;
        ESRMethodOutcome groupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("ShortName"), false);
        if(groupGetOutcome.isSearch()) {
            if (!groupGetOutcome.getSearchResult().isEmpty()) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
                GroupESR practitionerRolesGroup = (GroupESR) groupGetOutcome.getSearchResult().get(0);
                practitionerRoleESR.setRoleHistory(practitionerRolesGroup.getRoleHistory());
            }
        } else {
            if (groupGetOutcome.getEntry() != null) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
                GroupESR practitionerRolesGroup = (GroupESR) groupGetOutcome.getEntry();
                practitionerRoleESR.setRoleHistory(practitionerRolesGroup.getRoleHistory());
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
                GroupESR practitionerRolesGroup = (GroupESR)practitionersGroupGetOutcome.getSearchResult().get(0);
                practitionerRolesGroup.setRoleHistory(entry.getRoleHistory());
                groupBroker.updateGroup(practitionerRolesGroup);
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
    
    
    public ESRMethodOutcome updateCareTeams(String simplifiedId, PractitionerRoleCareTeamListESDT newCareTeams) throws ResourceInvalidSearchException {
        LOG.info(".getCareTeams(): Entry");
    	
    	PractitionerRoleESR practitionerRole = (PractitionerRoleESR) this.getResource(simplifiedId.toLowerCase()).getEntry();
    	
    	// Remove the practitioner role from the care teams they are in.
    	for (PractitionerRoleCareTeam practitionerRoleCareTeam : practitionerRole.getCareTeams()) {
    		CareTeamESR careTeam = (CareTeamESR)careTeamBroker.getResource(practitionerRoleCareTeam.getName().toLowerCase()).getEntry();
    		careTeam.removeParticipant(practitionerRole.getSimplifiedID());
    		
    		careTeamBroker.updateCareTeam(careTeam);
    		
    		practitionerRole.setCareTeams(newCareTeams.getCareTeams());
    		updatePractitionerRole(practitionerRole);
    	}
    	
    	
    	// Add the practitioner role to the care teams.
    	for (PractitionerRoleCareTeam practitionerRoleCareTeam : newCareTeams.getCareTeams()) {
    		CareTeamESR careTeam = (CareTeamESR)careTeamBroker.getResource(practitionerRoleCareTeam.getName().toLowerCase()).getEntry();
    		careTeam.addParticipant(new ParticipantESDT(simplifiedId, practitionerRoleCareTeam.getRole()));
    		
    		careTeamBroker.updateCareTeam(careTeam);
    	}

    	ESRMethodOutcome outcome = new ESRMethodOutcome();
    	outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
    	
    	return outcome;
    	
    }
}
