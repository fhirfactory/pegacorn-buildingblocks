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
package net.fhirfactory.pegacorn.internals.communicate.entities.practitionerrole;

import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.session.datatypes.CommunicateSessionID;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PractitionerRoleESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTTypesEnum;
import org.hl7.fhir.r4.model.PractitionerRole;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CommunicatePractitionerRole extends PractitionerRoleESR {
    private CommunicateRoomReference centralRoom;
    private CommunicateUserReference surrogateCommunicateUser;
    private ArrayList<CommunicateSessionID> practitionerRoleSessions;

    @Inject
    private IdentifierESDTTypesEnum identifierESDTTypesEnum;

    public CommunicatePractitionerRole(){
        super();
        this.centralRoom = null;
        this.surrogateCommunicateUser = null;
    }

    public CommunicatePractitionerRole(PractitionerRole practitionerRole){
        super();
        this.centralRoom = null;
        this.surrogateCommunicateUser = null;
    }

    public CommunicateRoomReference getCentralRoom() {
        return centralRoom;
    }

    public void setCentralRoom(CommunicateRoomReference centralRoom) {
        this.centralRoom = centralRoom;
    }

    public List<CommunicateSessionID> getPractitionerRoleSessions() {
        return (this.practitionerRoleSessions);
    }

    public void setPractitionerRoleSessions(List<CommunicateSessionID> sideRooms) {
        this.practitionerRoleSessions = new ArrayList<>();
        this.practitionerRoleSessions.addAll(sideRooms);
    }

    public CommunicateUserReference getSurrogateCommunicateUser() {
        return surrogateCommunicateUser;
    }

    public void setSurrogateCommunicateUser(CommunicateUserReference surrogateCommunicateUser) {
        this.surrogateCommunicateUser = surrogateCommunicateUser;
    }

    protected IdentifierESDTTypesEnum getCommonIdentifierESDTTypes() {
        return identifierESDTTypesEnum;
    }

    protected void setCommonIdentifierESDTTypes(IdentifierESDTTypesEnum identifierESDTTypesEnum) {
        this.identifierESDTTypesEnum = identifierESDTTypesEnum;
    }

}
