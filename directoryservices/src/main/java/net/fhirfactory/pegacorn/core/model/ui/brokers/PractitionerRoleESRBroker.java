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
package net.fhirfactory.pegacorn.core.model.ui.brokers;

import net.fhirfactory.pegacorn.core.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.cache.PractitionerRoleESRCache;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.GroupESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PractitionerRoleESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.SystemManagedGroupTypesEnum;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.core.model.ui.cache.common.PegacornESRCache;

import javax.inject.Inject;

public abstract class PractitionerRoleESRBroker extends ESRBroker {

    @Inject
    private PractitionerRoleESRCache practitionerRoleCache;

    @Inject
    private GroupESRBroker groupBroker;

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
        resource.assignSimplifiedID(true, IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_SHORT_NAME.getIdentifierType(), IdentifierESDTUseEnum.OFFICIAL);
    }

    //
    // Create
    //
    public ESRMethodOutcome createPractitionerRole(PractitionerRoleESR directoryEntry){
        getLogger().debug(".createPractitionerRole(): Entry, directoryEntry --> {}", directoryEntry);
        ESRMethodOutcome outcome = practitionerRoleCache.addPractitionerRole(directoryEntry);
        GroupESR activePractitionerSet = new GroupESR();
        activePractitionerSet.setGroupManager(directoryEntry.getSimplifiedID());
        activePractitionerSet.setSystemManaged(true);
        activePractitionerSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITIONEROLE_MAP_PRACTITIONER_GROUP.getTypeCode());
        activePractitionerSet.setDisplayName("Practitioners-Fulfilling-PractitionerRole-"+directoryEntry.getIdentifierWithType("ShortName").getValue());
        activePractitionerSet.getIdentifiers().add(directoryEntry.getIdentifierWithType("ShortName"));
        ESRMethodOutcome groupCreateOutcome = groupBroker.createGroupDE(activePractitionerSet);
        getLogger().debug(".createPractitionerRole(): Exit");
        return(outcome);
    }


    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
        getLogger().debug(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        PractitionerRoleESR practitionerRoleESR = (PractitionerRoleESR) entry;
        ESRMethodOutcome groupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("ShortName"));
        if(groupGetOutcome.isSearch()) {
            if (!groupGetOutcome.getSearchResult().isEmpty()) {
                getLogger().trace(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
                GroupESR practitionerRolesGroup = (GroupESR) groupGetOutcome.getSearchResult().get(0);
                practitionerRoleESR.getActivePractitionerSet().clear();
                practitionerRoleESR.getActivePractitionerSet().addAll(practitionerRolesGroup.getGroupMembership());
            }
        } else {
            if (groupGetOutcome.getEntry() != null) {
                getLogger().trace(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
                GroupESR practitionerRolesGroup = (GroupESR) groupGetOutcome.getEntry();
                practitionerRoleESR.getActivePractitionerSet().clear();
                practitionerRoleESR.getActivePractitionerSet().addAll(practitionerRolesGroup.getGroupMembership());
            }
        }
        getLogger().debug(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //
    public ESRMethodOutcome updatePractitionerRole(PractitionerRoleESR entry) throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerRole(): Entry");
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
            getLogger().trace(".updatePractitioner(): Entry itself is updated, so updating its associated fulfilledPractitionerRole details");
            ESRMethodOutcome practitionersGroupGetOutcome = groupBroker.searchForDirectoryEntryUsingIdentifier(entry.getIdentifierWithType("ShortName"));
            boolean searchCompleted = practitionersGroupGetOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            boolean searchFoundSomething = practitionersGroupGetOutcome.getSearchResult().size() == 1;
            if(searchCompleted && searchFoundSomething){
                getLogger().trace(".updatePractitioner(): updating the associated group");
                GroupESR practitionerRolesGroup = (GroupESR)practitionersGroupGetOutcome.getSearchResult().get(0);
                practitionerRolesGroup.getGroupMembership().clear();
                practitionerRolesGroup.getGroupMembership().addAll(entry.getActivePractitionerSet());
                groupBroker.updateGroup(practitionerRolesGroup);
            }
        }
        getLogger().info(".updatePractitionerRole(): Exit");
        return(outcome);
    }

    //
    // Delete
    //
    public ESRMethodOutcome deletePractitionerRole(PractitionerRoleESR entry){
        ESRMethodOutcome outcome = new ESRMethodOutcome();

        return(outcome);
    }


}
