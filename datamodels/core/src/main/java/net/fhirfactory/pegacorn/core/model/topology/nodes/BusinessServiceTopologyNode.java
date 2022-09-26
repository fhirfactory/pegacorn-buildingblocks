/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.topology.nodes;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class BusinessServiceTopologyNode extends SoftwareComponent {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessServiceTopologyNode.class);

    private ArrayList<ComponentIdType> deploymentSites;
    private ArrayList<ComponentIdType> externalisedServices;
    private boolean preferringEncryption;
    private String defaultDNSName;

    public BusinessServiceTopologyNode(){
        super();
        this.deploymentSites = new ArrayList<>();
        this.externalisedServices = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<ComponentIdType> getDeploymentSites() {
        return deploymentSites;
    }

    public void setDeploymentSites(ArrayList<ComponentIdType> deploymentSites) {
        this.deploymentSites = deploymentSites;
    }

    public ArrayList<ComponentIdType> getExternalisedServices() {
        return externalisedServices;
    }

    public void setExternalisedServices(ArrayList<ComponentIdType> externalisedServices) {
        this.externalisedServices = externalisedServices;
    }

    public boolean isPreferringEncryption() {
        return preferringEncryption;
    }

    public void setPreferringEncryption(boolean preferringEncryption) {
        this.preferringEncryption = preferringEncryption;
    }

    public String getDefaultDNSName() {
        return defaultDNSName;
    }

    public void setDefaultDNSName(String defaultDNSName) {
        this.defaultDNSName = defaultDNSName;
    }
}
