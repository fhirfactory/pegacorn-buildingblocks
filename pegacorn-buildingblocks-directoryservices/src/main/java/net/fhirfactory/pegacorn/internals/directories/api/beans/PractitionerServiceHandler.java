package net.fhirfactory.pegacorn.internals.directories.api.beans;

import static org.apache.camel.builder.Builder.constant;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSortException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceNotFoundException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceUpdateException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerRoleListESDT;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.search.FavouriteTypes;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParam;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParamNames;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.CareTeamFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.LocationFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.PractitionerOtherPractitionerFavouriteFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.RoleCategoryFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.RoleFilter;

@Dependent
public class PractitionerServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerServiceHandler.class);

    @Inject
    private PractitionerESRBroker practitionerDirectoryResourceBroker;

    @Inject
    private RoleFilter roleFilter;

    @Inject
    private RoleCategoryFilter roleCategoryFilter;

    @Inject
    private CareTeamFilter careTeamFilter;

    @Inject
    private LocationFilter locationFilter;

    @Inject
    private PractitionerOtherPractitionerFavouriteFilter practitionerOtherPractitionerFavouriteFilter;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (practitionerDirectoryResourceBroker);
    }

    //
    // Update
    //

    public String updatePractitioner(PractitionerESR entry, Exchange camelExchange) throws ResourceUpdateException, ResourceInvalidSearchException {

        LOG.info(".update(): Requesting update from the Directory Resource Broker");

        // Make sure the simplified id matches the email address.
        if (!entry.getSimplifiedID().equals(entry.getIdentifierWithType("EmailAddress").getValue())) {
            throw new ResourceUpdateException("The simplified id must match the email address identifier for a practitioner update");
        }

        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitioner(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if (outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)) {
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
            LOG.info(".update(): Exit, returning updated resource");
            return (result);
        }
        if (outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)) {
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
            LOG.info(".update(): Exit, returning updated resource (after creating it)");
            return (result);
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
        return ("Hmmm... not good!");
    }

    public PractitionerRoleListESDT getPractitionerRoles(@Header("simplifiedID") String id, @Header("recent") String recent)
            throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerRoles(): Entry, pathValue --> {}", id);

        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        if (outcome.getEntry() != null) {
            PractitionerRoleListESDT output = new PractitionerRoleListESDT();
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            if (StringUtils.isEmpty(recent)) {
                output.getPractitionerRoles().addAll(practitioner.getRoleHistory().getAllCurrentPractitionerRolesSet());
            } else {

                try {
                    Integer recentValue = Integer.valueOf(recent);

                    if (recentValue == 0) {
                        throw new ResourceInvalidSearchException("recent param must be greater than 0");
                    }
                } catch (NumberFormatException e) {
                    throw new ResourceInvalidSearchException("recent param must be a number");
                }

                output.getPractitionerRoles().addAll(practitioner.getRoleHistory().getPreviousRolesAsString(Integer.valueOf(recent), true));
            }
            getLogger().debug(".getPractitionerRoles(): Exit, PractitionerRoles found, returning them");
            return (output);
        } else {
            getLogger().debug(".getPractitionerRoles(): Exit, No PractitionerRoles found");
            return (new PractitionerRoleListESDT());
        }
    }

    public List<ExtremelySimplifiedResource> practitionerSearch(Exchange exchange, @Header("simplifiedID") String simplifiedID,
            @Header("shortName") String shortName, @Header("longName") String longName, @Header("displayName") String displayName,
            @Header("allName") String allName, @Header("leafValue") String leafValue, @Header("sortBy") String sortBy, @Header("sortOrder") String sortOrder,
            @Header("pageSize") String pageSize, @Header("page") String page, @Header("roleFilter") String roleFilter,
            @Header("locationFilter") String locationFilter, @Header("careTeamFilter") String careTeamFilter,
            @Header("roleCategoryFilter") String roleCategoryFilter) throws ResourceNotFoundException, ResourceInvalidSortException,
            ResourceInvalidSearchException, ESRPaginationException, ESRSortingException, ESRFilteringException {
        getLogger().info(".practitionerSearch(): Entry, shortName->{}, longName->{}, displayName->{}," + "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page);
      
        List<BaseFilter> filters = new ArrayList<>();

        if (roleFilter != null) {
            this.roleFilter.setValue(roleFilter);
            filters.add(this.roleFilter);
        }

        if (locationFilter != null) {
            this.locationFilter.setValue(locationFilter);
            filters.add(this.locationFilter);
        }

        if (careTeamFilter != null) {
            this.careTeamFilter.setValue(careTeamFilter);
            filters.add(this.careTeamFilter);
        }

        if (roleCategoryFilter != null) {
            this.roleCategoryFilter.setValue(roleCategoryFilter);
            filters.add(this.roleCategoryFilter);
        }
        
        return practitionerSearch(exchange, simplifiedID, shortName, longName, displayName, allName, leafValue, sortBy, sortOrder, pageSize, page, filters);
    }

    public List<ExtremelySimplifiedResource> practitionerFavouriteSearch(Exchange exchange, @Header("id") String id,
            @Header("simplifiedID") String simplifiedID, @Header("shortName") String shortName, @Header("longName") String longName,
            @Header("displayName") String displayName, @Header("allName") String allName, @Header("leafValue") String leafValue,
            @Header("sortBy") String sortBy, @Header("sortOrder") String sortOrder, @Header("pageSize") String pageSize, @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRPaginationException, ESRSortingException,
            ESRFilteringException {
        getLogger().info(".practitionerFavouriteSearch(): Entry, shortName->{}, longName->{}, displayName->{}," + "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page);

        List<BaseFilter> filters = new ArrayList<>();
        practitionerOtherPractitionerFavouriteFilter.setValue(id);
        filters.add(practitionerOtherPractitionerFavouriteFilter);

        getLogger().debug(".practitionerFavouriteSearch(): Exit");

        return practitionerSearch(exchange, simplifiedID, shortName, longName, displayName, allName, leafValue, sortBy, sortOrder, pageSize, page, filters);
    }
    
    
    private List<ExtremelySimplifiedResource> practitionerSearch(Exchange exchange, String simplifiedID, String shortName, String longName, String displayName,
            String allName, String leafValue, String sortBy,  String sortOrder,
             String pageSize,  String page,  List<BaseFilter> filters) throws ResourceNotFoundException, ResourceInvalidSortException,
            ResourceInvalidSearchException, ESRPaginationException, ESRSortingException, ESRFilteringException {
        getLogger().info(".practitionerSearch(): Entry, shortName->{}, longName->{}, displayName->{}," + "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page);

        SearchParam searchParam = null;

        if (simplifiedID != null) {
            searchParam = new SearchParam(SearchParamNames.SIMPLIFIED_ID, simplifiedID);
        } else if (shortName != null) {
            searchParam = new SearchParam(SearchParamNames.SHORT_NAME, shortName);
        } else if (longName != null) {
            searchParam = new SearchParam(SearchParamNames.LONG_NAME, longName);
        } else if (displayName != null) {
            searchParam = new SearchParam(SearchParamNames.DISPLAY_NAME, displayName);
        } else if (allName != null) {
            searchParam = new SearchParam(SearchParamNames.ALL_NAME, allName);
        } else if (leafValue != null) {
            searchParam = new SearchParam(SearchParamNames.LEAF_VALUE, leafValue);
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

    public List<ExtremelySimplifiedResource> practitionerGetResourceList(Exchange exchange, @Header("sortBy") String sortBy,
            @Header("sortOrder") String sortOrder, @Header("pageSize") String pageSize, @Header("page") String page, @Header("roleFilter") String roleFilter,
            @Header("locationFilter") String locationFilter, @Header("careTeamFilter") String careTeamFilter,
            @Header("roleCategoryFilter") String roleCategoryFilter)
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

        List<BaseFilter> filters = new ArrayList<>();

        if (roleFilter != null) {
            this.roleFilter.setValue(roleFilter);
            filters.add(this.roleFilter);
        }

        if (locationFilter != null) {
            this.locationFilter.setValue(locationFilter);
            filters.add(this.locationFilter);
        }

        if (careTeamFilter != null) {
            this.careTeamFilter.setValue(careTeamFilter);
            filters.add(this.careTeamFilter);
        }

        if (roleCategoryFilter != null) {
            this.roleCategoryFilter.setValue(roleCategoryFilter);
            filters.add(this.roleCategoryFilter);
        }

        Pagination pagination = new Pagination(pageSizeValue, pageValue);
        Sort sort = new Sort(sortBy, sortOrder);

        ESRMethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(searchCriteria, filters, pagination, sort);

        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

        getLogger().debug(".defaultGetResourceList(): Exit");

        return (outcome.getSearchResult());
    }

    public List<ExtremelySimplifiedResource> practitionerFavouriteResourceList(Exchange exchange, @Header("simplifiedID") String id,
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

        List<BaseFilter> filters = new ArrayList<>();
        practitionerOtherPractitionerFavouriteFilter.setValue(id);
        filters.add(practitionerOtherPractitionerFavouriteFilter);

        Pagination pagination = new Pagination(pageSizeValue, pageValue);
        Sort sort = new Sort(sortBy, sortOrder);

        ESRMethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(searchCriteria, filters, pagination, sort);

        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

        getLogger().debug(".defaultGetResourceList(): Exit");

        return (outcome.getSearchResult());
    }

    public PractitionerESR updatePractitionerRoles(@Header("simplifiedID") String id, PractitionerRoleListESDT practitionerRoles)
            throws ResourceInvalidSearchException, ResourceUpdateException {
        getLogger().debug(".updatePractitionerRoles(): Entry, simplifiedID->{}, practitionerRoles->{}", id, practitionerRoles);
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitionerRoles(id, practitionerRoles);
        LOG.trace(".updatePractitionerRoles(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if (outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)) {
            ESRMethodOutcome updatedOutcome = getResourceBroker().getResource(id.toLowerCase());
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            LOG.debug(".updatePractitionerRoles(): Exit, returning updated resource");
            return (practitioner);
        } else {
            LOG.error(".updatePractitionerRoles(): Could not update Resource");
            throw (new ResourceUpdateException(outcome.getStatusReason()));
        }
    }

    public FavouriteListESDT getPractitionerRoleFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerRoleFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, FavouriteTypes.PRACTITIONER_ROLE_FAVOURITES);
        getLogger().debug(".getPractitionerRoleFavourites(): Exit");
        return (output);
    }

    public FavouriteListESDT getPractitionerFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, FavouriteTypes.PRACTITIONER_FAVOURITES);
        getLogger().debug(".getPractitionerFavourites(): Exit");
        return (output);
    }

    public FavouriteListESDT getServiceFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getServiceFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, FavouriteTypes.SERVICE_FAVOURITES);
        getLogger().debug(".getServiceFavourites(): Exit");
        return (output);
    }

    private FavouriteListESDT getFavourites(String id, FavouriteTypes favouriteType) throws ResourceInvalidSearchException {
        getLogger().debug(".getFavourites(): Entry, id->{}, favouriteType->{}", id, favouriteType);
        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        if (outcome.getEntry() != null) {
            FavouriteListESDT output = new FavouriteListESDT();
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            switch (favouriteType) {
            case PRACTITIONER_ROLE_FAVOURITES: {
                output.getFavourites().addAll(practitioner.getPractitionerRoleFavourites().getFavourites());
                break;
            }
            case PRACTITIONER_FAVOURITES: {
                output.getFavourites().addAll(practitioner.getPractitionerFavourites().getFavourites());
                break;
            }
            case SERVICE_FAVOURITES: {
                output.getFavourites().addAll(practitioner.getHealthcareServiceFavourites().getFavourites());
                break;
            }
            default: {
                // do nothing (and return an empty set)
            }
            }
            getLogger().debug(".getFavourites(): Exit, Favourites found, returning them");
            return (output);
        } else {
            getLogger().debug(".getFavourites(): Exit, No Favourites found");
            return (new FavouriteListESDT());
        }
    }

    public PractitionerESR updatePractitionerRoleFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites)
            throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerRoleFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, FavouriteTypes.PRACTITIONER_ROLE_FAVOURITES, newFavourites);
        getLogger().debug(".updatePractitionerRoleFavourites(): Exit");
        return (output);
    }

    public PractitionerESR updatePractitionerFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites)
            throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, FavouriteTypes.PRACTITIONER_FAVOURITES, newFavourites);
        getLogger().debug(".updatePractitionerFavourites(): Exit");
        return (output);
    }

    public PractitionerESR updateServiceFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updateServiceFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, FavouriteTypes.SERVICE_FAVOURITES, newFavourites);
        getLogger().debug(".updateServiceFavourites(): Exit");
        return (output);
    }

    private PractitionerESR updateFavourites(String id, FavouriteTypes favouriteType, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".getFavourites(): Entry, id->{}, favouriteType->{}", id, favouriteType);
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updateFavourites(id, favouriteType, newFavourites);
        if (outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)) {
            getLogger().debug(".getFavourites(): Exit, Favourites found, returning them");
            PractitionerESR updatedPractitioner = (PractitionerESR) getResource(id).getEntry();
            return (updatedPractitioner);
        } else {
            getLogger().debug(".getFavourites(): Exit, No Favourites found");
            return (null);
        }
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }
}
