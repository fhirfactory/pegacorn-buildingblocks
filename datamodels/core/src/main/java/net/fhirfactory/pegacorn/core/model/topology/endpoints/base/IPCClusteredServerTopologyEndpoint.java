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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPCClusteredServerTopologyEndpoint extends IPCServerTopologyEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(IPCClusteredServerTopologyEndpoint.class);

    private Integer servicePortValue;
    private String servicePortName;
    private Integer servicePortOffset;
    private String serviceDNSName;

    @JsonIgnore
    private String toStringString;

    //
    // Getters and Setters
    //

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public IPCClusteredServerTopologyEndpoint(){
        super();
    }

    public Integer getServicePortValue() {
        return servicePortValue;
    }

    public void setServicePortValue(Integer servicePortValue) {
        this.servicePortValue = servicePortValue;
    }

    public String getServicePortName() {
        return servicePortName;
    }

    public void setServicePortName(String servicePortName) {
        this.servicePortName = servicePortName;
    }

    public Integer getServicePortOffset() {
        return servicePortOffset;
    }

    public void setServicePortOffset(Integer servicePortOffset) {
        this.servicePortOffset = servicePortOffset;
    }

    public String getServiceDNSName() {
        return serviceDNSName;
    }

    public void setServiceDNSName(String serviceDNSName) {
        this.serviceDNSName = serviceDNSName;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IPCClusteredServerTopologyEndpoint{");
        sb.append("servicePortValue=").append(servicePortValue);
        sb.append(", servicePortName='").append(servicePortName).append('\'');
        sb.append(", servicePortOffset=").append(servicePortOffset);
        sb.append(", serviceDNSName='").append(serviceDNSName).append('\'');
        sb.append(", assignedDNSName='").append(getAssignedDNSName()).append('\'');
        sb.append(", actualHostIP='").append(getActualHostIP()).append('\'');
        sb.append(", endpointConfigurationName='").append(getEndpointConfigurationName()).append('\'');
        sb.append(", server=").append(isServer());
        sb.append(", implementingWUP=").append(getImplementingWUP());
        sb.append(", connectedSystemName='").append(getConnectedSystemName()).append('\'');
        sb.append(", adapterList=").append(getAdapterList());
        sb.append(", endpointType=").append(getEndpointType());
        sb.append(", enablingProcessingPlantId=").append(getEnablingProcessingPlantId());
        sb.append(", interfaceFunction=").append(getInterfaceFunction());
        sb.append(", endpointStatus=").append(getEndpointStatus());
        sb.append(", endpointDescription='").append(getEndpointDescription()).append('\'');
        sb.append(", capabilities=").append(getCapabilities());
        sb.append(", participantId=").append(getParticipant());
        sb.append(", deploymentSite='").append(getDeploymentSite()).append('\'');
        sb.append(", lastActivityInstant=").append(getLastActivityInstant());
        sb.append(", lastReportingInstant=").append(getLastReportingInstant());
        sb.append(", kubernetesDeployed=").append(isKubernetesDeployed());
        sb.append(", otherConfigurationParameters=").append(getOtherConfigurationParameters());
        sb.append(", concurrencyMode=").append(getConcurrencyMode());
        sb.append(", resilienceMode=").append(getResilienceMode());
        sb.append(", securityZone=").append(getSecurityZone());
        sb.append(", componentID=").append(getComponentId());
        sb.append(", componentType=").append(getComponentType());
        sb.append(", metrics=").append(getMetrics());
        sb.append(", componentSystemRole=").append(getComponentSystemRole());
        sb.append(", componentStatus=").append(getComponentStatus());
        sb.append(", componentExecutionControl=").append(getComponentExecutionStatus());
        sb.append('}');
        return sb.toString();
    }
}
