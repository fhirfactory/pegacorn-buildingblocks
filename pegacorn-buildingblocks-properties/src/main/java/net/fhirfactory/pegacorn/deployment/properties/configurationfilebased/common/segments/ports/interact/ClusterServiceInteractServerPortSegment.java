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
package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterServiceInteractServerPortSegment extends StandardInteractServerPortSegment {
    private static Logger LOG = LoggerFactory.getLogger(ClusterServiceInteractServerPortSegment.class);

    private Integer servicePortValue;
    private String servicePortName;
    private String serviceDNSEntry;
    private Integer clusterServicePortOffsetValue;

    @Override
    protected Logger specifyLogger(){
        return(LOG);
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

    public String getServiceDNSEntry() {
        return serviceDNSEntry;
    }

    public void setServiceDNSEntry(String serviceDNSEntry) {
        this.serviceDNSEntry = serviceDNSEntry;
    }

    public Integer getClusterServicePortOffsetValue() {
        return clusterServicePortOffsetValue;
    }

    public void setClusterServicePortOffsetValue(Integer clusterServicePortOffsetValue) {
        this.clusterServicePortOffsetValue = clusterServicePortOffsetValue;
    }

    @Override
    public String toString() {
        return "ClusterServiceInteractServerPortSegment{" +
                "servicePortValue=" + servicePortValue +
                ", servicePortName='" + servicePortName + '\'' +
                ", serviceDNSEntry='" + serviceDNSEntry + '\'' +
                ", clusterServicePortOffsetValue=" + clusterServicePortOffsetValue +
                ", portType='" + getPortType() + '\'' +
                ", connectedSystem=" + getConnectedSystem() +
                ", startupDelay=" + getStartupDelay() +
                ", name='" + getName() + '\'' +
                ", server=" + isServer() +
                ", hostDNSEntry='" + getHostDNSEntry() + '\'' +
                ", supportedInterfaceProfiles=" + getSupportedInterfaceProfiles() +
                ", startupDelay=" + getStartupDelay() +
                ", portValue=" + getPortValue() +
                ", encrypted=" + isEncrypted() +
                '}';
    }
}
