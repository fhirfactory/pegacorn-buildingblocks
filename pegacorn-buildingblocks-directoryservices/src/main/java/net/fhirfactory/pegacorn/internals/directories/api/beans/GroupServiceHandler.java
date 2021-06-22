package net.fhirfactory.pegacorn.internals.directories.api.beans;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSortException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceUpdateException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.group.GroupESR;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.GroupESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParam;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParamNames;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;


@Dependent
public class GroupServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(GroupServiceHandler.class);

    @Inject
    private GroupESRBroker groupDirectoryResourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (groupDirectoryResourceBroker);
    }

    //
    // Update
    //

    public String updateGroup(String inputBody,  Exchange camelExchange)
            throws ResourceUpdateException {
        LOG.info(".update(): Entry, inputBody --> {}", inputBody);
        GroupESR entry = null;
        try{
            LOG.info(".update(): Attempting to parse Resource");
            JsonMapper jsonMapper = new JsonMapper();
            entry = jsonMapper.readValue(inputBody, GroupESR.class);
            LOG.info(".update(): Resource parsing successful");
        } catch (JsonMappingException mappingException) {
             throw(new ResourceUpdateException("Unable to parse (map) message, error --> " + mappingException.getMessage()));
        } catch (JsonProcessingException processingException) {
            throw(new ResourceUpdateException("Unable to process message, error --> " + processingException.getMessage()));
        }
        LOG.info(".update(): Requesting update from the Directory Resource Broker");
        ESRMethodOutcome outcome = groupDirectoryResourceBroker.updateGroup(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            LOG.info(".update(): Exit, returning updated resource");
            return(result);
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }

    //
    // Group Unique Search
    //
    public List<ExtremelySimplifiedResource> groupSearch(Exchange exchange, 
    													   @Header("shortName") String shortName,
                                                           @Header("longName") String longName,
                                                           @Header("displayName") String displayName,
                                                           @Header("allName") String allName,
                                                           @Header("groupType") String groupType,
                                                           @Header("groupManager") String groupManager,
                                                           @Header("sortBy") String sortBy,
                                                           @Header("sortOrder") String sortOrder,
                                                           @Header("pageSize") String pageSize,
                                                           @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRPaginationException, ESRSortingException, ESRFilteringException {
        getLogger().debug(".defaultSearch(): Entry, shortName->{}, longName->{}, displayName->{}"+
                        "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page);

        SearchParam searchParam = null;
        
        if(shortName != null) {
            searchParam = new SearchParam(SearchParamNames.SHORT_NAME, shortName);
        } else if(longName != null){
            searchParam = new SearchParam(SearchParamNames.LONG_NAME, longName);
        } else if(displayName != null){
            searchParam = new SearchParam(SearchParamNames.DISPLAY_NAME, displayName);
        } else if(allName != null) {
            searchParam = new SearchParam(SearchParamNames.ALL_NAME, allName);
        } else if(groupManager != null){
            searchParam = new SearchParam(SearchParamNames.GROUP_MANAGER, groupManager);
        } else if(groupType != null){
            searchParam = new SearchParam(SearchParamNames.GROUP_TYPE, groupType);
        } else {
            throw( new ResourceInvalidSearchException("Search parameter not specified"));
        }
        
        SearchCriteria searchCriteria = new SearchCriteria(searchParam);
        
        Integer pageSizeValue = null;
        Integer pageValue = null;

        if(pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if(page != null) {
            pageValue = Integer.valueOf(page);
        }

        Pagination pagination = new Pagination(pageSizeValue, pageValue);
        Sort sort = new Sort(sortBy, sortOrder);
        
        ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchCriteria, new ArrayList<BaseFilter>(), sort, pagination);
        
        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);
        
        getLogger().debug(".defaultSearch(): Exit");
                
        return(outcome.getSearchResult());
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {}

}
