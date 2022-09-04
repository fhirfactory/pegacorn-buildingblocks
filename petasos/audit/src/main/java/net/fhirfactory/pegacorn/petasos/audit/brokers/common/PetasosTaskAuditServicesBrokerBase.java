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
package net.fhirfactory.pegacorn.petasos.audit.brokers.common;

import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventGranularityLevelInterface;
import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceAgentInterface;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
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

public abstract class PetasosTaskAuditServicesBrokerBase {

    @Inject
    private PetasosAuditEventServiceAgentInterface auditWriter;

    @Inject
    private PetasosFulfillmentTask2FHIRAuditEvent fulfillmentTask2FHIRAuditEventTransformer;

    @Inject
    private UoWPayload2FHIRAuditEvent uow2AuditEventTransformer;

    @Inject
    private Exception2FHIRAuditEvent exception2AuditEventTransfomer;

    @Inject
    private PetasosAuditEventGranularityLevelInterface auditEventGranularityLevel;

    //
    // Constructor(s)
    //


    //
    // Post Construct
    //


    //
    // Business Methods
    //


    public void logCamelExecutionException(Object object, Exchange camelExchange){
        getLogger().debug(".logCamelExecutionException(): Entry, object->{}",object);
        CamelExecutionException camelExecutionException = camelExchange.getProperty(Exchange.EXCEPTION_CAUGHT, CamelExecutionException.class);
        if(camelExecutionException != null) {
            AuditEvent auditEvent = getException2AuditEventTransfomer().transformCamelExecutionException(camelExecutionException);
            Boolean success =  auditWriter.captureAuditEvent(auditEvent, true);
        }
        getLogger().debug(".logCamelExecutionException(): Entry, object->{}",object);
    }

    protected boolean shouldLogAuditEventForTask(PetasosFulfillmentTask fulfillmentTask){
        if(fulfillmentTask.getTaskFulfillment().getStatus().equals(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED)) {
            return(true);
        }
        if(fulfillmentTask.getTaskFulfillment().getStatus().equals(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED)) {
            switch (auditEventGranularityLevel.getAuditEventGranularityLevel()) {
                case AUDIT_LEVEL_BLACK_BOX:
                case AUDIT_LEVEL_COARSE:
                    break;
                case AUDIT_LEVEL_FINE:
                case AUDIT_LEVEL_VERY_FINE:
                case AUDIT_LEVEL_EXTREME:
                    return(true);
            }
        }
        if(fulfillmentTask.hasTaskFulfillment()){
            switch(fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getComponentSystemRole()){
                case COMPONENT_ROLE_INTERACT_EGRESS:
                case COMPONENT_ROLE_INTERACT_INGRES: {
                    switch (auditEventGranularityLevel.getAuditEventGranularityLevel()) {
                        case AUDIT_LEVEL_COARSE:
                        case AUDIT_LEVEL_FINE:
                        case AUDIT_LEVEL_VERY_FINE:
                        case AUDIT_LEVEL_EXTREME:
                        case AUDIT_LEVEL_BLACK_BOX:
                        default:
                            return (true);
                    }
                }
                case COMPONENT_ROLE_SUBSYSTEM_EDGE: {
                    switch (auditEventGranularityLevel.getAuditEventGranularityLevel()) {
                        case AUDIT_LEVEL_COARSE:
                        case AUDIT_LEVEL_FINE:
                        case AUDIT_LEVEL_VERY_FINE:
                        case AUDIT_LEVEL_EXTREME:
                            return (true);
                        case AUDIT_LEVEL_BLACK_BOX:
                        default:
                            return (false);
                    }
                }
                case COMPONENT_ROLE_SUBSYSTEM_TASK_DISTRIBUTION: {
                    switch (auditEventGranularityLevel.getAuditEventGranularityLevel()) {
                        case AUDIT_LEVEL_FINE:
                        case AUDIT_LEVEL_VERY_FINE:
                        case AUDIT_LEVEL_EXTREME:
                            return (true);
                        case AUDIT_LEVEL_BLACK_BOX:
                        case AUDIT_LEVEL_COARSE:
                        default:
                            return (false);
                    }
                }
                case COMPONENT_ROLE_SUBSYSTEM_INTERNAL: {
                    switch (auditEventGranularityLevel.getAuditEventGranularityLevel()) {
                        case AUDIT_LEVEL_VERY_FINE:
                        case AUDIT_LEVEL_EXTREME:
                            return (true);
                        case AUDIT_LEVEL_FINE:
                        case AUDIT_LEVEL_BLACK_BOX:
                        case AUDIT_LEVEL_COARSE:
                        default:
                            return (false);
                    }
                }
                default:{
                    switch (auditEventGranularityLevel.getAuditEventGranularityLevel()) {
                        case AUDIT_LEVEL_EXTREME:
                            return (true);
                        case AUDIT_LEVEL_FINE:
                        case AUDIT_LEVEL_VERY_FINE:
                        case AUDIT_LEVEL_BLACK_BOX:
                        case AUDIT_LEVEL_COARSE:
                        default:
                            return (false);
                    }
                }
            }
        }
        return(false);
    }

    //
    // Getters (and Setters)
    //

    abstract protected Logger getLogger();

    protected Exception2FHIRAuditEvent getException2AuditEventTransfomer(){
        return(this.exception2AuditEventTransfomer);
    }

    protected UoWPayload2FHIRAuditEvent getUow2AuditEventTransformer(){
        return(this.uow2AuditEventTransformer);
    }

    protected PetasosFulfillmentTask2FHIRAuditEvent getFulfillmentTask2FHIRAuditEventTransformer(){
        return(this.fulfillmentTask2FHIRAuditEventTransformer);
    }

    protected PetasosAuditEventServiceAgentInterface getAuditWriter(){
        return(this.auditWriter);
    }
}
