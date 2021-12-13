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
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.internals.SerializableObject;

import java.util.Objects;

public class PetasosActionableTask extends PetasosTask{

    private TaskFulfillmentType taskFulfillment;
    private SerializableObject taskFulfillmentLock;
    private TaskCompletionSummaryType taskCompletionSummary;
    private SerializableObject taskCompletionLock;

    //
    // Constructor(s)
    //

    public PetasosActionableTask(){
        super();
        this.taskFulfillment = null;
        this.taskFulfillmentLock = new SerializableObject();
        this.taskCompletionSummary = null;
        this.taskCompletionLock = new SerializableObject();
        setTaskType(new TaskTypeType(TaskTypeTypeEnum.PETASOS_ACTIONABLE_TASK_TYPE));
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasTaskFulfillment(){
        boolean hasValue = this.taskFulfillment != null;
        return(hasValue);
    }

    public TaskFulfillmentType getTaskFulfillment() {
        return taskFulfillment;
    }

    public void setTaskFulfillment(TaskFulfillmentType taskFulfillment) {
        this.taskFulfillment = taskFulfillment;
    }

    public SerializableObject getTaskFulfillmentLock() {
        return taskFulfillmentLock;
    }

    public void setTaskFulfillmentLock(SerializableObject taskFulfillmentLock) {
        this.taskFulfillmentLock = taskFulfillmentLock;
    }

    @JsonIgnore
    public boolean hasTaskCompletionSummary(){
        boolean hasValue = this.taskCompletionSummary != null;
        return(hasValue);
    }

    public TaskCompletionSummaryType getTaskCompletionSummary() {
        return taskCompletionSummary;
    }

    public void setTaskCompletionSummary(TaskCompletionSummaryType taskCompletion) {
        this.taskCompletionSummary = taskCompletion;
    }

    public SerializableObject getTaskCompletionLock() {
        return taskCompletionLock;
    }

    public void setTaskCompletionLock(SerializableObject taskCompletionLock) {
        this.taskCompletionLock = taskCompletionLock;
    }

    //
    // HashCode and Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetasosActionableTask that = (PetasosActionableTask) o;
        boolean theyAreEqual = isRegistered() == that.isRegistered()
                && Objects.equals(getCreationInstant(), that.getCreationInstant())
                && Objects.equals(getUpdateInstant(), that.getUpdateInstant())
                && Objects.equals(getTaskContext(), that.getTaskContext())
                && Objects.equals(getSourceResourceId(), that.getSourceResourceId())
                && Objects.equals(getTaskId(), that.getTaskId())
                && Objects.equals(getTaskType(), that.getTaskType())
                && Objects.equals(getTaskWorkItem(), that.getTaskWorkItem())
                && Objects.equals(getTaskTraceability(), that.getTaskTraceability())
                && Objects.equals(getTaskOutcomeStatus(), that.getTaskOutcomeStatus())
                && Objects.equals(getTaskPerformerTypes(), that.getTaskPerformerTypes())
                && Objects.equals(getTaskReason(), that.getTaskReason())
                && Objects.equals(getTaskNodeAffinity(), that.getTaskNodeAffinity())
                && Objects.equals(getAggregateTaskMembership(), that.getAggregateTaskMembership())
                && Objects.equals(getTaskFulfillment(), that.getTaskFulfillment())
                && Objects.equals(getTaskCompletionSummary(), that.getTaskCompletionSummary());
        return (theyAreEqual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getTaskFulfillment(),
                getTaskCompletionSummary(),
                getCreationInstant(),
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
                getSourceResourceId(),
                isRegistered());
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosActionableTask{" +
                "  taskFulfillment=" + taskFulfillment + ",\n" +
                "  sourceResourceId=" + getSourceResourceId() + ",\n" +
                "  taskCompletionSummary=" + taskCompletionSummary + ",\n" +
                "  taskId=" + getTaskId() + ",\n" +
                "  taskType=" + getTaskType() + ",\n" +
                "  taskWorkItem=" + getTaskWorkItem() + ",\n" +
                "  taskTraceability=" + getTaskTraceability() + ",\n" +
                "  taskOutcomeStatus=" + getTaskOutcomeStatus() + ",\n" +
                "  registered=" + isRegistered() + ",\n" +
                "  taskPerformerTypes=" + getTaskPerformerTypes() + ",\n" +
                "  taskReason=" + getTaskReason() + ",\n" +
                "  taskNodeAffinity=" + getTaskNodeAffinity() + ",\n" +
                "  taskMetadata=" + getTaskContext() + ",\n" +
                '}';
    }
}
