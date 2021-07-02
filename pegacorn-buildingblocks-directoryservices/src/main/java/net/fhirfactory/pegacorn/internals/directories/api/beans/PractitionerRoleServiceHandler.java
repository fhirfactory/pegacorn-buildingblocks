package net.fhirfactory.pegacorn.internals.directories.api.beans;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerRoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.PractitionerRoleCareTeamListESDT;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParam;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParamNames;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.PractitionerRoleFavouriteFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitionerrole.LocationFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitionerrole.RoleCategoryFilter;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSortException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceNotFoundException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceUpdateException;


@Dependent
public class PractitionerRoleServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleServiceHandler.class);

    @Inject
    private PractitionerRoleESRBroker practitionerRoleDirectoryResourceBroker;

    @Inject
    private PractitionerRoleFavouriteFilter practitionerRoleFavouriteFilter;
   
    @Inject
    private RoleCategoryFilter roleCategoryFilter;

    @Inject
    private LocationFilter locationFilter;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (practitionerRoleDirectoryResourceBroker);
    }

    //
    // Create
    //

    public void createPractitionerRole(PractitionerRoleESR entryToUpdate, Exchange camelExchange) {
        LOG.info(".update(): Entry, inputBody --> {}", entryToUpdate);
    }

    //
    // Review
    //

    //
    // Review (Search)
    //

    public List<ExtremelySimplifiedResource> practitionerRoleSearch(Exchange exchange, @Header("shortName") String shortName,
            @Header("longName") String longName, @Header("displayName") String displayName, @Header("allName") String allName,
            @Header("primaryRoleCategoryID") String primaryRoleCategoryID, @Header("primaryRoleID") String primaryRoleID,
            @Header("primaryOrganizationID") String primaryOrganizationID, @Header("primaryLocationID") String primaryLocationID,
            @Header("sortBy") String sortBy, @Header("sortOrder") String sortOrder, @Header("pageSize") String pageSize, @Header("locationFilter") String locationFilter, 
            @Header("roleCategoryFilter") String roleCategoryFilter, @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ESRPaginationException, ResourceInvalidSearchException, ESRSortingException,
            ESRFilteringException {
        
        List<BaseFilter> filters = new ArrayList<>();


        if (locationFilter != null) {
            this.locationFilter.setValue(locationFilter);
            filters.add(this.locationFilter);
        }

        if (roleCategoryFilter != null) {
            this.roleCategoryFilter.setValue(roleCategoryFilter);
            filters.add(this.roleCategoryFilter);
        }

        return practitionerRoleSearch(exchange, shortName, longName, displayName, allName, primaryRoleCategoryID, primaryRoleID, primaryOrganizationID,
                primaryLocationID, sortBy, sortOrder, pageSize, page, filters);
    }

    /**
     * Search for practitioner roles which are a favourite of the supplied
     * practitioner.
     * 
     * @param exchange
     * @param id
     * @param shortName
     * @param longName
     * @param displayName
     * @param allName
     * @param primaryRoleCategoryID
     * @param primaryRoleID
     * @param primaryOrganizationID
     * @param primaryLocationID
     * @param sortBy
     * @param sortOrder
     * @param pageSize
     * @param page
     * @return
     * @throws ResourceNotFoundException
     * @throws ResourceInvalidSortException
     * @throws ESRPaginationException
     * @throws ResourceInvalidSearchException
     * @throws ESRSortingException
     * @throws ESRFilteringException
     */
    public List<ExtremelySimplifiedResource> practitionerRoleFavouriteSearch(Exchange exchange, @Header("simplifiedID") String id,
            @Header("shortName") String shortName, @Header("longName") String longName, @Header("displayName") String displayName,
            @Header("allName") String allName, @Header("primaryRoleCategoryID") String primaryRoleCategoryID, @Header("primaryRoleID") String primaryRoleID,
            @Header("primaryOrganizationID") String primaryOrganizationID, @Header("primaryLocationID") String primaryLocationID,
            @Header("sortBy") String sortBy, @Header("sortOrder") String sortOrder, @Header("pageSize") String pageSize, @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ESRPaginationException, ResourceInvalidSearchException, ESRSortingException,
            ESRFilteringException {

        List<BaseFilter> filters = new ArrayList<>();
        practitionerRoleFavouriteFilter.setValue(id);
        filters.add(practitionerRoleFavouriteFilter);

        return practitionerRoleSearch(exchange, shortName, longName, displayName, allName, primaryRoleCategoryID, primaryRoleID, primaryOrganizationID,
                primaryLocationID, sortBy, sortOrder, pageSize, page, filters);
    }

    private List<ExtremelySimplifiedResource> practitionerRoleSearch(Exchange exchange, String shortName, String longName, String displayName, String allName,
            String primaryRoleCategoryID, String primaryRoleID, String primaryOrganizationID, String primaryLocationID, String sortBy, String sortOrder,
            String pageSize, String page, List<BaseFilter> filters) throws ResourceNotFoundException, ResourceInvalidSortException, ESRPaginationException,
            ResourceInvalidSearchException, ESRSortingException, ESRFilteringException {

        getLogger().debug(
                ".practitionerRoleSearch(): Entry, shortName->{}, longName->{}, displayName->{}"
                        + "sortBy->{}, sortOrder->{}, pageSize->{},page->{},primaryRoleCategoryID->{}"
                        + "primaryRoleID->{}, primaryOrganizationID->{}, primaryLocationID->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page, primaryRoleCategoryID, primaryRoleID, primaryOrganizationID,
                primaryLocationID);
        
        SearchParam searchParam = null;
        
        if (shortName != null) {
            searchParam = new SearchParam(SearchParamNames.SHORT_NAME, shortName);
        } else if (longName != null) {
            searchParam = new SearchParam(SearchParamNames.LONG_NAME, longName);
        } else if (displayName != null) {
            searchParam = new SearchParam(SearchParamNames.DISPLAY_NAME, displayName);
        } else if (allName != null) {
            searchParam = new SearchParam(SearchParamNames.ALL_NAME, allName);
        } else if (primaryRoleCategoryID != null) {
            searchParam = new SearchParam(SearchParamNames.PRIMARY_ROLE_CATEGORY_ID, primaryRoleCategoryID);
        } else if (primaryRoleID != null) {
            searchParam = new SearchParam(SearchParamNames.PRIMARY_ROLE_ID, primaryRoleID);
        } else if (primaryOrganizationID != null) {
            searchParam = new SearchParam(SearchParamNames.PRIMARY_ORGANISATION_ID, primaryOrganizationID);
        } else if (primaryLocationID != null) {
            searchParam = new SearchParam(SearchParamNames.PRIMARY_LOCATION_ID, primaryLocationID);
        } else {
            throw (new ResourceInvalidSearchException("Search parameter not specified"));
        }

        SearchCriteria searchCriteria = new SearchCriteria(searchParam);

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

        ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchCriteria, filters, sort, pagination);

        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

        getLogger().debug(".defaultSearch(): Exit");

        return (outcome.getSearchResult());
    }
    
    
    public List<ExtremelySimplifiedResource> practitionerRoleGetResourceList(Exchange exchange, @Header("sortBy") String sortBy, @Header("sortOrder") String sortOrder,
            @Header("pageSize") String pageSize, @Header("page") String page, @Header("locationFilter") String locationFilter, @Header("roleCategoryFilter") String roleCategoryFilter)
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
        
        List<BaseFilter> filters = new ArrayList<>();


        if (locationFilter != null) {
            this.locationFilter.setValue(locationFilter);
            filters.add(this.locationFilter);
        }

        if (roleCategoryFilter != null) {
            this.roleCategoryFilter.setValue(roleCategoryFilter);
            filters.add(this.roleCategoryFilter);
        }


        SearchCriteria searchCriteria = new SearchCriteria();

        Pagination pagination = new Pagination(pageSizeValue, pageValue);
        Sort sort = new Sort(sortBy, sortOrder);

        ESRMethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(searchCriteria, filters, pagination, sort);

        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

        getLogger().debug(".defaultGetResourceList(): Exit");

        return (outcome.getSearchResult());
}


    public List<ExtremelySimplifiedResource> practitionerRoleFavouriteResourceList(Exchange exchange, @Header("simplifiedID") String id,
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
        practitionerRoleFavouriteFilter.setValue(id);
        filters.add(practitionerRoleFavouriteFilter);

        ESRMethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(searchCriteria, filters, pagination, sort);

        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

        getLogger().debug(".defaultGetResourceList(): Exit");

        return (outcome.getSearchResult());
    }

    //
    // Update
    //

    public void updatePractitionerRole(PractitionerRoleESR entryToUpdate, Exchange camelExchange)
            throws ResourceUpdateException, ResourceInvalidSearchException {
        LOG.info(".update(): Entry, inputBody --> {}", entryToUpdate);
        PractitionerRoleESR entry = entryToUpdate;
        LOG.info(".update(): Requesting update from the Directory Resource Broker");
        ESRMethodOutcome outcome = practitionerRoleDirectoryResourceBroker.updatePractitionerRole(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if (outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)) {
            String result = convertToJSONString(outcome.getEntry());
            LOG.info(".update(): Exit, returning updated resource");
            return;
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
    }

    //
    // Delete
    //

    public void deletePractitionerRole(String id) {
        getLogger().info(".deletePractitionerRole(): Entry, id --> {}", id);
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {
        if (outcome.isSearchSuccessful()) {
            for (Integer counter = 0; counter < outcome.getSearchResult().size(); counter += 1) {
                PractitionerRoleESR currentEntry = (PractitionerRoleESR) outcome.getSearchResult().get(counter);
                getLogger().info("Info: Entry --> {} :: {}", currentEntry.getPrimaryRoleCategoryID(), currentEntry.getDisplayName());
            }
        }
    }

    /**
     * Updates the list of care teams for a practitioner role.
     * 
     * @param id
     * @param newCareTeams
     * @throws ResourceInvalidSearchException
     */
    public void updateCareTeams(@Header("simplifiedID") String id, PractitionerRoleCareTeamListESDT newCareTeams) throws ResourceInvalidSearchException {
        getLogger().info(".updateCareTeams: Entry, pathValue --> {}", id);

        practitionerRoleDirectoryResourceBroker.updateCareTeams(id, newCareTeams);

        getLogger().info(".updateCareTeams: Exit, pathValue --> {}", id);

    }

    /**
     * Returns the care teams for a practitioner role.
     * 
     * @param id
     * @return
     * @throws ResourceInvalidSearchException
     */
    public PractitionerRoleCareTeamListESDT getCareTeams(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().info(".getCareTeams: Entry, pathValue --> {}", id);

        ESRMethodOutcome outcome = practitionerRoleDirectoryResourceBroker.getResource(id.toLowerCase());

        if (outcome.getEntry() != null) {
            PractitionerRoleESR practitionerRole = (PractitionerRoleESR) outcome.getEntry();

            PractitionerRoleCareTeamListESDT careTeams = new PractitionerRoleCareTeamListESDT();
            careTeams.setCareTeams(practitionerRole.getCareTeams());

            getLogger().info(".getCareTeams: Exit, pathValue --> {}", id);

            return careTeams;
        }

        throw new ResourceInvalidSearchException("No practitioner role found for id: " + id);
    }
}
