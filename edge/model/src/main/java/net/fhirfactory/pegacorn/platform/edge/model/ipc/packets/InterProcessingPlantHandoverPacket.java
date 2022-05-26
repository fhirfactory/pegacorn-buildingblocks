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
package net.fhirfactory.pegacorn.platform.edge.model.ipc.packets;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;

import java.io.Serializable;
import java.time.Instant;

public class InterProcessingPlantHandoverPacket implements Serializable {
    private PetasosActionableTask actionableTask;
    private TaskTraceabilityElementType upstreamFulfillmentTaskDetails;
    private String messageIdentifier;
    private Integer messageSize;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant eventProcessingStartTime;
    private int messageTransferCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant messageSendStartInstant;
    private String source;
    private String target;

    //
    // Constructor(s)
    //

    public InterProcessingPlantHandoverPacket(){
        this.actionableTask = null;
        this.messageIdentifier = null;
        this.messageSize = null;
        this.eventProcessingStartTime = null;
        this.messageTransferCount = 0;
        this.messageSendStartInstant = null;
        this.upstreamFulfillmentTaskDetails = null;
        this.source = null;
        this.target = null;
    }

    //
    // Getters and Setters
    //

    public TaskTraceabilityElementType getUpstreamFulfillmentTaskDetails() {
        return upstreamFulfillmentTaskDetails;
    }

    public void setUpstreamFulfillmentTaskDetails(TaskTraceabilityElementType upstreamFulfillmentTaskDetails) {
        this.upstreamFulfillmentTaskDetails = upstreamFulfillmentTaskDetails;
    }

    public PetasosActionableTask getActionableTask() {
        return actionableTask;
    }

    public void setActionableTask(PetasosActionableTask actionableTask) {
        this.actionableTask = actionableTask;
    }

    public String getMessageIdentifier() {
        return (messageIdentifier);
    }

    public void setMessageIdentifier(String messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    public Integer getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(Integer messageSize) {
        this.messageSize = messageSize;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Instant getEventProcessingStartTime() {
        return eventProcessingStartTime;
    }

    public void setEventProcessingStartTime(Instant eventProcessingStartTime) {
        this.eventProcessingStartTime = eventProcessingStartTime;
    }

    public int getMessageTransferCount() {
        return messageTransferCount;
    }

    public void setMessageTransferCount(int messageTransferCount) {
        this.messageTransferCount = messageTransferCount;
    }

    public Instant getMessageSendStartInstant() {
        return messageSendStartInstant;
    }

    public void setMessageSendStartInstant(Instant messageSendStartInstant) {
        this.messageSendStartInstant = messageSendStartInstant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "InterProcessingPlantHandoverPacket{" +
                "actionableTask=" + actionableTask +
                ", upstreamFulfillmentTaskDetails=" + upstreamFulfillmentTaskDetails +
                ", messageIdentifier='" + messageIdentifier + '\'' +
                ", messageSize=" + messageSize +
                ", eventProcessingStartTime=" + eventProcessingStartTime +
                ", messageTransferCount=" + messageTransferCount +
                ", messageSendStartInstant=" + messageSendStartInstant +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
