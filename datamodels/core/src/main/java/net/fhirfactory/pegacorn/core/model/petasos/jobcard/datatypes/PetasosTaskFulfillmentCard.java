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
package net.fhirfactory.pegacorn.core.model.petasos.jobcard.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.time.Instant;

public class PetasosTaskFulfillmentCard implements Serializable {
    private TaskIdType fulfillmentTaskId;
    private PetasosParticipantId fulfillerParticipantId;
    private FulfillmentExecutionStatusEnum fulfillmentExecutionStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant fulfillmentStartInstant;

    //
    // Constructor(s)
    //

    public PetasosTaskFulfillmentCard(){
        this.fulfillmentTaskId = null;
        this.fulfillerParticipantId = null;
        this.fulfillmentExecutionStatus = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.fulfillmentStartInstant = null;
    }

    public PetasosTaskFulfillmentCard(TaskIdType taskId, PetasosParticipantId fulfillerParticipantId){
        this.fulfillmentTaskId = SerializationUtils.clone(taskId);
        this.fulfillerParticipantId = SerializationUtils.clone(fulfillerParticipantId);
        this.fulfillmentExecutionStatus = FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED;
        this.fulfillmentStartInstant = Instant.now();
    }

    //
    // Getters (and Setters)
    //

    public TaskIdType getFulfillmentTaskId() {
        return fulfillmentTaskId;
    }

    public void setFulfillmentTaskId(TaskIdType fulfillmentTaskId) {
        this.fulfillmentTaskId = fulfillmentTaskId;
    }

    public PetasosParticipantId getFulfillerParticipantId() {
        return fulfillerParticipantId;
    }

    public void setFulfillerParticipantId(PetasosParticipantId fulfillerParticipantId) {
        this.fulfillerParticipantId = fulfillerParticipantId;
    }

    public FulfillmentExecutionStatusEnum getFulfillmentExecutionStatus() {
        return fulfillmentExecutionStatus;
    }

    public void setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum fulfillmentExecutionStatus) {
        this.fulfillmentExecutionStatus = fulfillmentExecutionStatus;
    }

    public Instant getFulfillmentStartInstant() {
        return fulfillmentStartInstant;
    }

    public void setFulfillmentStartInstant(Instant fulfillmentStartInstant) {
        this.fulfillmentStartInstant = fulfillmentStartInstant;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskFulfillmentCard{");
        sb.append("fulfillmentTaskId=").append(fulfillmentTaskId);
        sb.append(", fulfillerParticipantId=").append(fulfillerParticipantId);
        sb.append(", fulfillmentExecutionStatus=").append(fulfillmentExecutionStatus);
        sb.append(", fulfillmentStartInstant=").append(fulfillmentStartInstant);
        sb.append('}');
        return sb.toString();
    }
}
