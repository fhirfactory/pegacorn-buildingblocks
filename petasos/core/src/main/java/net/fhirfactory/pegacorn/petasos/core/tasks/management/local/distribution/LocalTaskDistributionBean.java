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

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.IntraSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.subscriptions.manager.DataParcelSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosFulfillmentTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import org.apache.camel.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Mark A. Hunter
 */

@Dependent
public class LocalTaskDistributionBean {

    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskDistributionBean.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    DataParcelSubscriptionMapIM topicServer;

    @Inject
    TopologyIM topologyProxy;

    @Produce
    private ProducerTemplate camelProducerService;

    @Inject
    private PetasosFulfillmentTaskFactory fulfillmentTaskFactory;

    @Inject
    private LocalPetasosFulfilmentTaskActivityController fulfilmentTaskBroker;

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
    @RecipientList
    public PetasosActionableTask distributeNewFulfillmentTasks(PetasosActionableTask actionableTask, Exchange camelExchange) {
        getLogger().debug(".distributeNewFulfillmentTasks(): Entry, actionableTask (WorkUnitTransportPacket)->{}", actionableTask);

        //
        // Defensive Programming
        if(actionableTask == null){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task is null, returning an empty list for routing.");
            return(actionableTask);
        }
        if(!actionableTask.hasTaskWorkItem()){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task has no work item, returning an empty list for routing.");
            return(actionableTask);
        }
        if(!actionableTask.getTaskWorkItem().hasIngresContent()){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task has a work item with no ingres content, returning an empty list for routing.");
            return(actionableTask);
        }
        if(!actionableTask.getTaskWorkItem().getIngresContent().hasDataParcelQualityStatement()){
            getLogger().debug(".distributeNewFulfillmentTasks(): Exit, Ingres Actionable Task has a work item with no ingres content manifest, returning an empty list for routing.");
            return(actionableTask);
        }

        DataParcelManifest uowTopicID = actionableTask.getTaskWorkItem().getPayloadTopicID();

        getLogger().trace(".distributeNewFulfillmentTasks(): Getting the set of subscribers for the given topic (calling the topicServer)");
        getLogger().trace(".distributeNewFulfillmentTasks(): Looking for Subscribers To->{}:", uowTopicID);
        List<PubSubParticipant> subscriberSet = topicServer.getSubscriberSet(uowTopicID);
        getLogger().trace(".distributeNewFulfillmentTasks(): Before we do a general routing attempt, let's see if the message is directed somewhere specific");
        //
        // Because auditing is not running yet
        // Remove once Auditing is in place
        //
        if(getLogger().isWarnEnabled()) {
            int subscriberSetSize = 0;
            if (subscriberSet != null) {
                subscriberSetSize = subscriberSet.size();
            }
            getLogger().warn("Number of Subscribers->{}", subscriberSetSize);
        }
        //
        // Check to see if the message has a Target defined in the ParcelManifest
        //
        String alreadySentTo = "";
        if(!StringUtils.isEmpty(uowTopicID.getIntendedTargetSystem())){
            getLogger().trace(".distributeNewFulfillmentTasks(): It's not empty, so let's see if the appropriate downstream system is registered");
            for(PubSubParticipant currentSubscriber: subscriberSet){
                if(hasRemoteServiceName(currentSubscriber)) {
                    String subscriberName = currentSubscriber.getInterSubsystemParticipant().getEndpointServiceName();
                    if (subscriberName.contentEquals(uowTopicID.getIntendedTargetSystem())) {
                        forwardTask(currentSubscriber, actionableTask);
                        alreadySentTo = subscriberName;
                    }
                }
            }
        }

        getLogger().trace(".distributeNewFulfillmentTasks(): Iterate through the subscribers");
        if(getLogger().isDebugEnabled()){
            getLogger().debug(".distributeNewFulfillmentTasks(): number of subscribers to this UoW->{}", subscriberSet.size());
        }
        if (subscriberSet != null) {
            if(!subscriberSet.isEmpty()) {
                getLogger().trace(".distributeNewFulfillmentTasks(): Iterating through....");
                for (PubSubParticipant currentSubscriber : subscriberSet) {
                    getLogger().trace(".distributeNewFulfillmentTasks(): Iterating, currentSubscriber->{}", currentSubscriber);
                    boolean dontSendAgain = false;
                    if (hasRemoteServiceName(currentSubscriber)) {
                        getLogger().trace(".distributeNewFulfillmentTasks(): has Inter-Subsystem element");
                        if (currentSubscriber.getInterSubsystemParticipant().getEndpointServiceName().contentEquals(alreadySentTo)) {
                            dontSendAgain = true;
                        }
                    }
                    if (!dontSendAgain) {
                        getLogger().trace(".distributeNewFulfillmentTasks(): does not have Inter-Subsystem element");
                        forwardTask(currentSubscriber, actionableTask);
                    }
                }
            }
        }
        getLogger().debug(".distributeNewFulfillmentTasks(): Exiting");
        return (actionableTask);
    }

