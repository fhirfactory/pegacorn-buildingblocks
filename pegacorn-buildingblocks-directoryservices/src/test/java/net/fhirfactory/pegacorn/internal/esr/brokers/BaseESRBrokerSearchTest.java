package net.fhirfactory.pegacorn.internal.esr.brokers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSortException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;

/**
 * Base class for all seach tests.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class BaseESRBrokerSearchTest {
    
    protected abstract ESRBroker getBroker();

    
    /**
     * Performs a search.  Assert the expected count.
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
    List<ExtremelySimplifiedResource> search(SearchCriteria searchCriteria, int expectedResultCount) throws ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRPaginationException, ESRFilteringException {
        return search(searchCriteria, new Sort(), new Pagination(), expectedResultCount);
    }
    
    
    
    List<ExtremelySimplifiedResource> search(SearchCriteria searchCriteria, Sort sort, Pagination pagination, int expectedResultCount) throws ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRPaginationException, ESRFilteringException {
        ESRMethodOutcome outcome = getBroker().searchForESRsUsingAttribute(searchCriteria, null, sort, pagination);
        
        List<ExtremelySimplifiedResource> results = outcome.getSearchResult();
        
        assertEquals(expectedResultCount, results.size());
        
        return results;        
    }

}
