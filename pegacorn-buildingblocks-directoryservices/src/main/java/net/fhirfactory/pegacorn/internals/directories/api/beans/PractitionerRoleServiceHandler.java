package net.fhirfactory.pegacorn.internals.directories.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.directories.brokers.PractitionerRoleDirectoryResourceBroker;
import net.fhirfactory.pegacorn.internals.directories.brokers.common.ResourceDirectoryBroker;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerRoleDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryUpdateException;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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

    public String update(String inputBody,  Exchange camelExchange)
            throws DirectoryEntryUpdateException {
        LOG.info(".update(): Entry, inputBody --> {}", inputBody);
        PractitionerRoleDirectoryEntry entry = null;
        try{
            LOG.info(".update(): Attempting to parse Resource");
            JsonMapper jsonMapper = new JsonMapper();
            entry = jsonMapper.readValue(inputBody, PractitionerRoleDirectoryEntry.class);
            LOG.info(".update(): Resource parsing successful");
        } catch (JsonMappingException mappingException) {
             throw(new DirectoryEntryUpdateException("Unable to parse (map) message, error --> " + mappingException.getMessage()));
        } catch (JsonProcessingException processingException) {
            throw(new DirectoryEntryUpdateException("Unable to process message, error --> " + processingException.getMessage()));
        }
        LOG.info(".update(): Requesting update from the Directory Resource Broker");
        DirectoryMethodOutcome outcome = practitionerRoleDirectoryResourceBroker.updatePractitionerRole(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            LOG.info(".update(): Exit, returning updated resource");
            return(result);
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }
}
