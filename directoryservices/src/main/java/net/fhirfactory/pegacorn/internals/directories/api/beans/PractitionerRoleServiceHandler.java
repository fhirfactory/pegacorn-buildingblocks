package net.fhirfactory.pegacorn.internals.directories.api.beans;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.core.model.ui.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PractitionerRoleESR;
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
        getLogger().info(".update(): Entry, inputBody --> {}", entryToUpdate);
    }

    //
    // Review
    //

    //
    // Review (Search)
    //

    public List<ExtremelySimplifiedResource> practitionerRoleSearch(@Header("shortName") String shortName,
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
            sortOrderValue = Boolean.valueOf(sortOrder);
        }
        String searchAttributeValueURLDecoded = URLDecoder.decode(searchAttributeValue, StandardCharsets.UTF_8);
        ESRMethodOutcome outcome = getResourceBroker().searchForESRsUsingAttribute(searchAttributeName, searchAttributeValueURLDecoded, pageSizeValue, pageValue, sortBy, sortOrderValue);
        getLogger().debug(".defaultSearch(): Exit");
        return(outcome.getSearchResult());
    }


    //
    // Update
    //

    public void updatePractitionerRole(PractitionerRoleESR entryToUpdate, Exchange camelExchange)
            throws ResourceUpdateException, ResourceInvalidSearchException {
        getLogger().info(".update(): Entry, inputBody --> {}", entryToUpdate);
        PractitionerRoleESR entry = entryToUpdate;
        getLogger().info(".update(): Requesting update from the Directory Resource Broker");
        ESRMethodOutcome outcome = practitionerRoleDirectoryResourceBroker.updatePractitionerRole(entry);
        getLogger().info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            getLogger().info(".update(): Exit, returning updated resource");
            return;
        }
        getLogger().info(".update(): Exit, something has gone wrong.....");
    }

    //
    // Delete
    //

    public void deletePractitionerRole(String id){
        getLogger().debug(".deletePractitionerRole(): Entry, id --> {}", id);
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {
        if(outcome.isSearchSuccessful()){
            for(Integer counter = 0; counter < outcome.getSearchResult().size(); counter += 1){
                PractitionerRoleESR currentEntry = (PractitionerRoleESR)outcome.getSearchResult().get(counter);
                getLogger().debug("Info: Entry --> {} :: {}", currentEntry.getPrimaryRoleCategoryID(), currentEntry.getDisplayName() );
            }
        }
    }
}
