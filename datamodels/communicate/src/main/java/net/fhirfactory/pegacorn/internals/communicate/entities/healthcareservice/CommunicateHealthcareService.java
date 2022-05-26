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
package net.fhirfactory.pegacorn.internals.communicate.entities.healthcareservice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.internals.communicate.entities.session.CommunicateSession;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserID;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.HealthcareServiceESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTUseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CommunicateHealthcareService extends HealthcareServiceESR {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateHealthcareService.class);

    private CommunicateRoomReference centralRoom;
    private CommunicateUserReference surrogateCommunicateUser;
    private List<CommunicateSession> practitionerConnections;

    @Inject
    private IdentifierESDTTypesEnum identifierESDTTypesEnum;

    public CommunicateHealthcareService(){
        super();
        this.surrogateCommunicateUser = null;
        this.centralRoom = null;
        this.practitionerConnections = new ArrayList<>();
    }

    @Override
    protected Logger getLogger(){return(LOG);}

    public CommunicateRoomReference getCentralRoom() {
        return centralRoom;
    }

    public void setCentralRoom(CommunicateRoomReference centralRoom) {
        this.centralRoom = centralRoom;
    }

    public List<CommunicateSession> getPractitionerSessions() {
        return (this.practitionerConnections);
    }

    public void setPractitionerSessions(List<CommunicateSession> sideRooms) {
        this.practitionerConnections = new ArrayList<>();
        this.practitionerConnections.addAll(sideRooms);
    }

    public CommunicateUserReference getSurrogateCommunicateUser() {
        return surrogateCommunicateUser;
    }

    public void setSurrogateCommunicateUser(CommunicateUserReference surrogateCommunicateUser) {
        this.surrogateCommunicateUser = surrogateCommunicateUser;
    }

    @JsonIgnore
    public HealthcareServiceESR getHealthcareServiceESR(){
        HealthcareServiceESR healthcareService = new HealthcareServiceESR();

        return(healthcareService);
    }

    @JsonIgnore
    public void setHealthcareServiceESR(HealthcareServiceESR healthcareService){

    }

    @JsonIgnore
    public CommunicateUserID getCommunicateUserID() {
        IdentifierESDT identifier = getIdentifierWithType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_MATRIX_USER_ID);
        if (identifier != null) {
            CommunicateUserID userID = new CommunicateUserID(identifier.getValue());
            return(userID);
        }
        return (null);
    }

    @JsonIgnore
    public void setCommunicateUserID(CommunicateUserID matrixUserID) {
        this.surrogateCommunicateUser.setUserID(matrixUserID);
        IdentifierESDT identifier = getIdentifierWithType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_MATRIX_USER_ID);
        if(identifier == null){
            IdentifierESDT newIdentifier = new IdentifierESDT();
            newIdentifier.setUse(IdentifierESDTUseEnum.SECONDARY);
            newIdentifier.setType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_MATRIX_USER_ID.getIdentifierType());
            newIdentifier.setValue(matrixUserID.getValue());
            this.getIdentifiers().add(newIdentifier);
        } else {
            identifier.setValue(matrixUserID.getValue());
        }
    }
}
