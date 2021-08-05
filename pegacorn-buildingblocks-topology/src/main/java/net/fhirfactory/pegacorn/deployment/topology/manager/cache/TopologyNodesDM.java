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
package net.fhirfactory.pegacorn.deployment.topology.manager.cache;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.common.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.deployment.properties.codebased.DeploymentSystemIdentificationInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Mark A. Hunter
 * @since 2020-07-01
 */
@ApplicationScoped
public class TopologyNodesDM implements DeploymentSystemIdentificationInterface {

    private static final Logger LOG = LoggerFactory.getLogger(TopologyNodesDM.class);

    private SolutionTopologyNode deploymentSolution;
    private ConcurrentHashMap<TopologyNodeFDN, TopologyNode> nodeSet;

    public TopologyNodesDM() {
        LOG.debug(".TopologyDM(): Constructor initialisation");
        this.deploymentSolution = null;
        this.nodeSet = new ConcurrentHashMap<TopologyNodeFDN, TopologyNode>();
    }

    private void insertOrOverwriteTopologyNode(SolutionTopologyNode node){
        boolean found=false;
        if(nodeSet.containsKey(node.getNodeFDN())){
            nodeSet.remove(node.getNodeFDN());
        } else {
            Enumeration<TopologyNodeFDN> nodeEnumeration = nodeSet.keys();
            while (nodeEnumeration.hasMoreElements()) {
                TopologyNodeFDN currentNodeFDN = nodeEnumeration.nextElement();
                TopologyNode currentNode = nodeSet.get(currentNodeFDN);
                if (currentNode.getNodeFDN().equals(node.getNodeFDN())) {
                    nodeSet.remove(currentNodeFDN);
                    break;
                }
            }
        }
        nodeSet.put(node.getNodeFDN(), node);
    }

    public SolutionTopologyNode getDeploymentSolution() {
        return deploymentSolution;
    }

    public void setDeploymentSolution(SolutionTopologyNode deploymentSolution) {
        this.deploymentSolution = deploymentSolution;
        insertOrOverwriteTopologyNode(deploymentSolution);
    }

    @Override
    public String getSystemName() {
        return (getDeploymentSolution().getNodeFDN().extractRDNForNodeType(TopologyNodeTypeEnum.SOLUTION).getNodeName());
    }

    @Override
    public String getSystemVersion() {
        return (getDeploymentSolution().getNodeFDN().extractRDNForNodeType(TopologyNodeTypeEnum.SOLUTION).getNodeVersion());
    }

    @Override
    public FDNToken getSystemIdentifier() {
        return (getDeploymentSolution().getNodeFDN().toTypeBasedFDN().getToken());
    }

    @Override
    public String getSystemOwnerName() {
        return (getDeploymentSolution().getSystemOwner());
    }

    /**
     * This function adds an entry to the Element Set.
     * <p>
     * Note that the default behaviour is to UPDATE the values with the set if
     * there already exists an instance for the specified FDNToken (identifier).
     *
     * Note, we have to do a deep inspection of the ConcurrentHashMap key (FDNToken) content,
     * as the default only only looks for equivalence with respect to the action Object instance.
     *
     * @param newElement The NodeElement to be added to the Set
     */
    public void addTopologyNode(TopologyNode newElement) {
        LOG.debug(".addTopologyNode(): Entry, newElement --> {}", newElement);
        if (newElement == null) {
            throw (new IllegalArgumentException(".addTopologyNode(): newElement is null"));
        }
        if (newElement.getNodeFDN() == null) {
            throw (new IllegalArgumentException(".addTopologyNode(): bad elementID within newElement"));
        }
        boolean elementFound = false;
        Enumeration<TopologyNodeFDN> elementIdentifiers = this.nodeSet.keys();
        TopologyNodeFDN currentNodeID = null;
        while (elementIdentifiers.hasMoreElements()) {
            currentNodeID = elementIdentifiers.nextElement();
            if (LOG.isTraceEnabled()) {
                LOG.trace(".addTopologyNode(): Cache Entry --> {}", currentNodeID.toTag());
            }
            if (currentNodeID.equals(newElement.getNodeFDN())){
                LOG.trace(".addTopologyNode(): Element already in Cache");
                elementFound = true;
                break;
            }
        }
        if (elementFound) {
            this.nodeSet.remove(currentNodeID);
            this.nodeSet.put(currentNodeID, newElement);
        } else {
            this.nodeSet.put(newElement.getNodeFDN(), newElement);
        }
        LOG.debug(".addTopologyNode(): Exit");
    }

