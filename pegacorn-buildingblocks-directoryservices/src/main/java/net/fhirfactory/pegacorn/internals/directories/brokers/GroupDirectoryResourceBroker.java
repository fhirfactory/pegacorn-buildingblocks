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
import net.fhirfactory.pegacorn.internals.directories.cache.LocalGroupCache;
import net.fhirfactory.pegacorn.internals.directories.cache.PractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.directories.cache.common.PegacornDirectoryEntryCache;
import net.fhirfactory.pegacorn.internals.directories.entries.GroupDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GroupDirectoryResourceBroker extends ResourceDirectoryBroker {
    private static final Logger LOG = LoggerFactory.getLogger(GroupDirectoryResourceBroker.class);

    @Inject
    private LocalGroupCache groupCache;

    @Inject
    private PractitionerRoleMapCache roleMapCache;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    protected PegacornDirectoryEntryCache specifyCache(){
        return(groupCache);
    }

    //
    // Create
    //
    public DirectoryMethodOutcome createGroup(GroupDirectoryEntry directoryEntry){
        getLogger().info(".createGroup(): Entry, directoryEntry --> {}", directoryEntry);
        DirectoryMethodOutcome outcome = groupCache.addGroup(directoryEntry);
        if(directoryEntry.isSystemManaged() && !directoryEntry.getGroupMembership().isEmpty()) {
            switch (SystemManagedGroupTypesEnum.fromTypeCode(directoryEntry.getGroupType())) {
                case PRACTITIONEROLE_MAP_PRACTITIONER_GROUP: {
                    roleMapCache.addPractitionerRoleIfAbsent(directoryEntry.getGroupManager());
                    for (IdentifierDE practitionerIdentifier : directoryEntry.getGroupMembership()) {
                        roleMapCache.addPractitionerRoleFulfilledByPractitioner(directoryEntry.getGroupManager(), practitionerIdentifier);
                    }
                    break;
                }
                case PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP: {
                    roleMapCache.addPractitionerIfAbsent(directoryEntry.getGroupManager());
                    for (IdentifierDE practitionerRoleIdentifier : directoryEntry.getGroupMembership()) {
                        roleMapCache.addPractitionerRoleFulfilledByPractitioner(practitionerRoleIdentifier, directoryEntry.getGroupManager());
                    }
                    break;
                }
                case GENERAL:
                default:
                    // Do nothing more for now
            }
        }
        getLogger().info(".createGroup(): Exit, outcome --> {}", outcome.getStatus());
        return(outcome);
    }

    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(PegacornDirectoryEntry directoryEntry) {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry, directoryEntry --> {}", directoryEntry);
        GroupDirectoryEntry groupEntry = (GroupDirectoryEntry) directoryEntry;
        if(directoryEntry.isSystemManaged()) {
            getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Is System Managed");
            switch (SystemManagedGroupTypesEnum.fromTypeCode(groupEntry.getGroupType())) {
                case PRACTITIONEROLE_MAP_PRACTITIONER_GROUP: {
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a PractitionerRoleMap->Practitioner group");
                    List<IdentifierDE> practitionerList = roleMapCache.getListOfPractitionersFulfillingPractitionerRole(groupEntry.getGroupManager());
                    groupEntry.getGroupMembership().clear();
                    groupEntry.getGroupMembership().addAll(practitionerList);
                    break;
                }
                case PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP: {
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a PractitionerRoleMap->PractitionerRole group");
                    List<IdentifierDE> practitionerRoleList = roleMapCache.getListOfPractitionerRolesFulfilledByPractitioner(groupEntry.getGroupManager());
                    groupEntry.getGroupMembership().clear();
                    groupEntry.getGroupMembership().addAll(practitionerRoleList);
                    break;
                }
                case GENERAL:
                default:
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): unknown type");
                    // Do nothing more for now
            }
        }
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //

    public DirectoryMethodOutcome updateGroup(GroupDirectoryEntry directoryEntry){
        getLogger().info(".updateGroup(): Entry, directoryEntry --> {}", directoryEntry);
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        GroupDirectoryEntry groupEntry = null;
        DirectoryMethodOutcome groupOutcome = null;
        if(directoryEntry.getId() != null){
            getLogger().info(".updateGroup(): PegId is not null, so retrieving DirectoryEntry");
            groupEntry = (GroupDirectoryEntry) groupCache.getCacheEntry(directoryEntry.getId().getValue());
            groupOutcome = new DirectoryMethodOutcome();
            groupOutcome.setEntry(groupEntry);
            getLogger().info(".updateGroup(): There are no modifiable attributes in the Group PegacornDirectoryEntry superclass, only membership");
            groupOutcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
        }
        if(groupEntry == null){
            getLogger().info(".updateGroup(): PegId is not null, so retrieving DirectoryEntry");
            directoryEntry.generateId();
            groupOutcome = groupCache.addGroup(directoryEntry);
            if(groupOutcome.isCreated()){
                groupOutcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            }
            groupEntry = (GroupDirectoryEntry) groupOutcome.getEntry();
        }
        if(groupOutcome.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            getLogger().info(".updateGroup(): So far, update was successful, so let's update the membership details");
            if(directoryEntry.isSystemManaged()){
                getLogger().info(".updateGroup(): Is a SystemManaged room, type equals --> {}", groupEntry.getGroupType());
                switch (SystemManagedGroupTypesEnum.fromTypeCode(groupEntry.getGroupType())) {
                    case PRACTITIONEROLE_MAP_PRACTITIONER_GROUP: {
                        getLogger().info(".updateGroup(): Is a PractitionerRoleMap Practitioner Group");
                        List<IdentifierDE> currentStatePractitionerRoleFulfillmentList = roleMapCache.getListOfPractitionersFulfillingPractitionerRole(groupEntry.getGroupManager());
                        List<IdentifierDE> futureStatePractitionerRoleFulfillmentList = groupEntry.getGroupMembership();
                        ArrayList<IdentifierDE> practitionersToRemove = new ArrayList<>();
                        ArrayList<IdentifierDE> practitionersToAdd = new ArrayList<>();
                        for(IdentifierDE currentIncludedPractitioner: currentStatePractitionerRoleFulfillmentList){
                            if(!futureStatePractitionerRoleFulfillmentList.contains(currentIncludedPractitioner)){
                                practitionersToRemove.add(currentIncludedPractitioner);
                            }
                        }
                        for(IdentifierDE futureIncludedPractitioner: futureStatePractitionerRoleFulfillmentList){
                            if(!currentStatePractitionerRoleFulfillmentList.contains(futureIncludedPractitioner)){
                                practitionersToAdd.add(futureIncludedPractitioner);
                            }
                        }
                        for(IdentifierDE practitionerToRemove: practitionersToRemove){
                            roleMapCache.removePractitionerRoleFulfilledByPractitioner(groupEntry.getGroupManager(), practitionerToRemove);
                        }
                        for(IdentifierDE practitionerToAdd: practitionersToAdd){
                            roleMapCache.addPractitionerRoleFulfilledByPractitioner(groupEntry.getGroupManager(), practitionerToAdd);
                        }
                        break;
                    }
                    case PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP: {
                        getLogger().info(".updateGroup(): Is a PractitionerRoleMap PractitionerRole Group");
                        List<IdentifierDE> currentStatePractitionerFulfilledPractitionerRoleList = roleMapCache.getListOfPractitionerRolesFulfilledByPractitioner(groupEntry.getGroupManager());
                        List<IdentifierDE> futureStatePractitionerFulfilledPractitionerRoleList = groupEntry.getGroupMembership();
                        ArrayList<IdentifierDE> practitionerRolesToRemove = new ArrayList<>();
                        ArrayList<IdentifierDE> practitionerRolesToAdd = new ArrayList<>();
                        for(IdentifierDE currentIncludedPractitionerRole: currentStatePractitionerFulfilledPractitionerRoleList){
                            if(!futureStatePractitionerFulfilledPractitionerRoleList.contains(currentIncludedPractitionerRole)){
                                practitionerRolesToRemove.add(currentIncludedPractitionerRole);
                            }
                        }
                        for(IdentifierDE futureIncludedPractitionerRole: futureStatePractitionerFulfilledPractitionerRoleList){
                            if(!currentStatePractitionerFulfilledPractitionerRoleList.contains(futureIncludedPractitionerRole)){
                                practitionerRolesToAdd.add(futureIncludedPractitionerRole);
                            }
                        }
                        if(getLogger().isInfoEnabled()){
                            getLogger().info(".updateGroup(): Number of PractitionerRoles to be added --> {}", practitionerRolesToAdd.size());
                            getLogger().info(".updateGroup(): Number of PractitionerRoles to be removed --> {}", practitionerRolesToRemove.size());
                        }
                        for(IdentifierDE practitionerRoleToRemove: practitionerRolesToRemove){
                            roleMapCache.removePractitionerRoleFulfilledByPractitioner(practitionerRoleToRemove, groupEntry.getGroupManager());
                        }
                        for(IdentifierDE practitionerRoleToAdd: practitionerRolesToAdd){
                            roleMapCache.addPractitionerRoleFulfilledByPractitioner(practitionerRoleToAdd, groupEntry.getGroupManager());
                        }
                        break;
                    }
                    case GENERAL:
                    default:
                        getLogger().info(".updateGroup(): Not presently supported");
                }
            } else {
                // synchronise membership
            }
        }
        getLogger().info(".updateGroup(): Exit");
        return(outcome);
    }

    //
    // Delete
    //

}
