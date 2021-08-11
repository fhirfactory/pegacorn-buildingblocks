package net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.common;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.PetasosEnabledSubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.InterfaceDefinitionSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc.HTTPIPCServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc.JGroupsIPCServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc.JGroupsInitialHostSegment;
import net.fhirfactory.pegacorn.core.model.topology.common.IPCInterface;
import net.fhirfactory.pegacorn.core.model.topology.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.core.model.topology.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.technologies.HTTPServerClusterServiceTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.InitialHostSpecification;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.common.EndpointProviderInterface;
import net.fhirfactory.pegacorn.deployment.properties.codebased.PegacornReferenceProperties;

import javax.inject.Inject;

public abstract class PetasosEnabledSubsystemTopologyFactory extends PegacornTopologyFactoryBase{

    @Inject
    private PegacornReferenceProperties pegacornReferenceProperties;

    public PetasosEnabledSubsystemTopologyFactory(){
        super();
    }

    //
    // Build EdgeAnswer Port (if there)
    //

    protected void addEdgeAnswerPort( EndpointProviderInterface endpointProvider){
        getLogger().debug(".addEdgeAnswerPort(): Entry");
        PetasosEnabledSubsystemPropertyFile petasosEnabledSubsystemPropertyFile = (PetasosEnabledSubsystemPropertyFile)getPropertyFile();
        HTTPServerClusterServiceTopologyEndpointPort edgeAnswerPort = new HTTPServerClusterServiceTopologyEndpointPort();
        HTTPIPCServerPortSegment port = petasosEnabledSubsystemPropertyFile.getEdgeAnswer();
        if(port == null){
            getLogger().debug(".addEdgeAnswerPort(): Exit, no port to add");
            return;
        }
        edgeAnswerPort.setEncrypted(petasosEnabledSubsystemPropertyFile.getDeploymentMode().isUsingInternalEncryption());
        String name = getInterfaceNames().getEndpointServerName(getInterfaceNames().getFunctionNameEdgeAnswer());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, endpointProvider.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        edgeAnswerPort.setNodeRDN(nodeRDN);
        edgeAnswerPort.setName(getInterfaceNames().getFunctionNameEdgeAnswer());
        edgeAnswerPort.constructFDN(endpointProvider.getNodeFDN(), nodeRDN);
        edgeAnswerPort.setPortType(port.getPortType());
        edgeAnswerPort.setEndpointType(PetasosTopologyEndpointTypeEnum.HTTP_API_SERVER);
        edgeAnswerPort.setPortValue(port.getPortValue());
        edgeAnswerPort.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(), nodeRDN );
        edgeAnswerPort.setBasePath(pegacornReferenceProperties.getPegacornInternalFhirResourceR4Path());
        edgeAnswerPort.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        edgeAnswerPort.setaServer(true);
        edgeAnswerPort.setContainingNodeFDN(endpointProvider.getNodeFDN());
        endpointProvider.addEndpoint(edgeAnswerPort.getNodeFDN());
        getLogger().trace(".addEdgeAnswerPort(): Add the EdgeAnswer Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), edgeAnswerPort);
        getLogger().debug(".addEdgeAnswerPort(): Exit, endpoint added");
    }

