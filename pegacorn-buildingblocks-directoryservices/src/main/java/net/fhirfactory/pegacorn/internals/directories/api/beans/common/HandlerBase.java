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
import net.fhirfactory.pegacorn.internals.directories.brokers.common.ResourceDirectoryBroker;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDEUseEnum;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.PegId;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSearchException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSortException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;

import java.util.*;

public abstract class HandlerBase {

    private static String PAGINATION_PAGE_SIZE = "pageSize";
    private static String PAGINATION_PAGE_NUMBER = "page";
    private static String SORT_ATTRIBUTE = "sortBy";
    private static String SORT_ORDER = "sortOrder";

    abstract protected Logger getLogger();
    abstract protected ResourceDirectoryBroker specifyResourceBroker();
    abstract protected void printOutcome(DirectoryMethodOutcome outcome);

    protected ResourceDirectoryBroker getResourceBroker(){
        return(specifyResourceBroker());
    }

    public Map<String, String> extractParameters(String parameterString){

        HashMap<String, String> parameterMap = new HashMap<>();
        if(parameterString == null){
            return(parameterMap);
        }
        if(parameterString.length()<3){
            return(parameterMap);
        }
        String[] parameterSet = parameterString.split("&");
        int numberOfParameters = parameterSet.length;
        for(int counter = 0; counter < numberOfParameters; counter += 1){
            String[] parameterPair = parameterSet[counter].split("=");
            parameterMap.put(parameterPair[0], parameterPair[1]);
        }
        return(parameterMap);
    }

