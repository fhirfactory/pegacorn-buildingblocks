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
package net.fhirfactory.pegacorn.internals.directories.brokers;

import net.fhirfactory.pegacorn.internals.directories.brokers.common.ResourceDirectoryBroker;
import net.fhirfactory.pegacorn.internals.directories.cache.LocalPractitionerCache;
import net.fhirfactory.pegacorn.internals.directories.cache.common.PegacornDirectoryEntryCache;
import net.fhirfactory.pegacorn.internals.directories.entries.GroupDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDEUseEnum;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PractitionerDirectoryResourceBroker extends ResourceDirectoryBroker {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerDirectoryResourceBroker.class);

    @Inject
    private LocalPractitionerCache practitionerCache;

    @Inject
    private GroupDirectoryResourceBroker groupBroker;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    protected PegacornDirectoryEntryCache specifyCache(){
        return(practitionerCache);
    }

    //
    // Create
    //
    public DirectoryMethodOutcome createPractitioner(PractitionerDirectoryEntry entry){
        getLogger().info(".createPractitioner(): Entry");
        DirectoryMethodOutcome outcome = practitionerCache.addPractitioner(entry);
        GroupDirectoryEntry activePractitionerSet = new GroupDirectoryEntry();
        activePractitionerSet.setGroupManager(entry.getIdentifierWithType("EmailAddress"));
        activePractitionerSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP.getTypeCode());
        activePractitionerSet.setSystemManaged(true);
        activePractitionerSet.setDisplayName("PractitionerRoles-Fulfilled-by-Practitioner-"+entry.getIdentifierWithType("EmailAddress").getValue());
        activePractitionerSet.getIdentifiers().add(entry.getIdentifierWithType("EmailAddress"));
        DirectoryMethodOutcome groupCreateOutcome = groupBroker.createGroup(activePractitionerSet);
        getLogger().info(".createPractitioner(): Entry");
        return(outcome);
    }



    //
    // Review
    //


    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(PegacornDirectoryEntry entry) {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        PractitionerDirectoryEntry practitionerDirectoryEntry = (PractitionerDirectoryEntry) entry;
        DirectoryMethodOutcome groupGetOutcome = groupBroker.searchForDirectoryResourceRoleUsingIdentifier(entry.getIdentifierWithType("EmailAddress"));
        if(groupGetOutcome.isSearch()){
            if (!groupGetOutcome.getSearchResult().isEmpty()) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
                GroupDirectoryEntry practitionerRolesGroup = (GroupDirectoryEntry) groupGetOutcome.getSearchResult().get(0);
                practitionerDirectoryEntry.getCurrentPractitionerRoles().clear();
                practitionerDirectoryEntry.getCurrentPractitionerRoles().addAll(practitionerRolesGroup.getGroupMembership());
            }
        } else {
            if (groupGetOutcome.getEntry() != null) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
                GroupDirectoryEntry practitionerRolesGroup = (GroupDirectoryEntry) groupGetOutcome.getEntry();
                practitionerDirectoryEntry.getCurrentPractitionerRoles().clear();
                practitionerDirectoryEntry.getCurrentPractitionerRoles().addAll(practitionerRolesGroup.getGroupMembership());
            }
        }
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //

    public DirectoryMethodOutcome updatePractitioner(PractitionerDirectoryEntry entry){
        getLogger().info(".updatePractitioner(): Entry");
        DirectoryMethodOutcome entryUpdate = updateDirectoryEntry(entry);
        if(entryUpdate.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL) || entryUpdate.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            getLogger().info(".updatePractitioner(): Entry itself is updated, so updating its associated fulfilledPractitionerRole details");
            DirectoryMethodOutcome practitionerRolesGroupGetOutcome = groupBroker.searchForDirectoryResourceRoleUsingIdentifier(entry.getIdentifierWithType("EmailAddress"));
            boolean searchCompleted = practitionerRolesGroupGetOutcome.getStatus().equals(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            boolean searchFoundOneResultOnly = practitionerRolesGroupGetOutcome.getSearchResult().size() == 1;
            if(searchCompleted && searchFoundOneResultOnly && practitionerRolesGroupGetOutcome.isSearchSuccessful()){
                getLogger().info(".updatePractitioner(): updating the associated group");
                GroupDirectoryEntry practitionerRolesGroup = (GroupDirectoryEntry)practitionerRolesGroupGetOutcome.getSearchResult().get(0);
                practitionerRolesGroup.getGroupMembership().clear();
                practitionerRolesGroup.getGroupMembership().addAll(entry.getCurrentPractitionerRoles());
                DirectoryMethodOutcome groupUpdateOutcome = groupBroker.updateGroup(practitionerRolesGroup);
            }
        } else {
            getLogger().info(".updatePractitioner(): Update processed failed for the superclass PegacornDirectoryEntry, reason --> {}", entryUpdate.getStatusReason());
        }
        getLogger().info(".updatePractitioner(): Exit");
        return(entryUpdate);
    }

    //
    // Delete
    //
    public DirectoryMethodOutcome deletePractitioner(PractitionerDirectoryEntry entry){
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();

        return(outcome);
    }

    //
    // Search (by Identifier)
    //
    public DirectoryMethodOutcome searchForPractitionerUsingIdentifier(IdentifierDE identifier){
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        PegacornDirectoryEntry entry = practitionerCache.searchForPractitioner(identifier);
        if(entry == null){
            outcome.setSearchSuccessful(false);
        } else {
            outcome.setSearchSuccessful(true);
            outcome.getSearchResult().add(entry);
            outcome.setId(entry.getId());
        }
        return(outcome);
    }
}
