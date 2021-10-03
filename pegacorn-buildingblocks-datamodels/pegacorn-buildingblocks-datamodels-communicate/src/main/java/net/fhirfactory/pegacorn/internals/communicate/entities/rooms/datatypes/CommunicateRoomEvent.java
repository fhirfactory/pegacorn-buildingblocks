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
package net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes;

import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.valuesets.CommunicateRoomEventTypeEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;

import java.util.Date;

public class CommunicateRoomEvent {
    private CommunicateRoomReference roomReference;
    private CommunicateRoomEventTypeEnum roomEventType;
    private String roomEventID;
    private CommunicateUserReference roomEventSource;
    private Date roomEventTimestamp;
    private CommunicateRoomEventContent roomEventContent;

    public CommunicateRoomReference getRoomReference() {
        return roomReference;
    }

    public void setRoomReference(CommunicateRoomReference roomReference) {
        this.roomReference = roomReference;
    }

    public CommunicateRoomEventTypeEnum getRoomEventType() {
        return roomEventType;
    }

    public void setRoomEventType(CommunicateRoomEventTypeEnum roomEventType) {
        this.roomEventType = roomEventType;
    }

    public Date getRoomEventTimestamp() {
        return roomEventTimestamp;
    }

    public void setRoomEventTimestamp(Date roomEventTimestamp) {
        this.roomEventTimestamp = roomEventTimestamp;
    }

    public CommunicateRoomEventContent getRoomEventContent() {
        return roomEventContent;
    }

    public void setRoomEventContent(CommunicateRoomEventContent roomEventContent) {
        this.roomEventContent = roomEventContent;
    }

    public String getRoomEventID() {
        return roomEventID;
    }

    public void setRoomEventID(String roomEventID) {
        this.roomEventID = roomEventID;
    }

    public CommunicateUserReference getRoomEventSource() {
        return roomEventSource;
    }

    public void setRoomEventSource(CommunicateUserReference roomEventSource) {
        this.roomEventSource = roomEventSource;
    }

    @Override
    public String toString() {
        return "CommunicateRoomEvent{" +
                "roomReference=" + roomReference +
                ", roomEventType=" + roomEventType +
                ", roomEventID=" + roomEventID +
                ", roomEventSource=" + roomEventSource +
                ", roomEventTimestamp=" + roomEventTimestamp +
                ", roomEventContent=" + roomEventContent +
                '}';
    }
}
