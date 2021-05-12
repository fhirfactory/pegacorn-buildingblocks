package net.fhirfactory.pegacorn.internals.synapse.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SynapseRoom {
    private String roomID;
    private String name;
    private String canonicalAlias;
    private int joinedMembers;
    private int joinedLocalMembers;
    private String version;
    private String creator;
    private String encryption;
    private boolean federatable;
    private boolean publicRoom;
    private String joinRules;
    private String guestAccess;
    private String historyVisibility;
    private int stateEventsCount;

    @JsonProperty("room_id")
    public String getRoomID() {
        return roomID;
    }

    @JsonProperty("room_id")
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("canonical_alias")
    public String getCanonicalAlias() {
        return canonicalAlias;
    }

    @JsonProperty("canonical_alias")
    public void setCanonicalAlias(String canonicalAlias) {
        this.canonicalAlias = canonicalAlias;
    }

    @JsonProperty("joined_members")
    public int getJoinedMembers() {
        return joinedMembers;
    }

    @JsonProperty("joined_members")
    public void setJoinedMembers(int joinedMembers) {
        this.joinedMembers = joinedMembers;
    }

    @JsonProperty("joined_local_members")
    public int getJoinedLocalMembers() {
        return joinedLocalMembers;
    }

    @JsonProperty("joined_local_members")
    public void setJoinedLocalMembers(int joinedLocalMembers) {
        this.joinedLocalMembers = joinedLocalMembers;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("creator")
    public String getCreator() {
        return creator;
    }

    @JsonProperty("creator")
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @JsonProperty("encryption")
    public String getEncryption() {
        return encryption;
    }

    @JsonProperty("encryption")
    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    @JsonProperty("federatable")
    public boolean isFederatable() {
        return federatable;
    }

    @JsonProperty("federatable")
    public void setFederatable(boolean federatable) {
        this.federatable = federatable;
    }

    @JsonProperty("public")
    public boolean isPublicRoom() {
        return publicRoom;
    }

    @JsonProperty("public")
    public void setPublicRoom(boolean publicRoom) {
        this.publicRoom = publicRoom;
    }

    @JsonProperty("join_rules")
    public String getJoinRules() {
        return joinRules;
    }

    @JsonProperty("join_rules")
    public void setJoinRules(String join_rules) {
        this.joinRules = join_rules;
    }

    @JsonProperty("guest_access")
    public String getGuestAccess() {
        return guestAccess;
    }

    @JsonProperty("guest_access")
    public void setGuestAccess(String guestAccess) {
        this.guestAccess = guestAccess;
    }

    @JsonProperty("history_visibility")
    public String getHistoryVisibility() {
        return historyVisibility;
    }

    @JsonProperty("history_visibility")
    public void setHistoryVisibility(String historyVisibility) {
        this.historyVisibility = historyVisibility;
    }

    @JsonProperty("state_events")
    public int getStateEventsCount() {
        return stateEventsCount;
    }

    @JsonProperty("state_events")
    public void setStateEventsCount(int stateEventsCount) {
        this.stateEventsCount = stateEventsCount;
    }
}
