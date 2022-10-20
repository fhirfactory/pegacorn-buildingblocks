/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.jobcard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.datatypes.PetasosTaskFulfillmentCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskStorageType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;


public class PetasosTaskJobCard implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskJobCard.class);
    private TaskIdType taskId;
    private TaskStorageType persistenceStatus;
    private ComponentIdType affinityNode;
    private TaskOutcomeStatusEnum outcomeStatus;
    private PetasosTaskFulfillmentCard taskFulfillmentCard;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant updateInstant;

    private TaskExecutionCommandEnum currentStatus;
    private TaskExecutionCommandEnum grantedStatus;
    private ConcurrencyModeEnum clusterMode;
    private ResilienceModeEnum systemMode;

    //
    // Constructor(s)
    //

    public PetasosTaskJobCard(){
        this.taskId = null;
        this.updateInstant = null;
        this.currentStatus = TaskExecutionCommandEnum.TASK_COMMAND_WAIT;
        this.clusterMode = ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE;
        this.systemMode = ResilienceModeEnum.RESILIENCE_MODE_STANDALONE;
        this.grantedStatus = TaskExecutionCommandEnum.TASK_COMMAND_WAIT;
        this.affinityNode = null;
        this.persistenceStatus = null;
        this.outcomeStatus = TaskOutcomeStatusEnum.OUTCOME_STATUS_WAITING;
        this.taskFulfillmentCard = null;
    }

    public PetasosTaskJobCard(
            TaskIdType fulfillmentTaskId,
            PetasosParticipantId fulfillerParticipantId,
            TaskIdType taskId,
            TaskExecutionCommandEnum currentStatus,
            TaskExecutionCommandEnum grantedStatus,
            ConcurrencyModeEnum clusterMode,
            ResilienceModeEnum systemMode,
            Instant fulfillmentStartInstant) {
        //
        // Clear the deck
        this.taskFulfillmentCard = null;
        this.taskId = null;
        this.updateInstant = null;
        this.currentStatus = null;
        this.clusterMode = null;
        this.systemMode = null;
        this.grantedStatus = null;
        this.affinityNode = null;
        this.persistenceStatus = new TaskStorageType();
        //
        // Assign provided values
        if ((fulfillmentTaskId == null || fulfillerParticipantId == null)) {
            throw (new IllegalArgumentException("fulfillmentTaskId or fulfillerParticipantId is null in Constructor"));
        } else{
            setTaskFulfillmentCard(new PetasosTaskFulfillmentCard(fulfillmentTaskId, fulfillerParticipantId));
        }
        if(taskId == null) {
            throw (new IllegalArgumentException("Actionable Task Id (taskId) is null in constructor"));
        } else {
            setTaskId(taskId);
        }
        setCurrentStatus(currentStatus);
        setGrantedStatus(grantedStatus);
        setClusterMode(clusterMode);
        setSystemMode(systemMode);
        getTaskFulfillmentCard().setFulfillmentStartInstant(fulfillmentStartInstant);
    }

    public PetasosTaskJobCard(PetasosTaskJobCard ori) {
        //
        // Clear the deck
        this.taskFulfillmentCard = null;
        this.taskId = null;
        this.updateInstant = null;
        this.currentStatus = null;
        this.clusterMode = null;
        this.systemMode = null;
        this.grantedStatus = null;
        this.persistenceStatus = null;
        //
        // Assign provided values
        if(ori.hasTaskId()){
            setTaskId(SerializationUtils.clone(ori.getTaskId()));
        }
        if(ori.hasTaskFufillmentCard()){
            setTaskFulfillmentCard(SerializationUtils.clone(ori.getTaskFulfillmentCard()));
        } else {
            setTaskFulfillmentCard(new PetasosTaskFulfillmentCard());
        }
        if(ori.hasClusterMode()){
            setClusterMode(ori.getClusterMode());
        } else {
            setClusterMode(ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        }
        if(ori.hasCurrentStatus()){
            setCurrentStatus(ori.getCurrentStatus());
        } else {
            setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        }
        if(ori.hasGrantedStatus()){
            setGrantedStatus(ori.getGrantedStatus());
        } else {
            setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        }
        if(ori.hasSystemMode()){
            setSystemMode(ori.getSystemMode());
        } else {
            setSystemMode(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        }
        if(ori.hasUpdateInstant()){
            setUpdateInstant(SerializationUtils.clone(ori.getUpdateInstant()));
        } else {
            setUpdateInstant(Instant.now());
        }
        if(ori.hasPersistenceStatus()){
            setPersistenceStatus(ori.getPersistenceStatus());
        } else {
            setPersistenceStatus(new TaskStorageType());
        }
    }

    //
    // Getters (and Setters)
    //

    @JsonIgnore
    public boolean hasPersistenceStatus(){
        boolean hasValue = this.persistenceStatus != null;
        return(hasValue);
    }

    public TaskStorageType getPersistenceStatus() {
        return persistenceStatus;
    }

    public void setPersistenceStatus(TaskStorageType persistenceStatus) {
        this.persistenceStatus = persistenceStatus;
    }

    @JsonIgnore
    public boolean hasOutcomeStatus(){
        boolean hasValue = this.outcomeStatus != null;
        return(hasValue);
    }

    public TaskOutcomeStatusEnum getOutcomeStatus() {
        return outcomeStatus;
    }

    public void setOutcomeStatus(TaskOutcomeStatusEnum outcomeStatus) {
        this.outcomeStatus = outcomeStatus;
    }

    @JsonIgnore
    public boolean hasTaskFufillmentCard(){
        boolean hasValue = this.taskFulfillmentCard != null;
        return(hasValue);
    }

    public PetasosTaskFulfillmentCard getTaskFulfillmentCard() {
        return taskFulfillmentCard;
    }

    public void setTaskFulfillmentCard(PetasosTaskFulfillmentCard taskFulfillmentCard) {
        this.taskFulfillmentCard = taskFulfillmentCard;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    @JsonIgnore
    public boolean hasAffinityNode(){
        boolean hasValue = this.affinityNode != null;
        return(hasValue);
    }

    public ComponentIdType getAffinityNode() {
        return affinityNode;
    }

    public void setAffinityNode(ComponentIdType affinityNode) {
        this.affinityNode = affinityNode;
    }


    @JsonIgnore
    public boolean hasTaskId(){
        boolean hasValue = this.taskId != null;
        return(hasValue);
    }

    public TaskIdType getTaskId() {
        return taskId;
    }

    public void setTaskId(TaskIdType taskId) {
        this.taskId = taskId;
    }

    @JsonIgnore
    public boolean hasUpdateInstant(){
        boolean hasValue = this.updateInstant != null;
        return(hasValue);
    }

    public Instant getUpdateInstant() {
        return(this.updateInstant);
    }

    public void setUpdateInstant(Instant updateInstant) {
        this.updateInstant = updateInstant;
    }

    @JsonIgnore
    public boolean hasCurrentStatus(){
        boolean hasValue = this.currentStatus != null;
        return(hasValue);
    }

    public TaskExecutionCommandEnum getCurrentStatus() {
        return (this.currentStatus);
    }

    public void setCurrentStatus(TaskExecutionCommandEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    @JsonIgnore
    public boolean hasGrantedStatus(){
        boolean hasValue = this.grantedStatus != null;
        return(hasValue);
    }

    public TaskExecutionCommandEnum getGrantedStatus() {
        return (this.grantedStatus);
    }

    public void setGrantedStatus(TaskExecutionCommandEnum grantedStatus) {
        this.grantedStatus = grantedStatus;
    }

    @JsonIgnore
    public boolean hasClusterMode(){
        boolean hasValue = this.clusterMode != null;
        return(hasValue);
    }

    public ConcurrencyModeEnum getClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(ConcurrencyModeEnum clusterMode) {
        this.clusterMode = clusterMode;
    }

    @JsonIgnore
    public boolean hasSystemMode(){
        boolean hasValue = this.systemMode != null;
        return(hasValue);
    }

    public ResilienceModeEnum getSystemMode() {
        return systemMode;
    }

    public void setSystemMode(ResilienceModeEnum systemMode) {
        this.systemMode = systemMode;
    }


    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosTaskJobCard{");
        sb.append("taskId=").append(taskId);
        sb.append(", persistenceStatus=").append(persistenceStatus);
        sb.append(", affinityNode=").append(affinityNode);
        sb.append(", outcomeStatus=").append(outcomeStatus);
        sb.append(", taskFulfillmentCard=").append(taskFulfillmentCard);
        sb.append(", updateInstant=").append(updateInstant);
        sb.append(", currentStatus=").append(currentStatus);
        sb.append(", grantedStatus=").append(grantedStatus);
        sb.append(", clusterMode=").append(clusterMode);
        sb.append(", systemMode=").append(systemMode);
        sb.append('}');
        return sb.toString();
    }
}
