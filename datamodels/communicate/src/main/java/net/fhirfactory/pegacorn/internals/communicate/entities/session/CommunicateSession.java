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
package net.fhirfactory.pegacorn.internals.communicate.entities.session;

import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.session.datatypes.CommunicateSessionID;
import net.fhirfactory.pegacorn.internals.communicate.entities.session.datatypes.CommunicateSessionParticipant;
import net.fhirfactory.pegacorn.internals.communicate.entities.session.valuesets.CommunicateSessionTypeEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.CommunicateSessionESR;

import java.util.ArrayList;
import java.util.List;

public class CommunicateSession extends CommunicateSessionESR {
    private CommunicateSessionID sessionID;
    private List<CommunicateSessionParticipant> sessionParticipants;
    private CommunicateRoomReference sessionRoom;
    private CommunicateSessionTypeEnum sessionType;

    public CommunicateSession(){
        this.sessionParticipants = new ArrayList<>();
        this.sessionID = null;
        this.sessionRoom = null;
        this.sessionType = null;
    }

    public CommunicateSessionID getSessionID() {
        return sessionID;
    }

    public void setSessionID(CommunicateSessionID sessionID) {
        this.sessionID = sessionID;
    }

    public List<CommunicateSessionParticipant> getSessionParticipants() {
        return sessionParticipants;
    }

    public void setSessionParticipants(List<CommunicateSessionParticipant> sessionParticipants) {
        this.sessionParticipants = sessionParticipants;
    }

    public CommunicateRoomReference getSessionRoom() {
        return sessionRoom;
    }

    public void setSessionRoom(CommunicateRoomReference sessionRoom) {
        this.sessionRoom = sessionRoom;
    }

    public CommunicateSessionTypeEnum getSessionType() {
        return sessionType;
    }

    public void setSessionType(CommunicateSessionTypeEnum sessionType) {
        this.sessionType = sessionType;
    }

    @Override
    public String toString() {
        return "CommunicateSession{" +
                "sessionID=" + sessionID +
                ", sessionParticipants=" + sessionParticipants +
                ", sessionRoom=" + sessionRoom +
                ", sessionType=" + sessionType +
                '}';
    }
}
