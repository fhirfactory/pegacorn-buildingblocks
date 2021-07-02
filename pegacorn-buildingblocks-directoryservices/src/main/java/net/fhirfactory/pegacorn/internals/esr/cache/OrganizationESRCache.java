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

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.OrganizationESR;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.search.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.result.OrganizationSearchResult;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;

@ApplicationScoped
public class OrganizationESRCache extends PegacornESRCache {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationESRCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public OrganizationESRCache(){
        super();
    }

    public ESRMethodOutcome addDirectoryEntry(OrganizationESR directoryEntry){
        ESRMethodOutcome outcome = addCacheEntry(directoryEntry);
        return(outcome);
    }

    public OrganizationESR getDirectoryEntry(IdentifierESDT entryIdentifierESDT){
        ExtremelySimplifiedResource foundEntry = this.getCacheEntry(entryIdentifierESDT);
        OrganizationESR foundEntrySubclass = (OrganizationESR) foundEntry;
        return(foundEntrySubclass );
    }

    //
    // Search Functions
    //

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult(){
        OrganizationSearchResult result = new OrganizationSearchResult();
        return(result);
    }


    @Override
    public ESRSearchResult search(SearchCriteria searchCriteria)
            throws ResourceInvalidSearchException {
    	
        getLogger().debug(".search(): Entry, searchAttributeName->{}, searchAttributeValue->{}", searchCriteria.getSearchParam().getName(), searchCriteria.getSearchParam().getValue());
        
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
        

        switch(searchCriteria.getSearchParam().getName()) {
            case SIMPLIFIED_ID: {
                result = this.searchCacheUsingSimplifiedID(searchCriteria);
                return (result);
            }
            case SHORT_NAME: {
                result = this.searchCacheForESRUsingIdentifierParameters(searchCriteria, IdentifierType.SHORT_NAME, IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case LONG_NAME: {
                result = this.searchCacheForESRUsingIdentifierParameters(searchCriteria, IdentifierType.LONG_NAME, IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case DISPLAY_NAME: {
                result = this.searchCacheUsingDisplayName(searchCriteria);
                return(result);
            }
            case ALL_NAME: {
            	result = this.searchCacheUsingAllNames(searchCriteria, IdentifierESDTUseEnum.USUAL, IdentifierESDTUseEnum.USUAL);
            	return result;
            }
            default: {
                return (result);
            }
        }
    }
    
    
    /**
     * Returns a directory entry where the leaf value matches the supplied recordID.
     * 
     * @param recordID
     * @return
     */
    public OrganizationESR getDirectoryEntryForLeafValue(String recordID) {
        
        for (Map.Entry<IdentifierESDT, ExtremelySimplifiedResource> entry : this.getIdentifier2ESRMap().entrySet()) {
            String leafValue = entry.getKey().getLeafValue();
            
            if (leafValue != null && leafValue.equalsIgnoreCase(recordID)) {
                return (OrganizationESR)entry.getValue();
            }
        
        }
        
        return null;
    } 
}
