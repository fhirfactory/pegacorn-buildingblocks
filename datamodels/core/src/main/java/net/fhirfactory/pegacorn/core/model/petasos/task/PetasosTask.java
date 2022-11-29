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
package net.fhirfactory.pegacorn.core.model.petasos.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.datagrid.datatypes.DatagridElementSourceResourceIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.context.TaskContextType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.reason.datatypes.TaskReasonType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.datatypes.TaskExecutionControl;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class PetasosTask implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(PetasosTask.class);

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant creationInstant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant updateInstant;

    private DatagridElementSourceResourceIdType sourceResourceId;

    private TaskContextType taskContext;

    private TaskIdType taskId;

    private TaskTypeType taskType;

    private TaskWorkItemType taskWorkItem;

    private TaskTraceabilityType taskTraceability;

    private TaskOutcomeStatusType taskOutcomeStatus;

    private List<TaskPerformerTypeType> taskPerformerTypes;

    private TaskReasonType taskReason;

    private ComponentIdType taskNodeAffinity;

    private Set<TaskIdType> aggregateTaskMembership;

    private TaskExecutionControl taskExecutionDetail;

    private boolean registered;


    //
    // Constructor(s)
    //

    public PetasosTask(){
        this.taskId = null;
        this.sourceResourceId = null;
        this.creationInstant = Instant.now();
        this.updateInstant = Instant.now();
        this.taskWorkItem = new TaskWorkItemType();
        this.taskTraceability = new TaskTraceabilityType();
        this.aggregateTaskMembership = new HashSet<>();
        this.taskOutcomeStatus = null;
        this.registered = false;
        this.taskPerformerTypes = new ArrayList<>();
        this.taskType = null;
        this.taskReason = null;
        this.taskNodeAffinity = null;
        this.taskContext = null;
        this.taskExecutionDetail = new TaskExecutionControl();
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasTaskExecutionDetail(){
        boolean hasValue = this.taskExecutionDetail != null;
        return(hasValue);
    }

    public TaskExecutionControl getTaskExecutionDetail() {
        return taskExecutionDetail;
    }

    public void setTaskExecutionDetail(TaskExecutionControl executionStatus) {
        this.taskExecutionDetail = executionStatus;
    }

    @JsonIgnore
    public boolean hasSourceResourceId(){
        boolean hasValue = this.sourceResourceId != null;
        return(hasValue);
    }

    public DatagridElementSourceResourceIdType getSourceResourceId() {
        return sourceResourceId;
    }

    public void setSourceResourceId(DatagridElementSourceResourceIdType sourceResourceId) {
        this.sourceResourceId = sourceResourceId;
    }

    @JsonIgnore
    public boolean hasCreationInstant(){
        boolean hasValue = this.creationInstant != null;
        return(hasValue);
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public void setCreationInstant(Instant creationInstant) {
        this.creationInstant = creationInstant;
    }

    public Instant getUpdateInstant() {
        return updateInstant;
    }

    public void setUpdateInstant(Instant updateInstant) {
        this.updateInstant = updateInstant;
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
    public boolean hasTaskType(){
        boolean hasValue = this.taskType != null;
        return(hasValue);
    }

    public TaskTypeType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypeType taskType) {
        this.taskType = taskType;
    }


    @JsonIgnore
    public boolean hasTaskWorkItem(){
        boolean hasValue = this.taskWorkItem != null;
        return(hasValue);
    }

    public TaskWorkItemType getTaskWorkItem() {
        return taskWorkItem;
    }

    public void setTaskWorkItem(TaskWorkItemType taskWorkItem) {
        this.taskWorkItem = taskWorkItem;
    }

    @JsonIgnore
    public boolean hasTaskTraceability(){
        boolean hasValue = this.taskTraceability != null;
        return(hasValue);
    }

    public TaskTraceabilityType getTaskTraceability() {
        return taskTraceability;
    }

    public void setTaskTraceability(TaskTraceabilityType taskTraceability) {
        this.taskTraceability = taskTraceability;
    }


    @JsonIgnore
    public boolean hasTaskOutcomeStatus(){
        boolean hasValue = this.taskOutcomeStatus != null;
        return(hasValue);
    }

    public TaskOutcomeStatusType getTaskOutcomeStatus() {
        return taskOutcomeStatus;
    }

    public void setTaskOutcomeStatus(TaskOutcomeStatusType taskOutcomeStatus) {
        this.taskOutcomeStatus = taskOutcomeStatus;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    @JsonIgnore
    public boolean hasTaskPerformerTypes(){
        boolean hasValue = this.taskPerformerTypes != null;
        return(hasValue);
    }

    public List<TaskPerformerTypeType> getTaskPerformerTypes() {
        return taskPerformerTypes;
    }

    public void setTaskPerformerTypes(List<TaskPerformerTypeType> taskPerformerTypes) {
        this.taskPerformerTypes = taskPerformerTypes;
    }

    @JsonIgnore
    public boolean hasTaskReason(){
        boolean hasValue = this.taskReason != null;
        return(hasValue);
    }

    public TaskReasonType getTaskReason() {
        return taskReason;
    }

    public void setTaskReason(TaskReasonType taskReason) {
        this.taskReason = taskReason;
    }

    @JsonIgnore
    public boolean hasTaskNodeAffinity(){
        boolean hasValue = this.taskNodeAffinity != null;
        return(hasValue);
    }

    public ComponentIdType getTaskNodeAffinity() {
        return taskNodeAffinity;
    }

    public void setTaskNodeAffinity(ComponentIdType taskNodeAffinity) {
        this.taskNodeAffinity = taskNodeAffinity;
    }

    @JsonIgnore
    public boolean hasAggregateTaskMembership(){
        boolean hasValue = this.aggregateTaskMembership != null;
        return(hasValue);
    }

    public Set<TaskIdType> getAggregateTaskMembership() {
        return aggregateTaskMembership;
    }

    public void setAggregateTaskMembership(Set<TaskIdType> aggregateTaskMembership) {
        this.aggregateTaskMembership = aggregateTaskMembership;
    }

    @JsonIgnore
    public boolean hasTaskContext(){
        boolean hasValue = this.taskContext != null;
        return(hasValue);
    }

    public TaskContextType getTaskContext() {
        return taskContext;
    }

    public void setTaskContext(TaskContextType taskContext) {
        this.taskContext = taskContext;
    }


    //
    // Hashcode and Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetasosTask that = (PetasosTask) o;
        boolean theyAreEqual = isRegistered() == that.isRegistered()
                && Objects.equals(getSourceResourceId(), that.getSourceResourceId())
                && Objects.equals(getCreationInstant(), that.getCreationInstant())
                && Objects.equals(getUpdateInstant(), that.getUpdateInstant())
                && Objects.equals(getTaskContext(), that.getTaskContext())
                && Objects.equals(getTaskId(), that.getTaskId())
                && Objects.equals(getTaskType(), that.getTaskType())
                && Objects.equals(getTaskWorkItem(), that.getTaskWorkItem())
                && Objects.equals(getTaskTraceability(), that.getTaskTraceability())
                && Objects.equals(getTaskOutcomeStatus(), that.getTaskOutcomeStatus())
                && Objects.equals(getTaskPerformerTypes(), that.getTaskPerformerTypes())
                && Objects.equals(getTaskReason(), that.getTaskReason())
                && Objects.equals(getTaskNodeAffinity(), that.getTaskNodeAffinity())
                && Objects.equals(getAggregateTaskMembership(), that.getAggregateTaskMembership())
                && Objects.equals(getTaskExecutionDetail(), that.getTaskExecutionDetail());
        return(theyAreEqual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreationInstant(),
                getSourceResourceId(),
                getUpdateInstant(),
                getTaskContext(),
                getTaskId(),
                getTaskType(),
                getTaskWorkItem(),
                getTaskTraceability(),
                getTaskOutcomeStatus(),
                getTaskPerformerTypes(),
                getTaskReason(),
                getTaskNodeAffinity(),
                getAggregateTaskMembership(),
                getTaskExecutionDetail(),
                isRegistered());
    }


    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosTask{" +
                "creationInstant=" + creationInstant +
                ", sourceResourceId=" + getSourceResourceId() +
                ", updateInstant=" + updateInstant +
                ", taskContext=" + taskContext +
                ", taskId=" + taskId +
                ", taskType=" + taskType +
                ", taskWorkItem=" + taskWorkItem +
                ", taskTraceability=" + taskTraceability +
                ", taskOutcomeStatus=" + taskOutcomeStatus +
                ", taskPerformerTypes=" + taskPerformerTypes +
                ", taskReason=" + taskReason +
                ", taskNodeAffinity=" + taskNodeAffinity +
                ", aggregateTaskMembership=" + aggregateTaskMembership +
                ", registered=" + registered +
                ", taskExecutionDetail=" + getTaskExecutionDetail() +
                '}';
    }

    //
    // Update
    //

    public PetasosTask update(PetasosTask update){
        PetasosTask petasosTask = updatePetasosTask(update);
        return(petasosTask);
    }

    protected PetasosTask updatePetasosTask(PetasosTask update){
        LOG.debug(".updatePetasosTask(): Entry, petasosTask->{}", update);
        if(update == null){
            return(this);
        }
        // sourceResourceId
        if(update.hasSourceResourceId()){
            this.setSourceResourceId(update.getSourceResourceId());
        }
        // taskContext
        if(update.hasTaskContext()){
            LOG.trace(".updatePetasosTask(): Updating TaskContext");
            if(!this.hasTaskContext()){
                LOG.trace(".updatePetasosTask(): Updating TaskContext: Copying Whole TaskContext");
                this.setTaskContext(update.getTaskContext());
            } else {
                LOG.trace(".updatePetasosTask(): Updating TaskContext: Performing TaskContext per-attribute update ");
                if(update.getTaskContext().hasTaskEncounter()) {
                    LOG.trace(".updatePetasosTask(): Updating TaskContext: updating TaskEncounter attribute");
                    this.getTaskContext().setTaskEncounter(update.getTaskContext().getTaskEncounter());
                }
                if(update.getTaskContext().hasTaskBeneficiary()){
                    LOG.trace(".updatePetasosTask(): Updating TaskContext: updating TaskBeneficiary attribute");
                    this.getTaskContext().setTaskBeneficiary(update.getTaskContext().getTaskBeneficiary());
                }
                if(update.getTaskContext().hasTaskTriggerSummary()){
                    LOG.trace(".updatePetasosTask(): Updating TaskContext: updating TaskTriggerSummary attribute");
                    this.getTaskContext().setTaskTriggerSummary(update.getTaskContext().getTaskTriggerSummary());
                }
            }
        }
        // taskType
        if(update.hasTaskType()){
            LOG.trace(".updatePetasosTask(): Updating TaskType");
            if(!this.hasTaskType()){
                LOG.trace(".updatePetasosTask(): Updating TaskType: Copying Whole TaskType");
                this.setTaskType(update.getTaskType());
            } else {
                LOG.trace(".updatePetasosTask(): Updating TaskType: Performing TaskType per-attribute update ");
                if(update.getTaskType().hasTaskSubType()) {
                    LOG.trace(".updatePetasosTask(): Updating TaskType: updating TaskSubType attribute");
                    this.getTaskType().setTaskSubType(update.getTaskType().getTaskSubType());
                }
                if(update.getTaskType().hasTaskType()){
                    LOG.trace(".updatePetasosTask(): Updating TaskType: updating TaskType attribute");
                    this.getTaskType().setTaskType(update.getTaskType().getTaskType());
                }
            }
        }
        // taskWorkItem
        if(update.hasTaskWorkItem()){
            LOG.trace(".updatePetasosTask(): Updating TaskWorkItem");
            if(!this.hasTaskWorkItem()){
                LOG.trace(".updatePetasosTask(): Updating TaskWorkItem: Copying Whole TaskWorkItem");
                this.setTaskWorkItem(update.getTaskWorkItem());
            } else {
                LOG.trace(".updatePetasosTask(): Updating TaskWorkItem: Performing TaskWorkItem per-attribute update");
                if(update.getTaskWorkItem().hasIngresContent()){
                    LOG.trace(".updatePetasosTask(): Updating TaskWorkItem: Updating IngresContent attribute");
                    this.getTaskWorkItem().setIngresContent(update.getTaskWorkItem().getIngresContent());
                }
                if(update.getTaskWorkItem().hasEgressContent()){
                    LOG.trace(".updatePetasosTask(): Updating TaskWorkItem: Updating EgressContent attribute");
                    this.getTaskWorkItem().setEgressContent(update.getTaskWorkItem().getEgressContent());
                }
                if(update.getTaskWorkItem().hasFailureDescription()){
                    LOG.trace(".updatePetasosTask(): Updating TaskWorkItem: Updating FailureDescription attribute");
                    this.getTaskWorkItem().setFailureDescription(update.getTaskWorkItem().getFailureDescription());
                }
                if(update.getTaskWorkItem().hasInstanceID()){
                    LOG.trace(".updatePetasosTask(): Updating TaskWorkItem: Updating InstanceId attribute");
                    this.getTaskWorkItem().setInstanceID(update.getTaskWorkItem().getInstanceID());
                }
                if(update.getTaskWorkItem().hasPayloadTopicID()){
                    LOG.trace(".updatePetasosTask(): Updating TaskWorkItem: Updating TypeId attribute");
                    this.getTaskWorkItem().setUoWTypeID(update.getTaskWorkItem().getTypeID());
                }
                if(update.getTaskWorkItem().hasProcessingOutcome()){
                    LOG.trace(".updatePetasosTask(): Updating TaskWorkItem: Updating ProcessingOutcome attribute");
                    this.getTaskWorkItem().setProcessingOutcome(update.getTaskWorkItem().getProcessingOutcome());
                }
            }
        }
        // taskTraceability
        if(update.hasTaskTraceability()){
            if(!this.hasTaskTraceability()){
                this.setTaskTraceability(update.getTaskTraceability());
            } else {
                if(update.getTaskTraceability().getTaskJourney() != null){
                    this.getTaskTraceability().setTaskJourney(update.getTaskTraceability().getTaskJourney());
                }
            }
        }
        // taskOutcomeStatus
        if(update.hasTaskOutcomeStatus()){
            if(!this.hasTaskOutcomeStatus()){
                this.setTaskOutcomeStatus(update.getTaskOutcomeStatus());
            } else {
                if(update.getTaskOutcomeStatus().getOutcomeStatus() != null){
                    this.getTaskOutcomeStatus().setOutcomeStatus(update.getTaskOutcomeStatus().getOutcomeStatus());
                }
                if(update.getTaskOutcomeStatus().getEntryInstant() != null){
                    this.getTaskOutcomeStatus().setEntryInstant(update.getTaskOutcomeStatus().getEntryInstant());
                }
            }
        }
        // taskPerformerTypes
        if(update.hasTaskPerformerTypes()){
            if(!this.hasTaskPerformerTypes()){
                this.setTaskPerformerTypes(update.getTaskPerformerTypes());
            }
        }
        // taskReason
        if(update.hasTaskReason()){
            this.setTaskReason(update.getTaskReason());
        }
        // taskNodeAffinity
        if(update.hasTaskNodeAffinity()){
            if(!this.hasTaskNodeAffinity()){
                this.setTaskNodeAffinity(update.getTaskNodeAffinity());
            } else {
                if(update.getTaskNodeAffinity().hasIdValidityEndInstant()){
                    this.getTaskNodeAffinity().setIdValidityEndInstant(update.getTaskNodeAffinity().getIdValidityEndInstant());
                }
                if(update.getTaskNodeAffinity().hasIdValidityStartInstant()){
                    this.getTaskNodeAffinity().setIdValidityStartInstant(update.getTaskNodeAffinity().getIdValidityStartInstant());
                }
                if(update.getTaskNodeAffinity().hasId()){
                    this.getTaskNodeAffinity().setId(update.getTaskNodeAffinity().getId());
                }
                if(update.getTaskNodeAffinity().hasDisplayName()){
                    this.getTaskNodeAffinity().setDisplayName(this.getTaskNodeAffinity().getDisplayName());
                }
            }
        }
        // aggregateTaskMembership
        if(update.hasAggregateTaskMembership()){
            if(!this.hasAggregateTaskMembership()){
                this.setAggregateTaskMembership(update.getAggregateTaskMembership());
            } else {
                this.getAggregateTaskMembership().clear();
                this.getAggregateTaskMembership().addAll(update.getAggregateTaskMembership());
            }
        }
        // taskExecutionDetail
        if(update.hasTaskExecutionDetail()){
            if(!this.hasTaskExecutionDetail()){
                this.setTaskExecutionDetail(update.getTaskExecutionDetail());
            } else {
                this.getTaskExecutionDetail().setExecutionWindow(update.getTaskExecutionDetail().getExecutionWindow());
                this.getTaskExecutionDetail().setCurrentExecutionStatus(update.getTaskExecutionDetail().getCurrentExecutionStatus());
                this.getTaskExecutionDetail().setExecutionCommand(update.getTaskExecutionDetail().getExecutionCommand());
                this.getTaskExecutionDetail().setUpdateInstant(update.getTaskExecutionDetail().getUpdateInstant());
            }

        }
        // registered
        this.setRegistered(update.isRegistered());
        // done... except for updateInstant
        this.setUpdateInstant(update.getUpdateInstant());
        // now, definitely done!
        return(this);
    }
}
