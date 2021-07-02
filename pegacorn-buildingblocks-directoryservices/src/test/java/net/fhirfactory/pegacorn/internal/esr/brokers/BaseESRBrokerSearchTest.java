package net.fhirfactory.pegacorn.internal.esr.brokers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParam;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParamNames;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSortException;

/**
 * Base class for all search tests.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class BaseESRBrokerSearchTest {

    protected abstract ESRBroker getBroker();
    
    protected abstract String getLongNamePrefix();
    
    protected abstract String getShortNamePrefix();

    /**
     * Performs a search. Assert the expected count.
     * 
     * @param searchCriteria
     * @param expectedResultCount
     * @return
     * @throws ResourceInvalidSortException
     * @throws ResourceInvalidSearchException
     * @throws ESRSortingException
     * @throws ESRPaginationException
     * @throws ESRFilteringException
     */
    protected List<ExtremelySimplifiedResource> search(SearchCriteria searchCriteria, int expectedResultCount)
            throws ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRPaginationException, ESRFilteringException {
        return search(searchCriteria, null, new Sort(), new Pagination(), expectedResultCount);
    }
    
    protected List<ExtremelySimplifiedResource> search(SearchCriteria searchCriteria, List<BaseFilter> filters, int expectedResultCount)
            throws ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRPaginationException, ESRFilteringException {
        return search(searchCriteria, filters, new Sort(), new Pagination(), expectedResultCount);
    }
    
    protected List<ExtremelySimplifiedResource> search(SearchCriteria searchCriteria, Sort sort, Pagination pagination, int expectedResultCount)
            throws ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRPaginationException, ESRFilteringException {
        return search(searchCriteria, null, sort, pagination, expectedResultCount);
    }

    protected List<ExtremelySimplifiedResource> search(SearchCriteria searchCriteria, List<BaseFilter> filters, Sort sort, Pagination pagination, int expectedResultCount)
            throws ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRPaginationException, ESRFilteringException {
        ESRMethodOutcome outcome = getBroker().searchForESRsUsingAttribute(searchCriteria, filters, sort, pagination);

        List<ExtremelySimplifiedResource> results = outcome.getSearchResult();

        assertEquals(expectedResultCount, results.size());

        return results;
    }

    
    /**
     * A search test which matches all records. Also tests the default sort order
     */
    @Test
    public void testSearchUsingShortNameAllRecordsMatch() {

        try {
            createResources(4);

            search(new SearchCriteria(new SearchParam(SearchParamNames.SHORT_NAME, "short")), 4);
            search(new SearchCriteria(new SearchParam(SearchParamNames.LONG_NAME, "long")), 4);
            search(new SearchCriteria(new SearchParam(SearchParamNames.SHORT_NAME, "namea")), 1);
            search(new SearchCriteria(new SearchParam(SearchParamNames.LONG_NAME, "namey")), 1);
            search(new SearchCriteria(new SearchParam(SearchParamNames.SHORT_NAME, "zzzz")), 0);
            search(new SearchCriteria(new SearchParam(SearchParamNames.LONG_NAME, "xxxxx")), 0);

        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }
    
    
    
    
    /**
     * Search by all names.
     */
    @Test
    public void testAllNameSearch() {
        try {
            createResources(26);

            search(new SearchCriteria(new SearchParam(SearchParamNames.ALL_NAME, "namez")), 2); // There will be 2. One matches short name, the other long name.
        } catch (Exception e) {
            fail("Error performing search", e);
        }

    }

    
    /**
     * A search test which returns 1 page of records only. The test will return 25
     * records.
     */
    @Test
    public void testSearchUsingShortNameDefaultPaging() {

        try {
            createResources(26);

            // Match sure the default pagination returns the first 25 records only.
            search(new SearchCriteria(new SearchParam(SearchParamNames.SHORT_NAME, getShortNamePrefix())), 25);
            search(new SearchCriteria(new SearchParam(SearchParamNames.LONG_NAME, getLongNamePrefix())), 25);
        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }
    
    
    /**
     * A short name search test which returns 1 page of records using custom paging.
     */
    @Test
    public void testSearchUsingShortNameCustomPaging() {

        try {
            createResources(26);

            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search(new SearchCriteria(new SearchParam(SearchParamNames.SHORT_NAME, getShortNamePrefix())), new Sort(), new Pagination(5, 3), 5);

            assertEquals(getShortNamePrefix() + "short namep", searchResult.get(0).getDisplayName());
            assertEquals(getShortNamePrefix() + "short nameq", searchResult.get(1).getDisplayName());
            assertEquals(getShortNamePrefix() + "short namer", searchResult.get(2).getDisplayName());
            assertEquals(getShortNamePrefix() + "short names", searchResult.get(3).getDisplayName());
            assertEquals(getShortNamePrefix() + "short namet", searchResult.get(4).getDisplayName());

        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }
    
    
    /**
     * Tests default ascending sorting.
     */
    @Test
    public void testDefaultAscendingSorting() {

        try {
            createResources(26);

            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search(new SearchCriteria(new SearchParam(SearchParamNames.SHORT_NAME, getShortNamePrefix())), new Sort(), new Pagination(), 25);

            String alphabet = "abcdefghijklmnopqrstuvwxyz";

            for (int i = 0; i < 25; i++) {
                char shortNameChar = alphabet.charAt(i);
                assertEquals(getShortNamePrefix() + "short name" + shortNameChar, searchResult.get(i).getDisplayName());
            }
        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }
    
    
    /**
     * tests descending sorting.
     */
    @Test
    public void testDescendingSorting() {

        try {
            createResources(26);

            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search(new SearchCriteria(new SearchParam(SearchParamNames.SHORT_NAME, getShortNamePrefix())), new Sort("shortName", "descending"),
                    new Pagination(), 25);

            String alphabetForward = "abcdefghijklmnopqrstuvwxyz";
            String alphabetbackward = StringUtils.reverse(alphabetForward);

            for (int i = 0; i < 25; i++) {
                char shortNameChar = alphabetbackward.charAt(i);
                assertEquals(getShortNamePrefix() + "short name" + shortNameChar, searchResult.get(i).getDisplayName());
            }
        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }

    
    /**
     * Create some health care services.
     * 
     * @param number
     * @throws ResourceInvalidSearchException
     */
    protected void createResources(int number) throws ResourceInvalidSearchException {
        if (number > 26) {
            throw new RuntimeException("Can only create max 26 records");
        }

        String alphabetForward = "abcdefghijklmnopqrstuvwxyz";
        String alphabetbackward = StringUtils.reverse(alphabetForward);

        for (int i = 0; i < number; i++) {
            char shortNameChar = alphabetForward.charAt(i);
            char longtNameChar = alphabetbackward.charAt(i);

            storeResource(createResource(getShortNamePrefix() + "short name" + shortNameChar, getLongNamePrefix() + "long name" + longtNameChar, shortNameChar));
        }
    }

    
    protected abstract ExtremelySimplifiedResource createResource(String shortName, String longName, char charToAdd);
    
    protected abstract void storeResource(ExtremelySimplifiedResource resource) throws ResourceInvalidSearchException;
}
