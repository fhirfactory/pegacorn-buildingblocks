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
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.common.PetasosSubscriptionSummaryBase;

public class PetasosPublisherSubscriptionSummary extends PetasosSubscriptionSummaryBase {
    private ComponentIdType subscriberComponentId;
    private String subscriberParticipantName;

    //
    // Constructor(s)
    //

    public PetasosPublisherSubscriptionSummary(){
        this.subscriberComponentId = null;
        this.subscriberParticipantName = null;
    }

    //
    // Getters and Setters
    //

    public ComponentIdType getSubscriberComponentId() {
        return subscriberComponentId;
    }

    public void setSubscriberComponentId(ComponentIdType subscriberComponentId) {
        this.subscriberComponentId = subscriberComponentId;
    }

    public String getSubscriberParticipantName() {
        return subscriberParticipantName;
    }

    public void setSubscriberParticipantName(String subscriberParticipantName) {
        this.subscriberParticipantName = subscriberParticipantName;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosPublisherSubscriptionSummary{" +
                "participantName='" + getParticipantName() + '\'' +
                ", componentID=" + getComponentID() +
                ", subscribedTaskWorkItems=" + getSubscribedTaskWorkItems() +
                ", summaryType=" + getSummaryType() +
                ", timestamp=" + getTimestamp() +
                ", subscriberComponentId=" + subscriberComponentId +
                ", subscriberParticipantName='" + subscriberParticipantName + '\'' +
                '}';
    }
}
