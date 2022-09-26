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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.TaskTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.core.model.petasos.wup.PetasosTaskJobCard;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Objects;

public class PetasosFulfillmentTask extends PetasosTask{

    private TaskFulfillmentType taskFulfillment;

    private PetasosTaskJobCard taskJobCard;

    private TaskIdType actionableTaskId;

    private boolean aRetry;

    //
    // Constructor(s)
    //

    public PetasosFulfillmentTask(){
        super();
        this.actionableTaskId = null;
        this.taskFulfillment = new TaskFulfillmentType();
        this.taskJobCard = null;
        this.aRetry = false;
        setTaskType(new TaskTypeType(TaskTypeTypeEnum.PETASOS_FULFILLMENT_TASK_TYPE));
    }

    //
    // Getters and Setters (Bean Methods)
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
    public boolean hasActionableTaskId(){
        boolean hasValue = this.actionableTaskId != null;
        return(hasValue);
    }

    public TaskIdType getActionableTaskId() {
        return actionableTaskId;
    }

    public void setActionableTaskId(TaskIdType fulfilledTaskPetasosTaskIdentity) {
        this.actionableTaskId = fulfilledTaskPetasosTaskIdentity;
    }

    @JsonIgnore
    public boolean hasTaskJobCard(){
        boolean hasValue = this.taskJobCard != null;
        return(hasValue);
    }

    public PetasosTaskJobCard getTaskJobCard() {
        return taskJobCard;
    }

    public void setTaskJobCard(PetasosTaskJobCard taskJobCard) {
        this.taskJobCard = taskJobCard;
    }


    public boolean isaRetry() {
        return aRetry;
    }

    public void setaRetry(boolean aRetry) {
        this.aRetry = aRetry;
    }

    //
    // Hash and Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PetasosFulfillmentTask that = (PetasosFulfillmentTask) o;
        return isaRetry() == that.isaRetry() && Objects.equals(getTaskFulfillment(), that.getTaskFulfillment()) && Objects.equals(getTaskJobCard(), that.getTaskJobCard()) && Objects.equals(getActionableTaskId(), that.getActionableTaskId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTaskFulfillment(), getTaskJobCard(), getActionableTaskId(), isaRetry());
    }

    //
    // ToString
    //


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosFulfillmentTask{");
        sb.append("taskFulfillment=").append(taskFulfillment);
        sb.append(", taskJobCard=").append(taskJobCard);
        sb.append(", actionableTaskId=").append(actionableTaskId);
        sb.append(", aRetry=").append(aRetry);
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }
}
