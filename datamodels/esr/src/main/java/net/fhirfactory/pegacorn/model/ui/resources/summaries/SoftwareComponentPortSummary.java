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

public class SoftwareComponentPortSummary extends SoftwareComponentSummary {

    private String hostDNSName;
    private String hostPort;
    private String basePath;
    private String servicePort;
    private String serviceDNSName;
    private String portType;
    private boolean encrypted;

    //
    // Constructor(s)
    //

    public SoftwareComponentPortSummary(){
        this.hostPort = null;
        this.hostDNSName = null;
        this.basePath = null;
        this.servicePort = null;
        this.serviceDNSName = null;
        this.portType = null;
        this.encrypted = false;
    }

    //
    // Getters and Setters
    //


    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getHostDNSName() {
        return hostDNSName;
    }

    public void setHostDNSName(String hostDNSName) {
        this.hostDNSName = hostDNSName;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public String getServiceDNSName() {
        return serviceDNSName;
    }

    public void setServiceDNSName(String serviceDNSName) {
        this.serviceDNSName = serviceDNSName;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "SoftwareComponentPortSummary{" +
                "hostDNSName=" + hostDNSName +
                ", hostPort=" + hostPort +
                ", basePath=" + basePath +
                ", servicePort=" + servicePort +
                ", serviceDNSName=" + serviceDNSName +
                ", portType=" + portType +
                ", routing=" + getRouting() +
                ", topologyNodeFDN=" + getTopologyNodeFDN() +
                ", componentID=" + getComponentID() +
                ", nodeVersion=" + getNodeVersion() +
                ", nodeType=" + getNodeType() +
                ", concurrencyMode=" + getConcurrencyMode() +
                ", resilienceMode=" + getResilienceMode() +
                ", componentName=" + getComponentName() +
                ", encrypted=" + isEncrypted() +
                '}';
    }
}
