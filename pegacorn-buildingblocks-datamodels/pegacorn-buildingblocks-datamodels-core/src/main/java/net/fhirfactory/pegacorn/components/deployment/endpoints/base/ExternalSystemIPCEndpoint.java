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
package net.fhirfactory.pegacorn.components.deployment.endpoints.base;

import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalSystemIPCEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalSystemIPCEndpoint.class);

    private IPCInterfaceDefinition supportedInterfaceDefinition;
    private Boolean encryptionRequired;
    private Integer targetPortValue;
    private String targetPortDNSName;

    public IPCInterfaceDefinition getSupportedInterfaceDefinition() {
        return supportedInterfaceDefinition;
    }

    public void setSupportedInterfaceDefinition(IPCInterfaceDefinition supportedInterfaceDefinition) {
        this.supportedInterfaceDefinition = supportedInterfaceDefinition;
    }

    public Boolean getEncryptionRequired() {
        return encryptionRequired;
    }

    public void setEncryptionRequired(Boolean encryptionRequired) {
        this.encryptionRequired = encryptionRequired;
    }

    public Integer getTargetPortValue() {
        return targetPortValue;
    }

    public void setTargetPortValue(Integer targetPortValue) {
        this.targetPortValue = targetPortValue;
    }

    public String getTargetPortDNSName() {
        return targetPortDNSName;
    }

    public void setTargetPortDNSName(String targetPortDNSName) {
        this.targetPortDNSName = targetPortDNSName;
    }

    @Override
    public String toString() {
        return "ConnectedExternalSystemTopologyEndpoint{" +
                "supportedInterfaceDefinition=" + supportedInterfaceDefinition +
                ", encryptionRequired=" + encryptionRequired +
                ", targetPortValue=" + targetPortValue +
                ", targetPortDNSName='" + targetPortDNSName + '\'' +
                '}';
    }
}
