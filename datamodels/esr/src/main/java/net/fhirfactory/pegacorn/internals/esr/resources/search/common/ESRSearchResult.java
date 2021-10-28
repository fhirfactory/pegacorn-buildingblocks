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
package net.fhirfactory.pegacorn.internals.esr.resources.search.common;

import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class ESRSearchResult {
    private static Integer DEFAULT_PAGE_SIZE = 25;
    private ArrayList<ExtremelySimplifiedResource> searchResultList;
    private String statusReason;
    private boolean searchSuccessful;

    protected abstract Logger getLogger();
    public abstract ESRSearchResult filterBy(String attributeName, String attributeValue) throws ESRFilteringException;
    public abstract ESRSearchResult filterBy(String attributeName, String attributeValue, boolean isInclusive) throws ESRFilteringException;
    public abstract ESRSearchResult sortBy(String attributeName) throws ESRSortingException;
    public abstract ESRSearchResult sortBy(String attributeName, boolean ascendingOrder) throws ESRSortingException;
    protected abstract ESRSearchResult instatiateNewESRSearchResult();

    public ESRSearchResult(){
        this.searchResultList = new ArrayList<>();
    }

    public static Integer getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public ArrayList<ExtremelySimplifiedResource> getSearchResultList() {
        return searchResultList;
    }

    public void setSearchResultList(ArrayList<ExtremelySimplifiedResource> searchResultList) {
        this.searchResultList = searchResultList;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public boolean isSearchSuccessful() {
        return searchSuccessful;
    }

    public void setSearchSuccessful(boolean searchSuccessful) {
        this.searchSuccessful = searchSuccessful;
    }

    //
    // Sorting Services
    //

    /**
     * This function reverses the order of elements within a List.
     *
     * @param esrList the List whose elements are to be reverse sorted.
     * @return success (or otherwise) of the process.
     */
    protected boolean reverseSortOrder(List<ExtremelySimplifiedResource> esrList){
        if(esrList == null){
            return(false);
        }

        if(esrList.isEmpty()){
            return(true);
        }
        List<ExtremelySimplifiedResource> originalList = new ArrayList<>();
        for(int counter = 0; counter < esrList.size(); counter += 1){
            originalList.add(counter, esrList.get(counter));
        }
        esrList.clear();
        Integer size = esrList.size();
        for(Integer counter = 0; counter < size; counter += 1){
            Integer reverseLocation = (size - 1) - counter;
            esrList.add(counter, originalList.get(reverseLocation));
        }
        return(true);
    }

    //
    // Paginate Services
    //

    public ESRSearchResult paginate(Integer page) throws ESRPaginationException{
        getLogger().debug(".paginate(): Entry, page->{}", page);
        ESRSearchResult result = paginate(getDefaultPageSize(), page);
        getLogger().debug(".paginate(): Exit");
        return(result);
    }

    public ESRSearchResult paginate(Integer pageSize, Integer page) throws ESRPaginationException {
        getLogger().debug(".paginate(): Entry, pageSize->{}, page->{}", pageSize, page);
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(pageSize == null){
            getLogger().trace(".paginate():pageSize is null, so defaulting to " + DEFAULT_PAGE_SIZE);
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if(pageSize == 0){
            result.setSearchResultList(getSearchResultList());
            getLogger().debug(".paginate(): Exit, pageSize is zero, so returning ALL elements");
            return(result);
        }
        if(page == null){
            getLogger().debug(".paginate():page is null, so defaulting to first page");
            page = 0;
        }
        if(pageSize > 0) {
            Integer locationOffsetStart = pageSize * page;
            Integer numberOfEntries = getSearchResultList().size();
            if (numberOfEntries > locationOffsetStart) {
                for (Integer counter = 0; counter < pageSize; counter += 1) {
                    Integer listLocation = locationOffsetStart + counter;
                    if (listLocation < numberOfEntries) {
                        ExtremelySimplifiedResource currentEntry = getSearchResultList().get(listLocation);
                        result.getSearchResultList().add(counter, currentEntry);
                    } else {
                        break;
                    }
                }
            }
        }
        return (result);
    }

    public ESRMethodOutcome toESRMethodOutcome(){
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        outcome.setSearch(true);
        outcome.setStatus(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
        if(this.getSearchResultList().isEmpty()){
            outcome.setSearchSuccessful(false);
        } else {
            outcome.setSearchSuccessful(true);
        }
        outcome.setSearchResult(this.getSearchResultList());
        return(outcome);
    }

    @Override
    public String toString() {
        return "ESRSearchResult{" +
                "searchResultList=" + searchResultList +
                ", statusReason=" + statusReason +
                ", searchSuccessful=" + searchSuccessful +
                '}';
    }
}
