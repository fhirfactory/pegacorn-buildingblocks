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

import net.fhirfactory.pegacorn.petasos.model.task.segments.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;

import java.io.Serializable;
import java.time.Instant;

public class InterProcessingPlantHandoverPacket implements Serializable {
    private UoW payloadPacket;
    private String payloadType;
    private TaskFulfillmentType petasosTaskFulfillment;
    private String messageIdentifier;
    private Integer messageSize;
    private Instant eventProcessingStartTime;
    private int messageTransferCount;
    private Instant messageSendStartInstant;
    private String source;
    private String target;

    public TaskFulfillmentType getActivityID() {
        return petasosTaskFulfillment;
    }

    public void setActivityID(TaskFulfillmentType activity) {
        this.petasosTaskFulfillment = activity;
    }

    public UoW getPayloadPacket() {
        return (payloadPacket);
    }

    public void setPayloadPacket(UoW payloadPacket) {
        this.payloadPacket = payloadPacket;
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

    public String getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
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

    @Override
    public String toString() {
        return "net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket{" +
                "payloadPacket=" + payloadPacket +
                ", payloadType='" + payloadType + '\'' +
                ", activityID=" + petasosTaskFulfillment +
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
