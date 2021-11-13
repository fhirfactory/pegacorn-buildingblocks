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
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.time.Instant;

public class InterProcessingPlantHandoverResponsePacket implements Serializable {
    private TaskIdType downstreamActionableTaskId;
    private TaskIdType actionableTaskId;
    private String messageIdentifier;
    private Integer messageSize;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant messageSendFinishInstant;
    private InterProcessingPlantHandoverPacketStatusEnum status;
    private String statusReason;

    //
    // Constructor(s)
    //

    public InterProcessingPlantHandoverResponsePacket(){
        this.downstreamActionableTaskId = null;
        this.actionableTaskId = null;
        this.messageIdentifier = null;
        this.messageSize = null;
        this.messageSendFinishInstant = null;
        this.status = null;
        this.statusReason = null;
    }

    //
    // Getters (and Setters)
    //

    public InterProcessingPlantHandoverPacketStatusEnum getStatus() {
        return status;
    }

    public TaskIdType getActionableTaskId() {
        return actionableTaskId;
    }

    public void setActionableTaskId(TaskIdType actionableTaskId) {
        this.actionableTaskId = actionableTaskId;
    }

    public void setStatus(InterProcessingPlantHandoverPacketStatusEnum status) {
        this.status = status;
    }

    public TaskIdType getDownstreamActionableTaskId() {
        return downstreamActionableTaskId;
    }

    public void setDownstreamActionableTaskId(TaskIdType downstreamActionableTaskId) {
        this.downstreamActionableTaskId = downstreamActionableTaskId;
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

    //
    // To String
    //

    @Override
    public String toString() {
        return "InterProcessingPlantHandoverResponsePacket{" +
                "downstreamActionableTaskId=" + downstreamActionableTaskId +
                ", actionableTaskId=" + actionableTaskId +
                ", messageIdentifier='" + messageIdentifier + '\'' +
                ", messageSize=" + messageSize +
                ", messageSendFinishInstant=" + messageSendFinishInstant +
                ", status=" + status +
                ", statusReason='" + statusReason + '\'' +
                '}';
    }
}
