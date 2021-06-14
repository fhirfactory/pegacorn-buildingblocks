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

import java.util.HashMap;

public class CommunicateRoomPowerLevels {
    private Integer banPowerLevel;
    private Integer defaultEventPowerLevel;
    private Integer invitePowerLevel;
    private Integer kickPowerLevel;
    private Integer redactPowerLevel;
    private Integer defaultStatePowerLevel;
    private Integer defaultUsersPowerLevel;
    private HashMap<String, Integer> notificationsPowerLevel;
    private HashMap<CommunicateUserReference, Integer> userPowerLevels;
    private HashMap<CommunicateRoomEventTypeEnum, Integer> eventPowerLevels;

    public CommunicateRoomPowerLevels(){
        this.notificationsPowerLevel = new HashMap<>();
        this.userPowerLevels = new HashMap<>();
        this.eventPowerLevels = new HashMap<>();
        this.banPowerLevel = null;
        this.defaultEventPowerLevel = null;
        this.defaultStatePowerLevel = null;
        this.defaultUsersPowerLevel = null;
        this.invitePowerLevel = null;
        this.kickPowerLevel = null;
    }

    public Integer getBanPowerLevel() {
        return banPowerLevel;
    }

    public void setBanPowerLevel(Integer banPowerLevel) {
        this.banPowerLevel = banPowerLevel;
    }

    public Integer getInvitePowerLevel() {
        return invitePowerLevel;
    }

    public void setInvitePowerLevel(Integer invitePowerLevel) {
        this.invitePowerLevel = invitePowerLevel;
    }

    public Integer getKickPowerLevel() {
        return kickPowerLevel;
    }

    public void setKickPowerLevel(Integer kickPowerLevel) {
        this.kickPowerLevel = kickPowerLevel;
    }

    public Integer getRedactPowerLevel() {
        return redactPowerLevel;
    }

    public void setRedactPowerLevel(Integer redactPowerLevel) {
        this.redactPowerLevel = redactPowerLevel;
    }

    public Integer getDefaultStatePowerLevel() {
        return defaultStatePowerLevel;
    }

    public void setDefaultStatePowerLevel(Integer defaultStatePowerLevel) {
        this.defaultStatePowerLevel = defaultStatePowerLevel;
    }

    public Integer getDefaultUsersPowerLevel() {
        return defaultUsersPowerLevel;
    }

    public void setDefaultUsersPowerLevel(Integer defaultUsersPowerLevel) {
        this.defaultUsersPowerLevel = defaultUsersPowerLevel;
    }

    public Integer getDefaultEventPowerLevel() {
        return defaultEventPowerLevel;
    }

    public void setDefaultEventPowerLevel(Integer defaultEventPowerLevel) {
        this.defaultEventPowerLevel = defaultEventPowerLevel;
    }

    public HashMap<String, Integer> getNotificationsPowerLevel() {
        return notificationsPowerLevel;
    }

    public void setNotificationsPowerLevel(HashMap<String, Integer> notificationsPowerLevel) {
        this.notificationsPowerLevel = notificationsPowerLevel;
    }

    public HashMap<CommunicateUserReference, Integer> getUserPowerLevels() {
        return userPowerLevels;
    }

    public void setUserPowerLevels(HashMap<CommunicateUserReference, Integer> userPowerLevels) {
        this.userPowerLevels = userPowerLevels;
    }

    public HashMap<CommunicateRoomEventTypeEnum, Integer> getEventPowerLevels() {
        return eventPowerLevels;
    }

    public void setEventPowerLevels(HashMap<CommunicateRoomEventTypeEnum, Integer> eventPowerLevels) {
        this.eventPowerLevels = eventPowerLevels;
    }

    @Override
    public String toString() {
        return "CommunicateRoomPowerLevelEventContent{" +
                "banPowerLevel=" + banPowerLevel +
                ", defaultEventPowerLevel=" + defaultEventPowerLevel +
                ", invitePowerLevel=" + invitePowerLevel +
                ", kickPowerLevel=" + kickPowerLevel +
                ", redactPowerLevel=" + redactPowerLevel +
                ", defaultStatePowerLevel=" + defaultStatePowerLevel +
                ", defaultUsersPowerLevel=" + defaultUsersPowerLevel +
                ", notificationsPowerLevel=" + notificationsPowerLevel +
                ", userPowerLevels=" + userPowerLevels +
                ", eventPowerLevels=" + eventPowerLevels +
                '}';
    }
}
