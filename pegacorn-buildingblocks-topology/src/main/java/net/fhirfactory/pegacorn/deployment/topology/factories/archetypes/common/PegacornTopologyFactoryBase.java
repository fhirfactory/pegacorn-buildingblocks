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
package net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.core.model.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.core.model.topology.nodes.*;
import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.names.sites.SiteKeyNames;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.BaseSubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.standard.HTTPProcessingPlantServerPortSegment;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.interfaces.SolutionNodeFactoryInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.core.model.topology.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.technologies.HTTPProcessingPlantTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public abstract class PegacornTopologyFactoryBase implements PegacornTopologyFactoryInterface {

    private static final String PROPERTY_FILENAME_EXTENSION = ".yaml";

    private BaseSubsystemPropertyFile propertyFile;
    private ProcessingPlantTopologyNode processingPlantNode;
    private boolean initialised;

    @Inject
    private SiteKeyNames siteKeyNames;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private SolutionNodeFactoryInterface solutionNodeInterface;

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    //
    // Default Constructor
    //

    public PegacornTopologyFactoryBase(){
        this.initialised = false;
    }

    //
    // Abstract Methods
    //

    abstract protected Logger specifyLogger();
    abstract protected String specifyPropertyFileName();
    abstract protected Class specifyPropertyFileClass();
    abstract protected ProcessingPlantTopologyNode buildSubsystemTopology();

    //
    // PostConstruct function
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(!initialised) {
            getLogger().debug(".initialise(): Loading file!");
            this.solutionNodeInterface.initialise();
            this.propertyFile = readPropertyFile();
            this.processingPlantNode = buildSubsystemTopology();
            this.initialised = true;
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(specifyLogger());
    }

    public BaseSubsystemPropertyFile getPropertyFile() {
        return (this.propertyFile);
    }

    public TopologyIM getTopologyIM() {
        return topologyIM;
    }

    protected SiteKeyNames getSiteKeyNames(){
        return(siteKeyNames);
    }

    public SolutionNodeFactoryInterface getSolutionNodeInterface() {
        return solutionNodeInterface;
    }

    protected PegacornCommonInterfaceNames getInterfaceNames(){
        return(interfaceNames);
    }

    //
    // Node Builders
    //

    @Override
    public TopologyNodeRDN createNodeRDN(String nodeName, String nodeVersion, TopologyNodeTypeEnum nodeType){
        getLogger().debug(".createNodeRDN: Entry, nodeName->{}, nodeVersion->{}, nodeType->{}", nodeName, nodeVersion, nodeType);
        TopologyNodeRDN newRDN = new TopologyNodeRDN(nodeType, nodeName, nodeVersion);
        getLogger().debug(".createNodeRDN: Exit, newRDN->{}", newRDN);
        return (newRDN);
    }

    /**
     * Subsystem Node Builder
     *
     * @param solution
     * @return
     */

    public SubsystemTopologyNode addSubsystemNode(SolutionTopologyNode solution){
        getLogger().debug(".addSubsystemNode(): Entry, solution->{}", solution);
        SubsystemTopologyNode subsystem = new SubsystemTopologyNode();
        getLogger().trace(".addSubsystemNode(): Create the Subsystem RDN (createNodeRDN()) --> Start");
        TopologyNodeRDN nodeRDN = createNodeRDN(getPropertyFile().getSubsystemInstant().getSubsystemName(), getPropertyFile().getSubsystemInstant().getSubsystemVersion(), TopologyNodeTypeEnum.SUBSYSTEM);
        getLogger().trace(".addSubsystemNode(): Create the Subsystem RDN (createNodeRDN()) --> Finish, nodeRDN->{}", nodeRDN);
        getLogger().trace(".addSubsystemNode(): Create the Subsystem FDN (constructFDN()) --> Start");
        subsystem.constructFDN(solution.getNodeFDN(), nodeRDN);
        getLogger().trace(".addSubsystemNode(): Create the Subsystem FDN (constructFDN()) --> Finish, nodeFDN->{}", subsystem.getNodeFDN());
        getLogger().trace(".addSubsystemNode(): Create the Subsystem Function FDN (constructFunctionFDN()) --> Start");
        subsystem.constructFunctionFDN(solution.getNodeFunctionFDN(), nodeRDN);
        getLogger().trace(".addSubsystemNode(): Create the Subsystem FDN (constructFunctionFDN()) --> Finish, nodeFunctionFDN->{}", subsystem.getNodeFunctionFDN());
        subsystem.setNodeRDN(nodeRDN);
        getLogger().trace(".addSubsystemNode(): Set the Subsystem Concurrency Mode");
        subsystem.setConcurrencyMode(getConcurrenceMode());
        getLogger().trace(".addSubsystemNode(): Set the Subsystem Resilience Mode");
        subsystem.setResilienceMode(getResilienceMode());
        getLogger().trace(".addSubsystemNode(): Set the Subsystem Site Count");
        subsystem.setSiteCount(getPropertyFile().getDeploymentSites().getSiteCount());
        getLogger().trace(".addSubsystemNode(): Set the Subsystem's Parent Solution");
        subsystem.setContainingNodeFDN(solution.getNodeFDN());
        getLogger().trace(".addSubsystemNode(): Add the subsystem to the Solution subsystem list");
        solution.getSubsystemList().add(subsystem.getNodeFDN());
        subsystem.setComponentType(TopologyNodeTypeEnum.SUBSYSTEM);
        getLogger().trace(".addSubsystemNode(): Add the subsystem to the Topology Cache");
        getTopologyIM().addTopologyNode(solution.getNodeFDN(), subsystem);
        getLogger().debug(".addSubsystemNode(): Exit");
        return(subsystem);
    }

    /**
     * Business Service Node Set Builder
     *
     * @param subsystem
     * @return
     */

    public BusinessServiceTopologyNode addBusinessServiceNode( SubsystemTopologyNode subsystem){
        getLogger().debug(".addBusinessServiceNode(): Entry");
        BusinessServiceTopologyNode businessService = new BusinessServiceTopologyNode();
        getLogger().trace(".addBusinessServiceNode(): Create the Business Service RDN (createNodeRDN()) --> Start");
        TopologyNodeRDN nodeRDN = createNodeRDN(getPropertyFile().getSubsystemInstant().getExternalisedServiceName(), getPropertyFile().getSubsystemInstant().getSubsystemVersion(), TopologyNodeTypeEnum.EXTERNALISED_SERVICE);
        getLogger().trace(".addBusinessServiceNode(): Create the Business Service RDN (createNodeRDN()) --> Finish, nodeRDN->{}", nodeRDN);
        getLogger().trace(".addBusinessServiceNode(): Create the Business Service FDN (constructFDN()) --> Start");
        businessService.constructFDN(subsystem.getNodeFDN(),nodeRDN);
        getLogger().trace(".addBusinessServiceNode(): Create the Business Service FDN (constructFDN()) --> Finish, nodeFDN->{}", businessService.getNodeFDN());
        getLogger().trace(".addBusinessServiceNode(): Create the Business Service Function FDN (constructFunctionFDN()) --> Start");
        businessService.constructFunctionFDN(subsystem.getNodeFunctionFDN(),nodeRDN);
        getLogger().trace(".addBusinessServiceNode(): Create the Business Service FDN (constructFunctionFDN()) --> Finish, nodeFunctionFDN->{}", businessService.getNodeFunctionFDN());
        businessService.setNodeRDN(nodeRDN);
        businessService.setComponentType(TopologyNodeTypeEnum.EXTERNALISED_SERVICE);
        businessService.setContainingNodeFDN(subsystem.getNodeFDN());
        subsystem.getBusinessServices().add(businessService.getNodeFDN());
        getLogger().trace(".addBusinessServiceNode(): Add the BusinessService to the Topology Cache");
        getTopologyIM().addTopologyNode(subsystem.getNodeFDN(), businessService);
        getLogger().debug(".addBusinessServiceNode(): Exit");
        return(businessService);
    }

    /**
     * Deployment Site Node Builder
     *
     * @param businessService
     * @return
     */
    public DeploymentSiteTopologyNode addDeploymentSiteNode( BusinessServiceTopologyNode businessService){
        getLogger().debug(".addDeploymentSiteNode(): Entry, businessService->{}", businessService);
        DeploymentSiteTopologyNode site = new DeploymentSiteTopologyNode();
        String siteName = getPropertyFile().getDeploymentSites().getSite1Name();
        TopologyNodeFDN businessServiceFDN = businessService.getNodeFDN();
        TopologyNodeFunctionFDN businessServiceFunctionFDN = businessService.getNodeFunctionFDN();
        getLogger().trace(".addDeploymentSiteNode(): Create the Deployment Site RDN (createNodeRDN()) --> Start");
        TopologyNodeRDN nodeRDN = createNodeRDN(getPropertyFile().getDeploymentSites().getSite1Name(), getPropertyFile().getSubsystemInstant().getSubsystemVersion(), TopologyNodeTypeEnum.SITE);
        getLogger().trace(".addDeploymentSiteNode(): Create the Deployment Site RDN (createNodeRDN()) --> Finish, nodeRDN->{}", nodeRDN);
        getLogger().trace(".addDeploymentSiteNode(): Create the Deployment Site FDN (constructFDN()) --> Start, businessServiceFDN->{}, nodeRDN->{}", businessServiceFDN, nodeRDN);
        site.constructFDN(businessServiceFDN, nodeRDN);
        getLogger().trace(".addDeploymentSiteNode(): Create the Deployment Site FDN (constructFDN()) --> Finish, nodeFDN->{}", businessServiceFDN);
        getLogger().trace(".addDeploymentSiteNode(): Create the Deployment Site Function FDN (constructFunctionFDN()) --> Start, businessServiceFunctionFDN->{}, nodeRDN->{}", businessServiceFunctionFDN, nodeRDN);
        site.constructFunctionFDN(businessServiceFunctionFDN, nodeRDN);
        getLogger().trace(".addDeploymentSiteNode(): Create the Deployment Site FDN (constructFunctionFDN()) --> Finish, nodeFunctionFDN->{}", businessServiceFunctionFDN);
        site.setNodeRDN(nodeRDN);
        site.setComponentType(TopologyNodeTypeEnum.SITE);
        site.setInstanceCount(getPropertyFile().getDeploymentSites().getSiteCount());
        site.setContainingNodeFDN(businessService.getNodeFDN());
        businessService.getDeploymentSites().add(site.getNodeFDN());
        getLogger().trace(".addDeploymentSiteNode(): Add the DeploymentSite to the Topology Cache");
        getTopologyIM().addTopologyNode(businessService.getNodeFDN(), site);
        getLogger().debug(".addDeploymentSiteNode(): Exit");
        return(site);
    }

    /**
     * Build Cluster Service Node
     *
     * @param site
     * @return
     */
    public ClusterServiceTopologyNode addClusterServiceNode( DeploymentSiteTopologyNode site){
        getLogger().debug(".addClusterServiceNode(): Entry");
        ClusterServiceTopologyNode clusterService = new ClusterServiceTopologyNode();
        TopologyNodeRDN nodeRDN = createNodeRDN(getPropertyFile().getSubsystemInstant().getClusterServiceName(), getPropertyFile().getSubsystemInstant().getProcessingPlantVersion(), TopologyNodeTypeEnum.CLUSTER_SERVICE);
        clusterService.constructFDN(site.getNodeFDN(),nodeRDN);
        clusterService.constructFunctionFDN(site.getNodeFunctionFDN(),nodeRDN);
        clusterService.setNodeRDN(nodeRDN);
        clusterService.setComponentType(TopologyNodeTypeEnum.CLUSTER_SERVICE);
        clusterService.setResilienceMode(getResilienceMode());
        clusterService.setConcurrencyMode(getConcurrenceMode());
        clusterService.setDefaultDNSName(getPropertyFile().getSubsystemInstant().getClusterServiceDNSName());
        clusterService.setInternalTrafficEncrypted(getPropertyFile().getDeploymentMode().isUsingInternalEncryption());
        clusterService.setContainingNodeFDN(site.getNodeFDN());
        site.getClusterServices().add(clusterService.getNodeFDN());
        getLogger().trace(".addClusterServiceNode(): Add the ClusterService to the Topology Cache");
        getTopologyIM().addTopologyNode(site.getNodeFDN(), clusterService);
        getLogger().debug(".addClusterServiceNode(): Exit");
        return(clusterService);
    }

    /**
     * Platform Node Set Builder
     *
     * @param clusterService
     * @return
     */
    public PlatformTopologyNode addPlatformNode(ClusterServiceTopologyNode clusterService){
        getLogger().debug(".addPlatformNode(): Entry");
        PlatformTopologyNode node = new PlatformTopologyNode();
        String nodeName = "PlatformNode0"; // TODO Fix This --> lookup node namde for kubernets or do a reverse lookup DNS
        TopologyNodeRDN nodeRDN = createNodeRDN(nodeName, getPropertyFile().getSubsystemInstant().getProcessingPlantVersion(), TopologyNodeTypeEnum.PLATFORM);
        node.constructFDN(clusterService.getNodeFDN(), nodeRDN);
        node.setComponentType(TopologyNodeTypeEnum.PLATFORM);
        node.constructFunctionFDN(clusterService.getNodeFunctionFDN(), nodeRDN);
        node.setNodeRDN(nodeRDN);
        node.setInstanceCount(getPropertyFile().getDeploymentMode().getProcessingPlantReplicationCount());
        node.setContainingNodeFDN(clusterService.getNodeFDN());
        clusterService.getPlatformNodes().add(node.getNodeFDN());
        getLogger().trace(".addPlatformNode(): Add the PlatformNode to the Topology Cache");
        getTopologyIM().addTopologyNode(clusterService.getNodeFDN(), node);
        getLogger().debug(".addPlatformNode(): Exit");
        return(node);
    }

    /**
     *
     * @param node
     * @return
     */
    public ProcessingPlantTopologyNode addPegacornProcessingPlant( PlatformTopologyNode node){
        getLogger().debug(".addPegacornProcessingPlant(): Entry");
        ProcessingPlantTopologyNode processingPlant = new ProcessingPlantTopologyNode();
        String name = getPropertyFile().getSubsystemInstant().getProcessingPlantName();
        String version = getPropertyFile().getSubsystemInstant().getProcessingPlantVersion();
        TopologyNodeRDN nodeRDN = createNodeRDN(name, version, TopologyNodeTypeEnum.PROCESSING_PLANT);
        processingPlant.setNodeRDN(nodeRDN);
        processingPlant.constructFDN(node.getNodeFDN(), nodeRDN);
        processingPlant.constructFunctionFDN(node.getNodeFunctionFDN(), nodeRDN);
        processingPlant.setResilienceMode(getResilienceMode());
        processingPlant.setConcurrencyMode(getConcurrenceMode());
        processingPlant.setComponentType(TopologyNodeTypeEnum.PROCESSING_PLANT);
        processingPlant.setNameSpace(getPropertyFile().getDeploymentZone().getNameSpace());
        processingPlant.setInterZoneIPCStackConfigFile(getPropertyFile().getDeploymentMode().getInterNetworkIPCStackConfigFile());
        processingPlant.setInterZoneOAMStackConfigFile(getPropertyFile().getDeploymentMode().getInterNetworkOAMStackConfigFile());
        processingPlant.setIntraZoneIPCStackConfigFile(getPropertyFile().getDeploymentMode().getIntraNetworkIPCStackConfigFile());
        processingPlant.setIntraZoneOAMStackConfigFile(getPropertyFile().getDeploymentMode().getIntraNetworkOAMStackConfigFile());
        processingPlant.setDefaultDNSName(getPropertyFile().getSubsystemInstant().getProcessingPlantDNSName());
        processingPlant.setInternalTrafficEncrypted(getPropertyFile().getDeploymentMode().isUsingInternalEncryption());
        processingPlant.setInstanceCount(getPropertyFile().getDeploymentMode().getProcessingPlantReplicationCount());
        processingPlant.setContainingNodeFDN(node.getNodeFDN());
        processingPlant.setSecurityZone(NetworkSecurityZoneEnum.fromSecurityZoneString(getPropertyFile().getDeploymentZone().getSecurityZoneName()));
        node.getProcessingPlants().add(processingPlant.getNodeFDN());
        getLogger().trace(".addPegacornProcessingPlant(): Add the ProcessingPlant to the Topology Cache");
        getTopologyIM().addTopologyNode(node.getNodeFDN(), processingPlant);
        getLogger().debug(".addPegacornProcessingPlant(): Exit");
        return(processingPlant);
    }

    /**
     *
     * @param name
     * @param version
     * @param processingPlant
     */
    @Override
    public WorkshopTopologyNode createWorkshop(String name, String version, ProcessingPlantTopologyNode processingPlant, TopologyNodeTypeEnum nodeType){
        getLogger().debug(".addWorkshop(): Entry");
        WorkshopTopologyNode workshop = new WorkshopTopologyNode();
        TopologyNodeRDN nodeRDN = createNodeRDN(name, version,nodeType);
        workshop.setNodeRDN(nodeRDN);
        workshop.constructFDN(processingPlant.getNodeFDN(), nodeRDN);
        workshop.constructFunctionFDN(processingPlant.getNodeFunctionFDN(), nodeRDN);
        workshop.setComponentType(nodeType);
        workshop.setResilienceMode(getResilienceMode());
        workshop.setConcurrencyMode(getConcurrenceMode());
        workshop.setContainingNodeFDN(processingPlant.getNodeFDN());
        processingPlant.getWorkshops().add(workshop.getNodeFDN());
        getLogger().trace(".addWorkshop(): Add the Workshop to the Topology Cache");
        getTopologyIM().addTopologyNode(processingPlant.getNodeFDN(), workshop);
        getLogger().debug(".addWorkshop(): Exit");
        return(workshop);
    }

    /**
     *
     * @param name
     * @param version
     * @param workshop
     */
    @Override
    public WorkUnitProcessorTopologyNode createWorkUnitProcessor(String name, String version, WorkshopTopologyNode workshop, TopologyNodeTypeEnum nodeType){
        getLogger().debug(".addWorkUnitProcessor(): Entry, name->{}, version->{}", name, version);
        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(version)){
            getLogger().error(".createWorkUnitProcessor(): name or version are emtpy!!!!");
        }
        WorkUnitProcessorTopologyNode wup = new WorkUnitProcessorTopologyNode();
        TopologyNodeRDN nodeRDN = createNodeRDN(name, version, nodeType);
        wup.setNodeRDN(nodeRDN);
        wup.constructFDN(workshop.getNodeFDN(), nodeRDN);
        wup.constructFunctionFDN(workshop.getNodeFunctionFDN(), nodeRDN);
        wup.setContainingNodeFDN(workshop.getNodeFDN());
        wup.setComponentType(nodeType);
        wup.setConcurrencyMode(getConcurrenceMode());
        wup.setResilienceMode(getResilienceMode());
        workshop.getWupSet().add(wup.getNodeFDN());
        getLogger().trace(".addWorkUnitProcessor(): Add the WorkUnitProcessor to the Topology Cache");
        getTopologyIM().addTopologyNode(workshop.getNodeFDN(), wup);
        getLogger().debug(".addWorkUnitProcessor(): Exit");
        return(wup);
    }

    /**
     *
     * @param name
     * @param topologyType
     * @param wup
     */
    @Override
    public WorkUnitProcessorComponentTopologyNode createWorkUnitProcessorComponent(String name, TopologyNodeTypeEnum topologyType, WorkUnitProcessorTopologyNode wup){
        getLogger().debug(".addWorkUnitProcessorComponent(): Entry");
        WorkUnitProcessorComponentTopologyNode wupComponent = new WorkUnitProcessorComponentTopologyNode();
        TopologyNodeRDN nodeRDN = createNodeRDN(name, wup.getNodeRDN().getNodeVersion(), topologyType);
        wupComponent.constructFDN(wup.getNodeFDN(), nodeRDN);
        wupComponent.constructFunctionFDN(wup.getNodeFunctionFDN(), nodeRDN);
        wupComponent.setNodeRDN(nodeRDN);
        wupComponent.setComponentType(topologyType);
        wupComponent.setContainingNodeFDN(wup.getNodeFDN());
        wupComponent.setConcurrencyMode(getConcurrenceMode());
        wupComponent.setResilienceMode(getResilienceMode());
        wup.getWupComponents().add(wupComponent.getNodeFDN());
        getLogger().trace(".addWorkUnitProcessorComponent(): Add the WorkUnitProcessor Component to the Topology Cache");
        getTopologyIM().addTopologyNode(wup.getNodeFDN(), wupComponent);
        getLogger().debug(".addWorkUnitProcessorComponent(): Exit");
        return(wupComponent);
    }

    /**
     *
     * @param name
     * @param topologyNodeType
     * @param wup
     */
    @Override
    public WorkUnitProcessorInterchangeComponentTopologyNode createWorkUnitProcessingInterchangeComponent(String name, TopologyNodeTypeEnum topologyNodeType, WorkUnitProcessorTopologyNode wup){
        getLogger().debug(".addWorkUnitProcessingInterchangeComponent(): Entry");
        WorkUnitProcessorInterchangeComponentTopologyNode wupInterchangeComponent = new WorkUnitProcessorInterchangeComponentTopologyNode();
        TopologyNodeRDN nodeRDN = createNodeRDN(name, wup.getNodeRDN().getNodeVersion(), topologyNodeType);
        wupInterchangeComponent.constructFDN(wup.getNodeFDN(), nodeRDN);
        wupInterchangeComponent.constructFunctionFDN(wup.getNodeFunctionFDN(), nodeRDN);
        wupInterchangeComponent.setNodeRDN(nodeRDN);
        wupInterchangeComponent.setComponentType(topologyNodeType);
        wupInterchangeComponent.setContainingNodeFDN(wup.getNodeFDN());
        wupInterchangeComponent.setConcurrencyMode(getConcurrenceMode());
        wupInterchangeComponent.setResilienceMode(getResilienceMode());
        wup.getWupInterchangeComponents().add(wupInterchangeComponent.getNodeFDN());
        getLogger().trace(".addWorkUnitProcessorComponent(): Add the WorkUnitProcessor Interchange Component to the Topology Cache");
        getTopologyIM().addTopologyNode(wup.getNodeFDN(), wupInterchangeComponent);
        getLogger().debug(".addWorkUnitProcessingInterchangeComponent(): Exit");
        return(wupInterchangeComponent);
    }


    /**
     *
     * @return
     */

    @Override
    public Boolean getSubsystemInternalTrafficEncrypt(){
        getLogger().debug(".getSubsystemInternalTrafficEncrypt(): Entry");
        Boolean encryptTraffic = getPropertyFile().getDeploymentMode().isUsingInternalEncryption();
        getLogger().debug(".getSubsystemInternalTrafficEncrypt(): Exit, Encrypt Internal Traffic? --> {}", encryptTraffic);
        return(encryptTraffic);
    }



    //
    // Build Prometheus Port (if there)
    //

    protected void addPrometheusPort( ProcessingPlantTopologyNode processingPlant){
        getLogger().debug(".addPrometheusPort(): Entry, processingPlant->{}", processingPlant);
        HTTPProcessingPlantTopologyEndpointPort prometheusPort = new HTTPProcessingPlantTopologyEndpointPort();
        HTTPProcessingPlantServerPortSegment port = getPropertyFile().getPrometheusPort();
        if(port == null){
            getLogger().debug(".addPrometheusPort(): Exit, no port to add");
            return;
        }
        prometheusPort.setEncrypted(getPropertyFile().getDeploymentMode().isUsingInternalEncryption());
        String name = interfaceNames.getEndpointServerName(interfaceNames.getFunctionNamePrometheus());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, processingPlant.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        prometheusPort.setNodeRDN(nodeRDN);
        prometheusPort.setName(interfaceNames.getFunctionNamePrometheus());
        prometheusPort.constructFDN(processingPlant.getNodeFDN(), nodeRDN);
        prometheusPort.setPortType(port.getPortType());
        prometheusPort.setEndpointType(PetasosTopologyEndpointTypeEnum.HTTP_API_SERVER);
        prometheusPort.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        prometheusPort.setPortValue(port.getPortValue());
        prometheusPort.constructFunctionFDN(processingPlant.getNodeFunctionFDN(), nodeRDN );
        prometheusPort.setBasePath(port.getWebServicePath());
        prometheusPort.setaServer(true);
        prometheusPort.setContainingNodeFDN(processingPlant.getNodeFDN());
        processingPlant.getEndpoints().add(prometheusPort.getNodeFDN());
        getLogger().trace(".addPrometheusPort(): Add the Prometheus Port to the Topology Cache");
        getTopologyIM().addTopologyNode(processingPlant.getNodeFDN(), prometheusPort);
        getLogger().debug(".addPrometheusPort(): Exit, endpoint added");
    }

    //
    // Build Jolokia Port (if there)
    //

    protected void addJolokiaPort(ProcessingPlantTopologyNode processingPlant){
        getLogger().debug(".addJolokiaPort(): Entry, processingPlant->{}", processingPlant);
        HTTPProcessingPlantTopologyEndpointPort jolokiaPort = new HTTPProcessingPlantTopologyEndpointPort();
        HTTPProcessingPlantServerPortSegment port = getPropertyFile().getJolokiaPort();
        if(port == null){
            getLogger().debug(".addJolokiaPort(): Exit, no port to add");
            return;
        }
        jolokiaPort.setEncrypted(getPropertyFile().getDeploymentMode().isUsingInternalEncryption());
        String name = interfaceNames.getEndpointServerName(interfaceNames.getFunctionNameJolokia());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, processingPlant.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        jolokiaPort.setNodeRDN(nodeRDN);
        jolokiaPort.setName(interfaceNames.getFunctionNameJolokia());
        jolokiaPort.constructFDN(processingPlant.getNodeFDN(), nodeRDN);
        jolokiaPort.setPortType(port.getPortType());
        jolokiaPort.setEndpointType(PetasosTopologyEndpointTypeEnum.HTTP_API_SERVER);
        jolokiaPort.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        jolokiaPort.setPortValue(port.getPortValue());
        jolokiaPort.constructFunctionFDN(processingPlant.getNodeFunctionFDN(), nodeRDN );
        jolokiaPort.setBasePath(port.getWebServicePath());
        jolokiaPort.setaServer(true);
        jolokiaPort.setContainingNodeFDN(processingPlant.getNodeFDN());
        processingPlant.getEndpoints().add(jolokiaPort.getNodeFDN());
        getLogger().trace(".addJolokiaPort(): Add the Jolokia Port to the Topology Cache");
        getTopologyIM().addTopologyNode(processingPlant.getNodeFDN(), jolokiaPort);
        getLogger().debug(".addJolokiaPort(): Exit, endpoint added");
    }

    //
    // Build KubeLiveliness Port (if there)
    //

    protected void addKubeLivelinessPort(ProcessingPlantTopologyNode processingPlant){
        getLogger().debug(".addKubeLivelinessPort(): Entry, processingPlant->{}", processingPlant);
        HTTPProcessingPlantTopologyEndpointPort kubeLivelinessPort = new HTTPProcessingPlantTopologyEndpointPort();
        HTTPProcessingPlantServerPortSegment port = getPropertyFile().getKubeLivelinessProbe();
        if(port == null){
            getLogger().debug(".addKubeLivelinessPort(): Exit, no port to add");
            return;
        }
        kubeLivelinessPort.setEncrypted(getPropertyFile().getDeploymentMode().isUsingInternalEncryption());
        String name = interfaceNames.getEndpointServerName(interfaceNames.getFunctionNameKubeLiveliness());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, processingPlant.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        kubeLivelinessPort.setNodeRDN(nodeRDN);
        kubeLivelinessPort.setName(interfaceNames.getFunctionNameKubeLiveliness());
        kubeLivelinessPort.constructFDN(processingPlant.getNodeFDN(), nodeRDN);
        kubeLivelinessPort.setPortType(port.getPortType());
        kubeLivelinessPort.setEndpointType(PetasosTopologyEndpointTypeEnum.HTTP_API_SERVER);
        kubeLivelinessPort.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        kubeLivelinessPort.setPortValue(port.getPortValue());
        kubeLivelinessPort.constructFunctionFDN(processingPlant.getNodeFunctionFDN(), nodeRDN );
        kubeLivelinessPort.setBasePath(port.getWebServicePath());
        kubeLivelinessPort.setaServer(true);
        kubeLivelinessPort.setContainingNodeFDN(processingPlant.getNodeFDN());
        processingPlant.getEndpoints().add(kubeLivelinessPort.getNodeFDN());
        getLogger().trace(".addKubeLivelinessPort(): Add the KubeLivelinessPort Port to the Topology Cache");
        getTopologyIM().addTopologyNode(processingPlant.getNodeFDN(), kubeLivelinessPort);
        getLogger().debug(".addKubeLivelinessPort(): Exit, endpoint added");
    }

    //
    // Build KubeReadiness Port (if there)
    //

    protected void addKubeReadinessPort( ProcessingPlantTopologyNode processingPlant){
        getLogger().debug(".addKubeReadinessPort(): Entry, processingPlant->{}", processingPlant);
        HTTPProcessingPlantTopologyEndpointPort kubeReadinessPort = new HTTPProcessingPlantTopologyEndpointPort();
        HTTPProcessingPlantServerPortSegment port = getPropertyFile().getKubeReadinessProbe();
        if(port == null){
            getLogger().debug(".addKubeReadinessPort(): Exit, no port to add");
            return;
        }
        kubeReadinessPort.setEncrypted(getPropertyFile().getDeploymentMode().isUsingInternalEncryption());
        String name = interfaceNames.getEndpointServerName(interfaceNames.getFunctionNameKubeReadiness());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, processingPlant.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        kubeReadinessPort.setNodeRDN(nodeRDN);
        kubeReadinessPort.setName(interfaceNames.getFunctionNameKubeReadiness());
        kubeReadinessPort.constructFDN(processingPlant.getNodeFDN(), nodeRDN);
        kubeReadinessPort.setPortType(port.getPortType());
        kubeReadinessPort.setEndpointType(PetasosTopologyEndpointTypeEnum.HTTP_API_SERVER);
        kubeReadinessPort.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        kubeReadinessPort.setPortValue(port.getPortValue());
        kubeReadinessPort.constructFunctionFDN(processingPlant.getNodeFunctionFDN(), nodeRDN );
        kubeReadinessPort.setNodeRDN(nodeRDN);
        kubeReadinessPort.setBasePath(port.getWebServicePath());
        kubeReadinessPort.setaServer(true);
        kubeReadinessPort.setContainingNodeFDN(processingPlant.getNodeFDN());
        processingPlant.getEndpoints().add(kubeReadinessPort.getNodeFDN());
        getLogger().trace(".addKubeReadinessPort(): Add the KubeReadinessPort Port to the Topology Cache");
        getTopologyIM().addTopologyNode(processingPlant.getNodeFDN(), kubeReadinessPort);
        getLogger().debug(".addKubeReadinessPort(): Exit, endpoint added");
    }

    //
    // Resilience Mode Calculation
    //
    public ResilienceModeEnum getResilienceMode() {
        getLogger().debug(".getResilienceMode(): Entry");
        boolean clustered = false;
        boolean kubernetes = false;
        boolean multisite = false;

        if (getPropertyFile().getDeploymentSites().getSiteCount() < 2) {
            multisite = false;
        }

        if (getPropertyFile().getDeploymentMode().getProcessingPlantReplicationCount() < 2) {
            clustered = true;
        }

        if (getPropertyFile().getDeploymentMode().isKubernetes()) {
            kubernetes = true;
        }

        if (!clustered && !kubernetes && !multisite) {
            getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
            return (ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        }
        if (!clustered && kubernetes && !multisite) {
            getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_STANDALONE);
            return (ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_STANDALONE);
        }
        if (clustered && !kubernetes && !multisite) {
            getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_CLUSTERED);
            return (ResilienceModeEnum.RESILIENCE_MODE_CLUSTERED);
        }
        if (clustered && kubernetes && !multisite) {
            getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_CLUSTERED );
            return (ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_CLUSTERED);
        }
        if (!clustered && !kubernetes && multisite) {
            getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_MULTISITE);
            return (ResilienceModeEnum.RESILIENCE_MODE_MULTISITE);
        }
        if (clustered && !kubernetes && multisite) {
            getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_MULTISITE_CLUSTERED);
            return (ResilienceModeEnum.RESILIENCE_MODE_MULTISITE_CLUSTERED);
        }
        if (!clustered && kubernetes && multisite) {
            getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE);
            return (ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE);
        }
        if (clustered && kubernetes && multisite) {
            getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE_CLUSTERED);
            return (ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE_CLUSTERED);
        }
        getLogger().debug(".getResilienceMode(): Exit, returning->{}", ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
        return (ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
    }

    //
    // Concurrency Mode Calculation
    //

    public ConcurrencyModeEnum getConcurrenceMode() {
        getLogger().debug(".getConcurrenceMode(): Entry");
        boolean standalone = false;
        boolean concurrent = false;

        if (getResilienceMode().equals(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE)) {
            standalone = true;
        }
        if (getPropertyFile().getDeploymentMode().isConcurrent()) {
            concurrent = true;
        }
        if (!standalone && !concurrent) {
            getLogger().debug(".getConcurrenceMode(): Exit, returning->{}", ConcurrencyModeEnum.CONCURRENCY_MODE_ONDEMAND);
            return (ConcurrencyModeEnum.CONCURRENCY_MODE_ONDEMAND);
        }
        if (!standalone && concurrent) {
            getLogger().debug(".getConcurrenceMode(): Exit, returning->{}", ConcurrencyModeEnum.CONCURRENCY_MODE_CONCURRENT);
            return (ConcurrencyModeEnum.CONCURRENCY_MODE_CONCURRENT);
        }
        getLogger().debug(".getConcurrenceMode(): Exit, returning->{}", ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
        return (ConcurrencyModeEnum.CONCURRENCY_MODE_STANDALONE);
    }


    protected BaseSubsystemPropertyFile readPropertyFile(){
        String propertyFileName = specifyPropertyFileName();
        Class propertyFileClass = specifyPropertyFileClass();
        getLogger().info(".readPropertyFile(): Entry, propertyFileName->{}", propertyFileName);
        try {
            getLogger().trace(".readPropertyFile(): Establish YAML ObjectMapper");
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            yamlMapper.findAndRegisterModules();
            yamlMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
            getLogger().trace(".readPropertyFile(): Establishing InputStream");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream propertyYAMLFile = classLoader.getResourceAsStream("./"+propertyFileName);
            getLogger().trace(".readPropertyFile(): Reading YAML configuration file");
            this.propertyFile = (BaseSubsystemPropertyFile) yamlMapper.readValue(propertyYAMLFile, propertyFileClass);
            getLogger().debug(".loadPropertyFile(): Exit, file loaded, propertyFile->{}", this.propertyFile);
            return(this.propertyFile);
        } catch(FileNotFoundException noFile){
            getLogger().error(".loadPropertyFile(): Configuration File->{} is not found, error->{}", propertyFileName, noFile.getMessage());
        } catch(IOException ioError){
            getLogger().error(".loadPropertyFile(): Configuration File->{} could not be loaded, error->{}", propertyFileName, ioError.getMessage());
        }
        getLogger().debug(".loadPropertyFile(): failed to load file");
        return(null);
    }



    public ProcessingPlantTopologyNode getProcessingPlantNode() {
        return processingPlantNode;
    }

    public void setProcessingPlantNode(ProcessingPlantTopologyNode processingPlantNode) {
        this.processingPlantNode = processingPlantNode;
    }
}
