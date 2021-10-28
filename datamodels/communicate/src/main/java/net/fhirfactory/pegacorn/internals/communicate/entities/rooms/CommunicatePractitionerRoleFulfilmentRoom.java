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
package net.fhirfactory.pegacorn.internals.communicate.entities.rooms;

import net.fhirfactory.pegacorn.internals.communicate.entities.session.valuesets.CommunicateSessionFulfillmentStatusEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.practitionerrole.valuesets.PractitionerRoleFulfillmentRoomStatusEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunicatePractitionerRoleFulfilmentRoom extends CommunicateRoom {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicatePractitionerRoleFulfilmentRoom.class);
    private CommunicateSessionFulfillmentStatusEnum connectionStatus;
    private PractitionerRoleFulfillmentRoomStatusEnum fulfillmentStatus;

    private CommunicateUserID fulfillerPractitionerID;
    private CommunicateUserID clientPractitionerID;

    public CommunicatePractitionerRoleFulfilmentRoom(){
        super();
        this.fulfillerPractitionerID = null;
        this.clientPractitionerID = null;
    }

    public boolean hasFulfillerPractitionerID(){
        if(this.fulfillerPractitionerID == null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasClientPractitionerID(){
        if(this.clientPractitionerID == null){
            return(false);
        } else {
            return(true);
        }
    }

    @Override
    protected Logger getLogger(){return(LOG);}

    public CommunicateSessionFulfillmentStatusEnum getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(CommunicateSessionFulfillmentStatusEnum connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public CommunicateUserID getFulfillerPractitionerID() {
        return fulfillerPractitionerID;
    }

    public void setFulfillerPractitionerID(CommunicateUserID fulfillerPractitionerID) {
        this.fulfillerPractitionerID = fulfillerPractitionerID;
    }

    public CommunicateUserID getClientPractitionerID() {
        return clientPractitionerID;
    }

    public void setClientPractitionerID(CommunicateUserID clientPractitionerID) {
        this.clientPractitionerID = clientPractitionerID;
    }

    public PractitionerRoleFulfillmentRoomStatusEnum getFulfillmentStatus() {
        return fulfillmentStatus;
    }

    public void setFulfillmentStatus(PractitionerRoleFulfillmentRoomStatusEnum fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }
}
