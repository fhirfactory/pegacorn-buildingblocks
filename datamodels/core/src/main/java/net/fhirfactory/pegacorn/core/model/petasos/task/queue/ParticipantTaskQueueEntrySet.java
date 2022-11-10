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
package net.fhirfactory.pegacorn.core.model.petasos.task.queue;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ParticipantTaskQueueEntrySet implements Serializable {
    private Set<ParticipantTaskQueueEntry> queueEntries;

    //
    // Constructor(s)
    //

    public ParticipantTaskQueueEntrySet(){
        this.queueEntries = new HashSet();
    }

    //
    // Getters and Setters
    //

    public Set<ParticipantTaskQueueEntry> getQueueEntries() {
        return queueEntries;
    }

    public void setQueueEntries(Set<ParticipantTaskQueueEntry> queueEntries) {
        this.queueEntries = queueEntries;
    }

    // Helper Methods

    @JsonIgnore
    public void addQueueEntry(ParticipantTaskQueueEntry entry){
        if(entry != null) {
            if (!getQueueEntries().contains(entry)) {
                getQueueEntries().add(entry);
            }
        }
    }

    @JsonIgnore
    public void removeQueueEntry(ParticipantTaskQueueEntry entry){
        if(entry != null) {
            if (getQueueEntries().contains(entry)) {
                getQueueEntries().remove(entry);
            }
        }
    }

    @JsonIgnore
    public Integer getSetSize(){
        Integer size = getQueueEntries().size();
        return(size);
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParticipantTaskQueueEntrySet{");
        sb.append("queueEntries=").append(queueEntries);
        sb.append(", setSize=").append(getSetSize());
        sb.append('}');
        return sb.toString();
    }
}
