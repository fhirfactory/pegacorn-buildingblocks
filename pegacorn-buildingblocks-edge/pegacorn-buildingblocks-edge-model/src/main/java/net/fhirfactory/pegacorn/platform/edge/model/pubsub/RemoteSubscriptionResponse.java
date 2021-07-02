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

import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubPublisher;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class RemoteSubscriptionResponse implements Serializable {
    private PubSubPublisher publisher;
    private boolean subscriptionSuccessful;
    private String subscriptionCommentary;

    private Date subscriptionRegistrationDate;

    public RemoteSubscriptionResponse(){
        super();
        this.publisher = null;
        this.subscriptionRegistrationDate = null;
    }

    public PubSubPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(PubSubPublisher publisher) {
        this.publisher = publisher;
    }

    public Date getSubscriptionRegistrationDate() {
        return subscriptionRegistrationDate;
    }

    public void setSubscriptionRegistrationDate(Date subscriptionRegistrationDate) {
        this.subscriptionRegistrationDate = subscriptionRegistrationDate;
    }

    public boolean isSubscriptionSuccessful() {
        return subscriptionSuccessful;
    }

    public void setSubscriptionSuccessful(boolean subscriptionSuccessful) {
        this.subscriptionSuccessful = subscriptionSuccessful;
    }

    public String getSubscriptionCommentary() {
        return subscriptionCommentary;
    }

    public void setSubscriptionCommentary(String subscriptionCommentary) {
        this.subscriptionCommentary = subscriptionCommentary;
    }

    @Override
    public String toString() {
        return "RemoteSubscriptionResponse{" +
                "publisher=" + publisher +
                ", subscriptionSuccessful=" + subscriptionSuccessful +
                ", subscriptionCommentary='" + subscriptionCommentary + '\'' +
                ", subscriptionRegistrationDate=" + subscriptionRegistrationDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteSubscriptionResponse)) return false;
        RemoteSubscriptionResponse that = (RemoteSubscriptionResponse) o;
        return isSubscriptionSuccessful() == that.isSubscriptionSuccessful() && Objects.equals(getPublisher(), that.getPublisher()) && Objects.equals(getSubscriptionCommentary(), that.getSubscriptionCommentary()) && Objects.equals(getSubscriptionRegistrationDate(), that.getSubscriptionRegistrationDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPublisher(), isSubscriptionSuccessful(), getSubscriptionCommentary(), getSubscriptionRegistrationDate());
    }
}
