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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.core.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.search.exceptions.ESRPaginationException;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.search.exceptions.ESRSortingException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSortException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceNotFoundException;
import org.apache.camel.Header;
import org.slf4j.Logger;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class HandlerBase {

    private static String PAGINATION_PAGE_SIZE = "pageSize";
    private static String PAGINATION_PAGE_NUMBER = "page";
    private static String SORT_ATTRIBUTE = "sortBy";
    private static String SORT_ORDER = "sortOrder";

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
        getLogger().debug(".getEntry(): Entry, pathValue --> {}", id);
        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        getLogger().debug(".getEntry(): Exit, outcome --> {}", outcome.getStatus());
        return(outcome);
    }

    public List<ExtremelySimplifiedResource> defaultGetResourceList( @Header("sortBy") String sortBy,
                                                                     @Header("sortOrder") String sortOrder,
                                                                     @Header("pageSize") String pageSize,
                                                                     @Header("page") String page)
            throws ESRPaginationException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException {
        getLogger().debug(".defaultGetResourceList(): Entry, sortBy->{}, sortOrder->{}, pageSize->{}, page->{}",sortBy, sortOrder, pageSize, page );
        Integer pageSizeValue = null;
        Integer pageValue = null;
        Boolean sortOrderValue = true;
        if(pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if(page != null) {
            pageValue = Integer.valueOf(page);
        }
        if(sortOrder != null) {
            sortOrderValue = Boolean.valueOf(sortOrder);
        }
        if(sortBy == null){
            sortBy = "simplifiedID";
        }
        ESRMethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(pageSizeValue, pageValue, sortBy, sortOrderValue);
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

    public List<ExtremelySimplifiedResource> defaultSearch( @Header("shortName") String shortName,
                                                            @Header("longName") String longName,
                                                            @Header("displayName") String displayName,
                                                            @Header("leafValue") String leafValue,
                                                            @Header("sortBy") String sortBy,
                                                            @Header("sortOrder") String sortOrder,
                                                            @Header("pageSize") String pageSize,
                                                            @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRPaginationException, ESRSortingException {
        getLogger().debug(".defaultSearch(): Entry, shortName->{}, longName->{}, displayName->{}"+
                        "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page);
        String searchAttributeName = null;
        String searchAttributeValue = null;
        if(shortName != null) {
            searchAttributeValue = shortName;
            searchAttributeName = "shortName";
        } else if(longName != null){
            searchAttributeValue = longName;
            searchAttributeName = "longName";
        } else if(displayName != null){
            searchAttributeValue = displayName;
            searchAttributeName = "displayName";
        } else if(leafValue != null) {
            searchAttributeValue = leafValue;
            searchAttributeName = "leafValue";
        } else {
            throw( new ResourceInvalidSearchException("Search parameter not specified"));
        }
        Integer pageSizeValue = null;
        Integer pageValue = null;
        Boolean sortOrderValue = true;
        if(pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if(page != null) {
            pageValue = Integer.valueOf(page);
        }
        if(sortOrder != null) {
            sortOrderValue = Boolean.valueOf(sortOrder);
        }
        String searchAttributeValueURLDecoded = URLDecoder.decode(searchAttributeValue, StandardCharsets.UTF_8);
        ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchAttributeName, searchAttributeValueURLDecoded, pageSizeValue, pageValue, sortBy, sortOrderValue);
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
