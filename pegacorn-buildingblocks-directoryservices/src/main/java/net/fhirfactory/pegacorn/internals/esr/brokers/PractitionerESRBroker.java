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

import net.fhirfactory.pegacorn.deployment.communicate.matrix.CommunicateSystemManagedRoomNames;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.GroupESR;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.PractitionerRoleListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;

import javax.inject.Inject;


public abstract class PractitionerESRBroker extends ESRBroker {

    @Inject
    private PractitionerESRCache practitionerCache;

    @Inject
    private GroupESRBroker groupBroker;

    @Inject
    private CommunicateSystemManagedRoomNames managedRoomNames;

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
        resource.assignSimplifiedID(true, IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_EMAIL_ADDRESS.getIdentifierType(), IdentifierESDTUseEnum.OFFICIAL);
    }

    //
    // Create
    //
    public ESRMethodOutcome createPractitionerDE(PractitionerESR entry){
        getLogger().debug(".createPractitioner(): Entry");
        ESRMethodOutcome outcome = practitionerCache.addPractitioner(entry);
        GroupESR activePractitionerSet = new GroupESR();
        activePractitionerSet.setGroupManager(entry.getSimplifiedID());
        activePractitionerSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP.getTypeCode());
        activePractitionerSet.setSystemManaged(true);
        activePractitionerSet.setDisplayName("PractitionerRoles-Fulfilled-by-Practitioner-"+entry.getIdentifierWithType("EmailAddress").getValue());
        activePractitionerSet.getIdentifiers().add(entry.getIdentifierWithType("EmailAddress"));
        ESRMethodOutcome groupCreateOutcome = groupBroker.createGroupDE(activePractitionerSet);
        //createSystemManagedMatrixRooms(entry);
        getLogger().debug(".createPractitioner(): Exit");
        return(outcome);
    }

    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
        getLogger().debug(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        PractitionerESR practitionerESR = (PractitionerESR) entry;
        ESRMethodOutcome groupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("EmailAddress"));
        if(groupGetOutcome.isSearch()){
            if (!groupGetOutcome.getSearchResult().isEmpty()) {
                getLogger().trace(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
                GroupESR practitionerRolesGroup = (GroupESR) groupGetOutcome.getSearchResult().get(0);
                practitionerESR.getCurrentPractitionerRoles().clear();
                practitionerESR.getCurrentPractitionerRoles().addAll(practitionerRolesGroup.getGroupMembership());
            }
        } else {
            if (groupGetOutcome.getEntry() != null) {
                getLogger().trace(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
                GroupESR practitionerRolesGroup = (GroupESR) groupGetOutcome.getEntry();
                practitionerESR.getCurrentPractitionerRoles().clear();
                practitionerESR.getCurrentPractitionerRoles().addAll(practitionerRolesGroup.getGroupMembership());
            }
        }
        getLogger().debug(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //

    public ESRMethodOutcome updatePractitioner(PractitionerESR entry) throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitioner(): Entry");
        ESRMethodOutcome entryUpdate = updateDirectoryEntry(entry);
        if(entryUpdate.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL) || entryUpdate.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            getLogger().trace(".updatePractitioner(): Entry itself is updated, so updating its associated fulfilledPractitionerRole details");
            ESRMethodOutcome practitionerRolesGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("EmailAddress"));
            boolean searchCompleted = practitionerRolesGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            boolean searchFoundOneResultOnly = practitionerRolesGroupGetOutcome.getSearchResult().size() == 1;
            if(searchCompleted && searchFoundOneResultOnly && practitionerRolesGroupGetOutcome.isSearchSuccessful()){
                getLogger().trace(".updatePractitioner(): updating the associated group");
                GroupESR practitionerRolesGroup = (GroupESR)practitionerRolesGroupGetOutcome.getSearchResult().get(0);
                practitionerRolesGroup.getGroupMembership().clear();
                practitionerRolesGroup.getGroupMembership().addAll(entry.getCurrentPractitionerRoles());
                ESRMethodOutcome groupUpdateOutcome = groupBroker.updateGroup(practitionerRolesGroup);
            }
        } else {
            getLogger().trace(".updatePractitioner(): Update processed failed for the superclass PegacornDirectoryEntry, reason --> {}", entryUpdate.getStatusReason());
        }
        getLogger().debug(".updatePractitioner(): Exit");
        return(entryUpdate);
    }

    public ESRMethodOutcome updatePractitionerRoles(String simplifiedID, PractitionerRoleListESDT updatePractitionerRoles) throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerRoles():Entry");
        ESRMethodOutcome outcome = this.getResource(simplifiedID.toLowerCase());
        if (outcome.getEntry() != null) {
            getLogger().trace(".updatePractitionerRoles():Found Resource, so updating fulfilledPractitionerRole details");
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            ESRMethodOutcome practitionerRolesGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(practitioner.getIdentifierWithType("EmailAddress"));
            boolean searchCompleted = practitionerRolesGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY) || outcome.getStatus().equals(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
            boolean searchFoundOneResultOnly = practitionerRolesGroupGetOutcome.getSearchResult().size() == 1;
            if (searchCompleted && searchFoundOneResultOnly && practitionerRolesGroupGetOutcome.isSearchSuccessful()) {
                getLogger().trace(".updatePractitionerRoles(): updating the associated group");
                GroupESR practitionerRolesGroup = (GroupESR) practitionerRolesGroupGetOutcome.getSearchResult().get(0);
                practitionerRolesGroup.getGroupMembership().clear();
                practitionerRolesGroup.getGroupMembership().addAll(updatePractitionerRoles.getPractitionerRoles());
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
        } else {
            outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            outcome.setStatusReason("Could not update associated Resource");
        }
        getLogger().debug(".updatePractitionerRoles():Exit");
        return(outcome);
    }

    public ESRMethodOutcome updateFavourites(String simplifiedID, String favouriteType, FavouriteListESDT favourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updateFavourites():Entry");
        ESRMethodOutcome outcome = this.getResource(simplifiedID.toLowerCase());
        if (outcome.getEntry() != null) {
            getLogger().trace(".updateFavourites(): found Practitioner resource, updating");
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            boolean shouldBeUpdated = false;
            switch (favouriteType) {
                case "PractitionerRoleFavourites": {
                    practitioner.getPractitionerRoleFavourites().getFavourites().addAll(favourites.getFavourites());
                    shouldBeUpdated = true;
                    break;
                }
                case "PractitionerFavourites": {
                    practitioner.getPractitionerFavourites().getFavourites().addAll(favourites.getFavourites());
                    shouldBeUpdated = true;
                    break;
                }
                case "ServiceFavourites": {
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
