package net.fhirfactory.pegacorn.core.model.internal.cache.common;

import net.fhirfactory.pegacorn.core.model.internal.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.search.common.ESRSearchResult;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.core.model.internal.transactions.exceptions.ResourceInvalidSearchException;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PegacornESRCache {
    private ConcurrentHashMap<IdentifierESDT, ExtremelySimplifiedResource> identifier2ESRMap;
    private ConcurrentHashMap<String, ExtremelySimplifiedResource> simplifiedID2ESRMap;
    private ConcurrentHashMap<String, ExtremelySimplifiedResource> displayName2ESRMap;

    abstract protected Logger getLogger();

    abstract public ESRSearchResult search(String searchAttributeName, String searchAttributeValue)
            throws ResourceInvalidSearchException;
    abstract protected ESRSearchResult instatiateNewESRSearchResult();

    abstract public Boolean supportsSearchType(String attributeName);

    public PegacornESRCache(){
        identifier2ESRMap = new ConcurrentHashMap<>();
        simplifiedID2ESRMap = new ConcurrentHashMap<>();
        displayName2ESRMap = new ConcurrentHashMap<>();
    }

    public boolean hasEntry(String simplifiedID){
        if(this.simplifiedID2ESRMap.containsKey(simplifiedID)){
            return(true);
        } else {
            return(false);
        }
    }

    public ConcurrentHashMap<IdentifierESDT, ExtremelySimplifiedResource> getIdentifier2ESRMap() {
        return identifier2ESRMap;
    }

    protected void setIdentifier2ESRMap(ConcurrentHashMap<IdentifierESDT, ExtremelySimplifiedResource> identifier2ESRMap) {
        this.identifier2ESRMap = identifier2ESRMap;
    }

    public ConcurrentHashMap<String, ExtremelySimplifiedResource> getSimplifiedID2ESRMap() {
        return simplifiedID2ESRMap;
    }

    public void setSimplifiedID2ESRMap(ConcurrentHashMap<String, ExtremelySimplifiedResource> simplifiedID2ESRMap) {
        this.simplifiedID2ESRMap = simplifiedID2ESRMap;
    }

    public ConcurrentHashMap<String, ExtremelySimplifiedResource> getDisplayName2ESRMap() {
        return displayName2ESRMap;
    }

    public void setDisplayName2ESRMap(ConcurrentHashMap<String, ExtremelySimplifiedResource> displayName2ESRMap) {
        this.displayName2ESRMap = displayName2ESRMap;
    }

    public ESRMethodOutcome addCacheEntry(ExtremelySimplifiedResource entry){
        getLogger().debug(".addCacheEntry(): Entry");
        if(entry == null){
            getLogger().debug(".addCacheEntry(): Exit, entry to be added is null");
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.CREATE_ENTRY_INVALID);
            outcome.setCreated(false);
            outcome.setStatusReason("The entry is NULL");
            return(outcome);
        }
        if(entry.getIdentifiers().isEmpty()){
            getLogger().error(".addCacheEntry(): Exit, entry to be added has no Identifiers!!!");
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.CREATE_ENTRY_INVALID);
            outcome.setCreated(false);
            outcome.setStatusReason("No Identifiers in Entry, cannot persist");
            return(outcome);
        }
        if(entry.getOtherID() == null){
            String newUUID = UUID.randomUUID().toString();
            entry.setOtherID(newUUID);
        }
        if(entry.getSimplifiedID() == null){
            getLogger().trace(".addCacheEntry(): creating an primaryKey");
            entry.assignSimplifiedID(entry.getOtherID(), "OtherID");
            getLogger().trace(".addCacheEntry(): New Id --> {}", entry.getSimplifiedID());
        } else {
            getLogger().trace(".addCacheEntry(): Resource has an Id already... attempting to retrieve associated Resource");
            ExtremelySimplifiedResource foundEntry = getCacheEntry(entry.getSimplifiedID());
            if(foundEntry != null){
                getLogger().trace(".addCacheEntry(): Resource already exists, so cant create it again.... ");
                ESRMethodOutcome outcome = new ESRMethodOutcome();
                outcome.setStatus(ESRMethodOutcomeEnum.CREATE_ENTRY_DUPLICATE);
                outcome.setId(foundEntry.getSimplifiedID());
                outcome.setCreated(false);
                outcome.setEntry(foundEntry);
                getLogger().debug(".addCacheEntry(): Exit, resource already exists");
                return(outcome);
            }
        }
        getLogger().trace(".addCacheEntry(): Adding to Identifier based Cache");
        for(IdentifierESDT identifier: entry.getIdentifiers() ){
            this.identifier2ESRMap.put(identifier, entry);
        }
        getLogger().trace(".addCacheEntry(): Adding to displayName based Cache");
        if(entry.getDisplayName() == null){
            entry.setDisplayName(entry.getSimplifiedID());
        }
        this.displayName2ESRMap.putIfAbsent(entry.getDisplayName().toLowerCase(), entry);
        getLogger().trace(".addCacheEntry(): Adding to simplifiedID based Cache");
        this.simplifiedID2ESRMap.putIfAbsent(entry.getSimplifiedID().toLowerCase(), entry);
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        outcome.setStatus(ESRMethodOutcomeEnum.CREATE_ENTRY_SUCCESSFUL);
        outcome.setId(entry.getSimplifiedID());
        outcome.setCreated(true);
        outcome.setEntry(entry);
        getLogger().debug(".addCacheEntry(): Exit, entry added");
        return(outcome);
    }

    protected void removeCacheEntry(IdentifierESDT identifier){
        getLogger().debug(".removeCacheEntry(): Entry (using IdentifierDE)");
        boolean containsKey = this.identifier2ESRMap.containsKey(identifier);
        if(!containsKey){
            getLogger().debug(".removeCacheEntry(): Exit, Identifier not in the cache");
            return;
        }
        this.identifier2ESRMap.remove(identifier);
        getLogger().debug(".removeCacheEntry(): Exit, entry removed");
    }

    protected void removeCacheEntry(String id){
        getLogger().debug(".removeCacheEntry(): Entry (using Id)");
        if(simplifiedID2ESRMap.isEmpty()){
            return;
        }
        boolean containsKey = this.simplifiedID2ESRMap.containsKey(id.toLowerCase());
        if(containsKey) {
            ExtremelySimplifiedResource foundEntry = this.simplifiedID2ESRMap.get(id.toLowerCase());
            for(IdentifierESDT entryIdentifier: foundEntry.getIdentifiers()){
                removeCacheEntry(entryIdentifier);
            }
            this.displayName2ESRMap.remove(foundEntry.getDisplayName().toLowerCase());
            this.simplifiedID2ESRMap.remove(id.toLowerCase());
        }
        getLogger().debug(".removeCacheEntry(): Exit, entry removed");
    }

    protected ExtremelySimplifiedResource getCacheEntry(IdentifierESDT identifier){
        getLogger().debug(".getCacheEntry(): Entry (using IdentifierDE), identifier --> {}", identifier);
        if(identifier == null){
            getLogger().debug(".getCacheEntry(): Exit, Identifier is null, so exiting");
            return(null);
        }
        ExtremelySimplifiedResource foundEntry = this.identifier2ESRMap.get(identifier);
        if(foundEntry == null) {
            getLogger().debug(".getCacheEntry(): Exit, couldn't find element, returning NULL");
            return(null);
        } else {
            getLogger().debug(".getCacheEntry(): Exit, value retrieved, returning it!");
            return(foundEntry);
        }
    }

    public ExtremelySimplifiedResource getCacheEntry(String idValue){
        getLogger().debug(".getCacheEntry(): Entry (using Id), idValue --> {}", idValue);
        if(idValue == null){
            getLogger().debug(".getCacheEntry(): Exit, id is NULL, so exiting");
            return(null);
        }
        if(simplifiedID2ESRMap.isEmpty()){
            getLogger().debug(".getCacheEntry(): Exit, Cache is empty, so exiting");
            return(null);
        }
        ExtremelySimplifiedResource entry = this.simplifiedID2ESRMap.get(idValue);
        if(entry != null){
            getLogger().debug(".getCacheEntry(): Exit, entry found");
            return(entry);
        } else {
            getLogger().debug(".getCacheEntry(): Exit, entry not found");
            return (null);
        }
    }

    //
    // Helper Functions
    //

    protected boolean hasIdentifier(ExtremelySimplifiedResource testEntry, IdentifierESDT testIdentifier){
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

    //
    // Search Services
    //

    public ESRSearchResult searchCacheUsingSimplifiedID(String simplifiedIDValue)
        throws ResourceInvalidSearchException {
        getLogger().debug(".search(): Entry, parameterName->{}", simplifiedIDValue);
        if(simplifiedIDValue == null){
            throw(new ResourceInvalidSearchException("Search parameter name or value are null"));
        }
        if(simplifiedIDValue.isEmpty()){
            throw(new ResourceInvalidSearchException("Search parameter name is empty"));
        }
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(this.simplifiedID2ESRMap.isEmpty()){
            return(result);
        }
        String simplifiedIDValueAsLowerCase = simplifiedIDValue.toLowerCase();
        Enumeration<String> idSet = this.simplifiedID2ESRMap.keys();
        while(idSet.hasMoreElements()){
            String currentID = idSet.nextElement();
            if(currentID.toLowerCase().startsWith(simplifiedIDValueAsLowerCase)){
                ExtremelySimplifiedResource resource = this.simplifiedID2ESRMap.get(currentID);
                result.getSearchResultList().add(resource);
            }
        }
        return(result);
    }

    public ESRMethodOutcome searchCacheForESRUsingIdentifier(IdentifierESDT identifier) throws ResourceInvalidSearchException {
        if(identifier == null){
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.REVIEW_ENTRY_NOT_FOUND);
            return(outcome);
        }
        getLogger().trace("searchCacheForESRUsingIdentifierParameters(): Entry, value->{}, type->{}, use->{}", identifier);
        ESRSearchResult result = searchCacheForESRUsingIdentifierParameters(identifier.getValue(), identifier.getType(), identifier.getUse());
        ESRMethodOutcome outcome = result.toESRMethodOutcome();
        if(result.getSearchResultList().size() == 1){
            outcome.setStatus(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
            outcome.setSearchSuccessful(true);
            outcome.setEntry(result.getSearchResultList().get(0));
            outcome.setId(outcome.getEntry().getSimplifiedID());
            outcome.setSearch(false);
            return(outcome);
        } else {
            outcome.setStatus(ESRMethodOutcomeEnum.REVIEW_ENTRY_NOT_FOUND);
            return (outcome);
        }
    }

    protected ESRSearchResult searchCacheForESRUsingIdentifierParameters(String value,
                                                                         String type,
                                                                         IdentifierESDTUseEnum use)
            throws ResourceInvalidSearchException {
        getLogger().debug("searchCacheForESRUsingIdentifierParameters(): Entry, value->{}, type->{}, use->{}", value, type, use);
        // Search First
        getLogger().trace(".searchCacheForESRUsingIdentifierParameters(): First, let's do the search!");
        boolean valueIsNull = (value == null);
        boolean typeIsNull = (type == null);
        boolean useIsNull = (use == null);
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(useIsNull && typeIsNull && valueIsNull) {
            getLogger().debug(".searchCacheForESRUsingIdentifierParameters(): Exit, return empty result --> {}", result);
            return(result);
        } else {
            Enumeration<IdentifierESDT> identifierSet = this.identifier2ESRMap.keys();
            while(identifierSet.hasMoreElements()){
                IdentifierESDT currentIdentifier = identifierSet.nextElement();
                boolean valueMatches = false;
                if(!valueIsNull){
                    if(currentIdentifier.getValue().contains(value)){
                        valueMatches = true;
                    }
                } else {
                    valueMatches = true;
                }
                boolean typeMatches = false;
                if(!typeIsNull){
                    if(currentIdentifier.getType().contains(type)){
                        typeMatches = true;
                    }
                } else {
                    typeMatches = true;
                }
                boolean useMatches = false;
                if(!useIsNull){
                    if(currentIdentifier.getUse().equals(use)){
                        useMatches = true;
                    }
                } else {
                    useMatches = true;
                }
                if(useMatches && typeMatches && valueMatches){
                    ExtremelySimplifiedResource resource = this.identifier2ESRMap.get(currentIdentifier);
                    result.getSearchResultList().add(resource);
                    break;
                }
            }
            getLogger().debug(".searchCacheForESRUsingIdentifierParameters(): Exit, result --> {}", result);
            return(result);
        }
    }

    public ESRSearchResult searchCacheUsingDisplayName(String displayNameValue)
            throws ResourceInvalidSearchException {
        getLogger().debug(".searchCacheUsingDisplayName(): Entry, parameterName->{}", displayNameValue);
        if(displayNameValue == null){
            throw(new ResourceInvalidSearchException("Search parameter name or value are null"));
        }
        if(displayNameValue.isEmpty()){
            throw(new ResourceInvalidSearchException("Search parameter name is empty"));
        }
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(this.simplifiedID2ESRMap.isEmpty()){
            return(result);
        }
        String displayNameValueAsLowerCase = displayNameValue.toLowerCase();
        Enumeration<String> displayNameSet = displayName2ESRMap.keys();
        while(displayNameSet.hasMoreElements()){
            String currentDisplayName = displayNameSet.nextElement();
            if(currentDisplayName.toLowerCase().startsWith(displayNameValueAsLowerCase)){
                ExtremelySimplifiedResource resource = this.simplifiedID2ESRMap.get(currentDisplayName);
                result.getSearchResultList().add(resource);
            }
        }
        return(result);
    }

    public ESRSearchResult allResources(){
        getLogger().debug(".allResources(): Entry");
        ESRSearchResult result = instatiateNewESRSearchResult();
        result.getSearchResultList().addAll(this.simplifiedID2ESRMap.values());
        getLogger().debug(".allResources(): Exit");
        return(result);
    }

    protected ESRSearchResult searchCacheForESRUsingIdentifierLeafValue(String leafValue)
            throws ResourceInvalidSearchException {
        getLogger().debug("searchCacheForESRUsingIdentifierLeafValue(): Entry, leafValue->{}", leafValue);
        boolean valueIsNull = (leafValue == null);
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(valueIsNull) {
            return(result);
        } else {
            String leafValueAsLowerCase = leafValue.toLowerCase();
            Enumeration<IdentifierESDT> identifierSet = this.identifier2ESRMap.keys();
            while(identifierSet.hasMoreElements()){
                IdentifierESDT currentIdentifier = identifierSet.nextElement();
                boolean valueMatches = false;
                if(currentIdentifier.getLeafValue().toLowerCase().equals(leafValueAsLowerCase)){
                    ExtremelySimplifiedResource resource = this.identifier2ESRMap.get(currentIdentifier);
                    result.getSearchResultList().add(resource);
                    break;
                }
            }
            return(result);
        }
    }
}
