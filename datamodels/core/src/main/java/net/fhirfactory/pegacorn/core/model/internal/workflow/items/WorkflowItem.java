/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.internal.workflow.items;

import net.fhirfactory.pegacorn.core.model.internal.workflow.actions.datatypes.WorkflowActionAssuranceType;
import net.fhirfactory.pegacorn.core.model.internal.workflow.actions.datatypes.WorkflowActionDeliveryType;
import net.fhirfactory.pegacorn.core.model.internal.workflow.items.datatypes.WorkflowActivityType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.common.TaskInstantDetailSegmentBase;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;

import java.io.Serializable;

public class WorkflowItem extends TaskInstantDetailSegmentBase implements Serializable {
    TaskWorkItemType stimulus;
    WorkflowActionDeliveryType workDelivery;
    WorkflowActionAssuranceType workAssurance;
    WorkflowActivityType workActivityType;

    //
    // Constructor(s)
    //

    public WorkflowItem(){
        super();
        this.stimulus = null;
        this.workAssurance = null;
        this.workDelivery = null;
        this.workActivityType = null;
    }

    //
    // Getters and Setters
    //

    public WorkflowActivityType getWorkActivityType() {
        return workActivityType;
    }

    public void setWorkActivityType(WorkflowActivityType workActivityType) {
        this.workActivityType = workActivityType;
    }

    public TaskWorkItemType getStimulus() {
        return stimulus;
    }

    public void setStimulus(TaskWorkItemType stimulus) {
        this.stimulus = stimulus;
    }

    public WorkflowActionDeliveryType getWorkDelivery() {
        return workDelivery;
    }

    public void setWorkDelivery(WorkflowActionDeliveryType workDelivery) {
        this.workDelivery = workDelivery;
    }

    public WorkflowActionAssuranceType getWorkAssurance() {
        return workAssurance;
    }

    public void setWorkAssurance(WorkflowActionAssuranceType workAssurance) {
        this.workAssurance = workAssurance;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "WorkflowItem{" +
                "stimulus=" + stimulus +
                ", workDelivery=" + workDelivery +
                ", workAssurance=" + workAssurance +
                ", workActivityType=" + workActivityType +
                ", registrationInstant=" + getTimeFormatter().format(getRegistrationInstant()) +
                ", readyInstant=" + getTimeFormatter().format(getReadyInstant()) +
                ", startInstant=" + getTimeFormatter().format(getStartInstant()) +
                ", finishInstant=" + getTimeFormatter().format(getFinishInstant()) +
                ", finalisationInstant=" + getTimeFormatter().format(getFinalisationInstant()) +
                ", lastCheckedInstant=" + getTimeFormatter().format(getLastCheckedInstant()) +
                '}';
    }
}
