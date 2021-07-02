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

import javax.inject.Inject;

import net.fhirfactory.pegacorn.deployment.communicate.matrix.CommunicateSystemManagedRoomNames;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerRoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.PractitionerRoleListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.RoleHistoryDetail;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.group.PractitionerRolesFulfilledByPractitionerGroupESR;
import net.fhirfactory.pegacorn.internals.esr.search.FavouriteTypes;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;



public abstract class PractitionerESRBroker extends ESRBroker {

    @Inject
    private PractitionerESRCache practitionerCache;

    @Inject
    private GroupESRBroker groupBroker;

    @Inject
    private CommunicateSystemManagedRoomNames managedRoomNames;


    
	@Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;
	
	@Inject
    private RoleCategoryESRBroker roleCategoryBroker;

    abstract protected CommunicateRoomESRBroker specifyMatrixRoomESRBroker();


    @Override
    protected PegacornESRCache specifyCache(){
        return(practitionerCache);
    }

    protected PractitionerESRCache getPractitionerESRCache(){
        return(practitionerCache);
    }

    protected GroupESRBroker getGroupESRBroker(){
        return(groupBroker);
    }

    protected CommunicateSystemManagedRoomNames getManagedRoomNames(){
        return(managedRoomNames);
    }

    protected CommunicateRoomESRBroker getMatrixRoomESRBroker(){
        return(specifyMatrixRoomESRBroker());
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

        resource.assignSimplifiedID(true, IdentifierType.EMAIL_ADDRESS, IdentifierESDTUseEnum.OFFICIAL);
    }

    //
    // Create
    //
    public ESRMethodOutcome createPractitionerDE(PractitionerESR entry){
        getLogger().info(".createPractitioner(): Entry");
        ESRMethodOutcome outcome = practitionerCache.addPractitioner(entry);
        
        PractitionerRolesFulfilledByPractitionerGroupESR activePractitionerSet = new PractitionerRolesFulfilledByPractitionerGroupESR();
        activePractitionerSet.setGroupManager(entry.getSimplifiedID());
        activePractitionerSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITONER_ROLES_FULFILLED_BY_PRACTITIONER_GROUP.getTypeCode());
        activePractitionerSet.setSystemManaged(true);
        activePractitionerSet.setDisplayName("PractitionerRoles-Fulfilled-by-Practitioner-"+entry.getIdentifierWithType(IdentifierType.EMAIL_ADDRESS).getValue());
        activePractitionerSet.getIdentifiers().add(entry.getIdentifierWithType(IdentifierType.EMAIL_ADDRESS));
        ESRMethodOutcome groupCreateOutcome = groupBroker.createGroupDE(activePractitionerSet);
        
        //createSystemManagedMatrixRooms(entry);

