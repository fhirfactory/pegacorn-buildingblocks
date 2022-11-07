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
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.participant.queue.PetasosParticipantTaskQueueStatus;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PetasosParticipantBase implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosParticipantBase.class);
    private PetasosParticipantId participantId;
    private ComponentIdType componentId;
    private PetasosParticipantStatusEnum participantStatus;
    private PetasosParticipantControlStatusEnum controlStatus;
    private Set<TaskWorkItemSubscriptionType> subscriptions;
    private Set<TaskWorkItemManifestType> outputs;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant utilisationUpdateInstant;
    private PetasosParticipantFulfillment fulfillmentState;
    private PetasosParticipantTaskQueueStatus taskQueueStatus;
    private SoftwareComponentTypeEnum componentType;

    //
    // Constructor(s)
    //

    public PetasosParticipantBase(){
        this.componentId = null;
        this.participantId = new PetasosParticipantId();
        this.participantStatus = PetasosParticipantStatusEnum.PARTICIPANT_IS_NOT_READY;
        this.utilisationUpdateInstant = null;
        this.subscriptions = new HashSet<>();
        this.outputs = new HashSet<>();
        this.fulfillmentState = new PetasosParticipantFulfillment();
        this.taskQueueStatus = new PetasosParticipantTaskQueueStatus();
        this.controlStatus = PetasosParticipantControlStatusEnum.PARTICIPANT_IS_SUSPENDED;
        this.componentType = null;
    }

    public PetasosParticipantBase(PetasosParticipantBase ori){
        this.componentId = null;
        this.participantId = new PetasosParticipantId();
        this.participantStatus = PetasosParticipantStatusEnum.PARTICIPANT_IS_NOT_READY;
        this.utilisationUpdateInstant = null;
        this.subscriptions = new HashSet<>();
        this.controlStatus = PetasosParticipantControlStatusEnum.PARTICIPANT_IS_SUSPENDED;
        this.outputs = new HashSet<>();
        this.componentType = null;
        this.taskQueueStatus = new PetasosParticipantTaskQueueStatus();

        if(ori.hasComponentId()){
            this.setComponentId(ori.getComponentId());
        }
        if(ori.hasParticipantStatus()) {
            this.setParticipantStatus(ori.getParticipantStatus());
        }
        if(ori.hasUtilisationUpdateInstant()) {
            this.setUtilisationUpdateInstant(ori.getUtilisationUpdateInstant());
        }
        if(!ori.getSubscriptions().isEmpty()){
            this.getSubscriptions().addAll(ori.getSubscriptions());
        }
        if(!ori.getOutputs().isEmpty()){
            this.getOutputs().addAll(ori.getOutputs());
        }
        if(ori.hasFulfillmentState()) {
            this.setFulfillmentState(ori.getFulfillmentState());
        } else {
            this.setFulfillmentState(new PetasosParticipantFulfillment());
        }
        if(ori.hasTaskQueueStatus()){
            this.setTaskQueueStatus(ori.getTaskQueueStatus());
        }
        if(ori.hasParticipantId()){
            setParticipantId(ori.getParticipantId());
        }
        setControlStatus(ori.getControlStatus());
        if(ori.getComponentType() != null){
            setComponentType(ori.getComponentType());
        }
    }

    public PetasosParticipantBase(SoftwareComponent ori){
        this.componentId = null;
        this.participantId = new PetasosParticipantId();
        this.participantStatus = PetasosParticipantStatusEnum.PARTICIPANT_IS_NOT_READY;;
        this.utilisationUpdateInstant = null;
        this.subscriptions = new HashSet<>();
        this.outputs = new HashSet<>();
        this.taskQueueStatus = null;
        this.controlStatus = PetasosParticipantControlStatusEnum.PARTICIPANT_IS_SUSPENDED;
        if(ori.hasParticipant()) {
            if(ori.hasComponentID()){
                this.setComponentId(ori.getComponentId());
            }
            if (ori.getParticipant().hasParticipantStatus()) {
                this.setParticipantStatus(ori.getParticipant().getParticipantStatus());
            }
            if (ori.getParticipant().hasUtilisationUpdateInstant()) {
                this.setUtilisationUpdateInstant(ori.getParticipant().getUtilisationUpdateInstant());
            }
            if (!ori.getParticipant().getSubscriptions().isEmpty()) {
                this.getSubscriptions().addAll(ori.getParticipant().getSubscriptions());
            }
            if (!ori.getParticipant().getOutputs().isEmpty()) {
                this.getOutputs().addAll(ori.getParticipant().getOutputs());
            }
            if (ori.getParticipant().hasFulfillmentState()) {
                this.setFulfillmentState(ori.getParticipant().getFulfillmentState());
            }
            if (ori.getParticipant().hasTaskQueueStatus()) {
                this.setTaskQueueStatus(ori.getParticipant().getTaskQueueStatus());
            }
            if (ori.getParticipant().hasParticipantId()) {
                setParticipantId(ori.getParticipant().getParticipantId());
            }
            setControlStatus(ori.getParticipant().getControlStatus());
            setComponentType(ori.getComponentType());
        }
    }

    //
    // Getters (and Setters)
    //

    @JsonIgnore
    public boolean hasComponentType(){
        boolean hasValue = this.componentType != null;
        return(hasValue);
    }

    public SoftwareComponentTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(SoftwareComponentTypeEnum componentType) {
        this.componentType = componentType;
    }

    @JsonIgnore
    public boolean hasControlStatus(){
        boolean hasValue = this.controlStatus != null;
        return(hasValue);
    }

    public PetasosParticipantControlStatusEnum getControlStatus() {
        return controlStatus;
    }

    public void setControlStatus(PetasosParticipantControlStatusEnum controlStatus) {
        this.controlStatus = controlStatus;
    }

    @JsonIgnore
    public boolean hasComponentId(){
        boolean hasValue = this.componentId != null;
        return(hasValue);
    }

    public ComponentIdType getComponentId() {
        return componentId;
    }

    public void setComponentId(ComponentIdType componentId) {
        this.componentId = componentId;
    }

    @JsonIgnore
    public boolean hasParticipantId(){
        boolean hasValue = this.participantId != null;
        return(hasValue);
    }

    public PetasosParticipantId getParticipantId() {
        return participantId;
    }

    public void setParticipantId(PetasosParticipantId participantId) {
        this.participantId = participantId;
    }

    @JsonIgnore
    public boolean hasTaskQueueStatus(){
        boolean hasValue = this.taskQueueStatus != null;
        return(hasValue);
    }

    public PetasosParticipantTaskQueueStatus getTaskQueueStatus() {
        return taskQueueStatus;
    }

    public void setTaskQueueStatus(PetasosParticipantTaskQueueStatus taskQueueStatus) {
        this.taskQueueStatus = taskQueueStatus;
    }

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

    protected Logger getLogger() {
        return (LOG);
    }

    @JsonIgnore
    public boolean hasParticipantStatus(){
        boolean hasValue = this.participantStatus != null;
        return(hasValue);
    }

    public void setParticipantStatus(PetasosParticipantStatusEnum status) {
        if(status == null){
            this.participantStatus = PetasosParticipantStatusEnum.PARTICIPANT_IS_NOT_READY;
        } else {
            this.participantStatus = status;
        }
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

    public Set<TaskWorkItemManifestType> getOutputs() {
        return outputs;
    }

    public void setOutputs(Set<TaskWorkItemManifestType> outputs) {
        this.outputs = outputs;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosParticipantBase{");
        sb.append("participantId=").append(participantId);
        sb.append(", componentId=").append(componentId);
        sb.append(", participantStatus=").append(participantStatus);
        sb.append(", subscriptions=").append(subscriptions);
        sb.append(", outputs=").append(outputs);
        sb.append(", utilisationUpdateInstant=").append(utilisationUpdateInstant);
        sb.append(", fulfillmentState=").append(fulfillmentState);
        sb.append(", taskQueueStatus=").append(taskQueueStatus);
        sb.append(", controlStatus=").append(controlStatus);
        sb.append('}');
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
        return getParticipantStatus() == that.getParticipantStatus() && Objects.equals(getSubscriptions(), that.getSubscriptions()) && Objects.equals(getOutputs(), that.getOutputs()) && Objects.equals(getUtilisationUpdateInstant(), that.getUtilisationUpdateInstant());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParticipantStatus(), getSubscriptions(), getOutputs(), getUtilisationUpdateInstant());
    }
}
