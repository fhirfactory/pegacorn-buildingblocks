/*
 * Copyright (c) 2021 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.services.audit.forwarder.beans;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.components.transaction.model.SimpleResourceID;
import net.fhirfactory.pegacorn.components.transaction.model.SimpleTransactionOutcome;
import net.fhirfactory.pegacorn.petasos.endpoints.CapabilityUtilisationBroker;
import net.fhirfactory.pegacorn.petasos.model.audit.PetasosAuditWriterInterface;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class AuditEventPersistenceAccessor implements PetasosAuditWriterInterface {
    private static final Logger LOG = LoggerFactory.getLogger(AuditEventPersistenceAccessor.class);

    private ObjectMapper jsonMapper;
    private IParser fhirParser;

    // ***********************************************************************************
    //
    // W A R N I N G: Tactical Solution for Short-Term Integration Support
    //
    private static String AUDIT_EVENT_PERSISTENCE_CAPABILITY_PROVIDER = "aether-hestia-audit-im";
    //
    // ***********************************************************************************

    @Inject
    private FHIRContextUtility fhirContextUtility;

    @Inject
    private CapabilityUtilisationBroker capabilityUtilisationBroker;

    @PostConstruct
    public void initialise(){
        jsonMapper = new ObjectMapper();
        fhirParser = fhirContextUtility.getJsonParser();
    }

    @Override
    public AuditEvent logAuditEventAsynchronously(AuditEvent auditEvent) {
        LOG.debug(".logAuditEventAsynchronously(): Entry, auditEvent->{}", auditEvent);
        if(auditEvent == null){
            LOG.debug(".logAuditEventAsynchronously(): Exit, auditEvent is null");
            return(null);
        }
        MethodOutcome outcome = utiliseAuditEventPersistenceCapability(auditEvent);
        IIdType id = outcome.getId();
        if(id != null){
            auditEvent.setId(id);
        }
        LOG.debug(".logAuditEventAsynchronously(): Exit, outcome->{}", outcome);
        return(auditEvent);
    }

    @Override
    public AuditEvent logAuditEventSynchronously(AuditEvent auditEvent) {
        LOG.debug(".logAuditEventSynchronously(): Entry, auditEvent->{}", auditEvent);
        if(auditEvent == null){
            LOG.debug(".logAuditEventSynchronously(): Exit, auditEvent is null");
            return(null);
        }
        MethodOutcome outcome = utiliseAuditEventPersistenceCapability(auditEvent);
        IIdType id = outcome.getId();
        if(id != null){
            auditEvent.setId(id);
        }
        LOG.debug(".logAuditEventSynchronously(): Exit, outcome->{}", outcome);
        return(auditEvent);
    }

    public  MethodOutcome utiliseAuditEventPersistenceCapability(AuditEvent auditEvent){
        LOG.debug(".utiliseAuditEventPersistenceCapability(): Entry, auditEvent --> {}", auditEvent);
        //
        // Build Write
        //
        String auditEventString = convertToJSONString(auditEvent);
        CapabilityUtilisationRequest task = new CapabilityUtilisationRequest();
        task.setRequestID(UUID.randomUUID().toString());
        task.setRequestContent(auditEventString);
        task.setRequiredCapabilityName("FHIR-AuditEvent-Persistence");
        task.setRequestDate(Instant.now());
        //
        // Do Write
        //
        CapabilityUtilisationResponse auditEventWriteOutcome = capabilityUtilisationBroker.executeTask(AUDIT_EVENT_PERSISTENCE_CAPABILITY_PROVIDER, task);
        //
        // Extract the response
        //
        String resultString = auditEventWriteOutcome.getResponseContent();
        MethodOutcome methodOutcome = convertToMethodOutcome(resultString);
        LOG.debug(".utiliseAuditEventPersistenceCapability(): Entry, methodOutcome --> {}", methodOutcome);
        return(methodOutcome);
    }

    private String convertToJSONString(AuditEvent auditEvent){
        String auditEventString = fhirParser.encodeResourceToString(auditEvent);
        return(auditEventString);
    }

    private AuditEvent convertToAuditEvent(String auditEventString){
        AuditEvent auditEvent = fhirParser.parseResource(AuditEvent.class, auditEventString);
        return(auditEvent);
    }

    private MethodOutcome convertToMethodOutcome(String methodOutcomeString){
        if(StringUtils.isEmpty(methodOutcomeString)){
            MethodOutcome outcome = new MethodOutcome();
            outcome.setCreated(false);
            return(outcome);
        }
        SimpleTransactionOutcome transactionOutcome = null;
        try {
            transactionOutcome = getJSONMapper().readValue(methodOutcomeString, SimpleTransactionOutcome.class);
        } catch (JsonProcessingException e) {
            getLogger().error(".convertToMethodOutcome(): Cannot parse MethodOutcome object! ", e);
        }
        MethodOutcome methodOutcome = null;
        if(transactionOutcome != null){
            String resourceURL = null;
            String resourceType = "AuditEvent";
            if(transactionOutcome.isTransactionSuccessful()) {
                String resourceValue = transactionOutcome.getResourceID().getValue();
                String resourceVersion = SimpleResourceID.DEFAULT_VERSION;
                if(transactionOutcome.getResourceID() != null) {
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
        if(methodOutcome == null) {
            methodOutcome = new MethodOutcome();
            methodOutcome.setCreated(false);
        }
        return(methodOutcome);
    }

    protected ObjectMapper getJSONMapper(){
        return(jsonMapper);
    }

    protected Logger getLogger(){
        return(LOG);
    }
}
