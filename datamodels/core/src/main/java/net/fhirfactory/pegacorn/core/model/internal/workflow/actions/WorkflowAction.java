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
package net.fhirfactory.pegacorn.core.model.internal.workflow.actions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.internal.workflow.actions.datatypes.WorkflowActionAssuranceType;
import net.fhirfactory.pegacorn.core.model.internal.workflow.actions.datatypes.WorkflowActionDeliveryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.common.TaskInstantDetailSegmentBase;

import java.io.Serializable;

public class WorkflowAction extends TaskInstantDetailSegmentBase implements Serializable {
    private WorkflowActionDeliveryType actionDelivery;
    private WorkflowActionAssuranceType actionAssurance;

    //
    // Constructor(s)
    //

    public WorkflowAction(){
        super();
        this.actionAssurance = null;
        this.actionDelivery = null;
    }

    //
    // Getters (and Setters)
    //

    @JsonIgnore
    public boolean hasActionDelivery(){
        boolean hasValue = this.actionDelivery != null;
        return(hasValue);
    }

    public WorkflowActionDeliveryType getActionDelivery() {
        return actionDelivery;
    }

    public void setActionDelivery(WorkflowActionDeliveryType actionDelivery) {
        this.actionDelivery = actionDelivery;
    }

    @JsonIgnore
    public boolean hasActionAssurance(){
        boolean hasValue = this.actionAssurance != null;
        return(hasValue);
    }

    public WorkflowActionAssuranceType getActionAssurance() {
        return actionAssurance;
    }

    public void setActionAssurance(WorkflowActionAssuranceType actionAssurance) {
        this.actionAssurance = actionAssurance;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "WorkflowAction{" +
                "actionDelivery=" + actionDelivery +
                ", actionAssurance=" + actionAssurance +
                ", registrationInstant=" + getTimeFormatter().format(getRegistrationInstant()) +
                ", readyInstant=" + getTimeFormatter().format(getReadyInstant()) +
                ", startInstant=" + getTimeFormatter().format(getStartInstant()) +
                ", finishInstant=" + getTimeFormatter().format(getFinishInstant()) +
                ", finalisationInstant=" + getTimeFormatter().format(getFinalisationInstant()) +
                ", lastCheckedInstant=" + getTimeFormatter().format(getLastCheckedInstant()) +
                '}';
    }
}
