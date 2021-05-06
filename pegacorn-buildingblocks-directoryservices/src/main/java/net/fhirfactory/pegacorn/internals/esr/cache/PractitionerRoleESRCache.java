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
package net.fhirfactory.pegacorn.internals.esr.cache;

import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerRoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.search.PractitionerRoleSearchResult;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PractitionerRoleESRCache extends PegacornESRCache {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleESRCache.class);

    public PractitionerRoleESRCache(){
        super();
    }

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public ESRMethodOutcome addPractitionerRole(PractitionerRoleESR practitionerDirectoryEntry){
        ESRMethodOutcome outcome = addCacheEntry(practitionerDirectoryEntry);
        return(outcome);
    }

    public PractitionerRoleESR getPractitionerRole(IdentifierESDT practitionerID){
        ExtremelySimplifiedResource foundEntry = this.getCacheEntry(practitionerID);
        PractitionerRoleESR foundPractitionerDirectoryEntry = (PractitionerRoleESR) foundEntry;
        return(foundPractitionerDirectoryEntry);
    }

    //
    // Search Functions
    //

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult(){
        PractitionerRoleSearchResult result = new PractitionerRoleSearchResult();
        return(result);
    }

    @Override
    public Boolean supportsSearchType(String attributeName) {
        String searchAttributeNameLowerCase = attributeName.toLowerCase();
        switch(searchAttributeNameLowerCase){
            case "simplifiedid":
            case "shortnmae":
            case "longname":
            case "displayname":
            case "emailaddress":
            case "organizaton":
            case "primaryorganization":
            case "organisationid":
            case "primaryorganisation":
            case "primaryorganizationid":
            case "primaryorganisationid":
            case "location":
            case "primarylocation":
            case "primarylocationid":
            case "primaryrolecategory":
            case "primaryrolecategoryid":
            case "primaryrole":
            case "primaryroleid":
                return(true);
            default:
                return(false);
        }
    }

    @Override
    public ESRSearchResult search(SearchCriteria searchCriteria)
            throws ResourceInvalidSearchException {
    	
        getLogger().debug(".search(): Entry, searchAttributeName->{}, searchAttributeValue->{}", searchCriteria.getParamName(), searchCriteria.getValue());
        
        if(searchCriteria.isValueNull()){
            throw(new ResourceInvalidSearchException("Search Value is null"));
        }
        if(searchCriteria.isParamNameNull()){
            throw(new ResourceInvalidSearchException("Search Parameter Name is empty"));
        }
        
        
        ESRSearchResult result = instatiateNewESRSearchResult();
        
        if(searchCriteria.isValueEmpty()){
            return(result);
        }
        
        switch(searchCriteria.getParamName().toLowerCase()){
            case "simplifiedid":
            case "emailaddress":{
                result = this.searchCacheUsingSimplifiedID(searchCriteria);
                return (result);
            }
            case "shortname": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchCriteria, "ShortName", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "longname": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchCriteria, "LongName", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "displayname": {
                result = this.searchCacheUsingDisplayName(searchCriteria);
                return(result);
            }
            case "organizaton":
            case "primaryorganization":
            case "organisation":
            case "primaryorganisation":
            case "primaryorganizationid":
            case "primaryorganisationid":{
                result = this.searchCacheViaOrganization(searchCriteria);
                return(result);
            }
            case "location":
            case "primarylocation":
            case "primarylocationid": {
                result = this.searchCacheViaLocation(searchCriteria);
                return(result);
            }
            case "rolecategory":
            case "rolecategoryid":
            case "primaryrolecategoryid":{
                result = this.searchCacheViaRoleCategory(searchCriteria);
                return(result);
            }
            case "role":
            case "roleid":
            case "primaryroleid":{
                result = this.searchCacheViaRole(searchCriteria);
                return(result);
            }
            default: {
                return (result);
            }
        }
    }

    public ESRSearchResult searchCacheViaOrganization(SearchCriteria searchCriteria){
        LOG.debug(".searchCacheViaOrganization(): Entry");
        ESRSearchResult result = instatiateNewESRSearchResult();
        
        if(this.getSimplifiedID2ESRMap().isEmpty()){
            LOG.debug(".searchCacheViaOrganization(): Exit, cache is empty, so returning empty list!");
            return(result);
        }
        for(ExtremelySimplifiedResource currentResource: this.getSimplifiedID2ESRMap().values()){
            PractitionerRoleESR currentPractitionerRole = (PractitionerRoleESR) currentResource;
            if(currentPractitionerRole.getPrimaryOrganizationID().toLowerCase().contains(searchCriteria.getValue().toLowerCase())){
                result.getSearchResultList().add(currentPractitionerRole);
            }
        }
        
        LOG.debug(".searchCacheViaOrganization(): Exit");
        return(result);
    }

    public ESRSearchResult searchCacheViaLocation(SearchCriteria searchCriteria){
        ESRSearchResult result = instatiateNewESRSearchResult();

        if(this.getSimplifiedID2ESRMap().isEmpty()){
            return(result);
        }
        for(ExtremelySimplifiedResource currentResource: this.getSimplifiedID2ESRMap().values()){
            PractitionerRoleESR currentPractitionerRole = (PractitionerRoleESR) currentResource;
            if(currentPractitionerRole.getPrimaryLocationID().toLowerCase().contains(searchCriteria.getValue().toLowerCase())){
                result.getSearchResultList().add(currentPractitionerRole);
            }
        }
        return(result);
    }

    public ESRSearchResult searchCacheViaRoleCategory(SearchCriteria searchCriteria){
        ESRSearchResult result = instatiateNewESRSearchResult();
        

        if(this.getSimplifiedID2ESRMap().isEmpty()){
            return(result);
        }
        
        for(ExtremelySimplifiedResource currentResource: this.getSimplifiedID2ESRMap().values()){
            PractitionerRoleESR currentPractitionerRole = (PractitionerRoleESR) currentResource;
            if(currentPractitionerRole.getPrimaryRoleCategoryID().toLowerCase().contains(searchCriteria.getValue().toLowerCase())){
                result.getSearchResultList().add(currentPractitionerRole);
            }
        }
        return(result);
    }

    public ESRSearchResult searchCacheViaRole(SearchCriteria searchCriteria){
        ESRSearchResult result = instatiateNewESRSearchResult();

        if(this.getSimplifiedID2ESRMap().isEmpty()){
            return(result);
        }
        for(ExtremelySimplifiedResource currentResource: this.getSimplifiedID2ESRMap().values()){
            PractitionerRoleESR currentPractitionerRole = (PractitionerRoleESR) currentResource;
            if(currentPractitionerRole.getPrimaryRoleID().toLowerCase().contains(searchCriteria.getValue().toLowerCase())){
                result.getSearchResultList().add(currentPractitionerRole);
            }
        }
        return(result);
    }
}
