package net.fhirfactory.pegacorn.internals.directories.cache.common;

import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSearchException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSortException;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PegacornDirectoryEntryCache {
    private ConcurrentHashMap<IdentifierDE, PegacornDirectoryEntry> cacheEntriesByIdentifier;
    private ConcurrentHashMap<String, PegacornDirectoryEntry> cacheEntries;

    abstract protected Logger getLogger();

    abstract public DirectoryMethodOutcome directoryEntrySpecificSearch(List<PegacornDirectoryEntry> sortedEntryList, Map<String, String> searchParameters, Integer paginationSize, Integer paginationNumber)
            throws DirectoryEntryInvalidSortException, DirectoryEntryInvalidSearchException;

    abstract public DirectoryMethodOutcome getSortedDirectoryEntrySet(String sortParameter, Boolean sortAscendingOrder)
            throws DirectoryEntryInvalidSortException;

    abstract protected Boolean isSupportiveOfSearchType(String attributeName);

    public PegacornDirectoryEntryCache(){
        cacheEntriesByIdentifier = new ConcurrentHashMap<>();
        cacheEntries = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<IdentifierDE, PegacornDirectoryEntry> getCacheEntriesByIdentifier() {
        return cacheEntriesByIdentifier;
    }

    protected void setCacheEntriesByIdentifier(ConcurrentHashMap<IdentifierDE, PegacornDirectoryEntry> cacheEntriesByIdentifier) {
        this.cacheEntriesByIdentifier = cacheEntriesByIdentifier;
    }

    public ConcurrentHashMap<String, PegacornDirectoryEntry> getCacheEntries() {
        return cacheEntries;
    }

    public void setCacheEntries(ConcurrentHashMap<String, PegacornDirectoryEntry> cacheEntries) {
        this.cacheEntries = cacheEntries;
    }

    public DirectoryMethodOutcome addCacheEntry(PegacornDirectoryEntry entry){
        getLogger().debug(".addCacheEntry(): Entry");
        if(entry == null){
            getLogger().debug(".addCacheEntry(): Exit, entry to be added is null");
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.CREATE_ENTRY_INVALID);
            outcome.setCreated(false);
            outcome.setStatusReason("The entry is NULL");
            return(outcome);
        }
        if(entry.getIdentifiers().isEmpty()){
            getLogger().error(".addCacheEntry(): Exit, entry to be added has no Identifiers!!!");
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.CREATE_ENTRY_INVALID);
            outcome.setCreated(false);
            outcome.setStatusReason("No Identifiers in Entry, cannot persist");
            return(outcome);
        }
        if(entry.getId() == null){
            getLogger().info(".addCacheEntry(): creating an Id");
            entry.generateId();
            getLogger().info(".addCacheEntry(): New Id --> {}", entry.getId().getValue());
        } else {
            getLogger().info(".addCacheEntry(): Resource has an Id already... attempting to retrieve associated Resource");
            PegacornDirectoryEntry foundEntry = getCacheEntry(entry.getId().getValue());
            if(foundEntry != null){
                getLogger().info(".addCacheEntry(): Resource already exists, so cant create it again.... ");
                DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
                outcome.setStatus(DirectoryMethodOutcomeEnum.CREATE_ENTRY_DUPLICATE);
                outcome.setId(foundEntry.getId());
                outcome.setCreated(false);
                outcome.setEntry(foundEntry);
                getLogger().info(".addCacheEntry(): Exit, resource already exists");
                return(outcome);
            }
        }
        getLogger().info(".addCacheEntry(): Adding to Identifier based Cache");
        for(IdentifierDE identifier: entry.getIdentifiers() ){
            this.cacheEntriesByIdentifier.put(identifier, entry);
        }
        getLogger().info(".addCacheEntry(): Adding to Id based Cache");
        this.cacheEntries.put(entry.getId().getValue(), entry);
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        outcome.setStatus(DirectoryMethodOutcomeEnum.CREATE_ENTRY_SUCCESSFUL);
        outcome.setId(entry.getId());
        outcome.setCreated(false);
        outcome.setEntry(entry);
        getLogger().debug(".addCacheEntry(): Exit, entry added");
        return(outcome);
    }

    protected void removeCacheEntry(IdentifierDE identifier){
        getLogger().debug(".removeCacheEntry(): Entry (using IdentifierDE)");
        boolean containsKey = this.cacheEntriesByIdentifier.containsKey(identifier);
        if(!containsKey){
            getLogger().debug(".removeCacheEntry(): Exit, Identifier not in the cache");
            return;
        }
        this.cacheEntriesByIdentifier.remove(identifier);
        getLogger().debug(".removeCacheEntry(): Exit, entry removed");
    }

    protected void removeCacheEntry(String id){
        getLogger().debug(".removeCacheEntry(): Entry (using Id)");
        if(cacheEntries.isEmpty()){
            return;
        }
        boolean containsKey = this.cacheEntries.containsKey(id);
        if(containsKey) {
            PegacornDirectoryEntry foundEntry = this.cacheEntries.get(id);
            for(IdentifierDE entryIdentifier: foundEntry.getIdentifiers()){
                removeCacheEntry(entryIdentifier);
            }
            this.cacheEntries.remove(id);
        }
        getLogger().debug(".removeCacheEntry(): Exit, entry removed");
    }

    protected PegacornDirectoryEntry getCacheEntry(IdentifierDE identifier){
        getLogger().info(".getCacheEntry(): Entry (using IdentifierDE), identifier --> {}", identifier);
        if(identifier == null){
            getLogger().info(".getCacheEntry(): Exit, Identifier is null, so exiting");
            return(null);
        }
        PegacornDirectoryEntry foundEntry = this.cacheEntriesByIdentifier.get(identifier);
        if(foundEntry == null) {
            getLogger().info(".getCacheEntry(): Exit, couldn't find element, returning NULL");
            return(null);
        } else {
            getLogger().info(".getCacheEntry(): Exit, value retrieved, returning it!");
            return(foundEntry);
        }
    }

    public PegacornDirectoryEntry getCacheEntry(String idValue){
        getLogger().info(".getCacheEntry(): Entry (using Id), idValue --> {}", idValue);
        if(idValue == null){
            getLogger().info(".getCacheEntry(): Exit, id is NULL, so exiting");
            return(null);
        }
        if(cacheEntries.isEmpty()){
            getLogger().info(".getCacheEntry(): Exit, Cache is empty, so exiting");
            return(null);
        }
        PegacornDirectoryEntry entry = this.cacheEntries.get(idValue);
        if(entry != null){
            getLogger().info(".getCacheEntry(): Exit, entry found");
            return(entry);
        } else {
            getLogger().info(".getCacheEntry(): Exit, entry not found");
            return (null);
        }
    }

    /**
     * This is a VERY expensive function... :(
     *
     * @param identifierType
     * @param orderAscending
     * @return
     */
    public List<PegacornDirectoryEntry> getIdentifierSortedList(String identifierType, Boolean orderAscending){
        getLogger().info(".getCacheEntry(): Entry (using Id), orderAscending --> {}", orderAscending);

        // 1st, Let's get all the Identifiers that have the appropriate type
        ArrayList<String> sortableValues = new ArrayList<>();
        HashMap<String, IdentifierDE> sortableValuesMap = new HashMap<>();
        Enumeration<IdentifierDE> identifiers = this.getCacheEntriesByIdentifier().keys();
        while(identifiers.hasMoreElements()){
            IdentifierDE currentIdentifier = identifiers.nextElement();
            if(currentIdentifier.getType().equalsIgnoreCase(identifierType)) {
                sortableValues.add(currentIdentifier.getValue());
                sortableValuesMap.put(currentIdentifier.getValue(), currentIdentifier);
            }
        }
        if(sortableValues.isEmpty()){
            return(new ArrayList<>());
        }

        // Now, lets sort that list of values
        Collections.sort(sortableValues);

        // Now, lets create the newly sorted list of IdentifierDE List
        ArrayList<PegacornDirectoryEntry> sortedList = new ArrayList<>();
        Integer counter = 0;
        for(String currentValue: sortableValues){
            IdentifierDE currentIdentifier = sortableValuesMap.get(currentValue);
            PegacornDirectoryEntry currentEntry = this.cacheEntriesByIdentifier.get(currentIdentifier);
            sortedList.add(counter, currentEntry);
            counter += 1;
        }

        // Now, let's add the DirectoryEntries that can't be sorted using this type --> this is expensive
        Collection<PegacornDirectoryEntry> entrySet = this.cacheEntries.values();
        for(PegacornDirectoryEntry currentEntry: entrySet){
            if(!sortedList.contains(currentEntry)){
                sortedList.add(counter, currentEntry);
                counter += 1;
            }
        }

        // Now, let's adjust the order of the sort
        if(!orderAscending){
            ArrayList<PegacornDirectoryEntry> orderDescendingList = new ArrayList<>();
            Integer listSize = sortedList.size();
            for(Integer counter2 = 0; counter2 < listSize; counter2 += 1){
                orderDescendingList.set(counter2, sortedList.get(listSize-counter2-1));
            }
            return(orderDescendingList);
        } else {
            return (sortedList);
        }
    }

    //
    // Search Services
    //

    public DirectoryMethodOutcome searchCacheForEntryUsingIdentifierDE(IdentifierDE identifier){
        getLogger().info("searchCacheForEntryUsingIdentifierDE(): Entry, identifier --> {}", identifier);
        // Fast lookup first
        PegacornDirectoryEntry foundEntry = cacheEntriesByIdentifier.get(identifier);
        if(foundEntry != null){
            getLogger().info("searchCacheForEntryUsingIdentifierDE(): Exit, Entry found via HasMap Index");
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            outcome.getSearchResult().add(foundEntry);
            outcome.setSearchSuccessful(true);
            return(outcome);
        }
        // Now slow lookup
        ArrayList<PegacornDirectoryEntry> slowScanResultSet = new ArrayList<>();
        for(PegacornDirectoryEntry currentEntry: cacheEntriesByIdentifier.values()){
            if(hasIdentifier(currentEntry, identifier)){
                getLogger().info("searchCacheForEntryUsingIdentifierDE(): Exit, Entry found via parsing Identifier sets");
                slowScanResultSet.add(currentEntry);
            }
        }
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
        outcome.getSearchResult().addAll(slowScanResultSet);
        if(slowScanResultSet.isEmpty()) {
            outcome.setSearchSuccessful(false);
            getLogger().info("searchCacheForEntryUsingIdentifierDE(): No matching entries found");
        } else {
            outcome.setSearchSuccessful(true);
            getLogger().info("searchCacheForEntryUsingIdentifierDE(): Matching entries found");
        }
        getLogger().info("searchCacheForEntryUsingIdentifierDE(): Exit");
        return(outcome);
    }

    protected boolean hasIdentifier(PegacornDirectoryEntry testEntry, IdentifierDE testIdentifier){
        getLogger().trace(".hasIdentifier(): Entry");
        if(testEntry == null || testIdentifier == null){
            getLogger().trace(".hasIdentifier(): Exit, Test Entry or Test Identifier is null, return false");
            return(false);
        }
        if(testEntry.getIdentifiers().isEmpty()){
            getLogger().trace(".hasIdentifier(): Exit, Test Entry has no Identifiers, return false");
            return(false);
        }
        boolean listContains = testEntry.getIdentifiers().contains(testIdentifier);
        if(listContains){
            getLogger().trace(".hasIdentifier(): Exit, Test Identifier is found within Test Entry, return true");
            return(true);
        } else {
            getLogger().trace(".hasIdentifier(): Exit, Test Identifier no found within Test Entry, return false");
            return(false);
        }
    }

    protected ArrayList<PegacornDirectoryEntry> reverseSortOrder(ArrayList<PegacornDirectoryEntry> originalOrderedList){
        if(originalOrderedList == null){
            return(new ArrayList<>());
        }

        if(originalOrderedList.isEmpty()){
            return(new ArrayList<>());
        }
        ArrayList<PegacornDirectoryEntry> resortedList = new ArrayList<>();
        Integer size = originalOrderedList.size();
        for(Integer counter = 0; counter < size; counter += 1){
            Integer reverseLocation = (size - 1) - counter;
            resortedList.add(counter, originalOrderedList.get(reverseLocation));
        }
        return(resortedList);
    }

    public DirectoryMethodOutcome doAttributeBasedSearch(Map<String, String> searchParameters,
                                                         Integer paginationSize,
                                                         Integer paginationNumber,
                                                         String sortAttribute,
                                                         Boolean sortAscendingOrder)
            throws DirectoryEntryInvalidSortException, DirectoryEntryInvalidSearchException {

        DirectoryMethodOutcome sortOutcome = null;
        if(sortAttribute == null){
            sortOutcome = new DirectoryMethodOutcome();
            sortOutcome.getSearchResult().addAll(getCacheEntries().values());
            sortOutcome.setSearch(true);
        } else {
            sortOutcome = this.getSortedDirectoryEntrySet(sortAttribute, sortAscendingOrder);
            sortOutcome.setSearch(true);
        }
        if(sortOutcome.getSearchResult().isEmpty()){
            sortOutcome.setSearchSuccessful(false);
            sortOutcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            return(sortOutcome);
        }
        DirectoryMethodOutcome outcome = this.directoryEntrySpecificSearch(sortOutcome.getSearchResult(),searchParameters,paginationSize, paginationNumber );
        return(outcome);
    }

    public DirectoryMethodOutcome getPegIdSortedList(Integer pageSize, Integer page){
        Collection<PegacornDirectoryEntry> allEntries = this.getCacheEntries().values();
        if(allEntries.isEmpty()){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setSearch(true);
            outcome.setSearchSuccessful(false);
            outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            outcome.setStatusReason("No entries in cache");
            return(outcome);
        }
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        ArrayList<PegacornDirectoryEntry> allEntriesList = new ArrayList<>();
        for(PegacornDirectoryEntry current: allEntries){
            allEntriesList.add(current);
        }
        Collections.sort(allEntriesList, PegacornDirectoryEntry.pegIdComparator);
        if(pageSize > 0) {
            Integer locationOffsetStart = pageSize * page;
            Integer numberOfEntries = allEntries.size();
            if (numberOfEntries > locationOffsetStart) {
                for (Integer counter = 0; counter < pageSize; counter += 1) {
                    Integer listLocation = locationOffsetStart + counter;
                    if (listLocation < numberOfEntries) {
                        PegacornDirectoryEntry currentEntry = allEntriesList.get(listLocation);
                        outcome.getSearchResult().add(counter, currentEntry);
                    } else {
                        break;
                    }
                }
                outcome.setSearchSuccessful(true);
                outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                return (outcome);
            } else {
                outcome.setSearchSuccessful(false);
                outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                outcome.setStatusReason("No entries in page range");
                return (outcome);
            }
        } else {
            outcome.getSearchResult().addAll(allEntries);
            outcome.setSearchSuccessful(true);
            outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            return (outcome);
        }
    }
}
