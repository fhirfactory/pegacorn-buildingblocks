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

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.time.Instant;

public class TaskQueueStatus {
    private boolean queuedTasks;
    private int queueSize;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastUpdate;
    private TaskIdType lastTask;

    //
    // Constructor(s)
    //

    public TaskQueueStatus(){
        this.queuedTasks = false;
        this.queueSize = 0;
        this.lastUpdate = Instant.now();
        this.lastTask = null;
    }

    //
    // Getters and Setters
    //

    public TaskIdType getLastTask() {
        return lastTask;
    }

    public void setLastTask(TaskIdType lastTask) {
        this.lastTask = lastTask;
    }

    public boolean isQueuedTasks() {
        return queuedTasks;
    }

    public void setQueuedTasks(boolean queuedTasks) {
        this.queuedTasks = queuedTasks;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskQueueStatus{");
        sb.append("queuedTasks=").append(queuedTasks);
        sb.append(", queueSize=").append(queueSize);
        sb.append(", lastUpdate=").append(lastUpdate);
        sb.append(", lastTask=").append(lastTask);
        sb.append('}');
        return sb.toString();
    }
}
