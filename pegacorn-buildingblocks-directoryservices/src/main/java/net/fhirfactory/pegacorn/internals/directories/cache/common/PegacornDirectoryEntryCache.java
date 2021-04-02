package net.fhirfactory.pegacorn.internals.directories.cache.common;

import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public abstract class PegacornDirectoryEntryCache {
    private ConcurrentHashMap<IdentifierDE, PegacornDirectoryEntry> cacheEntriesByIdentifier;
    private ConcurrentHashMap<String, PegacornDirectoryEntry> cacheEntries;

    abstract protected Logger getLogger();

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
        for(PegacornDirectoryEntry currentEntry: cacheEntriesByIdentifier.values()){
            if(hasIdentifier(currentEntry, identifier)){
                getLogger().info("searchCacheForEntryUsingIdentifierDE(): Exit, Entry found via parsing Identifier sets");
                DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
                outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                outcome.getSearchResult().add(currentEntry);
                outcome.setSearchSuccessful(true);
                return(outcome);
            }
        }
        getLogger().info("searchCacheForEntryUsingIdentifierDE(): Exit, Entry not found");
        return(null);
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

    public PegacornDirectoryEntry getCacheEntry(String id){
        getLogger().info(".getCacheEntry(): Entry (using Id), id --> {}", id);
        if(cacheEntries.isEmpty()){
            getLogger().info(".getCacheEntry(): Exit, id is NULL, so exiting");
            return(null);
        }
        if(cacheEntries.isEmpty()){
            getLogger().info(".getCacheEntry(): Exit, Cache is empty, so exiting");
            return(null);
        }
        PegacornDirectoryEntry entry = this.cacheEntries.get(id);
        if(entry != null){
            getLogger().info(".getCacheEntry(): Exit, entry found");
            return(entry);
        } else {
            getLogger().info(".getCacheEntry(): Exit, entry not found");
            return (null);
        }
    }
}
