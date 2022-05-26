package net.fhirfactory.pegacorn.internals.directories.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.core.model.internal.brokers.CommunicateRoomESRBroker;
import net.fhirfactory.pegacorn.core.model.internal.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.GroupESR;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.core.model.internal.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.core.model.internal.transactions.exceptions.ResourceUpdateException;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MatrixRoomServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomServiceHandler.class);

    @Inject
    private CommunicateRoomESRBroker matrixRoomDirectoryResourceBroker;

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
        ESRMethodOutcome outcome = matrixRoomDirectoryResourceBroker.updateDirectoryEntry(entry);
        getLogger().debug(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            getLogger().debug(".update(): Exit, returning updated resource");
            return(result);
        }
        getLogger().debug(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }
}
