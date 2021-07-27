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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroupsOLD.base;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.DefaultWorkshopSetEnum;
import org.apache.commons.lang3.SerializationUtils;

import javax.inject.Inject;

public abstract class JGroupsPetasosInterfaceBase extends JGroupsInterfaceBase {
    private StandardEdgeIPCEndpoint topologyNode;

    @Inject
    private ProcessingPlantInterface processingPlantInterface;

    @Inject
    private TopologyIM topologyIM;

    //
    // Constructor
    //

    public JGroupsPetasosInterfaceBase(){
        super();
    }

    //
    // Abstract Methods
    //

    protected abstract String specifyGroupName();
    protected abstract String specifyFileName();
    protected abstract String specifyIPCInterfaceName();
    protected abstract PetasosTopologyEndpointTypeEnum specifyIPCType();
    protected abstract String specifyForwarderWUPName();
    protected abstract String specifyForwarderWUPVersion();

    //
    // Getters and Setters
    //

    public StandardEdgeIPCEndpoint getTopologyNode() {
        return topologyNode;
    }

    public void setTopologyNode(StandardEdgeIPCEndpoint topologyNode) {
        this.topologyNode = topologyNode;
    }

    protected TopologyIM getTopologyIM(){
        return(this.topologyIM);
    }

    public ProcessingPlantInterface getProcessingPlantInterface() {
        return processingPlantInterface;
    }

    //
    // Resolve my Endpoint Details
    //

    protected void deriveTopologyEndpoint(){
        getLogger().debug(".deriveIPCTopologyEndpoint(): Entry");
        for(TopologyNodeFDN currentEndpointFDN: getProcessingPlantInterface().getProcessingPlantNode().getEndpoints()){
            IPCTopologyEndpoint currentEndpoint = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
            PetasosTopologyEndpointTypeEnum endpointType = currentEndpoint.getEndpointType();
            boolean endpointTypeMatches = endpointType.equals(specifyIPCType());
            if(endpointTypeMatches){
                if(currentEndpoint.getName().contentEquals(specifyIPCInterfaceName())) {
                    setTopologyNode((StandardEdgeIPCEndpoint)currentEndpoint);
                    getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, found IPCTopologyEndpoint and assigned it");
                    break;
                }
            }
        }
        getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, Could not find appropriate Endpoint");
    }

    protected TopologyNodeFDNToken deriveAssociatedForwarderFDNToken(){
        if(this.getTopologyNode() == null){
            getLogger().error(".deriveAssociatedForwarderFDNToken(): Unresolvable endpoint");
            return(null);
        }
        TopologyNodeFDN workshopNodeFDN = null;
        for(TopologyNodeFDN currentWorkshopFDN: getProcessingPlantInterface().getProcessingPlantNode().getWorkshops()){
            if(currentWorkshopFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WORKSHOP).getNodeName().equals(DefaultWorkshopSetEnum.EDGE_WORKSHOP.getWorkshop())){
                workshopNodeFDN = currentWorkshopFDN;
                break;
            }
        }
        if(workshopNodeFDN == null ){
            getLogger().error(".deriveAssociatedForwarderFDNToken(): Unresolvable workshop");
            return(null);
        }
        TopologyNodeFDN wupNodeFDN = SerializationUtils.clone(workshopNodeFDN);
        wupNodeFDN.appendTopologyNodeRDN(new TopologyNodeRDN(TopologyNodeTypeEnum.WUP, specifyForwarderWUPName(), specifyForwarderWUPVersion()));
        TopologyNodeFDNToken associatedForwarderWUPToken = wupNodeFDN.getToken();
        return(associatedForwarderWUPToken);
    }
}
