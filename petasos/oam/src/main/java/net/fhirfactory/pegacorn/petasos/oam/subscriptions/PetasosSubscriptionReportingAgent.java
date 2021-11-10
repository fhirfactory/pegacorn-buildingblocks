/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.oam.subscriptions;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosProcessingPlantSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosPublisherSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosSubscriberSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosWorkUnitProcessorSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.valuesets.PetasosSubscriptionSummaryTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.InterSubsystemPubSubPublisherRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubSubscription;
import net.fhirfactory.pegacorn.petasos.core.subscriptions.manager.DataParcelSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.core.subscriptions.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.oam.subscriptions.cache.PetasosLocalSubscriptionReportingDM;
import net.fhirfactory.pegacorn.petasos.oam.subscriptions.factories.PetasosTopicSummaryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetasosSubscriptionReportingAgent {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosSubscriptionReportingAgent.class);

    @Inject
    private PetasosLocalSubscriptionReportingDM subscriptionReportingDM;

    @Inject
    private DataParcelSubscriptionMapIM subscriptionMapIM;

    @Inject
    private DistributedPubSubSubscriptionMapIM distributedSubscriptionMapIM;

    @Inject
    private PetasosTopicSummaryFactory topicSummaryFactory;

    @Inject
    private ProcessingPlantInterface processingPlant;

    public void refreshLocalProcessingPlantPubSubMap(){
        LOG.debug(".refreshLocalProcessingPlantPubSubMap(): Entry");
        LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Get all InterSubsystemPubSubPublisherSubscriptionRegistration(s)");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> allPublisherServiceSubscriptions = distributedSubscriptionMapIM.getAllPublisherServiceSubscriptions();
        LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Create a ProcessingPlantSubscriptionSummary skeleton");
        PetasosProcessingPlantSubscriptionSummary processingPlantSubscriptionSummary = new PetasosProcessingPlantSubscriptionSummary();
        LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Assign the ProcessintPlant ComponentID (processingPlantNode->{})", processingPlant.getProcessingPlantNode());
        processingPlantSubscriptionSummary.setComponentID(processingPlant.getProcessingPlantNode().getComponentID());
        LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Check if there are any subscriptions, if not, exit out");
        if(allPublisherServiceSubscriptions.isEmpty()){
            LOG.debug(".refreshLocalProcessingPlantPubSubMap(): Exit, publisher service subscriptions is empty");
            return;
        }
        LOG.trace(".refreshLocalProcessingPlantPubSubMap(): There are subscriptions, so processing them.");
        LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Iterate Through Registrations");
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentRegistration: allPublisherServiceSubscriptions){
            String publisherServiceName = currentRegistration.getPublisherServiceName();
            LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Registrations For Service Name->{}", publisherServiceName);
            List<InterSubsystemPubSubPublisherRegistration> publisherServiceProviderInstanceRegistrations = distributedSubscriptionMapIM.getPublisherServiceProviderInstanceRegistrations(publisherServiceName);
            for(InterSubsystemPubSubPublisherRegistration currentPublisherRegistration: publisherServiceProviderInstanceRegistrations) {
                ComponentIdType processingPlantComponentID = currentPublisherRegistration.getPublisher().getEndpointID().getProcessingPlantComponentID();
                LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Registrations For ProcessingPlant Name->{}", processingPlantComponentID);
                PetasosSubscriberSubscriptionSummary publisherSubscriptionSummary = new PetasosSubscriberSubscriptionSummary();
                publisherSubscriptionSummary.setPublisherServiceName(publisherServiceName);
                publisherSubscriptionSummary.setTimestamp(Instant.from(currentRegistration.getRegistrationDate().toInstant()));
                publisherSubscriptionSummary.setSummaryType(PetasosSubscriptionSummaryTypeEnum.PROCESSING_PLANT_SUBSCRIPTION_SUMMARY);
                publisherSubscriptionSummary.setPublisher(processingPlantComponentID);
                for(DataParcelManifest currentManifest: currentRegistration.getSubscriptionList()){
                    String simpleTopicName = topicSummaryFactory.transformToSimpleTopicName(currentManifest);
                    LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Adding Topic->{}", simpleTopicName);
                    publisherSubscriptionSummary.getSubscribedTopics().add(simpleTopicName);
                }
                LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Adding to SubscriberSummary List");
                processingPlantSubscriptionSummary.addSubscriberSummary(publisherSubscriptionSummary);
            }
        }
        LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Create Summary Set");
        List<PubSubSubscription> allSubscriptions = subscriptionMapIM.getAllSubscriptions();
        for(PubSubSubscription currentSubscription: allSubscriptions){
            if(currentSubscription.getSubscriber().getInterSubsystemParticipant() != null){
                ComponentIdType processingPlantID = currentSubscription.getSubscriber().getInterSubsystemParticipant().getEndpointID().getProcessingPlantComponentID();
                String topic = topicSummaryFactory.transformToSimpleTopicName(currentSubscription.getParcelManifest());
                boolean added = processingPlantSubscriptionSummary.addSubscriptionForExistingSubscriber(processingPlantID, topic);
                if(!added){
                    PetasosPublisherSubscriptionSummary subscriberSummary = new PetasosPublisherSubscriptionSummary();
                    subscriberSummary.setSubscriber(processingPlantID);
                    subscriberSummary.setSummaryType(PetasosSubscriptionSummaryTypeEnum.PROCESSING_PLANT_SUBSCRIPTION_SUMMARY);
                    subscriberSummary.setTimestamp(currentSubscription.getRegistrationInstant());
                    subscriberSummary.setSubscriberServiceName(currentSubscription.getSubscriber().getInterSubsystemParticipant().getEndpointServiceName());
                    subscriberSummary.addTopic(topic);
                    subscriberSummary.setComponentID(processingPlantID);
                    processingPlantSubscriptionSummary.addPublisherSummary(subscriberSummary);
                }
            }
        }
        LOG.trace(".refreshLocalProcessingPlantPubSubMap(): Add Summary Set to the ProcessingPlant cache");
        subscriptionReportingDM.addProcessingPlantSubscriptionSummary(processingPlantSubscriptionSummary);
        LOG.debug(".refreshLocalProcessingPlantPubSubMap(): Exit");
    }

    public void refreshWorkUnitProcessorPubSubMap(){
        LOG.debug(".refreshWorkUnitProcessorPubSubMap(): Entry");
        List<PubSubSubscription> allSubscriptions = subscriptionMapIM.getAllSubscriptions();
        Map<ComponentIdType, PetasosWorkUnitProcessorSubscriptionSummary> summaries = new HashMap<>();
        for(PubSubSubscription currentSubscription: allSubscriptions){
            if(summaries.containsKey(currentSubscription.getSubscriber().getIntraSubsystemParticipant().getIdentifier())){
                PetasosWorkUnitProcessorSubscriptionSummary currentSummary = summaries.get(currentSubscription.getSubscriber().getIntraSubsystemParticipant().getIdentifier());
                currentSummary.addTopic(topicSummaryFactory.transformToSimpleTopicName(currentSubscription.getParcelManifest()));
            } else {
                PetasosWorkUnitProcessorSubscriptionSummary currentSummary = new PetasosWorkUnitProcessorSubscriptionSummary();
                currentSummary.setSummaryType(PetasosSubscriptionSummaryTypeEnum.WORK_UNIT_PROCESSOR_SUMMARY);
                currentSummary.setSubscriber(currentSubscription.getSubscriber().getIntraSubsystemParticipant().getIdentifier());
                currentSummary.setTimestamp(currentSubscription.getRegistrationInstant());
                currentSummary.addTopic(topicSummaryFactory.transformToSimpleTopicName(currentSubscription.getParcelManifest()));
                currentSummary.setComponentID(currentSubscription.getSubscriber().getIntraSubsystemParticipant().getIdentifier());
                summaries.put(currentSummary.getSubscriber(), currentSummary);
            }
        }
        if(!summaries.isEmpty()){
            for(PetasosWorkUnitProcessorSubscriptionSummary currentSummary: summaries.values()){
                subscriptionReportingDM.addWorkUnitProcessorSubscriptionSummary(currentSummary);
            }
        }
        LOG.debug(".refreshWorkUnitProcessorPubSubMap(): Exit");
    }
}
