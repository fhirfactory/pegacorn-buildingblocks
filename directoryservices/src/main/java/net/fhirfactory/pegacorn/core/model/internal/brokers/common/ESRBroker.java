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
package net.fhirfactory.pegacorn.core.model.internal.brokers.common;

import net.fhirfactory.pegacorn.core.model.internal.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.search.exceptions.ESRPaginationException;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.search.exceptions.ESRSortingException;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.core.model.internal.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.core.model.internal.transactions.exceptions.ResourceInvalidSortException;
import net.fhirfactory.pegacorn.core.model.internal.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.common.ExtremelySimplifiedResource;
import org.slf4j.Logger;

import javax.inject.Inject;

public abstract class ESRBroker {

    @Inject
    private IdentifierESDTTypesEnum identifierESDTTypesEnum;

    protected abstract Logger getLogger();
    protected abstract PegacornESRCache specifyCache();
    protected abstract void assignSimplifiedID(ExtremelySimplifiedResource resource);

    protected abstract ExtremelySimplifiedResource synchroniseResource(ExtremelySimplifiedResource resource);

    protected PegacornESRCache getCache(){
        return(specifyCache());
    }

    protected IdentifierESDTTypesEnum getCommonIdentifierTypes(){
        return(identifierESDTTypesEnum);
    }

    //
    // Create
    //

    public ESRMethodOutcome createDirectoryEntry(ExtremelySimplifiedResource newResource){
        ESRMethodOutcome outcome = getCache().addCacheEntry(newResource);
        return(outcome);
    }

    //
    // Review
    //

    abstract protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException;

