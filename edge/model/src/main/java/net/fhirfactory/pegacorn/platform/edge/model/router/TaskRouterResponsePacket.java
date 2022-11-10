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
package net.fhirfactory.pegacorn.platform.edge.model.router;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.time.Instant;

public class TaskRouterResponsePacket implements Serializable {
    private TaskIdType successorTaskId;
    private TaskIdType routedTaskId;
    private PetasosParticipantControlStatusEnum participantStatus;
    private Integer localCacheSize;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant routingActivityInstant;
    private String responseCommentary;

    //
    // Constructor(s)
    //

    public TaskRouterResponsePacket(){
        this.successorTaskId = null;
        this.routedTaskId = null;
        this.participantStatus = null;
        this.routingActivityInstant = null;
        this.localCacheSize = 0;
        this.responseCommentary = null;
    }

    //
    // Getters (and Setters)
    //


    public String getResponseCommentary() {
        return responseCommentary;
    }

    public void setResponseCommentary(String responseCommentary) {
        this.responseCommentary = responseCommentary;
    }

    public Integer getLocalCacheSize() {
        return localCacheSize;
    }

    public void setLocalCacheSize(Integer localCacheSize) {
        this.localCacheSize = localCacheSize;
    }

    public TaskIdType getRoutedTaskId() {
        return routedTaskId;
    }

    public void setRoutedTaskId(TaskIdType routedTaskId) {
        this.routedTaskId = routedTaskId;
    }

    public TaskIdType getSuccessorTaskId() {
        return successorTaskId;
    }

    public void setSuccessorTaskId(TaskIdType successorTaskId) {
        this.successorTaskId = successorTaskId;
    }

    public PetasosParticipantControlStatusEnum getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(PetasosParticipantControlStatusEnum participantStatus) {
        this.participantStatus = participantStatus;
    }

    public Instant getRoutingActivityInstant() {
        return routingActivityInstant;
    }

    public void setRoutingActivityInstant(Instant routingActivityInstant) {
        this.routingActivityInstant = routingActivityInstant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskRouterResponsePacket{");
        sb.append("successorTaskId=").append(successorTaskId);
        sb.append(", routedTaskId=").append(routedTaskId);
        sb.append(", participantStatus=").append(participantStatus);
        sb.append(", localCacheSize=").append(localCacheSize);
        sb.append(", routingActivityInstant=").append(routingActivityInstant);
        sb.append(", responseCommentary=").append(responseCommentary);
        sb.append('}');
        return sb.toString();
    }
}
