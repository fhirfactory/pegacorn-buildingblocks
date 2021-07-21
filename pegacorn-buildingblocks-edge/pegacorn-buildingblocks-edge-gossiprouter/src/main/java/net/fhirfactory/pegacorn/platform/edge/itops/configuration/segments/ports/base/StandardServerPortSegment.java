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

import java.util.ArrayList;

public abstract class StandardServerPortSegment {

    protected abstract Logger specifyLogger();

    protected Logger getLogger(){
        return(specifyLogger());
    }

    private int portValue;
    private String portType;
    private Boolean isServer;
    private Boolean isEncrypted;
    private String hostDNSEntry;
    private ArrayList<InterfaceDefinitionSegment> supportedInterfaceProfiles;
    private int startupDelay;
    private String name;

    public StandardServerPortSegment(){
        super();
        this.supportedInterfaceProfiles = new ArrayList<>();
        this.isEncrypted = false;
        this.isServer = false;
        this.startupDelay = 0;
        this.name = null;
    }
    public int getPortValue() {
        return portValue;
    }

    public void setPortValue(int portValue) {
        this.portValue = portValue;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public Boolean getServer() {
        return isServer;
    }

    public Boolean isServer(){
        return(isServer);
    }

    public void setServer(Boolean server) {
        isServer = server;
    }

    public Boolean getEncrypted() {
        return isEncrypted;
    }

    public Boolean isEncrypted(){
        return(isEncrypted);
    }

    public void setEncrypted(Boolean encrypted) {
        isEncrypted = encrypted;
    }

    public ArrayList<InterfaceDefinitionSegment> getSupportedInterfaceProfiles() {
        return supportedInterfaceProfiles;
    }

    public void setSupportedInterfaceProfiles(ArrayList<InterfaceDefinitionSegment> supportedInterfaceProfiles) {
        this.supportedInterfaceProfiles = supportedInterfaceProfiles;
    }

    public String getHostDNSEntry() {
        return hostDNSEntry;
    }

    public void setHostDNSEntry(String hostDNSEntry) {
        this.hostDNSEntry = hostDNSEntry;
    }

    public int getStartupDelay() {
        return startupDelay;
    }

    public void setStartupDelay(int startupDelay) {
        this.startupDelay = startupDelay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StandardServerPortSegment{" +
                "portValue=" + portValue +
                ", portType='" + portType + '\'' +
                ", isServer=" + isServer +
                ", isEncrypted=" + isEncrypted +
                ", hostDNSEntry='" + hostDNSEntry + '\'' +
                ", supportedInterfaceProfiles=" + supportedInterfaceProfiles +
                ", startupDelay=" + startupDelay +
                ", name='" + name + '\'' +
                ", server=" + getServer() +
                ", server=" + isServer() +
                ", encrypted=" + getEncrypted() +
                ", encrypted=" + isEncrypted() +
                '}';
    }
}
