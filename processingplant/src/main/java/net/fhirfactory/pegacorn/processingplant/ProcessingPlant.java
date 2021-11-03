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
package net.fhirfactory.pegacorn.processingplant;

import net.fhirfactory.pegacorn.core.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.ClusterServiceDeliverySubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.interfaces.SolutionNodeFactoryInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.util.PegacornEnvironmentProperties;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ProcessingPlant extends RouteBuilder implements ProcessingPlantInterface {

    private ProcessingPlantTopologyNode processingPlantNode;
    private String hostName;
    private String instanceQualifier;
    private boolean isInitialised;

    ConcurrentHashMap<String, CapabilityFulfillmentInterface> capabilityDeliveryServices;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private PegacornEnvironmentProperties environmentProperties;

    abstract protected ClusterServiceDeliverySubsystemPropertyFile specifyPropertyFile();
    abstract protected Logger specifyLogger();
    abstract protected PegacornTopologyFactoryInterface specifyTopologyFactory();
    abstract protected SolutionNodeFactoryInterface specifySolutionNodeFactory();
    abstract protected void executePostConstructActivities();

    protected Logger getLogger(){return(specifyLogger());}
    protected ClusterServiceDeliverySubsystemPropertyFile getPropertyFile(){return(specifyPropertyFile());}

    @Override
    public PegacornTopologyFactoryInterface getTopologyFactory(){
        return(specifyTopologyFactory());
    }

    public ProcessingPlant() {
        super();
        this.capabilityDeliveryServices = new ConcurrentHashMap<>();
        this.isInitialised = false;
        this.instanceQualifier = UUID.randomUUID().toString();

    }

    @PostConstruct
    public void initialise() {
        getLogger().debug(".initalise(): Entry");
        if (!isInitialised) {
            getLogger().info("ProcessingPlant::initialise(): Initialising....");
            getLogger().info("ProcessingPlant::initialise(): [TopologyIM Initialisation] Start");
            getTopologyIM().initialise();
            getLogger().info("ProcessingPlant::initialise(): [TopologyIM Initialisation] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Topology Factory Initialisation] Start");
            getTopologyFactory().initialise();
            getLogger().info("ProcessingPlant::initialise(): [Topology Factory Initialisation] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Solution Node Factory Initialisation] Start");
            specifySolutionNodeFactory().initialise();
            getLogger().info("ProcessingPlant::initialise(): [Solution Node Factory Initialisation] Finish");
            getLogger().info("ProcessingPlant::initialise(): [ProcessingPlant Resolution] Start");
            resolveProcessingPlant();
            getLogger().info("ProcessingPlant::initialise(): [ProcessingPlant Resolution] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Capaility Delivery Services Map Initialisation] Start");
            getLogger().info("ProcessingPlant::initialise(): [POD Name Resolution and Assignment] Start");
            String myPodName = environmentProperties.getMandatoryProperty("MY_POD_NAME");
            setHostName(myPodName);
            getLogger().info("ProcessingPlant::initialise(): [POD Name Resolution and Assignment] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Executing other PostConstruct Activities] Start");
            executePostConstructActivities();
            getLogger().info("ProcessingPlant::initialise(): [Executing other PostConstruct Activities] Finish");
            isInitialised = true;
            getLogger().info("StandardProcessingPlatform::initialise(): Done...");
        } else {
            getLogger().debug(".initialise(): Already initialised, nothing to do!");
        }
        getLogger().debug(".initialise(): Exit");
    }

    @Override
    public void initialisePlant() {
        initialise();
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public TopologyIM getTopologyIM() {
        return (topologyIM);
    }

    public TopologyNodeFunctionFDN getNodeToken() {
        return (this.processingPlantNode.getNodeFunctionFDN());
    }

    private void resolveProcessingPlant() {
        getLogger().debug(".resolveProcessingPlant(): Entry");
        String processingPlantName = getPropertyFile().getSubsystemInstant().getProcessingPlantName();
        String processingPlantVersion = getPropertyFile().getSubsystemInstant().getProcessingPlantVersion();
        getLogger().debug(".resolveProcessingPlant(): Getting ProcessingPlant->{}, version->{}", processingPlantName, processingPlantVersion);
        getLogger().trace(".resolveProcessingPlant(): Resolving list of available ProcessingPlants");
        List<SoftwareComponent> topologyNodes = topologyIM.nodeSearch(ComponentTypeTypeEnum.PROCESSING_PLANT, processingPlantName, processingPlantVersion);
        if(getLogger().isTraceEnabled()){
            if(topologyNodes == null){
                getLogger().trace(".resolveProcessingPlant(): nodeSearch return a null list");
            }
            if(topologyNodes.isEmpty()){
                getLogger().trace(".resolveProcessingPlant(): nodeSearch return an empty list");
            }
            if(topologyNodes.size() > 1){
                getLogger().trace(".resolveProcessingPlant(): nodeSearch return a list containing more than 1 entry!");
            }
        }
        getLogger().trace(".resolveProcessingPlant(): Matching to my Name/Version");
        if(topologyNodes.isEmpty() || topologyNodes.size() > 1){
            throw new RuntimeException("Unable to resolve ProcessingPlant");
        }
        this.processingPlantNode = (ProcessingPlantTopologyNode) topologyNodes.get(0);
        getLogger().debug(".resolveProcessingPlant(): Exit, Resolved ProcessingPlant, processingPlant->{}", processingPlantNode);
    }

    @Override
    public void configure() throws Exception {
        String processingPlantName = getFriendlyName();

        from("timer://"+processingPlantName+"?delay=1000&repeatCount=1")
            .routeId("ProcessingPlant::"+processingPlantName)
            .log(LoggingLevel.DEBUG, "Starting....");
    }

    private String getFriendlyName(){
        getLogger().debug(".getFriendlyName(): Entry");
        String nodeName = getProcessingPlantNode().getComponentRDN().getNodeName();
        String nodeVersion = getProcessingPlantNode().getComponentRDN().getNodeVersion();
        String friendlyName = nodeName + "(" + nodeVersion + ")";
        return(nodeName);
    }

    @Override
    public ProcessingPlantTopologyNode getProcessingPlantNode() {
        return (this.processingPlantNode);
    }

    public TopologyNodeFDN getProcessingPlantNodeFDN() {
        return (this.processingPlantNode.getComponentFDN());
    }

    @Override
    public WorkshopTopologyNode getWorkshop(String workshopName, String version) {
        getLogger().debug(".getWorkshop(): Entry, workshopName --> {}, version --> {}", workshopName, version);
        boolean found = false;
        WorkshopTopologyNode foundWorkshop = null;
        for (TopologyNodeFDN containedWorkshopFDN : this.processingPlantNode.getWorkshops()) {
            WorkshopTopologyNode containedWorkshop = (WorkshopTopologyNode)topologyIM.getNode(containedWorkshopFDN);
            TopologyNodeRDN testRDN = new TopologyNodeRDN(ComponentTypeTypeEnum.WORKSHOP, workshopName, version);
            if (testRDN.equals(containedWorkshop.getComponentRDN())) {
                found = true;
                foundWorkshop = containedWorkshop;
                break;
            }
        }
        if (found) {
            getLogger().debug(".getWorkshop(): Exit, workshop found!");
            return (foundWorkshop);
        }
        getLogger().debug(".getWorkshop(): Exit, workshop not found!");
        return (null);
    }

    public WorkshopTopologyNode getWorkshop(String workshopName){
        getLogger().debug(".getWorkshop(): Entry, workshopName --> {}", workshopName);
        String version = this.processingPlantNode.getComponentRDN().getNodeVersion();
        WorkshopTopologyNode workshop = getWorkshop(workshopName, version);
        getLogger().debug(".getWorkshop(): Exit");
        return(workshop);
    }

    @Override
    public String getSimpleFunctionName() {
        TopologyNodeRDN functionRDN = getProcessingPlantNode().getNodeFunctionFDN().extractRDNForNodeType(ComponentTypeTypeEnum.PROCESSING_PLANT);
        String functionName = functionRDN.getNodeName();
        return (functionName);
    }

    @Override
    public String getSimpleInstanceName() {
        String instanceName = getSimpleFunctionName() + "(" + instanceQualifier + ")";
        return (instanceName);
    }

    @Override
    public NetworkSecurityZoneEnum getNetworkZone(){
        NetworkSecurityZoneEnum securityZone = getProcessingPlantNode().getSecurityZone();
        return(securityZone);
    }

    @Override
    public String getIPCServiceName() {
        return (getProcessingPlantNode().getSubsystemName());
    }

    @Override
    public String getDeploymentSite() {
        TopologyNodeRDN siteRDN = getProcessingPlantNode().getComponentFDN().extractRDNForNodeType(ComponentTypeTypeEnum.SITE);
        String siteName = siteRDN.getNodeName();
        return (siteName);
    }


    @Override
    public void registerCapabilityFulfillmentService(String capabilityName, CapabilityFulfillmentInterface fulfillmentInterface) {
        getLogger().debug(".registerCapabilityFulfillmentService(): Entry, capabilityName->{}", capabilityName);
        if(fulfillmentInterface == null){
            getLogger().debug(".registerCapabilityFulfillmentService(): Exit, Capability Fulfillment Interface is NULL");
            return;
        }
        this.capabilityDeliveryServices.put(capabilityName, fulfillmentInterface);
        getLogger().debug(".registerCapabilityFulfillmentService(): Exit, Capability Fulillment Interface registered");
    }

    @Override
    public CapabilityUtilisationResponse executeTask(CapabilityUtilisationRequest request) {
        getLogger().debug(".executeTask(): Entry, request->{}", request);
        if(request == null){
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setDateCompleted(Instant.now());
            response.setSuccessful(false);
            getLogger().debug(".executeTask(): Exit, request is null, response->{}", response);
            return(response);
        }
        String capabilityName = request.getRequiredCapabilityName();
        CapabilityFulfillmentInterface interfaceToUse = this.capabilityDeliveryServices.get(capabilityName);
        if(interfaceToUse == null){
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setDateCompleted(Instant.now());
            response.setSuccessful(false);
            response.setInScope(false);
            getLogger().debug(".executeTask(): Exit, not registered capability, response->{}", response);
            return(response);
        }
        CapabilityUtilisationResponse capabilityUtilisationResponse = interfaceToUse.executeTask(request);
        getLogger().debug(".executeTask(): Exit, capabilityUtilisationResponse->{}", capabilityUtilisationResponse);
        return(capabilityUtilisationResponse);
    }

    @Override
    public boolean isITOpsNode() {
        return (false);
    }
}
