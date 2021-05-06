package net.fhirfactory.pegacorn.internals.directories.api.beans;

import static org.apache.camel.builder.Builder.constant;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.model.rest.RestParamType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.PractitionerRoleListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.Pagination;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.Sort;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.resources.search.filter.practitioner.CareTeamFilter;
import net.fhirfactory.pegacorn.internals.esr.resources.search.filter.practitioner.LocationFilter;
import net.fhirfactory.pegacorn.internals.esr.resources.search.filter.practitioner.RoleCategoryFilter;
import net.fhirfactory.pegacorn.internals.esr.resources.search.filter.practitioner.RoleFilter;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSortException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceNotFoundException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceUpdateException;

@Dependent
public class PractitionerServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerServiceHandler.class);

    @Inject
    private PractitionerESRBroker practitionerDirectoryResourceBroker;

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

    public String updatePractitioner(String inputBody,  Exchange camelExchange)
            throws ResourceUpdateException, ResourceInvalidSearchException {
        LOG.info(".update(): Entry, inputBody --> {}", inputBody);
        PractitionerESR entry = null;
        try{
            LOG.info(".update(): Attempting to parse Resource");
            JsonMapper jsonMapper = new JsonMapper();
            entry = jsonMapper.readValue(inputBody, PractitionerESR.class);
            LOG.info(".update(): Resource parsing successful");
        } catch (JsonMappingException mappingException) {
            throw(new ResourceUpdateException("Unable to parse (map) message, error --> " + mappingException.getMessage()));
        } catch (JsonProcessingException processingException) {
            throw(new ResourceUpdateException("Unable to process message, error --> " + processingException.getMessage()));
        }
        LOG.info(".update(): Requesting update from the Directory Resource Broker");
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitioner(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
            LOG.info(".update(): Exit, returning updated resource");
            return(result);
        }
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
            LOG.info(".update(): Exit, returning updated resource (after creating it)");
            return(result);
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }

    public PractitionerRoleListESDT getPractitionerRoles(@Header("simplifiedID") String id, @Header("recent") String recent) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerRoles(): Entry, pathValue --> {}", id);
        
        
        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        if (outcome.getEntry() != null) {
            PractitionerRoleListESDT output = new PractitionerRoleListESDT();
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            if (StringUtils.isEmpty(recent)) {
            	output.getPractitionerRoles().addAll(practitioner.getRoleHistory().getAllCurrentRolesAsString());
            } else {
            	
            	try {
            		Integer recentValue = Integer.valueOf(recent);
            		
            		if (recentValue == 0) {
            			throw new ResourceInvalidSearchException("recent param must be greater than 0");
            		}
            	} catch(NumberFormatException e) {
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
    
    
    
	public List<ExtremelySimplifiedResource> practitionerSearch(Exchange exchange,
			@Header("simplifiedID") String simplifiedID, 
			@Header("shortName") String shortName,
			@Header("longName") String longName, 
			@Header("displayName") String displayName,
			@Header("leafValue") String leafValue, 
			@Header("sortBy") String sortBy,
			@Header("sortOrder") String sortOrder, 
			@Header("pageSize") String pageSize, 
			@Header("page") String page,
			@Header("roleFilter") String roleFilter,
			@Header("locationFilter") String locationFilter,
			@Header("careTeamFilter") String careTeamFilter,
			@Header("roleCategoryFilter") String roleCategoryFilter)
			throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException,
			ESRPaginationException, ESRSortingException, ESRFilteringException {
		getLogger().info(
				".defaultSearch(): Entry, shortName->{}, longName->{}, displayName->{},"
						+ "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
				shortName, longName, displayName, sortBy, sortOrder, pageSize, page);
		String searchAttributeName = null;
		String searchAttributeValue = null;

		
		if (simplifiedID != null) {
			searchAttributeValue = simplifiedID;
			searchAttributeName = "simplifiedID";
		} else if (shortName != null) {
			searchAttributeValue = shortName;
			searchAttributeName = "shortName";
		} else if (longName != null) {
			searchAttributeValue = longName;
			searchAttributeName = "longName";
		} else if (displayName != null) {
			searchAttributeValue = displayName;
			searchAttributeName = "displayName";
		} else if (leafValue != null) {
			searchAttributeValue = leafValue;
			searchAttributeName = "leafValue";
		} else {
			throw (new ResourceInvalidSearchException("Search parameter not specified"));
		}
		
				
		SearchCriteria searchCriteria = new SearchCriteria(searchAttributeName, searchAttributeValue);
		
		if (roleFilter != null) {
			searchCriteria.addFilter(new RoleFilter(roleFilter));
		} 
		
		if (locationFilter != null) {
			searchCriteria.addFilter(new LocationFilter(locationFilter));	
		}
		
		if (careTeamFilter != null) {
			searchCriteria.addFilter(new CareTeamFilter(careTeamFilter));		
		}
		
		if (roleCategoryFilter != null) {
			searchCriteria.addFilter(new RoleCategoryFilter(roleCategoryFilter));				
		}
		

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

		ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchCriteria, sort, pagination);

		exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
		exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

		getLogger().debug(".defaultSearch(): Exit");

		return (outcome.getSearchResult());
	}
	
	
	public List<ExtremelySimplifiedResource> practitionerGetResourceList(Exchange exchange, 
			@Header("sortBy") String sortBy,
			@Header("sortOrder") String sortOrder, 
			@Header("pageSize") String pageSize, 
			@Header("page") String page,
			@Header("roleFilter") String roleFilter,
			@Header("locationFilter") String locationFilter,
			@Header("careTeamFilter") String careTeamFilter,
			@Header("roleCategoryFilter") String roleCategoryFilter)
			throws ESRPaginationException, ResourceInvalidSortException, ResourceInvalidSearchException,
			ESRSortingException, ESRFilteringException {
		getLogger().debug(".defaultGetResourceList(): Entry, sortBy->{}, sortOrder->{}, pageSize->{}, page->{}", sortBy,
				sortOrder, pageSize, page);
		Integer pageSizeValue = null;
		Integer pageValue = null;

		if (pageSize != null) {
			pageSizeValue = Integer.valueOf(pageSize);
		}
		if (page != null) {
			pageValue = Integer.valueOf(page);
		}

		SearchCriteria searchCriteria = new SearchCriteria();
		
		if (roleFilter != null) {
			searchCriteria.addFilter(new RoleFilter(roleFilter));
		} 
		
		if (locationFilter != null) {
			searchCriteria.addFilter(new LocationFilter(locationFilter));	
		}
		
		if (careTeamFilter != null) {
			searchCriteria.addFilter(new CareTeamFilter(careTeamFilter));		
		}
		
		if (roleCategoryFilter != null) {
			searchCriteria.addFilter(new RoleCategoryFilter(roleCategoryFilter));				
		}

		Pagination pagination = new Pagination(pageSizeValue, pageValue);
		Sort sort = new Sort(sortBy, sortOrder);

		ESRMethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(searchCriteria, pagination,sort);

		exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
		exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

		getLogger().debug(".defaultGetResourceList(): Exit");

		return (outcome.getSearchResult());
	}

    public PractitionerESR updatePractitionerRoles(@Header("simplifiedID") String id, PractitionerRoleListESDT practitionerRoles) throws ResourceInvalidSearchException, ResourceUpdateException {
        getLogger().debug(".updatePractitionerRoles(): Entry, simplifiedID->{}, practitionerRoles->{}", id, practitionerRoles);
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitionerRoles(id, practitionerRoles);
        LOG.trace(".updatePractitionerRoles(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            ESRMethodOutcome updatedOutcome = getResourceBroker().getResource(id.toLowerCase());
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            LOG.debug(".updatePractitionerRoles(): Exit, returning updated resource");
            return(practitioner);
        } else {
            LOG.error(".updatePractitionerRoles(): Could not update Resource");
            throw(new ResourceUpdateException(outcome.getStatusReason()) );
        }
    }

    public FavouriteListESDT getPractitionerRoleFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerRoleFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, "PractitionerRoleFavourites");
        getLogger().debug(".getPractitionerRoleFavourites(): Exit");
        return (output);
    }
    
    
    public FavouriteListESDT getPractitionerFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, "PractitionerFavourites");
        getLogger().debug(".getPractitionerFavourites(): Exit");
        return (output);
    }

    public FavouriteListESDT getServiceFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getServiceFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, "ServiceFavourites");
        getLogger().debug(".getServiceFavourites(): Exit");
        return (output);
    }

    private FavouriteListESDT getFavourites(String id, String favouriteType) throws ResourceInvalidSearchException {
        getLogger().debug(".getFavourites(): Entry, id->{}, favouriteType->{}", id, favouriteType);
        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        if (outcome.getEntry() != null) {
            FavouriteListESDT output = new FavouriteListESDT();
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            switch(favouriteType){
                case "PractitionerRoleFavourites":{
                    output.getFavourites().addAll(practitioner.getPractitionerRoleFavourites().getFavourites());
                    break;
                }
                case "PractitionerFavourites":{
                    output.getFavourites().addAll(practitioner.getPractitionerFavourites().getFavourites());
                    break;
                }
                case "ServiceFavourites":{
                    output.getFavourites().addAll(practitioner.getHealthcareServiceFavourites().getFavourites());
                    break;
                }
                default:{
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

    public PractitionerESR updatePractitionerRoleFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerRoleFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, "PractitionerRoleFavourites", newFavourites);
        getLogger().debug(".updatePractitionerRoleFavourites(): Exit");
        return (output);
    }

    public PractitionerESR updatePractitionerFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, "PractitionerFavourites", newFavourites);
        getLogger().debug(".updatePractitionerFavourites(): Exit");
        return (output);
    }

    public PractitionerESR updateServiceFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updateServiceFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, "ServiceFavourites", newFavourites);
        getLogger().debug(".updateServiceFavourites(): Exit");
        return (output);
    }

    private PractitionerESR updateFavourites(String id, String favouriteType, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
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
