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
package net.fhirfactory.pegacorn.petasos.endpoints.roles.common;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherRegistration;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;

import java.util.ArrayList;
import java.util.List;

public class MultiPublisherResponseSet {
    private List<DataParcelManifest> subscriptionList;
    private PubSubParticipant publisher;
    private RemoteSubscriptionResponse subscriptionRequestResponse;
    private InterSubsystemPubSubPublisherRegistration publisherRegistration;

    public MultiPublisherResponseSet(){
        this.subscriptionList = new ArrayList<>();
        this.subscriptionRequestResponse = null;
        this.publisher = null;
        this.publisherRegistration = null;
    }

    public List<DataParcelManifest> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(List<DataParcelManifest> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    public PubSubParticipant getPublisher() {
        return publisher;
    }

    public void setPublisher(PubSubParticipant publisher) {
        this.publisher = publisher;
    }

    public RemoteSubscriptionResponse getSubscriptionRequestResponse() {
        return subscriptionRequestResponse;
    }

    public void setSubscriptionRequestResponse(RemoteSubscriptionResponse subscriptionRequestResponse) {
        this.subscriptionRequestResponse = subscriptionRequestResponse;
    }

    public InterSubsystemPubSubPublisherRegistration getPublisherRegistration() {
        return publisherRegistration;
    }

    public void setPublisherRegistration(InterSubsystemPubSubPublisherRegistration publisherRegistration) {
        this.publisherRegistration = publisherRegistration;
    }
}
