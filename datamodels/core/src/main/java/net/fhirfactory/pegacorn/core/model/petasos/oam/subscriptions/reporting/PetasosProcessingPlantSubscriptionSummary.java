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
package net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.reporting;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PetasosProcessingPlantSubscriptionSummary implements Serializable {
    private Map<ComponentIdType, PetasosSubscriberSubscriptionSummary> asSubscriber;
    private Map<ComponentIdType, PetasosPublisherSubscriptionSummary> asPublisher;
    private ComponentIdType componentID;
    private String participantName;

    //
    // Constructor(s)
    //

    public PetasosProcessingPlantSubscriptionSummary(){
        asPublisher = new HashMap<>();
        asSubscriber = new HashMap<>();
        componentID = new ComponentIdType();
        componentID.setId("unknown");
        componentID.setDisplayName("unknown");
        this.participantName = null;
    }

    //
    // Getters and Setters
    //

    public Map<ComponentIdType, PetasosSubscriberSubscriptionSummary> getAsSubscriber() {
        return asSubscriber;
    }

    public void setAsSubscriber(Map<ComponentIdType, PetasosSubscriberSubscriptionSummary> asSubscriber) {
        this.asSubscriber = asSubscriber;
    }

    public Map<ComponentIdType, PetasosPublisherSubscriptionSummary> getAsPublisher() {
        return asPublisher;
    }

    public void setAsPublisher(Map<ComponentIdType, PetasosPublisherSubscriptionSummary> asPublisher) {
        this.asPublisher = asPublisher;
    }

    public ComponentIdType getComponentID() {
        return componentID;
    }

    public void setComponentID(ComponentIdType componentID) {
        this.componentID = componentID;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    //
    // Helper Methods
    //

    public void addPublisherSummary(PetasosPublisherSubscriptionSummary publisherSubscriptionSummary){
        if(asPublisher.containsKey(publisherSubscriptionSummary.getSubscriber())){
            asPublisher.remove(publisherSubscriptionSummary.getSubscriber());
        }
        asPublisher.put(publisherSubscriptionSummary.getSubscriber(), publisherSubscriptionSummary);
    }

    public void addSubscriberSummary(PetasosSubscriberSubscriptionSummary subscriberSubscriptionSummary){
        if(asSubscriber.containsKey(subscriberSubscriptionSummary.getPublisher())){
            asSubscriber.remove(subscriberSubscriptionSummary.getPublisher());
        }
        asSubscriber.put(subscriberSubscriptionSummary.getPublisher(), subscriberSubscriptionSummary);
    }

    public boolean addSubscriptionForExistingSubscriber(ComponentIdType componentID, String topic){
        if(asPublisher.containsKey(componentID)){
            asPublisher.get(componentID).addTopic(topic);
            return(true);
        } else {
            return(false);
        }
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosProcessingPlantSubscriptionSummary{" +
                "asSubscriber=" + asSubscriber +
                ", asPublisher=" + asPublisher +
                ", componentID=" + componentID +
                ", participantName='" + participantName + '\'' +
                '}';
    }
}
