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

import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CommunicatePractitionerRoleCentralRoom extends CommunicateRoom {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicatePractitionerRoleCentralRoom.class);
    private ConcurrentHashMap<CommunicateUserID, List<CommunicateRoomReference>> fullfillerMappedRooms;
    private ConcurrentHashMap<CommunicateUserID, List<CommunicateRoomReference>> clientMappedRooms;

    @Override
    protected Logger getLogger(){return(LOG);}

    public CommunicatePractitionerRoleCentralRoom(){
        super();
        this.fullfillerMappedRooms = new ConcurrentHashMap<>();
        this.clientMappedRooms = new ConcurrentHashMap<>();
    }

    @Override
    public String toString() {
        return "CommunicatePractitionerRoleCentralRoom{" +
                "fullfillerMappedRooms=" + fullfillerMappedRooms +
                ", clientMappedRooms=" + clientMappedRooms +
                '}';
    }

    public void addCommunicatePractitionerRoleFulfillmentRoom( CommunicatePractitionerRoleFulfilmentRoom fulfilmentRoom){
        getLogger().debug(".addCommunicatePractitionerRoleFulfillmentRoom(): Entry, fulfilmentRoom->{}", fulfilmentRoom);
        if(fulfilmentRoom == null){
            getLogger().debug(".addCommunicatePractitionerRoleFulfillmentRoom(): Exit, fulfilmentRoom is null, doing nothing");
            return;
        }
        if(!fulfilmentRoom.hasClientPractitionerID()){

        }
    }
}
