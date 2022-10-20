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
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.reporting.PetasosProcessingPlantSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.reporting.PetasosPublisherSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.reporting.PetasosSubscriberSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.reporting.PetasosWorkUnitProcessorSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.valuesets.PetasosSubscriptionSummaryTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.participants.cache.LocalParticipantRegistrationCache;
import net.fhirfactory.pegacorn.petasos.oam.subscriptions.cache.PetasosLocalSubscriptionReportingDM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class PetasosSubscriptionReportingAgent {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosSubscriptionReportingAgent.class);

    @Inject
    private PetasosLocalSubscriptionReportingDM subscriptionReportingDM;

    @Inject
    private LocalParticipantRegistrationCache participantRegistrationCache;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private TopologyIM topologyIM;

    //
    // Constructor(s)
    //

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }

    //
    // Business Methods
    //

    public void refreshLocalPubSubReportingMap(){
        getLogger().debug(".refreshLocalProcessingPlantPubSubMap(): Entry");
        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Get all SoftwareComponents(s)");
        Set<SoftwareComponent> allComponents = topologyIM.getNodeElementSet();
        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Create a ProcessingPlantSubscriptionSummary skeleton");
        PetasosProcessingPlantSubscriptionSummary processingPlantSubscriptionSummary = new PetasosProcessingPlantSubscriptionSummary();
        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Assign the ProcessingPlant ComponentID (processingPlantNode->{})", processingPlant.getTopologyNode());
        processingPlantSubscriptionSummary.setComponentID(processingPlant.getTopologyNode().getComponentId());
        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Assign the ProcessingPlant Participant ID (participantName->{})", processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
        processingPlantSubscriptionSummary.setParticipantName(processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Check if there are any subscriptions, if not, exit out");
        if(allComponents.isEmpty()){
            getLogger().debug(".refreshLocalProcessingPlantPubSubMap(): Exit, publisher service subscriptions is empty");
            return;
        }
        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): There are subscriptions, so processing them.");
        for(SoftwareComponent currentComponent: allComponents) {
            if(currentComponent.hasParticipant()) {
                if (getLogger().isInfoEnabled()) {
                    getLogger().debug(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Processing currentComponent->{}", currentComponent.getParticipant().getParticipantId());
                }
                if (currentComponent.getComponentType().equals(SoftwareComponentTypeEnum.PROCESSING_PLANT)) {
                    if(StringUtils.isNotEmpty(currentComponent.getParticipant().getParticipantId().getName())) {
                        if (currentComponent.getParticipant().getParticipantId().getName().equals(processingPlant.getTopologyNode().getParticipant().getParticipantId().getName())) {
                            //
                            // My ProcessingPlant as a Subscriber
                            for (TaskWorkItemSubscriptionType currentSubscription : currentComponent.getParticipant().getSubscriptions()) {
                                PetasosSubscriberSubscriptionSummary publisherSubscriptionSummary = new PetasosSubscriberSubscriptionSummary();
                                getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Registrations For currentSubscription->{}", currentSubscription);
                                publisherSubscriptionSummary.addTopic(currentSubscription);
                                getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Adding to SubscriberSummary List");

                                publisherSubscriptionSummary.setPublisherParticipantName(currentSubscription.getSourceProcessingPlantParticipantName());
                                publisherSubscriptionSummary.setParticipantName(processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
                                publisherSubscriptionSummary.setSummaryType(PetasosSubscriptionSummaryTypeEnum.PROCESSING_PLANT_SUBSCRIPTION_SUMMARY);
                                publisherSubscriptionSummary.setTimestamp(Instant.now());
                                processingPlantSubscriptionSummary.addSubscriberSummary(publisherSubscriptionSummary);
                            }
                        } else {
                            for (TaskWorkItemSubscriptionType currentSubscription : currentComponent.getParticipant().getSubscriptions()) {
                                if (currentSubscription.hasSourceProcessingPlantParticipantName()) {
                                    if (StringUtils.isNotEmpty(currentSubscription.getSourceProcessingPlantParticipantName())) {
                                        if (currentSubscription.getSourceProcessingPlantParticipantName().equals(processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName())) {
                                            getLogger().trace(".refreshLocalPubSubReportingMap(): Processing me as a Publisher/Producer");
                                            //
                                            // My ProcessingPlant as a Publisher
                                            PetasosPublisherSubscriptionSummary publisherSubscriptionSummary = new PetasosPublisherSubscriptionSummary();
                                            publisherSubscriptionSummary.setSubscriberParticipantName(currentComponent.getParticipant().getParticipantId().getName());
                                            publisherSubscriptionSummary.setComponentID(currentComponent.getComponentId());
                                            publisherSubscriptionSummary.setParticipantName(processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
                                            publisherSubscriptionSummary.setSummaryType(PetasosSubscriptionSummaryTypeEnum.PROCESSING_PLANT_SUBSCRIPTION_SUMMARY);
                                            publisherSubscriptionSummary.setTimestamp(Instant.now());
                                            getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Registrations For currentSubscription->{}", currentSubscription);
                                            publisherSubscriptionSummary.addTopic(currentSubscription);
                                            getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Adding to PublisherSummary List");
                                            processingPlantSubscriptionSummary.addPublisherSummary(publisherSubscriptionSummary);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //
                    // A WorkUnitProcessor as a Subscriber
                    getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Processing WUP Subscription");
                    PetasosWorkUnitProcessorSubscriptionSummary publisherSubscriptionSummary = new PetasosWorkUnitProcessorSubscriptionSummary();
                    publisherSubscriptionSummary.setComponentID(currentComponent.getComponentId());
                    publisherSubscriptionSummary.setSummaryType(PetasosSubscriptionSummaryTypeEnum.WORK_UNIT_PROCESSOR_SUMMARY);
                    publisherSubscriptionSummary.setParticipantName(currentComponent.getParticipant().getParticipantId().getName());
                    publisherSubscriptionSummary.setTimestamp(Instant.now());
                    for (TaskWorkItemSubscriptionType currentSubscription : currentComponent.getParticipant().getSubscriptions()) {
                        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Registrations For currentSubscription->{}", currentSubscription);
                        publisherSubscriptionSummary.addTopic(currentSubscription);
                        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Iterating:: Adding to SubscriberSummary List");
                    }
                    subscriptionReportingDM.addWorkUnitProcessorSubscriptionSummary(publisherSubscriptionSummary);
                }
            }
        }
        getLogger().trace(".refreshLocalProcessingPlantPubSubMap(): Add Summary Set to the ProcessingPlant cache");
        subscriptionReportingDM.addProcessingPlantSubscriptionSummary(processingPlantSubscriptionSummary);
        getLogger().debug(".refreshLocalProcessingPlantPubSubMap(): Exit");
    }
}
