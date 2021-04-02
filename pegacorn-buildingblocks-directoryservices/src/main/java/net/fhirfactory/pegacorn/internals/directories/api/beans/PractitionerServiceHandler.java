package net.fhirfactory.pegacorn.internals.directories.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.directories.brokers.PractitionerDirectoryResourceBroker;
import net.fhirfactory.pegacorn.internals.directories.brokers.common.ResourceDirectoryBroker;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryUpdateException;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.constant;

@Dependent
public class PractitionerServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerServiceHandler.class);

    @Inject
    private PractitionerDirectoryResourceBroker practitionerDirectoryResourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ResourceDirectoryBroker specifyResourceBroker() {
        return (practitionerDirectoryResourceBroker);
    }

    public String update(String inputBody,  Exchange camelExchange)
            throws DirectoryEntryUpdateException {
        LOG.info(".update(): Entry, inputBody --> {}", inputBody);
        PractitionerDirectoryEntry entry = null;
        try{
            LOG.info(".update(): Attempting to parse Resource");
            JsonMapper jsonMapper = new JsonMapper();
            entry = jsonMapper.readValue(inputBody, PractitionerDirectoryEntry.class);
            LOG.info(".update(): Resource parsing successful");
        } catch (JsonMappingException mappingException) {
            throw(new DirectoryEntryUpdateException("Unable to parse (map) message, error --> " + mappingException.getMessage()));
        } catch (JsonProcessingException processingException) {
            throw(new DirectoryEntryUpdateException("Unable to process message, error --> " + processingException.getMessage()));
        }
        LOG.info(".update(): Requesting update from the Directory Resource Broker");
        DirectoryMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitioner(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
            LOG.info(".update(): Exit, returning updated resource");
            return(result);
        }
        if(outcome.getStatus().equals(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
            LOG.info(".update(): Exit, returning updated resource (after creating it)");
            return(result);
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }
}
