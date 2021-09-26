/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.common;

import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcomeFactory;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.datatypes.CacheActivityStatusElement;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.datatypes.CacheResourceEntry;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.factories.CacheActivityStatusElementFactory;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.factories.CacheResourceIdentifierFactory;

import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.valuesets.CacheActivityActionEnum;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.valuesets.CacheActivityOutcomeEnum;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Enumeration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class IdTypeBasedCacheBase {

    @Inject
    private CacheResourceIdentifierFactory virtualDBKeyManagement;

    @Inject
    private CacheActivityStatusElementFactory outcomeFactory;

    private ConcurrentHashMap<IdType, CacheResourceEntry> resourceCacheById;
    private ConcurrentHashMap<IdType, Object> resourceCacheLockSet;
    boolean isInitialised;

    protected IdTypeBasedCacheBase() {
        resourceCacheById = new ConcurrentHashMap<>();
        resourceCacheLockSet = new ConcurrentHashMap<>();
        this.isInitialised = false;
    }

    protected abstract Logger getLogger();
    protected abstract boolean areTheSame(Resource a, Resource b);
    protected abstract List<Identifier> resolveIdentifierSet(Resource resourceToAdd);
    protected abstract void addIdentifierToResource(Identifier identifierToAdd, Resource resource);
    protected abstract String getCacheClassName();
    protected abstract Resource createClonedResource(Resource resource);

    @PostConstruct
    protected void initialise() {
        if (!this.isInitialised) {
            getLogger().debug(".initialise(): Initialising the FHIR Parser framework");
            this.isInitialised = true;
        }
    }

    /**
     * The function adds a Resource to the Resource Cache. It wraps the Resource in a CacheResourceEntry,
     * which enables the cache management functions to ascertain the age of the cache entry for clean-up
     * purposes.
     *
     * @param resourceToAdd A FHIR::Resource that is to be added to the Cache.
     * @return A CacheActivityStatusElement instance detailing the success (or otherwise) of the Resource
     * addition to the Cache.
     */
    private CacheActivityStatusElement addResourceToCache(Resource resourceToAdd){
        getLogger().debug(".addResourceToCache(): Entry, resourceToAdd->{}", resourceToAdd);
        String activityLocation = getCacheClassName() + "::addResourceToCache()";
        if(resourceToAdd == null) {
            getLogger().error(".addResourceToCache(): resourceToAdd (Resource) is null, failing out");
            CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement(resourceToAdd.getIdElement(), CacheActivityActionEnum.CACHE_ACTION_RESOURCE_CREATE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation, "Resource is empty");
            getLogger().debug(".addResourceToCache(): Exit, outcome->{}", outcome);
            return (outcome);
        }
        IdType resourceId = resourceToAdd.getIdElement();
        if(!resourceToAdd.hasId()){
            String newID = resourceToAdd.getResourceType().toString() + ":" + UUID.randomUUID().toString();
            resourceToAdd.setId(newID);
            resourceId = new IdType(newID);
        }
        if(resourceCacheById.containsKey(resourceId)){
            CacheResourceEntry resourceEntry = resourceCacheById.get(resourceId);
            if(resourceEntry != null){
                Resource existingResource = resourceEntry.getResource();
                if(areTheSame(existingResource, resourceToAdd)){
                    CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement(resourceId, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_CREATE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_CREATED, activityLocation, "Resource did already exist!");
                    getLogger().debug(".addResourceToCache(): Exit, outcome->{}", outcome);
                    return(outcome);
                }
            }
            resourceCacheById.remove(resourceId);
            resourceCacheLockSet.remove(resourceId);
        }
        resourceCacheLockSet.put(resourceId, new Object());
        CacheResourceEntry newEntry = new CacheResourceEntry(resourceToAdd);
        resourceCacheById.put(resourceId, newEntry);
        CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement(resourceId, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_CREATE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_CREATED, activityLocation, null);
        outcome.setResourceId(resourceToAdd.getIdElement());
        getLogger().debug(".addResourceToCache(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    /**
     * This function removes a Resource from the Resource Cache using the provided id.
     *
     * @param id
     * @return A CacheActivityStatusElement instance detailing the success (or otherwise) of the Resource removal activity.
     */
    private CacheActivityStatusElement deleteResourceFromCache(IdType id){
        String activityLocation = getCacheClassName() + "::deleteResourceFromCache()";
        if(id == null){
            getLogger().debug(".deleteResourceFromCache(): id (IdType) is null, failing out");
            CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement(id, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_REMOVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation, "Parameter identifier (Identifier) content is invalid");
            return(outcome);
        }
        if(resourceCacheById.containsKey(id)) {
            resourceCacheById.remove(id);
            resourceCacheLockSet.remove(id);
            CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement(id, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_REMOVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_REMOVED, activityLocation, null);
            return (outcome);
        } else {
            CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement(id, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_REMOVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation, "Resource not in Cache");
            return (outcome);
        }
    }

    /**
     *
     * @param identifier
     * @return
     */
    private CacheActivityStatusElement getResourceFromCache(Identifier identifier){
        getLogger().debug(".getResourceFromCache(): Entry, identifier (Identifier) --> {}", identifier);
        String activityLocation = getCacheClassName() + "::getResourceFromCache()";
        if(identifier == null){
            getLogger().debug(".getResourceFromCache(): identifier (Identifier) is null, failing out");
            CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement(identifier, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_RETRIEVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation,"Parameter identifier (Identifier) content is invalid");
            return(outcome);
        }
        CacheResourceEntry foundResourceEntry = null;
        for(CacheResourceEntry currentResourceEntry: resourceCacheById.values()){
            boolean found = false;
            List<Identifier> identifiers = resolveIdentifierSet(currentResourceEntry.getResource());
            for(Identifier currentIdentifier: identifiers){
                boolean systemIsSame = identifier.getSystem().equals(currentIdentifier.getSystem());
                boolean valueIsSame = identifier.getValue().equals(currentIdentifier.getValue());
                if(systemIsSame && valueIsSame){
                    found = true;
                    foundResourceEntry = currentResourceEntry;
                    break;
                }
            }
            if(found){
                break;
            }
        }
        CacheActivityStatusElement outcome;
        if(foundResourceEntry != null){
            outcome = outcomeFactory.newCacheActivityStatusElement(identifier, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_RETRIEVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_RETRIEVED, activityLocation, null);
            outcome.setResourceId(foundResourceEntry.getResource().getIdElement());
            outcome.setResource(foundResourceEntry.getResource());
        } else {
            outcome = outcomeFactory.newCacheActivityStatusElement(identifier, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_RETRIEVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation, "Could Not Find Resource");
        }
        getLogger().debug(".getResourceFromCache(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    /**
     *
     * @param id
     * @return
     */
    private CacheActivityStatusElement getResourceFromCache(IdType id) {
        getLogger().debug(".getResourceFromCache(): Entry, id (IdType) --> {}", id);
        String activityLocation = getCacheClassName() + "::getResourceFromCache()";
        if (id == null) {
            getLogger().error(".getResourceFromCache(): identifier (Identifier) is null, failing out");
            CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement( id, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_RETRIEVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation,"Parameter identifier (Identifier) content is invalid");
            getLogger().debug(".getResourceFromCache(): Exit, outcome->{}", outcome);
            return (outcome);
        }
        CacheActivityStatusElement outcome = null;
        if(resourceCacheById.containsKey(id)){
            CacheResourceEntry cacheResourceEntry = this.resourceCacheById.get(id);
            outcome = new CacheActivityStatusElement();
            outcome = outcomeFactory.newCacheActivityStatusElement(id, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_RETRIEVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_RETRIEVED, activityLocation, null);
            outcome.setResource(cacheResourceEntry.getResource());
        } else {
            outcome = outcomeFactory.newCacheActivityStatusElement(id, CacheActivityActionEnum.CACHE_ACTION_RESOURCE_RETRIEVE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation, "Could Not Find Resource");
        }
        getLogger().debug(".getResourceFromCache(): Exit, outcome->{}", outcome);
        return (outcome);
    }


    /**
     * This is a helper method, and is not intended for use outside of finding Resources
     * @return A collection of ALL the Resources within the Cache
     */
    public Collection<Resource> getAllResourcesFromCache(){
        getLogger().debug(".getAllResourcesFromCache(): Entry");
        ArrayList<Resource> resourceSet = new ArrayList<>();
        for(CacheResourceEntry resourceEntry: resourceCacheById.values() ){
            resourceSet.add(resourceEntry.getResource());
        }
        getLogger().debug(".getAllResourcesFromCache(): Exit");
        return(resourceSet);
    }

    /**
     * This method is a simple facade to the VirtualDBKeyManagement method of the same name.
     *
     * This method cycles through all the Identifiers and attempts to return "the best"!
     *
     * Order of preference is: OFFICIAL --> USUAL --> SECONDARY --> TEMP --> OLD --> ANY
     *
     * @param identifierSet The list of Identifiers contained within a Resource
     * @return The "Best" identifier from the set.
     */
    protected Identifier getBestIdentifier(List<Identifier> identifierSet){
        Identifier bestIdentifier = virtualDBKeyManagement.getBestIdentifier(identifierSet);
        return(bestIdentifier);
    }

    //
    // Public Cache Methods
    //

    public CacheActivityStatusElement getResource(Identifier identifier){
        getLogger().debug(".getResource(): Entry, id (Identifier) --> {}", identifier);
        CacheActivityStatusElement retrievedResource = getResourceFromCache(identifier);
        getLogger().debug(".getResource(): Exit, outcome --> {}", retrievedResource);
        return(retrievedResource);
    }

    public CacheActivityStatusElement getResource(IdType id){
        getLogger().debug(".getResource(): Entry, id (IdType) --> {}", id);
        CacheActivityStatusElement retrievedResource = getResourceFromCache(id);
        getLogger().debug(".getResource(): Exit, outcome --> {}", retrievedResource);
        return(retrievedResource);
    }

    public CacheActivityStatusElement createResource(Resource resourceToAdd){
        getLogger().debug(".createResource(): resourceToAdd --> {}", resourceToAdd);
        CacheActivityStatusElement outcome = addResourceToCache(resourceToAdd);
        getLogger().debug(".createResource(): Resource inserted, outcome (CacheActivityStatusElement) --> {}", outcome);
        return(outcome);
    }

    public CacheActivityStatusElement deleteResource(Resource resourceToRemove){
        getLogger().debug(".removeResource(): resourceToRemove --> {}", resourceToRemove);
        CacheActivityStatusElement outcome = deleteResourceFromCache(resourceToRemove.getIdElement());
        getLogger().debug(".removeResource(): Resource removed, outcome (CacheActivityStatusElement) --> {}", outcome);
        return(outcome);
    }

    public CacheActivityStatusElement updateResource(Resource resourceToUpdate){
        getLogger().debug(".updateResource(): resourceToUpdate --> {}", resourceToUpdate);
        CacheActivityStatusElement deleteOutcome = deleteResourceFromCache(resourceToUpdate.getIdElement());
        CacheActivityStatusElement updateOutcome = addResourceToCache(resourceToUpdate);
        getLogger().debug(".updateResource(): Resource updated, outcome (CacheActivityStatusElement) --> {}", updateOutcome);
        return(updateOutcome);
    }

    public CacheActivityStatusElement syncResource(Resource resourceToSync){
        String activityLocation = getCacheClassName() + "::" + "syncResource()";
        if(resourceToSync == null){
            CacheActivityStatusElement outcome = outcomeFactory.newCacheActivityStatusElement(resourceToSync.getIdElement(), CacheActivityActionEnum.CACHE_ACTION_RESOURCE_SYNCHRONISE , CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation, "Parameter resourceToSync (Resource) content is invalid");
            return(outcome);
        }
        if(!resourceToSync.hasId()){
            String newID = resourceToSync.getResourceType().toString() + ":" + UUID.randomUUID().toString();
            resourceToSync.setId(newID);
        }
        IdType resourceId = resourceToSync.getIdElement();
        CacheActivityStatusElement outcome;
        if(resourceCacheById.containsKey(resourceId)){
            Object lockObject = resourceCacheLockSet.get(resourceId);
            if(lockObject == null){
                lockObject = new Object();
                resourceCacheLockSet.put(resourceToSync.getIdElement(), lockObject);
            }
            CacheResourceEntry cacheEntry = resourceCacheById.get(resourceId);
            Resource cacheResource = cacheEntry.getResource();
            synchronized(lockObject) {
                deleteResourceFromCache(resourceId);
                addResourceToCache(resourceToSync);
            }
            outcome = outcomeFactory.newCacheActivityStatusElement(resourceToSync.getIdElement(), CacheActivityActionEnum.CACHE_ACTION_RESOURCE_SYNCHRONISE, CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_UPDATED, activityLocation, null);
        } else {
            outcome = outcomeFactory.newCacheActivityStatusElement(resourceToSync.getIdElement(), CacheActivityActionEnum.CACHE_ACTION_RESOURCE_SYNCHRONISE , CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR, activityLocation, "Resource is not in the Cache");
            return(outcome);
        }
        getLogger().debug(".syncResource(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    public Object getResourceLock(IdType resourceId){
        if(resourceCacheLockSet.containsKey(resourceId)){
            return(resourceCacheLockSet.get(resourceId));
        } else {
            Object newLock = new Object();
            resourceCacheLockSet.put(resourceId,newLock);
            return(newLock);
        }
    }

    public Resource startResourceAttributeUpdate(Resource resourceToModify){
        Resource modifiableResource = createClonedResource(resourceToModify);
        return(modifiableResource);
    }

    public void finaliseResourceAttributeUpdate(Resource modifiedResource){
        syncResource(modifiedResource);
    }
}
