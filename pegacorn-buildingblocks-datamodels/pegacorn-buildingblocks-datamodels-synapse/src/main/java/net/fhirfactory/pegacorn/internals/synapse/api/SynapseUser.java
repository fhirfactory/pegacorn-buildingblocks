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
package net.fhirfactory.pegacorn.internals.synapse.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SynapseUser {
    private String name;
    private boolean guest;
    private boolean admin;
    private String userType;
    private boolean deactivated;
    private boolean shadowBanned;
    private String displayName;
    private String avatarURL;

    public SynapseUser(){
        this.name = null;
        this.guest = false;
        this.admin = false;
        this.userType = null;
        this.deactivated = false;
        this.shadowBanned = false;
        this.displayName = null;
        this.avatarURL = null;
    }

    public SynapseUser(SynapseUser synapseUser){
        this.setName(synapseUser.getName());
        this.setGuest(synapseUser.isGuest());
        this.setAdmin(synapseUser.isAdmin());
        this.setUserType(synapseUser.getUserType());
        this.setDeactivated(synapseUser.isDeactivated());
        this.setShadowBanned(synapseUser.isShadowBanned());
        this.setDisplayName(synapseUser.getDisplayName());
        this.setAvatarURL(synapseUser.getAvatarURL());
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("is_guest")
    public boolean isGuest() {
        return guest;
    }

    @JsonProperty("is_guest")
    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    @JsonProperty("admin")
    public boolean isAdmin() {
        return admin;
    }

    @JsonProperty("admin")
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @JsonProperty("user_type")
    public String getUserType() {
        return userType;
    }

    @JsonProperty("user_type")
    public void setUserType(String userType) {
        this.userType = userType;
    }

    @JsonProperty("deactivated")
    public boolean isDeactivated() {
        return deactivated;
    }

    @JsonProperty("deactivated")
    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    @JsonProperty("shadow_banned")
    public boolean isShadowBanned() {
        return shadowBanned;
    }

    @JsonProperty("shadow_banned")
    public void setShadowBanned(boolean shadowBanned) {
        this.shadowBanned = shadowBanned;
    }

    @JsonProperty("displayname")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayname")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("avatar_url")
    public String getAvatarURL() {
        return avatarURL;
    }

    @JsonProperty("avatar_url")
    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynapseUser that = (SynapseUser) o;
        return isAdmin() == that.isAdmin() && isDeactivated() == that.isDeactivated() && getName().equals(that.getName()) && Objects.equals(getUserType(), that.getUserType()) && getDisplayName().equals(that.getDisplayName()) && Objects.equals(getAvatarURL(), that.getAvatarURL());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), isAdmin(), getUserType(), isDeactivated(), getDisplayName(), getAvatarURL());
    }

    @Override
    public String toString() {
        return "SynapseUser{" +
                "name='" + name + '\'' +
                ", guest=" + guest +
                ", admin=" + admin +
                ", userType='" + userType + '\'' +
                ", deactivated=" + deactivated +
                ", shadowBanned=" + shadowBanned +
                ", displayName='" + displayName + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                '}';
    }
}
