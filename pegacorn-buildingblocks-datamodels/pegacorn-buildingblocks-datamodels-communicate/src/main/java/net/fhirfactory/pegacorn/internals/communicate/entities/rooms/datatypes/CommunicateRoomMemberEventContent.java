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

import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;

public class CommunicateRoomMemberEventContent extends CommunicateRoomEventContent{
    private CommunicateUserReference eventTargetUser;
    private CommunicateUserReference eventSourceUser;

    public CommunicateUserReference getEventTargetUser() {
        return eventTargetUser;
    }

    public void setEventTargetUser(CommunicateUserReference eventTargetUser) {
        this.eventTargetUser = eventTargetUser;
    }

    public CommunicateUserReference getEventSourceUser() {
        return eventSourceUser;
    }

    public void setEventSourceUser(CommunicateUserReference eventSourceUser) {
        this.eventSourceUser = eventSourceUser;
    }

    @Override
    public String toString() {
        return "CommunicateRoomMemberEventContent{" +
                "eventTargetUser=" + eventTargetUser +
                ", eventSourceUser=" + eventSourceUser +
                '}';
    }
}
