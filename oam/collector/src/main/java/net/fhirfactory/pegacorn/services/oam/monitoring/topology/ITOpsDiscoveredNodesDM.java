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
package net.fhirfactory.pegacorn.services.oam.monitoring.topology;

import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ITOpsDiscoveredNodesDM {

    private static final Logger LOG = LoggerFactory.getLogger(ITOpsDiscoveredNodesDM.class);

    private ConcurrentHashMap<TopologyNodeFDN, PetasosEndpointIdentifier> nodeOriginatingPoint;
    private ConcurrentHashMap<TopologyNodeFDN, SoftwareComponent> nodeSet;
    private ConcurrentHashMap<String, TopologyNodeFDN> simpleNameMap;

    public ITOpsDiscoveredNodesDM() {
        LOG.debug(".ITOpsCollatedNodesDM(): Constructor initialisation");
        this.nodeSet = new ConcurrentHashMap<TopologyNodeFDN, SoftwareComponent>();
        this.nodeOriginatingPoint = new ConcurrentHashMap<>();
        this.simpleNameMap = new ConcurrentHashMap<>();
    }

    public void addTopologyNode(PetasosEndpointIdentifier endpointID, SoftwareComponent newElement) {
        LOG.debug(".addTopologyNode(): Entry, newElement --> {}", newElement);
        if (newElement == null) {
            throw (new IllegalArgumentException(".addTopologyNode(): newElement is null"));
        }
        if (newElement.getComponentFDN() == null) {
            throw (new IllegalArgumentException(".addTopologyNode(): bad elementID within newElement"));
        }
        switch(newElement.getComponentType()){
            case ENDPOINT:
            case WUP:
            case OAM_WORK_UNIT_PROCESSOR:
            case PROCESSING_PLANT:
            case OAM_WORKSHOP:
            case WORKSHOP:{
                break;
            }
            default:{
                return;
            }
        }
        boolean elementFound = false;
        Enumeration<TopologyNodeFDN> elementIdentifiers = this.nodeSet.keys();
        TopologyNodeFDN currentNodeID = null;
        while (elementIdentifiers.hasMoreElements()) {
            currentNodeID = elementIdentifiers.nextElement();
            if (LOG.isTraceEnabled()) {
                LOG.trace(".addTopologyNode(): Cache Entry --> {}", currentNodeID.toTag());
            }
            if (currentNodeID.equals(newElement.getComponentFDN())){
                LOG.trace(".addTopologyNode(): Element already in Cache");
                elementFound = true;
                break;
            }
        }
        String simpleName = newElement.getComponentID().getId();
        if (elementFound) {
            this.nodeSet.remove(currentNodeID);
            this.nodeSet.put(currentNodeID, newElement);
            this.nodeOriginatingPoint.put(currentNodeID, endpointID);
            this.simpleNameMap.put(simpleName, currentNodeID);
        } else {
            this.nodeSet.put(newElement.getComponentFDN(), newElement);
            this.nodeOriginatingPoint.put(newElement.getComponentFDN(), endpointID);
            this.simpleNameMap.put(simpleName, newElement.getComponentFDN());
        }
        LOG.debug(".addTopologyNode(): Exit");
    }

    public void deleteTopologyNode(TopologyNodeFDN elementID) {
        LOG.debug(".deleteTopologyNode(): Entry, elementID --> {}", elementID);
        if (elementID == null) {
            throw (new IllegalArgumentException(".removeNode(): elementID is null"));
        }
        this.nodeSet.remove(elementID);
        this.nodeOriginatingPoint.remove(elementID);
        String simpleName = null;
        Enumeration<String> nameEnumeration = this.simpleNameMap.keys();
        while(nameEnumeration.hasMoreElements()){
            String currentName = nameEnumeration.nextElement();
            TopologyNodeFDN currentNodeFDN = this.simpleNameMap.get(currentName);
            if(currentNodeFDN.equals(elementID)){
                simpleName = currentName;
                break;
            }
        }
        if(simpleName != null){
            this.simpleNameMap.remove(simpleName);
        }
        LOG.debug(".deleteTopologyNode(): Exit");
    }

    public Set<SoftwareComponent> getTopologyNodeSet() {
        LOG.debug(".getTopologyNodeSet(): Entry");
        Set<SoftwareComponent> elementSet = new LinkedHashSet<SoftwareComponent>();
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

    public SoftwareComponent getTopologyNode(TopologyNodeFDN nodeID) {
        LOG.info(".getTopologyNode(): Entry, nodeID --> {}", nodeID);
        if (nodeID == null) {
            LOG.debug(".getTopologyNode(): Exit, provided a null nodeID , so returning null");
            return (null);
        }
        Enumeration<TopologyNodeFDN> list = this.nodeSet.keys();
        while (list.hasMoreElements()) {
            TopologyNodeFDN currentNodeID = list.nextElement();
            LOG.info(".getTopologyNode(): Cache Entry --> {}", currentNodeID);
            if (currentNodeID.equals(nodeID)) {
                LOG.info(".getTopologyNode(): Node found!!! WooHoo!");
                SoftwareComponent retrievedNode = this.nodeSet.get(currentNodeID);
                LOG.info(".getTopologyNode(): Exit, returning Endpoint --> {}", retrievedNode);
                return (retrievedNode);
            }
        }
        LOG.info(".getTopologyNode(): Exit, returning null as an element with the specified ID was not in the map");
        return (null);
    }

    public List<SoftwareComponent> nodeSearch(ComponentTypeTypeEnum nodeType, String nodeName, String nodeVersion){
        LOG.debug(".nodeSearch(): Entry, nodeType->{}, nodeName->{}, nodeVersion->{}", nodeType, nodeName, nodeVersion);
        ArrayList<SoftwareComponent> nodeList = new ArrayList<>();
        for(SoftwareComponent currentNode: nodeSet.values()){
            if(LOG.isTraceEnabled()){
                LOG.trace(".nodeSearch(): Search Cache Entry : nodeRDN->{}, nodeComponentType->{}", currentNode.getComponentRDN(), currentNode.getComponentType());
            }
            boolean nodeTypeMatches = nodeType.equals(currentNode.getComponentType());
            boolean nodeNameMatches = nodeName.contentEquals(currentNode.getComponentRDN().getNodeName());
            boolean nodeVersionMatches = nodeVersion.contentEquals(currentNode.getComponentRDN().getNodeVersion());
            if(nodeTypeMatches && nodeNameMatches && nodeVersionMatches){
                LOG.trace(".nodeSearch(): Node found!!! Adding to search result!");
                nodeList.add(currentNode);
            }
        }
        return(nodeList);
    }

    //
    // Processing Plant Origination Actions
    //

    public void removeDiscoveredProcessingPlant(PetasosEndpointIdentifier discoveredProcessingPlant){
        LOG.debug(".removeDiscoveredProcessingPlant(): Entry, discoveredProcessingPlant->{}", discoveredProcessingPlant);
        List<TopologyNodeFDN> associatedNodes = new ArrayList<>();
        Enumeration<TopologyNodeFDN> nodeFDNs = this.nodeOriginatingPoint.keys();
        while (nodeFDNs.hasMoreElements()) {
            TopologyNodeFDN currentFDN = nodeFDNs.nextElement();
            PetasosEndpointIdentifier currentIdentifier = this.nodeOriginatingPoint.get(currentFDN);
            if(currentIdentifier.equals(discoveredProcessingPlant)){
                associatedNodes.add(currentFDN);
            }
        }
        for(TopologyNodeFDN currentFDN: associatedNodes){
            this.nodeOriginatingPoint.remove(currentFDN);
            this.nodeSet.remove(currentFDN);
        }
        List<String> associatedNameSet = new ArrayList<>();
        Enumeration<String> currentMappedNameSet = this.simpleNameMap.keys();
        while (currentMappedNameSet.hasMoreElements()) {
            String currentMappedName = currentMappedNameSet.nextElement();
            TopologyNodeFDN currentMappedFDN = this.simpleNameMap.get(currentMappedName);
            if(associatedNodes.contains(currentMappedFDN)){
                associatedNameSet.add(currentMappedName);
            }
        }
        for(String currentMappedName: associatedNameSet){
            this.simpleNameMap.remove(currentMappedName);
        }
    }

    //
    // Get Node
    //

    public SoftwareComponent getNode(String simpleName){
        if(StringUtils.isEmpty(simpleName)){
            return(null);
        }
        TopologyNodeFDN foundNodeFDN = this.simpleNameMap.get(simpleName);
        if(foundNodeFDN == null){
            return(null);
        }
        SoftwareComponent foundNode = this.nodeSet.get(foundNodeFDN);
        return(foundNode);
    }


    //
    // Get All Nodes
    //
}
