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

public class TaskRouterStatusPacket implements Serializable {
    private PetasosParticipantControlStatusEnum participantStatus;
    private Integer localCacheSize;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant activityInstant;

    //
    // Constructor(s)
    //

    public TaskRouterStatusPacket(){
        this.participantStatus = null;
        this.activityInstant = null;
        this.localCacheSize = 0;
    }

    //
    // Getters (and Setters)
    //


    public Integer getLocalCacheSize() {
        return localCacheSize;
    }

    public void setLocalCacheSize(Integer localCacheSize) {
        this.localCacheSize = localCacheSize;
    }

    public PetasosParticipantControlStatusEnum getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(PetasosParticipantControlStatusEnum participantStatus) {
        this.participantStatus = participantStatus;
    }

    public Instant getActivityInstant() {
        return activityInstant;
    }

    public void setActivityInstant(Instant activityInstant) {
        this.activityInstant = activityInstant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskRouterStatusPacket{");
        sb.append(", participantStatus=").append(participantStatus);
        sb.append(", localCacheSize=").append(localCacheSize);
        sb.append(", activityInstant=").append(activityInstant);
        sb.append('}');
        return sb.toString();
    }
}
