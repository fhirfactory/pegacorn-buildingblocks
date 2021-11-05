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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.base.factories.MethodOutcomeFactory;
import net.fhirfactory.pegacorn.petasos.endpoints.CapabilityUtilisationBroker;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class AuditEventPersistenceAccessor {
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

    @Inject
    private MethodOutcomeFactory outcomeFactory;

    //
    // Constructor
    //

    public AuditEventPersistenceAccessor() {
        jsonMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        jsonMapper.registerModule(module);
    }



    public AuditEvent logAuditEventAsynchronously(AuditEvent auditEvent) {
        getLogger().debug(".logAuditEventAsynchronously(): Entry, auditEvent->{}", auditEvent);
        if(auditEvent == null){
            getLogger().debug(".logAuditEventAsynchronously(): Exit, auditEvent is null");
            return(null);
        }
        //
        // TODO Remove this, it is a temporary fix to Reduce AuditTrail Load
        //
        /*
        MethodOutcome outcome = utiliseAuditEventPersistenceCapability(auditEvent);

        IIdType id = outcome.getId();
        if(id != null){
            auditEvent.setId(id);
        }
         */
        UUID uuid = UUID.randomUUID();
        String id = Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits());
        auditEvent.setId(id);
//        getLogger().debug(".logAuditEventAsynchronously(): Exit, outcome->{}", outcome);
        return(auditEvent);
    }

    @PostConstruct
    public void initialise() {
        fhirParser = fhirContextUtility.getJsonParser();
    }

    public synchronized MethodOutcome utiliseAuditEventPersistenceCapability(AuditEvent auditEvent) {
        getLogger().debug(".utiliseAuditEventPersistenceCapability(): Entry, auditEvent --> {}", auditEvent);
        //
        // Build Write
        //
        String auditEventString = convertToJSONString(auditEvent);
        CapabilityUtilisationRequest task = new CapabilityUtilisationRequest();
        task.setRequestID(UUID.randomUUID().toString());
        task.setRequestContent(auditEventString);
        task.setRequiredCapabilityName("FHIR-AuditEvent-Persistence");
        task.setRequestInstant(Instant.now());
        //
        // Do Write
        //
        CapabilityUtilisationResponse auditEventWriteOutcome = capabilityUtilisationBroker.executeTask(AUDIT_EVENT_PERSISTENCE_CAPABILITY_PROVIDER, task);
        //
        // Extract the response
        //
        String resultString = auditEventWriteOutcome.getResponseStringContent();
        MethodOutcome methodOutcome = outcomeFactory.convertToMethodOutcome(resultString);
        getLogger().debug(".utiliseAuditEventPersistenceCapability(): Entry, methodOutcome --> {}", methodOutcome);
        return (methodOutcome);
    }

    private String convertToJSONString(AuditEvent auditEvent) {
        String auditEventString = fhirParser.encodeResourceToString(auditEvent);
        return (auditEventString);
    }

    private AuditEvent convertToAuditEvent(String auditEventString) {
        AuditEvent auditEvent = fhirParser.parseResource(AuditEvent.class, auditEventString);
        return (auditEvent);
    }



    //
    // Getters (and Setters)
    //

    protected ObjectMapper getJSONMapper() {
        return (jsonMapper);
    }

    protected Logger getLogger() {
        return (LOG);
    }
}
