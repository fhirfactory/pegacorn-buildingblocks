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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base;

import java.util.Objects;

public class IPCClusteredSocketBasedAdapter extends IPCSocketBasedAdapter {
    private Integer servicePortValue;
    private String serviceDNSName;

    //
    // Constructor(s)
    //

    public IPCClusteredSocketBasedAdapter(){
        super();
        this.servicePortValue = null;
        this.serviceDNSName = null;
    }

    //
    // Getters and Setters
    //

    public Integer getServicePortValue() {
        return servicePortValue;
    }

    public void setServicePortValue(Integer servicePortValue) {
        this.servicePortValue = servicePortValue;
    }

    public String getServiceDNSName() {
        return serviceDNSName;
    }

    public void setServiceDNSName(String serviceDNSName) {
        this.serviceDNSName = serviceDNSName;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "IPCClusteredSocketBasedInterface{" +
                "servicePortValue=" + servicePortValue +
                ", serviceDNSName=" + serviceDNSName +
                ", supportedDeploymentModes=" + getSupportedDeploymentModes() +
                ", targetNameInstant=" + getTargetSystemName() +
                ", enablingTopologyEndpoint=" + getEnablingTopologyEndpoint() +
                ", supportedInterfaceDefinitions=" + getSupportedInterfaceDefinitions() +
                ", supportInterfaceTags=" + getSupportInterfaceTags() +
                ", encrypted=" + isEncrypted() +
                ", groupName=" + getGroupName() +
                ", portNumber=" + getPortNumber() +
                ", hostName=" + getHostName() +
                ", active=" + isActive() +
                ", lastActivity=" + getLastActivity() +
                '}';
    }

    //
    // Hashcode and equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IPCClusteredSocketBasedAdapter)) return false;
        if (!super.equals(o)) return false;
        IPCClusteredSocketBasedAdapter that = (IPCClusteredSocketBasedAdapter) o;
        return Objects.equals(getServicePortValue(), that.getServicePortValue()) && Objects.equals(getServiceDNSName(), that.getServiceDNSName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getServicePortValue(), getServiceDNSName());
    }
}
