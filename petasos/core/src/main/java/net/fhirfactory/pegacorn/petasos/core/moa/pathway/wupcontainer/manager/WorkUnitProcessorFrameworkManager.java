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
package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.manager;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.archetypes.ExternalEgressWUPContainerRoute;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.archetypes.ExternalIngresWUPContainerRoute;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.archetypes.StandardWUPContainerRoute;
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalPetasosParticipantSubscriptionMapIM;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Mark A. Hunter
 */

@ApplicationScoped
public class WorkUnitProcessorFrameworkManager {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorFrameworkManager.class);

    @Inject
    private CamelContext camelctx;

    @Inject
    private LocalPetasosParticipantSubscriptionMapIM topicServer;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosFulfillmentTaskAuditServicesBroker auditBroker;

    public void buildWUPFramework(WorkUnitProcessorSoftwareComponent wupNode, List<DataParcelManifest> subscribedTopics, WUPArchetypeEnum wupArchetype, WorkUnitProcessorMetricsAgent metricsAgent) {
        LOG.debug(".buildWUPFramework(): Entry, wupNode --> {}, subscribedTopics --> {}, wupArchetype --> {}", wupNode, subscribedTopics, wupArchetype);
        try {
            switch (wupArchetype) {

                case WUP_NATURE_STIMULI_TRIGGERED_WORKFLOW: {
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_STIMULI_TRIGGERED_BEHAVIOUR route");
                    StandardWUPContainerRoute standardWUPRoute = new StandardWUPContainerRoute(camelctx, wupNode, auditBroker, true, metricsAgent);
                    LOG.trace(".buildWUPFramework(): Route created, now adding it to he CamelContext!");
                    camelctx.addRoutes(standardWUPRoute);
                    break;
                }
                case WUP_NATURE_TIMER_TRIGGERED_WORKFLOW: {
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_LADON_TIMER_TRIGGERED_BEHAVIOUR route");
                    ExternalIngresWUPContainerRoute ingresRoute = new ExternalIngresWUPContainerRoute(camelctx, wupNode, auditBroker, metricsAgent);
                    camelctx.addRoutes(ingresRoute);
                    break;
                }
                case WUP_NATURE_LADON_BEHAVIOUR_WRAPPER:
                case WUP_NATURE_LADON_STANDARD_MOA: {
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_LADON_STANDARD_MOA route");
                    StandardWUPContainerRoute standardWUPRoute = new StandardWUPContainerRoute(camelctx, wupNode, auditBroker, true, metricsAgent);
                    LOG.trace(".buildWUPFramework(): Route created, now adding it to he CamelContext!");
                    camelctx.addRoutes(standardWUPRoute);
                    break;
                }
                case WUP_NATURE_MESSAGE_WORKER: {
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_MESSAGE_WORKER route");
                    StandardWUPContainerRoute standardWUPRoute = new StandardWUPContainerRoute(camelctx, wupNode, auditBroker, metricsAgent);
                    LOG.trace(".buildWUPFramework(): Route created, now adding it to he CamelContext!");
                    camelctx.addRoutes(standardWUPRoute);
                    break;
                }
                case WUP_NATURE_API_PUSH:
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_API_PUSH route");
                    ExternalIngresWUPContainerRoute ingresRouteForAPIPush = new ExternalIngresWUPContainerRoute(camelctx, wupNode, auditBroker, metricsAgent);
                    camelctx.addRoutes(ingresRouteForAPIPush);
                    break;
                case WUP_NATURE_API_ANSWER:
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_API_ANSWER route");
                    break;
                case WUP_NATURE_API_RECEIVE:
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_API_RECEIVE route");
                    break;
                case WUP_NATURE_MESSAGE_EXTERNAL_EGRESS_POINT:
                case WUP_NATURE_API_CLIENT:
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_MESSAGE_EXTERNAL_EGRESS_POINT route");
                    ExternalEgressWUPContainerRoute egressRoute = new ExternalEgressWUPContainerRoute(camelctx, wupNode, auditBroker, metricsAgent);
                    camelctx.addRoutes(egressRoute);
                    break;
                case WUP_NATURE_MESSAGE_EXTERNAL_INGRES_POINT:
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_MESSAGE_EXTERNAL_INGRES_POINT route");
                    ExternalIngresWUPContainerRoute ingresRoute = new ExternalIngresWUPContainerRoute(camelctx, wupNode, auditBroker, metricsAgent);
                    camelctx.addRoutes(ingresRoute);
                    break;
                case WUP_NATURE_MESSAGE_EXTERNAL_CONCURRENT_INGRES_POINT:
                    LOG.trace(".buildWUPFramework(): Building a WUP_NATURE_MESSAGE_EXTERNAL_CONCURRENT_INGRES_POINT route");
            }
        } catch (Exception Ex) {
            // TODO We really must handle this exception, either by cancelling the whole Processing Plant or, at least, raising an alarm
        }
    }

    /*
    public void uowTopicSubscribe(List<DataParcelManifest> subscribedTopics, WorkUnitProcessorSoftwareComponent wupNode) {
        LOG.debug(".uowTopicSubscribe(): Entry, subscribedTopics --> {}, wupNode --> {}", subscribedTopics, wupNode);
        if (subscribedTopics.isEmpty()) {
            LOG.debug(".uowTopicSubscribe(): Something's wrong, no Topics are subscribed for this WUP");
            return;
        }
        for(DataParcelManifest currentTopicID: subscribedTopics) {
            LOG.trace(".uowTopicSubscribe(): wupNode --> {} is subscribing to UoW Content Topic --> {}", wupNode, currentTopicID);
            PetasosParticipant subscriber = new PetasosParticipant(wupNode);
            if(!subscriber.hasProcessingPlantServiceName()){
                subscriber.setProcessingPlantServiceName(processingPlant.getIPCServiceName());
            }
            for(DataParcelManifest currentParcelManifest: subscribedTopics){
                TaskWorkItemManifestType workItemManifest = new TaskWorkItemManifestType(currentParcelManifest);
                workItemManifest.setTaskProducerProcessingPlantServiceName(processingPlant.getIPCServiceName());
                if(subscriber.getSubscribedWorkItemManifests().contains(workItemManifest)) {
                    // Do nothing
                } else {
                    subscriber.getSubscribedWorkItemManifests().add(workItemManifest);
                }
            }
            topicServer.addTopicSubscriber(currentTopicID, subscriber);
        }
        LOG.debug(".uowTopicSubscribe(): Exit");
    }

    private PubSubParticipant constructPubSubSubscriber(WorkUnitProcessorSoftwareComponent wupNode){
        PubSubParticipant subscriber = new PubSubParticipant();
        IntraSubsystemPubSubParticipant localSubscriber = new IntraSubsystemPubSubParticipant();
        IntraSubsystemPubSubParticipantIdentifier identifier = new IntraSubsystemPubSubParticipantIdentifier(wupNode.getComponentID());
        localSubscriber.setIdentifier(identifier);
        subscriber.setIntraSubsystemParticipant(localSubscriber);
        return(subscriber);
    }

     */
}
