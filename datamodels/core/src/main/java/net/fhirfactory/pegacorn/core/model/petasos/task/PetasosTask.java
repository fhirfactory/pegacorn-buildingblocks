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
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.metadata.TaskMetadataType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.reason.datatypes.TaskReasonType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.internals.SerializableObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetasosTask implements Serializable {

    private TaskMetadataType taskMetadata;
    private SerializableObject taskMetadataLock;

    private TaskIdType taskId;
    private SerializableObject taskIdLock;

    private TaskTypeType taskType;
    private SerializableObject taskTypeLock;

    private TaskWorkItemType taskWorkItem;
    private SerializableObject taskWorkItemLock;

    private TaskTraceabilityType taskTraceability;
    private SerializableObject taskTraceabilityLock;

    private TaskOutcomeStatusType taskOutcomeStatus;
    private SerializableObject taskOutcomeStatusLock;

    private List<TaskPerformerTypeType> taskPerformerTypes;
    private SerializableObject taskPerformerTypesLock;

    private TaskReasonType taskReason;
    private SerializableObject taskReasonLock;

    private ComponentIdType taskNodeAffinity;
    private SerializableObject taskNodeAffinityLock;

    private Map<TaskIdType, PetasosTask> subTasks;
    private SerializableObject subTasksLock;

    private boolean registered;

    //
    // Constructor(s)
    //

    public PetasosTask(){
        this.taskId = null;
        this.taskIdLock = new SerializableObject();
        this.taskWorkItem = new TaskWorkItemType();
        this.taskWorkItemLock = new SerializableObject();
        this.taskTraceability = new TaskTraceabilityType();
        this.taskTraceabilityLock = new SerializableObject();
        this.subTasks = new HashMap<>();
        this.subTasksLock = new SerializableObject();
        this.taskOutcomeStatus = null;
        this.taskOutcomeStatusLock = new SerializableObject();
        this.registered = false;
        this.taskPerformerTypes = new ArrayList<>();
        this.taskPerformerTypesLock = new SerializableObject();
        this.taskType = null;
        this.taskTypeLock = new SerializableObject();
        this.taskReason = null;
        this.taskReasonLock = new SerializableObject();
        this.taskNodeAffinity = null;
        this.taskNodeAffinityLock = new SerializableObject();
        this.taskMetadata = null;
        this.taskMetadataLock = new SerializableObject();
    }

    //
    // Getters and Setters (Bean Methods)
    //

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

    public SerializableObject getTaskIdLock() {
        return taskIdLock;
    }

    public void setTaskIdLock(SerializableObject taskIdLock) {
        this.taskIdLock = taskIdLock;
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

    public SerializableObject getTaskTypeLock() {
        return taskTypeLock;
    }

    public void setTaskTypeLock(SerializableObject taskTypeLock) {
        this.taskTypeLock = taskTypeLock;
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

    public SerializableObject getTaskWorkItemLock() {
        return taskWorkItemLock;
    }

    public void setTaskWorkItemLock(SerializableObject taskWorkItemLock) {
        this.taskWorkItemLock = taskWorkItemLock;
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

    public SerializableObject getTaskTraceabilityLock() {
        return taskTraceabilityLock;
    }

    public void setTaskTraceabilityLock(SerializableObject taskTraceabilityLock) {
        this.taskTraceabilityLock = taskTraceabilityLock;
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

    public SerializableObject getTaskOutcomeStatusLock() {
        return taskOutcomeStatusLock;
    }

    public void setTaskOutcomeStatusLock(SerializableObject taskOutcomeStatusLock) {
        this.taskOutcomeStatusLock = taskOutcomeStatusLock;
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

    public SerializableObject getTaskPerformerTypesLock() {
        return taskPerformerTypesLock;
    }

    public void setTaskPerformerTypesLock(SerializableObject taskPerformerTypesLock) {
        this.taskPerformerTypesLock = taskPerformerTypesLock;
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

    public SerializableObject getTaskReasonLock() {
        return taskReasonLock;
    }

    public void setTaskReasonLock(SerializableObject taskReasonLock) {
        this.taskReasonLock = taskReasonLock;
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

    public SerializableObject getTaskNodeAffinityLock() {
        return taskNodeAffinityLock;
    }

    public void setTaskNodeAffinityLock(SerializableObject taskNodeAffinityLock) {
        this.taskNodeAffinityLock = taskNodeAffinityLock;
    }

    @JsonIgnore
    public boolean hasSubTasks(){
        boolean hasValue = this.subTasks != null;
        return(hasValue);
    }

    public Map<TaskIdType, PetasosTask> getSubTasks() {
        return (this.subTasks);
    }

    public void setSubTasks(Map<TaskIdType, PetasosTask> tasks) {
        if(this.subTasks == null){
            this.subTasks = new HashMap<>();
        }
        this.subTasks.clear();
        for(TaskIdType currentTaskId: tasks.keySet()){
            this.subTasks.put(currentTaskId, tasks.get(currentTaskId));
        }
    }

    public SerializableObject getSubTasksLock() {
        return subTasksLock;
    }

    public void setSubTasksLock(SerializableObject subTasksLock) {
        this.subTasksLock = subTasksLock;
    }

    @JsonIgnore
    public boolean hasTaskMetadata(){
        boolean hasValue = this.taskMetadata != null;
        return(hasValue);
    }

    public TaskMetadataType getTaskMetadata() {
        return taskMetadata;
    }

    public void setTaskMetadata(TaskMetadataType taskMetadata) {
        this.taskMetadata = taskMetadata;
    }

    public SerializableObject getTaskMetadataLock() {
        return taskMetadataLock;
    }

    public void setTaskMetadataLock(SerializableObject taskMetadataLock) {
        this.taskMetadataLock = taskMetadataLock;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosTask{" +
                "taskId=" + taskId +
                ", taskType=" + taskType +
                ", taskWorkItem=" + taskWorkItem +
                ", taskTraceability=" + taskTraceability +
                ", taskOutcomeStatus=" + taskOutcomeStatus +
                ", registered=" + registered +
                ", taskPerformers=" + taskPerformerTypes +
                ", taskReason=" + taskReason +
                ", subTasks=" + getSubTasks() +
                ", taskMetadata=" + getTaskMetadata() +
                '}';
    }
}
