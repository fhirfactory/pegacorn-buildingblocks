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
package net.fhirfactory.pegacorn.internals.matrix.r061.events.room.contenttypes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class MRoomPowerLevelsContentType {
    private int ban;
    private Map<String, Integer> eventPowerLevels;
    private int eventDefaultPowerLevel;
    private int invite;
    private int kick;
    private int redact;
    private Map<String, Integer> userPowerLevels;
    private int userDefaultPowerLevel;
    private Map<String, Integer> notificationPowerLevels;
    private int stateDefault;

    @JsonProperty("state_default")
    public int getStateDefault() {
        return stateDefault;
    }

    @JsonProperty("state_default")
    public void setStateDefault(int stateDefault) {
        this.stateDefault = stateDefault;
    }

    public int getBan() {
        return ban;
    }

    public void setBan(int ban) {
        this.ban = ban;
    }

    @JsonProperty("events")
    public Map<String, Integer> getEventPowerLevels() {
        return eventPowerLevels;
    }

    @JsonProperty("events")
    public void setEventPowerLevels(Map<String, Integer> eventPowerLevels) {
        this.eventPowerLevels = eventPowerLevels;
    }

    @JsonProperty("events_default")
    public int getEventDefaultPowerLevel() {
        return eventDefaultPowerLevel;
    }

    @JsonProperty("events_default")
    public void setEventDefaultPowerLevel(int eventDefaultPowerLevel) {
        this.eventDefaultPowerLevel = eventDefaultPowerLevel;
    }

    public int getInvite() {
        return invite;
    }

    public void setInvite(int invite) {
        this.invite = invite;
    }

    public int getKick() {
        return kick;
    }

    public void setKick(int kick) {
        this.kick = kick;
    }

    public int getRedact() {
        return redact;
    }

    public void setRedact(int redact) {
        this.redact = redact;
    }

    @JsonProperty("users")
    public Map<String, Integer> getUserPowerLevels() {
        return userPowerLevels;
    }

    @JsonProperty("users")
    public void setUserPowerLevels(Map<String, Integer> userPowerLevels) {
        this.userPowerLevels = userPowerLevels;
    }

    @JsonProperty("users_default")
    public int getUserDefaultPowerLevel() {
        return userDefaultPowerLevel;
    }

    @JsonProperty("users_default")
    public void setUserDefaultPowerLevel(int userDefaultPowerLevel) {
        this.userDefaultPowerLevel = userDefaultPowerLevel;
    }

    @JsonProperty("notifications")
    public Map<String, Integer> getNotificationPowerLevels() {
        return notificationPowerLevels;
    }

    @JsonProperty("notifications")
    public void setNotificationPowerLevels(Map<String, Integer> notificationPowerLevels) {
        this.notificationPowerLevels = notificationPowerLevels;
    }
}
