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
package net.fhirfactory.pegacorn.platform.edge.model.pubsub;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RemoteSubscriptionRequest implements Serializable {
    private PubSubParticipant subscriber;
    private List<DataParcelManifest> subscriptionList;
    private Date subscriptionRequestDate;

    public RemoteSubscriptionRequest(){
        super();
        this.subscriptionRequestDate = null;
    }

    public Date getSubscriptionRequestDate() {
        return subscriptionRequestDate;
    }

    public void setSubscriptionRequestDate(Date subscriptionRequestDate) {
        this.subscriptionRequestDate = subscriptionRequestDate;
    }

    public PubSubParticipant getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(PubSubParticipant subscriber) {
        this.subscriber = subscriber;
    }

    public List<DataParcelManifest> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(List<DataParcelManifest> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    @Override
    public String toString() {
        return "RemoteSubscriptionRequest{" +
                "subscriber=" + subscriber +
                ", subscriptionList=" + subscriptionList +
                ", subscriptionRequestDate=" + subscriptionRequestDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteSubscriptionRequest)) return false;
        RemoteSubscriptionRequest that = (RemoteSubscriptionRequest) o;
        return Objects.equals(getSubscriber(), that.getSubscriber()) && Objects.equals(getSubscriptionList(), that.getSubscriptionList()) && Objects.equals(getSubscriptionRequestDate(), that.getSubscriptionRequestDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubscriber(), getSubscriptionList(), getSubscriptionRequestDate());
    }
}
