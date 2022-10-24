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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.queue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ParticipantTaskQueue {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipantTaskQueue.class);
    private PetasosParticipantId participantId;
    private ParticipantTaskQueueEntry queueHead;
    private Object queueLock;

    private Integer size;

    //
    // Constructor(s)
    //

    public ParticipantTaskQueue(){
        this.participantId = null;
        this.queueHead = null;
        this.queueLock = new Object();
        this.size = 0;
    }

    public ParticipantTaskQueue(PetasosParticipantId participantId){
        this.participantId = participantId;
        this.queueHead = null;
        this.queueLock = new Object();
        this.size = 0;
    }

    //
    // Getters and Setters
    //

    protected Object getQueueLock(){
        return(queueLock);
    }

    public PetasosParticipantId getParticipantId() {
        return participantId;
    }

    public void setParticipantId(PetasosParticipantId participantId) {
        this.participantId = participantId;
    }

    @JsonIgnore
    protected Logger getLogger(){
        return(LOG);
    }

    public Integer getSize(){
        return(size);
    }

    //
    // Business Methods
    //

    private void insertSingleEntry(ParticipantTaskQueueEntry entry) {
        getLogger().debug(".insertSingleEntry(): Entry, entry->{}", entry);
        if (entry == null) {
            getLogger().debug(".insertSingleEntry(): Exit, entry is null, exiting");
            return;
        }
        if (entry.getTaskId() == null || entry.getSequenceNumber() == null) {
            getLogger().debug(".insertSingleEntry(): Exit, taskId or sequenceNumber is null, exiting");
            return;
        }
        size += 1;
        if (queueHead == null) {
            queueHead = entry;
            getLogger().debug(".insertSingleEntry(): Exit, queue is empty, assigning entry as head");
            return;
        }
        if (entry.compareTo(queueHead) < 0) {
            getLogger().debug(".insertSingleEntry(): Exit, entry is 'less-then' queueHead, reassigning queueHead to entry");
            entry.setNextNode(queueHead);
            queueHead = entry;
            return;
        }
        ParticipantTaskQueueEntry nextEntry = queueHead.getNextNode();
        ParticipantTaskQueueEntry currentEntry = queueHead;
        while (nextEntry != null) {
            if (nextEntry.compareTo(entry) <= 0) {
                currentEntry = nextEntry;
                nextEntry = nextEntry.getNextNode();
            } else {
                break;
            }
        }
        getLogger().debug(".insertSingleEntry(): Inserting entry AFTER->{} and BEFORE->{}", currentEntry, nextEntry);
        currentEntry.setNextNode(entry);
        entry.setNextNode(nextEntry);
        entry.setPreviousNode(currentEntry);
        if (nextEntry != null) {
            nextEntry.setPreviousNode(entry);
        }
        getLogger().debug(".insertSingleEntry(): Exit");
    }

    @JsonIgnore
    public void insertEntry(ParticipantTaskQueueEntry entry){
        getLogger().debug(".insertEntry(): Entry, entry->{}", entry);
        if(entry == null){
            getLogger().debug(".insertEntry(): Exit, entry is null, exiting");
            return;
        }
        synchronized (getQueueLock()){
            insertSingleEntry(entry);
        }
        getLogger().debug(".insertEntry(): Exit");
    }

    @JsonIgnore
    public void insertEntryList(List<ParticipantTaskQueueEntry> entryList){
        getLogger().debug(".insertEntryList(): Entry, entryList->{}", entryList);
        if(entryList == null){
            getLogger().debug(".insertEntryList(): Exit, entryList is null");
            return;
        }
        if(entryList.isEmpty()){
            getLogger().debug(".insertEntryList(): Exit, entryList is empty");
            return;
        }
        synchronized (getQueueLock()) {
            for (ParticipantTaskQueueEntry currentListEntry : entryList) {
                if (currentListEntry != null) {
                    insertEntry(currentListEntry);
                }
            }
        }
        getLogger().debug(".insertEntryList(): Exit");
    }

    public ParticipantTaskQueueEntry peek(){
        getLogger().debug(".peek(): Entry");
        getLogger().debug(".peek(): Exit, returning queueHead->{}",queueHead);
        return(queueHead);
    }

    public ParticipantTaskQueueEntry poll(){
        getLogger().debug(".poll(): Entry");
        ParticipantTaskQueueEntry currentHead = null;
        synchronized (getQueueLock()){
            currentHead = queueHead;
            if(currentHead != null) {
                queueHead = currentHead.getNextNode();
                if (queueHead != null) {

                    queueHead.setPreviousNode(null);
                }
            }
            size -= 1;
        }
        getLogger().debug(".poll(): Exit, entry->{}", currentHead);
        return(currentHead);
    }

    public boolean hasEntries(){
        if(queueHead != null){
            return(true);
        }
        return(false);
    }

    //
    // toString
    //

    private String queueToString(){
        StringBuilder sb = new StringBuilder();
        ParticipantTaskQueueEntry currentEntry = queueHead;
        if(currentEntry == null){
            sb.append("queueHead=null");
            return(sb.toString());
        }
        sb.append("queueHead=").append(currentEntry);
        currentEntry = queueHead.getNextNode();
        while(currentEntry != null){
            sb.append(", ").append(currentEntry);
        }
        return(sb.toString());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LocalParticipantTaskQueue{");
        sb.append("participantId=").append(participantId);
        sb.append(", taskQueue=").append(queueToString());
        sb.append(", queueLock=").append(queueLock);
        sb.append('}');
        return sb.toString();
    }
}
