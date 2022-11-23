/*
 * Copyright (c) 2022 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.petasos.participant.id;

import java.io.Serializable;
import java.util.Objects;

public class PetasosParticipantId implements Serializable {
    private String fullName;
    private String name;
    private String displayName;
    private String subsystemName;
    private String version;

    private static final String DEFAULT_VERSION = "1.0.0";

    //
    // Constructor(s)
    //

    public PetasosParticipantId(){
        fullName = null;
        name = null;
        displayName = null;
        subsystemName = null;
        version = DEFAULT_VERSION;
    }

    public PetasosParticipantId(String subsystemName, String name, String version){
        this.name = subsystemName + "." + name;
        this.version = version;
        this.displayName = name;
        this.subsystemName = subsystemName;
        this.fullName = subsystemName + "." + name;
    }

    public PetasosParticipantId(String subsystemName, String workshop, String name, String version){
        this.name = subsystemName + "." + workshop + "." + name;
        this.version = version;
        this.displayName = name;
        this.subsystemName = subsystemName;
        this.fullName = subsystemName + "." + workshop + "." + name;
    }

    //
    // Getters and Setters
    //

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosParticipantId{");
        sb.append("name=").append(name);
        sb.append(", displayName=").append(displayName);
        sb.append(", subsystemName=").append(subsystemName);
        sb.append(", version=").append(version);
        sb.append(", fullName=").append(fullName);
        sb.append('}');
        return sb.toString();
    }

    //
    // equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosParticipantId)) return false;
        PetasosParticipantId that = (PetasosParticipantId) o;
        return Objects.equals(getName(), that.getName()) && getVersion().equals(that.getVersion()) && Objects.equals(getSubsystemName(), that.getSubsystemName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getVersion(), getSubsystemName());
    }
}
