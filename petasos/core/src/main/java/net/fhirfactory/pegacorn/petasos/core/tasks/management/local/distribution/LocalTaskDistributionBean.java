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
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosActionableTaskSharedInstance;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstance;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstanceAccessorFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosTaskJobCardSharedInstanceAccessorFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosFulfillmentTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosTaskJobCardFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.routing.LocalTaskDistributionDecisionEngine;
import net.fhirfactory.pegacorn.petasos.oam.metrics.collectors.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.participants.cache.LocalPetasosParticipantCacheDM;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
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
    private ProcessingPlantInterface myProcessingPlant;

    @Inject
    private LocalTaskDistributionDecisionEngine taskDistributionDecisionEngine;

    @Inject
    private PetasosFulfillmentTaskSharedInstanceAccessorFactory fulfillmentTaskSharedInstanceFactory;

    @Inject
    private PetasosTaskJobCardSharedInstanceAccessorFactory taskJobCardInstanceFactory;

    @Inject
    private PetasosTaskJobCardFactory taskJobCardFactory;


    @Inject
    private LocalPetasosParticipantCacheDM localPetasosParticipantCacheDM;

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

    public void distributeNewActionableTasks(PetasosActionableTaskSharedInstance actionableTask, Exchange camelExchange) {
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

        //
        // First, we need to get the target component (PerformerType) from the ActionableTask. Notionally, the
        // PerformerType is a list in the ActionableTask, but we only want the first one... :)
        if(actionableTask.getTaskPerformerTypes().isEmpty()){
            getLogger().warn(".distributeNewActionableTasks(): No Target To Deliver Task To!!!");
            return;
        }
        TaskPerformerTypeType targetComponent = actionableTask.getTaskPerformerTypes().get(0);
        String targetParticipantName = targetComponent.getRequiredParticipantName();
        PetasosParticipant targetParticipant = localPetasosParticipantCacheDM.getPetasosParticipant(targetParticipantName);
        if(targetParticipant == null){
            getLogger().warn(".distributeNewActionableTasks(): No Target To Deliver Task To!!!");
            return;
        }

        //
        // Now we can forward the task!
        if(getLogger().isDebugEnabled()){
            getLogger().debug(".distributeNewFulfillmentTasks(): Sending Task To->{}", targetComponent.getKnownFulfillerInstance());
        }
        forwardTask(targetParticipant, actionableTask, camelExchange);

        getLogger().debug(".distributeNewFulfillmentTasks(): Exiting");
        return;
    }



    private void forwardTask(PetasosParticipant subscriber, PetasosActionableTaskSharedInstance actionableTask, Exchange camelExchange){
        getLogger().debug(".forwardTask(): Subscriber --> {}", subscriber);
        // 1st check if the Subscriber is actually a remote one! If so, ensure it has a proper "IntendedTarget" entry
        boolean isRemoteTarget = false;
        String intendedTargetName = null;
        DataParcelManifest payloadTopicID = actionableTask.getTaskWorkItem().getPayloadTopicID();

        getLogger().trace(".forwardTask(): Assigning target component id, based on whether it is a remote component or local");
        if(isRemoteTarget){
            getLogger().warn(".forwardTask(): It is a remote target, so routing locally is impossible... why did i get it?");
        }
        getLogger().trace(".forwardTask(): The (LocalSubscriber aspect) Participant Name->{}", subscriber.getParticipantName());
        WorkUnitProcessorSoftwareComponent currentNodeElement = (WorkUnitProcessorSoftwareComponent)topologyProxy.getNode(subscriber.getParticipantName());
        getLogger().trace(".forwardTask(): The TopologyNode for the target currentNodeElement->{}", currentNodeElement);
        TopologyNodeFunctionFDNToken targetWUPFunctionToken = currentNodeElement.getNodeFunctionFDN().getFunctionToken();
        getLogger().trace(".forwardTask(): The WUPToken for the target targetWUPFunctionToken->{}", targetWUPFunctionToken);
        RouteElementNames routeName = new RouteElementNames(targetWUPFunctionToken);
        // Create FulfillmentTask and Inject into Target WUP
        getLogger().trace(".forwardTask(): Create actually PetasosFulfillmentTask: Start");
        PetasosFulfillmentTask petasosFulfillmentTask = fulfillmentTaskFactory.newFulfillmentTask(actionableTask.getInstance(), currentNodeElement);
        getLogger().trace(".forwardTask(): Create actually PetasosFulfillmentTask: petasosFulfillmentTask->{}", petasosFulfillmentTask);
        getLogger().trace(".forwardTask(): Create actually PetasosFulfillmentTask: Finish");
        petasosFulfillmentTask.getTaskWorkItem().getPayloadTopicID().setDestinationParticipant(subscriber.getParticipantId());
        //
        // Register The FulfillmentTask
        getLogger().trace(".forwardTask(): Register PetasosFulfillmentTask: Start");
        PetasosFulfillmentTaskSharedInstance petasosFulfillmentSharedInstance = fulfilmentTaskBroker.registerFulfillmentTask(petasosFulfillmentTask, false);
        getLogger().trace(".forwardTask(): Register PetasosFulfillmentTask: Finish");
        getLogger().trace(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: Start");
        String targetCamelEndpoint = routeName.getEndPointWUPContainerIngresProcessorIngres();
        getLogger().trace(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: targetCamelEndpoint->{}", targetCamelEndpoint);
        //
        // Create The TaskJobCard
        PetasosTaskJobCard petasosTaskJobCard = taskJobCardFactory.newPetasosTaskJobCard(petasosFulfillmentSharedInstance.getInstance());
        taskJobCardInstanceFactory.newTaskJobCardSharedInstanceAccessor(petasosTaskJobCard);
        //
        // Get out metricsAgent & do add some metrics
        /*
        WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
        metricsAgent.incrementInternalMessageDistributionCount();
        metricsAgent.touchLastActivityInstant();
         */
        //
        // Forward the Task
        if(getLogger().isDebugEnabled())
        {
            getLogger().debug(".forwardTask(): Forwarding To->{}, Task->{}",subscriber.getParticipantName(), petasosFulfillmentTask.getActionableTaskId().getId() );
        }

        camelProducerService.sendBody(targetCamelEndpoint, ExchangePattern.InOnly, petasosFulfillmentSharedInstance);
        getLogger().trace(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: Finish");
    }

    private void tracePrintSubscribedWUPSet(Set<WorkUnitProcessorSoftwareComponent> wupSet) {
        getLogger().trace(".tracePrintSubscribedWUPSet(): Subscribed WUP Set --> {}", wupSet.size());
        Iterator<WorkUnitProcessorSoftwareComponent> tokenIterator = wupSet.iterator();
        while (tokenIterator.hasNext()) {
            getLogger().trace(".forwardUoW2WUPs(): Subscribed WUP Ingres Point --> {}", tokenIterator.next());
        }
    }
}