    protected String convertToJSONString(List<PegacornDirectoryEntry> entrySet){
        JsonMapper jsonMapper = new JsonMapper();
        try {
            String arrayAsList = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entrySet);
            return(arrayAsList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return("");
    }

    protected String convertToJSONString(PegacornDirectoryEntry entry){
        JsonMapper jsonMapper = new JsonMapper();
        try {
            String resourceAsString = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entry);
            return(resourceAsString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return("");
    }
    
    //
    // get
    //

    public String get(@Header("CamelHttpQuery") String parameters, @Header("CamelHttpPath") String extraPath, String inputBody, Exchange camelExchange)
            throws DirectoryEntryNotFoundException, DirectoryEntryInvalidSearchException, DirectoryEntryInvalidSortException {
        getLogger().info(".get(): Entry, extraPath --> {}, parameters --> {}, inputBody --> {}", extraPath, parameters, inputBody);
        if(extraPath.isEmpty() || extraPath == null){
            getLogger().info(".get(): Is a Search, so invoking Search function");
            String outcome = doSearch(parameters, inputBody, camelExchange);
            return(outcome);
        } else {
            getLogger().info(".get(): Is a singular Get");
            DirectoryMethodOutcome outcome = getEntry(extraPath, inputBody, camelExchange);
            if(outcome.getStatus().equals(DirectoryMethodOutcomeEnum.REVIEW_ENTRY_FOUND)) {
                PegacornDirectoryEntry directoryEntry = outcome.getEntry();
                getLogger().info(".get(): Result --> {}", directoryEntry);
                String result = convertToJSONString(directoryEntry);
                return (result);
            } else {
                throw( new DirectoryEntryNotFoundException(outcome.getId().getValue()));
            }
        }
    }

    private DirectoryMethodOutcome getEntry(String pathValue, String inputBody, Exchange camelExchange){
        getLogger().info(".getEntry(): Entry, pathValue --> {}", pathValue);
        String idValue = pathValue.substring(1);
        getLogger().info(".getEntry(): Extracted DirectoryEntry Id --> {}", idValue);
        PegId id = new PegId(idValue);
        DirectoryMethodOutcome outcome = getResourceBroker().reviewDirectoryEntry(id);
        getLogger().info(".getEntry(): Exit, outcome --> {}", outcome.getStatus());
        return(outcome);
    }

    //
    // Search
    //

    private String doSearch(String parameters, String inputBody,  Exchange camelExchange)
            throws DirectoryEntryInvalidSearchException, DirectoryEntryInvalidSortException {
        getLogger().info(".doSearch(): Entry, parameters --> {}, inputBody --> {}", parameters, inputBody);
        Map<String, String> parameterSet = this.extractParameters(parameters);
        Integer pageSize = extractPaginationPageSize(parameterSet);
        Integer pageNumber = extractPaginationPageNumber(parameterSet);
        String sortParameter = extractSortAttribute(parameterSet);
        Boolean sortOrderAscending = extractSortOrder(parameterSet);
        Map<String, String> strippedParameterSet = new HashMap<>();
        for(String currentParameter: parameterSet.keySet()) {
            boolean isPageSize = currentParameter.contentEquals(PAGINATION_PAGE_SIZE);
            boolean isPageNumber = currentParameter.contentEquals(PAGINATION_PAGE_NUMBER);
            boolean isSortOrder = currentParameter.equalsIgnoreCase(SORT_ORDER);
            boolean isSortAttribute = currentParameter.equalsIgnoreCase(SORT_ATTRIBUTE);
            if (!isPageSize && !isPageNumber && !isSortOrder && !isSortAttribute) {
                strippedParameterSet.put(currentParameter, parameterSet.get(currentParameter));
            }
        }
        if(strippedParameterSet.isEmpty() ) {
            String outcome = doFullDirectoryEntrySetRetrieval(pageSize, pageNumber, sortParameter, sortOrderAscending);
            return(outcome);
        }
        if(strippedParameterSet.size() > 1) {
            throw (new DirectoryEntryInvalidSearchException("Compound search parameters currently not supported"));
        }
        Boolean isIdentifierBasedSearch = isIdentifierBasedSearch(strippedParameterSet);
        DirectoryMethodOutcome outcome = null;
        if(isIdentifierBasedSearch){
            outcome = doIdentifierSearch(strippedParameterSet);
        } else {
            outcome = getResourceBroker().doAttributeBasedSearch(strippedParameterSet, pageSize, pageNumber, sortParameter, sortOrderAscending);
        }
        if (!outcome.isSearchSuccessful()) {
            return ("");
        } else {
            String resutAsString = convertToJSONString(outcome.getSearchResult());
            return (resutAsString);
        }
    }

    protected Integer extractPaginationPageSize(Map<String, String> parameterSet){
        getLogger().info(".extractPaginationPageSize(): Entry");
        for(String currentParameter: parameterSet.keySet()){
            boolean isPageSize = currentParameter.contentEquals(PAGINATION_PAGE_SIZE);
            if(isPageSize){
                Integer pageSize = Integer.valueOf(parameterSet.get(currentParameter));
                getLogger().info(".extractSortAttribute(): Exit, pageSize --> {}", pageSize);
                return(pageSize);
            }
        }
        getLogger().info(".extractSortAttribute(): Exit, pageSize cannot be found");
        return(0);
    }

    protected Integer extractPaginationPageNumber(Map<String, String> parameterSet){
        getLogger().info(".extractPaginationPageNumber(): Entry");
        for(String currentParameter: parameterSet.keySet()) {
            boolean isPageNumber = currentParameter.contentEquals(PAGINATION_PAGE_NUMBER);
            if (isPageNumber) {
                Integer pageNumber = Integer.valueOf(parameterSet.get(currentParameter));
                getLogger().info(".extractSortAttribute(): Exit, page --> {}", pageNumber);
                return (pageNumber);
            }
        }
        getLogger().info(".extractSortAttribute(): Exit, page cannot be found");
        return(0);
    }

    protected String extractSortAttribute(Map<String, String> parameterSet){
        getLogger().info(".extractSortAttribute(): Entry");
        for(String currentParameter: parameterSet.keySet()) {
            boolean isSortingAttribute = currentParameter.contentEquals(SORT_ATTRIBUTE);
            if (isSortingAttribute) {
                String sortAttribute = parameterSet.get(currentParameter);
                getLogger().info(".extractSortAttribute(): Exit, sortAttribute --> {}", sortAttribute);
                return (sortAttribute);
            }
        }
        getLogger().info(".extractSortAttribute(): Exit, sortAttribute cannot be found");
        return(null);
    }

    protected boolean extractSortOrder(Map<String, String> parameterSet){
        getLogger().info(".extractSortOrder(): Entry");
        for(String currentParameter: parameterSet.keySet()) {
            boolean isSortingOrder = currentParameter.contentEquals(SORT_ORDER);
            if (isSortingOrder) {
                if(parameterSet.get(currentParameter).equalsIgnoreCase("Ascending")){
                    getLogger().info(".extractSortAttribute(): Exit, sortOrder --> ascending");
                    return(true);
                }
                if(parameterSet.get(currentParameter).equalsIgnoreCase("Descending")){
                    getLogger().info(".extractSortAttribute(): Exit, sortOrder --> descending");
                    return(false);
                }
            }
        }
        return(true);
    }

    protected boolean isIdentifierBasedSearch(Map<String,String> parameterSet){
        if(parameterSet == null){
            return(false);
        }
        if(parameterSet.isEmpty()){
            return(false);
        }
        for(String attributeName: parameterSet.keySet()){
            if(attributeName.startsWith("identifier")){
                return(true);
            }
            if(attributeName.startsWith("Identifier")){
                return(true);
            }
        }
        return(false);
    }

    //
    // Search via Identifier
    //

    protected DirectoryMethodOutcome doIdentifierSearch( Map<String, String> parameterSet){
        getLogger().info(".doEntrySearch(): One parameter search, assuming search by Identifier");
        String searchCriteria = parameterSet.get("identifier");
        if(searchCriteria == null){
            searchCriteria = parameterSet.get("Identifier");
        }
        getLogger().info(".doEntrySearch(): Searching for PegacornDirectoryEntry with Identifier (full detail) --> {}" , searchCriteria);
        String[] splitCriteria = searchCriteria.split("\\|");
        String identifierType = splitCriteria[0];
        IdentifierDEUseEnum identifierUse = IdentifierDEUseEnum.fromUseCode(splitCriteria[1]);
        String identifierValue = splitCriteria[2];
        getLogger().info(".doEntrySearch(): Creating an IdentifierDE with values --> Type:{}, Use:{}, Value:{}" , identifierType, identifierUse, identifierValue);
        IdentifierDE requestedIdentifier = new IdentifierDE();
        requestedIdentifier.setType(identifierType);
        requestedIdentifier.setUse(identifierUse);
        requestedIdentifier.setValue(identifierValue);
        getLogger().info(".doEntrySearch(): IdentifierDE created for query, now invoking search");
        DirectoryMethodOutcome outcome = this.getResourceBroker().searchForDirectoryResourceRoleUsingIdentifier(requestedIdentifier);
        getLogger().info(".doEntrySearch(): Exit");
        return(outcome);
    }

    //
    // General Search
    //

    protected String doFullDirectoryEntrySetRetrieval(Integer pageSize,
                                                    Integer page,
                                                    String sortAttribute,
                                                    Boolean sortAscendingOrder)
            throws DirectoryEntryInvalidSortException {
        getLogger().info(".doFullDirectoryEntrySetRetrieval(): Entry");
        getLogger().info(".doFullDirectoryEntrySetRetrieval(): Zero parameter search, so returning ALL Entries, paginated and/or sorted");
        DirectoryMethodOutcome outcome = null;
        if(sortAttribute == null ){
            getLogger().info(".doFullDirectoryEntrySetRetrieval(): sortAttribute is null, so getting unsorted entry set");
            outcome = getResourceBroker().getPaginatedUnsortedDirectoryEntrySet(pageSize, page);
        } else {
            getLogger().info(".doFullDirectoryEntrySetRetrieval(): sortAttribute is {}, so getting sorted entry set", sortAttribute);
            outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(pageSize, page, sortAttribute, sortAscendingOrder);
        }
        getLogger().info(".doFullDirectoryEntrySetRetrieval(): Zero parameter search completed, number of entries --> {}", outcome.getSearchResult().size());
        getLogger().info(".doFullDirectoryEntrySetRetrieval(): Convert result to JSON String");
        this.printOutcome(outcome);
        String result = convertToJSONString(outcome.getSearchResult());
        getLogger().info(".doFullDirectoryEntrySetRetrieval(): Exit");
        return (result);
    }


    
}
