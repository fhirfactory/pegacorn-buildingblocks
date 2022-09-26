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
package net.fhirfactory.pegacorn.core.model.petasos.participant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PetasosParticipant extends SoftwareComponent implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosParticipant.class);

    private PetasosParticipantStatusEnum participantStatus;
    private Set<TaskWorkItemSubscriptionType> subscriptions;
    private Set<TaskWorkItemManifestType> publishedWorkItemManifests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant utilisationUpdateInstant;
    private PetasosParticipantFulfillment fulfillmentState;

    //
    // Constructor(s)
    //

    public PetasosParticipant(){
        super();
        this.participantStatus = PetasosParticipantStatusEnum.PETASOS_PARTICIPANT_STARTING;
        this.utilisationUpdateInstant = null;
        this.subscriptions = new HashSet<>();
        this.publishedWorkItemManifests = new HashSet<>();
        this.fulfillmentState = null;
    }

    public PetasosParticipant(PetasosParticipant ori){
        super(ori);

        this.participantStatus = PetasosParticipantStatusEnum.PETASOS_PARTICIPANT_STARTING;
        this.utilisationUpdateInstant = null;
        this.subscriptions = new HashSet<>();
        this.publishedWorkItemManifests = new HashSet<>();

        if(ori.hasParticipantStatus()) {
            this.setParticipantStatus(ori.getParticipantStatus());
        }
        if(ori.hasUtilisationUpdateInstant()) {
            this.setUtilisationUpdateInstant(ori.getUtilisationUpdateInstant());
        }
        if(!ori.getSubscriptions().isEmpty()){
            this.getSubscriptions().addAll(ori.getSubscriptions());
        }
        if(!ori.getPublishedWorkItemManifests().isEmpty()){
            this.getPublishedWorkItemManifests().addAll(ori.getPublishedWorkItemManifests());
        }
        if(ori.hasFulfillmentState()){
            this.setFulfillmentState(ori.getFulfillmentState());
        }
    }

    public PetasosParticipant(String participantName, SoftwareComponent ori){
        super(ori);
        this.participantStatus = PetasosParticipantStatusEnum.PETASOS_PARTICIPANT_STARTING;
        this.setParticipantName(participantName);
        this.utilisationUpdateInstant = null;
        this.subscriptions = new HashSet<>();
        this.publishedWorkItemManifests = new HashSet<>();
    }

    public PetasosParticipant(SoftwareComponent ori){
        super(ori);
        this.participantStatus = PetasosParticipantStatusEnum.PETASOS_PARTICIPANT_STARTING;;
        this.utilisationUpdateInstant = null;
        this.subscriptions = new HashSet<>();
        this.publishedWorkItemManifests = new HashSet<>();
    }

    //
    // Getters (and Setters)
    //

    public boolean hasFulfillmentState(){
        boolean hasValue = this.fulfillmentState != null;
        return(hasValue);
    }

    public PetasosParticipantFulfillment getFulfillmentState() {
        return fulfillmentState;
    }

    public void setFulfillmentState(PetasosParticipantFulfillment fulfillmentState) {
        this.fulfillmentState = fulfillmentState;
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @JsonIgnore
    public boolean hasParticipantStatus(){
        boolean hasValue = this.participantStatus != null;
        return(hasValue);
    }

    public void setParticipantStatus(PetasosParticipantStatusEnum connectionStatus) {
        this.participantStatus = connectionStatus;
    }

    public PetasosParticipantStatusEnum getParticipantStatus() {
        return participantStatus;
    }


    @JsonIgnore
    public boolean hasUtilisationUpdateInstant(){
        boolean hasValue = this.utilisationUpdateInstant != null;
        return(hasValue);
    }

    public void setUtilisationUpdateInstant(Instant utilisationUpdateInstant) {
        this.utilisationUpdateInstant = utilisationUpdateInstant;
    }

    public Instant getUtilisationUpdateInstant() {
        return utilisationUpdateInstant;
    }

    public Set<TaskWorkItemSubscriptionType> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<TaskWorkItemSubscriptionType> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Set<TaskWorkItemManifestType> getPublishedWorkItemManifests() {
        return publishedWorkItemManifests;
    }

    public void setPublishedWorkItemManifests(Set<TaskWorkItemManifestType> publishedWorkItemManifests) {
        this.publishedWorkItemManifests = publishedWorkItemManifests;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosParticipant{");
        sb.append("participantStatus=").append(participantStatus);
        sb.append(", subscriptions=").append(subscriptions);
        sb.append(", publishedWorkItemManifests=").append(publishedWorkItemManifests);
        sb.append(", utilisationUpdateInstant=").append(utilisationUpdateInstant);
        sb.append(", fulfillmentState=").append(fulfillmentState);
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }


    //
    // Hash and Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosParticipant)) return false;
        if (!super.equals(o)) return false;
        PetasosParticipant that = (PetasosParticipant) o;
        return getParticipantStatus() == that.getParticipantStatus() && Objects.equals(getSubscriptions(), that.getSubscriptions()) && Objects.equals(getPublishedWorkItemManifests(), that.getPublishedWorkItemManifests()) && Objects.equals(getUtilisationUpdateInstant(), that.getUtilisationUpdateInstant());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParticipantStatus(), getSubscriptions(), getPublishedWorkItemManifests(), getUtilisationUpdateInstant());
    }
}
