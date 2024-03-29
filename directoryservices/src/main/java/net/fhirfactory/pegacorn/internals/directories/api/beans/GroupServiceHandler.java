package net.fhirfactory.pegacorn.internals.directories.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.core.model.ui.brokers.GroupESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.GroupESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.search.exceptions.ESRPaginationException;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.search.exceptions.ESRSortingException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSortException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceNotFoundException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

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
        getLogger().debug(".update(): Entry, inputBody --> {}", inputBody);
        GroupESR entry = null;
        try{
            getLogger().debug(".update(): Attempting to parse Resource");
            JsonMapper jsonMapper = new JsonMapper();
            entry = jsonMapper.readValue(inputBody, GroupESR.class);
            getLogger().debug(".update(): Resource parsing successful");
        } catch (JsonMappingException mappingException) {
             throw(new ResourceUpdateException("Unable to parse (map) message, error --> " + mappingException.getMessage()));
        } catch (JsonProcessingException processingException) {
            throw(new ResourceUpdateException("Unable to process message, error --> " + processingException.getMessage()));
        }
        getLogger().debug(".update(): Requesting update from the Directory Resource Broker");
        ESRMethodOutcome outcome = groupDirectoryResourceBroker.updateGroup(entry);
        getLogger().debug(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            getLogger().debug(".update(): Exit, returning updated resource");
            return(result);
        }
        getLogger().debug(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }

    //
    // Group Unique Search
    //
    public List<ExtremelySimplifiedResource> groupSearch(@Header("shortName") String shortName,
                                                           @Header("longName") String longName,
                                                           @Header("displayName") String displayName,
                                                           @Header("groupType") String groupType,
                                                           @Header("groupManager") String groupManager,
                                                           @Header("sortBy") String sortBy,
                                                           @Header("sortOrder") String sortOrder,
                                                           @Header("pageSize") String pageSize,
                                                           @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException, ESRPaginationException, ESRSortingException {
        getLogger().debug(".defaultSearch(): Entry, shortName->{}, longName->{}, displayName->{}"+
                        "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page);
        String searchAttributeName = null;
        String searchAttributeValue = null;
        if(shortName != null) {
            searchAttributeValue = shortName;
            searchAttributeName = "shortName";
        } else if(longName != null){
            searchAttributeValue = longName;
            searchAttributeName = "longName";
        } else if(displayName != null){
            searchAttributeValue = displayName;
            searchAttributeName = "displayName";
        } else if(groupManager != null){
            searchAttributeValue = groupManager;
            searchAttributeName = "groupManager";
        } else if(groupType != null){
            searchAttributeValue = groupType;
            searchAttributeName = "groupType";
        } else {
            throw( new ResourceInvalidSearchException("Search parameter not specified"));
        }
        Integer pageSizeValue = null;
        Integer pageValue = null;
        Boolean sortOrderValue = true;
        if(pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if(page != null) {
            pageValue = Integer.valueOf(page);
        }
        if(sortOrder != null) {
            sortOrderValue = Boolean.valueOf(sortOrder);
        }
        ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchAttributeName, searchAttributeValue, pageSizeValue, pageValue, sortBy, sortOrderValue);
        getLogger().debug(".defaultSearch(): Exit");
        return(outcome.getSearchResult());
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {}

}
