/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.ui.resources.summaries;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.common.ResourceSummaryBase;

public class SoftwareComponentSummary extends ResourceSummaryBase {
    private ComponentIdType componentID;
    private PetasosParticipantId participantId;
    private String nodeVersion;
    private String concurrencyMode;
    private String resilienceMode;
    private PetasosMonitoredComponentTypeEnum nodeType;

    //
    // Constructor(s)
    //

    public SoftwareComponentSummary(){
        this.componentID = null;
        this.participantId = null;
        this.nodeVersion = null;
        this.concurrencyMode = null;
        this.resilienceMode = null;
        this.nodeType = null;
    }

    //
    // Getters and Setters
    //

    public PetasosParticipantId getParticipantId() {
        return participantId;
    }

    public void setParticipantId(PetasosParticipantId participantId) {
        this.participantId = participantId;
    }

    public ComponentIdType getComponentID() {
        return componentID;
    }

    public void setComponentID(ComponentIdType componentID) {
        this.componentID = componentID;
    }

    public String getNodeVersion() {
        return nodeVersion;
    }

    public void setNodeVersion(String nodeVersion) {
        this.nodeVersion = nodeVersion;
    }

    public PetasosMonitoredComponentTypeEnum getNodeType() {
        return nodeType;
    }

    public void setNodeType(PetasosMonitoredComponentTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    public String getConcurrencyMode() {
        return concurrencyMode;
    }

    public void setConcurrencyMode(String concurrencyMode) {
        this.concurrencyMode = concurrencyMode;
    }

    public String getResilienceMode() {
        return resilienceMode;
    }

    public void setResilienceMode(String resilienceMode) {
        this.resilienceMode = resilienceMode;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "SoftwareComponentSummary{" +
                "componentID=" + componentID +
                ", participantId=" + participantId +
                ", nodeVersion='" + nodeVersion + '\'' +
                ", concurrencyMode='" + concurrencyMode + '\'' +
                ", resilienceMode='" + resilienceMode + '\'' +
                ", nodeType=" + nodeType +
                ", lastSynchronisationInstant=" + getLastSynchronisationInstant() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", resourceId='" + getResourceId() + '\'' +
                '}';
    }
}