        getLogger().info(".createPractitioner(): Exit");
        return(outcome);
    }

    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        PractitionerESR practitionerESR = (PractitionerESR) entry;
        ESRMethodOutcome groupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType(IdentifierType.EMAIL_ADDRESS), false);
        if(groupGetOutcome.isSearch()){
            if (!groupGetOutcome.getSearchResult().isEmpty()) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
                PractitionerRolesFulfilledByPractitionerGroupESR practitionerRolesGroup = (PractitionerRolesFulfilledByPractitionerGroupESR) groupGetOutcome.getSearchResult().get(0);
                practitionerESR.setRoleHistory(practitionerRolesGroup.getRoleHistory());
                
                practitionerESR.getCurrentPractitionerRoles().clear();
                
                for (RoleHistoryDetail roleHistoryDetail : practitionerRolesGroup.getRoleHistory().getAllCurrentRoles()) {
                	PractitionerRoleESR practitionerRole = (PractitionerRoleESR)practitionerRoleBroker.getResource(roleHistoryDetail.getRole().toLowerCase(), false).getEntry();
                	practitionerESR.addCurrentPractitionerRole(practitionerRole);
                }
            }
        } else {
            if (groupGetOutcome.getEntry() != null) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
                PractitionerRolesFulfilledByPractitionerGroupESR practitionerRolesGroup = (PractitionerRolesFulfilledByPractitionerGroupESR) groupGetOutcome.getEntry();
                practitionerESR.setRoleHistory(practitionerRolesGroup.getRoleHistory()); 
                
                
                practitionerESR.getCurrentPractitionerRoles().clear();
                
                for (RoleHistoryDetail roleHistoryDetail : practitionerRolesGroup.getRoleHistory().getAllCurrentRoles()) {
                	PractitionerRoleESR practitionerRole = (PractitionerRoleESR)practitionerRoleBroker.getResource(roleHistoryDetail.getRole().toLowerCase(), false).getEntry();
                	practitionerESR.addCurrentPractitionerRole(practitionerRole);
                }
            }
        }
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }
    
    
    //
    // Update
    //

    public ESRMethodOutcome updatePractitioner(PractitionerESR entry) throws ResourceInvalidSearchException {
        getLogger().info(".updatePractitioner(): Entry");
        ESRMethodOutcome entryUpdate = updateDirectoryEntry(entry);
        if(entryUpdate.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL) || entryUpdate.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            getLogger().info(".updatePractitioner(): Entry itself is updated, so updating its associated fulfilledPractitionerRole details");
            ESRMethodOutcome practitionerRolesGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType(IdentifierType.EMAIL_ADDRESS));
            boolean searchCompleted = practitionerRolesGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            boolean searchFoundOneResultOnly = practitionerRolesGroupGetOutcome.getSearchResult().size() == 1;
            if(searchCompleted && searchFoundOneResultOnly && practitionerRolesGroupGetOutcome.isSearchSuccessful()){
                getLogger().info(".updatePractitioner(): updating the associated group");
                PractitionerRolesFulfilledByPractitionerGroupESR practitionerRolesGroup = (PractitionerRolesFulfilledByPractitionerGroupESR)practitionerRolesGroupGetOutcome.getSearchResult().get(0);
                practitionerRolesGroup.setRoleHistory(entry.getRoleHistory());
                ESRMethodOutcome groupUpdateOutcome = groupBroker.updateGroup(practitionerRolesGroup);
            }
        } else {
            getLogger().info(".updatePractitioner(): Update processed failed for the superclass PegacornDirectoryEntry, reason --> {}", entryUpdate.getStatusReason());
        }
        getLogger().info(".updatePractitioner(): Exit");
        return(entryUpdate);
    }

    public ESRMethodOutcome updatePractitionerRoles(String simplifiedID, PractitionerRoleListESDT updatePractitionerRoles) throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerRoles():Entry");
        ESRMethodOutcome outcome = this.getResource(simplifiedID.toLowerCase());
        if (outcome.getEntry() != null) {
            getLogger().trace(".updatePractitionerRoles():Found Resource, so updating fulfilledPractitionerRole details");
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            
            
            boolean practitionerRoleFound = true;
            
            // Make sure all the provided practitioner roles are valid.  Fail the request if one isn't.
            for (String practitionerRole : updatePractitionerRoles.getPractitionerRoles()) {
                ESRMethodOutcome practitionerRoleOutcome = practitionerRoleBroker.getResource(practitionerRole.toLowerCase(), false);
                
                if (practitionerRoleOutcome.getStatus().equals(ESRMethodOutcomeEnum.REVIEW_ENTRY_NOT_FOUND)) {
                    outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
                    outcome.setStatusReason("Practitioner role not found: " + practitionerRole);     
                    practitionerRoleFound = false;
                    break;
                }
            }
            
            if (practitionerRoleFound) {
                ESRMethodOutcome practitionerRolesGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(practitioner.getIdentifierWithType(IdentifierType.EMAIL_ADDRESS));
                boolean searchCompleted = practitionerRolesGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY) || outcome.getStatus().equals(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
                boolean searchFoundOneResultOnly = practitionerRolesGroupGetOutcome.getSearchResult().size() == 1;

                if (searchCompleted && searchFoundOneResultOnly && practitionerRolesGroupGetOutcome.isSearchSuccessful()) {
                    getLogger().trace(".updatePractitionerRoles(): updating the associated group");
                    PractitionerRolesFulfilledByPractitionerGroupESR practitionerRolesGroup = (PractitionerRolesFulfilledByPractitionerGroupESR) practitionerRolesGroupGetOutcome.getSearchResult().get(0);
                    
                    practitionerRolesGroup.getRoleHistory().update(updatePractitionerRoles.getPractitionerRoles());
                    
                    ESRMethodOutcome groupUpdateOutcome = groupBroker.updateGroup(practitionerRolesGroup);
                    if(groupUpdateOutcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
                        outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
                    } else {
                        outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
                        outcome.setStatusReason("Could not update associated PractitionerRoles Group");
                    } 
                } else {
                    outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
                    outcome.setStatusReason("Could not find associated PractitionerRoles Group");
                }
            }
        } else {
            outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            outcome.setStatusReason("Could not update associated Resource");
        }
        
        getLogger().debug(".updatePractitionerRoles():Exit");
        return(outcome);
    }

    public ESRMethodOutcome updateFavourites(String simplifiedID, FavouriteTypes favouriteType, FavouriteListESDT favourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updateFavourites():Entry");
        ESRMethodOutcome outcome = this.getResource(simplifiedID.toLowerCase());
        if (outcome.getEntry() != null) {
            getLogger().trace(".updateFavourites(): found Practitioner resource, updating");
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            boolean shouldBeUpdated = false;
            
            switch (favouriteType) {
                case PRACTITIONER_ROLE_FAVOURITES: {
                	practitioner.getPractitionerRoleFavourites().getFavourites().clear();
                    practitioner.getPractitionerRoleFavourites().getFavourites().addAll(favourites.getFavourites());
                    shouldBeUpdated = true;
                    break;
                }
                case PRACTITIONER_FAVOURITES: {
                	practitioner.getPractitionerFavourites().getFavourites().clear();
                    practitioner.getPractitionerFavourites().getFavourites().addAll(favourites.getFavourites());
                    shouldBeUpdated = true;
                    break;
                }
                case SERVICE_FAVOURITES: {
                	practitioner.getHealthcareServiceFavourites().getFavourites().clear();
                    practitioner.getHealthcareServiceFavourites().getFavourites().addAll(favourites.getFavourites());
                    shouldBeUpdated = true;
                    break;
                }
                default: {
                    // do nothing (and return an empty set)
                }
            }
            if(shouldBeUpdated){
                ESRMethodOutcome updatePractitionerOutcome = updatePractitioner(practitioner);
                outcome.setStatus(updatePractitionerOutcome.getStatus());
                outcome.setEntry(updatePractitionerOutcome.getEntry());
                outcome.setId(updatePractitionerOutcome.getId());
            }
        }
        return(outcome);
    }

    //
    // Delete
    //
    public ESRMethodOutcome deletePractitioner(PractitionerESR entry){
        ESRMethodOutcome outcome = new ESRMethodOutcome();

        return(outcome);
    }
  
}
