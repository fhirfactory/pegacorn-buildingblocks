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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.reporting.datatypes.TaskReportingType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.AggregateTaskStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.internals.SerializableObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PetasosAggregateTask extends PetasosTask{

    private TaskReportingType taskReporting;

    private AggregateTaskStatusType aggregateTaskStatus;

    private TaskIdType actionableTaskId;

    private Set<TaskIdType> subTasks;

    //
    // Constructor(s)
    //

    public PetasosAggregateTask(){
        super();
        this.taskReporting = null;
        this.aggregateTaskStatus = null;
        this.actionableTaskId = null;
        this.subTasks = new HashSet<>();
        setTaskType(new TaskTypeType(TaskTypeTypeEnum.PETASOS_AGGREGATE_TASK_TYPE));
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasActionableTaskId() {
        boolean hasValue = this.actionableTaskId != null;
        return(hasValue);
    }

    public TaskIdType getActionableTaskId() {
        return actionableTaskId;
    }

    public void setActionableTaskId(TaskIdType actionableTaskId) {
        this.actionableTaskId = actionableTaskId;
    }

    @JsonIgnore
    public boolean hasTaskReporting(){
        boolean hasValue = this.taskReporting != null;
        return(hasValue);
    }

    public TaskReportingType getTaskReporting() {
        return taskReporting;
    }

    public void setTaskReporting(TaskReportingType taskReporting) {
        this.taskReporting = taskReporting;
    }

    @JsonIgnore
    public boolean hasTaskAggregateStatus(){
        boolean hasValue = this.aggregateTaskStatus != null;
        return(hasValue);
    }

    public AggregateTaskStatusType getAggregateTaskStatus() {
        return aggregateTaskStatus;
    }

    public void setAggregateTaskStatus(AggregateTaskStatusType aggregateTaskStatus) {
        this.aggregateTaskStatus = aggregateTaskStatus;
    }

    @JsonIgnore
    public boolean hasSubTasks(){
        boolean hasValue = this.subTasks != null;
        return(hasValue);
    }

    public Set<TaskIdType> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(Set<TaskIdType> subTasks) {
        this.subTasks = subTasks;
    }

    //
    // Equals and Hash
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PetasosAggregateTask that = (PetasosAggregateTask) o;
        return Objects.equals(getTaskReporting(), that.getTaskReporting()) && Objects.equals(getAggregateTaskStatus(), that.getAggregateTaskStatus()) && Objects.equals(getActionableTaskId(), that.getActionableTaskId()) && Objects.equals(getSubTasks(), that.getSubTasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTaskReporting(), getAggregateTaskStatus(), getActionableTaskId(), getSubTasks());
    }


    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosAggregateTask{" +
                "taskReporting=" + taskReporting +
                ", sourceResourceId=" + getSourceResourceId() +
                ", subTasks=" + getSubTasks() +
                ", aggregateTaskStatus=" + aggregateTaskStatus +
                ", actionableTaskId=" + actionableTaskId +
                ", taskId=" + getTaskId() +
                ", taskType=" + getTaskType() +
                ", taskWorkItem=" + getTaskWorkItem() +
                ", taskTraceability=" + getTaskTraceability() +
                ", taskOutcomeStatus=" + getTaskOutcomeStatus() +
                ", registered=" + isRegistered() +
                ", taskPerformerTypes=" + getTaskPerformerTypes() +
                ", taskReason=" + getTaskReason() +
                ", taskNodeAffinity=" + getTaskNodeAffinity() +
                ", taskMetadata=" + getTaskContext() +
                ", subTasks=" + getSubTasks() +
                ", executionStatus=" + getExecutionStatus() +
                '}';
    }
}
