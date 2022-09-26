/*
 * Copyright (c) 2022 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.queue;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskSequenceNumber;
import org.jetbrains.annotations.NotNull;

public class ParticipantTaskQueueEntry implements Comparable<ParticipantTaskQueueEntry> {
    private TaskSequenceNumber sequenceNumber;
    private TaskIdType taskId;
    private ParticipantTaskQueueEntry nextNode;
    private ParticipantTaskQueueEntry previousNode;

    //
    // Constructor(s)
    //

    public ParticipantTaskQueueEntry(){
        sequenceNumber = null;
        taskId = null;
    }

    //
    // Getters and Setters
    //

    public TaskSequenceNumber getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(TaskSequenceNumber sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public TaskIdType getTaskId() {
        return taskId;
    }

    public void setTaskId(TaskIdType taskId) {
        this.taskId = taskId;
    }

    public ParticipantTaskQueueEntry getNextNode() {
        return nextNode;
    }

    public void setNextNode(ParticipantTaskQueueEntry nextNode) {
        this.nextNode = nextNode;
    }

    public ParticipantTaskQueueEntry getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(ParticipantTaskQueueEntry previousNode) {
        this.previousNode = previousNode;
    }

    //
    // Comparison
    //

    @Override
    public int compareTo(@NotNull ParticipantTaskQueueEntry other) {
        if(other == null){
            return(1);
        }
        if(getSequenceNumber().getMajorSequenceNumber() > other.getSequenceNumber().getMajorSequenceNumber()){
            return(1);
        }
        if(getSequenceNumber().getMajorSequenceNumber() < other.getSequenceNumber().getMinorSequenceNumber()){
            return(-1);
        }
        // major sequence numbers are equal, so check minor sequence number
        if(getSequenceNumber().getMinorSequenceNumber() > other.getSequenceNumber().getMinorSequenceNumber()){
            return(1);
        }
        if(getSequenceNumber().getMinorSequenceNumber() < other.getSequenceNumber().getMinorSequenceNumber()){
            return(-1);
        }
        // minor sequence numbers are equal, so return zero
        return(0);
    }


    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParticipantTaskQueueEntry{");
        sb.append("sequenceNumber=").append(sequenceNumber);
        sb.append(", taskId=").append(taskId);
        if(getPreviousNode() != null) {
            sb.append(", previousEntry=").append(getPreviousNode().getSequenceNumber());
        } else {
            sb.append(", nextEntry=").append("null");
        }
        if(getNextNode() != null){
            sb.append(", nextEntry=").append(getNextNode().getSequenceNumber());
        } else {
            sb.append(", nextEntry=").append("null");
        }
        sb.append('}');
        return sb.toString();
    }
}
