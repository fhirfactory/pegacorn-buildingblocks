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

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.search.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.result.MatrixRoomSearchResult;

@ApplicationScoped
public class MatrixRoomESRCache extends PegacornESRCache {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomESRCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public MatrixRoomESRCache(){
        super();
    }

    //
    // Search Functions
    //

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult(){
        MatrixRoomSearchResult result = new MatrixRoomSearchResult();
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
            case ALL_NAME : {
            	result = this.searchCacheUsingAllNames(searchCriteria, IdentifierESDTUseEnum.USUAL, IdentifierESDTUseEnum.USUAL);
            	return result;
            }
            default: {
                return (result);
            }
        }
    }
}
