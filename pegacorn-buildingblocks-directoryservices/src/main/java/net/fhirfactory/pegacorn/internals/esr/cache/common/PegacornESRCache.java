package net.fhirfactory.pegacorn.internals.esr.cache.common;

import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.search.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;


public abstract class PegacornESRCache {
    private ConcurrentHashMap<IdentifierESDT, ExtremelySimplifiedResource> identifier2ESRMap;
    private ConcurrentHashMap<String, ExtremelySimplifiedResource> simplifiedID2ESRMap;
    private ConcurrentHashMap<String, ExtremelySimplifiedResource> displayName2ESRMap;

    abstract protected Logger getLogger();

    abstract public ESRSearchResult search(SearchCriteria searchCriteria)
            throws ResourceInvalidSearchException;
    abstract protected ESRSearchResult instatiateNewESRSearchResult();

    abstract public Boolean supportsSearchType(String attributeName);

    public PegacornESRCache(){
        identifier2ESRMap = new ConcurrentHashMap<>();
        simplifiedID2ESRMap = new ConcurrentHashMap<>();
        displayName2ESRMap = new ConcurrentHashMap<>();
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
            getLogger().info(".addCacheEntry(): creating an primaryKey");
            entry.assignSimplifiedID(entry.getOtherID(), "OtherID");
            getLogger().info(".addCacheEntry(): New Id --> {}", entry.getSimplifiedID());
        } else {
            getLogger().info(".addCacheEntry(): Resource has an Id already... attempting to retrieve associated Resource");
            ExtremelySimplifiedResource foundEntry = getCacheEntry(entry.getSimplifiedID());
            if(foundEntry != null){
                getLogger().info(".addCacheEntry(): Resource already exists, so cant create it again.... ");
                ESRMethodOutcome outcome = new ESRMethodOutcome();
                outcome.setStatus(ESRMethodOutcomeEnum.CREATE_ENTRY_DUPLICATE);
                outcome.setId(foundEntry.getSimplifiedID());
                outcome.setCreated(false);
                outcome.setEntry(foundEntry);
                getLogger().info(".addCacheEntry(): Exit, resource already exists");
                return(outcome);
            }
        }
        getLogger().info(".addCacheEntry(): Adding to Identifier based Cache");
        for(IdentifierESDT identifier: entry.getIdentifiers() ){
            this.identifier2ESRMap.put(identifier, entry);
        }
        getLogger().info(".addCacheEntry(): Adding to displayName based Cache");
        if(entry.getDisplayName() == null){
            entry.setDisplayName(entry.getSimplifiedID());
        }
        this.displayName2ESRMap.putIfAbsent(entry.getDisplayName().toLowerCase(), entry);
        getLogger().info(".addCacheEntry(): Adding to simplifiedID based Cache");
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
        getLogger().info(".getCacheEntry(): Entry (using IdentifierDE), identifier --> {}", identifier);
        if(identifier == null){
            getLogger().info(".getCacheEntry(): Exit, Identifier is null, so exiting");
            return(null);
        }
        ExtremelySimplifiedResource foundEntry = this.identifier2ESRMap.get(identifier);
        if(foundEntry == null) {
            getLogger().info(".getCacheEntry(): Exit, couldn't find element, returning NULL");
            return(null);
        } else {
            getLogger().info(".getCacheEntry(): Exit, value retrieved, returning it!");
            return(foundEntry);
        }
    }

    public ExtremelySimplifiedResource getCacheEntry(String idValue){
        getLogger().info(".getCacheEntry(): Entry (using Id), idValue --> {}", idValue);
        if(idValue == null){
            getLogger().info(".getCacheEntry(): Exit, id is NULL, so exiting");
            return(null);
        }
        if(simplifiedID2ESRMap.isEmpty()){
            getLogger().info(".getCacheEntry(): Exit, Cache is empty, so exiting");
            return(null);
        }
        ExtremelySimplifiedResource entry = this.simplifiedID2ESRMap.get(idValue);
        if(entry != null){
            getLogger().info(".getCacheEntry(): Exit, entry found");
            return(entry);
        } else {
            getLogger().info(".getCacheEntry(): Exit, entry not found");
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

    public ESRSearchResult searchCacheUsingSimplifiedID(SearchCriteria searchCriteria)
        throws ResourceInvalidSearchException {
        getLogger().debug(".search(): Entry, parameterName->{}", searchCriteria.getValue());
        
        if(searchCriteria.isValueNull()){
            throw(new ResourceInvalidSearchException("Search parameter value is null"));
        }
        
        if(searchCriteria.isParamNameNull()){
            throw(new ResourceInvalidSearchException("Search parameter name is null"));
        }
        
        if(searchCriteria.isValueEmpty()){
            throw(new ResourceInvalidSearchException("Search parameter value is empty"));
        }
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(this.simplifiedID2ESRMap.isEmpty()){
            return(result);
        }

        Enumeration<String> idSet = this.simplifiedID2ESRMap.keys();
        while(idSet.hasMoreElements()){
            String currentID = idSet.nextElement();
            if(currentID.toLowerCase().contains(searchCriteria.getValue().toLowerCase())){
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
        getLogger().info("searchCacheForESRUsingIdentifierParameters(): Entry, value->{}, type->{}, use->{}", identifier);
        
        SearchCriteria searchCriteria = new SearchCriteria(identifier.getType(), identifier.getValue());
        
        ESRSearchResult result = searchCacheForESRUsingIdentifierParameters(searchCriteria, identifier.getType(), identifier.getUse());
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

    protected ESRSearchResult searchCacheForESRUsingIdentifierParameters(SearchCriteria searchCriteria,
                                                                         String type,
                                                                         IdentifierESDTUseEnum use)
            throws ResourceInvalidSearchException {
        getLogger().debug("searchCacheForESRUsingIdentifierParameters(): Entry, value->{}, type->{}, use->{}", searchCriteria.getValue(), type, use);
        // Search First
        getLogger().trace(".searchCacheForESRUsingIdentifierParameters(): First, let's do the search!");
        boolean valueIsNull = (searchCriteria.isValueNull());
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
                    if(currentIdentifier.getValue().toLowerCase().contains(searchCriteria.getValue().toLowerCase())){
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
                }
            }
            getLogger().debug(".searchCacheForESRUsingIdentifierParameters(): Exit, result --> {}", result);
            return(result);
        }
    }

    public ESRSearchResult searchCacheUsingDisplayName(SearchCriteria searchCriteria)
            throws ResourceInvalidSearchException {
        getLogger().debug(".searchCacheUsingDisplayName(): Entry, parameterName->{}", searchCriteria.getValue());
        
        if(searchCriteria.isParamNameNull()){
            throw(new ResourceInvalidSearchException("Search parameter name empty"));
        }
        
        if(searchCriteria.isValueEmpty()){
            throw(new ResourceInvalidSearchException("Search parameter value is empty"));
        }
        
        
        if(searchCriteria.isValueNull()) {
            throw(new ResourceInvalidSearchException("Search parameter name is empty"));
        }
        
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(this.simplifiedID2ESRMap.isEmpty()){
        	getLogger().info(".searchCacheUsingDisplayName(): Exit, map is empty, no entries found");
            return(result);
        }

        
        Enumeration<String> displayNameSet = displayName2ESRMap.keys();
        while(displayNameSet.hasMoreElements()){
            String currentDisplayName = displayNameSet.nextElement();
            getLogger().info(".searchCacheUsingDisplayName(): Iterating through ESR Elements, currentEntry.displayName->{}, searchForDisplayName->{}", currentDisplayName, searchCriteria.getValue());
            if(currentDisplayName.toLowerCase().contains(searchCriteria.getValue().toLowerCase().toLowerCase())){
                ExtremelySimplifiedResource resource = this.displayName2ESRMap.get(currentDisplayName);
                                
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
        getLogger().info("searchCacheForESRUsingIdentifierLeafValue(): Entry, leafValue->{}", leafValue);
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
