/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.petasos.participant.queue;

import java.io.Serializable;

public class PetasosParticipantTaskQueueStatus implements Serializable {
    private TaskQueueStatus localQueueStatus;
    private TaskQueueStatus centralQueueStatus;
    private boolean pendingTasksOffloaded;
    private boolean pendingTasksPersisted;

    //
    // Constructor(s)
    //

    public PetasosParticipantTaskQueueStatus(){
        this.localQueueStatus = new TaskQueueStatus();
        this.centralQueueStatus = new TaskQueueStatus();
        this.pendingTasksPersisted = false;
        this.pendingTasksOffloaded = false;
    }

    //
    // Getters and Setters
    //

    public boolean isPendingTasksOffloaded() {
        return pendingTasksOffloaded;
    }

    public void setPendingTasksOffloaded(boolean pendingTasksOffloaded) {
        this.pendingTasksOffloaded = pendingTasksOffloaded;
    }

    public boolean isPendingTasksPersisted() {
        return pendingTasksPersisted;
    }

    public void setPendingTasksPersisted(boolean pendingTasksPersisted) {
        this.pendingTasksPersisted = pendingTasksPersisted;
    }

    public TaskQueueStatus getLocalQueueStatus() {
        return localQueueStatus;
    }

    public void setLocalQueueStatus(TaskQueueStatus localQueueStatus) {
        this.localQueueStatus = localQueueStatus;
    }

    public TaskQueueStatus getCentralQueueStatus() {
        return centralQueueStatus;
    }

    public void setCentralQueueStatus(TaskQueueStatus centralQueueStatus) {
        this.centralQueueStatus = centralQueueStatus;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PetasosParticipantTaskQueueStatus{");
        sb.append("localQueueStatus=").append(localQueueStatus);
        sb.append(", centralQueueStatus=").append(centralQueueStatus);
        sb.append(", pendingTasksOffloaded=").append(pendingTasksOffloaded);
        sb.append(", pendingTasksPersisted=").append(pendingTasksPersisted);
        sb.append('}');
        return sb.toString();
    }
}
