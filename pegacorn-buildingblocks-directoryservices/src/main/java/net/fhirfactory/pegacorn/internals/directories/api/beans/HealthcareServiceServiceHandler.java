package net.fhirfactory.pegacorn.internals.directories.api.beans;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSortException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceNotFoundException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.HealthcareServiceESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParamTypes;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.PractitionerHealthCareServiceFavouriteFilter;

@Dependent
public class HealthcareServiceServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(HealthcareServiceServiceHandler.class);

    @Inject
    private HealthcareServiceESRBroker resourceBroker;

    @Inject
    private PractitionerHealthCareServiceFavouriteFilter practitionerHealthCareServiceFavouriteFilter;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (resourceBroker);
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }

    public List<ExtremelySimplifiedResource> practitionerHealthCareServiceFavouriteSearch(Exchange exchange, @Header("simplifiedID") String simplifiedID,
            @Header("id") String id, @Header("shortName") String shortName, @Header("longName") String longName,
            @Header("displayName") String displayName, @Header("allName") String allName, @Header("leafValue") String leafValue,
            @Header("sortBy") String sortBy, @Header("sortOrder") String sortOrder, @Header("pageSize") String pageSize, @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRPaginationException, ESRSortingException,
            ESRFilteringException {
        getLogger().info(".defaultSearch(): Entry, shortName->{}, longName->{}, displayName->{}," + "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page);
        SearchParamTypes searchAttributeName = null;
        String searchAttributeValue = null;
        if (simplifiedID != null) {
            searchAttributeValue = simplifiedID;
            searchAttributeName = SearchParamTypes.SIMPLIFIED_ID;
        } else if (shortName != null) {
            searchAttributeValue = shortName;
            searchAttributeName = SearchParamTypes.SHORT_NAME;
        } else if (longName != null) {
            searchAttributeValue = longName;
            searchAttributeName = SearchParamTypes.LONG_NAME;
        } else if (displayName != null) {
            searchAttributeValue = displayName;
            searchAttributeName = SearchParamTypes.DISPLAY_NAME;
        } else if (leafValue != null) {
            searchAttributeValue = leafValue;
            searchAttributeName = SearchParamTypes.LEAF_VALUE;
        } else if (allName != null) {
            searchAttributeValue = allName;
            searchAttributeName = SearchParamTypes.ALL_NAME;
        } else {
            throw (new ResourceInvalidSearchException("Search parameter not specified"));
        }

        SearchCriteria searchCriteria = new SearchCriteria(searchAttributeName, searchAttributeValue);

        Integer pageSizeValue = null;
        Integer pageValue = null;

        if (pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if (page != null) {
            pageValue = Integer.valueOf(page);
        }

        Pagination pagination = new Pagination(pageSizeValue, pageValue);
        Sort sort = new Sort(sortBy, sortOrder);

        List<BaseFilter> filters = new ArrayList<>();
        practitionerHealthCareServiceFavouriteFilter.setValue(id);
        filters.add(practitionerHealthCareServiceFavouriteFilter);

        ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchCriteria, filters, sort, pagination);

        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

        getLogger().debug(".defaultSearch(): Exit");

        return (outcome.getSearchResult());
    }

    public List<ExtremelySimplifiedResource> practitionerHealthCareServiceFavouriteResourceList(Exchange exchange, @Header("simplifiedID") String id,
            @Header("sortBy") String sortBy, @Header("sortOrder") String sortOrder, @Header("pageSize") String pageSize, @Header("page") String page)
            throws ESRPaginationException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRFilteringException {
        getLogger().debug(".defaultGetResourceList(): Entry, sortBy->{}, sortOrder->{}, pageSize->{}, page->{}", sortBy, sortOrder, pageSize, page);
        Integer pageSizeValue = null;
        Integer pageValue = null;

        if (pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if (page != null) {
            pageValue = Integer.valueOf(page);
        }

        SearchCriteria searchCriteria = new SearchCriteria();

        Pagination pagination = new Pagination(pageSizeValue, pageValue);
        Sort sort = new Sort(sortBy, sortOrder);

        List<BaseFilter> filters = new ArrayList<>();
        practitionerHealthCareServiceFavouriteFilter.setValue(id);
        filters.add(practitionerHealthCareServiceFavouriteFilter);

        ESRMethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(searchCriteria, filters, pagination, sort);

        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

        getLogger().debug(".defaultGetResourceList(): Exit");

        return (outcome.getSearchResult());
    }
}
