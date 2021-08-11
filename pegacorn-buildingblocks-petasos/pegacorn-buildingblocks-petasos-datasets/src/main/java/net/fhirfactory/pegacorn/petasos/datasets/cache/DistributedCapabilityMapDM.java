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
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNode;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNodeRegistration;
import net.fhirfactory.pegacorn.core.model.tasks.base.PetasosCapabilityCommonName;
import net.fhirfactory.pegacorn.core.model.tasks.base.PetasosCapabilityDefinition;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNodeSet;
import net.fhirfactory.pegacorn.core.model.tasks.valuesets.PetasosCapabilityDeliveryNodeRegistrationStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class DistributedCapabilityMapDM {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedCapabilityMapDM.class);

    // ConcurrentHashMap<PetasosCapabilityDeliveryNode.getDeliveryNodeID(), PetasosCapabilityDeliveryNode.getEndpointServiceName()>
    // Maintains a map of the actual task completion engine and the routing endpoint (service) via which it may be contacted
    private ConcurrentHashMap<TopologyNodeFDNToken, String> capabilityDeliveryNodeRoutingMap;
    private Object capabilityDeliveryNodeRoutingMapLock;

    // ConcurrentHashMap<TopologyNodeFDNToken, PetasosCapabilityDeliveryNodeRegistration>
    // Maintains a map of the task completion engine and its registration within the local cache
    private ConcurrentHashMap<TopologyNodeFDNToken, PetasosCapabilityDeliveryNodeRegistration> capabilityDeliveryNodeRegistrationMap;
    private Object capabilityDeliveryNodeRegistrationMapLock;

    // ConcurrentHashMap<PetasosCapabilityCommonName, List<PetasosCapabilityDeliveryNode>>
    // Maintains a map of the (common name of) capabilities and the task completion engines that can fulfill them
    private ConcurrentHashMap<PetasosCapabilityCommonName, List<PetasosCapabilityDeliveryNode>> capabilityMap;
    private Object capabilityMapLock;

    public DistributedCapabilityMapDM(){
        this.capabilityDeliveryNodeRoutingMap = new ConcurrentHashMap<>();
        this.capabilityDeliveryNodeRegistrationMap = new ConcurrentHashMap<>();
        this.capabilityMap = new ConcurrentHashMap<>();
        this.capabilityDeliveryNodeRoutingMapLock = new Object();
        this.capabilityDeliveryNodeRegistrationMapLock = new Object();
        this.capabilityMapLock = new Object();
    }

    //
    // Routing Map
    //

    public void addTopologyNodeRoute(PetasosCapabilityDeliveryNode node){
        if(node == null){
            return;
        }
        if(node.getRoutingEndpointID() == null){
            return;
        }
        String nodeEndpointRouteServiceName = node.getRoutingEndpointID().getEndpointServiceName();
        TopologyNodeFDNToken nodeFDNToken = node.getDeliveryNodeID();
        if(StringUtils.isEmpty(nodeEndpointRouteServiceName) || nodeFDNToken == null){
            return;
        }
        synchronized(capabilityDeliveryNodeRoutingMapLock) {
            if (capabilityDeliveryNodeRoutingMap.containsKey(nodeFDNToken)) {
                capabilityDeliveryNodeRoutingMap.remove(nodeFDNToken);
            }
            capabilityDeliveryNodeRoutingMap.put(nodeFDNToken, nodeEndpointRouteServiceName);
        }
    }

    public void removeTopologyNodeRoute(PetasosCapabilityDeliveryNode node){
        if(node == null){
            return;
        }
        TopologyNodeFDNToken nodeFDNToken = node.getDeliveryNodeID();
        if(nodeFDNToken == null){
            return;
        }
        synchronized(capabilityDeliveryNodeRoutingMapLock) {
            if (capabilityDeliveryNodeRoutingMap.containsKey(nodeFDNToken)) {
                capabilityDeliveryNodeRoutingMap.remove(nodeFDNToken);
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
        registration.setRegistrationStatus(PetasosCapabilityDeliveryNodeRegistrationStatusEnum.REGISTRATION_STATUS_DISCOVERED);
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
}
