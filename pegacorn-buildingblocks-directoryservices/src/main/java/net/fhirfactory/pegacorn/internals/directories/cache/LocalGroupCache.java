/*
 * Copyright (c) 2021 Mark Hunter
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
package net.fhirfactory.pegacorn.internals.directories.cache;

import net.fhirfactory.pegacorn.internals.directories.cache.common.PegacornDirectoryEntryCache;
import net.fhirfactory.pegacorn.internals.directories.entries.GroupDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.PegId;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSearchException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSortException;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class LocalGroupCache extends PegacornDirectoryEntryCache {
    private static final Logger LOG = LoggerFactory.getLogger(LocalGroupCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public LocalGroupCache(){
        super();
    }

    public DirectoryMethodOutcome addGroup(GroupDirectoryEntry groupDirectoryEntry){
        DirectoryMethodOutcome outcome = addCacheEntry(groupDirectoryEntry);
        return(outcome);
    }

    public GroupDirectoryEntry getGroup(PegId id){
        PegacornDirectoryEntry foundEntry = this.getCacheEntry(id.getValue());
        GroupDirectoryEntry foundGroupEntry = (GroupDirectoryEntry) foundEntry;
        return(foundGroupEntry);
    }

    public GroupDirectoryEntry getGroup(IdentifierDE groupID){
        PegacornDirectoryEntry foundEntry = this.getCacheEntry(groupID);
        GroupDirectoryEntry foundGroupEntry = (GroupDirectoryEntry) foundEntry;
        return(foundGroupEntry);
    }

    public GroupDirectoryEntry searchForGroup(IdentifierDE groupID){
        PegacornDirectoryEntry foundEntry = null;
        DirectoryMethodOutcome outcome = this.searchCacheForEntryUsingIdentifierDE(groupID);
        outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
        outcome.getSearchResult().add(foundEntry);
        if(foundEntry == null){
            return(null);
        }
        GroupDirectoryEntry foundGroupDirectoryEntry = (GroupDirectoryEntry) foundEntry;
        return(foundGroupDirectoryEntry);
    }

    public DirectoryMethodOutcome addMember(IdentifierDE groupIdentifier, IdentifierDE memberIdentifier){
        GroupDirectoryEntry foundGroup = getGroup(groupIdentifier);
        if(foundGroup == null || memberIdentifier == null){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            outcome.setStatusReason("Group does not exist");
            return(outcome);
        }
        if(foundGroup.getGroupMembership().contains(memberIdentifier)){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        } else {
            foundGroup.getGroupMembership().add(memberIdentifier);
        }
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            outcome.setId(foundGroup.getId());
            outcome.setEntry(foundGroup);
            return (outcome);
    }

    public DirectoryMethodOutcome removeMember(IdentifierDE groupIdentifier, IdentifierDE memberIdentifier){
        GroupDirectoryEntry foundGroup = getGroup(groupIdentifier);
        if(foundGroup == null || memberIdentifier == null){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            outcome.setStatusReason("Group does not exist");
            return(outcome);
        }
        if(foundGroup.getGroupMembership().contains(memberIdentifier)){
            foundGroup.getGroupMembership().remove(memberIdentifier);
        }
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
        outcome.setId(foundGroup.getId());
        outcome.setEntry(foundGroup);
        return(outcome);
    }

    //
    // Search Services
    //

    @Override
    public DirectoryMethodOutcome directoryEntrySpecificSearch(List<PegacornDirectoryEntry> sortedEntryList, Map<String, String> searchParameters, Integer paginationSize, Integer paginationNumber) throws DirectoryEntryInvalidSortException, DirectoryEntryInvalidSearchException {
        if(sortedEntryList == null){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setSearch(true);
            outcome.setSearchSuccessful(false);
            outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_DID_NOT_COMPLETE);
            outcome.setStatusReason("No entry list provided");
            return(outcome);
        }
        if(sortedEntryList.isEmpty()){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setSearch(true);
            outcome.setSearchSuccessful(false);
            outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_DID_NOT_COMPLETE);
            outcome.setStatusReason("Entry list is empty");
            return(outcome);
        }
        // Don't need to check for an Identifier based search, this is handled elsewhere
        if(searchParameters.containsKey("groupType") || searchParameters.containsKey("GroupType")) {
            String searchValue = searchParameters.get("groupType");
            if(searchValue == null){
                searchValue = searchParameters.get("GroupType");
            }
            Integer counter = 0;
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            for(PegacornDirectoryEntry currentEntry: sortedEntryList){
                GroupDirectoryEntry currentGroupEntry = (GroupDirectoryEntry) currentEntry;
                if(currentGroupEntry.getGroupType().equalsIgnoreCase(searchValue)){
                    outcome.getSearchResult().add(counter, currentEntry);
                    counter += 1;
                }
            }
            outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            outcome.setSearch(true);
            if(outcome.getSearchResult().isEmpty()){
                outcome.setSearchSuccessful(false);
            } else {
                outcome.setSearchSuccessful(true);
            }
            return(outcome);
        }
        if(searchParameters.containsKey("groupManager") || searchParameters.containsKey("GroupManager")) {
            String searchValue = searchParameters.get("groupManager");
            if (searchValue == null) {
                searchValue = searchParameters.get("GroupManager");
            }
            Integer counter = 0;
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            for (PegacornDirectoryEntry currentEntry : sortedEntryList) {
                GroupDirectoryEntry currentGroupEntry = (GroupDirectoryEntry) currentEntry;
                if (currentGroupEntry.getGroupManager().getValue().equalsIgnoreCase(searchValue)) {
                    outcome.getSearchResult().add(counter, currentEntry);
                    counter += 1;
                }
            }
            outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            outcome.setSearch(true);
            if (outcome.getSearchResult().isEmpty()) {
                outcome.setSearchSuccessful(false);
            } else {
                outcome.setSearchSuccessful(true);
            }
            return (outcome);
        }
        throw(new DirectoryEntryInvalidSearchException("Search attribute not supported"));
    }

    @Override
    protected Boolean isSupportiveOfSearchType(String attributeName) {
        if(attributeName == null){
            return(false);
        }
        if(attributeName.isEmpty()){
            return(false);
        }
        if(attributeName.startsWith("Identifier") || attributeName.startsWith("identifier")){
            return(true);
        }
        if(attributeName.equalsIgnoreCase("groupManager")){
            return(true);
        }
        if(attributeName.equalsIgnoreCase("groupType")){
            return(true);
        }
        return false;
    }

    //
    // Sorting Services
    //

    @Override
    public DirectoryMethodOutcome getSortedDirectoryEntrySet(String sortAttribute, Boolean sortOrderAscending)
            throws DirectoryEntryInvalidSortException {
        if(sortAttribute == null){
            DirectoryMethodOutcome failureOutcome = new DirectoryMethodOutcome();
            failureOutcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_DID_NOT_COMPLETE);
            failureOutcome.setSearch(true);
            failureOutcome.setStatusReason("Sort Attribute is null");
            return(failureOutcome);
        }

        if(sortAttribute.startsWith("Identifier") || sortAttribute.startsWith("identifier")){
            if(!sortAttribute.contains("\\|")){
                throw(new DirectoryEntryInvalidSortException("Does not contain a valid Identifier Type parameter = \"Identifier|Type\""));
            }
            String[] identifierQualifier = sortAttribute.split("\\|");
            if(identifierQualifier.length != 2){
                throw(new DirectoryEntryInvalidSortException("Format of the sort request must be = \"Identifier|Type\""));
            }
            String identifierType = identifierQualifier[1];
            DirectoryMethodOutcome output = this.getSortedDirectoryEntrySet(identifierType, sortOrderAscending);
            return(output);
        }

        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        outcome.setSearch(true);
        outcome.setSearchSuccessful(false);
        ArrayList<PegacornDirectoryEntry> sortedList = null;
        Boolean sortAttributeSupported = false;
        if(sortAttribute.equalsIgnoreCase("groupType")) {
            sortAttributeSupported = true;
            sortedList = getSortedByGroupTypeEntrySet();
            if(!sortedList.isEmpty()) {
                outcome.setSearchSuccessful(true);
            }
        }
        if(sortAttribute.equalsIgnoreCase("groupManager")) {
            sortAttributeSupported = true;
            sortedList = getSortedByGroupManagerEntrySet();
            if(!sortedList.isEmpty()) {
                outcome.setSearchSuccessful(true);
            }
        }
        if(!sortAttributeSupported){
            throw(new DirectoryEntryInvalidSortException("Sort strategy not supported"));
        }
        if(outcome.isSearchSuccessful()) {
            if (sortOrderAscending) {
                Integer counter = 0;
                for (PegacornDirectoryEntry currentEntry : sortedList) {
                    outcome.getSearchResult().add(counter, currentEntry);
                    counter += 1;
                }
                outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            } else {
                ArrayList<PegacornDirectoryEntry> reverseList = reverseSortOrder(sortedList);
                Integer counter = 0;
                for (PegacornDirectoryEntry currentEntry : reverseList) {
                    outcome.getSearchResult().add(counter, currentEntry);
                    counter += 1;
                }
            }
        }
        return(outcome);
    }

    protected ArrayList<PegacornDirectoryEntry> getSortedByGroupTypeEntrySet(){
        if(this.getCacheEntries().isEmpty()){
            return(new ArrayList<>());
        }
        Collection<PegacornDirectoryEntry> unsortedEntriesCollection = this.getCacheEntries().values();
        ArrayList<PegacornDirectoryEntry> entryList = new ArrayList<>();
        entryList.addAll(unsortedEntriesCollection);
        Collections.sort(entryList, GroupDirectoryEntry.groupTypeComparator);
        return(entryList);
    }

    protected ArrayList<PegacornDirectoryEntry> getSortedByGroupManagerEntrySet(){
        if(this.getCacheEntries().isEmpty()){
            return(new ArrayList<>());
        }
        Collection<PegacornDirectoryEntry> unsortedEntriesCollection = this.getCacheEntries().values();
        ArrayList<PegacornDirectoryEntry> entryList = new ArrayList<>();
        entryList.addAll(unsortedEntriesCollection);
        Collections.sort(entryList, GroupDirectoryEntry.groupManagerComparator);
        return(entryList);
    }
}

