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

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.GroupESR;
import net.fhirfactory.buildingblocks.esr.models.resources.MatrixRoomESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerRoleListESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.deployment.communicate.matrix.SystemManagedRoomNames;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;

@ApplicationScoped
public class PractitionerESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerESRBroker.class);

    @Inject
    private PractitionerESRCache practitionerCache;

    @Inject
    private GroupESRBroker groupBroker;

    @Inject
    private SystemManagedRoomNames managedRoomNames;

    @Inject
    private MatrixRoomESRBroker matrixRoomDirectoryResourceBroker;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    protected PegacornESRCache specifyCache(){
        return(practitionerCache);
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
        resource.assignSimplifiedID(true, getCommonIdentifierTypes().getEmailAddress(), IdentifierESDTUseEnum.OFFICIAL);
    }

    //
    // Create
    //
    public ESRMethodOutcome createPractitionerDE(PractitionerESR entry){
        getLogger().info(".createPractitioner(): Entry");
        ESRMethodOutcome outcome = practitionerCache.addPractitioner(entry);
        GroupESR activePractitionerSet = new GroupESR();
        activePractitionerSet.setGroupManager(entry.getSimplifiedID());
        activePractitionerSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP.getTypeCode());
        activePractitionerSet.setSystemManaged(true);
        activePractitionerSet.setDisplayName("PractitionerRoles-Fulfilled-by-Practitioner-"+entry.getIdentifierWithType("EmailAddress").getValue());
        activePractitionerSet.getIdentifiers().add(entry.getIdentifierWithType("EmailAddress"));
        ESRMethodOutcome groupCreateOutcome = groupBroker.createGroupDE(activePractitionerSet);
        createSystemManagedMatrixRooms(entry);
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
        ESRMethodOutcome groupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("EmailAddress"));
        if(groupGetOutcome.isSearch()){
            if (!groupGetOutcome.getSearchResult().isEmpty()) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
                GroupESR practitionerRolesGroup = (GroupESR) groupGetOutcome.getSearchResult().get(0);
                practitionerESR.setRoleHistory(practitionerRolesGroup.getRoleHistory());
            }
        } else {
            if (groupGetOutcome.getEntry() != null) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
                GroupESR practitionerRolesGroup = (GroupESR) groupGetOutcome.getEntry();
                practitionerESR.setRoleHistory(practitionerRolesGroup.getRoleHistory());
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
            ESRMethodOutcome practitionerRolesGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("EmailAddress"));
            boolean searchCompleted = practitionerRolesGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            boolean searchFoundOneResultOnly = practitionerRolesGroupGetOutcome.getSearchResult().size() == 1;
            if(searchCompleted && searchFoundOneResultOnly && practitionerRolesGroupGetOutcome.isSearchSuccessful()){
                getLogger().info(".updatePractitioner(): updating the associated group");
                GroupESR practitionerRolesGroup = (GroupESR)practitionerRolesGroupGetOutcome.getSearchResult().get(0);
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
            ESRMethodOutcome practitionerRolesGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(practitioner.getIdentifierWithType("EmailAddress"));
            boolean searchCompleted = practitionerRolesGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY) || outcome.getStatus().equals(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
            boolean searchFoundOneResultOnly = practitionerRolesGroupGetOutcome.getSearchResult().size() == 1;
            if (searchCompleted && searchFoundOneResultOnly && practitionerRolesGroupGetOutcome.isSearchSuccessful()) {
                getLogger().trace(".updatePractitionerRoles(): updating the associated group");
                GroupESR practitionerRolesGroup = (GroupESR) practitionerRolesGroupGetOutcome.getSearchResult().get(0);
                
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
                	practitioner.getPractitionerRoleFavourites().getFavourites().clear();
                    practitioner.getPractitionerRoleFavourites().getFavourites().addAll(favourites.getFavourites());
                    shouldBeUpdated = true;
                    break;
                }
                case "PractitionerFavourites": {
                	practitioner.getPractitionerFavourites().getFavourites().clear();
                    practitioner.getPractitionerFavourites().getFavourites().addAll(favourites.getFavourites());
                    shouldBeUpdated = true;
                    break;
                }
                case "ServiceFavourites": {
                	practitioner.getHealthcareServiceFavourites().getFavourites();
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



    //
    // Room Based Services
    //

    protected void createSystemManagedMatrixRooms(PractitionerESR practitioner){
        if(practitioner == null){
            return;
        }
        IdentifierESDT practitionerIdentifier = practitioner.getIdentifierWithType("EmailAddress");
        createSystemManagedMatrixRoom(
                practitioner.getSimplifiedID(),
                managedRoomNames.getPractitionerCallRoom(),
                managedRoomNames.getPractitionerCallRoomAlias(practitioner.getSimplifiedID()),
                managedRoomNames.getPractitionerCallRoomAlias(practitionerIdentifier.getValue()));
        createSystemManagedMatrixRoom(
                practitioner.getSimplifiedID(),
                managedRoomNames.getPractitionerCodeNotificationsRoom(),
                managedRoomNames.getPractitionerCodeNotificationsRoomAlias(practitioner.getSimplifiedID()),
                managedRoomNames.getPractitionerCodeNotificationsRoomAlias(practitionerIdentifier.getValue()));
        createSystemManagedMatrixRoom(
                practitioner.getSimplifiedID(),
                managedRoomNames.getPractitionerMediaRoom(),
                managedRoomNames.getPractitionerMediaRoomAlias(practitioner.getSimplifiedID()),
                managedRoomNames.getPractitionerMediaRoomAlias(practitionerIdentifier.getValue()));
        createSystemManagedMatrixRoom(
                practitioner.getSimplifiedID(),
                managedRoomNames.getPractitionerCriticalResultsNotificationsRoom(),
                managedRoomNames.getPractitionerCriticalResultsNotificationsRoomAlias(practitioner.getSimplifiedID()),
                managedRoomNames.getPractitionerCriticalResultsNotificationsRoomAlias(practitionerIdentifier.getValue()));
        createSystemManagedMatrixRoom(
                practitioner.getSimplifiedID(),
                managedRoomNames.getPractitionerSystemMessagesRoom(),
                managedRoomNames.getPractitionerSystemMessagesRoomAlias(practitioner.getSimplifiedID()),
                managedRoomNames.getPractitionerSystemMessagesRoomAlias(practitionerIdentifier.getValue()));
    }

    protected void createSystemManagedMatrixRoom(String practitionerRecordID, String displayName, String practitionerRecordIDBasedName, String practitionerEmailBasedAlias){
        IdentifierESDT roomIdBasedIdentifier = new IdentifierESDT();
        roomIdBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        roomIdBasedIdentifier.setType(getCommonIdentifierTypes().getMatrixRoomID());
        roomIdBasedIdentifier.setValue(UUID.randomUUID().toString());
        IdentifierESDT roomNameBasedIdentifier = new IdentifierESDT();
        roomNameBasedIdentifier.setUse(IdentifierESDTUseEnum.OFFICIAL);
        roomNameBasedIdentifier.setValue(practitionerRecordIDBasedName);
        roomNameBasedIdentifier.setType(getCommonIdentifierTypes().getMatrixRoomSystemID());
        MatrixRoomESR matrixRoom = new MatrixRoomESR();
        matrixRoom.addIdentifier(roomIdBasedIdentifier);
        matrixRoom.addIdentifier(roomNameBasedIdentifier);
        matrixRoom.setRoomOwner(practitionerRecordID);
        matrixRoom.setSystemManaged(true);
        matrixRoom.setDisplayName(practitionerEmailBasedAlias);
        matrixRoomDirectoryResourceBroker.createMatrixRoomDE(matrixRoom);
    }
}
