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

import javax.inject.Inject;

import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamPractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.GroupESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.ParticipantESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.group.CareTeamsContainingPractitionerRoleGroupESR;
import net.fhirfactory.pegacorn.internals.esr.resources.group.GroupESR;
import net.fhirfactory.pegacorn.internals.esr.resources.group.PractitionerRolesFulfilledByPractitionerGroupESR;
import net.fhirfactory.pegacorn.internals.esr.resources.group.PractitionerRolesInCareTeamGroupESR;
import net.fhirfactory.pegacorn.internals.esr.resources.group.PractitionersFulfillingPractitionerRolesGroupESR;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;


public abstract class GroupESRBroker extends ESRBroker {

    @Inject
    private GroupESRCache groupCache;

    @Inject
    private PractitionerRoleMapCache roleMapCache;
    
    @Inject
    private CareTeamPractitionerRoleMapCache careTeamPractitionerRoleMapCache;

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
                case PRACTITIONERS_FULFILLING_PRACTITIONER_ROLE_GROUP: {
                    roleMapCache.addPractitionerRoleIfAbsent(directoryEntry.getGroupManager());
                    
                    for (String practitionerRID :  ((PractitionersFulfillingPractitionerRolesGroupESR)directoryEntry).getGroupMembership()) {
                        roleMapCache.addPractitionerRoleFulfilledByPractitioner(directoryEntry.getGroupManager(), practitionerRID);
                    }
                    break;
                }
                case PRACTITONER_ROLES_FULFILLED_BY_PRACTITIONER_GROUP: {
                    roleMapCache.addPractitionerIfAbsent(directoryEntry.getGroupManager());
                    
                    for (String practitionerRoleRID : ((PractitionerRolesFulfilledByPractitionerGroupESR)directoryEntry).getGroupMembership()) {
                        roleMapCache.addPractitionerRoleFulfilledByPractitioner(practitionerRoleRID, directoryEntry.getGroupManager());
                    }
                    break;
                }
                case CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP: {
                	careTeamPractitionerRoleMapCache.addPractitionerRoleIfAbsent(directoryEntry.getGroupManager());
                	
                	for (String careTeam : ((CareTeamsContainingPractitionerRoleGroupESR)directoryEntry).getGroupMembership()) {
                		careTeamPractitionerRoleMapCache.addCareTeamToPractitionerRole(directoryEntry.getGroupManager(), careTeam);
                	}
                	
                	break;
                }
                case PRACTITIONER_ROLES_IN_CARE_TEAM_GROUP: {
                	careTeamPractitionerRoleMapCache.addCareTeamIfAbsent(directoryEntry.getGroupManager());
                    
                    for (ParticipantESDT practitionerRole :  ((PractitionerRolesInCareTeamGroupESR)directoryEntry).getGroupMembership()) {                    	
                    	careTeamPractitionerRoleMapCache.addPractitionerRoleToCareTeam(directoryEntry.getGroupManager(), practitionerRole);
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
                case PRACTITIONERS_FULFILLING_PRACTITIONER_ROLE_GROUP: {
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a PractitionerRoleMap->Practitioner group");
                    List<String> practitionerList = roleMapCache.getListOfPractitionersFulfillingPractitionerRole(groupEntry.getGroupManager());
                    ((PractitionersFulfillingPractitionerRolesGroupESR)groupEntry).setGroupMembership(practitionerList);
                    break;
                }
                case PRACTITONER_ROLES_FULFILLED_BY_PRACTITIONER_GROUP: {
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a PractitionerRoleMap->PractitionerRole group");
                    List<String> practitionerRoleList = roleMapCache.getListOfPractitionerRolesFulfilledByPractitioner(groupEntry.getGroupManager());
                    ((PractitionerRolesFulfilledByPractitionerGroupESR)groupEntry).getRoleHistory().update(practitionerRoleList);
                    break;
                }
                case CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP: {
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a PractitionerRoleMap->Practitioner group");
                    List<String> careTeamList = careTeamPractitionerRoleMapCache.getCareTeamsForPractitionerRole(groupEntry.getGroupManager());
                    ((CareTeamsContainingPractitionerRoleGroupESR)groupEntry).setGroupMembership(careTeamList);
                    break;
                }
                case PRACTITIONER_ROLES_IN_CARE_TEAM_GROUP: {
                    getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): is a PractitionerRoleMap->Practitioner group");
                    List<ParticipantESDT> practitionerRoleList = careTeamPractitionerRoleMapCache.getPractitionerRolesWithinCareTeam(groupEntry.getGroupManager());
                    ((PractitionerRolesInCareTeamGroupESR)groupEntry).setGroupMembership(practitionerRoleList);
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
                    case PRACTITIONERS_FULFILLING_PRACTITIONER_ROLE_GROUP: {
                        getLogger().trace(".updateGroup(): Is a PractitionerRoleMap Practitioner Group");
                        List<String> currentStatePractitionerRoleFulfillmentList = roleMapCache.getListOfPractitionersFulfillingPractitionerRole(cachedGroup.getGroupManager());
                        List<String> futureStatePractitionerRoleFulfillmentList = ((PractitionersFulfillingPractitionerRolesGroupESR)cachedGroup).getGroupMembership();
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
                    case PRACTITONER_ROLES_FULFILLED_BY_PRACTITIONER_GROUP: {
                        getLogger().trace(".updateGroup(): Is a PractitionerRoleMap PractitionerRole Group");
                        List<String> currentStatePractitionerFulfilledPractitionerRoleList = roleMapCache.getListOfPractitionerRolesFulfilledByPractitioner(cachedGroup.getGroupManager());
                        List<String> futureStatePractitionerFulfilledPractitionerRoleList = ((PractitionerRolesFulfilledByPractitionerGroupESR)cachedGroup).getGroupMembership();
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
                    case CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP: {
 
                    	 getLogger().trace(".updateGroup(): Is a Practitioner Role Care Team Group");
                         List<String> currentStateCareTeamsForPractitionerRoleList = careTeamPractitionerRoleMapCache.getCareTeamsForPractitionerRole(cachedGroup.getGroupManager());
                         
                         if (currentStateCareTeamsForPractitionerRoleList == null) {
                        	 currentStateCareTeamsForPractitionerRoleList = new ArrayList<>();
                         }
                         
                         List<String> futureStateCareTeamsForPractitionerRoleList = ((CareTeamsContainingPractitionerRoleGroupESR)cachedGroup).getGroupMembership();
                         
                         List<String> careTeamsToRemove = new ArrayList<>();
                         List<String> careTeamsToAdd = new ArrayList<>();
                    
                         for(String currentIncludedPractitioner: currentStateCareTeamsForPractitionerRoleList){
                             if(!futureStateCareTeamsForPractitionerRoleList.contains(currentIncludedPractitioner)){
                            	 careTeamsToRemove.add(currentIncludedPractitioner);
                             }
                         }
                         
                         for(String futureIncludedPractitioner: futureStateCareTeamsForPractitionerRoleList){
                             if(!currentStateCareTeamsForPractitionerRoleList.contains(futureIncludedPractitioner)){
                            	 careTeamsToAdd.add(futureIncludedPractitioner);
                             }
                         }
                         
                         for(String careTeamToRemove: careTeamsToRemove){
                             careTeamPractitionerRoleMapCache.removeCareTeamFromPractitionerRole(cachedGroup.getGroupManager(), careTeamToRemove);
                         }
                         
                         for(String careTeamToAdd: careTeamsToAdd){
                             careTeamPractitionerRoleMapCache.addCareTeamToPractitionerRole(cachedGroup.getGroupManager(), careTeamToAdd);
                         }
                         
                         break;
                    }
                    case PRACTITIONER_ROLES_IN_CARE_TEAM_GROUP: {
                    	
                    	 getLogger().trace(".updateGroup(): Is a Practitioner Role Care Team Group");
                         List<ParticipantESDT> currentStatePractitionerRolesInCareTeamsList = careTeamPractitionerRoleMapCache.getPractitionerRolesWithinCareTeam(cachedGroup.getGroupManager());
                         
                         if (currentStatePractitionerRolesInCareTeamsList == null) {
                        	 currentStatePractitionerRolesInCareTeamsList = new ArrayList<>();
                         }
                         
                         List<ParticipantESDT> futureStatePractitionerRolesInCareTeamList = ((PractitionerRolesInCareTeamGroupESR)cachedGroup).getGroupMembership();
                         
                         List<ParticipantESDT> practitionerRolesToRemove = new ArrayList<>();
                         List<ParticipantESDT> practitionerRolesToAdd = new ArrayList<>();
                    
                         for(ParticipantESDT currentIncludedPractitioner: currentStatePractitionerRolesInCareTeamsList){
                             if(!futureStatePractitionerRolesInCareTeamList.contains(currentIncludedPractitioner)){
                            	 practitionerRolesToRemove.add(currentIncludedPractitioner);
                             }
                         }
                         
                         for(ParticipantESDT futureIncludedPractitioner: futureStatePractitionerRolesInCareTeamList){
                             if(!currentStatePractitionerRolesInCareTeamsList.contains(futureIncludedPractitioner)){
                            	 practitionerRolesToAdd.add(futureIncludedPractitioner);
                             }
                         }
                         
                         for(ParticipantESDT practitionerRoleToRemove: practitionerRolesToRemove){
                             careTeamPractitionerRoleMapCache.removePractitionerRoleFromCareTeam(cachedGroup.getGroupManager(), practitionerRoleToRemove);
                         }
                         
                         for(ParticipantESDT practitionerRoleToAdd: practitionerRolesToAdd){
                             careTeamPractitionerRoleMapCache.addPractitionerRoleToCareTeam(cachedGroup.getGroupManager(), practitionerRoleToAdd);
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