    protected void addInterZoneIPCJGroupsPort( EndpointProviderInterface endpointProvider){
        getLogger().debug(".addInterZoneIPCJGroupsPort(): Entry");
        PetasosEnabledSubsystemPropertyFile petasosEnabledSubsystemPropertyFile = (PetasosEnabledSubsystemPropertyFile)getPropertyFile();
        StandardEdgeIPCEndpoint interZoneIPC = new StandardEdgeIPCEndpoint();
        JGroupsIPCServerPortSegment port = petasosEnabledSubsystemPropertyFile.getInterZoneIPC();
        if(port == null){
            getLogger().debug(".addInterZoneIPCJGroupsPort(): Exit, no port to add");
            return;
        }
        interZoneIPC.setEncrypted(petasosEnabledSubsystemPropertyFile.getDeploymentMode().isUsingInternalEncryption());
        String name = getInterfaceNames().getEndpointServerName(getInterfaceNames().getFunctionNameInterZoneJGroupsIPC());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, endpointProvider.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        interZoneIPC.setName(getInterfaceNames().getFunctionNameInterZoneJGroupsIPC());
        interZoneIPC.setNodeRDN(nodeRDN);
        interZoneIPC.constructFDN(endpointProvider.getNodeFDN(), nodeRDN);
        interZoneIPC.setPortType(port.getPortType());
        interZoneIPC.setHostDNSName(port.getHostDNSEntry());
        interZoneIPC.setEndpointType(PetasosTopologyEndpointTypeEnum.JGROUPS_INTERZONE_SERVICE);
        interZoneIPC.setSecurityZone(NetworkSecurityZoneEnum.fromSecurityZoneString(getPropertyFile().getDeploymentZone().getSecurityZoneName()));
        interZoneIPC.setPortValue(port.getPortValue());
        interZoneIPC.setNameSpace(getPropertyFile().getDeploymentZone().getNameSpace());
        interZoneIPC.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(), nodeRDN );
        for(JGroupsInitialHostSegment currentInitialHostSegment: port.getInitialHosts()) {
            getLogger().trace("addInterZoneIPCJGroupsPort(): Adding initialHost->{}", currentInitialHostSegment);
            InitialHostSpecification currentInitialHostSpec = new InitialHostSpecification();
            currentInitialHostSpec.setHostName(currentInitialHostSegment.getHostName());
            currentInitialHostSpec.setPort(currentInitialHostSegment.getPortNumber());
            interZoneIPC.getInitialHosts().add(currentInitialHostSpec);
        }
        interZoneIPC.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        interZoneIPC.setaServer(true);
        interZoneIPC.setContainingNodeFDN(endpointProvider.getNodeFDN());
        for(InterfaceDefinitionSegment currentSegment: port.getSupportedInterfaceProfiles()) {
            IPCInterface currentInterface = new IPCInterface();
            IPCInterfaceDefinition currentInterfaceDefinition = new IPCInterfaceDefinition();
            currentInterfaceDefinition.setInterfaceFormalName(currentSegment.getInterfaceDefinitionName());
            currentInterfaceDefinition.setInterfaceFormalVersion(currentSegment.getInterfaceDefinitionVersion());
            currentInterface.getSupportedInterfaceDefinitions().add(currentInterfaceDefinition);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE_CLUSTERED);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_STANDALONE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_CLUSTERED);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_MULTISITE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_CLUSTERED);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_MULTISITE_CLUSTERED);
            interZoneIPC.getSupportedInterfaceSet().add(currentInterface);
        }
        endpointProvider.addEndpoint(interZoneIPC.getNodeFDN());
        getLogger().trace(".addInterZoneIPCJGroupsPort(): Add the InterZone JGroups IPC Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), interZoneIPC);
        getLogger().debug(".addInterZoneIPCJGroupsPort(): Exit, endpoint added");
    }

    protected void addInterZoneOAMJGroupsPort( EndpointProviderInterface endpointProvider){
        getLogger().debug(".addInterZoneOAMJGroupsPort(): Entry");
        PetasosEnabledSubsystemPropertyFile petasosEnabledSubsystemPropertyFile = (PetasosEnabledSubsystemPropertyFile)getPropertyFile();
        StandardEdgeIPCEndpoint interZoneOAM = new StandardEdgeIPCEndpoint();
        JGroupsIPCServerPortSegment port = petasosEnabledSubsystemPropertyFile.getInterZoneOAM();
        if(port == null){
            getLogger().debug(".addInterZoneOAMJGroupsPort(): Exit, no port to add");
            return;
        }
        interZoneOAM.setEncrypted(petasosEnabledSubsystemPropertyFile.getDeploymentMode().isUsingInternalEncryption());
        String name = getInterfaceNames().getEndpointServerName(getInterfaceNames().getFunctionNameInterZoneJGroupsIPC());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, endpointProvider.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        interZoneOAM.setName(getInterfaceNames().getFunctionNameInterZoneJGroupsIPC());
        interZoneOAM.setNodeRDN(nodeRDN);
        interZoneOAM.constructFDN(endpointProvider.getNodeFDN(), nodeRDN);
        interZoneOAM.setPortType(port.getPortType());
        interZoneOAM.setHostDNSName(port.getHostDNSEntry());
        interZoneOAM.setEndpointType(PetasosTopologyEndpointTypeEnum.JGROUPS_INTERZONE_SERVICE);
        interZoneOAM.setSecurityZone(NetworkSecurityZoneEnum.fromSecurityZoneString(getPropertyFile().getDeploymentZone().getSecurityZoneName()));
        interZoneOAM.setPortValue(port.getPortValue());
        interZoneOAM.setNameSpace(getPropertyFile().getDeploymentZone().getNameSpace());
        interZoneOAM.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(), nodeRDN );
        for(JGroupsInitialHostSegment currentInitialHostSegment: port.getInitialHosts()) {
            getLogger().trace("addInterZoneOAMJGroupsPort(): Adding initialHost->{}", currentInitialHostSegment);
            InitialHostSpecification currentInitialHostSpec = new InitialHostSpecification();
            currentInitialHostSpec.setHostName(currentInitialHostSegment.getHostName());
            currentInitialHostSpec.setPort(currentInitialHostSegment.getPortNumber());
            interZoneOAM.getInitialHosts().add(currentInitialHostSpec);
        }
        interZoneOAM.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        interZoneOAM.setaServer(true);
        interZoneOAM.setContainingNodeFDN(endpointProvider.getNodeFDN());
        for(InterfaceDefinitionSegment currentSegment: port.getSupportedInterfaceProfiles()) {
            IPCInterface currentInterface = new IPCInterface();
            IPCInterfaceDefinition currentInterfaceDefinition = new IPCInterfaceDefinition();
            currentInterfaceDefinition.setInterfaceFormalName(currentSegment.getInterfaceDefinitionName());
            currentInterfaceDefinition.setInterfaceFormalVersion(currentSegment.getInterfaceDefinitionVersion());
            currentInterface.getSupportedInterfaceDefinitions().add(currentInterfaceDefinition);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE_CLUSTERED);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_STANDALONE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_CLUSTERED);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_MULTISITE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_STANDALONE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_CLUSTERED);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_MULTISITE_CLUSTERED);
            interZoneOAM.getSupportedInterfaceSet().add(currentInterface);
        }
        endpointProvider.addEndpoint(interZoneOAM.getNodeFDN());
        getLogger().trace(".addInterZoneOAMJGroupsPort(): Add the InterZone JGroups IPC Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), interZoneOAM);
        getLogger().debug(".addInterZoneOAMJGroupsPort(): Exit, endpoint added");
    }

    protected void addIntraZoneIPCJGroupsPort( EndpointProviderInterface endpointProvider){
        getLogger().debug(".addInterZoneIPCJGroupsPort(): Entry");
        PetasosEnabledSubsystemPropertyFile petasosEnabledSubsystemPropertyFile = (PetasosEnabledSubsystemPropertyFile)getPropertyFile();
        StandardEdgeIPCEndpoint intraZoneIPC = new StandardEdgeIPCEndpoint();
        JGroupsIPCServerPortSegment port = petasosEnabledSubsystemPropertyFile.getIntraZoneIPC();
        if(port == null){
            getLogger().debug(".addInterZoneIPCJGroupsPort(): Exit, no port to add");
            return;
        }
        intraZoneIPC.setEncrypted(petasosEnabledSubsystemPropertyFile.getDeploymentMode().isUsingInternalEncryption());
        String name = getInterfaceNames().getEndpointServerName(getInterfaceNames().getFunctionNameIntraZoneJGroupsIPC());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, endpointProvider.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        intraZoneIPC.setName(getInterfaceNames().getFunctionNameIntraZoneJGroupsIPC());
        intraZoneIPC.setNodeRDN(nodeRDN);
        intraZoneIPC.setHostDNSName(port.getHostDNSEntry());
        intraZoneIPC.constructFDN(endpointProvider.getNodeFDN(), nodeRDN);
        intraZoneIPC.setPortType(port.getPortType());
        intraZoneIPC.setEndpointType(PetasosTopologyEndpointTypeEnum.JGROUPS_INTRAZONE_SERVICE);
        intraZoneIPC.setPortValue(port.getPortValue());
        intraZoneIPC.setSecurityZone(NetworkSecurityZoneEnum.fromSecurityZoneString(getPropertyFile().getDeploymentZone().getSecurityZoneName()));
        intraZoneIPC.setNameSpace(getPropertyFile().getDeploymentZone().getNameSpace());
        intraZoneIPC.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(), nodeRDN );
        for(JGroupsInitialHostSegment currentInitialHostSegment: port.getInitialHosts()) {
            InitialHostSpecification currentInitialHostSpec = new InitialHostSpecification();
            currentInitialHostSpec.setHostName(currentInitialHostSegment.getHostName());
            currentInitialHostSpec.setPort(currentInitialHostSegment.getPortNumber());
            intraZoneIPC.getInitialHosts().add(currentInitialHostSpec);
        }
        intraZoneIPC.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        intraZoneIPC.setaServer(true);
        intraZoneIPC.setContainingNodeFDN(endpointProvider.getNodeFDN());
        for(InterfaceDefinitionSegment currentSegment: port.getSupportedInterfaceProfiles()) {
            IPCInterface currentInterface = new IPCInterface();
            IPCInterfaceDefinition currentInterfaceDefinition = new IPCInterfaceDefinition();
            currentInterfaceDefinition.setInterfaceFormalName(currentSegment.getInterfaceDefinitionName());
            currentInterfaceDefinition.setInterfaceFormalVersion(currentSegment.getInterfaceDefinitionVersion());
            currentInterface.getSupportedInterfaceDefinitions().add(currentInterfaceDefinition);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE_CLUSTERED);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_STANDALONE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_CLUSTERED);
            intraZoneIPC.getSupportedInterfaceSet().add(currentInterface);
        }
        endpointProvider.addEndpoint(intraZoneIPC.getNodeFDN());
        getLogger().trace(".addInterZoneIPCJGroupsPort(): Add the IntraZone JGroups IPC Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), intraZoneIPC);
        getLogger().debug(".addInterZoneIPCJGroupsPort(): Exit, endpoint added");
    }

    protected void addIntraZoneOAMJGroupsPort( EndpointProviderInterface endpointProvider){
        getLogger().debug(".addIntraZoneOAMJGroupsPort(): Entry");
        PetasosEnabledSubsystemPropertyFile petasosEnabledSubsystemPropertyFile = (PetasosEnabledSubsystemPropertyFile)getPropertyFile();
        StandardEdgeIPCEndpoint intraZoneOAM = new StandardEdgeIPCEndpoint();
        JGroupsIPCServerPortSegment port = petasosEnabledSubsystemPropertyFile.getIntraZoneOAM();
        if(port == null){
            getLogger().debug(".addIntraZoneOAMJGroupsPort(): Exit, no port to add");
            return;
        }
        intraZoneOAM.setEncrypted(petasosEnabledSubsystemPropertyFile.getDeploymentMode().isUsingInternalEncryption());
        String name = getInterfaceNames().getEndpointServerName(getInterfaceNames().getFunctionNameIntraZoneJGroupsIPC());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, endpointProvider.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        intraZoneOAM.setName(getInterfaceNames().getFunctionNameIntraZoneJGroupsIPC());
        intraZoneOAM.setNodeRDN(nodeRDN);
        intraZoneOAM.setHostDNSName(port.getHostDNSEntry());
        intraZoneOAM.constructFDN(endpointProvider.getNodeFDN(), nodeRDN);
        intraZoneOAM.setPortType(port.getPortType());
        intraZoneOAM.setEndpointType(PetasosTopologyEndpointTypeEnum.JGROUPS_INTRAZONE_SERVICE);
        intraZoneOAM.setPortValue(port.getPortValue());
        intraZoneOAM.setSecurityZone(NetworkSecurityZoneEnum.fromSecurityZoneString(getPropertyFile().getDeploymentZone().getSecurityZoneName()));
        intraZoneOAM.setNameSpace(getPropertyFile().getDeploymentZone().getNameSpace());
        intraZoneOAM.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(), nodeRDN );
        for(JGroupsInitialHostSegment currentInitialHostSegment: port.getInitialHosts()) {
            InitialHostSpecification currentInitialHostSpec = new InitialHostSpecification();
            currentInitialHostSpec.setHostName(currentInitialHostSegment.getHostName());
            currentInitialHostSpec.setPort(currentInitialHostSegment.getPortNumber());
            intraZoneOAM.getInitialHosts().add(currentInitialHostSpec);
        }
        intraZoneOAM.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        intraZoneOAM.setaServer(true);
        intraZoneOAM.setContainingNodeFDN(endpointProvider.getNodeFDN());
        for(InterfaceDefinitionSegment currentSegment: port.getSupportedInterfaceProfiles()) {
            IPCInterface currentInterface = new IPCInterface();
            IPCInterfaceDefinition currentInterfaceDefinition = new IPCInterfaceDefinition();
            currentInterfaceDefinition.setInterfaceFormalName(currentSegment.getInterfaceDefinitionName());
            currentInterfaceDefinition.setInterfaceFormalVersion(currentSegment.getInterfaceDefinitionVersion());
            currentInterface.getSupportedInterfaceDefinitions().add(currentInterfaceDefinition);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE_CLUSTERED);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_MULTISITE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_STANDALONE);
            currentInterface.getSupportedDeploymentModes().add(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_CLUSTERED);
            intraZoneOAM.getSupportedInterfaceSet().add(currentInterface);
        }
        endpointProvider.addEndpoint(intraZoneOAM.getNodeFDN());
        getLogger().trace(".addIntraZoneOAMJGroupsPort(): Add the IntraZone JGroups IPC Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), intraZoneOAM);
        getLogger().debug(".addIntraZoneOAMJGroupsPort(): Exit, endpoint added");
    }

    //
    // Build EdgeAnswer Port (if there)
    //
    /*

    protected void addEdgeReceivePort( EndpointProviderInterface endpointProvider){
        getLogger().debug(".addEdgeReceivePort(): Entry");
        PetasosEnabledSubsystemPropertyFile petasosEnabledSubsystemPropertyFile = (PetasosEnabledSubsystemPropertyFile)getPropertyFile();
        HTTPServerClusterServiceTopologyEndpointPort edgeReceivePort = new HTTPServerClusterServiceTopologyEndpointPort();
        HTTPIPCPortSegment port = petasosEnabledSubsystemPropertyFile.getEdgeReceive();
        if(port == null){
            getLogger().debug(".addEdgeReceivePort(): Exit, no port to add");
            return;
        }
        edgeReceivePort.setEncrypted(petasosEnabledSubsystemPropertyFile.getDeploymentMode().isUsingInternalEncryption());
        String name = getSubsystemBaseNames().getEndpointServerName(getSubsystemBaseNames().getEndpointServerName(getSubsystemBaseNames().getFunctionNameEdgeReceive()));
        TopologyNodeRDN nodeRDN = createNodeRDN(name, endpointProvider.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        edgeReceivePort.setNodeRDN(nodeRDN);
        edgeReceivePort.constructFDN(endpointProvider.getNodeFDN(), nodeRDN);
        edgeReceivePort.setPortType(port.getPortType());
        edgeReceivePort.setEndpointType(TopologyEndpointTypeEnum.HTTP_API_SERVER);
        edgeReceivePort.setPortValue(port.getPortValue());
        edgeReceivePort.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(), nodeRDN );
        edgeReceivePort.setBasePath(pegacornReferenceProperties.getPegacornPetasosIPCPath());
        edgeReceivePort.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        edgeReceivePort.setaServer(true);
        edgeReceivePort.setContainingNodeFDN(endpointProvider.getNodeFDN());
        endpointProvider.addEndpoint( edgeReceivePort.getNodeFDN());
        getLogger().trace(".addEdgeReceivePort(): Add the EdgeReceive Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), edgeReceivePort);
        getLogger().debug(".addEdgeReceivePort(): Exit, endpoint added");
    }

     */

    //
    // Build PetasosReplication Port (if there)
    //
