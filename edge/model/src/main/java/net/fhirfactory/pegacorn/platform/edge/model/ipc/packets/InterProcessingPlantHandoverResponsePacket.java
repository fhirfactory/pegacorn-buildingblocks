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

import net.fhirfactory.pegacorn.petasos.model.pathway.ActivityID;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public class InterProcessingPlantHandoverResponsePacket implements Serializable {
    private ActivityID activityID;
    private String messageIdentifier;
    private Integer messageSize;
    private Instant messageSendFinishInstant;
    private InterProcessingPlantHandoverPacketStatusEnum status;
    private String statusReason;

    public InterProcessingPlantHandoverPacketStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InterProcessingPlantHandoverPacketStatusEnum status) {
        this.status = status;
    }

    public ActivityID getActivityID() {
        return activityID;
    }

    public void setActivityID(ActivityID activityID) {
        this.activityID = activityID;
    }

    public String getMessageIdentifier() {
        return messageIdentifier;
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

    public Instant getMessageSendFinishInstant() {
        return messageSendFinishInstant;
    }

    public void setMessageSendFinishInstant(Instant messageSendFinishInstant) {
        this.messageSendFinishInstant = messageSendFinishInstant;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    @Override
    public String toString() {
        return "net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket{" +
                "activityID=" + activityID +
                ", messageIdentifier='" + messageIdentifier + '\'' +
                ", messageSize=" + messageSize +
                ", messageSendFinishInstant=" + messageSendFinishInstant +
                ", status=" + status +
                ", statusReason='" + statusReason + '\'' +
                '}';
    }
}
