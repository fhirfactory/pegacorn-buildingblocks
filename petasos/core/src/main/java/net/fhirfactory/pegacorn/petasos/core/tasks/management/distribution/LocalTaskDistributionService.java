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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.distribution;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.edge.PetasosEdgeMessageForwarderService;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.participants.cache.LocalParticipantRegistrationCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosFulfillmentTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosTaskJobCardFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalTaskActivityManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.outcomes.TaskPerformerSubscriptionDecisionEngine;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosWUPMetricsAgentCache;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Mark A. Hunter
 */

@ApplicationScoped
public class LocalTaskDistributionService {

    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskDistributionService.class);

    private DateTimeFormatter timeFormatter;

    @Inject
    TopologyIM topologyProxy;

    @Produce
    private ProducerTemplate camelProducerService;

    @Inject
    private PetasosFulfillmentTaskFactory fulfillmentTaskFactory;

    @Inject
    private PetasosEdgeMessageForwarderService forwarderService;

    @Inject
    private ProcessingPlantInterface myProcessingPlant;

    @Inject
    private LocalTaskActivityManager taskActivityManager;

    @Inject
    private TaskPerformerSubscriptionDecisionEngine taskDistributionDecisionEngine;

    @Inject
    private LocalParticipantRegistrationCache localParticipantRegistrationCache;

    @Inject
    private PetasosWUPMetricsAgentCache metricsAgentCache;

    //
    // Constructor(s)
    //

    public LocalTaskDistributionService(){
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

    protected TaskPerformerSubscriptionDecisionEngine getTaskDistributionDecisionEngine(){
        return(taskDistributionDecisionEngine);
    }

    protected LocalTaskActivityManager getTaskActivityManager(){
        return(taskActivityManager);
    }

    protected PetasosWUPMetricsAgentCache getMetricsAgentCache(){
        return(metricsAgentCache);
    }

    //
    // Business Logic
    //

    /**
     * Essentially, we get the set of WUPs subscribing to a particular UoW type,
     * create a property within the CamelExchange and then we use that Property
     * as a mechanism of keeping track of who we have already forwarded the UoW
     * to. Once we've cycled through all the targets (subscribers), we return
     * null.
     *
     * @param actionableTask Incoming WorkUnitTransportPacket that will be distributed to all
     * Subscribed WUPs
     * @return An endpoint (name) for a recipient for the incoming UoW
     */

    public void distributeNewActionableTasks(PetasosParticipantId participantId, PetasosActionableTask actionableTask) {
        getLogger().debug(".distributeNewFulfillmentTasks(): Entry, participatId->{}, actionableTask->{}", participantId, actionableTask);

        //
        // Defensive Programming
        if(actionableTask == null){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task is null, returning an empty list for routing.");
            return;
        }
        if(!actionableTask.hasTaskWorkItem()){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task has no work item, returning an empty list for routing.");
            return;
        }
        if(!actionableTask.getTaskWorkItem().hasIngresContent()){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task has a work item with no ingres content, returning an empty list for routing.");
            return;
        }
        if(!actionableTask.getTaskWorkItem().getIngresContent().hasPayloadManifest()){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task has a work item with no ingres content manifest, returning an empty list for routing.");
            return;
        }
        if(participantId == null){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, participantId is null");
            return;
        }
        if(StringUtils.isEmpty(participantId.getName())){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, participantId.getName() is empty");
            return;
        }

        PetasosParticipantRegistration targetParticipant = localParticipantRegistrationCache.getParticipantRegistration(participantId);
        if(targetParticipant == null){
            getLogger().warn(".distributeNewActionableTasks(): No Target To Deliver Task To!!!");
            return;
        }

        getLogger().warn(".distributeNewActionableTasks(): Sending Task->{} to Participant->{}", actionableTask.getTaskId().getId(), participantId.getName());

        boolean isRemoteTarget = false;
        String intendedTargetName = null;
        DataParcelManifest payloadTopicID = actionableTask.getTaskWorkItem().getPayloadTopicID();

        if (getTaskDistributionDecisionEngine().hasRemoteServiceName(targetParticipant)) {
            getLogger().trace(".forwardTask(): Has Remote Service as Target");
            boolean hasEmptyIntendedTarget = StringUtils.isEmpty(payloadTopicID.getIntendedTargetSystem());
            boolean hasWildcardTarget = false;
            if (getTaskDistributionDecisionEngine().hasIntendedTarget(payloadTopicID)) {
                hasWildcardTarget = payloadTopicID.getIntendedTargetSystem().contentEquals(DataParcelManifest.WILDCARD_CHARACTER);
            }
            boolean hasRemoteElement = getTaskDistributionDecisionEngine().hasRemoteServiceName(targetParticipant);
            getLogger().trace(".forwardTask(): hasEmptyIntendedTarget->{}, hasWildcardTarget->{}, hasRemoteElement->{} ", hasEmptyIntendedTarget, hasWildcardTarget, hasRemoteElement);
            if ((hasEmptyIntendedTarget || hasWildcardTarget) && hasRemoteElement) {
                intendedTargetName = participantId.getSubsystemName();
                getLogger().trace(".forwardTask(): Setting the intendedTargetSystem->{}", participantId.getSubsystemName());
                isRemoteTarget = true;
            }
        }

        ComponentIdType actualSubscriberId = null;
        getLogger().trace(".forwardTask(): Assigning target component id, based on whether it is a remote component or local");
        if(isRemoteTarget){
            getLogger().trace(".forwardTask(): It is a remote target, so routing via the local forwarder service");
            actualSubscriberId = forwarderService.getComponentId();
        } else {
            getLogger().trace(".forwardTask(): It is a local target, so routing directly to target");
            actualSubscriberId = targetParticipant.getLocalComponentId();
        }
        getLogger().trace(".forwardTask(): The (LocalSubscriber aspect) IdentifieFHIRCommunicationToUoWr->{}", actualSubscriberId);
        WorkUnitProcessorSoftwareComponent currentNodeElement = (WorkUnitProcessorSoftwareComponent)topologyProxy.getNode(actualSubscriberId);
        getLogger().trace(".forwardTask(): The WUPToken for the target participantId->{}", targetParticipant.getParticipantId());
        RouteElementNames routeName = new RouteElementNames(targetParticipant.getParticipantId());
        // Create FulfillmentTask and Inject into Target WUP
        getLogger().trace(".forwardTask(): Create actually PetasosFulfillmentTask: Start");
        PetasosFulfillmentTask petasosFulfillmentTask = fulfillmentTaskFactory.newFulfillmentTask(actionableTask, currentNodeElement);
        getLogger().trace(".forwardTask(): Create actually PetasosFulfillmentTask: petasosFulfillmentTask->{}", petasosFulfillmentTask);
        getLogger().trace(".forwardTask(): Create actually PetasosFulfillmentTask: Finish");
        petasosFulfillmentTask.getTaskWorkItem().getPayloadTopicID().setTargetProcessingPlantParticipantName(targetParticipant.getParticipantId().getSubsystemName());
        //
        // Register The FulfillmentTask
        getLogger().trace(".forwardTask(): Register PetasosFulfillmentTask: Start");
        PetasosFulfillmentTask fulfillmentTask = getTaskActivityManager().registerFulfillmentTask(petasosFulfillmentTask, false);
        getLogger().trace(".forwardTask(): Register PetasosFulfillmentTask: Finish");
        getLogger().trace(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: Start");
        String targetCamelEndpoint = routeName.getEndPointWUPContainerIngresProcessorIngres();
        getLogger().trace(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: targetCamelEndpoint->{}", targetCamelEndpoint);
        //
        // Get out metricsAgent & do add some metrics
        WorkUnitProcessorMetricsAgent metricsAgent = getMetricsAgentCache().getMetricsAgent(participantId.getName());
        metricsAgent.incrementInternalMessageDistributionCount();
        metricsAgent.touchLastActivityInstant();
        StringBuilder distributionMessageBuilder = new StringBuilder();
        distributionMessageBuilder.append("--- Distributing new PetasosFulfillmentTask ---");
        distributionMessageBuilder.append(" ("+ getTimeFormatter().format(Instant.now())+ ") ---\n");
        distributionMessageBuilder.append("TaskID (FulfillmentTask) --> " + petasosFulfillmentTask.getTaskId().getId() + "\n");
        distributionMessageBuilder.append("TaskID (ActionableTask) --> " + petasosFulfillmentTask.getActionableTaskId().getId() + "\n");
        distributionMessageBuilder.append("Target --> " + participantId + "\n");
        metricsAgent.sendITOpsNotification(distributionMessageBuilder.toString());
        //
        // Forward the Task
        if(getLogger().isDebugEnabled())
        {
            getLogger().debug(".forwardTask(): Forwarding To->{}, Task->{}",participantId, petasosFulfillmentTask.getActionableTaskId().getId() );
        }
//        Object propertyValue = camelExchange.getProperty("CamelAttachmentObjects");
//        camelProducerService.sendBodyAndProperty(targetCamelEndpoint, ExchangePattern.InOnly, petasosFulfillmentTask, "CamelAttachmentObjects", propertyValue);
        camelProducerService.sendBody(targetCamelEndpoint, ExchangePattern.InOnly, petasosFulfillmentTask);
        getLogger().trace(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: Finish");
    }

}