    public ESRMethodOutcome getResource(String recordID) throws ResourceInvalidSearchException {
        getLogger().debug(".getResource(): Entry, recordID --> {}", recordID);
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        ExtremelySimplifiedResource entry = getCache().getCacheEntry(recordID);
        if(entry == null){
            outcome.setStatus(ESRMethodOutcomeEnum.REVIEW_ENTRY_NOT_FOUND);
            outcome.setId(recordID);
        } else {
            enrichWithDirectoryEntryTypeSpecificInformation(entry);
            outcome.setEntry(entry);
            outcome.setStatus(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
            outcome.setId(entry.getSimplifiedID());
        }
        getLogger().debug(".getResource(): Exit");
        return(outcome);
    }

    public boolean hasEntry(String simplifiedID){
        boolean entryExists = getCache().hasEntry(simplifiedID);
        return(entryExists);
    }


    //
    // Review (Search - by Identifier)
    //
    public ESRMethodOutcome searchForDirectoryEntryUsingIdentifier(IdentifierESDT identifier) throws ResourceInvalidSearchException {
        getLogger().debug(".searchForDirectoryResourceRoleUsingIdentifier(): Entry, identifier --> {}", identifier);
        ESRMethodOutcome outcome = getCache().searchCacheForESRUsingIdentifier(identifier);
        boolean searchCompleted = outcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY) || outcome.getStatus().equals(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
        boolean searchFoundOnlyOneEntry = outcome.getSearchResult().size() == 1;
        ExtremelySimplifiedResource entry = null;
        if(searchCompleted && searchFoundOnlyOneEntry && outcome.isSearchSuccessful()) {
            entry = outcome.getSearchResult().get(0);
            if (entry == null) {
                getLogger().warn(".searchForDirectoryResourceRoleUsingIdentifier(): Incongruous state, search found result, but no entry provided");
                outcome.setSearchSuccessful(false);
                outcome.setSearch(true);
                outcome.setStatus(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                getLogger().debug(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
                return (outcome);
            } else {
                getLogger().trace(".searchForDirectoryResourceRoleUsingIdentifier(): Entry found");
                outcome.setStatus(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                enrichWithDirectoryEntryTypeSpecificInformation(entry);
                outcome.setSearch(true);
                getLogger().debug(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
                return (outcome);
            }
        }
        if(outcome.getSearchResult().isEmpty()) {
            getLogger().trace(".searchForDirectoryResourceRoleUsingIdentifier(): No entry found");
            outcome.setSearchSuccessful(true);
            outcome.getSearchResult().add(entry);
            outcome.setId(entry.getSimplifiedID());
            outcome.setSearch(true);
            getLogger().debug(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
            return (outcome);
        } else {
            getLogger().warn(".searchForDirectoryResourceRoleUsingIdentifier(): multiple Entries found... Error");
            outcome.setSearchSuccessful(false);
            outcome.setSearch(true);
            getLogger().debug(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
            return (outcome);
        }
    }

    public ESRMethodOutcome searchForESRsUsingAttribute(String attributeName,
                                                        String attributeValue,
                                                        Integer pageSize,
                                                        Integer page,
                                                        String sortAttribute,
                                                        Boolean sortAscendingOrder)
            throws ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRPaginationException {

        ESRMethodOutcome outcome = getCache().search(attributeName, attributeValue)
                .sortBy(sortAttribute, sortAscendingOrder)
                .paginate(pageSize, page)
                .toESRMethodOutcome();
        if(outcome.isSearchSuccessful()){
            for(ExtremelySimplifiedResource resource: outcome.getSearchResult()){
                enrichWithDirectoryEntryTypeSpecificInformation(resource);
            }
        }
        return(outcome);

    }

    public ESRMethodOutcome getSortedDirectoryEntrySet(String sortParameter, Boolean sortOrder)
            throws ResourceInvalidSortException, ESRSortingException, ResourceInvalidSearchException {
        ESRMethodOutcome outcome = getCache().allResources().sortBy(sortParameter, sortOrder).toESRMethodOutcome();
        if(outcome.isSearchSuccessful()) {
            for (ExtremelySimplifiedResource currentEntry : outcome.getSearchResult()){
                enrichWithDirectoryEntryTypeSpecificInformation(currentEntry);
            }
        }
        return(outcome);
    }

    public ESRMethodOutcome getPaginatedSortedDirectoryEntrySet(Integer pageSize, Integer page, String sortParameter, Boolean sortOrder)
            throws ResourceInvalidSortException, ESRSortingException, ESRPaginationException, ResourceInvalidSearchException {
        getLogger().debug(".getPaginatedSortedDirectoryEntrySet(): Entry, pageSize->{}, page->{}, sortParameter->{}, sortOrder->{}", pageSize, page, sortParameter, sortOrder);
        ESRMethodOutcome outcome = getCache().allResources().sortBy(sortParameter, sortOrder).paginate(pageSize, page).toESRMethodOutcome();
        if(outcome.isSearchSuccessful()) {
            for (ExtremelySimplifiedResource currentEntry : outcome.getSearchResult()){
                enrichWithDirectoryEntryTypeSpecificInformation(currentEntry);
            }
        }
        getLogger().debug(".getPaginatedSortedDirectoryEntrySet(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    public ESRMethodOutcome getPaginatedUnsortedDirectoryEntrySet(Integer pageSize, Integer page)
            throws ESRSortingException, ESRPaginationException, ResourceInvalidSearchException {
        // Merely a pass-through at this time, just enriching each entry
        ESRMethodOutcome retrievalOutcome = getCache().allResources().sortBy("SimplifiedID").paginate(pageSize,page).toESRMethodOutcome();
        if(retrievalOutcome.isSearchSuccessful()){
            for(ExtremelySimplifiedResource currentEntry: retrievalOutcome.getSearchResult()){
                enrichWithDirectoryEntryTypeSpecificInformation(currentEntry);
            }
        }
        return(retrievalOutcome);
    }

    //
    // Update
    //

    protected ESRMethodOutcome updateDirectoryEntry(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
        getLogger().debug(".PegacornDirectoryEntry(): Entry");
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        ExtremelySimplifiedResource foundResource = null;
        getLogger().trace(".PegacornDirectoryEntry(): Attempting to retrieve existing Resource");
        if(entry.getSimplifiedID() != null){
            getLogger().trace(".PegacornDirectoryEntry(): The PegId is not-Null, so we should be able to retrieve Resource with it");
            if(getLogger().isInfoEnabled()){
                getLogger().trace(".PegacornDirectoryEntry(): Attempting to retrieve PegacornDirectoryEntry for Id --> {}", entry.getSimplifiedID());
            }
            foundResource = getCache().getCacheEntry(entry.getSimplifiedID());
        }
        getLogger().trace(".updatePractitionerEntry(): Check to see if we were able to retrieve existing Resource");
        if(foundResource != null){
            outcome.setId(entry.getSimplifiedID());
            outcome.setEntry(entry);
            outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            return(outcome);
        } else {
            getLogger().trace(".updatePractitionerEntry(): No Resource retrieved, trying individual Identifiers");
            boolean entryWithIdentifier = false;
            for(IdentifierESDT identifier: entry.getIdentifiers()) {
                if (!getCache().getIdentifier2ESRMap().containsKey(identifier)){
                    entryWithIdentifier = true;
                    break;
                }
            }
            getLogger().trace(".updatePractitionerEntry(): Completed per-Identifier retrieval process, checking result");
            if(!entryWithIdentifier) {
                getLogger().trace(".updatePractitionerEntry(): Nope, no existing resource... that's odd... generating one");
                assignSimplifiedID(entry);
                getCache().addCacheEntry(entry);
                outcome.setId(entry.getSimplifiedID());
                outcome.setEntry(entry);
                outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE);
                getLogger().debug(".updatePractitionerEntry(): Exit, we've just written this resource since there wasn't one there.");
                return (outcome);
            }
        }
        outcome.setId(entry.getSimplifiedID());
        outcome.setEntry(entry);
        outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
        getLogger().debug(".updatePractitionerEntry(): Exit, problem retrieving/updating original entry");
        return(outcome);
    }
}