    /**
     *
     * @param elementID
     */
    public void deleteTopologyNode(TopologyNodeFDN elementID) {
        LOG.debug(".deleteTopologyNode(): Entry, elementID --> {}", elementID);
        if (elementID == null) {
            throw (new IllegalArgumentException(".removeNode(): elementID is null"));
        }
        boolean elementFound = false;
        Enumeration<TopologyNodeFDN> list = this.nodeSet.keys();
        while (list.hasMoreElements()) {
            TopologyNodeFDN currentNodeID = list.nextElement();
            if (LOG.isTraceEnabled()) {
                LOG.trace(".deleteTopologyNode(): Cache Entry --> {}", currentNodeID.toTag());
            }
            if (currentNodeID.equals(elementID)) {
                LOG.trace(".deleteTopologyNode(): Element found, now removing it...");
                this.nodeSet.remove(elementID);
                elementFound = true;
            }
        }
        if(!elementFound){
            LOG.trace(".deleteTopologyNode(): No element with that elementID is in the map");
        }
        LOG.debug(".deleteTopologyNode(): Exit");
    }

    /**
     *
     * @return
     */
    public Set<TopologyNode> getTopologyNodeSet() {
        LOG.debug(".getTopologyNodeSet(): Entry");
        LinkedHashSet<TopologyNode> elementSet = new LinkedHashSet<TopologyNode>();
        if (this.nodeSet.isEmpty()) {
            LOG.debug(".getTopologyNodeSet(): Exit, The module map is empty, returning null");
            return (null);
        }
        elementSet.addAll(this.nodeSet.values());
        if (LOG.isDebugEnabled()) {
            LOG.debug(".getTopologyNodeSet(): Exit, returning an element set, size --> {}", elementSet.size());
        }
        return (elementSet);
    }

    /**
     *
     * @param nodeID
     * @return
     */
    public TopologyNode nodeSearch(TopologyNodeFDN nodeID) {
        LOG.debug(".getTopologyNode(): Entry, nodeID --> {}", nodeID);
        if (nodeID == null) {
            LOG.debug(".getTopologyNode(): Exit, provided a null nodeID , so returning null");
            return (null);
        }
        Enumeration<TopologyNodeFDN> list = this.nodeSet.keys();
        while (list.hasMoreElements()) {
            TopologyNodeFDN currentNodeID = list.nextElement();
            if (LOG.isTraceEnabled()) {
                LOG.trace(".getTopologyNode(): Cache Entry --> {}", currentNodeID.toTag());
            }
            if (currentNodeID.equals(nodeID)) {
                LOG.trace(".getTopologyNode(): Node found!!! WooHoo!");
                TopologyNode retrievedNode = this.nodeSet.get(currentNodeID);
                LOG.debug(".getTopologyNode(): Exit, returning Endpoint --> {}", retrievedNode);
                return (retrievedNode);
            }
        }
        LOG.debug(".getNode(): Exit, returning null as an element with the specified ID was not in the map");
        return (null);
    }


    private boolean stringValuesMatch(String stringA, String stringB){
        if(stringA == null && stringB == null){
            return(true);
        }
        if(stringA == null || stringB == null){
            return(false);
        }
        boolean stringsAreEqual = stringA.contentEquals(stringB);
        return(stringsAreEqual);
    }

    public List<TopologyNode> nodeSearch(TopologyNodeTypeEnum nodeType, String nodeName, String nodeVersion){
        LOG.debug(".nodeSearch(): Entry, nodeType->{}, nodeName->{}, nodeVersion->{}", nodeType, nodeName, nodeVersion);
        ArrayList<TopologyNode> nodeList = new ArrayList<>();
        for(TopologyNode currentNode: nodeSet.values()){
            if(LOG.isTraceEnabled()){
                LOG.trace(".nodeSearch(): Search Cache Entry : nodeRDN->{}, nodeComponentType->{}", currentNode.getNodeRDN(), currentNode.getComponentType());
            }
            boolean nodeTypeMatches = nodeType.equals(currentNode.getComponentType());
            boolean nodeNameMatches = nodeName.contentEquals(currentNode.getNodeRDN().getNodeName());
            boolean nodeVersionMatches = nodeVersion.contentEquals(currentNode.getNodeRDN().getNodeVersion());
            if(nodeTypeMatches && nodeNameMatches && nodeVersionMatches){
                LOG.trace(".nodeSearch(): Node found!!! Adding to search result!");
                nodeList.add(currentNode);
            }
        }
        return(nodeList);
    }

    public NetworkSecurityZoneEnum getDeploymentNetworkSecurityZone(String nodeName){
        Collection<TopologyNode> nodeCollection = nodeSet.values();
        for(TopologyNode currentNode: nodeCollection){
            boolean nameSame = currentNode.getNodeRDN().getNodeName().contentEquals(nodeName);
            boolean isProcessingPlant = currentNode.getComponentType().equals(TopologyNodeTypeEnum.PROCESSING_PLANT);
            if(nameSame && isProcessingPlant){
                return(currentNode.getSecurityZone());
            }
        }
        return(NetworkSecurityZoneEnum.ZONE_PUBLIC_INTERNET);
    }
}
