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
package net.fhirfactory.pegacorn.core.model.internal.resources.summaries;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.internal.resources.summaries.common.ResourceSummaryBase;

public class SoftwareComponentSummary extends ResourceSummaryBase {
    private ComponentIdType componentID;
    private String participantName;
    private String subsystemParticipantName;
    private String participantDisplayName;
    private String nodeVersion;
    private String concurrencyMode;
    private String resilienceMode;
    private TopologyNodeFDN topologyNodeFDN;
    private TopologyNodeFunctionFDN topologyNodeFunctionFDN;
    private PetasosMonitoredComponentTypeEnum nodeType;

    //
    // Constructor(s)
    //

    public SoftwareComponentSummary(){
        this.componentID = null;
        this.participantName = null;
        this.nodeVersion = null;
        this.subsystemParticipantName = null;
        this.concurrencyMode = null;
        this.resilienceMode = null;
        this.topologyNodeFDN = null;
        this.nodeType = null;
        this.topologyNodeFunctionFDN = null;
        this.participantDisplayName = null;
    }

    //
    // Getters and Setters
    //


    public String getParticipantDisplayName() {
        return participantDisplayName;
    }

    public void setParticipantDisplayName(String participantDisplayName) {
        this.participantDisplayName = participantDisplayName;
    }

    public TopologyNodeFunctionFDN getTopologyNodeFunctionFDN() {
        return topologyNodeFunctionFDN;
    }

    public void setTopologyNodeFunctionFDN(TopologyNodeFunctionFDN topologyNodeFunctionFDN) {
        this.topologyNodeFunctionFDN = topologyNodeFunctionFDN;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getSubsystemParticipantName() {
        return subsystemParticipantName;
    }

    public void setSubsystemParticipantName(String subsystemParticipantName) {
        this.subsystemParticipantName = subsystemParticipantName;
    }

    public TopologyNodeFDN getTopologyNodeFDN() {
        return topologyNodeFDN;
    }

    public void setTopologyNodeFDN(TopologyNodeFDN topologyNodeFDN) {
        this.topologyNodeFDN = topologyNodeFDN;
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
                ", participantName='" + participantName + '\'' +
                ", subsystemParticipantName='" + subsystemParticipantName + '\'' +
                ", participantDisplayName='" + participantDisplayName + '\'' +
                ", nodeVersion='" + nodeVersion + '\'' +
                ", concurrencyMode='" + concurrencyMode + '\'' +
                ", resilienceMode='" + resilienceMode + '\'' +
                ", topologyNodeFDN=" + topologyNodeFDN +
                ", topologyNodeFunctionFDN=" + topologyNodeFunctionFDN +
                ", nodeType=" + nodeType +
                ", lastSynchronisationInstant=" + getLastSynchronisationInstant() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", resourceId='" + getResourceId() + '\'' +
                '}';
    }
}
