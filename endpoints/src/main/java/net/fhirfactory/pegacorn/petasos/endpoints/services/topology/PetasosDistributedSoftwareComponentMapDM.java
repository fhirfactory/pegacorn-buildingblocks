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
package net.fhirfactory.pegacorn.petasos.endpoints.services.topology;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PetasosDistributedSoftwareComponentMapDM {

    private static final Logger LOG = LoggerFactory.getLogger(PetasosDistributedSoftwareComponentMapDM.class);

    private ConcurrentHashMap<ComponentIdType, JGroupsIntegrationPointSummary> nodeOriginatingPoint;
    private ConcurrentHashMap<ComponentIdType, SoftwareComponent> nodeSet;
    private ConcurrentHashMap<ComponentIdType, String> participantMap;

    @Inject
    private ProcessingPlantInterface processingPlant;

    //
    // Constructor(s)
    //

    public PetasosDistributedSoftwareComponentMapDM() {
        LOG.debug(".ITOpsCollatedNodesDM(): Constructor initialisation");
        this.nodeSet = new ConcurrentHashMap<>();
        this.nodeOriginatingPoint = new ConcurrentHashMap<>();
        this.participantMap = new ConcurrentHashMap<>();
    }

    //
    // Business Functions
    //

    public void addTopologyNode(JGroupsIntegrationPointSummary endpointID, SoftwareComponent newElement) {
        LOG.debug(".addTopologyNode(): Entry, newElement --> {}", newElement);
        if (newElement == null) {
            throw (new IllegalArgumentException(".addTopologyNode(): newElement is null"));
        }
        if (newElement.getComponentId() == null) {
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
        Enumeration<ComponentIdType> elementIdentifiers = this.nodeSet.keys();
        ComponentIdType currentNodeID = null;
        while (elementIdentifiers.hasMoreElements()) {
            currentNodeID = elementIdentifiers.nextElement();
            if (LOG.isTraceEnabled()) {
                LOG.trace(".addTopologyNode(): Cache Entry --> {}", currentNodeID);
            }
            if (currentNodeID.equals(newElement.getComponentId())){
                LOG.trace(".addTopologyNode(): Element already in Cache");
                elementFound = true;
                break;
            }
        }
        if(StringUtils.isEmpty(newElement.getParticipantId().getSubsystemName())){
            newElement.getParticipant().getParticipantId().setSubsystemName(processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
        }
        String participantName = newElement.getParticipant().getParticipantId().getName();
        if (elementFound) {
            this.nodeSet.remove(currentNodeID);
            this.nodeSet.put(currentNodeID, newElement);
            this.nodeOriginatingPoint.put(currentNodeID, endpointID);
            this.participantMap.put(currentNodeID, participantName);
        } else {
            this.nodeSet.put(newElement.getComponentId(), newElement);
            this.nodeOriginatingPoint.put(newElement.getComponentId(), endpointID);
            this.participantMap.put(newElement.getComponentId(), participantName);
        }
        LOG.debug(".addTopologyNode(): Exit");
    }

    public void deleteTopologyNode(ComponentIdType elementID) {
        LOG.debug(".deleteTopologyNode(): Entry, elementID --> {}", elementID);
        if (elementID == null) {
            throw (new IllegalArgumentException(".removeNode(): elementID is null"));
        }
        this.nodeSet.remove(elementID);
        this.nodeOriginatingPoint.remove(elementID);
        this.participantMap.remove(elementID);
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

    public SoftwareComponent getTopologyNode(ComponentIdType nodeID) {
        LOG.debug(".getTopologyNode(): Entry, nodeID --> {}", nodeID);
        if (nodeID == null) {
            LOG.debug(".getTopologyNode(): Exit, provided a null nodeID , so returning null");
            return (null);
        }
        Enumeration<ComponentIdType> list = this.nodeSet.keys();
        while (list.hasMoreElements()) {
            ComponentIdType currentNodeID = list.nextElement();
            LOG.debug(".getTopologyNode(): Cache Entry --> {}", currentNodeID);
            if (currentNodeID.equals(nodeID)) {
                LOG.debug(".getTopologyNode(): Node found!!! WooHoo!");
                SoftwareComponent retrievedNode = this.nodeSet.get(currentNodeID);
                LOG.debug(".getTopologyNode(): Exit, returning Endpoint --> {}", retrievedNode);
                return (retrievedNode);
            }
        }
        LOG.debug(".getTopologyNode(): Exit, returning null as an element with the specified ID was not in the map");
        return (null);
    }

    public List<SoftwareComponent> nodeSearch(SoftwareComponentTypeEnum nodeType, String nodeName, String nodeVersion){
        LOG.debug(".nodeSearch(): Entry, nodeType->{}, nodeName->{}, nodeVersion->{}", nodeType, nodeName, nodeVersion);
        ArrayList<SoftwareComponent> nodeList = new ArrayList<>();
        for(SoftwareComponent currentNode: nodeSet.values()){
            if(LOG.isTraceEnabled()){
                LOG.trace(".nodeSearch(): Search Cache Entry : nodeRDN->{}, nodeComponentType->{}", currentNode.getComponentId(), currentNode.getComponentType());
            }
            boolean nodeTypeMatches = nodeType.equals(currentNode.getComponentType());
            boolean nodeNameMatches = nodeName.contentEquals(currentNode.getComponentId().getName());
            boolean nodeDisplayNameMatches = nodeName.contentEquals(currentNode.getComponentId().getDisplayName());
            boolean nodeParticipantNameMatches = nodeName.contentEquals(currentNode.getParticipant().getParticipantId().getName());
            boolean nodeVersionMatches = nodeVersion.contentEquals(currentNode.getVersion());
            if(nodeTypeMatches && (nodeNameMatches || nodeDisplayNameMatches || nodeParticipantNameMatches) && nodeVersionMatches){
                LOG.trace(".nodeSearch(): Node found!!! Adding to search result!");
                nodeList.add(currentNode);
            }
        }
        return(nodeList);
    }

    //
    // Processing Plant Origination Actions
    //

    public void removeDiscoveredProcessingPlant(JGroupsIntegrationPointSummary discoveredProcessingPlant){
        LOG.debug(".removeDiscoveredProcessingPlant(): Entry, discoveredProcessingPlant->{}", discoveredProcessingPlant);
        List<ComponentIdType> associatedNodes = new ArrayList<>();
        Enumeration<ComponentIdType> nodeFDNs = this.nodeOriginatingPoint.keys();
        while (nodeFDNs.hasMoreElements()) {
            ComponentIdType currentId = nodeFDNs.nextElement();
            JGroupsIntegrationPointSummary currentIP = this.nodeOriginatingPoint.get(currentId);
            if(currentIP.getChannelName().equals(discoveredProcessingPlant.getChannelName())){
                associatedNodes.add(currentId);
            }
        }
        for(ComponentIdType currentId: associatedNodes){
            this.nodeOriginatingPoint.remove(currentId);
            this.nodeSet.remove(currentId);
            this.participantMap.remove(currentId);
        }
    }

    //
    // Get Node
    //

    public SoftwareComponent getNode(String participantName){
        if(StringUtils.isEmpty(participantName)){
            return(null);
        }
        if(this.participantMap.isEmpty()){
            return(null);
        }
        Enumeration<ComponentIdType> nodeIdEnumerator = this.participantMap.keys();
        ComponentIdType foundId = null;
        while(nodeIdEnumerator.hasMoreElements()) {
            ComponentIdType currentComponentId = nodeIdEnumerator.nextElement();
            String currentParticipantName = this.participantMap.get(currentComponentId);
            if(currentComponentId.equals(participantName)){
                foundId = currentComponentId;
                break;
            }
        }
        if(foundId == null){
            return(null);
        }
        SoftwareComponent foundNode = this.nodeSet.get(foundId);
        return(foundNode);
    }


    //
    // Get All Nodes
    //
}
