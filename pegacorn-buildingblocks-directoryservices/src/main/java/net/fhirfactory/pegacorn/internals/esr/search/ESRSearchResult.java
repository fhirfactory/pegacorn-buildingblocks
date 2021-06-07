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
package net.fhirfactory.pegacorn.internals.esr.search;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;


public abstract class ESRSearchResult {
    private static Integer DEFAULT_PAGE_SIZE = 25;
    private List<ExtremelySimplifiedResource> searchResultList;
    private String statusReason;
    private boolean searchSuccessful;
    
    private int totalSearchResultCount;

    protected abstract Logger getLogger();
    public abstract ESRSearchResult filterBy(List<BaseFilter> filters) throws ESRFilteringException;
    public abstract ESRSearchResult sortBy(Sort sort) throws ESRSortingException;
    protected abstract ESRSearchResult instatiateNewESRSearchResult();

    public ESRSearchResult(){
        this.searchResultList = new ArrayList<>();
    }

    public static Integer getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public List<ExtremelySimplifiedResource> getSearchResultList() {
        return searchResultList;
    }

    public void setSearchResultList(List<ExtremelySimplifiedResource> searchResultList) {
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
    
    public int getTotalSearchResultCount() {
		return totalSearchResultCount;
	}
	public void setTotalSearchResultCount(int totalSearchResultCount) {
		this.totalSearchResultCount = totalSearchResultCount;
	}



    public ESRSearchResult paginate(Pagination pagination) throws ESRPaginationException {
        getLogger().debug(".paginate(): Entry, pageSize->{}, page->{}", pagination.getPageSize(), pagination.getPageNumber());
        ESRSearchResult result = instatiateNewESRSearchResult();
               
        result.setTotalSearchResultCount(getSearchResultList().size());
               
        if(pagination.getPageSize() == 0){
            result.setSearchResultList(getSearchResultList());
            getLogger().debug(".paginate(): Exit, pageSize is zero, so returning ALL elements");
            return(result);
        }
        if(pagination.getPageNumber() == null){
            getLogger().debug(".paginate():page is null, so defaulting to first page");
            pagination.setPageNumber(0);
        }
        
        
        if(pagination.getPageSize() > 0) {
            Integer locationOffsetStart = pagination.getPageSize() * pagination.getPageNumber();
            Integer numberOfEntries = getSearchResultList().size();
            if (numberOfEntries > locationOffsetStart) {
                for (Integer counter = 0; counter < pagination.getPageSize(); counter += 1) {
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
    
    
    public List<ExtremelySimplifiedResource>doFilter(List<BaseFilter> filters) throws ESRFilteringException {
    	 List<ExtremelySimplifiedResource>filteredList = new ArrayList<>();
         
         for (ExtremelySimplifiedResource resource : getSearchResultList()) {
         	
         	// we have the resource so now apply the filters.
         	for (BaseFilter filter : filters) {
         		boolean match = filter.doFilter(resource);
         		
         		if (match) {
         			filteredList.add(resource);
         			        			
         			break; // We only need a match on a single filter for the record to be included.  it is an OR, not an AND.
         		}
         	}
         }

         return filteredList;
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
        outcome.setTotalSearchResultCount(getTotalSearchResultCount());
        return(outcome);
    }

    @Override
    public String toString() {
        return "ESRSearchResult{" +
                "searchResultList=" + searchResultList +
                ", statusReason='" + statusReason + '\'' +
                ", searchSuccessful=" + searchSuccessful +
                '}';
    }
}
