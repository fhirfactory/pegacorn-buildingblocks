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
package net.fhirfactory.pegacorn.internals.communicate.entities.practitioner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.session.datatypes.CommunicateSessionID;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PractitionerESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTTypesEnum;

import java.util.ArrayList;
import java.util.List;

public class CommunicatePractitioner extends PractitionerESR {
    private List<CommunicateSessionID> participatingSessions;
    private CommunicateRoomReference myCallsRoom;
    private CommunicateRoomReference myMediaRoom;
    private CommunicateRoomReference myResultNotificationsRoom;
    private CommunicateRoomReference myGeneralNotificationsRoom;
    private CommunicateUserReference actualUser;

    public CommunicatePractitioner(){
        super();
        this.participatingSessions = new ArrayList<>();
        this.myCallsRoom = null;
        this.myMediaRoom = null;
        this.myResultNotificationsRoom = null;
        this.myGeneralNotificationsRoom = null;
        this.actualUser = null;
    }

    public List<CommunicateSessionID> getParticipatingSessions() {
        return this.participatingSessions;
    }

    public void setParticipatingSessions(List<CommunicateSessionID> sessions) {
        this.participatingSessions.clear();
        this.participatingSessions.addAll(sessions);
    }

    public CommunicateRoomReference getMyCallsRoom() {
        return myCallsRoom;
    }

    public void setMyCallsRoom(CommunicateRoomReference myCallsRoom) {
        this.myCallsRoom = myCallsRoom;
    }

    public CommunicateRoomReference getMyMediaRoom() {
        return myMediaRoom;
    }

    public void setMyMediaRoom(CommunicateRoomReference myMediaRoom) {
        this.myMediaRoom = myMediaRoom;
    }

    public CommunicateRoomReference getMyResultNotificationsRoom() {
        return myResultNotificationsRoom;
    }

    public void setMyResultNotificationsRoom(CommunicateRoomReference myResultNotificationsRoom) {
        this.myResultNotificationsRoom = myResultNotificationsRoom;
    }

    public CommunicateRoomReference getMyGeneralNotificationsRoom() {
        return myGeneralNotificationsRoom;
    }

    public void setMyGeneralNotificationsRoom(CommunicateRoomReference myGeneralNotificationsRoom) {
        this.myGeneralNotificationsRoom = myGeneralNotificationsRoom;
    }

    @JsonIgnore
    public String getMatrixUserID() {
        IdentifierESDT matrixUserIdentifier = getIdentifierWithType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_MATRIX_USER_ID);
        if(matrixUserIdentifier != null){
            return (matrixUserIdentifier.getValue());
        } else {
            return(null);
        }
    }

    @JsonIgnore
    public void setMatrixUserID(String matrixUserID) {
        IdentifierESDT identifier = getIdentifierWithType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_MATRIX_USER_ID);
        if(identifier == null){
            IdentifierESDT newIdentifier = new IdentifierESDT();
            newIdentifier.setUse(IdentifierESDTUseEnum.SECONDARY);
            newIdentifier.setType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_MATRIX_USER_ID.getIdentifierType());
            newIdentifier.setValue(matrixUserID);
            this.getIdentifiers().add(newIdentifier);
        } else {
            identifier.setValue(matrixUserID);
        }
    }

    public CommunicateUserReference getActualUser() {
        return actualUser;
    }

    public void setActualUser(CommunicateUserReference actualUser) {
        this.actualUser = actualUser;
    }
}
