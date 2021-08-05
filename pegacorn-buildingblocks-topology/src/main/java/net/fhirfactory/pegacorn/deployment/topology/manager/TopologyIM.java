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
package net.fhirfactory.pegacorn.deployment.topology.manager;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.interfaces.SolutionNodeFactoryInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.cache.TopologyNodesDM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * This class WILL do more in the future, but it is for now just a proxy to the
 * TopologyDM.
 */
@ApplicationScoped
public class TopologyIM {

    private static final Logger LOG = LoggerFactory.getLogger(TopologyIM.class);

    @Inject
    private TopologyNodesDM topologyDataManager;

    @Inject
    private SolutionNodeFactoryInterface solutionNodeFactory;

    @PostConstruct
    public void initialise(){
        solutionNodeFactory.initialise();
        SolutionTopologyNode solutionNode = solutionNodeFactory.getSolutionTopologyNode();
        setDeploymentSolution(solutionNode);
    }

    public void setDeploymentSolution(SolutionTopologyNode solution){
        LOG.debug(".setDeploymentSolution(): Entry, solution->{}", solution);
        topologyDataManager.setDeploymentSolution(solution);
        LOG.debug(".setDeploymentSolution(): Exit");
    }

    public SolutionTopologyNode getSolutionTopology(){
        SolutionTopologyNode solution = (SolutionTopologyNode) topologyDataManager.getDeploymentSolution();
        return(solution);
    }

    public void addTopologyNode(TopologyNodeFDN parentNodeFDN, TopologyNode newNodeElement) {
        LOG.debug(".addTopologyNode(): Entry, parentNodeFDN->{}, newElement->{}", parentNodeFDN, newNodeElement);

        newNodeElement.setContainingNodeFDN(parentNodeFDN);
        topologyDataManager.addTopologyNode(newNodeElement);
        TopologyNode parentNodeElement = getNode(parentNodeFDN);
        switch(newNodeElement.getComponentType()){
            case SUBSYSTEM: {
                SolutionTopologyNode solution = (SolutionTopologyNode) parentNodeElement;
                LOG.trace(".addTopologyNode(): Adding a Subsystem, parent Solution->{}", solution);
                SubsystemTopologyNode subsystem = (SubsystemTopologyNode) newNodeElement;
                TopologyNodeFDN newNodeFDN = subsystem.getNodeFDN();
                solution.getSubsystemList().add(newNodeFDN);
                break;
            }
            case EXTERNALISED_SERVICE: {
                SubsystemTopologyNode subsystem = (SubsystemTopologyNode) parentNodeElement;
                BusinessServiceTopologyNode businessService = (BusinessServiceTopologyNode) newNodeElement;
                subsystem.getBusinessServices().add(businessService.getNodeFDN());
                break;
            }
            case SITE: {
                BusinessServiceTopologyNode businessService = (BusinessServiceTopologyNode) parentNodeElement;
                DeploymentSiteTopologyNode deploymentSite = (DeploymentSiteTopologyNode) newNodeElement;
                businessService.getDeploymentSites().add(deploymentSite.getNodeFDN());
                break;
            }
            case CLUSTER_SERVICE:{
                DeploymentSiteTopologyNode deploymentSite = (DeploymentSiteTopologyNode) parentNodeElement;
                ClusterServiceTopologyNode clusterService = (ClusterServiceTopologyNode) newNodeElement;
                deploymentSite.getClusterServices().add(clusterService.getNodeFDN());
                break;
            }
            case PLATFORM:{
                ClusterServiceTopologyNode clusterService = (ClusterServiceTopologyNode) parentNodeElement;
                PlatformTopologyNode platformTopologyNode = (PlatformTopologyNode) newNodeElement;
                clusterService.getPlatformNodes().add(platformTopologyNode.getNodeFDN());
                break;
            }
            case PROCESSING_PLANT:{
                PlatformTopologyNode platformTopologyNode = (PlatformTopologyNode) parentNodeElement;
                ProcessingPlantTopologyNode processingPlant = (ProcessingPlantTopologyNode) newNodeElement;
                platformTopologyNode.getProcessingPlants().add(processingPlant.getNodeFDN());
                break;
            }
            case WORKSHOP:{
                ProcessingPlantTopologyNode processingPlant = (ProcessingPlantTopologyNode) parentNodeElement;
                WorkshopTopologyNode workshop = (WorkshopTopologyNode) newNodeElement;
                processingPlant.getWorkshops().add(workshop.getNodeFDN());
                break;
            }
            case WUP:{
                WorkshopTopologyNode workshop = (WorkshopTopologyNode) parentNodeElement;
                WorkUnitProcessorTopologyNode wup = (WorkUnitProcessorTopologyNode) newNodeElement;
                workshop.getWupSet().add(wup.getNodeFDN());
                WorkUnitProcessorComponentTopologyNode wupCore =  new WorkUnitProcessorComponentTopologyNode();
                wupCore.setContainingNodeFDN(workshop.getNodeFDN());
                wupCore.setComponentType(TopologyNodeTypeEnum.WUP_CORE);
                TopologyNodeFDN newFDN = new TopologyNodeFDN(wup.getNodeFDN());
                newFDN.appendTopologyNodeRDN(wup.getNodeRDN());
                wupCore.setNodeFDN(newFDN);
                wupCore.constructFunctionFDN(wup.getNodeFunctionFDN(), wup.getNodeRDN());
                topologyDataManager.addTopologyNode(wupCore);
                break;
            }
            case WUP_INTERCHANGE_ROUTER:
            case WUP_INTERCHANGE_PAYLOAD_TRANSFORMER:{
                WorkUnitProcessorTopologyNode wup = (WorkUnitProcessorTopologyNode) parentNodeElement;
                WorkUnitProcessorInterchangeComponentTopologyNode wupInterchangeComponent = (WorkUnitProcessorInterchangeComponentTopologyNode) newNodeElement;
                wup.getWupInterchangeComponents().add(wupInterchangeComponent.getNodeFDN());
            }
            case WUP_CORE:
            case WUP_CONTAINER_EGRESS_CONDUIT:
            case WUP_CONTAINER_INGRES_CONDUIT:
            case WUP_CONTAINER_EGRESS_PROCESSOR:
            case WUP_CONTAINER_INGRES_PROCESSOR:
            case WUP_CONTAINER_EGRESS_GATEKEEPER:
            case WUP_CONTAINER_INGRES_GATEKEEPER:{
                WorkUnitProcessorTopologyNode wup = (WorkUnitProcessorTopologyNode) parentNodeElement;
                WorkUnitProcessorComponentTopologyNode wupComponent = (WorkUnitProcessorComponentTopologyNode) newNodeElement;
                wup.getWupComponents().add(wupComponent.getNodeFDN());
            }
            case ENDPOINT:{
                switch(parentNodeElement.getComponentType()){
                    case EXTERNALISED_SERVICE:{
                        BusinessServiceTopologyNode businessService = (BusinessServiceTopologyNode) parentNodeElement;
                        IPCTopologyEndpoint endpoint = (IPCTopologyEndpoint) newNodeElement;
                        businessService.getExternalisedServices().add(endpoint.getNodeFDN());
                        break;
                    }
                    case CLUSTER_SERVICE:{
                        ClusterServiceTopologyNode clusterService = (ClusterServiceTopologyNode) parentNodeElement;
                        IPCTopologyEndpoint endpoint = (IPCTopologyEndpoint) newNodeElement;
                        clusterService.getServiceEndpoints().add(endpoint.getNodeFDN() );
                        break;
                    }
                    case PROCESSING_PLANT:{
                        ProcessingPlantTopologyNode processingPlant = (ProcessingPlantTopologyNode) parentNodeElement;
                        IPCTopologyEndpoint endpoint = (IPCTopologyEndpoint) newNodeElement;
                        processingPlant.getEndpoints().add(endpoint.getNodeFDN());
                        break;
                    }
                    default:
                        // Do nothing
                }
            }
            case SOLUTION:
            default:{
                // Do nothing
            }
        }
    }

