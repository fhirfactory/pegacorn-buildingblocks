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
import net.fhirfactory.pegacorn.internals.directories.cache.LocalPractitionerRoleCache;
import net.fhirfactory.pegacorn.internals.directories.cache.common.PegacornDirectoryEntryCache;
import net.fhirfactory.pegacorn.internals.directories.entries.GroupDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerRoleDirectoryEntry;
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
public class PractitionerRoleDirectoryResourceBroker extends ResourceDirectoryBroker {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleDirectoryResourceBroker.class);

    @Inject
    private LocalPractitionerRoleCache practitionerRoleCache;

    @Inject
    private GroupDirectoryResourceBroker groupBroker;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    protected PegacornDirectoryEntryCache specifyCache(){
        return(practitionerRoleCache);
    }

    //
    // Create
    //
    public DirectoryMethodOutcome createPractitionerRole(PractitionerRoleDirectoryEntry directoryEntry){
        getLogger().info(".createPractitionerRole(): Entry, directoryEntry --> {}", directoryEntry);
        DirectoryMethodOutcome outcome = practitionerRoleCache.addPractitionerRole(directoryEntry);
        GroupDirectoryEntry activePractitionerSet = new GroupDirectoryEntry();
        activePractitionerSet.setGroupManager(directoryEntry.getIdentifierWithType("ShortName"));
        activePractitionerSet.setSystemManaged(true);
        activePractitionerSet.setGroupType(SystemManagedGroupTypesEnum.PRACTITIONEROLE_MAP_PRACTITIONER_GROUP.getTypeCode());
        activePractitionerSet.setDisplayName("Practitioners-Fulfilling-PractitionerRole-"+directoryEntry.getIdentifierWithType("ShortName").getValue());
        activePractitionerSet.getIdentifiers().add(directoryEntry.getIdentifierWithType("ShortName"));
        DirectoryMethodOutcome groupCreateOutcome = groupBroker.createGroup(activePractitionerSet);
        getLogger().info(".createPractitionerRole(): Exit");
        return(outcome);
    }

    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(PegacornDirectoryEntry entry) {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        PractitionerRoleDirectoryEntry practitionerRoleDirectoryEntry = (PractitionerRoleDirectoryEntry) entry;
        DirectoryMethodOutcome groupGetOutcome = groupBroker.searchForDirectoryResourceRoleUsingIdentifier(entry.getIdentifierWithType("ShortName"));
        if(groupGetOutcome.isSearch()) {
            if (!groupGetOutcome.getSearchResult().isEmpty()) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a search and found directory entry, using first");
                GroupDirectoryEntry practitionerRolesGroup = (GroupDirectoryEntry) groupGetOutcome.getSearchResult().get(0);
                practitionerRoleDirectoryEntry.getActivePractitionerSet().clear();
                practitionerRoleDirectoryEntry.getActivePractitionerSet().addAll(practitionerRolesGroup.getGroupMembership());
            }
        } else {
            if (groupGetOutcome.getEntry() != null) {
                getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): found associated Group entry");
                GroupDirectoryEntry practitionerRolesGroup = (GroupDirectoryEntry) groupGetOutcome.getEntry();
                practitionerRoleDirectoryEntry.getActivePractitionerSet().clear();
                practitionerRoleDirectoryEntry.getActivePractitionerSet().addAll(practitionerRolesGroup.getGroupMembership());
            }
        }
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //
    public DirectoryMethodOutcome updatePractitionerRole(PractitionerRoleDirectoryEntry entry){
        LOG.info(".updatePractitionerRole(): Entry");
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        PractitionerRoleDirectoryEntry foundEntry = null;
        if(entry.getId() != null){
            foundEntry = (PractitionerRoleDirectoryEntry) practitionerRoleCache.getCacheEntry(entry.getId().getValue());
        } else {
            IdentifierDE entryIdentifier = entry.getIdentifierWithType("ShortName");
            if(entryIdentifier != null){
                if(entryIdentifier.getUse().equals(IdentifierDEUseEnum.OFFICIAL)){
                    DirectoryMethodOutcome practitionerRoleQueryOutcome = practitionerRoleCache.searchCacheForEntryUsingIdentifierDE(entryIdentifier);
                    boolean searchCompleted = practitionerRoleQueryOutcome.getStatus().equals(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                    boolean searchFoundSomething = practitionerRoleQueryOutcome.getSearchResult().size() == 1;
                    if(searchCompleted && searchFoundSomething) {
                        foundEntry = (PractitionerRoleDirectoryEntry) practitionerRoleQueryOutcome.getSearchResult().get(0);
                        outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
                    }
                }
            }
        }
        if(foundEntry == null){
            entry.generateId();
            practitionerRoleCache.addPractitionerRole(entry);
            outcome.setId(entry.getId());
            outcome.setEntry(entry);
            outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE);
        }
        if(outcome.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL) ||outcome.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            getLogger().info(".updatePractitioner(): Entry itself is updated, so updating its associated fulfilledPractitionerRole details");
            DirectoryMethodOutcome practitionersGroupGetOutcome = groupBroker.searchForDirectoryResourceRoleUsingIdentifier(entry.getIdentifierWithType("ShortName"));
            boolean searchCompleted = practitionersGroupGetOutcome.getStatus().equals(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            boolean searchFoundSomething = practitionersGroupGetOutcome.getSearchResult().size() == 1;
            if(searchCompleted && searchFoundSomething){
                getLogger().info(".updatePractitioner(): updating the associated group");
                GroupDirectoryEntry practitionerRolesGroup = (GroupDirectoryEntry)practitionersGroupGetOutcome.getSearchResult().get(0);
                practitionerRolesGroup.getGroupMembership().clear();
                practitionerRolesGroup.getGroupMembership().addAll(entry.getActivePractitionerSet());
                groupBroker.updateGroup(practitionerRolesGroup);
            }
        }
        LOG.info(".updatePractitionerRole(): Exit");
        return(outcome);
    }

    //
    // Delete
    //
    public DirectoryMethodOutcome deletePractitionerRole(PractitionerRoleDirectoryEntry entry){
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();

        return(outcome);
    }


}
