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
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.GroupESR;
import net.fhirfactory.buildingblocks.esr.models.resources.RoleHistoryDetail;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.GroupESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;

@ApplicationScoped
public class GroupESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(GroupESRBroker.class);

    @Inject
    private GroupESRCache groupCache;

    @Inject
    private PractitionerRoleMapCache roleMapCache;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    protected PegacornESRCache specifyCache(){
        return(groupCache);
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
        resource.assignSimplifiedID(resource.getDisplayName(), "Resource.displayName");
    }

    //
    // Create
    //
    public ESRMethodOutcome createGroupDE(GroupESR directoryEntry){
        getLogger().info(".createGroup(): Entry, directoryEntry --> {}", directoryEntry);
        ESRMethodOutcome outcome = groupCache.addGroup(directoryEntry);
        if(directoryEntry.isSystemManaged() && !directoryEntry.getGroupMembership().isEmpty()) {
            switch (SystemManagedGroupTypesEnum.fromTypeCode(directoryEntry.getGroupType())) {
                case PRACTITIONEROLE_MAP_PRACTITIONER_GROUP: {
                    roleMapCache.addPractitionerRoleIfAbsent(directoryEntry.getGroupManager());
                    
                    
                    for (RoleHistoryDetail practitionerRID : directoryEntry.getRoleHistory().getAllCurrentRoles()) {
                        roleMapCache.addPractitionerRoleFulfilledByPractitioner(directoryEntry.getGroupManager(), practitionerRID.getIdentifier());
                    }
                    break;
                }
                case PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP: {
                    roleMapCache.addPractitionerIfAbsent(directoryEntry.getGroupManager());
                    for (RoleHistoryDetail practitionerRoleRID : directoryEntry.getRoleHistory().getAllCurrentRoles()) {
                        roleMapCache.addPractitionerRoleFulfilledByPractitioner(practitionerRoleRID.getIdentifier(), directoryEntry.getGroupManager());
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
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource directoryEntry) {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry, directoryEntry --> {}", directoryEntry);
        GroupESR groupEntry = (GroupESR) directoryEntry;
        if(directoryEntry.isSystemManaged()) {
            getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Is System Managed");
            switch (SystemManagedGroupTypesEnum.fromTypeCode(groupEntry.getGroupType())) {
                case PRACTITIONEROLE_MAP_PRACTITIONER_GROUP: {
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a PractitionerRoleMap->Practitioner group");
                    List<String> practitionerList = roleMapCache.getListOfPractitionersFulfillingPractitionerRole(groupEntry.getGroupManager());
                    groupEntry.getRoleHistory().update(practitionerList);
                    break;
                }
                case PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP: {
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a PractitionerRoleMap->PractitionerRole group");
                    List<String> practitionerRoleList = roleMapCache.getListOfPractitionerRolesFulfilledByPractitioner(groupEntry.getGroupManager());
                    groupEntry.getRoleHistory().update(practitionerRoleList);
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

    public ESRMethodOutcome updateGroup(GroupESR group){
        getLogger().debug(".updateGroup(): Entry, group --> {}", group);
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        GroupESR cachedGroup = null;
        ESRMethodOutcome groupOutcome = null;
        if(group.getSimplifiedID() != null){
            getLogger().trace(".updateGroup(): PegId is not null, so retrieving DirectoryEntry");
            cachedGroup = (GroupESR) groupCache.getCacheEntry(group.getSimplifiedID());
            groupOutcome = new ESRMethodOutcome();
            groupOutcome.setEntry(cachedGroup);
            getLogger().info(".updateGroup(): There are no modifiable attributes in the Group PegacornDirectoryEntry superclass, only membership");
            groupOutcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            groupOutcome.setId(cachedGroup.getSimplifiedID());
        }
        if(cachedGroup == null){
            getLogger().trace(".updateGroup(): PegId is not null, so retrieving DirectoryEntry");
            assignSimplifiedID(group);
            groupOutcome = groupCache.addGroup(group);
            if(groupOutcome.isCreated()){
                groupOutcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            }
            cachedGroup = (GroupESR) groupOutcome.getEntry();
            groupOutcome.setId(cachedGroup.getSimplifiedID());
            groupOutcome.setEntry(cachedGroup);
        }
        if(groupOutcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            getLogger().trace(".updateGroup(): So far, update was successful, so let's update the membership details");
            if(group.isSystemManaged()){
                getLogger().trace(".updateGroup(): Is a SystemManaged room, type equals --> {}", cachedGroup.getGroupType());
                switch (SystemManagedGroupTypesEnum.fromTypeCode(cachedGroup.getGroupType())) {
                    case PRACTITIONEROLE_MAP_PRACTITIONER_GROUP: {
                        getLogger().trace(".updateGroup(): Is a PractitionerRoleMap Practitioner Group");
                        List<String> currentStatePractitionerRoleFulfillmentList = roleMapCache.getListOfPractitionersFulfillingPractitionerRole(cachedGroup.getGroupManager());
                        List<String> futureStatePractitionerRoleFulfillmentList = cachedGroup.getRoleHistory().getAllCurrentRolesAsString();
                        ArrayList<String> practitionersToRemove = new ArrayList<>();
                        ArrayList<String> practitionersToAdd = new ArrayList<>();
                        for(String currentIncludedPractitioner: currentStatePractitionerRoleFulfillmentList){
                            if(!futureStatePractitionerRoleFulfillmentList.contains(currentIncludedPractitioner)){
                                practitionersToRemove.add(currentIncludedPractitioner);
                            }
                        }
                        for(String futureIncludedPractitioner: futureStatePractitionerRoleFulfillmentList){
                            if(!currentStatePractitionerRoleFulfillmentList.contains(futureIncludedPractitioner)){
                                practitionersToAdd.add(futureIncludedPractitioner);
                            }
                        }
                        for(String practitionerToRemove: practitionersToRemove){
                            roleMapCache.removePractitionerRoleFulfilledByPractitioner(cachedGroup.getGroupManager(), practitionerToRemove);
                        }
                        for(String practitionerToAdd: practitionersToAdd){
                            roleMapCache.addPractitionerRoleFulfilledByPractitioner(cachedGroup.getGroupManager(), practitionerToAdd);
                        }
                        break;
                    }
                    case PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP: {
                        getLogger().trace(".updateGroup(): Is a PractitionerRoleMap PractitionerRole Group");
                        List<String> currentStatePractitionerFulfilledPractitionerRoleList = roleMapCache.getListOfPractitionerRolesFulfilledByPractitioner(cachedGroup.getGroupManager());
                        List<String> futureStatePractitionerFulfilledPractitionerRoleList = cachedGroup.getRoleHistory().getAllCurrentRolesAsString();
                        ArrayList<String> practitionerRolesToRemove = new ArrayList<>();
                        ArrayList<String> practitionerRolesToAdd = new ArrayList<>();
                        for(String currentIncludedPractitionerRole: currentStatePractitionerFulfilledPractitionerRoleList){
                            if(!futureStatePractitionerFulfilledPractitionerRoleList.contains(currentIncludedPractitionerRole)){
                                practitionerRolesToRemove.add(currentIncludedPractitionerRole);
                            }
                        }
                        for(String futureIncludedPractitionerRole: futureStatePractitionerFulfilledPractitionerRoleList){
                            if(!currentStatePractitionerFulfilledPractitionerRoleList.contains(futureIncludedPractitionerRole)){
                                practitionerRolesToAdd.add(futureIncludedPractitionerRole);
                            }
                        }
                        if(getLogger().isTraceEnabled()){
                            getLogger().trace(".updateGroup(): Number of PractitionerRoles to be added --> {}", practitionerRolesToAdd.size());
                            getLogger().trace(".updateGroup(): Number of PractitionerRoles to be removed --> {}", practitionerRolesToRemove.size());
                        }
                        for(String practitionerRoleToRemove: practitionerRolesToRemove){
                            roleMapCache.removePractitionerRoleFulfilledByPractitioner(practitionerRoleToRemove, cachedGroup.getGroupManager());
                        }
                        for(String practitionerRoleToAdd: practitionerRolesToAdd){
                            roleMapCache.addPractitionerRoleFulfilledByPractitioner(practitionerRoleToAdd, cachedGroup.getGroupManager());
                        }
                        break;
                    }
                    case GENERAL:
                    default:
                        getLogger().trace(".updateGroup(): Not presently supported");
                }
            } else {
                // synchronise membership
            }
        }
        outcome.setStatus(groupOutcome.getStatus());
        outcome.setEntry(groupOutcome.getEntry());
        outcome.setId(groupOutcome.getId());
        getLogger().debug(".updateGroup(): Exit");
        return(outcome);
    }

    //
    // Delete
    //

}
