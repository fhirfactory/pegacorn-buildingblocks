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
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.ITOpsNotificationContent;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalTaskActivityManager;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.notifications.PetasosITOpsNotificationContentFactory;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Dependent
public class WUPContainerIngresProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerIngresProcessor.class);

    private DateTimeFormatter timeFormatter;

    @Inject
    private PetasosFulfillmentTaskAuditServicesBroker auditServicesBroker;

    @Inject
    private PetasosITOpsNotificationContentFactory notificationContentFactory;

    @Inject
    private LocalTaskActivityManager localTaskActivityManager;

    @Inject
    private LocalParticipantManager participantManager;


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

    protected LocalTaskActivityManager getLocalTaskActivityManager(){
        return(localTaskActivityManager);
    }

    protected LocalParticipantManager getParticipantManager(){
        return(participantManager);
    }

    //
    // Business Methods
    //

    /**
     * This class/method is used as the injection point into the WUP Processing Framework for the specific WUP Type/Instance in question.
     * It is only invoked if the WUP is cleared to process the TaskWorkItem and is not in a "Suspended" State.
     *
     * @param fulfillmentTask The WorkUnitTransportPacket that is to be forwarded to the Intersection (if all is OK)
     * @param camelExchange The Apache Camel Exchange object, used to store a Semaphors and Attributes
     * @return A PetasosFulfillmentTask that is forwarding onto the WUP Ingres Gatekeeper.
     */
    public PetasosFulfillmentTask ingresContentProcessor(PetasosFulfillmentTask fulfillmentTask, Exchange camelExchange) {
        if(getLogger().isInfoEnabled()) {
            getLogger().info(".ingresContentProcessor(): Entry, fulfillmentTaskId/ActionableTaskId->{}/{}", fulfillmentTask.getTaskId(), fulfillmentTask.getActionableTaskId());
        }

        PetasosParticipantId wupParticipantId = fulfillmentTask.getTaskFulfillment().getFulfiller().getParticipant().getParticipantId();
        getLogger().trace(".ingresContentProcessor(): wupParticipantId (PetasosParticipantId) for this activity --> {}", wupParticipantId);

        //
        // Now, continue with business logic
        RouteElementNames elementNames = new RouteElementNames(wupParticipantId);

        TaskExecutionCommandEnum taskExecutionCommandEnum = getLocalTaskActivityManager().notifyTaskStart(fulfillmentTask.getActionableTaskId(), fulfillmentTask);


        //
        // Get out metricsAgent & do add some metrics
        WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
        metricsAgent.incrementRegisteredTasks();
        metricsAgent.touchLastActivityInstant();

        //
        // Add some notifications
        ITOpsNotificationContent notificationContent = new ITOpsNotificationContent();
        try {
            if (fulfillmentTask.hasTaskWorkItem()) {
                if (fulfillmentTask.getTaskWorkItem().hasIngresContent()) {
                    UoWPayload payload = fulfillmentTask.getTaskWorkItem().getIngresContent();

                    StringBuilder unformattedMessageBuilder = new StringBuilder();
                    unformattedMessageBuilder.append("--- Received Task (PetasosFulfillmentTask) ---");
                    unformattedMessageBuilder.append(" (" + getTimeFormatter().format(Instant.now()) + ") ---\n");
                    unformattedMessageBuilder.append("Task ID (FulfillmentTask) --> " + fulfillmentTask.getTaskId().getId() + "\n");
                    unformattedMessageBuilder.append("Task ID (ActionableTask) --> " + fulfillmentTask.getActionableTaskId().getId() + "\n");
                    unformattedMessageBuilder.append(notificationContentFactory.newNotificationContentFromUoWPayload(payload));
                    notificationContent.setContent(unformattedMessageBuilder.toString());

                    StringBuilder formattedMessageBuilder = new StringBuilder();
                    formattedMessageBuilder.append("<table>");
                    formattedMessageBuilder.append("<tr>");
                    formattedMessageBuilder.append("<th>Ingres Task</th><th>" + getTimeFormatter().format(Instant.now()) + "</th>");
                    formattedMessageBuilder.append("</tr>");
                    formattedMessageBuilder.append("<tr>");
                    formattedMessageBuilder.append("<td>FulfillmentTaskId</td><td>" + fulfillmentTask.getTaskId().getId() + "</td>");
                    formattedMessageBuilder.append("</tr>");
                    formattedMessageBuilder.append("<tr>");
                    formattedMessageBuilder.append("<td>ActionableTask</td><td>" + fulfillmentTask.getActionableTaskId().getId() + "</td>");
                    formattedMessageBuilder.append("</tr>");
                    formattedMessageBuilder.append("<tr>");
                    formattedMessageBuilder.append("<td>Payload</td>" + notificationContentFactory.payloadTypeFromUoW(payload) + "</td>");
                    formattedMessageBuilder.append("</tr>");
                    formattedMessageBuilder.append("</table>");
                    notificationContent.setFormattedContent(formattedMessageBuilder.toString());
                }
            }
            if (StringUtils.isEmpty(notificationContent.getContent())) {
                notificationContent.setContent("Task Received (Metadata) \n" +
                        "Task Id (FulfillmentTask)--> " + fulfillmentTask.getTaskId().getId() + "\n" +
                        "Task Id (ActionableTask)--> " + fulfillmentTask.getActionableTaskId().getId());
            }
            metricsAgent.sendITOpsNotification(notificationContent.getContent(), notificationContent.getFormattedContent());
        } catch( Exception ex){
            getLogger().warn(".ingresContentProcessor(): Cannot send ITOps Notification: printing here->{}", notificationContent.getContent());
        }

        //
        // Write an AuditEvent
        try {
            auditServicesBroker.logActivity(fulfillmentTask);
        } catch(Exception ex){
            getLogger().warn(".ingresContentProcessor(): Could not generate/forward audit event!");
        }

        //
        // Set the Participant Status
        getParticipantManager().updateParticipantStatus(wupParticipantId.getName(), PetasosParticipantStatusEnum.PARTICIPANT_IS_ACTIVE);
        //
        // Write Some Metrics
        getLogger().debug(".ingresContentProcessor(): Will be executing!");
        metricsAgent.incrementStartedTasks();
        metricsAgent.touchLastActivityStartInstant();

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