    private boolean hasRemoteServiceName(PubSubParticipant subscriber){
        if(subscriber == null){
            return(false);
        }
        if(subscriber.getInterSubsystemParticipant() == null){
            return(false);
        }
        if(subscriber.getInterSubsystemParticipant().getEndpointID() == null){
            return(false);
        }
        if(subscriber.getInterSubsystemParticipant().getEndpointServiceName() == null){
            return(false);
        }
        return(true);
    }

    private boolean hasIntendedTarget(DataParcelManifest parcelManifest){
        if(parcelManifest == null){
            return(false);
        }
        if(StringUtils.isEmpty(parcelManifest.getIntendedTargetSystem())){
            return(false);
        }
        return(true);
    }

    private void forwardTask(PubSubParticipant subscriber, PetasosActionableTask actionableTask){
        getLogger().debug(".forwardTask(): Subscriber --> {}", subscriber);
        IntraSubsystemPubSubParticipantIdentifier localSubscriberIdentifier = subscriber.getIntraSubsystemParticipant().getIdentifier();
        getLogger().debug(".forwardTask(): The (LocalSubscriber aspect) Identifier->{}", localSubscriberIdentifier);
        WorkUnitProcessorSoftwareComponent currentNodeElement = (WorkUnitProcessorSoftwareComponent)topologyProxy.getNode(localSubscriberIdentifier);
        getLogger().debug(".forwardTask(): The TopologyNode for the target currentNodeElement->{}", currentNodeElement);
        TopologyNodeFunctionFDNToken targetWUPFunctionToken = currentNodeElement.getNodeFunctionFDN().getFunctionToken();
        getLogger().debug(".forwardTask(): The WUPToken for the target targetWUPFunctionToken->{}", targetWUPFunctionToken);
        RouteElementNames routeName = new RouteElementNames(targetWUPFunctionToken);
        // Create FulfillmentTask and Inject into Target WUP
        getLogger().debug(".forwardTask(): Create actually PetasosFulfillmentTask: Start");
        PetasosFulfillmentTask petasosFulfillmentTask = fulfillmentTaskFactory.newFulfillmentTask(actionableTask, currentNodeElement);
        getLogger().trace(".forwardTask(): Create actually PetasosFulfillmentTask: petasosFulfillmentTask->{}", petasosFulfillmentTask);
        getLogger().debug(".forwardTask(): Create actually PetasosFulfillmentTask: Finish");
        // Now check if the Subscriber is actually a remote one! If so, ensure it has a proper "IntendedTarget" entry
        if(hasRemoteServiceName(subscriber)){
            getLogger().trace(".forwardTask(): Has Remote Service as Target");
            DataParcelManifest payloadTopicID = actionableTask.getTaskWorkItem().getPayloadTopicID();
            boolean hasEmptyIntendedTarget = StringUtils.isEmpty(payloadTopicID.getIntendedTargetSystem());
            boolean hasWildcardTarget = false;
            if(hasIntendedTarget(payloadTopicID)){
                hasWildcardTarget = payloadTopicID.getIntendedTargetSystem().contentEquals(DataParcelManifest.WILDCARD_CHARACTER);
            }
            boolean hasRemoteElement = hasRemoteServiceName(subscriber);
            getLogger().trace(".forwardTask(): hasEmptyIntendedTarget->{}, hasWildcardTarget->{}, hasRemoteElement->{} ", hasEmptyIntendedTarget, hasWildcardTarget, hasRemoteElement);
            if((hasEmptyIntendedTarget || hasWildcardTarget) && hasRemoteElement){
                petasosFulfillmentTask.getTaskWorkItem().getPayloadTopicID().setIntendedTargetSystem(subscriber.getInterSubsystemParticipant().getEndpointServiceName());
                getLogger().trace(".forwardTask(): Setting the intendedTargetSystem->{}", subscriber.getInterSubsystemParticipant().getEndpointServiceName());
            }
        }
        //
        // Register The FulfillmentTask
        getLogger().debug(".forwardTask(): Register PetasosFulfillmentTask: Start");
        fulfilmentTaskBroker.registerFulfillmentTask(petasosFulfillmentTask, false);
        getLogger().debug(".forwardTask(): Register PetasosFulfillmentTask: Finish");
        getLogger().debug(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: Start");
        String targetCamelEndpoint = routeName.getEndPointWUPContainerIngresProcessorIngres();
        getLogger().debug(".forwardTask(): Insert PetasosFulfillmentTask into Next WUP Ingress Processor: targetCamelEndpoint->{}", targetCamelEndpoint);
        camelProducerService.sendBody(targetCamelEndpoint, ExchangePattern.InOnly, petasosFulfillmentTask);
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
