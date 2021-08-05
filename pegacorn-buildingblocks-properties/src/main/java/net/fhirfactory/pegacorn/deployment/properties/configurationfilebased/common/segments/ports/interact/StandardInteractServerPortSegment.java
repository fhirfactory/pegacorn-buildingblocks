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

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.InterfaceDefinitionSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class StandardInteractServerPortSegment extends StandardExternalFacingPort {
    private static Logger LOG = LoggerFactory.getLogger(StandardInteractServerPortSegment.class);

    private int portValue;
    private String hostDNSEntry;
    private ArrayList<InterfaceDefinitionSegment> supportedInterfaceProfiles;
    private int startupDelay;
    private boolean encrypted;

    public String getHostDNSEntry() {
        return hostDNSEntry;
    }

    public void setHostDNSEntry(String hostDNSEntry) {
        this.hostDNSEntry = hostDNSEntry;
    }

    public ArrayList<InterfaceDefinitionSegment> getSupportedInterfaceProfiles() {
        return supportedInterfaceProfiles;
    }

    public void setSupportedInterfaceProfiles(ArrayList<InterfaceDefinitionSegment> supportedInterfaceProfiles) {
        this.supportedInterfaceProfiles = supportedInterfaceProfiles;
    }

    public int getStartupDelay() {
        return startupDelay;
    }

    public void setStartupDelay(int startupDelay) {
        this.startupDelay = startupDelay;
    }

    public int getPortValue() {
        return portValue;
    }

    public void setPortValue(int portValue) {
        this.portValue = portValue;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    public String toString() {
        return "StandardInteractServerPortSegment{" +
                "portType='" + getPortType() + '\'' +
                ", connectedSystem=" + getConnectedSystem() +
                ", name='" + getName() + '\'' +
                ", server=" + isServer() +
                ", portValue=" + portValue +
                ", hostDNSEntry='" + hostDNSEntry + '\'' +
                ", supportedInterfaceProfiles=" + supportedInterfaceProfiles +
                ", startupDelay=" + startupDelay +
                ", encrypted=" + encrypted +
                '}';
    }
}
