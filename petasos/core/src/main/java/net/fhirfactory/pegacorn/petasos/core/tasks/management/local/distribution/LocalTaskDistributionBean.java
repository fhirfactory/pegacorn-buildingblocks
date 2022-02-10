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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.distribution;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.edge.PetasosEdgeMessageForwarderService;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstance;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstanceAccessorFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosTaskJobCardSharedInstanceAccessorFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosFulfillmentTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosTaskJobCardFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Mark A. Hunter
 */

@ApplicationScoped
public class LocalTaskDistributionBean {

    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskDistributionBean.class);

    private DateTimeFormatter timeFormatter;

    @Inject
    TopologyIM topologyProxy;

    @Produce
    private ProducerTemplate camelProducerService;

    @Inject
    private PetasosFulfillmentTaskFactory fulfillmentTaskFactory;

    @Inject
    private LocalPetasosFulfilmentTaskActivityController fulfilmentTaskBroker;

    @Inject
    private PetasosEdgeMessageForwarderService forwarderService;

    @Inject
    private ProcessingPlantInterface myProcessingPlant;

    @Inject
    private LocalTaskDistributionDecisionEngine taskDistributionDecisionEngine;

    @Inject
    private PetasosFulfillmentTaskSharedInstanceAccessorFactory fulfillmentTaskSharedInstanceFactory;

    @Inject
    private PetasosTaskJobCardSharedInstanceAccessorFactory taskJobCardInstanceFactory;

    @Inject
    private PetasosTaskJobCardFactory taskJobCardFactory;

    //
    // Constructor(s)
    //

    public LocalTaskDistributionBean(){
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

    protected LocalTaskDistributionDecisionEngine getTaskDistributionDecisionEngine(){
        return(taskDistributionDecisionEngine);
    }

    protected PetasosFulfillmentTaskSharedInstanceAccessorFactory getFulfillmentTaskSharedInstanceFactory(){
        return(this.fulfillmentTaskSharedInstanceFactory);
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
     * @param camelExchange The Apache Camel Exchange instance associated with
     * this route.
     * @return An endpoint (name) for a recipient for the incoming UoW
     */

    public void distributeNewFulfillmentTasks(PetasosActionableTask actionableTask, Exchange camelExchange) {
        getLogger().debug(".distributeNewFulfillmentTasks(): Entry, actionableTask (WorkUnitTransportPacket)->{}", actionableTask);

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
        if(!actionableTask.getTaskWorkItem().getIngresContent().hasDataParcelQualityStatement()){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task has a work item with no ingres content manifest, returning an empty list for routing.");
            return;
        }

        DataParcelManifest uowTopicID = actionableTask.getTaskWorkItem().getPayloadTopicID();
        getLogger().trace(".distributeNewFulfillmentTasks(): Looking for Subscribers To->{}:", uowTopicID);
        getLogger().trace(".distributeNewFulfillmentTasks(): Getting the set of subscribers for the given topic (calling the topicServer)");
        List<PetasosParticipant> subscriberSet = getTaskDistributionDecisionEngine().deriveSubscriberList(uowTopicID);
        if(subscriberSet.isEmpty()){
            getLogger().debug(".distributeNewFulfillmentTasks(): There are no subscribers, exiting");
            return;
        }

        getLogger().trace(".distributeNewFulfillmentTasks(): Distributing new PetasosTaskFulfillment tasks");
        for(PetasosParticipant currentSubscriber: subscriberSet){
            forwardTask(currentSubscriber, actionableTask, camelExchange);
        }

        getLogger().debug(".distributeNewFulfillmentTasks(): Exiting");
        return;
    }



    private void forwardTask(PetasosParticipant subscriber, PetasosActionableTask actionableTask, Exchange camelExchange){
        getLogger().debug(".forwardTask(): Subscriber --> {}", subscriber);
        // 1st check if the Subscriber is actually a remote one! If so, ensure it has a proper "IntendedTarget" entry
        boolean isRemoteTarget = false;
        String intendedTargetName = null;
        DataParcelManifest payloadTopicID = actionableTask.getTaskWorkItem().getPayloadTopicID();
//        if(getTaskDistributionDecisionEngine().hasIntendedTarget(payloadTopicID)){
//            if(!payloadTopicID.getIntendedTargetSystem().contentEquals(DataParcelManifest.WILDCARD_CHARACTER)){
//                if(!payloadTopicID.getIntendedTargetSystem().equals(myProcessingPlant.getSubsystemParticipantName())){
//                    isRemoteTarget = true;
//                }
//            }
//        }
        if (getTaskDistributionDecisionEngine().hasRemoteServiceName(subscriber)) {
            getLogger().debug(".forwardTask(): Has Remote Service as Target");
            boolean hasEmptyIntendedTarget = StringUtils.isEmpty(payloadTopicID.getIntendedTargetSystem());
            boolean hasWildcardTarget = false;
            if (getTaskDistributionDecisionEngine().hasIntendedTarget(payloadTopicID)) {
                hasWildcardTarget = payloadTopicID.getIntendedTargetSystem().contentEquals(DataParcelManifest.WILDCARD_CHARACTER);
            }
            boolean hasRemoteElement = getTaskDistributionDecisionEngine().hasRemoteServiceName(subscriber);
            getLogger().debug(".forwardTask(): hasEmptyIntendedTarget->{}, hasWildcardTarget->{}, hasRemoteElement->{} ", hasEmptyIntendedTarget, hasWildcardTarget, hasRemoteElement);
            if ((hasEmptyIntendedTarget || hasWildcardTarget) && hasRemoteElement) {
                intendedTargetName = subscriber.getSubsystemParticipantName();
                getLogger().trace(".forwardTask(): Setting the intendedTargetSystem->{}", subscriber.getSubsystemParticipantName());
                isRemoteTarget = true;
            }
        }

        ComponentIdType actualSubscriberId = null;
        getLogger().debug(".forwardTask(): Assigning target component id, based on whether it is a remote component or local");
        if(isRemoteTarget){
            getLogger().debug(".forwardTask(): It is a remote target, so routing via the local forwarder service");
            actualSubscriberId = forwarderService.getComponentId();
        } else {
            getLogger().debug(".forwardTask(): It is a local target, so routing directly to target");
            actualSubscriberId = subscriber.getComponentID();
        }
        getLogger().debug(".forwardTask(): The (LocalSubscriber aspect) IdentifieFHIRCommunicationToUoWr->{}", actualSubscriberId);
        WorkUnitProcessorSoftwareComponent currentNodeElement = (WorkUnitProcessorSoftwareComponent)topologyProxy.getNode(actualSubscriberId);
        getLogger().debug(".forwardTask(): The TopologyNode for the target currentNodeElement->{}", currentNodeElement);
        TopologyNodeFunctionFDNToken targetWUPFunctionToken = currentNodeElement.getNodeFunctionFDN().getFunctionToken();
        getLogger().debug(".forwardTask(): The WUPToken for the target targetWUPFunctionToken->{}", targetWUPFunctionToken);
        RouteElementNames routeName = new RouteElementNames(targetWUPFunctionToken);
        // Create FulfillmentTask and Inject into Target WUP
        getLogger().debug(".forwardTask(): Create actually PetasosFulfillmentTask: Start");
        PetasosFulfillmentTask petasosFulfillmentTask = fulfillmentTaskFactory.newFulfillmentTask(actionableTask, currentNodeElement);
        getLogger().trace(".forwardTask(): Create actually PetasosFulfillmentTask: petasosFulfillmentTask->{}", petasosFulfillmentTask);
        getLogger().debug(".forwardTask(): Create actually PetasosFulfillmentTask: Finish");
        petasosFulfillmentTask.getTaskWorkItem().getPayloadTopicID().setTargetProcessingPlantParticipantName(subscriber.getSubsystemParticipantName());
        //
        // Register The FulfillmentTask
        getLogger().debug(".forwardTask(): Register PetasosFulfillmentTask: Start");
        PetasosFulfillmentTaskSharedInstance petasosFulfillmentSharedInstance = fulfilmentTaskBroker.registerFulfillmentTask(petasosFulfillmentTask, false);
        getLogger().debug(".forwardTask(): Register PetasosFulfillmentTask: Finish");
        getLogger().debug(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: Start");
        String targetCamelEndpoint = routeName.getEndPointWUPContainerIngresProcessorIngres();
        getLogger().debug(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: targetCamelEndpoint->{}", targetCamelEndpoint);
        //
        // Create The TaskJobCard
        PetasosTaskJobCard petasosTaskJobCard = taskJobCardFactory.newPetasosTaskJobCard(petasosFulfillmentSharedInstance.getInstance());
        taskJobCardInstanceFactory.newTaskJobCardSharedInstanceAccessor(petasosTaskJobCard);
        //
        // Get out metricsAgent & do add some metrics
        WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
        metricsAgent.incrementInternalMessageDistributionCount();
        metricsAgent.touchLastActivityInstant();
        StringBuilder distributionMessageBuilder = new StringBuilder();
        distributionMessageBuilder.append("--- Distributing new PetasosFulfillmentTask ---");
        distributionMessageBuilder.append(" ("+ getTimeFormatter().format(Instant.now())+ ") ---\n");
        distributionMessageBuilder.append("TaskID (FulfillmentTask) --> " + petasosFulfillmentTask.getTaskId().getId() + "\n");
        distributionMessageBuilder.append("TaskID (ActionableTask) --> " + petasosFulfillmentTask.getActionableTaskId().getId() + "\n");
        distributionMessageBuilder.append("Target --> " + subscriber.getParticipantName() + "\n");
        metricsAgent.sendITOpsNotification(distributionMessageBuilder.toString());
        //
        // Forward the Task
        camelProducerService.sendBody(targetCamelEndpoint, ExchangePattern.InOnly, petasosFulfillmentSharedInstance);
        getLogger().debug(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: Finish");
    }

    private void tracePrintSubscribedWUPSet(Set<WorkUnitProcessorSoftwareComponent> wupSet) {
        getLogger().trace(".tracePrintSubscribedWUPSet(): Subscribed WUP Set --> {}", wupSet.size());
        Iterator<WorkUnitProcessorSoftwareComponent> tokenIterator = wupSet.iterator();
        while (tokenIterator.hasNext()) {
            getLogger().trace(".forwardUoW2WUPs(): Subscribed WUP Ingres Point --> {}", tokenIterator.next());
        }
    }
}
