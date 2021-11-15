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
package net.fhirfactory.pegacorn.model.ui.resources.summaries;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SoftwareComponentEndpointSummary extends SoftwareComponentSummary {

    private String connectedSystemName;
    private PetasosEndpointTopologyTypeEnum endpointType;
    private boolean server;
    private Set<SoftwareComponentPortSummary> serverPorts;
    private Set<SoftwareComponentPortSummary> clientPorts;

    //
    // Constructor(s)
    //

    public SoftwareComponentEndpointSummary(){
        this.connectedSystemName = null;
        this.endpointType = null;
        this.server = false;
        this.serverPorts = new HashSet<>();
        this.clientPorts = new HashSet<>();
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

    public Set<SoftwareComponentPortSummary> getServerPorts() {
        return serverPorts;
    }

    public void setServerPorts(Set<SoftwareComponentPortSummary> serverPorts) {
        this.serverPorts = serverPorts;
    }

    public Set<SoftwareComponentPortSummary> getClientPorts() {
        return clientPorts;
    }

    public void setClientPorts(Set<SoftwareComponentPortSummary> clientPorts) {
        this.clientPorts = clientPorts;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "SoftwareComponentEndpointSummary{" +
                "connectedSystemName='" + connectedSystemName + '\'' +
                ", endpointType=" + endpointType +
                ", server=" + server +
                ", serverPorts=" + serverPorts +
                ", clientPorts=" + clientPorts +
                ", routing=" + getRouting() +
                ", topologyNodeFDN=" + getTopologyNodeFDN() +
                ", componentID=" + getComponentID() +
                ", nodeVersion='" + getNodeVersion() + '\'' +
                ", nodeType=" + getNodeType() +
                ", concurrencyMode='" + getConcurrencyMode() + '\'' +
                ", resilienceMode='" + getResilienceMode() + '\'' +
                ", componentName='" + getComponentName() + '\'' +
                '}';
    }
}