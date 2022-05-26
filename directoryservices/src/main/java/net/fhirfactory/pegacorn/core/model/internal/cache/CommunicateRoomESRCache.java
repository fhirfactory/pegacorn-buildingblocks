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
package net.fhirfactory.pegacorn.core.model.internal.cache;

import net.fhirfactory.pegacorn.core.model.internal.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.CommunicateRoomESR;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.search.MatrixRoomSearchResult;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.search.common.ESRSearchResult;
import net.fhirfactory.pegacorn.core.model.internal.transactions.exceptions.ResourceInvalidSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Enumeration;

@ApplicationScoped
public class CommunicateRoomESRCache extends PegacornESRCache {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateRoomESRCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public CommunicateRoomESRCache(){
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
    public Boolean supportsSearchType(String attributeName) {
        String searchAttributeNameLowerCase = attributeName.toLowerCase();
        switch(searchAttributeNameLowerCase){
            case "simplifiedid":
            case "shortnmae":
            case "longname":
            case "displayname":
            case "canonicalAlias":
                return(true);
            default:
                return(false);
        }
    }

    @Override
    public ESRSearchResult search(String searchAttributeName, String searchAttributeValue)
            throws ResourceInvalidSearchException {
        getLogger().debug(".search(): Entry, searchAttributeName->{}, searchAttributeValue->{}", searchAttributeName, searchAttributeValue);
        if(searchAttributeName == null || searchAttributeValue == null){
            throw(new ResourceInvalidSearchException("Search Parameter Name or Value are null"));
        }
        if(searchAttributeName.isEmpty()){
            throw(new ResourceInvalidSearchException("Search Parameter Name is empty"));
        }
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(searchAttributeValue.isEmpty()){
            return(result);
        }
        String searchAttributeNameLowerCase = searchAttributeName.toLowerCase();
        switch(searchAttributeNameLowerCase){
            case "simplifiedid": {
                result = this.searchCacheUsingSimplifiedID(searchAttributeValue);
                return (result);
            }
            case "shortname": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchAttributeValue, "ShortName", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "longname": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchAttributeValue, "LongName", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "displayname": {
                result = this.searchCacheUsingDisplayName(searchAttributeValue);
                return(result);
            }
            case "canonicalalias":{
                result = this.searchCacheUsingCanonicalAlias(searchAttributeValue);
                return(result);
            }
            default: {
                return (result);
            }
        }
    }

    public ESRSearchResult searchCacheUsingCanonicalAlias(String canonicalAlias)
            throws ResourceInvalidSearchException {
        getLogger().debug(".searchCacheUsingCanonicalAlias(): Entry, canonicalAlias->{}", canonicalAlias);
        if(canonicalAlias == null){
            throw(new ResourceInvalidSearchException("Search parameter name or value are null"));
        }
        if(canonicalAlias.isEmpty()){
            throw(new ResourceInvalidSearchException("Search parameter name is empty"));
        }
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(this.getSimplifiedID2ESRMap().isEmpty()){
            return(result);
        }
        String simplifiedIDValueAsLowerCase = canonicalAlias.toLowerCase();
        Enumeration<String> idSet = this.getSimplifiedID2ESRMap().keys();
        while(idSet.hasMoreElements()){
            String currentID = idSet.nextElement();
            CommunicateRoomESR matrixRoom = (CommunicateRoomESR)this.getSimplifiedID2ESRMap().get(currentID);
            String currentCanonicalAlias = matrixRoom.getCanonicalAlias();
            if(currentCanonicalAlias.toLowerCase().contentEquals(canonicalAlias)){
               result.getSearchResultList().add(matrixRoom);
            }
        }
        return(result);
    }
}
