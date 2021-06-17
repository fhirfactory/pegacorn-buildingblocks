/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.directories.api.beans.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSortException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceNotFoundException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParamNames;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;


public abstract class HandlerBase {
       
    protected static final String TOTAL_RECORD_COUNT_HEADER = "X-Total-Count";
    protected static final String ACCESS_CONTROL_EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";

    abstract protected Logger getLogger();
    abstract protected ESRBroker specifyResourceBroker();
    abstract protected void printOutcome(ESRMethodOutcome outcome);

    protected ESRBroker getResourceBroker(){
        return(specifyResourceBroker());
    }


    //
    // Review
    //

    protected ESRMethodOutcome getResource(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().info(".getEntry(): Entry, pathValue --> {}", id);
        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        getLogger().info(".getEntry(): Exit, outcome --> {}", outcome.getStatus());
        return(outcome);
    }

    public List<ExtremelySimplifiedResource> defaultGetResourceList( Exchange exchange,
    																 @Header("sortBy") String sortBy,
                                                                     @Header("sortOrder") String sortOrder,
                                                                     @Header("pageSize") String pageSize,
                                                                     @Header("page") String page)
            throws ESRPaginationException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRFilteringException {
        getLogger().debug(".defaultGetResourceList(): Entry, sortBy->{}, sortOrder->{}, pageSize->{}, page->{}",sortBy, sortOrder, pageSize, page );
        Integer pageSizeValue = null;
        Integer pageValue = null;

        if(pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if(page != null) {
            pageValue = Integer.valueOf(page);
        }

        SearchCriteria searchCriteria = new SearchCriteria();
        
        Pagination pagination = new Pagination(pageSizeValue, pageValue);
        Sort sort = new Sort(sortBy, sortOrder);
        
        ESRMethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(searchCriteria, new ArrayList<BaseFilter>(), pagination, sort);
        
        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);
        
        getLogger().debug(".defaultGetResourceList(): Exit");
                       
        return(outcome.getSearchResult());
    }

    //
    // Review (Search via Identifier)
    //

    protected ESRMethodOutcome identifierBasedSearch(IdentifierESDT identifier) throws ResourceInvalidSearchException {
        getLogger().debug(".doEntrySearch(): Entry, value->{}, type->{}, use->{}", identifier.getValue(), identifier.getType(), identifier.getUse());
        ESRMethodOutcome outcome = this.getResourceBroker().searchForDirectoryEntryUsingIdentifier(identifier);
        getLogger().debug(".doEntrySearch(): Exit");
        return(outcome);
    }

    //
    // Review (General Search)
    //

    public List<ExtremelySimplifiedResource> defaultSearch( Exchange exchange, 
    														@Header("simplifiedID") String simplifiedID,
    														@Header("shortName") String shortName,
                                                            @Header("longName") String longName,
                                                            @Header("displayName") String displayName,
                                                            @Header("allName") String allName,
                                                            @Header("leafValue") String leafValue,
                                                            @Header("sortBy") String sortBy,
                                                            @Header("sortOrder") String sortOrder,
                                                            @Header("pageSize") String pageSize,
                                                            @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRPaginationException, ESRSortingException, ESRFilteringException {
        getLogger().info(".defaultSearch(): Entry, shortName->{}, longName->{}, displayName->{},"+
                        "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page);
        SearchParamNames searchAttributeName = null;
        String searchAttributeValue = null;
        if(simplifiedID != null) {
            searchAttributeValue = simplifiedID;
            searchAttributeName = SearchParamNames.SIMPLIFIED_ID;
        } else if(shortName != null) {
            searchAttributeValue = shortName;
            searchAttributeName = SearchParamNames.SHORT_NAME;
        } else if(longName != null){
            searchAttributeValue = longName;
            searchAttributeName = SearchParamNames.LONG_NAME;
        } else if(displayName != null){
            searchAttributeValue = displayName;
            searchAttributeName = SearchParamNames.DISPLAY_NAME;
        } else if(leafValue != null) {
            searchAttributeValue = leafValue;
            searchAttributeName = SearchParamNames.LEAF_VALUE;
        } else if(allName != null) {
            searchAttributeValue = allName;
            searchAttributeName = SearchParamNames.ALL_NAME;
        }
        else {
            throw( new ResourceInvalidSearchException("Search parameter not specified"));
        }
        
        SearchCriteria searchCriteria = new SearchCriteria(searchAttributeName, searchAttributeValue);
        
        
        Integer pageSizeValue = null;
        Integer pageValue = null;

        if(pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if(page != null) {
            pageValue = Integer.valueOf(page);
        }

        Pagination pagination = new Pagination(pageSizeValue, pageValue);
        Sort sort = new Sort(sortBy, sortOrder);

        ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchCriteria, new ArrayList<BaseFilter>(), sort, pagination);
        
        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);
        
        getLogger().debug(".defaultSearch(): Exit");
                         
        return(outcome.getSearchResult());
    }

    //
    // JSON Helpers
    //

    protected String convertToJSONString(List<ExtremelySimplifiedResource> entrySet){
        JsonMapper jsonMapper = new JsonMapper();
        try {
            String arrayAsList = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entrySet);
            return(arrayAsList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return("");
    }

    protected String convertToJSONString(ExtremelySimplifiedResource entry){
        JsonMapper jsonMapper = new JsonMapper();
        try {
            String resourceAsString = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entry);
            return(resourceAsString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return("");
    }
}
