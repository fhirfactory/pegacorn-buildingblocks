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
package net.fhirfactory.pegacorn.internals.directories.brokers.common;

import net.fhirfactory.pegacorn.internals.directories.cache.common.PegacornDirectoryEntryCache;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDEUseEnum;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.PegId;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import org.slf4j.Logger;

import java.util.ArrayList;

public abstract class ResourceDirectoryBroker {

    protected abstract Logger getLogger();
    protected abstract PegacornDirectoryEntryCache specifyCache();

    protected PegacornDirectoryEntryCache getCache(){
        return(specifyCache());
    }

    //
    // Review
    //

    abstract protected void enrichWithDirectoryEntryTypeSpecificInformation(PegacornDirectoryEntry entry);

    public DirectoryMethodOutcome reviewDirectoryEntry(PegId id){
        getLogger().debug(".reviewDirectoryEntry(): Entry, id --> {}", id);
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        PegacornDirectoryEntry entry = getCache().getCacheEntry(id.getValue());
        if(entry == null){
            outcome.setStatus(DirectoryMethodOutcomeEnum.REVIEW_ENTRY_NOT_FOUND);
            outcome.setId(id);
        } else {
            outcome.setEntry(entry);
            outcome.setStatus(DirectoryMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
            outcome.setId(entry.getId());
        }
        getLogger().debug(".reviewDirectoryEntry(): Exit");
        return(outcome);
    }

    public DirectoryMethodOutcome reviewDirectoryEntry(){
        getLogger().debug(".reviewDirectoryEntry(): Entry (for unqualified get)");
        ArrayList<PegacornDirectoryEntry> resultList = new ArrayList<>();
        resultList.addAll(getCache().getCacheEntries().values());
        for(PegacornDirectoryEntry currentEntry: resultList){
            enrichWithDirectoryEntryTypeSpecificInformation(currentEntry);
        }
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        outcome.getSearchResult().addAll(resultList);
        outcome.setSearchSuccessful(true);
        getLogger().debug(".reviewDirectoryEntry(): Exit");
        return(outcome);
    }

    //
    // Update
    //

    protected DirectoryMethodOutcome updateDirectoryEntry(PegacornDirectoryEntry entry){
        getLogger().info(".PegacornDirectoryEntry(): Entry");
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        PegacornDirectoryEntry foundEntry = null;
        getLogger().info(".PegacornDirectoryEntry(): Attempting to retrieve existing Resource");
        if(entry.getId() != null){
            getLogger().info(".PegacornDirectoryEntry(): The PegId is not-Null, so we should be able to retrieve Resource with it");
            if(getLogger().isInfoEnabled()){
                getLogger().info(".PegacornDirectoryEntry(): Attempting to retrieve PegacornDirectoryEntry for Id --> {}", entry.getId());
            }
            foundEntry = getCache().getCacheEntry(entry.getId().getValue());
        } else {
            getLogger().info(".PegacornDirectoryEntry(): The PegId is Null, so seeing if a suitable Identifier is available");
            IdentifierDE entryIdentifier = entry.getIdentifierWithType("EmailAddress");
            if(entryIdentifier != null){
                getLogger().info(".PegacornDirectoryEntry(): Have a suitable Identifier, now retrieving");
                if(entryIdentifier.getUse().equals(IdentifierDEUseEnum.OFFICIAL)){
                    DirectoryMethodOutcome retrievalOutcome = getCache().searchCacheForEntryUsingIdentifierDE(entryIdentifier);
                    boolean searchCompleted = retrievalOutcome.getStatus().equals(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                    boolean searchFoundSomething = retrievalOutcome.getSearchResult().size() == 1;
                    if(searchCompleted && searchFoundSomething) {
                        foundEntry = retrievalOutcome.getSearchResult().get(0);
                    }
                }
            }
        }
        getLogger().info(".updatePractitionerEntry(): Check to see if we were able to retrieve existing Resource");
        if(foundEntry != null){
            outcome.setId(entry.getId());
            outcome.setEntry(entry);
            outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            return(outcome);
        } else {
            getLogger().info(".updatePractitionerEntry(): No Resource retrieved, trying individual Identifiers");
            boolean entryWithIdentifier = false;
            for(IdentifierDE identifier: entry.getIdentifiers()) {
                if (!getCache().getCacheEntriesByIdentifier().containsKey(identifier)){
                    entryWithIdentifier = true;
                    break;
                }
            }
            getLogger().info(".updatePractitionerEntry(): Completed per-Identifier retrieval process, checking result");
            if(!entryWithIdentifier) {
                getLogger().info(".updatePractitionerEntry(): Nope, no existing resource... that's odd... generating one");
                entry.generateId();
                getCache().addCacheEntry(entry);
                outcome.setId(entry.getId());
                outcome.setEntry(entry);
                outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE);
                getLogger().info(".updatePractitionerEntry(): Exit, we've just written this resource since there wasn't one there.");
                return (outcome);
            }
        }
        outcome.setId(entry.getId());
        outcome.setEntry(entry);
        outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
        getLogger().info(".updatePractitionerEntry(): Exit, problem retrieving/updating original entry");
        return(outcome);
    }

    //
    // Search (by Identifier)
    //
    public DirectoryMethodOutcome searchForDirectoryResourceRoleUsingIdentifier(IdentifierDE identifier){
        getLogger().info(".searchForDirectoryResourceRoleUsingIdentifier(): Entry, identifier --> {}", identifier);
        DirectoryMethodOutcome outcome = getCache().searchCacheForEntryUsingIdentifierDE(identifier);
        boolean searchCompleted = outcome.getStatus().equals(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
        boolean searchFoundOnlyOneEntry = outcome.getSearchResult().size() == 1;
        PegacornDirectoryEntry entry = null;
        if(searchCompleted && searchFoundOnlyOneEntry && outcome.isSearchSuccessful()) {
            entry = outcome.getSearchResult().get(0);
            if (entry == null) {
                getLogger().error(".searchForDirectoryResourceRoleUsingIdentifier(): Incongruous state, search found result, but no entry provided");
                outcome.setSearchSuccessful(false);
                outcome.setSearch(true);
                outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                getLogger().info(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
                return (outcome);
            } else {
                getLogger().info(".searchForDirectoryResourceRoleUsingIdentifier(): Entry found");
                outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                enrichWithDirectoryEntryTypeSpecificInformation(entry);
                outcome.setSearch(true);
                getLogger().info(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
                return (outcome);
            }
        }
        if(outcome.getSearchResult().isEmpty()) {
            getLogger().info(".searchForDirectoryResourceRoleUsingIdentifier(): No entry found");
            outcome.setSearchSuccessful(true);
            outcome.getSearchResult().add(entry);
            outcome.setId(entry.getId());
            outcome.setSearch(true);
            getLogger().info(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
            return (outcome);
        } else {
            getLogger().error(".searchForDirectoryResourceRoleUsingIdentifier(): multiple Entries found... Error");
            outcome.setSearchSuccessful(false);
            outcome.setSearch(true);
            getLogger().info(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
            return (outcome);
        }
    }
}