    public void removeNode(TopologyNodeFDN elementID) {
        LOG.debug(".unregisterNode(): Entry, elementID --> {}", elementID);
        topologyDataManager.deleteTopologyNode(elementID);
    }

    public Set<TopologyNode> getNodeElementSet() {
        LOG.debug(".getNodeSet(): Entry");
        return (topologyDataManager.getTopologyNodeSet());
    }

    public TopologyNode getNode(TopologyNodeFDN nodeID) {
        LOG.debug(".getNode(): Entry, nodeID --> {}", nodeID);
        TopologyNode retrievedNode = topologyDataManager.nodeSearch(nodeID);
        LOG.debug(".getNode(): Exit, retrievedNode --> {}", retrievedNode);
        return (retrievedNode);
    }

    public TopologyNode getNode(TopologyNodeFDNToken nodeFDNToken){
        LOG.debug(".getNode(): Entry, nodeFDNToken --> {}", nodeFDNToken);
        TopologyNodeFDN nodeFDN = new TopologyNodeFDN(nodeFDNToken);
        TopologyNode retrievedNode = getNode(nodeFDN);
        LOG.debug(".getNode(): Exit, retrievedNode --> {}", retrievedNode);
        return(retrievedNode);
    }

    public List<TopologyNode> nodeSearch(TopologyNodeTypeEnum nodeType, String nodeName,  String nodeVersion ){
        List<TopologyNode> nodeList = topologyDataManager.nodeSearch(nodeType, nodeName, nodeVersion);
        return(nodeList);
    }

    public NetworkSecurityZoneEnum getDeploymentNetworkSecurityZone(String nodeName){
        return(getDeploymentNetworkSecurityZone(nodeName));
    }
}
