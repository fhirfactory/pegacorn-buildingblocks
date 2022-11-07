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
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentStatusEnum;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import org.apache.commons.lang3.SerializationUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PetasosParticipant extends PetasosParticipantBase {
    private SoftwareComponentStatusEnum localComponentStatus;
    private Set<ComponentIdType> instanceComponentIds;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant reportingInstant;
    private String registrationId;
    private PetasosParticipantRegistrationStatusEnum localRegistrationStatus;
    private PetasosParticipantRegistrationStatusEnum centralRegistrationStatus;
    private String registrationCommentary;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant localRegistrationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant centralRegistrationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant updateInstant;
    private SerializableObject lock;


    //
    // Constructor(s)
    //

    public PetasosParticipant(){
        super();
        this.localComponentStatus = SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_STATUS_UNKNOWN;
        this.instanceComponentIds = new HashSet<>();
        this.reportingInstant = Instant.EPOCH;
        this.localRegistrationStatus = PetasosParticipantRegistrationStatusEnum.PARTICIPANT_UNREGISTERED;
        this.localRegistrationInstant = Instant.EPOCH;
        this.registrationCommentary = null;
        this.centralRegistrationInstant = Instant.EPOCH;
        this.updateInstant = Instant.now();
        this.centralRegistrationStatus = PetasosParticipantRegistrationStatusEnum.PARTICIPANT_UNREGISTERED;
        this.lock = new SerializableObject();
        this.registrationId = UUID.randomUUID().toString();
    }

    public PetasosParticipant(PetasosParticipant ori){
        super(ori);
        this.localComponentStatus = SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_STATUS_UNKNOWN;
        this.instanceComponentIds = new HashSet<>();
        this.reportingInstant = Instant.EPOCH;
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
        if(ori.getRegistrationId() != null){
            setRegistrationId(ori.getRegistrationId());
        } else {
            this.registrationId = UUID.randomUUID().toString();
        }
        if(ori.getLocalRegistrationStatus() != null){
            setLocalRegistrationStatus(ori.getLocalRegistrationStatus());
        }
        if(ori.getLocalRegistrationInstant() != null){
            setLocalRegistrationInstant(ori.getLocalRegistrationInstant());
        }
        if(ori.getRegistrationCommentary() != null){
            setRegistrationCommentary(ori.getRegistrationCommentary());
        }
        if(ori.getCentralRegistrationInstant() != null){
            setCentralRegistrationInstant(ori.getCentralRegistrationInstant());
        }
        if(ori.getCentralRegistrationStatus() != null){
            setCentralRegistrationStatus(ori.getCentralRegistrationStatus());
        }
        this.lock = new SerializableObject();
    }

    //
    // Getters and Setters
    //

    public Instant getUpdateInstant() {
        return updateInstant;
    }

    public void setUpdateInstant(Instant updateInstant) {
        this.updateInstant = updateInstant;
    }

    public PetasosParticipantRegistrationStatusEnum getCentralRegistrationStatus() {
        return centralRegistrationStatus;
    }

    public void setCentralRegistrationStatus(PetasosParticipantRegistrationStatusEnum centralRegistrationStatus) {
        this.centralRegistrationStatus = centralRegistrationStatus;
    }

    public Instant getCentralRegistrationInstant() {
        return centralRegistrationInstant;
    }

    public void setCentralRegistrationInstant(Instant centralRegistrationInstant) {
        this.centralRegistrationInstant = centralRegistrationInstant;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public SerializableObject getLock() {
        return lock;
    }

    public void setLock(SerializableObject lock) {
        this.lock = lock;
    }

    public PetasosParticipantRegistrationStatusEnum getLocalRegistrationStatus() {
        return localRegistrationStatus;
    }

    public void setLocalRegistrationStatus(PetasosParticipantRegistrationStatusEnum localRegistrationStatus) {
        this.localRegistrationStatus = localRegistrationStatus;
    }

    public Instant getLocalRegistrationInstant() {
        return localRegistrationInstant;
    }

    public void setLocalRegistrationInstant(Instant localRegistrationInstant) {
        this.localRegistrationInstant = localRegistrationInstant;
    }

    public String getRegistrationCommentary() {
        return registrationCommentary;
    }

    public void setRegistrationCommentary(String registrationCommentary) {
        this.registrationCommentary = registrationCommentary;
    }


    public Instant getReportingInstant() {
        return reportingInstant;
    }

    public void setReportingInstant(Instant reportingInstant) {
        this.reportingInstant = reportingInstant;
    }


    @JsonIgnore
    public boolean hasLocalComponentId(){
        boolean hasValue = hasComponentId();
        return(hasValue);
    }

    public ComponentIdType getLocalComponentId() {
        return getComponentId();
    }

    public void setLocalComponentId(ComponentIdType localComponentId) {
        setComponentId(localComponentId);
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

    //
    // Update Methods
    //

    public PetasosParticipant updateFromCentralParticipant(PetasosParticipant centrallyPersistedParticipant){
        getLogger().debug(".updateFromCentralParticipant(): Entry, centrallyPersistedParticipant->{}",centrallyPersistedParticipant);
        if(centrallyPersistedParticipant == null){
            getLogger().debug(".updateFromCentralParticipant(): Exit, centrallyPersistedParticipant is null");
            return(this);
        }
        getLogger().trace(".updateFromCentralParticipant(): [Update Task Queue Status] Start");
        if(centrallyPersistedParticipant.hasTaskQueueStatus()){
            getLogger().trace(".updateFromCentralParticipant(): [Update Task Queue Status] has Updated Queue Status Details");
            getTaskQueueStatus().setCentralQueueStatus(centrallyPersistedParticipant.getTaskQueueStatus().getCentralQueueStatus());
            getTaskQueueStatus().setPendingTasksOffloaded(centrallyPersistedParticipant.getTaskQueueStatus().isPendingTasksOffloaded());
            getTaskQueueStatus().setPendingTasksPersisted(centrallyPersistedParticipant.getTaskQueueStatus().isPendingTasksPersisted());
        }
        getLogger().trace(".updateFromCentralParticipant(): [Update Task Queue Status] Start");
        getLogger().trace(".updateFromCentralParticipant(): [Update Control Status] Start");
        if(centrallyPersistedParticipant.hasControlStatus()){
            getLogger().trace(".updateFromCentralParticipant(): [Update Control Status] has Updated Control Status Details");
            setControlStatus(centrallyPersistedParticipant.getControlStatus());
        }
        getLogger().trace(".updateFromCentralParticipant(): [Update Control Status] End");
        if(centrallyPersistedParticipant.getCentralRegistrationInstant() != null){
            setCentralRegistrationInstant(centrallyPersistedParticipant.getCentralRegistrationInstant());
        }
        if(centrallyPersistedParticipant.getCentralRegistrationStatus() != null){
            setCentralRegistrationStatus(centrallyPersistedParticipant.getCentralRegistrationStatus());
        }
        if(centrallyPersistedParticipant.getSubscriptions() != null){
            getSubscriptions().clear();
            for(TaskWorkItemSubscriptionType currentSubscription: centrallyPersistedParticipant.getSubscriptions()){
                getSubscriptions().add(currentSubscription);
            }
        }
        getLogger().debug(".updateFromCentralParticipant(): Exit");
        return(this);
    }

    public PetasosParticipant updateFromLocalParticipant(PetasosParticipant updatedParticipant){
        getLogger().debug(".updateFromLocalParticipant(): Entry");
        if(updatedParticipant == null){
            return(this);
        }
        if(updatedParticipant.hasTaskQueueStatus()){
            getTaskQueueStatus().setLocalQueueStatus(updatedParticipant.getTaskQueueStatus().getLocalQueueStatus());
        }
        //if(updatedParticipant.hasControlStatus()){
        //    setControlStatus(updatedParticipant.getControlStatus());
        //}
        if(updatedParticipant.getLocalRegistrationInstant() != null){
            setLocalRegistrationInstant(updatedParticipant.getLocalRegistrationInstant());
        }
        if(updatedParticipant.getLocalRegistrationStatus() != null){
            setLocalRegistrationStatus(updatedParticipant.getLocalRegistrationStatus());
        }
        if(updatedParticipant.getSubscriptions() != null){
            for(TaskWorkItemSubscriptionType currentSubscription: updatedParticipant.getSubscriptions()){
                if(!getSubscriptions().contains(currentSubscription)) {
                    getSubscriptions().add(currentSubscription);
                }
            }
        }
        if(updatedParticipant.getOutputs() != null){
            for(TaskWorkItemManifestType currentOutput: updatedParticipant.getOutputs()){
                if(!getOutputs().contains(currentOutput)) {
                    getOutputs().add(currentOutput);
                }
            }
        }
        if(updatedParticipant.hasComponentId()){
            setComponentId(updatedParticipant.getComponentId());
        }
        if(updatedParticipant.hasFulfillmentState()){
            if(updatedParticipant.getFulfillmentState().getFulfillerComponents() != null){
                for(ComponentIdType currentComponentId: updatedParticipant.getFulfillmentState().getFulfillerComponents()){
                    if(!getFulfillmentState().getFulfillerComponents().contains(currentComponentId)){
                        getFulfillmentState().getFulfillerComponents().add(currentComponentId);
                    }
                }
            }
            if(updatedParticipant.getFulfillmentState().getFulfillmentStatus() != null){
                getFulfillmentState().setFulfillmentStatus(updatedParticipant.getFulfillmentState().getFulfillmentStatus());
            }
        }
        if(updatedParticipant.getParticipantStatus() != null){
            setParticipantStatus(updatedParticipant.getParticipantStatus());
        }
        if(updatedParticipant.getReportingInstant() != null){
            setReportingInstant(updatedParticipant.getReportingInstant());
        }
        return(this);
    }


    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosParticipant{");
        sb.append("componentType=").append(getComponentType());
        sb.append(", localComponentStatus=").append(localComponentStatus);
        sb.append(", instanceComponentIds=").append(instanceComponentIds);
        sb.append(", reportingInstant=").append(reportingInstant);
        sb.append(", registrationId='").append(registrationId).append('\'');
        sb.append(", localRegistrationStatus=").append(localRegistrationStatus);
        sb.append(", centralRegistrationStatus=").append(centralRegistrationStatus);
        sb.append(", registrationCommentary='").append(registrationCommentary).append('\'');
        sb.append(", localRegistrationInstant=").append(localRegistrationInstant);
        sb.append(", centralRegistrationInstant=").append(centralRegistrationInstant);
        sb.append(", updateInstant=").append(updateInstant);
        sb.append(", lock=").append(lock);
        sb.append(", controlStatus=").append(getControlStatus());
        sb.append(", componentId=").append(getComponentId());
        sb.append(", participantId=").append(getParticipantId());
        sb.append(", taskQueueStatus=").append(getTaskQueueStatus());
        sb.append(", fulfillmentState=").append(getFulfillmentState());
        sb.append(", participantStatus=").append(getParticipantStatus());
        sb.append(", utilisationUpdateInstant=").append(getUtilisationUpdateInstant());
        sb.append(", outputs=").append(getOutputs());
        sb.append('}');
        return sb.toString();
    }

    //
    // Hash and Equals overrides
    //


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosParticipant)) return false;
        if (!super.equals(o)) return false;
        PetasosParticipant that = (PetasosParticipant) o;
        return getComponentType() == that.getComponentType() && getLocalComponentStatus() == that.getLocalComponentStatus() && Objects.equals(getInstanceComponentIds(), that.getInstanceComponentIds()) && Objects.equals(getReportingInstant(), that.getReportingInstant()) && Objects.equals(getRegistrationId(), that.getRegistrationId()) && getLocalRegistrationStatus() == that.getLocalRegistrationStatus() && getCentralRegistrationStatus() == that.getCentralRegistrationStatus() && Objects.equals(getRegistrationCommentary(), that.getRegistrationCommentary()) && Objects.equals(getLocalRegistrationInstant(), that.getLocalRegistrationInstant()) && Objects.equals(getCentralRegistrationInstant(), that.getCentralRegistrationInstant()) && Objects.equals(getUpdateInstant(), that.getUpdateInstant()) && Objects.equals(getLock(), that.getLock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getComponentType(), getLocalComponentStatus(), getInstanceComponentIds(), getReportingInstant(), getRegistrationId(), getLocalRegistrationStatus(), getCentralRegistrationStatus(), getRegistrationCommentary(), getLocalRegistrationInstant(), getCentralRegistrationInstant(), getUpdateInstant(), getLock());
    }
}
