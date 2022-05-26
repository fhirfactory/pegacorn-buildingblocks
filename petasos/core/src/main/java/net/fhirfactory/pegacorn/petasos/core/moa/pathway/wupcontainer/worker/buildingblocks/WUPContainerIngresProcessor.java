/*
 * Copyright (c) 2020 MAHun
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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.oam.notifications.PetasosITOpsNotificationAgentInterface;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.ITOpsNotificationContent;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosTaskExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstance;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.notifications.PetasosITOpsNotificationContentFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.seda.SedaEndpoint;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class WUPContainerIngresProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerIngresProcessor.class);

    private DateTimeFormatter timeFormatter;

    @Inject
    private LocalPetasosFulfilmentTaskActivityController fulfillmentActivityController;

    @Inject
    private PetasosFulfillmentTaskAuditServicesBroker auditServicesBroker;

    @Inject
    private PetasosITOpsNotificationAgentInterface notificationAgent;

    @Inject
    private PetasosITOpsNotificationContentFactory notificationContentFactory;

    @Inject
    private ProcessingPlantMetricsAgentAccessor metricsAgentAccessor;

    @Inject
    private CamelContext camelctx;


    //
    // Constructor(s)
    //

    public WUPContainerIngresProcessor(){
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS").withZone(ZoneId.of(PetasosPropertyConstants.DEFAULT_TIMEZONE));
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected DateTimeFormatter getTimeFormatter(){
        return(this.timeFormatter);
    }

    //
    // Business Methods
    //

    /**
     * This class/method is used as the injection point into the WUP Processing Framework for the specific WUP Type/Instance in question.
     * It registers the following:
     *      - A ResilienceParcel for the UoW (registered with the SystemModule Parcel Cache: via the PetasosServiceBroker)
     *      - A WUPJobCard for the associated Work Unit Activity (registered into the SystemModule Activity Matrix: via the PetasosServiceBroker)
     *      - A ParcelStatusElement for the ResilienceParcel (again, register into the SystemModule Activity Matrix: via the PetasosServiceBroker)
     *
     * The function handles both new UoW or UoW instances that are being re-tried.
     *
     * It performs checks on the Status (WUPJobCard.currentStatus & ParcelStatusElement.hasClusterFocus) to determine if this WUP-Thread should
     * actually perform the Processing of the UoW via the WUP.
     *
     * It also checks on / assigns values to the Status (ParcelStatusElement.parcelStatus) if there are issues with the parcel. If there are, it may also
     * assign a "failed" status to both the WUPJobCard and ParcelStatusElement, and trigger a discard of this Parcel (for a retry) via setting the
     * WUPJobCard.isToBeDiscarded attribute to true.
     *
     * Finally, if all is going OK, but this WUP-Thread does not have the Cluster Focus (or SystemWide Focus), it waits in a sleep/loop until a condition
     * changes.
     *
     * @param fulfillmentTask The WorkUnitTransportPacket that is to be forwarded to the Intersection (if all is OK)
     * @param camelExchange The Apache Camel Exchange object, used to store a Semaphors and Attributes
     * @return Should return a WorkUnitTransportPacket that is forwarding onto the WUP Ingres Gatekeeper.
     */
    public PetasosFulfillmentTaskSharedInstance ingresContentProcessor(PetasosFulfillmentTaskSharedInstance fulfillmentTask, Exchange camelExchange) {
        if(getLogger().isInfoEnabled()) {
            getLogger().info(".ingresContentProcessor(): Entry, fulfillmentTaskId/ActionableTaskId->{}/{}", fulfillmentTask.getTaskId(), fulfillmentTask.getActionableTaskId());
        }

        //
        // Now, set out wait-loop sleep period
        long waitTime = PetasosPropertyConstants.WUP_SLEEP_INTERVAL_MILLISECONDS;

        //
        // Set our wait-loop check state
        boolean waitState = true;

        TopologyNodeFunctionFDNToken wupFunctionToken = fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getNodeFunctionFDN().getFunctionToken();
        getLogger().trace(".receiveFromWUP(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken);

        //
        // Now, continue with business logic
        RouteElementNames elementNames = new RouteElementNames(wupFunctionToken);
        SedaEndpoint seda = (SedaEndpoint) camelctx.getEndpoint(elementNames.getEndPointWUPContainerIngresProcessorIngres());
        int currentQueueSize = seda.getCurrentQueueSize();

        //
        // Get out metricsAgent & do add some metrics
        WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
        metricsAgent.incrementRegisteredTasks();
        metricsAgent.touchLastActivityInstant();

        //
        // Add some notifications
        ITOpsNotificationContent notificationContent = new ITOpsNotificationContent();
        if(fulfillmentTask.hasTaskWorkItem()) {
            if(fulfillmentTask.getTaskWorkItem().hasIngresContent()) {
                UoWPayload payload = fulfillmentTask.getTaskWorkItem().getIngresContent();

                StringBuilder unformattedMessageBuilder = new StringBuilder();
                unformattedMessageBuilder.append("--- Received Task (PetasosFulfillmentTask): Ingres Queue Size: "+currentQueueSize+" ---");
                unformattedMessageBuilder.append(" ("+ getTimeFormatter().format(Instant.now())+ ") ---\n");
                unformattedMessageBuilder.append("Task ID (FulfillmentTask) --> " + fulfillmentTask.getTaskId().getId() + "\n");
                unformattedMessageBuilder.append("Task ID (ActionableTask) --> " + fulfillmentTask.getActionableTaskId().getId() + "\n");
                unformattedMessageBuilder.append(notificationContentFactory.newNotificationContentFromUoWPayload(payload));
                notificationContent.setContent(unformattedMessageBuilder.toString());

                StringBuilder formattedMessageBuilder = new StringBuilder();
                formattedMessageBuilder.append("<table>");
                formattedMessageBuilder.append("<tr>");
                formattedMessageBuilder.append("<th>Ingres Task</th><th>"+getTimeFormatter().format(Instant.now())+", QueueSize:"+currentQueueSize+"</th>");
                formattedMessageBuilder.append("</tr>");
                formattedMessageBuilder.append("<tr>");
                formattedMessageBuilder.append("<td>FulfillmentTaskId</td><td>"+fulfillmentTask.getTaskId().getId()+"</td>");
                formattedMessageBuilder.append("</tr>");
                formattedMessageBuilder.append("<tr>");
                formattedMessageBuilder.append("<td>ActionableTask</td><td>"+fulfillmentTask.getActionableTaskId().getId()+"</td>");
                formattedMessageBuilder.append("</tr>");
                formattedMessageBuilder.append("<tr>");
                formattedMessageBuilder.append("<td>Payload</td>"+notificationContentFactory.payloadTypeFromUoW(payload)+"</td>");
                formattedMessageBuilder.append("</tr>");
                formattedMessageBuilder.append("</table>");
                notificationContent.setFormattedContent(formattedMessageBuilder.toString());
            }
        }
        if(StringUtils.isEmpty(notificationContent.getContent())){
            notificationContent.setContent("Task Received (Metadata) \n" +
            "Task Id (FulfillmentTask)--> " + fulfillmentTask.getTaskId().getId() + "\n" +
            "Task Id (ActionableTask)--> " + fulfillmentTask.getActionableTaskId().getId());
        }
        metricsAgent.sendITOpsNotification(notificationContent.getContent(), notificationContent.getFormattedContent());

        //
        // Write an AuditEvent
        auditServicesBroker.logActivity(fulfillmentTask.getInstance());
        //
        // Now check status
        boolean willExecute = false;
        boolean willBeCancelled = false;
        while (waitState) {
            if(getLogger().isDebugEnabled()){
                getLogger().debug(".ingresContentProcessor(): jobCard.getCurrentStatus --> {}",fulfillmentTask.getTaskJobCard().getCurrentStatus());
            }
            //
            // A bit of defensive programming //TODO Find out why this is needed
            if(fulfillmentTask.getExecutionStatus() == null){
                fulfillmentTask.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_WAITING);
            }
            if(fulfillmentTask.getExecutionStatus().equals(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_WAITING)){
                PetasosTaskExecutionStatusEnum petasosTaskExecutionStatusEnum = fulfillmentActivityController.requestFulfillmentTaskExecutionPrivilege(fulfillmentTask);
                if (petasosTaskExecutionStatusEnum == PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING) {
                    fulfillmentTask.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING);
                    fulfillmentTask.setUpdateInstant(Instant.now());
                    fulfillmentTask.getTaskFulfillment().setStartInstant(Instant.now());
                    fulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
                    fulfillmentTask.getTaskFulfillment().setToBeDiscarded(false);
                    fulfillmentTask.update();
                    fulfillmentActivityController.notifyFulfillmentTaskExecutionStart(fulfillmentTask);
                    waitState = false;
                    willExecute = true;
                    break;
                }
            } else {
                fulfillmentTask.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_CANCELLED);
                fulfillmentTask.setUpdateInstant(Instant.now());
                fulfillmentTask.getTaskFulfillment().setStartInstant(Instant.now());
                fulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
                fulfillmentActivityController.notifyFulfillmentTaskExecutionCancellation(fulfillmentTask);
                fulfillmentTask.getTaskFulfillment().setToBeDiscarded(true);
                if(getLogger().isDebugEnabled()) {
                    getLogger().debug(".ingresContentProcessor(): jobcard->{}", fulfillmentTask.getTaskJobCard());
                }
                waitState = false;
                willBeCancelled = true;
            }
            if (waitState) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    getLogger().trace(".ingresContentProcessor(): Something interrupted my nap! reason --> {}", e.getMessage());
                }
            }
            if(getLogger().isInfoEnabled()){
                getLogger().debug(".ingresContentProcessor(): Looping, current fulfillmentTask.getTaskJobCard().getCurrentStatus()->{}", fulfillmentTask.getTaskJobCard().getCurrentStatus());
            }
        }
        //
        // Write Some Metrics
        if(willExecute){
            getLogger().debug(".ingresContentProcessor(): Will be executing!");
            metricsAgent.incrementStartedTasks();
            metricsAgent.touchLastActivityStartInstant();
        }
        if(willBeCancelled){
            metricsAgent.incrementCancelledTasks();
        }
        //
        // Do some Logging
        if(getLogger().isInfoEnabled()){
            getLogger().info(".ingresContentProcessor(): Exit, fulfillmentTask.getTaskJobCard().getCurrentStatus()->{}", fulfillmentTask.getTaskJobCard().getCurrentStatus());
        }
        getLogger().debug(".ingresContentProcessor(): Exit, newTransportPacket --> {}", fulfillmentTask);
        //
        // Now We are Doing!
        return (fulfillmentTask);
    }
}
