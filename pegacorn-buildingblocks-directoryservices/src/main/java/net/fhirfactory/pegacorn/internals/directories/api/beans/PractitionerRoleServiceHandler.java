package net.fhirfactory.pegacorn.internals.directories.api.beans;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerRoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRPaginationException;
import net.fhirfactory.pegacorn.internals.esr.resources.search.exceptions.ESRSortingException;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSortException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceNotFoundException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Dependent
public class PractitionerRoleServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleServiceHandler.class);

    @Inject
    private PractitionerRoleESRBroker practitionerRoleDirectoryResourceBroker;

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
                                                                    @Header("primaryRoleCategoryID") String primaryRoleCategoryID,
                                                                    @Header("primaryRoleID") String primaryRoleID,
                                                                    @Header("primaryOrganizationID") String primaryOrganizationID,
                                                                    @Header("primaryLocationID") String primaryLocationID,
                                                                    @Header("sortBy") String sortBy,
                                                                    @Header("sortOrder") String sortOrder,
                                                                    @Header("pageSize") String pageSize,
                                                                    @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ESRPaginationException, ResourceInvalidSearchException, ESRSortingException {
        getLogger().debug(".defaultSearch(): Entry, shortName->{}, longName->{}, displayName->{}"
                        + "sortBy->{}, sortOrder->{}, pageSize->{},page->{},primaryRoleCategoryID->{}"
                        + "primaryRoleID->{}, primaryOrganizationID->{}, primaryLocationID->{}",
                shortName, longName, displayName, sortBy, sortOrder, pageSize, page, primaryRoleCategoryID, primaryRoleID, primaryOrganizationID, primaryLocationID);
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
        } else if(primaryRoleCategoryID != null){
            searchAttributeValue = primaryRoleCategoryID;
            searchAttributeName = "primaryRoleCategoryID";
        } else if(primaryRoleID != null){
            searchAttributeValue = primaryRoleID;
            searchAttributeName = "primaryRoleID";
        } else if(primaryOrganizationID != null){
            searchAttributeValue = primaryOrganizationID;
            searchAttributeName = "primaryOrganizationID";
        } else if(primaryLocationID != null){
            searchAttributeValue = primaryLocationID;
            searchAttributeName = "primaryLocationID";
        }
        else {
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
        	if (sortOrder.equals(SORT_ORDER_ASCENDING)) {
        		sortOrderValue = true;
        	} else if (sortOrder.equals(SORT_ORDER_DESCENDING)) {
        		sortOrderValue = false;
        	}
        }
        String searchAttributeValueURLDecoded = URLDecoder.decode(searchAttributeValue, StandardCharsets.UTF_8);
        ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchAttributeName, searchAttributeValueURLDecoded, pageSizeValue, pageValue, sortBy, sortOrderValue);
        
        exchange.getMessage().setHeader(TOTAL_RECORD_COUNT_HEADER, outcome.getTotalSearchResultCount());
        exchange.getMessage().setHeader(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, TOTAL_RECORD_COUNT_HEADER);
        
        getLogger().debug(".defaultSearch(): Exit");
               
        return(outcome.getSearchResult());
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
}
