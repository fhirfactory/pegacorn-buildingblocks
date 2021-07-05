/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.matrix.r061.events.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MatrixEventKey extends MatrixEvent{
    private String eventIdentifier;
    private String roomIdentifier;

    public MatrixEventKey(){
        this.eventIdentifier = null;
        this.roomIdentifier = null;
    }

    @JsonProperty("event_id")
    public String getEventIdentifier() {
        return eventIdentifier;
    }

    @JsonProperty("event_id")
    public void setEventIdentifier(String eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }

    @JsonProperty("room_id")
    public String getRoomIdentifier() {
        return roomIdentifier;
    }

    @JsonProperty("room_id")
    public void setRoomIdentifier(String roomIdentifier) {
        this.roomIdentifier = roomIdentifier;
    }

    @JsonIgnore
    public boolean hasEventIdentifier(){
        if(this.eventIdentifier == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasRoomIdentifier(){
        if(this.roomIdentifier == null){
            return(false);
        } else {
            return(true);
        }
    }
}
