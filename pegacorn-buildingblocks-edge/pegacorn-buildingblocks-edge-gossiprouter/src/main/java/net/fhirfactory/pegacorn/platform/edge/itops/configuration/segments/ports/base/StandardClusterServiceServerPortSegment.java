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
package net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments.ports.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardClusterServiceServerPortSegment extends StandardServerPortSegment {
    private static Logger LOG = LoggerFactory.getLogger(StandardClusterServiceServerPortSegment.class);

    private Integer servicePortValue;
    private String servicePortName;
    private String serviceDNSEntry;
    private Integer clusterServicePortOffsetValue;

    public StandardClusterServiceServerPortSegment(){
        super();
        clusterServicePortOffsetValue = null;
    }

    @Override
    protected Logger specifyLogger() {
        return null;
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

    public Integer getClusterServicePortIncludingOffset(Integer offset){
        if(clusterServicePortOffsetValue == null){
            getLogger().warn(".getEdgeReceiveCommunicationClusterServicePort(): Not such port type defined");
            return(0);
        }
        Integer servicePortNumber = offset + getClusterServicePortOffsetValue();
        return(servicePortNumber);
    }

    public Integer getClusterServicePortOffsetValue() {
        return clusterServicePortOffsetValue;
    }

    public void setClusterServicePortOffsetValue(Integer clusterServicePortOffsetValue) {
        this.clusterServicePortOffsetValue = clusterServicePortOffsetValue;
    }

    public String getServiceDNSEntry() {
        return serviceDNSEntry;
    }

    public void setServiceDNSEntry(String serviceDNSEntry) {
        this.serviceDNSEntry = serviceDNSEntry;
    }

    @Override
    public String toString() {
        return "StandardClusterServicePortSegment{" +
                "portValue=" + getPortValue() +
                ", portType='" + getPortType() + '\'' +
                ", isServer=" + isServer() +
                ", isEncrypted=" + isEncrypted() +
                ", hostDNSEntry='" + getHostDNSEntry() + '\'' +
                ", supportedInterfaceProfiles=" + getSupportedInterfaceProfiles() +
                ", servicePortValue=" + servicePortValue +
                ", servicePortName='" + servicePortName + '\'' +
                ", serviceDNSEntry='" + serviceDNSEntry + '\'' +
                ", clusterServicePortOffsetValue=" + clusterServicePortOffsetValue +
                '}';
    }
}