/*
    protected void addPetasosReplicationPort(EndpointProviderInterface endpointProvider){
        getLogger().debug(".addPetasosReplicationPort(): Entry");
        PetasosEnabledSubsystemPropertyFile petasosEnabledSubsystemPropertyFile = (PetasosEnabledSubsystemPropertyFile)getPropertyFile();
        HTTPServerClusterServiceTopologyEndpointPort petasosReplicationPort = new HTTPServerClusterServiceTopologyEndpointPort();
        HTTPIPCPortSegment port = petasosEnabledSubsystemPropertyFile.getPetasosReplication();
        if(port == null){
            getLogger().debug(".addPetasosReplicationPort(): Exit, no port to add");
            return;
        }
        petasosReplicationPort.setEncrypted(petasosEnabledSubsystemPropertyFile.getDeploymentMode().isUsingInternalEncryption());
        String name = getSubsystemBaseNames().getEndpointServerName(getSubsystemBaseNames().getEndpointServerName(getSubsystemBaseNames().getFunctionNameEdgeReceive()));
        TopologyNodeRDN nodeRDN = createNodeRDN(name, endpointProvider.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        petasosReplicationPort.setNodeRDN(nodeRDN);
        petasosReplicationPort.constructFDN(endpointProvider.getNodeFDN(), nodeRDN);
        petasosReplicationPort.setPortType(port.getPortType());
        petasosReplicationPort.setEndpointType(TopologyEndpointTypeEnum.HTTP_API_SERVER);
        petasosReplicationPort.setPortValue(port.getPortValue());
        petasosReplicationPort.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(), nodeRDN );
        petasosReplicationPort.setBasePath(pegacornReferenceProperties.getPegacornPetasosReplicationPath());
        petasosReplicationPort.setaServer(true);
        petasosReplicationPort.setContainingNodeFDN(endpointProvider.getNodeFDN());
        petasosReplicationPort.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        endpointProvider.addEndpoint( petasosReplicationPort.getNodeFDN());
        getLogger().trace(".addPetasosReplicationPort(): Add the PetasosReplication Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), petasosReplicationPort);
        getLogger().debug(".addPetasosReplicationPort(): Exit, endpoint added");
    }

    //
    // Build PetasosStatus Port (if there)
    //

    protected void addPetasosStatusPort(EndpointProviderInterface endpointProvider){
        getLogger().debug(".addPetasosStatusPort(): Entry");
        PetasosEnabledSubsystemPropertyFile petasosEnabledSubsystemPropertyFile = (PetasosEnabledSubsystemPropertyFile)getPropertyFile();
        HTTPServerClusterServiceTopologyEndpointPort petasosStatusPort = new HTTPServerClusterServiceTopologyEndpointPort();
        if(port == null){
            getLogger().debug(".addPetasosStatusPort(): Exit, no port to add");
            return;
        }
        petasosStatusPort.setEncrypted(petasosEnabledSubsystemPropertyFile.getDeploymentMode().isUsingInternalEncryption());
        String name = getSubsystemBaseNames().getEndpointServerName(getSubsystemBaseNames().getEndpointServerName(getSubsystemBaseNames().getFunctionNameEdgeReceive()));
        TopologyNodeRDN nodeRDN = createNodeRDN(name, endpointProvider.getNodeRDN().getNodeVersion(), TopologyNodeTypeEnum.ENDPOINT);
        petasosStatusPort.setNodeRDN(nodeRDN);
        petasosStatusPort.constructFDN(endpointProvider.getNodeFDN(), nodeRDN);
        petasosStatusPort.setPortType(port.getPortType());
        petasosStatusPort.setEndpointType(TopologyEndpointTypeEnum.HTTP_API_SERVER);
        petasosStatusPort.setPortValue(port.getPortValue());
        petasosStatusPort.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(), nodeRDN );
        petasosStatusPort.setBasePath(pegacornReferenceProperties.getPegacornPetasosStatusPath());
        petasosStatusPort.setaServer(true);
        petasosStatusPort.setComponentType(TopologyNodeTypeEnum.ENDPOINT);
        petasosStatusPort.setContainingNodeFDN(endpointProvider.getNodeFDN());
        endpointProvider.addEndpoint(petasosStatusPort.getNodeFDN());
        getLogger().trace(".addPetasosStatusPort(): Add the PetasosStatus Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getNodeFDN(), petasosStatusPort);
        getLogger().debug(".addPetasosStatusPort(): Exit, endpoint added");
    }

 */
}
