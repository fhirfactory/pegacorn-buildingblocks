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

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.common.EndpointProviderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ClusterServiceTopologyNode extends SoftwareComponent implements EndpointProviderInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterServiceTopologyNode.class);

    private ArrayList<TopologyNodeFDN> platformNodes;
    private Integer platformNodeCount;
    private ArrayList<TopologyNodeFDN> serviceEndpoints;
    private String defaultDNSName;
    private boolean internalTrafficEncrypted;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ClusterServiceTopologyNode(){
        super();
        this.platformNodes = new ArrayList<>();
        this.serviceEndpoints = new ArrayList<>();
    }

    public ArrayList<TopologyNodeFDN> getPlatformNodes() {
        return platformNodes;
    }

    public void setPlatformNodes(ArrayList<TopologyNodeFDN> platformNodes) {
        this.platformNodes = platformNodes;
    }

    public ArrayList<TopologyNodeFDN> getServiceEndpoints() {
        return serviceEndpoints;
    }

    public void setServiceEndpoints(ArrayList<TopologyNodeFDN> serviceEndpoints) {
        this.serviceEndpoints = serviceEndpoints;
    }

    public String getDefaultDNSName() {
        return defaultDNSName;
    }

    public void setDefaultDNSName(String defaultDNSName) {
        this.defaultDNSName = defaultDNSName;
    }

    public boolean isInternalTrafficEncrypted() {
        return internalTrafficEncrypted;
    }

    public void setInternalTrafficEncrypted(boolean internalTrafficEncrypted) {
        this.internalTrafficEncrypted = internalTrafficEncrypted;
    }

    public Integer getPlatformNodeCount() {
        return platformNodeCount;
    }

    public void setPlatformNodeCount(Integer platformNodeCount) {
        this.platformNodeCount = platformNodeCount;
    }

    @Override
    public void addEndpoint(TopologyNodeFDN endpointFDN) {
        getLogger().debug(".addEndpoint(): Entry, endpointFDN->{}", endpointFDN);
        serviceEndpoints.add(endpointFDN);
    }
}
