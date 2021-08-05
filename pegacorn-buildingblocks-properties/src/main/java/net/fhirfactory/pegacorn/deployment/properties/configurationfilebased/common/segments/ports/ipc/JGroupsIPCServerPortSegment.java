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
package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.StandardServerPortSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JGroupsIPCServerPortSegment extends StandardServerPortSegment {
    private static Logger LOG = LoggerFactory.getLogger(JGroupsIPCServerPortSegment.class);

    public JGroupsIPCServerPortSegment(){
        super();
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    private List<JGroupsInitialHostSegment> initialHosts;

    public List<JGroupsInitialHostSegment> getInitialHosts() {
        return initialHosts;
    }

    public void setInitialHosts(List<JGroupsInitialHostSegment> initialHosts) {
        this.initialHosts = initialHosts;
    }

    @Override
    public String toString() {
        return "JGroupsIPCPortSegment{" +
                "portValue=" + getPortValue() +
                ", portType='" + getPortType() + '\'' +
                ", server=" + isServer() +
                ", encrypted=" + isEncrypted() +
                ", supportedInterfaceProfiles=" + getSupportedInterfaceProfiles() +
                ", hostDNSEntry='" + getHostDNSEntry() + '\'' +
                ", initialHosts='" + initialHosts + '\'' +
                '}';
    }
}
