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
package net.fhirfactory.pegacorn.internals.matrix.r061.api.rooms;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.datatypes.MCreationContent;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.datatypes.MInvite3PID;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.datatypes.MPowerLevelEventContent;
import net.fhirfactory.pegacorn.internals.matrix.r061.api.datatypes.MStateEvent;

import java.util.ArrayList;

public class MRoomCreation {
    private MRoomVisibilityEnum visibility;
    private String roomAliasName;
    private String name;
    private String topic;
    private ArrayList<String> invite;
    private ArrayList<MInvite3PID> invite3PID;
    private String roomVersion;
    private MCreationContent creationContent;
    private ArrayList<MStateEvent> initialState;
    private MRoomPresetEnum preset;
    private boolean direct;
    private MPowerLevelEventContent powerLevelContentOverride;

    @JsonProperty("visibility")
    public MRoomVisibilityEnum getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(MRoomVisibilityEnum visibility) {
        this.visibility = visibility;
    }

    @JsonProperty("room_alias_name")
    public String getRoomAliasName() {
        return roomAliasName;
    }

    @JsonProperty("room_alias_name")
    public void setRoomAliasName(String roomAliasName) {
        this.roomAliasName = roomAliasName;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("topic")
    public String getTopic() {
        return topic;
    }

    @JsonProperty("topic")
    public void setTopic(String topic) {
        this.topic = topic;
    }

    @JsonProperty("invite")
    public ArrayList<String> getInvite() {
        return invite;
    }

    @JsonProperty("invite")
    public void setInvite(ArrayList<String> invite) {
        this.invite = invite;
    }

    @JsonProperty("invite_3pid")
    public ArrayList<MInvite3PID> getInvite3PID() {
        return invite3PID;
    }

    @JsonProperty("invite_3pid")
    public void setInvite3PID(ArrayList<MInvite3PID> invite3PID) {
        this.invite3PID = invite3PID;
    }

    @JsonProperty("room_version")
    public String getRoomVersion() {
        return roomVersion;
    }

    @JsonProperty("room_version")
    public void setRoomVersion(String roomVersion) {
        this.roomVersion = roomVersion;
    }

    @JsonProperty("creation_content")
    public MCreationContent getCreationContent() {
        return creationContent;
    }

    @JsonProperty("creation_content")
    public void setCreationContent(MCreationContent creationContent) {
        this.creationContent = creationContent;
    }

    @JsonProperty("initial_state")
    public ArrayList<MStateEvent> getInitialState() {
        return initialState;
    }

    @JsonProperty("initial_state")
    public void setInitialState(ArrayList<MStateEvent> initialState) {
        this.initialState = initialState;
    }

    @JsonProperty("preset")
    public MRoomPresetEnum getPreset() {
        return preset;
    }

    @JsonProperty("preset")
    public void setPreset(MRoomPresetEnum preset) {
        this.preset = preset;
    }

    @JsonProperty("is_direct")
    public boolean isDirect() {
        return direct;
    }

    @JsonProperty("is_direct")
    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    @JsonProperty("power_level_content_override")
    public MPowerLevelEventContent getPowerLevelContentOverride() {
        return powerLevelContentOverride;
    }

    @JsonProperty("power_level_content_override")
    public void setPowerLevelContentOverride(MPowerLevelEventContent powerLevelContentOverride) {
        this.powerLevelContentOverride = powerLevelContentOverride;
    }
}
