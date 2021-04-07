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
package net.fhirfactory.pegacorn.internals.matrix.r061.events.presence.contenttypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MPresenceContentType {
    private String avatarURL;
    private String displayName;
    private Long timeSinceLastActive;
    private MPresenceStatusEnum presenceStatus;
    private Boolean currentlyActive;
    private String statusMessage;

    @JsonProperty("avatar_url")
    public String getAvatarURL() {
        return avatarURL;
    }

    @JsonProperty("avatar_url")
    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    @JsonProperty("displayname")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayname")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("last_active_ago")
    public Long getTimeSinceLastActive() {
        return timeSinceLastActive;
    }

    @JsonProperty("last_active_ago")
    public void setTimeSinceLastActive(Long timeSinceLastActive) {
        this.timeSinceLastActive = timeSinceLastActive;
    }

    @JsonProperty("presence")
    public MPresenceStatusEnum getPresenceStatus() {
        return presenceStatus;
    }

    @JsonProperty("presence")
    public void setPresenceStatus(MPresenceStatusEnum presenceStatus) {
        this.presenceStatus = presenceStatus;
    }

    @JsonProperty("currently_active")
    public Boolean getCurrentlyActive() {
        return currentlyActive;
    }

    @JsonProperty("currently_active")
    public void setCurrentlyActive(Boolean currentlyActive) {
        this.currentlyActive = currentlyActive;
    }

    @JsonProperty("status_msg")
    public String getStatusMessage() {
        return statusMessage;
    }

    @JsonProperty("status_msg")
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
