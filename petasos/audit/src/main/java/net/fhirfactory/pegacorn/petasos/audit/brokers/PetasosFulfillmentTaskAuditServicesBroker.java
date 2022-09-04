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

import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventGranularityLevelInterface;
import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceAgentInterface;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.petasos.audit.brokers.common.PetasosTaskAuditServicesBrokerBase;
import net.fhirfactory.pegacorn.petasos.audit.transformers.Exception2FHIRAuditEvent;
import net.fhirfactory.pegacorn.petasos.audit.transformers.PetasosFulfillmentTask2FHIRAuditEvent;
import net.fhirfactory.pegacorn.petasos.audit.transformers.UoWPayload2FHIRAuditEvent;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosFulfillmentTaskAuditServicesBroker extends PetasosTaskAuditServicesBrokerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosFulfillmentTaskAuditServicesBroker.class);


    //
    // Constructor(s)
    //


    //
    // Post Construct
    //


    //
    // Business Methods
    //

    public Boolean logActivity(PetasosFulfillmentTask fulfillmentTask) {
        getLogger().debug(".logActivity(): Entry, fulfillmentTask->{}",fulfillmentTask);
        boolean success = false;
        if(shouldLogAuditEventForTask(fulfillmentTask)) {
            success = logActivity(fulfillmentTask, false);
        }
        getLogger().debug(".logActivity(): Exit, success->{}",success);
        return(success);
    }

    public Boolean logActivity(PetasosFulfillmentTask fulfillmentTask, boolean requiresSynchronousWrite){
        getLogger().debug(".logActivity(): Entry, fulfillmentTask->{}, requiresSynchronousWrite->{}",fulfillmentTask, requiresSynchronousWrite);
        AuditEvent fulfillmentTaskAuditEntry = null;
        boolean success = false;
        if(shouldLogAuditEventForTask(fulfillmentTask)) {
            AuditEvent auditEntry = getFulfillmentTask2FHIRAuditEventTransformer().transform(fulfillmentTask);
            success = getAuditWriter().captureAuditEvent(fulfillmentTaskAuditEntry, requiresSynchronousWrite);
        }
        getLogger().debug(".logActivity(): Exit, success->{}",success);
        return(success);
    }

    public void logMLLPTransactions(PetasosFulfillmentTask fulfillmentTask, String filteredState, boolean requiresSynchronousWrite){
        getLogger().debug(".logMLLPTransactions(): Entry, fulfillmentTask->{}, filteredState->{}, requiresSynchronousWrite->{}",fulfillmentTask, filteredState, requiresSynchronousWrite);
        boolean isInteractEgressActivity = false;
        boolean isInteractIngresActivity = false;
        getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] Start...");
        if(fulfillmentTask.hasTaskFulfillment()){
            getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] fulfillmentTask has TaskFulfillmentType element");
            if(fulfillmentTask.getTaskFulfillment().hasFulfillerWorkUnitProcessor()) {
                getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] fulfillmentTask.getTaskFulfillmentType has FulfillmentWorkUnitProcessor");
                WorkUnitProcessorSoftwareComponent wupSoftwareComponent = (WorkUnitProcessorSoftwareComponent)fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor();
                if(wupSoftwareComponent.getIngresEndpoint() != null){
                    getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] fulfillmentTask.getTaskFulfillmentType().getFulfillmentWorkUnitProcessor has ingresEndpoint");
                    if(wupSoftwareComponent.getIngresEndpoint().getComponentSystemRole() != null){
                        getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] fulfillmentTask.getTaskFulfillmentType().getFulfillmentWorkUnitProcessor().getIngresEndpoint has ComponentSystemRole");
                        if(wupSoftwareComponent.getIngresEndpoint().getComponentSystemRole().equals(SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_INTERACT_INGRES)){
                            isInteractIngresActivity = true;
                        }
                    }
                }
                if(wupSoftwareComponent.getEgressEndpoint() != null){
                    getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] fulfillmentTask.getTaskFulfillmentType().getFulfillmentWorkUnitProcessor has egressEndpoint");
                    if(wupSoftwareComponent.getEgressEndpoint().getComponentSystemRole() != null){
                        getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] fulfillmentTask.getTaskFulfillmentType().getFulfillmentWorkUnitProcessor().getEgressEndpoint has ComponentSystemRole");
                        if(wupSoftwareComponent.getEgressEndpoint().getComponentSystemRole().equals(SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_INTERACT_EGRESS)){
                            isInteractEgressActivity = true;
                        }
                    }
                }
            }
        }
        getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] isInteractEgressActivity->{}", isInteractEgressActivity);
        getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] isInteractIngresActivity->{}", isInteractIngresActivity);
        getLogger().debug(".logMLLPTransactions(): [Derive Endpoint Role] Finish...");

        if(!(isInteractEgressActivity || isInteractIngresActivity)){
            getLogger().debug(".logMLLPTransactions(): Not an endpoint (Ingres/Egress)!");
            return;
        }
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event] Start...");
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event][Converting from PetasosFulfillmentTask to AuditEvent] Start...");
        AuditEvent auditEvent = getUow2AuditEventTransformer().transform(fulfillmentTask, filteredState, true);
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event][Converting from PetasosFulfillmentTask to AuditEvent] Finish..., auditEvent->{}", auditEvent);
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event][Calling auditWriter service] Start...");
        Boolean success =  getAuditWriter().captureAuditEvent(auditEvent, requiresSynchronousWrite);
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event][Calling auditWriter service] Finish..., success->{}", success);
        getLogger().debug(".logMLLPTransactions(): [Capture Audit Event] Finish...");
    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }
}
