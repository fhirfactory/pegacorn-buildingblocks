package net.fhirfactory.pegacorn.internals.directories.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
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
public class PractitionerServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerServiceHandler.class);

    @Inject
    private PractitionerESRBroker practitionerDirectoryResourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (practitionerDirectoryResourceBroker);
    }

    //
    // Update
    //

    public String updatePractitioner(String inputBody,  Exchange camelExchange)
            throws ResourceUpdateException, ResourceInvalidSearchException {
        LOG.info(".update(): Entry, inputBody --> {}", inputBody);
        PractitionerESR entry = null;
        try{
            LOG.info(".update(): Attempting to parse Resource");
            JsonMapper jsonMapper = new JsonMapper();
            entry = jsonMapper.readValue(inputBody, PractitionerESR.class);
            LOG.info(".update(): Resource parsing successful");
        } catch (JsonMappingException mappingException) {
            throw(new ResourceUpdateException("Unable to parse (map) message, error --> " + mappingException.getMessage()));
        } catch (JsonProcessingException processingException) {
            throw(new ResourceUpdateException("Unable to process message, error --> " + processingException.getMessage()));
        }
        LOG.info(".update(): Requesting update from the Directory Resource Broker");
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitioner(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
            LOG.info(".update(): Exit, returning updated resource");
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

    public PractitionerRoleListESDT getPractitionerRoles(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerRoles(): Entry, pathValue --> {}", id);
        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        if (outcome.getEntry() != null) {
            PractitionerRoleListESDT output = new PractitionerRoleListESDT();
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            output.getPractitionerRoles().addAll(practitioner.getCurrentPractitionerRoles());
            getLogger().debug(".getPractitionerRoles(): Exit, PractitionerRoles found, returning them");
            return (output);
        } else {
            getLogger().debug(".getPractitionerRoles(): Exit, No PractitionerRoles found");
            return (new PractitionerRoleListESDT());
        }
    }

    public PractitionerESR updatePractitionerRoles(@Header("simplifiedID") String id, PractitionerRoleListESDT practitionerRoles) throws ResourceInvalidSearchException, ResourceUpdateException {
        getLogger().debug(".updatePractitionerRoles(): Entry, simplifiedID->{}, practitionerRoles->{}", id, practitionerRoles);
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitionerRoles(id, practitionerRoles);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            ESRMethodOutcome updatedOutcome = getResourceBroker().getResource(id.toLowerCase());
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            LOG.info(".update(): Exit, returning updated resource");
            return(practitioner);
        } else {
            throw(new ResourceUpdateException(outcome.getStatusReason()) );
        }
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }
}
