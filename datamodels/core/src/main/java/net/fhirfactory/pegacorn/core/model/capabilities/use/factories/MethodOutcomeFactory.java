package net.fhirfactory.pegacorn.core.model.capabilities.use.factories;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.core.model.transaction.model.PegacornTransactionOutcome;
import net.fhirfactory.pegacorn.core.model.transaction.model.SimpleResourceID;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MethodOutcomeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MethodOutcomeFactory.class);

    private ObjectMapper jsonMapper;

    //
    // Constructor(s)
    //

    public MethodOutcomeFactory(){
        this.jsonMapper = new ObjectMapper();
    }

    public MethodOutcome convertToMethodOutcome(String methodOutcomeString) {
        if (StringUtils.isEmpty(methodOutcomeString)) {
            MethodOutcome outcome = new MethodOutcome();
            outcome.setCreated(false);
            return (outcome);
        }
        PegacornTransactionOutcome transactionOutcome = null;
        try {
            transactionOutcome = getJSONMapper().readValue(methodOutcomeString, PegacornTransactionOutcome.class);
        } catch (JsonProcessingException e) {
            getLogger().error(".convertToMethodOutcome(): Cannot parse MethodOutcome object! ", e);
        }
        MethodOutcome methodOutcome = null;
        if (transactionOutcome != null) {
            String resourceURL = null;
            String resourceType = "AuditEvent";
            if (transactionOutcome.isTransactionSuccessful()) {
                String resourceValue = transactionOutcome.getResourceID().getValue();
                String resourceVersion = SimpleResourceID.DEFAULT_VERSION;
                if (transactionOutcome.getResourceID() != null) {
                    if (transactionOutcome.getResourceID().getResourceType() != null) {
                        resourceType = transactionOutcome.getResourceID().getResourceType();
                    }
                    if (transactionOutcome.getResourceID().getVersion() != null) {
                        resourceVersion = transactionOutcome.getResourceID().getVersion();
                    }
                    if (transactionOutcome.getResourceID().getUrl() != null) {
                        resourceURL = transactionOutcome.getResourceID().getUrl();
                    }
                    IdType id = new IdType();
                    id.setParts(resourceURL, resourceType, resourceValue, resourceVersion);
                    methodOutcome = new MethodOutcome();
                    methodOutcome.setId(id);
                    methodOutcome.setCreated(transactionOutcome.isTransactionSuccessful());
                }
            }
        }
        if (methodOutcome == null) {
            methodOutcome = new MethodOutcome();
            methodOutcome.setCreated(false);
        }
        return (methodOutcome);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    ObjectMapper getJSONMapper() {
        return (jsonMapper);
    }
}
