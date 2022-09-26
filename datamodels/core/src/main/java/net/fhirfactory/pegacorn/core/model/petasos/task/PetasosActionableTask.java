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
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PetasosActionableTask extends PetasosTask{
    private final static Logger LOG = LoggerFactory.getLogger(PetasosActionableTask.class);

    private TaskFulfillmentType taskFulfillment;
    private TaskCompletionSummaryType taskCompletionSummary;

    //
    // Constructor(s)
    //

    public PetasosActionableTask(){
        super();
        this.taskFulfillment = null;
        this.taskCompletionSummary = null;
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

    //
    // HashCode and Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetasosActionableTask that = (PetasosActionableTask) o;
        boolean theyAreEqual = isRegistered() == that.isRegistered()
                && Objects.equals(getSequenceNumber(), that.getSequenceNumber())
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
                getSequenceNumber(),
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
        final StringBuilder sb = new StringBuilder("PetasosActionableTask{");
        sb.append("taskFulfillment=").append(taskFulfillment);
        sb.append(", taskCompletionSummary=").append(taskCompletionSummary);
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }


    //
    // Update ActionableTask
    //

    @JsonIgnore
    public PetasosTask update(PetasosActionableTask update){
        LOG.debug(".update(): Entry, update->{}", update);
        //
        // 1st, update the super-class attributes
        PetasosActionableTask updatedPetasosTask = (PetasosActionableTask)updatePetasosTask(update);
        LOG.trace(".update(): After PetasosTask update, updatedPetasosTask->{}", updatedPetasosTask);
        LOG.trace(".update(): After PetasosTask update, this->{}", this);
        //
        // 2nd, update the PetasosActionableTask specific attributes
        PetasosActionableTask updatedTask = updatePetasosActionableTask(update);
        LOG.debug(".update(): Exit, updatedTask->{}", updatedTask);
        return(updatedTask);
    }

    @JsonIgnore
    protected PetasosActionableTask updatePetasosActionableTask(PetasosActionableTask update){
        if(update == null){
            return(this);
        }
        if (update.hasTaskCompletionSummary()) {
            if (!this.hasTaskCompletionSummary()){
                this.setTaskCompletionSummary(update.getTaskCompletionSummary());
            }else {
                this.getTaskCompletionSummary().setLastInChain(update.getTaskCompletionSummary().isLastInChain());
                this.getTaskCompletionSummary().setFinalised(update.getTaskCompletionSummary().isFinalised());
                this.getTaskCompletionSummary().setDownstreamTaskMap(update.getTaskCompletionSummary().getDownstreamTaskMap());
            }
        }
        LOG.trace(".updatePetasosActionableTask(): About to Update: update->getTaskFulfillment()->{}",update.getTaskFulfillment());
        LOG.trace(".updatePetasosActionableTask(): About to Update: this->getTaskFulfillment()->{}",this.getTaskFulfillment());
        if(update.hasTaskFulfillment()){
            if(!this.hasTaskFulfillment()){
                this.setTaskFulfillment(update.getTaskFulfillment());
            } else {
                if(update.getTaskFulfillment().hasFulfiller()) {
                    this.getTaskFulfillment().setFulfiller(update.getTaskFulfillment().getFulfiller());
                }
                if(update.getTaskFulfillment().hasFinalisationInstant()){
                    this.getTaskFulfillment().setFinalisationInstant(update.getTaskFulfillment().getFinalisationInstant());
                }
                if(update.getTaskFulfillment().hasFinishInstant()){
                    this.getTaskFulfillment().setFinishInstant(update.getTaskFulfillment().getFinishInstant());
                }
                if(update.getTaskFulfillment().hasLastCheckedInstant()){
                    this.getTaskFulfillment().setLastCheckedInstant(update.getTaskFulfillment().getLastCheckedInstant());
                }
                if(update.getTaskFulfillment().hasReadyInstant()){
                    this.getTaskFulfillment().setReadyInstant(update.getTaskFulfillment().getReadyInstant());
                }
                if(update.getTaskFulfillment().hasRegistrationInstant()){
                    this.getTaskFulfillment().setRegistrationInstant(update.getTaskFulfillment().getRegistrationInstant());
                }
                if(update.getTaskFulfillment().hasStartInstant()){
                    this.getTaskFulfillment().setStartInstant(update.getTaskFulfillment().getStartInstant());
                }
                if(update.getTaskFulfillment().hasStatus()){
                    this.getTaskFulfillment().setStatus(update.getTaskFulfillment().getStatus());
                }
                if(update.getTaskFulfillment().hasTrackingID()){
                    this.getTaskFulfillment().setTrackingID(update.getTaskFulfillment().getTrackingID());
                }
            }
        }
        LOG.debug(".updatePetasosActionableTask(): After Update: this->getTaskFulfillment()->{}",this.getTaskFulfillment());
        return(this);
    }
}
