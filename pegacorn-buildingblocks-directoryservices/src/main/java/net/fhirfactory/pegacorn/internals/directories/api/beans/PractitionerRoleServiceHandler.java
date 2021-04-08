package net.fhirfactory.pegacorn.internals.directories.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.directories.brokers.PractitionerRoleDirectoryResourceBroker;
import net.fhirfactory.pegacorn.internals.directories.brokers.common.ResourceDirectoryBroker;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerRoleDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.PegId;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSortException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryNotFoundException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.spi.ApiParam;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class PractitionerRoleServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleServiceHandler.class);

    @Inject
    private PractitionerRoleDirectoryResourceBroker practitionerRoleDirectoryResourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ResourceDirectoryBroker specifyResourceBroker() {
        return (practitionerRoleDirectoryResourceBroker);
    }


    public void createPractitionerRole(PractitionerRoleDirectoryEntry entryToUpdate,  Exchange camelExchange){
        LOG.info(".update(): Entry, inputBody --> {}", entryToUpdate);
    }

    public void updatePractitionerRole(PractitionerRoleDirectoryEntry entryToUpdate,  Exchange camelExchange)
            throws DirectoryEntryUpdateException {
        LOG.info(".update(): Entry, inputBody --> {}", entryToUpdate);
        PractitionerRoleDirectoryEntry entry = entryToUpdate;
        LOG.info(".update(): Requesting update from the Directory Resource Broker");
        DirectoryMethodOutcome outcome = practitionerRoleDirectoryResourceBroker.updatePractitionerRole(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            LOG.info(".update(): Exit, returning updated resource");
            return;
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
    }

    public PractitionerRoleDirectoryEntry getPractitionerRole(@Header("id") String id)
            throws DirectoryEntryNotFoundException {
        getLogger().info(".getPractitionerRole(): Entry, id --> {}", id);
        PegId pegId = new PegId(id);
        DirectoryMethodOutcome outcome = getResourceBroker().reviewDirectoryEntry(pegId);
        if(outcome.getEntry()!=null){
            PractitionerRoleDirectoryEntry retrievedValue = (PractitionerRoleDirectoryEntry) outcome.getEntry();
            return(retrievedValue);
        } else {
            throw (new DirectoryEntryNotFoundException(id));
        }
    }

    public List<PegacornDirectoryEntry> getPractitionerRoleList(@Header("pageSize") String pageSize, @Header("page") String page)
            throws DirectoryEntryNotFoundException, DirectoryEntryInvalidSortException {
        LOG.info(".getPractitionerRoleList(): Entry, pageSize --> {}, page --> {}", pageSize, page);
        DirectoryMethodOutcome outcome = getResourceBroker().getPaginatedUnsortedDirectoryEntrySet(0,0);
        return(outcome.getSearchResult());
    }

    public void deletePractitionerRole(String id){
        getLogger().info(".deletePractitionerRole(): Entry, id --> {}", id);
    }

    @Override
    protected void printOutcome(DirectoryMethodOutcome outcome) {
        if(outcome.isSearchSuccessful()){
            for(Integer counter = 0; counter < outcome.getSearchResult().size(); counter += 1){
                PractitionerRoleDirectoryEntry currentEntry = (PractitionerRoleDirectoryEntry)outcome.getSearchResult().get(counter);
                getLogger().info("Info: Entry --> {} :: {}", currentEntry.getPrimaryRoleCategory(), currentEntry.getDisplayName() );
            }
        }
    }
}
