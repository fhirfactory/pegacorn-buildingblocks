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
package net.fhirfactory.pegacorn.core.model.petasos.participant.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentStatusEnum;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashSet;
import java.util.Set;

public class PetasosParticipantRegistration extends PetasosParticipantRegistrationStatus{
    private PetasosParticipantId participantId;
    private ComponentIdType localComponentId;
    private SoftwareComponentTypeEnum componentType;
    private SoftwareComponentStatusEnum localComponentStatus;
    private Set<ComponentIdType> instanceComponentIds;
    private PetasosParticipantStatusEnum participantStatus;
    private PetasosParticipantControlStatusEnum controlStatus;
    private Set<TaskWorkItemSubscriptionType> subscriptions;
    private Set<TaskWorkItemManifestType> outputs;

    //
    // Constructor(s)
    //

    public PetasosParticipantRegistration(){
        super();
        this.componentType = null;
        this.participantId = null;
        this.localComponentId = null;
        this.localComponentStatus = null;
        this.controlStatus = null;
        this.instanceComponentIds = new HashSet<>();
        this.participantStatus = null;
        this.subscriptions = new HashSet<>();
        this.outputs = new HashSet<>();
    }

    public PetasosParticipantRegistration(PetasosParticipantRegistrationStatus registrationStatus){
        super(registrationStatus);
        this.componentType = null;
        this.participantId = null;
        this.localComponentId = null;
        this.localComponentStatus = null;
        this.controlStatus = null;
        this.instanceComponentIds = new HashSet<>();
        this.participantStatus = null;
        this.subscriptions = new HashSet<>();
        this.outputs = new HashSet<>();
    }

    public PetasosParticipantRegistration(PetasosParticipantRegistration ori){
        super(ori);
        this.componentType = null;
        this.participantId = null;
        this.localComponentId = null;
        this.localComponentStatus = null;
        this.instanceComponentIds = new HashSet<>();
        this.participantStatus = null;
        this.controlStatus = null;
        this.subscriptions = new HashSet<>();
        this.outputs = new HashSet<>();
        if(ori.hasComponentType()){
            setComponentType(ori.getComponentType());
        }
        if(ori.hasParticipantId()){
            setParticipantId(SerializationUtils.clone(ori.getParticipantId()));
        }
        if(ori.hasLocalComponentId()){
            setLocalComponentId(SerializationUtils.clone(ori.getLocalComponentId()));
        }
        if(ori.hasLocalComponentStatus()){
            setLocalComponentStatus(ori.getLocalComponentStatus());
        }
        if(!ori.getInstanceComponentIds().isEmpty()){
            for(ComponentIdType currentComponentId: ori.getInstanceComponentIds()){
                getInstanceComponentIds().add(SerializationUtils.clone(currentComponentId));
            }
        }
        if(ori.hasParticipantStatus()){
            setParticipantStatus(ori.getParticipantStatus());
        }
        if(!ori.getSubscriptions().isEmpty()){
            for(TaskWorkItemSubscriptionType currentSubscription: ori.getSubscriptions()){
                getSubscriptions().add(SerializationUtils.clone(currentSubscription));
            }
        }
        if(!ori.getOutputs().isEmpty()){
            for(TaskWorkItemManifestType currentOutput: ori.getOutputs()){
                getOutputs().add(SerializationUtils.clone(currentOutput));
            }
        }
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasConstrolStatus(){
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
    public boolean hasLocalComponentId(){
        boolean hasValue = this.localComponentId != null;
        return(hasValue);
    }

    public ComponentIdType getLocalComponentId() {
        return localComponentId;
    }

    public void setLocalComponentId(ComponentIdType localComponentId) {
        this.localComponentId = localComponentId;
    }

    @JsonIgnore
    public boolean hasLocalComponentStatus(){
        boolean hasValue = this.localComponentStatus != null;
        return(hasValue);
    }

    public SoftwareComponentStatusEnum getLocalComponentStatus() {
        return localComponentStatus;
    }

    public void setLocalComponentStatus(SoftwareComponentStatusEnum localComponentStatus) {
        this.localComponentStatus = localComponentStatus;
    }

    public Set<ComponentIdType> getInstanceComponentIds() {
        return instanceComponentIds;
    }

    public void setInstanceComponentIds(Set<ComponentIdType> instanceComponentIds) {
        this.instanceComponentIds = instanceComponentIds;
    }

    @JsonIgnore
    public boolean hasParticipantStatus(){
        boolean hasValue = this.participantStatus != null;
        return(hasValue);
    }

    public PetasosParticipantStatusEnum getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(PetasosParticipantStatusEnum participantStatus) {
        this.participantStatus = participantStatus;
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
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosParticipantRegistration{");
        sb.append("participantId=").append(participantId);
        sb.append(", localComponentId=").append(localComponentId);
        sb.append(", localComponentStatus=").append(localComponentStatus);
        sb.append(", instanceComponentIds=").append(instanceComponentIds);
        sb.append(", participantStatus=").append(participantStatus);
        sb.append(", controlStatus=").append(controlStatus);
        sb.append(", subscriptions=").append(subscriptions);
        sb.append(", outputs=").append(outputs);
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }
}
