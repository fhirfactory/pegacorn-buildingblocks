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
package net.fhirfactory.pegacorn.internals.esr.resources.search;

import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRSortingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class CareTeamSearchResult extends ESRSearchResult {
    private static final Logger LOG = LoggerFactory.getLogger(CareTeamSearchResult.class);

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    public ESRSearchResult filterBy(String attributeName, String attributeValue) throws ESRFilteringException{
        getLogger().debug(".filterBy(): Entry, attributeName->{}, attributeValue->{}", attributeName, attributeValue);
        CareTeamSearchResult result = (CareTeamSearchResult) filterBy(attributeName, attributeValue, true);
        getLogger().debug(".filterBy(): Exit");
        return(result);
    }

    @Override
    public ESRSearchResult filterBy(String attributeName, String attributeValue, boolean isInclusive) throws ESRFilteringException {
        getLogger().debug(".filterBy(): Entry, attributeName->{}, attributeValue->{}, isInclusive->{}", attributeName, attributeValue, isInclusive);
        CareTeamSearchResult result = (CareTeamSearchResult)instatiateNewESRSearchResult();

        return(result);
    }

    @Override
    public ESRSearchResult sortBy(String attributeName) throws ESRSortingException {
        getLogger().debug(".sortBy(): Entry, attributeName->{}, attributeValue->{}", attributeName);
        CareTeamSearchResult result = (CareTeamSearchResult) sortBy(attributeName, true);
        getLogger().debug(".sortBy(): Exit");
        return(result);
    }

    @Override
    public ESRSearchResult sortBy(String attributeName, boolean ascendingOrder) throws ESRSortingException {
        getLogger().debug(".sortBy(): Entry, attributeName->{}, ascendingOrder->{}", attributeName, ascendingOrder);
        if(attributeName == null){
            attributeName = "simplifiedID";
        }
        CareTeamSearchResult result = (CareTeamSearchResult)instatiateNewESRSearchResult();
        result.getSearchResultList().addAll(getSearchResultList());
        String sortByLowerCase = attributeName.toLowerCase();
        switch(sortByLowerCase){
            case "simplifiedid": {
                Collections.sort(result.getSearchResultList(), ExtremelySimplifiedResource.simplifiedIDComparator);
                break;
            }
            case "shortname": {
                Collections.sort(result.getSearchResultList(), ExtremelySimplifiedResource.identifierShortNameBasedComparator);
                break;
            }
            case "longname": {
                Collections.sort(result.getSearchResultList(), ExtremelySimplifiedResource.identifierLongNameTypeComparator);
                break;
            }
            case "displayname": {
                Collections.sort(result.getSearchResultList(), ExtremelySimplifiedResource.displayNameComparator);
                break;
            }
            default:{
                Collections.sort(result.getSearchResultList(), ExtremelySimplifiedResource.simplifiedIDComparator);
            }
        }
        if(!ascendingOrder){
            reverseSortOrder(result.getSearchResultList());
        }
        return(result);
    }

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult() {
        CareTeamSearchResult newCareTeamSearchResult = new CareTeamSearchResult();
        return (newCareTeamSearchResult);
    }
}
