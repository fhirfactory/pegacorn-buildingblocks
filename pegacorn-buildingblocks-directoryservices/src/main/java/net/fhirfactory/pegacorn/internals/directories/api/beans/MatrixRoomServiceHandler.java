package net.fhirfactory.pegacorn.internals.directories.api.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.MatrixRoomESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.group.GroupESR;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceUpdateException;

@Dependent
public class MatrixRoomServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomServiceHandler.class);

    @Inject
    private MatrixRoomESRBroker matrixRoomDirectoryResourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (matrixRoomDirectoryResourceBroker);
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {}

    public String update(String inputBody,  Exchange camelExchange)
            throws ResourceUpdateException, ResourceInvalidSearchException {
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
        ESRMethodOutcome outcome = matrixRoomDirectoryResourceBroker.updateDirectoryEntry(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            LOG.info(".update(): Exit, returning updated resource");
            return(result);
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }
}
