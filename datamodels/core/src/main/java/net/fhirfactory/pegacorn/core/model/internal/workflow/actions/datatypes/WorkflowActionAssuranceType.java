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

public class WorkflowActionAssuranceType extends TaskInstantDetailSegmentBase implements Serializable {
    private WorkflowActor requiredAssuranceActor;
    private HealthcareServiceESR requiredAssuranceService;
    private WorkflowActor actualAssuranceActor;
    private HealthcareServiceESR actualAssuranceService;

    //
    // Constructor(s)
    //

    public WorkflowActionAssuranceType(){
        super();
        this.requiredAssuranceService = null;
        this.requiredAssuranceActor = null;
        this.actualAssuranceActor = null;
        this.actualAssuranceService = null;
    }

    //
    // Getters and Setters
    //

    public WorkflowActor getRequiredAssuranceActor() {
        return requiredAssuranceActor;
    }

    public void setRequiredAssuranceActor(WorkflowActor requiredAssuranceActor) {
        this.requiredAssuranceActor = requiredAssuranceActor;
    }

    public HealthcareServiceESR getRequiredAssuranceService() {
        return requiredAssuranceService;
    }

    public void setRequiredAssuranceService(HealthcareServiceESR requiredAssuranceService) {
        this.requiredAssuranceService = requiredAssuranceService;
    }

    public WorkflowActor getActualAssuranceActor() {
        return actualAssuranceActor;
    }

    public void setActualAssuranceActor(WorkflowActor actualAssuranceActor) {
        this.actualAssuranceActor = actualAssuranceActor;
    }

    public HealthcareServiceESR getActualAssuranceService() {
        return actualAssuranceService;
    }

    public void setActualAssuranceService(HealthcareServiceESR actualAssuranceService) {
        this.actualAssuranceService = actualAssuranceService;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "WorkflowActionAssuranceType{" +
                "requiredAssuranceActor=" + requiredAssuranceActor +
                ", requiredAssuranceService=" + requiredAssuranceService +
                ", actualAssuranceActor=" + actualAssuranceActor +
                ", actualAssuranceService=" + actualAssuranceService +
                ", registrationInstant=" + getTimeFormatter().format(getRegistrationInstant()) +
                ", readyInstant=" + getTimeFormatter().format(getReadyInstant()) +
                ", startInstant=" + getTimeFormatter().format(getStartInstant()) +
                ", finishInstant=" + getTimeFormatter().format(getFinishInstant()) +
                ", finalisationInstant=" + getTimeFormatter().format(getFinalisationInstant()) +
                ", lastCheckedInstant=" + getTimeFormatter().format(getLastCheckedInstant()) +
                '}';
    }
}
