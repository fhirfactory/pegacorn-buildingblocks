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
package net.fhirfactory.pegacorn.internals.esr.search.result;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.search.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;


public class PractitionerSearchResult extends ESRSearchResult {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerSearchResult.class);

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    public ESRSearchResult filterBy(List<BaseFilter> filters) throws ESRFilteringException {
    	
    	if (filters != null) {
        	
	    	for (BaseFilter filter : filters) {
	    		getLogger().info(".filterBy(): Entry, filter->{}", filter);
	    	}
    	}
    	
        PractitionerSearchResult result = (PractitionerSearchResult)instatiateNewESRSearchResult();
        result.setSearchResultList(getSearchResultList());
        
        if (filters == null || filters.isEmpty()) {
        	return result;
        }
                
        result.setSearchResultList(doFilter(filters));
        
        getLogger().debug(".filterBy(): Exit");
    	
        return result;
    }


    @Override
    public ESRSearchResult sortBy(Sort sort) throws ESRSortingException{
        getLogger().debug(".sortBy(): Entry, attributeName->{}, ascendingOrder->{}", sort.getSortBy(), sort.getSortOrder());
               
        PractitionerSearchResult result = (PractitionerSearchResult)instatiateNewESRSearchResult();
        result.getSearchResultList().addAll(getSearchResultList());

        switch(sort.getSortBy().toLowerCase()){
            case "simplifiedid": {
            	Collections.sort(result.getSearchResultList(), sort.isAscendingOrder() ? ExtremelySimplifiedResource.simplifiedIDComparator : Collections.reverseOrder(ExtremelySimplifiedResource.simplifiedIDComparator));
                break;
            }
            case "displayname": {
                Collections.sort(result.getSearchResultList(),  sort.isAscendingOrder() ? ExtremelySimplifiedResource.displayNameComparator : Collections.reverseOrder(ExtremelySimplifiedResource.displayNameComparator));
                break;
            }
            case "lastroleselected": {
                Collections.sort(result.getSearchResultList(),  sort.isAscendingOrder() ? PractitionerESR.lastRoleSelectionDateComparator : Collections.reverseOrder(PractitionerESR.lastRoleSelectionDateComparator));
                break;            	
            }
            default:{
                Collections.sort(result.getSearchResultList(),  sort.isAscendingOrder() ? ExtremelySimplifiedResource.simplifiedIDComparator : Collections.reverseOrder(ExtremelySimplifiedResource.simplifiedIDComparator));
                break;
            }
        }

        return(result);
    }

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult() {
        PractitionerSearchResult practitionerSearchResult = new PractitionerSearchResult();
        return (practitionerSearchResult);
    }
}