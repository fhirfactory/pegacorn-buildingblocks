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

public class IPCSocketBasedAdapter extends IPCAdapter {
    private Integer portNumber;
    private String hostName;

    //
    // Constructor(s)
    //

    public IPCSocketBasedAdapter(){
        this.portNumber = null;
        this.hostName = null;
    }

    //
    // Getters and Setters
    //

    public Integer getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "IPCSocketBasedInterface{" +
                "supportedDeploymentModes=" + getSupportedDeploymentModes() +
                ", targetNameInstant=" + getTargetSystemName() +
                ", enablingTopologyEndpoint=" + getEnablingTopologyEndpoint() +
                ", supportedInterfaceDefinitions=" + getSupportedInterfaceDefinitions() +
                ", supportInterfaceTags=" + getSupportInterfaceTags() +
                ", encrypted=" + isEncrypted() +
                ", groupName=" + getGroupName() +
                ", active=" + isActive() +
                ", lastActivity=" + getLastActivity() +
                ", portNumber=" + portNumber +
                ", hostName=" + hostName +
                '}';
    }

    //
    // hashcode and equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IPCSocketBasedAdapter)) return false;
        if (!super.equals(o)) return false;
        IPCSocketBasedAdapter that = (IPCSocketBasedAdapter) o;
        return Objects.equals(getPortNumber(), that.getPortNumber()) && Objects.equals(getHostName(), that.getHostName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPortNumber(), getHostName());
    }
}
