package net.fhirfactory.pegacorn.internals.directories.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.PatientESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.PatientESR;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.PractitionerRoleListESDT;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.constant;

@Dependent
public class PatientServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PatientServiceHandler.class);

    @Inject
    private PatientESRBroker patientDirectoryResourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (patientDirectoryResourceBroker);
    }

    //
    // Update
    //

    public String updatePatient(String inputBody,  Exchange camelExchange)
            throws ResourceUpdateException, ResourceInvalidSearchException {
        LOG.info(".updatePatient(): Entry, inputBody --> {}", inputBody);
        PatientESR entry = null;
        try{
            LOG.info(".updatePatient(): Attempting to parse Resource");
            JsonMapper jsonMapper = new JsonMapper();
            entry = jsonMapper.readValue(inputBody, PatientESR.class);
            LOG.info(".updatePatient(): Resource parsing successful");
        } catch (JsonMappingException mappingException) {
            throw(new ResourceUpdateException("Unable to parse (map) message, error --> " + mappingException.getMessage()));
        } catch (JsonProcessingException processingException) {
            throw(new ResourceUpdateException("Unable to process message, error --> " + processingException.getMessage()));
        }
        LOG.info(".updatePatient(): Requesting update from the Directory Resource Broker");
        ESRMethodOutcome outcome = patientDirectoryResourceBroker.updatePatient(entry);
        LOG.info(".updatePatient(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
            LOG.info(".updatePatient(): Exit, returning updated resource");
            return(result);
        }
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
            LOG.info(".update(): Exit, returning updated resource (after creating it)");
            return(result);
        }
        LOG.info(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }
}
