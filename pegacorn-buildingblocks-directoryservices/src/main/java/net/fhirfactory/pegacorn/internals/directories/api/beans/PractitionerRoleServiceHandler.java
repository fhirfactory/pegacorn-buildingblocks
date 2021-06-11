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
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceUpdateException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerRoleCareTeamListESDT;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.PractitionerRoleFavouriteFilter;

@Dependent
public class PractitionerRoleServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleServiceHandler.class);

    @Inject
    private PractitionerRoleESRBroker practitionerRoleDirectoryResourceBroker;
    
    @Inject
    private PractitionerRoleFavouriteFilter practitionerRoleFavouriteFilter;
    
    @Inject
    private PractitionerESRBroker practitionerBroker;

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

    public void createPractitionerRole(PractitionerRoleESR entryToUpdate, Exchange camelExchange){
        LOG.info(".update(): Entry, inputBody --> {}", entryToUpdate);
    }

    //
    // Review
    //

    //
    // Review (Search)
    //

    public List<ExtremelySimplifiedResource> practitionerRoleSearch(Exchange exchange,
    															    @Header("shortName") String shortName,
                                                                    @Header("longName") String longName,
                                                                    @Header("displayName") String displayName,
                                                                    @Header("allName") String allName,
                                                                    @Header("primaryRoleCategoryID") String primaryRoleCategoryID,
                                                                    @Header("primaryRoleID") String primaryRoleID,
                                                                    @Header("primaryOrganizationID") String primaryOrganizationID,
                                                                    @Header("primaryLocationID") String primaryLocationID,
                                                                    @Header("sortBy") String sortBy,
                                                                    @Header("sortOrder") String sortOrder,
                                                                    @Header("pageSize") String pageSize,
                                                                    @Header("page") String page) throws ResourceNotFoundException, ResourceInvalidSortException, ESRPaginationException, ResourceInvalidSearchException, ESRSortingException, ESRFilteringException {
    	
    	return practitionerRoleSearch(exchange, shortName, longName, displayName, allName, primaryRoleCategoryID, primaryRoleID,primaryOrganizationID, primaryLocationID, sortBy,sortOrder,pageSize, page, new ArrayList<>());
    }

    
    
    /**
     * Search for practitioner roles which are a favourite of the supplied practitioner.
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
    public List<ExtremelySimplifiedResource> practitionerRoleFavouriteSearch(Exchange exchange,
																			@Header("simplifiedID") String id,
																		    @Header("shortName") String shortName,
																		    @Header("longName") String longName,
																		    @Header("displayName") String displayName,
																		    @Header("allName") String allName,
																		    @Header("primaryRoleCategoryID") String primaryRoleCategoryID,
																		    @Header("primaryRoleID") String primaryRoleID,
																		    @Header("primaryOrganizationID") String primaryOrganizationID,
																		    @Header("primaryLocationID") String primaryLocationID,
																		    @Header("sortBy") String sortBy,
																		    @Header("sortOrder") String sortOrder,
																		    @Header("pageSize") String pageSize,
																		    @Header("page") String page) throws ResourceNotFoundException, ResourceInvalidSortException, ESRPaginationException, ResourceInvalidSearchException, ESRSortingException, ESRFilteringException {

    	List<BaseFilter>filters = new ArrayList<>();
    	practitionerRoleFavouriteFilter.setValue(id);
    	filters.add(practitionerRoleFavouriteFilter);
    	
    	return practitionerRoleSearch(exchange, shortName, longName, displayName, allName, primaryRoleCategoryID, primaryRoleID,primaryOrganizationID, primaryLocationID, sortBy,sortOrder,pageSize, page, filters);
    }

    
    
    private List<ExtremelySimplifiedResource> practitionerRoleSearch(Exchange exchange,
			@Header("shortName") String shortName, @Header("longName") String longName,
			@Header("displayName") String displayName, @Header("allName") String allName,
			@Header("primaryRoleCategoryID") String primaryRoleCategoryID,
			@Header("primaryRoleID") String primaryRoleID,
			@Header("primaryOrganizationID") String primaryOrganizationID,
			@Header("primaryLocationID") String primaryLocationID, @Header("sortBy") String sortBy,
			@Header("sortOrder") String sortOrder, @Header("pageSize") String pageSize, @Header("page") String page,
			List<BaseFilter> filters) throws ResourceNotFoundException, ResourceInvalidSortException,
			ESRPaginationException, ResourceInvalidSearchException, ESRSortingException, ESRFilteringException {
		
		getLogger().debug(
				".defaultSearch(): Entry, shortName->{}, longName->{}, displayName->{}"
						+ "sortBy->{}, sortOrder->{}, pageSize->{},page->{},primaryRoleCategoryID->{}"
						+ "primaryRoleID->{}, primaryOrganizationID->{}, primaryLocationID->{}",
				shortName, longName, displayName, sortBy, sortOrder, pageSize, page, primaryRoleCategoryID,
				primaryRoleID, primaryOrganizationID, primaryLocationID);
		String searchAttributeName = null;
		String searchAttributeValue = null;
		if (shortName != null) {
			searchAttributeValue = shortName;
			searchAttributeName = "shortName";
		} else if (longName != null) {
			searchAttributeValue = longName;
			searchAttributeName = "longName";
		} else if (displayName != null) {
			searchAttributeValue = displayName;
			searchAttributeName = "displayName";
		} else if (allName != null) {
			searchAttributeValue = allName;
			searchAttributeName = "allName";
		} else if (primaryRoleCategoryID != null) {
			searchAttributeValue = primaryRoleCategoryID;
			searchAttributeName = "primaryRoleCategoryID";
		} else if (primaryRoleID != null) {
			searchAttributeValue = primaryRoleID;
			searchAttributeName = "primaryRoleID";
		} else if (primaryOrganizationID != null) {
			searchAttributeValue = primaryOrganizationID;
			searchAttributeName = "primaryOrganizationID";
		} else if (primaryLocationID != null) {
			searchAttributeValue = primaryLocationID;
			searchAttributeName = "primaryLocationID";
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

		ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchCriteria,filters, sort, pagination);

		exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
		exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);

		getLogger().debug(".defaultSearch(): Exit");

		return (outcome.getSearchResult());
	}
    
    
    
    
	public List<ExtremelySimplifiedResource> practitionerRoleFavouriteResourceList(Exchange exchange, 
																					@Header("simplifiedID") String id, 
																					@Header("sortBy") String sortBy,
																					@Header("sortOrder") String sortOrder, 
																					@Header("pageSize") String pageSize, 
																					@Header("page") String page)
																					throws ESRPaginationException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRSortingException, ESRFilteringException {
		getLogger().debug(".defaultGetResourceList(): Entry, sortBy->{}, sortOrder->{}, pageSize->{}, page->{}", sortBy,sortOrder, pageSize, page);
		
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
		
    	List<BaseFilter>filters = new ArrayList<>();
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
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            LOG.info(".update(): Exit, returning updated resource");
            return;
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
    }

    //
    // Delete
    //

    public void deletePractitionerRole(String id){
        getLogger().info(".deletePractitionerRole(): Entry, id --> {}", id);
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {
        if(outcome.isSearchSuccessful()){
            for(Integer counter = 0; counter < outcome.getSearchResult().size(); counter += 1){
                PractitionerRoleESR currentEntry = (PractitionerRoleESR)outcome.getSearchResult().get(counter);
                getLogger().info("Info: Entry --> {} :: {}", currentEntry.getPrimaryRoleCategoryID(), currentEntry.getDisplayName() );
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
        	PractitionerRoleESR practitionerRole = (PractitionerRoleESR)outcome.getEntry();
        	
        	PractitionerRoleCareTeamListESDT careTeams = new PractitionerRoleCareTeamListESDT();
        	careTeams.setCareTeams( practitionerRole.getCareTeams());
        	        	
            getLogger().info(".getCareTeams: Exit, pathValue --> {}", id);
        	
        	return careTeams;
        }
       
        throw new ResourceInvalidSearchException("No practitioner role found for id: " + id);
    }
}
