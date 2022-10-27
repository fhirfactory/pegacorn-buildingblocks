/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.common.TaskInstantDetailSegmentBase;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class TaskTraceabilityElementType extends TaskInstantDetailSegmentBase implements Serializable {
    private PetasosParticipantId fulfillerId;
    private ComponentIdType fulfillerInstanceId;
    private TaskIdType actionableTaskId;
    private TaskIdType fulfillerTaskId;

    //
    // Constructors
    //

    public TaskTraceabilityElementType(){
        super();
        this.fulfillerId = null;
        this.actionableTaskId = null;
        this.fulfillerTaskId = null;
        this.fulfillerInstanceId = null;
    }

    public TaskTraceabilityElementType(TaskTraceabilityElementType ori){
        super(ori);
        this.fulfillerId = null;
        this.actionableTaskId = null;
        this.fulfillerTaskId = null;
        this.fulfillerInstanceId = null;
        if(ori.hasFulfillerInstanceId()){
            this.setFulfillerInstanceId(SerializationUtils.clone(ori.getFulfillerInstanceId()));
        }
        if(ori.hasActionableTaskId()){
            this.setActionableTaskId(SerializationUtils.clone(ori.getActionableTaskId()));
        }
        if(ori.hasFulfillerTaskId()){
            this.setFulfillerTaskId(SerializationUtils.clone(ori.getFulfillerTaskId()));
        }
        if(ori.hasFulfillerId()){
            this.setFulfillerId(SerializationUtils.clone(ori.getFulfillerId()));
        }
    }

    public TaskTraceabilityElementType(TaskIdType previousActionableTask, TaskFulfillmentType fullfillment){

    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasFulfillerInstanceId(){
        boolean hasValue = this.fulfillerId != null;
        return(hasValue);
    }

    public ComponentIdType getFulfillerInstanceId() {
        return fulfillerInstanceId;
    }

    public void setFulfillerInstanceId(ComponentIdType instanceId) {
        this.fulfillerInstanceId = instanceId;
    }

    @JsonIgnore
    public boolean hasFulfillerId(){
        boolean hasValue = this.fulfillerId != null;
        return(hasValue);
    }

    public PetasosParticipantId getFulfillerId() {
        return fulfillerId;
    }

    public void setFulfillerId(PetasosParticipantId fulfillerId) {
        this.fulfillerId = fulfillerId;
    }

    @JsonIgnore
    public boolean hasActionableTaskId(){
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
    public boolean hasFulfillerTaskId(){
        boolean hasValue = this.fulfillerTaskId != null;
        return(hasValue);
    }

    public TaskIdType getFulfillerTaskId() {
        return fulfillerTaskId;
    }

    public void setFulfillerTaskId(TaskIdType fulfillerTaskId) {
        this.fulfillerTaskId = fulfillerTaskId;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "TraceabilityElement{" +
                "registrationInstant=" + getRegistrationInstant() +
                ", readyInstant=" + getReadyInstant() +
                ", startInstant=" + getStartInstant() +
                ", finishInstant=" + getFinishInstant() +
                ", finalisationInstant=" + getFinalisationInstant() +
                ", lastCheckedInstant=" + getLastCheckedInstant() +
                ", fulfillerId=" + fulfillerId +
                ", taskId=" + actionableTaskId +
                '}';
    }
}
