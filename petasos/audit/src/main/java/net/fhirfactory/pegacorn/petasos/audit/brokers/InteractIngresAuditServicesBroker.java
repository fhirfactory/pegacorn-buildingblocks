/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.audit.brokers;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventGranularityLevelInterface;
import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceAgentInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.factories.AuditEventEntityFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.factories.AuditEventFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.*;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.factories.DeviceFactory;
import net.fhirfactory.pegacorn.petasos.audit.transformers.Exception2FHIRAuditEvent;
import net.fhirfactory.pegacorn.petasos.audit.transformers.PetasosFulfillmentTask2FHIRAuditEvent;
import net.fhirfactory.pegacorn.petasos.audit.transformers.UoWPayload2FHIRAuditEvent;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class InteractIngresAuditServicesBroker {
    private static final Logger LOG = LoggerFactory.getLogger(InteractIngresAuditServicesBroker.class);

    @Inject
    private PetasosAuditEventServiceAgentInterface auditWriter;

    @Inject
    private PetasosFulfillmentTask2FHIRAuditEvent parcefulfillmentTask2FHIRAuditEvent2auditevent;

    @Inject
    private UoWPayload2FHIRAuditEvent uow2auditevent;

    @Inject
    private Exception2FHIRAuditEvent exception2FHIRAuditEvent;

    @Inject
    private PetasosAuditEventGranularityLevelInterface auditEventGranularityLevel;

    @Inject
    private AuditEventFactory auditEventFactory;

    @Inject
    private AuditEventEntityFactory auditEventEntityFactory;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private DeviceFactory deviceFactory;

    //
    // Constructor(s)
    //


    //
    // Post Construct
    //


    //
    // Business Methods
    //

    public void logHTTPIngresActivity(Object ingresPayload, Exchange camelExchange){
        getLogger().debug(".logMLLPTransactions(): Entry, ingresPayload->{}",ingresPayload);

        getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] Start...");
        IPCTopologyEndpoint endpoint = (IPCTopologyEndpoint) camelExchange.getProperty(PetasosPropertyConstants.ENDPOINT_TOPOLOGY_NODE_EXCHANGE_PROPERTY);

        String body = null;
        if(ingresPayload instanceof String){
            body = (String)ingresPayload;
        } else {
            body = "Unknown content";
        }

        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event] Start...");
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event][Converting from PetasosFulfillmentTask to AuditEvent] Start...");
        AuditEvent auditEvent = transformHTTPActivity2AuditEvent(body, endpoint);
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event][Converting from PetasosFulfillmentTask to AuditEvent] Finish..., auditEvent->{}", auditEvent);
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event][Calling auditWriter service] Start...");
        Boolean success =  auditWriter.captureAuditEvent(auditEvent, true);
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event][Calling auditWriter service] Finish..., success->{}", success);
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event] Finish...");
    }

    public AuditEvent transformHTTPActivity2AuditEvent(String body, IPCTopologyEndpoint endpoint){

        deviceFactory.newDeviceFromSoftwareComponent(endpoint);

        String auditEventEntityName = "HTTP.Ingres.Body";

        List<AuditEvent.AuditEventEntityDetailComponent> detailList = new ArrayList<>();
        AuditEvent.AuditEventEntityDetailComponent ingresDetailComponent = auditEventEntityFactory.newAuditEventEntityDetailComponent("HTTP.Ingress.Body", body);

        String descriptionText = "Interact.Ingres: HTTP Server";

        if(StringUtils.isEmpty(descriptionText)){
            descriptionText = "Ingres and Egress content fromm UoW (Unit of Work) Processors";
        }

        AuditEvent.AuditEventEntityComponent auditEventEntityComponent = auditEventEntityFactory.newAuditEventEntity(
                AuditEventEntityTypeEnum.PEGACORN_MLLP_MSG,
                AuditEventEntityRoleEnum.HL7_JOB,
                AuditEventEntityLifecycleEnum.HL7_TRANSMIT,
                auditEventEntityName,
                descriptionText,
                detailList);

        String sourceSite = null;
        if(endpoint != null) {
            if (endpoint.hasConnectedSystemName()) {
                sourceSite = endpoint.getConnectedSystemName();
            }
        }
        if(StringUtils.isEmpty(sourceSite)) {
            sourceSite = "Undisclosed";
        }

        /*
            AuditEventAction
            C	Create	Create a new database object, such as placing an order.
            R	Read/View/Print	Display or print data, such as a doctor census.
            U	Update	Update data, such as revise patient information.
            D	Delete	Delete items, such as a doctor master file record.
            E	Execute	Perform a system or application function such as log-on, program execution or use of an object's method, or perform a query/search operation.
         */
        AuditEvent.AuditEventAction auditEventAction = AuditEvent.AuditEventAction.C;

        /*
        	AuditEventOutcome
            0	Success	The operation completed successfully (whether with warnings or not).
            4	Minor failure	The action was not successful due to some kind of minor failure (often equivalent to an HTTP 400 response).
            8	Serious failure	The action was not successful due to some kind of unexpected error (often equivalent to an HTTP 500 response).
            12	Major failure	An error of such magnitude occurred that the system is no longer available for use (i.e. the system died).
         */
        AuditEvent.AuditEventOutcome auditEventOutcome = AuditEvent.AuditEventOutcome._0;

        Period period = new Period();
        period.setStart(Date.from(Instant.now()));
        period.setEnd(Date.from(Instant.now()));

        String outcomeString = "HTTP Action Received";

        AuditEvent auditEvent = auditEventFactory.newAuditEvent(
                null,
                processingPlant.getSimpleInstanceName(),
                processingPlant.getHostName(),
                sourceSite,
                null,
                AuditEventSourceTypeEnum.HL7_APPLICATION_SERVER,
                AuditEventTypeEnum.DICOM_APPLICATION_ACTIVITY,
                AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STARTED,
                auditEventAction,
                auditEventOutcome,
                outcomeString,
                period,
                auditEventEntityComponent);

        return(auditEvent);
    }



    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
