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
package net.fhirfactory.pegacorn.internals.communicate.entities.rooms.factories;

import net.fhirfactory.pegacorn.core.model.dates.EffectivePeriod;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.CommunicateRoom;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomStatus;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.valuesets.CommunicateRoomTypeEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.valuesets.CommunicateRoomStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class CommunicateRoomFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateRoomFactory.class);

    public CommunicateRoom newCommunicateRoom(String roomID,
                                              String roomName,
                                              String canonicalAlias,
                                              CommunicateRoomTypeEnum typeEnum,
                                              CommunicateRoomStatusEnum statusEnum ){
        LOG.debug(".newCommunicateRoom(): Entry, roomID->{}, roomName->{}, canonicalAlias->{}, typeEnum->{}, statusEnum->{}",
                roomID, roomName, canonicalAlias, typeEnum, statusEnum);
        CommunicateRoom room = new CommunicateRoom();
        room.setMatrixRoomID(roomID);
        room.setRoomType(typeEnum);
        room.setDisplayName(roomName);
        CommunicateRoomStatus newStatus = new CommunicateRoomStatus();
        EffectivePeriod newStatusEffectivePeriod = new EffectivePeriod();
        newStatusEffectivePeriod.setEffectiveStartDate(Date.from(Instant.now()));
        newStatus.setStatusValue(statusEnum);
        newStatus.setStatusPeriod(newStatusEffectivePeriod);
        room.setRoomStatus(newStatus);
        room.setCanonicalAlias(canonicalAlias);
        LOG.debug(".newCommunicateRoom(): Exit, room->{}", room);
        return(room);
    }
}
