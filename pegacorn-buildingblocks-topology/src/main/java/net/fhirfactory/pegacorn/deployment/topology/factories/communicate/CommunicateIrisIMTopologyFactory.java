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
package net.fhirfactory.pegacorn.deployment.topology.factories.communicate;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.names.functionality.subsystem.CommunicateIrisSubsystemFunctionalityNames;
import net.fhirfactory.pegacorn.deployment.names.functionality.subsystem.CommunicateRoomServerComponentNames;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.InterfaceDefinitionSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.communicate.iris.im.CommunicateIrisIMPropertyFile;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.common.PetasosEnabledSubsystemTopologyFactory;
import net.fhirfactory.pegacorn.core.model.topology.common.IPCInterface;
import net.fhirfactory.pegacorn.core.model.topology.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.technologies.HTTPProcessingPlantTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.technologies.HTTPServerClusterServiceTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.common.EndpointProviderInterface;

import javax.inject.Inject;

public abstract class CommunicateIrisIMTopologyFactory extends PetasosEnabledSubsystemTopologyFactory {


    public CommunicateIrisIMTopologyFactory(){
        super();
    }

    private CommunicateIrisIMPropertyFile getCommunicateIrisPropertyFile(){
        return((CommunicateIrisIMPropertyFile)getPropertyFile());
    }

    @Inject
    private CommunicateIrisSubsystemFunctionalityNames communicateIrisComponentNames;

    @Inject
    private CommunicateRoomServerComponentNames communicateRoomServerComponentNames;

    public void addApplicationServicesServerEndpoint( EndpointProviderInterface endpointProvider) {
        getLogger().debug(".addMatrixServicesServerEndpoint(): Entry");
        HTTPServerClusterServiceTopologyEndpointPort matrixApplicationServicesServer = new HTTPServerClusterServiceTopologyEndpointPort();
        String name = communicateIrisComponentNames.getEndpointServerName(communicateIrisComponentNames.getFunctionNameInteractMatrixApplicationServices());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, getCommunicateIrisPropertyFile().getSubsystemInstant().getProcessingPlantVersion(), TopologyNodeTypeEnum.ENDPOINT);
        matrixApplicationServicesServer.setNodeRDN(nodeRDN);
        matrixApplicationServicesServer.constructFDN(endpointProvider.getNodeFDN(),nodeRDN);
        matrixApplicationServicesServer.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(),nodeRDN);
        matrixApplicationServicesServer.setEndpointType(PetasosTopologyEndpointTypeEnum.HTTP_API_SERVER);
        matrixApplicationServicesServer.setaServer(true);
        if(endpointProvider.getComponentType().equals(TopologyNodeTypeEnum.CLUSTER_SERVICE)) {
            matrixApplicationServicesServer.setHostDNSName(getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getServiceDNSEntry());
        } else {
            matrixApplicationServicesServer.setHostDNSName(getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getHostDNSEntry());
        }
        matrixApplicationServicesServer.setPortType("HTTPS");
        matrixApplicationServicesServer.setServicePortOffset(getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getClusterServicePortOffsetValue());
        matrixApplicationServicesServer.setPortValue(getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getPortValue());
        matrixApplicationServicesServer.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        Integer portValue = getCommunicateIrisPropertyFile().getDefaultServicePortLowerBound() + getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getClusterServicePortOffsetValue();
        matrixApplicationServicesServer.setServicePortValue(portValue);
        matrixApplicationServicesServer.setEncrypted(getCommunicateIrisPropertyFile().getDeploymentMode().isUsingInternalEncryption());
        IPCInterface ipcInterface = new IPCInterface();
        ipcInterface.setEnablingTopologyEndpoint(matrixApplicationServicesServer.getNodeFDN());
        ipcInterface.setGroupName(name);
        for(InterfaceDefinitionSegment currentInterface: getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getSupportedInterfaceProfiles()){
            IPCInterfaceDefinition interfaceDef = new IPCInterfaceDefinition();
            interfaceDef.setInterfaceFormalVersion(currentInterface.getInterfaceDefinitionVersion());
            interfaceDef.setInterfaceFormalName(currentInterface.getInterfaceDefinitionName());
            ipcInterface.getSupportedInterfaceDefinitions().add(interfaceDef);
            ipcInterface.getSupportedDeploymentModes().add(endpointProvider.getResilienceMode());
        }
        matrixApplicationServicesServer.getSupportedInterfaceSet().add(ipcInterface);
        matrixApplicationServicesServer.setContainingNodeFDN(endpointProvider.getNodeFDN());
        endpointProvider.addEndpoint(matrixApplicationServicesServer.getNodeFDN());
        getLogger().trace(".addMatrixServicesServerEndpoint(): Add the MatrixApplicationServicesServer Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), matrixApplicationServicesServer);
        getLogger().debug(".addMatrixServicesServerEndpoint(): Exit");
    }

    public void addApplicationServicesClientEndpoint( EndpointProviderInterface endpointProvider) {
        getLogger().debug(".addApplicationServicesClientEndpoint(): Entry");
        HTTPProcessingPlantTopologyEndpointPort matrixClientServicesAPIClient = new HTTPProcessingPlantTopologyEndpointPort();
        String clientName = communicateIrisComponentNames.getEndpointClientName(communicateRoomServerComponentNames.getFunctionNameMatrixClientServices());
        TopologyNodeRDN nodeRDN = createNodeRDN(clientName, getCommunicateIrisPropertyFile().getSubsystemInstant().getProcessingPlantVersion(), TopologyNodeTypeEnum.ENDPOINT);
        matrixClientServicesAPIClient.setNodeRDN(nodeRDN);
        matrixClientServicesAPIClient.constructFDN(endpointProvider.getNodeFDN(),nodeRDN);
        matrixClientServicesAPIClient.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(),nodeRDN);
        matrixClientServicesAPIClient.setEndpointType(PetasosTopologyEndpointTypeEnum.HTTP_API_CLIENT);
        matrixClientServicesAPIClient.setaServer(false);
        matrixClientServicesAPIClient.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        matrixClientServicesAPIClient.setEncrypted(getCommunicateIrisPropertyFile().getDeploymentMode().isUsingInternalEncryption());
        endpointProvider.addEndpoint(matrixClientServicesAPIClient.getNodeFDN());
        getLogger().trace(".addApplicationServicesClientEndpoint(): Add the MatrixApplicationServicesClientEndpoint Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), matrixClientServicesAPIClient);
        getLogger().debug(".addApplicationServicesClientEndpoint(): Exit");
    }

    //
    // Populate Base SubSystem Topology
    //
