
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
package net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.valuesets.PetasosSubscriptionSummaryTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.DataParcelManifestSubscriptionMaskType;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PetasosSubscriptionSummaryBase implements Serializable {
    private PetasosSubscriptionSummaryTypeEnum summaryType;
    private ComponentIdType componentID;
    private String participantName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant timestamp;
    private List<DataParcelManifestSubscriptionMaskType> subscribedTaskWorkItems;

    //
    // Constructor(s)
    //

    public PetasosSubscriptionSummaryBase(){
        this.summaryType = null;
        this.componentID = null;
        this.participantName = null;
        this.timestamp = null;
        this.subscribedTaskWorkItems = new ArrayList<>();
    }

    //
    // Getters and Setters
    //


    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public ComponentIdType getComponentID() {
        return componentID;
    }

    public void setComponentID(ComponentIdType componentID) {
        this.componentID = componentID;
    }

    public List<DataParcelManifestSubscriptionMaskType> getSubscribedTaskWorkItems() {
        return subscribedTaskWorkItems;
    }

    public void setSubscribedTaskWorkItems(List<DataParcelManifestSubscriptionMaskType> subscribedTaskWorkItems) {
        this.subscribedTaskWorkItems = subscribedTaskWorkItems;
    }

    @JsonIgnore
    public void addTopic(DataParcelManifestSubscriptionMaskType topic){
        boolean topicExists = false;
        if(!subscribedTaskWorkItems.contains(topic)){
            subscribedTaskWorkItems.add(topic);
        }
    }

    public PetasosSubscriptionSummaryTypeEnum getSummaryType() {
        return summaryType;
    }

    public void setSummaryType(PetasosSubscriptionSummaryTypeEnum summaryType) {
        this.summaryType = summaryType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosSubscriptionSummaryBase{" +
                "summaryType=" + summaryType +
                ", componentID=" + componentID +
                ", participantName='" + participantName + '\'' +
                ", timestamp=" + timestamp +
                ", subscribedTaskWorkItems=" + subscribedTaskWorkItems +
                '}';
    }
}
