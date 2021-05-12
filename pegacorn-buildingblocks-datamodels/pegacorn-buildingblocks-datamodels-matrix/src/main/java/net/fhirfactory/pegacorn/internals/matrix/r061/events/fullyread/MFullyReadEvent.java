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
package net.fhirfactory.pegacorn.internals.matrix.r061.events.fullyread;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.MatrixEvent;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MEventTypeEnum;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.fullyread.contenttypes.MFullyReadContentType;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.presence.contenttypes.MPresenceContentType;

public class MFullyReadEvent extends MatrixEvent {
    private MFullyReadContentType content;
    private String roomID;
    private MEventTypeEnum eventType;

    @JsonProperty("content")
    public MFullyReadContentType getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(MFullyReadContentType content) {
        this.content = content;
    }

    @JsonProperty("room_id")
    public String getRoomID() {
        return roomID;
    }

    @JsonProperty("room_id")
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @JsonProperty("type")
    public MEventTypeEnum getEventType() {
        return eventType;
    }

    @JsonProperty("type")
    public void setEventType(MEventTypeEnum eventType) {
        this.eventType = eventType;
    }
}
