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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.subscription;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalPetasosParticipantSubscriptionMapIM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class TaskSubscriptionCheck {

    private static final Logger LOG = LoggerFactory.getLogger(TaskSubscriptionCheck.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    LocalPetasosParticipantSubscriptionMapIM topicServer;

    @Inject
    private ProcessingPlantInterface myProcessingPlant;

    public List<PetasosParticipant> getSubscriberSet(DataParcelManifest parcelManifest){
        getLogger().debug(".getSubscriberSet(): Entry, parcelManifest->{}", parcelManifest);

        List<PetasosParticipant> subscriberSet = topicServer.getSubscriberSet(parcelManifest);
        getLogger().trace(".getSubscriberSet(): Before we do a general routing attempt, let's see if the message is directed somewhere specific");
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
        if(subscriberSet == null){
            getLogger().debug(".subscriberSet(): Exit, number of subscribers is 0");
            return(subscriberSet);
        }
        if(subscriberSet.isEmpty()){
            getLogger().debug(".subscriberSet(): Exit, number of subscribers is 0");
            return(subscriberSet);
        }
        //
        // Check to see if the message has a Target defined in the ParcelManifest: This ONLY works if the target
        // system has actually registered interest in getting the message anyhow, so a bit redundant from that
        // sense. It is mainly used a mechanism for restricting OTHER subscribers from getting the message.
        //
        if(hasIntendedTarget(parcelManifest)){
            getLogger().trace(".getSubscriberSet(): It's not empty, so let's see if the appropriate downstream system is registered");
            for(PetasosParticipant currentSubscriber: subscriberSet){
                if(hasRemoteServiceName(currentSubscriber)) {
                    String subscriberName = currentSubscriber.getSubsystemParticipantName();
                    if (subscriberName.contentEquals(parcelManifest.getIntendedTargetSystem())) {
                        subscriberSet.add(currentSubscriber);
                    }
                }
            }
        }
        //
        // TODO Need to add ability to look up remote participant for parcels that explicitly define an intended target
        // that isn't in the system
        //

        getLogger().trace(".getSubscriberSet(): Iterate through the subscribers");
        if(getLogger().isDebugEnabled()){
            getLogger().debug(".getSubscriberSet(): number of subscribers to this UoW->{}", subscriberSet.size());
        }

        if (subscriberSet != null) {
            getLogger().trace(".getSubscriberSet(): Iterating through....");
            for (PetasosParticipant currentSubscriber : subscriberSet) {
                getLogger().trace(".distributeNewFulfillmentTasks(): Iterating, currentSubscriber->{}", currentSubscriber);
                if (!subscriberSet.contains(currentSubscriber)) {
                    subscriberSet.add(currentSubscriber);
                }
            }
        }
        getLogger().debug(".getSubscriberSet(): Exit, number of subscribers data parcel->{}", subscriberSet.size());
        return(subscriberSet);
    }

    public boolean hasRemoteServiceName(PetasosParticipant subscriber){
        if(subscriber == null){
            return(false);
        }
        if(subscriber.getSubsystemParticipantName() == null){
            return(false);
        }
        if(subscriber.getSubsystemParticipantName().equals(myProcessingPlant.getSubsystemParticipantName())){
            return(false);
        }
        return(true);
    }

    public boolean hasIntendedTarget(DataParcelManifest parcelManifest){
        if(parcelManifest == null){
            return(false);
        }
        if(StringUtils.isEmpty(parcelManifest.getIntendedTargetSystem())){
            return(false);
        }
        return(true);
    }

    public boolean hasAtLeastOneSubscriber(DataParcelManifest parcelManifest){
        if(parcelManifest == null){
            return(false);
        }
        if(getSubscriberSet(parcelManifest).isEmpty()){
            return(false);
        }
        return(true);
    }
}
