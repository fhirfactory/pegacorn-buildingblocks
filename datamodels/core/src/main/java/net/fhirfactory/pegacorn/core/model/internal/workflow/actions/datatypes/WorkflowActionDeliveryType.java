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
package net.fhirfactory.pegacorn.core.model.internal.workflow.actions.datatypes;

import net.fhirfactory.pegacorn.core.model.internal.resources.simple.*;
import net.fhirfactory.pegacorn.core.model.internal.workflow.actors.WorkflowActor;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.common.TaskInstantDetailSegmentBase;

import java.io.Serializable;

public class WorkflowActionDeliveryType extends TaskInstantDetailSegmentBase implements Serializable {
    private WorkflowActor requiredDeliveryActor;
    private HealthcareServiceESR requiredDeliveryService;
    private HealthcareServiceESR actualDeliveryService;
    private WorkflowActor actualDeliveryActor;

    //
    // Constructor(s)
    //

    public WorkflowActionDeliveryType(){
        super();
        this.requiredDeliveryService = null;
        this.requiredDeliveryActor = null;
        this.actualDeliveryActor = null;
        this.actualDeliveryService = null;
    }

    //
    // Getters and Setters
    //

    public WorkflowActor getRequiredDeliveryActor() {
        return requiredDeliveryActor;
    }

    public void setRequiredDeliveryActor(WorkflowActor requiredDeliveryActor) {
        this.requiredDeliveryActor = requiredDeliveryActor;
    }

    public HealthcareServiceESR getRequiredDeliveryService() {
        return requiredDeliveryService;
    }

    public void setRequiredDeliveryService(HealthcareServiceESR requiredDeliveryService) {
        this.requiredDeliveryService = requiredDeliveryService;
    }

    public HealthcareServiceESR getActualDeliveryService() {
        return actualDeliveryService;
    }

    public void setActualDeliveryService(HealthcareServiceESR actualDeliveryService) {
        this.actualDeliveryService = actualDeliveryService;
    }

    public WorkflowActor getActualDeliveryActor() {
        return actualDeliveryActor;
    }

    public void setActualDeliveryActor(WorkflowActor actualDeliveryActor) {
        this.actualDeliveryActor = actualDeliveryActor;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "WorkflowActionDeliveryType{" +
                "requiredDeliveryActor=" + requiredDeliveryActor +
                ", requiredDeliveryService=" + requiredDeliveryService +
                ", actualDeliveryActor=" + actualDeliveryActor +
                ", actualDeliveryService=" + actualDeliveryService +
                ", registrationInstant=" + getTimeFormatter().format(getRegistrationInstant()) +
                ", readyInstant=" + getTimeFormatter().format(getReadyInstant()) +
                ", startInstant=" + getTimeFormatter().format(getStartInstant()) +
                ", finishInstant=" + getTimeFormatter().format(getFinishInstant()) +
                ", finalisationInstant=" + getTimeFormatter().format(getFinalisationInstant()) +
                ", lastCheckedInstant=" + getTimeFormatter().format(getLastCheckedInstant()) +
                '}';
    }
}