/*
    public void populateSubsystemTopology( ){
        SolutionTopologyNode solutionNode = getTopologyIM().getSolutionTopology();
        CommunicateIrisIMPropertyFile propertyFile = (CommunicateIrisIMPropertyFile)getPropertyFile();
        SubsystemTopologyNode subsystemNode = this.addSubsystemNode(solutionNode);
        BusinessServiceTopologyNode businessServiceNode = this.addBusinessServiceNode(subsystemNode);
        DeploymentSiteTopologyNode siteNode = this.addDeploymentSiteNode( businessServiceNode);
        ClusterServiceTopologyNode clusterServiceNode = this.addClusterServiceNode( siteNode);
        this.addEdgeAnswerPort(clusterServiceNode);
        this.addEdgeReceivePort( clusterServiceNode);
        this.addPetasosReplicationPort(clusterServiceNode);
        this.addPetasosStatusPort( clusterServiceNode);
        this.addApplicationServicesClientEndpoint(clusterServiceNode);
        PlatformTopologyNode platformTopologyNode = this.addPlatformNode(clusterServiceNode);
        ProcessingPlantTopologyNode processingPlantNode = this.addPegacornProcessingPlant(platformTopologyNode);
        this.addJolokiaPort( processingPlantNode);
        this.addPrometheusPort( processingPlantNode);
        this.addKubeLivelinessPort( processingPlantNode);
        this.addKubeReadinessPort(processingPlantNode);
        this.addEdgeAnswerPort(processingPlantNode);
        this.addEdgeReceivePort(processingPlantNode);
        this.addPetasosReplicationPort(processingPlantNode);
        this.addPetasosStatusPort( processingPlantNode);
        this.addApplicationServicesServerEndpoint( processingPlantNode);
    }

 */
}
