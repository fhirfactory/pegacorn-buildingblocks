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

import net.fhirfactory.pegacorn.internals.esr.resources.GroupESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRSortingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class GroupSearchResult extends ESRSearchResult {
    private static final Logger LOG = LoggerFactory.getLogger(GroupSearchResult.class);

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    public ESRSearchResult filterBy(String attributeName, String attributeValue) throws ESRFilteringException{
        getLogger().debug(".filterBy(): Entry, attributeName->{}, attributeValue->{}", attributeName, attributeValue);
        GroupSearchResult result = (GroupSearchResult) filterBy(attributeName, attributeValue, true);
        getLogger().debug(".filterBy(): Exit");
        return(result);
    }

    @Override
    public ESRSearchResult filterBy(String attributeName, String attributeValue, boolean isInclusive) throws ESRFilteringException {
        getLogger().debug(".filterBy(): Entry, attributeName->{}, attributeValue->{}, isInclusive->{}", attributeName, attributeValue, isInclusive);
        GroupSearchResult result = (GroupSearchResult)instatiateNewESRSearchResult();

        return(result);
    }

    @Override
    public ESRSearchResult sortBy(String attributeName) throws ESRSortingException {
        getLogger().debug(".sortBy(): Entry, attributeName->{}, attributeValue->{}", attributeName);
        GroupSearchResult result = (GroupSearchResult) sortBy(attributeName, true);
        getLogger().debug(".sortBy(): Exit");
        return(result);
    }

    @Override
    public ESRSearchResult sortBy(String attributeName, boolean ascendingOrder) throws ESRSortingException {
        getLogger().debug(".sortBy(): Entry, attributeName->{}, ascendingOrder->{}", attributeName, ascendingOrder);
        if(attributeName == null){
            attributeName = "simplifiedID";
        }
        GroupSearchResult result = (GroupSearchResult)instatiateNewESRSearchResult();
        result.getSearchResultList().addAll(getSearchResultList());
        String sortByLowerCase = attributeName.toLowerCase();
        switch(sortByLowerCase){
            case "simplifiedid": {
                Collections.sort(result.getSearchResultList(), ascendingOrder ? ExtremelySimplifiedResource.simplifiedIDComparator : Collections.reverseOrder(ExtremelySimplifiedResource.simplifiedIDComparator));
                break;
            }
            case "shortname": {
                Collections.sort(result.getSearchResultList(), ascendingOrder ? ExtremelySimplifiedResource.identifierShortNameBasedComparator : Collections.reverseOrder(ExtremelySimplifiedResource.identifierShortNameBasedComparator));
                break;
            }
            case "longname": {
                Collections.sort(result.getSearchResultList(), ascendingOrder ? ExtremelySimplifiedResource.identifierLongNameTypeComparator : Collections.reverseOrder(ExtremelySimplifiedResource.identifierLongNameTypeComparator));
                break;
            }
            case "grouptype": {
                Collections.sort(result.getSearchResultList(), ascendingOrder ? GroupESR.groupTypeComparator : Collections.reverseOrder(GroupESR.groupTypeComparator));
                break;
            }
            case "groupmanager": {
                Collections.sort(result.getSearchResultList(), ascendingOrder ? GroupESR.groupManagerComparator : Collections.reverseOrder(GroupESR.groupManagerComparator));
                break;

            }
            default:{
                Collections.sort(result.getSearchResultList(), ascendingOrder ? ExtremelySimplifiedResource.simplifiedIDComparator : Collections.reverseOrder(ExtremelySimplifiedResource.simplifiedIDComparator));
                break;
            }
        }

        return(result);
    }

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult() {
        GroupSearchResult newCareTeamSearchResult = new GroupSearchResult();
        return (newCareTeamSearchResult);
    }
}
