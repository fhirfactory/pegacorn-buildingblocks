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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class IPCServerTopologyEndpoint extends IPCTopologyEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(IPCServerTopologyEndpoint.class);

    private String assignedDNSName;
    private String actualHostIP;

    //
    // Constructor(s)
    //

    public IPCServerTopologyEndpoint(){
        super();
        this.actualHostIP = null;
        this.assignedDNSName = null;
    }

    public IPCServerTopologyEndpoint(IPCServerTopologyEndpoint ori){
        super(ori);
        if(ori.hasActualHostIP()){
            setActualHostIP(ori.getActualHostIP());
        }
        if(ori.hasAssignedDNSName()){
            setAssignedDNSName(ori.getAssignedDNSName());
        }
    }

    //
    // Getters and Setters

    @JsonIgnore
    public boolean hasAssignedDNSName(){
        boolean hasValue = this.assignedDNSName != null;
        return(hasValue);
    }

    public String getAssignedDNSName() {
        return assignedDNSName;
    }

    public void setAssignedDNSName(String assignedDNSName) {
        this.assignedDNSName = assignedDNSName;
    }

    @JsonIgnore
    public boolean hasActualHostIP(){
        boolean hasValue = this.actualHostIP != null;
        return(hasValue);
    }

    public String getActualHostIP() {
        return actualHostIP;
    }

    public void setActualHostIP(String actualHostIP) {
        this.actualHostIP = actualHostIP;
    }

    //

    @JsonIgnore
    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "IPCServerTopologyEndpoint{" +
                "componentFDN=" + getComponentFDN() +
                ", kubernetesDeployed=" + isKubernetesDeployed() +
                ", otherConfigurationParameters=" + getOtherConfigurationParameters() +
                ", concurrencyMode=" + getConcurrencyMode() +
                ", resilienceMode=" + getResilienceMode() +
                ", securityZone=" + getSecurityZone() +
                ", componentID=" + getComponentID() +
                ", nodeFunctionFDN=" + getNodeFunctionFDN() +
                ", componentType=" + getComponentType() +
                ", containingNodeFDN=" + getContainingNodeFDN() +
                ", actualHostIP='" + getActualHostIP() + '\'' +
                ", assignedDNSName='" + getAssignedDNSName() + '\'' +
                ", componentRDN=" + getComponentRDN() +
                ", metrics=" + getMetrics() +
                ", componentSystemRole=" + getComponentSystemRole() +
                ", server=" + isServer() +
                ", implementingWUP=" + getImplementingWUP() +
                ", connectedSystemName='" + getConnectedSystemName() + '\'' +
                ", endpointType=" + getEndpointType() +
                '}';
    }
}
