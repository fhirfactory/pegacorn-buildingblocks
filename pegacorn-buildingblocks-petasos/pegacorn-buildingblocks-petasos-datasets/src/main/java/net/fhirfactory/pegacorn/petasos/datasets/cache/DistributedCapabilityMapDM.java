/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.datasets.cache;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosEndpoint;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNode;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNodeRegistration;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityRoutingEndpoint;
import net.fhirfactory.pegacorn.core.model.tasks.base.PetasosCapabilityCommonName;
import net.fhirfactory.pegacorn.core.model.tasks.base.PetasosCapabilityDefinition;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNodeSet;
import net.fhirfactory.pegacorn.core.model.tasks.valuesets.PetasosCapabilityDeliveryNodeRegistrationStatusEnum;
import net.fhirfactory.pegacorn.core.model.tasks.valuesets.PetasosCapabilityDeliveryNodeStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The DistributedCapabilityMapDM maintains a present-state representation of the discovered CapabilityDeliveryNodes,
 * this ProcessingPlant's required Capabilities and a map between the required capabilities and which
 * CapabilityDeliveryNodes are fulfilling them.
 *
 * The key players (elements, classes) within the scope of the DistributedCapabilityMapDM function are:
 *
 * PetasosCapabilityDeliveryNode: A purpose-defined WorkUnitProcessor (WUP) that presents an EntryPoint (Camel Route) for the
 * synchronous or asynchronous execution (delivery) of a task-completion (method/business-process) activity (effectively
 * delivering the Capability). A CapabilityDeliveryNode may support one or more PetasosCapabilityDefinitions (Capabilities).
 *
 * PetasosCapabilityDeliveryNodeStatusEnum: Is included in the above PetasosCapabilityDeliveryNode, and informs this
 * ProcessingPlant of the operational status (detected, tested) of the PetasosCapabilityDeliveryNode.
 *
 * PetasosCapabilityDeliveryNodeRegistration: An encapsulating object of the CapabilityDeliveryNode which includes a
 * an attriubte to capture the registration status (PetasosCapabilityDeliveryNodeRegistrationStatusEnum) and the last
 * activity time of the node (from this ProcessingPlant's perspective).
 *
 * PetasosCapabilityRoutingEndpoint: A subclass of the PetasosEndpoint, it represents the routing (messaging, API call-
 * point) which can be used to reeach specific CapabilityDeliveryNodes.
 *
 * The "State-Machine" for the various elements is as follows (note, there are two distinct state-machines, one
 * for the REQUIRED Capabilities and one for the DISCOVERED CAPABILITIES).
 *
 * DISCOVERED CAPABILITY STATE-MACHINE
 *
 * 0. No Capability(s) Detected
 *
 * 1. PetasosCapabilityRoutingEndpoint detected by the OAMDiscovery framework
 *      - An Entry in the capabilityRoutingEndpointMap (with status DETECTED) will be made
 *
 * 2. PetasosCapabilityDeliveryNode Detected (via the OAMCapability framework)
 *      - An Entry (PetasosCapabilityDeliveryNodeRegistration) in the capabilityDeliveryNodeRegistrationMap is made
 *      - The capabilityRoutingEndpointMap is updated to reflect status of OPERATIONAL
 *      - The status of the Entry (PetasosCapabilityDeliveryNode) is checked:
 *          - OPERATIONAL: The capabilityRequirementMap is checked to see if any required capabilities are met
 */
@ApplicationScoped
public class DistributedCapabilityMapDM {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedCapabilityMapDM.class);

    private ConcurrentHashMap<TopologyNodeFDNToken, PetasosCapabilityDeliveryNode> localCapabilitySet;
    private Object localCapabilitySetLock;

    // ConcurrentHashMap<PetasosEndpointIdentifier.endpointName, PetasosCapabilityRoutingEndpoint>
    // Maintains a map of the task completion engine and its registration within the local cache
    private ConcurrentHashMap<String, PetasosCapabilityRoutingEndpoint> capabilityRoutingEndpointMap;
    private Object capabilityRoutingEndpointMapLock;

    // ConcurrentHashMap<PetasosCapabilityDeliveryNode.deliveryNodeID, PetasosCapabilityDeliveryNodeRegistration>
    // Maintains a map of the ID of a PetasosCapabilityDeliveryNode and its ID
    private ConcurrentHashMap<TopologyNodeFDNToken, PetasosCapabilityDeliveryNodeRegistration> capabilityDeliveryNodeRegistrationMap;
    private Object capabilityDeliveryNodeRegistrationMapLock;

    // ConcurrentHashMap<PetasosCapabilityCommonName, List<TopologyNodeFDNToken>>
    // Maintains a map of the (common name of) capabilities and the task completion engines that can fulfill them
    private ConcurrentHashMap<PetasosCapabilityCommonName, List<TopologyNodeFDNToken>> capabilityMap;
    private Object capabilityMapLock;

    // ConcurrentHashMap<PetasosCapabilityCommonName, boolean>
    // Maintains a map of capabilities required by this ProcessingPlant and whether they are fulfilled or not
    private ConcurrentHashMap<PetasosCapabilityCommonName, Boolean> capabilityFulfillmentStatusMap;
    private Object capabilityFulfillmentStatusMapLock;

    // ConcurrentHashMap<PetasosCapabilityCommonName, List<TopologyNodeFDNToken>>
    // Maintains a map of capabilities required and which (local) WUPs require them
    private ConcurrentHashMap<PetasosCapabilityCommonName, List<TopologyNodeFDNToken>> capabilityRequirementMap;
    private Object capabilityRequirementMapLock;

    public DistributedCapabilityMapDM(){
        this.localCapabilitySet = new ConcurrentHashMap<>();
        this.localCapabilitySetLock = new Object();
        this.capabilityRoutingEndpointMap = new ConcurrentHashMap<>();
        this.capabilityRoutingEndpointMapLock = new ConcurrentHashMap<>();
        this.capabilityDeliveryNodeRegistrationMap = new ConcurrentHashMap<>();
        this.capabilityDeliveryNodeRegistrationMapLock = new Object();
        this.capabilityMap = new ConcurrentHashMap<>();
        this.capabilityMapLock = new Object();
        this.capabilityFulfillmentStatusMap = new ConcurrentHashMap<>();
        this.capabilityFulfillmentStatusMapLock = new Object();
        this.capabilityRequirementMap = new ConcurrentHashMap<>();
        this.capabilityRequirementMapLock = new Object();
    }

    //
    // Local Capability Delivery Node Set Management
    //

    public void addLocalCapabilityNode(PetasosCapabilityDeliveryNode localNode){
        if(localNode == null){
            return;
        }
        TopologyNodeFDNToken nodeID = localNode.getDeliveryNodeID();
        if(nodeID == null){
            return;
        }
        synchronized (localCapabilitySetLock){
            if(localCapabilitySet.containsKey(nodeID)){
                localCapabilitySet.remove(nodeID);
            }
            localCapabilitySet.put(nodeID, localNode);
        }
    }

    public void removeLocalCapabilityNode(PetasosCapabilityDeliveryNode localNode){
        if(localNode == null){
            return;
        }
        TopologyNodeFDNToken nodeID = localNode.getDeliveryNodeID();
        removeLocalCapabilityNode(nodeID);
    }

    public void removeLocalCapabilityNode(TopologyNodeFDNToken localNodeID){
        if(localNodeID == null){
            return;
        }
        synchronized(localCapabilitySetLock){
            if(localCapabilitySet.containsKey(localNodeID)){
                localCapabilitySet.remove(localNodeID);
            }
        }
    }

    public PetasosCapabilityDeliveryNodeSet getLocalCapabilityNodeSet(){
        PetasosCapabilityDeliveryNodeSet deliveryNodeSet= new PetasosCapabilityDeliveryNodeSet();
        if(localCapabilitySet.isEmpty()){
            return(deliveryNodeSet);
        }
        synchronized (localCapabilitySetLock){
            Collection<PetasosCapabilityDeliveryNode> capabilityDeliveryNodes = localCapabilitySet.values();
            deliveryNodeSet.getCapabilityDeliveryNodeSet().addAll(capabilityDeliveryNodes);
        }
        return(deliveryNodeSet);
    }
    //
    // Routing Endpoint Map
    //

    public void addCapabilityRoutingEndpoint(PetasosCapabilityRoutingEndpoint routingEndpoint){
        if(routingEndpoint == null){
            return;
        }
        PetasosEndpointIdentifier petasosEndpointID = routingEndpoint.getEndpointID();
        if(petasosEndpointID == null){
            return;
        }
        String petasosEndpointName = petasosEndpointID.getEndpointName();
        if(StringUtils.isEmpty(petasosEndpointName)){
            return;
        }
        synchronized(this.capabilityRoutingEndpointMapLock) {
            if (this.capabilityRoutingEndpointMap.containsKey(petasosEndpointName)) {
                this.capabilityRoutingEndpointMap.remove(petasosEndpointName);
            }
            this.capabilityRoutingEndpointMap.put(petasosEndpointName, routingEndpoint);
        }
    }

    public void removeCapabilityRoutingEndpoint(PetasosCapabilityRoutingEndpoint routingEndpoint){
        if(routingEndpoint == null){
            return;
        }
        PetasosEndpointIdentifier petasosEndpointID = routingEndpoint.getEndpointID();
        if(petasosEndpointID == null){
            return;
        }
        String petasosEndpointName = petasosEndpointID.getEndpointName();
        if(StringUtils.isEmpty(petasosEndpointName)){
            return;
        }
        synchronized(this.capabilityRoutingEndpointMapLock) {
            if (this.capabilityRoutingEndpointMap.containsKey(petasosEndpointName)) {
                this.capabilityRoutingEndpointMap.remove(petasosEndpointName);
            }
        }
    }

    private void removeAssociatedCapabilityDeliveryNodes(PetasosCapabilityRoutingEndpoint routingEndpoint){
        synchronized(this.capabilityMapLock){
            Collection<List<PetasosCapabilityDeliveryNodeRegistration>> deliveryNodeSetPerCapability = this.capabilityMap.values();
            for(List<PetasosCapabilityDeliveryNodeRegistration> currentList: deliveryNodeSetPerCapability){
                List<PetasosCapabilityDeliveryNodeRegistration> removalList = new ArrayList<>();
                for(PetasosCapabilityDeliveryNodeRegistration currentNodeRegistration: currentList){
                    if(currentNodeRegistration.getDeliveryNode().getRoutingEndpointID().getEndpointName().contentEquals(routingEndpoint.getRoutingEndpoint().getEndpointID().getEndpointName())){
                        removalList.add(currentNodeRegistration);
                    }
                }
                for(PetasosCapabilityDeliveryNodeRegistration currentNodeToBeRemoved: removalList){
                    currentList.remove(currentNodeToBeRemoved);
                }
            }
            Enumeration<PetasosCapabilityCommonName> capabilityNameSet = this.capabilityMap.keys();
            List<PetasosCapabilityCommonName> capabilityRemovalList = new ArrayList<>();
            while(capabilityNameSet.hasMoreElements()){
                PetasosCapabilityCommonName currentCapabilityName = capabilityNameSet.nextElement();
                List<PetasosCapabilityDeliveryNodeRegistration> deliveryNodeList = this.capabilityMap.get(currentCapabilityName);
                if(deliveryNodeList.isEmpty()){
                    capabilityRemovalList.add(currentCapabilityName);
                }
            }
            for(PetasosCapabilityCommonName currentCapabilityNameToBeRemoved: capabilityRemovalList){
                this.capabilityMap.remove(currentCapabilityNameToBeRemoved);
            }
        }
    }

    //
    // Capability Registration
    //

    public List<PetasosCapabilityDeliveryNodeRegistration> registerCapabilityDeliveryNodeSet(PetasosCapabilityDeliveryNodeSet capabilityDeliveryNodeSet){
        List<PetasosCapabilityDeliveryNodeRegistration> registrationList = new ArrayList<>();
        if(capabilityDeliveryNodeSet == null){
            return(registrationList);
        }
        if(capabilityDeliveryNodeSet.getCapabilityDeliveryNodeSet().isEmpty()){
            return(registrationList);
        }
        for(PetasosCapabilityDeliveryNode currentCapabilityNode: capabilityDeliveryNodeSet.getCapabilityDeliveryNodeSet()){
            PetasosCapabilityDeliveryNodeRegistration currentRegistration = registerCapabilityDeliveryNode(currentCapabilityNode);
            if(currentRegistration != null){
                registrationList.add(currentRegistration);
            }
        }
        return(registrationList);
    }

    public PetasosCapabilityDeliveryNodeRegistration registerCapabilityDeliveryNode(PetasosCapabilityDeliveryNode capabilityDeliveryNode){
        LOG.info(".registerCapabilityDeliveryNode(): Entry, capabilityDeliveryNode->{}", capabilityDeliveryNode);
        LOG.trace(".registerCapabilityDeliveryNode(): First, we check the content of the passed-in parameter");
        if(capabilityDeliveryNode == null){
            LOG.debug("registerCapabilityDeliveryNode(): Exit, engine is null, returning -null-");
            return(null);
        }
        if(capabilityDeliveryNode.getRoutingEndpointID() == null){
            LOG.debug("registerCapabilityDeliveryNode(): Exit, engine.routingEnpointID is null, return -null");
            return(null);
        }
        String endpointServiceName = capabilityDeliveryNode.getRoutingEndpointID().getEndpointServiceName();
        if(StringUtils.isEmpty(endpointServiceName)){
            LOG.debug("registerCapabilityDeliveryNode(): Exit, engine.routingEnpointID.endpointServiceName is null, return -null");
            return(null);
        }
        TopologyNodeFDNToken capabilityNodeID = capabilityDeliveryNode.getDeliveryNodeID();
        if(capabilityNodeID == null){
            LOG.debug("registerCapabilityDeliveryNode(): Exit, engine.getDeliveryNodeID is null, return -null");
            return(null);
        }

        LOG.trace(".registerCapabilityDeliveryNode(): Now, check to see if Engine (instance) is already cached and, if so, do nothing!");
        if(capabilityDeliveryNodeRegistrationMap.containsKey(capabilityNodeID)){
            PetasosCapabilityDeliveryNodeRegistration currentRegistration = capabilityDeliveryNodeRegistrationMap.get(capabilityNodeID);
            LOG.debug(".registerCapabilityDeliveryNode(): Exit, node already registered->{}", currentRegistration);
            return(currentRegistration);
        }
        LOG.info(".registerCapabilityDeliveryNode(): Engine is not in Map, so add it!");
        PetasosCapabilityDeliveryNodeRegistration registration = new PetasosCapabilityDeliveryNodeRegistration();
        registration.setRegistrationDate(Instant.now());
        registration.setLastActivityDate(Instant.now());
        registration.setDeliveryNode(capabilityDeliveryNode);
        registration.setRegistrationStatus(PetasosCapabilityDeliveryNodeRegistrationStatusEnum.REGISTRATION_STATUS_REGISTERED);
        synchronized (this.capabilityDeliveryNodeRegistrationMapLock) {
            this.capabilityDeliveryNodeRegistrationMap.put(capabilityDeliveryNode.getDeliveryNodeID(), registration);
        }
        addTopologyNodeRoute(capabilityDeliveryNode);
        addCapabilityDeliveryNodeInstance(capabilityDeliveryNode);
        LOG.warn(".registerCapabilityDeliveryNode(): Exit, registration->{}", registration);
        return (registration);
    }

    public void unregisterCapabilityDeliveryNode(String petasosRoutingEndpointName){
        LOG.info(".unregisterCapabilityDeliveryNode(): Entry, petasosRoutingEndpointName->{}", petasosRoutingEndpointName);
        PetasosCapabilityDeliveryNodeRegistration registration = null;
        if(StringUtils.isEmpty(petasosRoutingEndpointName)){
            LOG.info("unregisterCapabilityDeliveryNode(): Exit, petasosRoutingEndpointName is null");
        }
        Collection<PetasosCapabilityDeliveryNodeRegistration> registrationSet = capabilityDeliveryNodeRegistrationMap.values();
        for(PetasosCapabilityDeliveryNodeRegistration currentRegistration: registrationSet){
            String currentEndpointName = currentRegistration.getDeliveryNode().getRoutingEndpointID().getEndpointName();
            if(currentEndpointName.contentEquals(petasosRoutingEndpointName)){
                LOG.debug(".unregisterCapabilityDeliveryNode(): Exit, Engine registered for the specified routingEndpointName->{}", currentRegistration);
                registration = currentRegistration;
            }
        }
        LOG.info(".unregisterCapabilityDeliveryNode(): petasosRoutingEndpointName is not null");
        if(registration != null){
            synchronized (this.capabilityDeliveryNodeRegistrationMapLock) {
                registration = this.capabilityDeliveryNodeRegistrationMap.remove(registration.getDeliveryNode().getDeliveryNodeID());
            }
            LOG.info(".unregisterCapabilityDeliveryNode(): Clean up ServiceFulfillmentMap [Start]");
            removeCapabilityNodeInstance(registration.getDeliveryNode());
            LOG.info(".unregisterCapabilityDeliveryNode(): Clean up ServiceFulfillmentMap [Finish]");
            LOG.info(".unregisterCapabilityDeliveryNode(): Validating availability of Task Execution Engine [Start]");
            for(PetasosCapabilityDefinition currentCapabilityDefinition: registration.getDeliveryNode().getSupportedCapabilities()){
                LOG.info(".unregisterCapabilityDeliveryNode(): Validating availability of Capability->{}", currentCapabilityDefinition);
                isCapabilityDeliveryNodeAvailableForCapability(currentCapabilityDefinition.getPetasosCapabilityCommonName());
            }
            LOG.info(".unregisterCapabilityDeliveryNode(): Validating availability of Task Execution Engine [Finished]");
        }
    }

    public PetasosCapabilityDeliveryNodeRegistration getCapabilityDeliveryNodeRegistration(String deliveryNodeRouteEndpointName){
        LOG.debug(".getTaskEngineRegistration(): Entry, deliveryNodeRouteEndpointName->{}", deliveryNodeRouteEndpointName);
        LOG.trace(".getTaskEngineRegistration(): First, we check the content of the passed-in parameter");
        if(StringUtils.isEmpty(deliveryNodeRouteEndpointName)){
            LOG.debug("getTaskEngineRegistration(): Exit, deliveryNodeRouteEndpointName is empty, returning null");
            return(null);
        }
        LOG.trace(".getTaskEngineRegistration(): Now, check to see if node (instance) is in the cache and, if so, return detail!");
        Collection<PetasosCapabilityDeliveryNodeRegistration> registrationSet = capabilityDeliveryNodeRegistrationMap.values();
        PetasosCapabilityDeliveryNodeRegistration registration = null;
        for(PetasosCapabilityDeliveryNodeRegistration currentRegistration: registrationSet){
            String currentEndpointName = currentRegistration.getDeliveryNode().getRoutingEndpointID().getEndpointName();
            if(currentEndpointName.contentEquals(deliveryNodeRouteEndpointName)){
                LOG.debug(".isCapabilityDeliveryNodeRegistered(): Exit, Engine registered for the specified routingEndpointName->{}", currentRegistration);
                return(currentRegistration);
            }
        }
        LOG.debug("getTaskEngineRegistration(): Exit, Could not find registration, returning null");
        return (null);
    }

    //
    // Capability Delivery Traceability
    //

    private void addCapabilityDeliveryNodeInstance(PetasosCapabilityDeliveryNode deliveryNode){
        LOG.info(".addTaskExecutionEngineServiceInstance(): Entry, deliveryNode->{}", deliveryNode);
        if(deliveryNode == null){
            LOG.info(".addTaskExecutionEngineServiceInstance(): Exit, Engine is null");
            return;
        }
        if(deliveryNode.getRoutingEndpointID() == null){
            LOG.info(".addTaskExecutionEngineServiceInstance(): Exit, deliveryNode.getRoutingEndpointID() is null");
            return;
        }
        String engineRoutingEndpointName = deliveryNode.getRoutingEndpointID().getEndpointName();
        for(PetasosCapabilityDefinition currentCapabilityDefintion: deliveryNode.getSupportedCapabilities()) {
            PetasosCapabilityCommonName currentCommonName = currentCapabilityDefintion.getPetasosCapabilityCommonName();
            synchronized (capabilityMapLock) {
                List<PetasosCapabilityDeliveryNode> deliveryNodeList = capabilityMap.get(currentCommonName);
                if (deliveryNodeList == null) {
                    LOG.info(".addTaskExecutionEngineServiceInstance(): No map entry exists for service ({}), so creating it", currentCommonName);
                    deliveryNodeList = new ArrayList<>();
                    deliveryNodeList.add(deliveryNode);
                    capabilityMap.put(currentCommonName, deliveryNodeList);
                } else {
                    LOG.info(".addTaskExecutionEngineServiceInstance(): No map entry for service ({}), so just adding list-entry", engineRoutingEndpointName);
                    if (deliveryNodeList.contains(engineRoutingEndpointName)) {
                        // do nothing
                    } else {
                        deliveryNodeList.add(deliveryNode);
                    }
                }
            }
        }
        checkForCapabilityFulfillment(deliveryNode.getSupportedCapabilities());
        LOG.info(".addTaskExecutionEngineServiceInstance(): Exit, Execution Engine instance added");
    }

    private void removeCapabilityNodeInstance(PetasosCapabilityDeliveryNode deliveryNode){
        LOG.info(".removeCapabilityNodeInstance(): Entry, deliveryNode->{}", deliveryNode);
        if(deliveryNode == null){
            LOG.info(".removeCapabilityNodeInstance(): Exit, deliveryNode is null");
            return;
        }
        if(deliveryNode.getRoutingEndpointID() == null){
            LOG.info(".removeCapabilityNodeInstance(): Exit, deliveryNode.getRoutingEndpointID() is null");
            return;
        }
        String engineRoutingEndpointName = deliveryNode.getRoutingEndpointID().getEndpointName();
        for(PetasosCapabilityDefinition currentCapabilityDefintion: deliveryNode.getSupportedCapabilities()) {
            PetasosCapabilityCommonName currentCommonName = currentCapabilityDefintion.getPetasosCapabilityCommonName();
            synchronized (capabilityMapLock) {
                List<PetasosCapabilityDeliveryNode> ptceInstanceList = capabilityMap.get(currentCommonName);
                if (ptceInstanceList != null) {
                    if (ptceInstanceList.contains(engineRoutingEndpointName)) {
                        ptceInstanceList.remove(engineRoutingEndpointName);
                    }
                }
                if (ptceInstanceList.isEmpty()) {
                    capabilityMap.remove(currentCommonName);
                }
            }
            LOG.info(".removeCapabilityNodeInstance(): Exit, publisher removed added");
        }
        checkForCapabilityFulfillment(deliveryNode.getSupportedCapabilities());
    }

    public List<PetasosCapabilityDeliveryNodeRegistration> getDeliveryNodeRegistrationSet(PetasosCapabilityCommonName capabilityName){
        LOG.info(".getDeliveryNodeRegistrationSet(): Entry, capabilityName->{}", capabilityName);
        if(capabilityName == null){
            LOG.info(".getDeliveryNodeRegistrationSet(): Exit, capabilityName is empty/null");
            return(new ArrayList<>());
        }
        if(StringUtils.isEmpty(capabilityName.getDeliveredFunction()) && StringUtils.isEmpty(capabilityName.getDeliveredService())){
            LOG.info(".getDeliveryNodeRegistrationSet(): Exit, capabilityName is empty/null");
            return(new ArrayList<>());
        }
        List<PetasosCapabilityDeliveryNode> executorRoutingEndpointNameSet = capabilityMap.get(capabilityName);
        if(executorRoutingEndpointNameSet == null){
            LOG.info(".getDeliveryNodeRegistrationSet(): Exit, no executors (list) for provided capabilityName");
            return(new ArrayList<>());
        }
        if(executorRoutingEndpointNameSet.isEmpty()){
            LOG.info(".getDeliveryNodeRegistrationSet(): Exit, empty executors (list) for provided capabilityName");
            return(new ArrayList<>());
        }
        List<PetasosCapabilityDeliveryNodeRegistration> executorRegistrationList = new ArrayList<>();
        LOG.info(".getDeliveryNodeRegistrationSet(): Creating executorRegistrationList");
        for(PetasosCapabilityDeliveryNode currentNode: executorRoutingEndpointNameSet){
            LOG.info(".getDeliveryNodeRegistrationSet(): processing=>{}", currentNode);
            PetasosCapabilityDeliveryNodeRegistration executorRegistration = capabilityDeliveryNodeRegistrationMap.get(currentNode.getDeliveryNodeID());
            LOG.info(".getDeliveryNodeRegistrationSet(): Registration=>{}", executorRegistration);
            if(executorRegistration != null){
                LOG.info(".getDeliveryNodeRegistrationSet(): adding entry to the executorRegistrationList");
                executorRegistrationList.add(executorRegistration);
            }
        }
        LOG.info(".getDeliveryNodeRegistrationSet(): Exit, returning list");
        return(executorRegistrationList);
    }

    public boolean isCapabilityDeliveryNodeRegistered(String engineRoutingEndpointName){
        LOG.debug(".isCapabilityDeliveryNodeRegistered(): Entry, engineRoutingEndpointName->{}", engineRoutingEndpointName);
        if(StringUtils.isEmpty(engineRoutingEndpointName)){
            LOG.debug(".isCapabilityDeliveryNodeRegistered(): Exit, engineRoutingEndpointName is null, return -false-");
            return(false);
        }
        Collection<PetasosCapabilityDeliveryNodeRegistration> registrationSet = capabilityDeliveryNodeRegistrationMap.values();
        for(PetasosCapabilityDeliveryNodeRegistration currentRegistration: registrationSet){
            String currentEndpointName = currentRegistration.getDeliveryNode().getRoutingEndpointID().getEndpointName();
            if(currentEndpointName.contentEquals(engineRoutingEndpointName)){
                LOG.debug(".isCapabilityDeliveryNodeRegistered(): Exit, Engine registered for the specified routingEndpointName, return -true-");
                return(true);
            }
        }
        LOG.debug(".isCapabilityDeliveryNodeRegistered(): Exit, No execution engine registered for the specified routingEndpointName, return -false-");
        return(false);
    }

    public boolean isCapabilityDeliveryNodeAvailableForCapability(PetasosCapabilityCommonName commonName) {
        LOG.debug(".isTaskExecutionEngineAvailableToSupportCapability(): Entry, commonName->{}", commonName);
        if (commonName == null) {
            LOG.debug(".isTaskExecutionEngineAvailableToSupportCapability(): Exit, commonName is null, returning -false-");
            return (false);
        }
        if (StringUtils.isEmpty(commonName.getDeliveredService()) && StringUtils.isEmpty(commonName.getDeliveredFunction())) {
            LOG.debug(".isTaskExecutionEngineAvailableToSupportCapability(): Exit, commonName is empty, returning -false-");
            return (false);
        }
        List<PetasosCapabilityDeliveryNodeRegistration> engineServiceRegistrationSet = getDeliveryNodeRegistrationSet(commonName);
        if (engineServiceRegistrationSet.isEmpty()) {
            LOG.debug(".isTaskExecutionEngineAvailableToSupportCapability(): Exit, no engines are registered for the commonName, returning -false-");
            return (false);
        } else {
            LOG.debug(".isTaskExecutionEngineAvailableToSupportCapability(): Exit, at least 1 engine is registered for the commonName, returning -true-");
            return (true);
        }
    }

    //
    // Capability Fulfillment Status
    //

    public void addRequiredCapability(PetasosCapabilityCommonName commonName){
        if(commonName == null){
            return;
        }
        synchronized (this.capabilityFulfillmentStatusMapLock){
            if(!capabilityFulfillmentStatusMap.containsKey(commonName)){
                capabilityFulfillmentStatusMap.put(commonName, false);
            }
        }
    }

    public void setRequiredCapabilityFulfillmentStatus(PetasosCapabilityCommonName commonName, boolean status){
        if(commonName == null){
            return;
        }
        synchronized(this.capabilityFulfillmentStatusMapLock){
            if(capabilityFulfillmentStatusMap.containsKey(commonName)) {
                this.capabilityFulfillmentStatusMap.remove(commonName);
            }
            this.capabilityFulfillmentStatusMap.put(commonName, status);
        }
    }

    public void removeRequiredCapability(PetasosCapabilityCommonName commonName) {
        if (commonName == null) {
            return;
        }
        synchronized (this.capabilityFulfillmentStatusMapLock) {
            if (capabilityFulfillmentStatusMap.containsKey(commonName)) {
                capabilityFulfillmentStatusMap.remove(commonName);
            }
        }
    }

    public boolean isRequiredCapabilityFulfilled(PetasosCapabilityCommonName commonName){
        if(commonName == null){
            return(false);
        }
        boolean isFulfilled = false;
        synchronized (this.capabilityFulfillmentStatusMapLock){
            if(capabilityFulfillmentStatusMap.containsKey(commonName)){
                isFulfilled = capabilityFulfillmentStatusMap.get(commonName);
            }
        }
        return(isFulfilled);
    }

    //
    // Capability Requirement Map
    //

    public void addRequiredCapability(PetasosCapabilityCommonName commonName, TopologyNodeFDNToken requirementOrigin){
        if(commonName == null || requirementOrigin == null){
            return;
        }
        synchronized(capabilityRequirementMapLock){
            if(!capabilityRequirementMap.containsKey(commonName)){
                List<TopologyNodeFDNToken> newList = new ArrayList<>();
                capabilityRequirementMap.put(commonName, newList);
                addRequiredCapability(commonName);
            }
            List<TopologyNodeFDNToken> tokenList = capabilityRequirementMap.get(commonName);
            if(!tokenList.contains(requirementOrigin)){
                tokenList.add(requirementOrigin);
            }
        }
        checkForCapabilityFulfillment(commonName);
    }

    public void removeRequiredCapability(PetasosCapabilityCommonName commonName, TopologyNodeFDNToken requirementOrigin){
        if(commonName == null || requirementOrigin == null){
            return;
        }
        synchronized(capabilityRequirementMapLock){
            if(capabilityRequirementMap.containsKey(commonName)){
                List<TopologyNodeFDNToken> tokenList = capabilityRequirementMap.get(commonName);
                if(tokenList.contains(requirementOrigin)){
                    tokenList.remove(requirementOrigin);
                }
                if(tokenList.isEmpty()) {
                    capabilityRequirementMap.remove(commonName);
                    removeRequiredCapability(commonName);
                }
            }
        }
    }

    public void checkForCapabilityFulfillment(PetasosCapabilityCommonName commonName){
        if(commonName == null){
            return;
        }
        boolean isFulfilled = false;
        synchronized(capabilityMapLock){
            if(this.capabilityMap.containsKey(commonName)){
                List<PetasosCapabilityDeliveryNode> petasosCapabilityDeliveryNodes = this.capabilityMap.get(commonName);
                for(PetasosCapabilityDeliveryNode currentDeliveryNode: petasosCapabilityDeliveryNodes){
                    if(currentDeliveryNode.getCapabilityDeliveryNodeStatus().equals(PetasosCapabilityDeliveryNodeStatusEnum.FULFILLMENT_CAPABILITY_OPERATIONAL)){
                        isFulfilled = true;
                    }
                }
            }
        }
        setRequiredCapabilityFulfillmentStatus(commonName, isFulfilled);
    }

    public void checkForCapabilityFulfillment(List<PetasosCapabilityDefinition> capabilityList){
        if(capabilityList == null){
            return;
        }
        if(capabilityList.isEmpty()){
            return;
        }
        for(PetasosCapabilityDefinition currentDefinition: capabilityList){
            checkForCapabilityFulfillment(currentDefinition);
        }
    }
}
