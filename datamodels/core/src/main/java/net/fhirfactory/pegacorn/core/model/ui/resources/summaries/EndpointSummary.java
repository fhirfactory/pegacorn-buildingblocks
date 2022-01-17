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

import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;

import java.util.HashSet;
import java.util.Set;

public class EndpointSummary extends SoftwareComponentSummary {

    private String connectedSystemName;
    private PetasosEndpointTopologyTypeEnum endpointType;
    private boolean server;
    private Set<PortSoftwareComponentSummary> serverPorts;
    private Set<PortSoftwareComponentSummary> clientPorts;
    private String wupParticipantName;

    //
    // Constructor(s)
    //

    public EndpointSummary(){
        this.connectedSystemName = null;
        this.endpointType = null;
        this.server = false;
        this.serverPorts = new HashSet<>();
        this.clientPorts = new HashSet<>();
        this.wupParticipantName = null;
    }

    //
    // Getters and Setters
    //

    public String getWupParticipantName() {
        return wupParticipantName;
    }

    public void setWupParticipantName(String wupParticipantName) {
        this.wupParticipantName = wupParticipantName;
    }

    public String getConnectedSystemName() {
        return connectedSystemName;
    }

    public void setConnectedSystemName(String connectedSystemName) {
        this.connectedSystemName = connectedSystemName;
    }

    public PetasosEndpointTopologyTypeEnum getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(PetasosEndpointTopologyTypeEnum endpointType) {
        this.endpointType = endpointType;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public Set<PortSoftwareComponentSummary> getServerPorts() {
        return serverPorts;
    }

    public void setServerPorts(Set<PortSoftwareComponentSummary> serverPorts) {
        this.serverPorts = serverPorts;
    }

    public Set<PortSoftwareComponentSummary> getClientPorts() {
        return clientPorts;
    }

    public void setClientPorts(Set<PortSoftwareComponentSummary> clientPorts) {
        this.clientPorts = clientPorts;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "EndpointSummary{" +
                "connectedSystemName='" + connectedSystemName + '\'' +
                ", endpointType=" + endpointType +
                ", server=" + server +
                ", serverPorts=" + serverPorts +
                ", clientPorts=" + clientPorts +
                ", participantName='" + getParticipantName() + '\'' +
                ", subsystemParticipantName='" + getSubsystemParticipantName() + '\'' +
                ", wupParticipantName=" + getWupParticipantName() +
                ", topologyNodeFDN=" + getTopologyNodeFDN() +
                ", componentID=" + getComponentID() +
                ", nodeVersion='" + getNodeVersion() + '\'' +
                ", nodeType=" + getNodeType() +
                ", concurrencyMode='" + getConcurrencyMode() + '\'' +
                ", resilienceMode='" + getResilienceMode() + '\'' +
                ", lastSynchronisationInstant=" + getLastSynchronisationInstant() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", resourceId='" + getResourceId() + '\'' +
                '}';
    }
}
