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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.helpers;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointChannelScopeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.DefaultWorkshopSetEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.CoreSubsystemPetasosEndpointsWatchdog;
import net.fhirfactory.pegacorn.petasos.endpoints.map.PetasosEndpointMap;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class JGroupsBasedParticipantInformationService {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsBasedParticipantInformationService.class);

    private PubSubParticipant myInterZoneParticipantRole;
    private PubSubParticipant myIntraZoneParticipantRole;
    private StandardEdgeIPCEndpoint myIntraZoneTopologyEndpoint;
    private StandardEdgeIPCEndpoint myInterZoneTopologyEndpoint;
    private boolean initialised;
    private String myIntraZoneIPCEndpointName;
    private String myInterZoneIPCEndpointName;
    private String myIntraZoneOAMPubSubEndpointName;
    private String myInterZoneOAMPubSubEndpointName;
    private String myIntraZoneOAMDiscoveryEndpointName;
    private String myInterZoneOAMDiscoveryEndpointName;
    private String myIntraZoneIPCEndpointAddressName;
    private String myInterZoneIPCEndpointAddressName;
    private String myIntraZoneOAMPubSubEndpointAddressName;
    private String myInterZoneOAMPubSubEndpointAddressName;
    private String myIntraZoneOAMDiscoveryEndpointAddressName;
    private String myInterZoneOAMDiscoveryEndpointAddressName;

    private String instanceQualifier;

    private static String INTRAZONE_EDGE_FORWARDER_WUP_NAME = "EdgeIntraZoneMessageForwardWUP";
    private static String INTERZONE_EDGE_FORWARDER_WUP_NAME = "EdgeInterZoneMessageForwardWUP";
    private static String EDGE_FORWARDER_WUP_VERSION = "1.0.0";

    private static String INTERSITE_PREFIX = ".InterSite";
    private static String INTRAZONE_PREFIX = ".IntraZone";
    private static String INTERZONE_PREFIX = ".InterZone";

    private static String INTERZONE_OAM_GROUP_NAME = "InterZone.OAM";
    private static String INTERZONE_IPC_GROUP_NAME = "InterZone.IPC";
    private static String INTRAZONE_OAM_GROUP_NAME = "IntraZone.OAM";
    private static String INTRAZONE_IPC_GROUP_NAME = "IntraZone.IPC";
    private static String INTRASITE_OAM_GROUP_NAME = "IntraSite.OAM";
    private static String INTRASITE_IPC_GROUP_NAME = "IntraSite.IPC";

    private static Long ENDPOINT_VALIDATION_START_DELAY = 30000L;
    private static Long ENDPOINT_VALIDATION_PERIOD = 10000L;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    @Inject
    private PetasosEndpointMap endpointMap;

    @Inject
    private CoreSubsystemPetasosEndpointsWatchdog coreSubsystemPetasosEndpointsWatchdog;

    //
    // Constructor
    //

    public JGroupsBasedParticipantInformationService(){
        this.initialised = false;
        this.instanceQualifier = UUID.randomUUID().toString();
    }

    //
    // PostConstruct initialisation
    //

    @PostConstruct
    public void initialise() {
        if(initialised){
            return;
        }
        getLogger().info(".initialise(): [build myIntraZoneIPCEndpointName] start");
        String intraZoneIPCKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneIPCEndpointAddressName = intraZoneIPCKey;
        String intraZoneIPCName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneIPCEndpointName = intraZoneIPCName;
        getLogger().info(".initialise(): [build myIntraZoneIPCEndpointName] finish, myIntraZoneIPCEndpointName->{}", this.myIntraZoneIPCEndpointName);
        getLogger().info(".initialise(): [build myInterZoneIPCEndpointName] start");
        String interZoneIPCKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX +  PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneIPCEndpointAddressName = interZoneIPCKey;
        String interZoneIPCName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX +  "(" + getInstanceQualifier() + ")";
        this.myInterZoneIPCEndpointName = interZoneIPCName;
        getLogger().info(".initialise(): [build myInterZoneIPCEndpointName] finish, myInterZoneIPCEndpointName->{}", this.myInterZoneIPCEndpointName);
        getLogger().info(".initialise(): [build myIntraZoneOAMPubSubEndpointName] start");
        String intraZoneOAMPubSubKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX +  PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneOAMPubSubEndpointAddressName = intraZoneOAMPubSubKey;
        String intraZoneOAMPubSubName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneOAMPubSubEndpointName = intraZoneOAMPubSubName;
        getLogger().info(".initialise(): [build myIntraZoneOAMPubSubEndpointName] finish, myIntraZoneOAMPubSubEndpointName->{}", this.myIntraZoneOAMPubSubEndpointName);
        getLogger().info(".initialise(): [build myInterZoneOAMPubSubEndpointName] start");
        String interZoneOAMPubSubKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneOAMPubSubEndpointAddressName = interZoneOAMPubSubKey;
        String interZoneOAMPubSubName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myInterZoneOAMPubSubEndpointName = interZoneOAMPubSubName;
        getLogger().info(".initialise(): [build myInterZoneOAMPubSubEndpointName] finish, myInterZoneOAMPubSubEndpointName->{}", this.myInterZoneOAMPubSubEndpointName);
        getLogger().info(".initialise(): [build myIntraZoneOAMTopologyEndpointName] start");
        String intraZoneOAMTopologyKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneOAMDiscoveryEndpointAddressName = intraZoneOAMTopologyKey;
        String intraZoneOAMTopologyName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneOAMDiscoveryEndpointName = intraZoneOAMTopologyName;
        getLogger().info(".initialise(): [build myIntraZoneOAMTopologyEndpointName] finish, myIntraZoneOAMTopologyEndpointName->{}", this.myIntraZoneOAMDiscoveryEndpointName);
        getLogger().info(".initialise(): [build myInterZoneOAMTopologyEndpointName] start");
        String interZoneOAMTopologyKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneOAMDiscoveryEndpointAddressName = interZoneOAMTopologyKey;
        String interZoneOAMTopologyName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myInterZoneOAMDiscoveryEndpointName = interZoneOAMTopologyName;
        getLogger().info(".initialise(): [build myInterZoneOAMTopologyEndpointName] finish, myInterZoneOAMTopologyEndpointName->{}", this.myInterZoneOAMDiscoveryEndpointName);

        // Derive the IPCEndpoints
        getLogger().info(".initialise(): [resolving interzone topology endpoint] Start");
        this.myInterZoneTopologyEndpoint = deriveTopologyEndpoint(PetasosTopologyEndpointTypeEnum.JGROUPS_INTERZONE_SERVICE, getInterfaceNames().getFunctionNameInterZoneJGroupsIPC());
        getLogger().info(".initialise(): [resolving interzone topology endpoint] finish, myInterZoneTopologyEndpoint->{}", this.myInterZoneTopologyEndpoint);
        getLogger().info(".initialise(): [resolving intrazone topology endpoint] Start");
        this.myIntraZoneTopologyEndpoint = deriveTopologyEndpoint(PetasosTopologyEndpointTypeEnum.JGROUPS_INTRAZONE_SERVICE, getInterfaceNames().getFunctionNameIntraZoneJGroupsIPC());
        getLogger().info(".initialise(): [resolving intrazone topology endpoint] finish, myIntraZoneTopologyEndpoint->{}", this.myIntraZoneTopologyEndpoint);

        // Create the Participants
        getLogger().info(".initialise(): [create the interzone participant] Start");
        this.myInterZoneParticipantRole = buildParticipant(coreSubsystemPetasosEndpointsWatchdog.getInterzoneIPC());
        getLogger().info(".initialise(): [create the interzone participant] Finish, myInterZoneParticipantRole->{}", this.myInterZoneParticipantRole);
        getLogger().info(".initialise(): [create the intrazone participant] Start");
        this.myIntraZoneParticipantRole = buildParticipant(coreSubsystemPetasosEndpointsWatchdog.getIntrazoneIPC());
        getLogger().info(".initialise(): [create the intrazone participant] Finish, myIntraZoneParticipantRole->{}", this.myIntraZoneParticipantRole);

    }

    //
    // Address a Race Condition
    //

    public PubSubParticipant buildMyInterZoneParticipantRole(PetasosEndpoint endpoint){
        if(this.myInterZoneParticipantRole == null){
            this.myInterZoneParticipantRole = buildParticipant(endpoint);
        }
        return(this.myInterZoneParticipantRole);
    }

    public PubSubParticipant buildMyIntraZoneParticipantRole(PetasosEndpoint endpoint){
        if(this.myIntraZoneParticipantRole == null){
            this.myIntraZoneParticipantRole = buildParticipant(endpoint);
        }
        return(this.myIntraZoneParticipantRole);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public PubSubParticipant getMyInterZoneParticipantRole() {
        if(this.myInterZoneParticipantRole == null){
            this.myInterZoneParticipantRole = buildParticipant(coreSubsystemPetasosEndpointsWatchdog.getInterzoneIPC());
        }
        return myInterZoneParticipantRole;
    }

    public PubSubParticipant getMyIntraZoneParticipantRole() {
        if(this.myIntraZoneParticipantRole == null){
            this.myIntraZoneParticipantRole = buildParticipant(coreSubsystemPetasosEndpointsWatchdog.getIntrazoneIPC());
        }
        return myIntraZoneParticipantRole;
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    protected TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    public StandardEdgeIPCEndpoint getMyIntraZoneTopologyEndpoint() {
        return (myIntraZoneTopologyEndpoint);
    }

    public StandardEdgeIPCEndpoint getMyInterZoneTopologyEndpoint() {
        return (myInterZoneTopologyEndpoint);
    }

    protected PegacornCommonInterfaceNames getInterfaceNames(){
        return(interfaceNames);
    }

    public String getMyIntraZoneIPCEndpointName() {
        return myIntraZoneIPCEndpointName;
    }

    public String getMyInterZoneIPCEndpointName() {
        return myInterZoneIPCEndpointName;
    }

    public String getMyIntraZoneOAMPubSubEndpointName() {
        return myIntraZoneOAMPubSubEndpointName;
    }

    public String getMyInterZoneOAMPubSubEndpointName() {
        return myInterZoneOAMPubSubEndpointName;
    }

    public String getMyIntraZoneOAMDiscoveryEndpointName() {
        return myIntraZoneOAMDiscoveryEndpointName;
    }

    public String getMyInterZoneOAMDiscoveryEndpointName() {
        return myInterZoneOAMDiscoveryEndpointName;
    }

    public String getMyIntraZoneIPCEndpointAddressName() {
        return myIntraZoneIPCEndpointAddressName;
    }

    public String getMyInterZoneIPCEndpointAddressName() {
        return myInterZoneIPCEndpointAddressName;
    }

    public String getMyIntraZoneOAMPubSubEndpointAddressName() {
        return myIntraZoneOAMPubSubEndpointAddressName;
    }

    public String getMyInterZoneOAMPubSubEndpointAddressName() {
        return myInterZoneOAMPubSubEndpointAddressName;
    }

    public String getMyIntraZoneOAMDiscoveryEndpointAddressName() {
        return myIntraZoneOAMDiscoveryEndpointAddressName;
    }

    public String getMyInterZoneOAMDiscoveryEndpointAddressName() {
        return myInterZoneOAMDiscoveryEndpointAddressName;
    }

    public String getInterZoneOAMGroupName() {
        return INTERZONE_OAM_GROUP_NAME;
    }

    public String getInterZoneIPCGroupName() {
        return INTERZONE_IPC_GROUP_NAME;
    }

    public String getIntraZoneOAMGroupName() {
        return INTRAZONE_OAM_GROUP_NAME;
    }

    public String getIntraZoneIPCGroupName() {
        return INTRAZONE_IPC_GROUP_NAME;
    }

    public String getInstanceQualifier(){
        return(this.instanceQualifier);
    }

    public Long getEndpointValidationStartDelay() {
        return ENDPOINT_VALIDATION_START_DELAY;
    }

    public Long getEndpointValidationPeriod() {
        return ENDPOINT_VALIDATION_PERIOD;
    }

    public String getInterSitePrefix() {
        return INTERSITE_PREFIX;
    }

    public String getIntraZonePrefix() {
        return INTRAZONE_PREFIX;
    }

    public String getInterZonePrefix() {
        return INTERZONE_PREFIX;
    }

    //
    // Deriving My Role
    //

    protected TopologyNodeFDNToken deriveAssociatedForwarderFDNToken(PetasosEndpointChannelScopeEnum forwarderScope){
        getLogger().info(".deriveAssociatedForwarderFDNToken(): Entry");
        String forwarderName;
        switch(forwarderScope){
            case ENDPOINT_CHANNEL_SCOPE_INTERSITE:
                forwarderName = INTERZONE_EDGE_FORWARDER_WUP_NAME;
                break;
            case ENDPOINT_CHANNEL_SCOPE_INTERZONE:
                forwarderName = INTERZONE_EDGE_FORWARDER_WUP_NAME;
                break;
            case ENDPOINT_CHANNEL_SCOPE_INTRAZONE:
            default:
                forwarderName = INTRAZONE_EDGE_FORWARDER_WUP_NAME;
                break;
        }
        TopologyNodeFDN workshopNodeFDN = deriveWorkshopFDN();
        TopologyNodeFDN wupNodeFDN = SerializationUtils.clone(workshopNodeFDN);
        wupNodeFDN.appendTopologyNodeRDN(new TopologyNodeRDN(TopologyNodeTypeEnum.WUP, forwarderName, EDGE_FORWARDER_WUP_VERSION));
        TopologyNodeFDNToken associatedForwarderWUPToken = wupNodeFDN.getToken();
        return(associatedForwarderWUPToken);
    }

    //
    // Resolve my Endpoint Details
    //

    protected StandardEdgeIPCEndpoint deriveTopologyEndpoint(PetasosTopologyEndpointTypeEnum requiredEndpointType, String interfaceName){
        getLogger().info(".deriveIPCTopologyEndpoint(): Entry, requiredEndpointType->{}, interfaceName->{}", requiredEndpointType, interfaceName);
        for(TopologyNodeFDN currentEndpointFDN: getProcessingPlant().getProcessingPlantNode().getEndpoints()){
            IPCTopologyEndpoint currentEndpoint = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
            getLogger().info(".deriveIPCTopologyEndpoint(): currentEndpoint->{}",currentEndpoint);
            PetasosTopologyEndpointTypeEnum endpointType = currentEndpoint.getEndpointType();
            boolean endpointTypeMatches = endpointType.equals(requiredEndpointType);
            if(endpointTypeMatches){
                if(currentEndpoint.getName().contentEquals(interfaceName)) {
                    StandardEdgeIPCEndpoint resolvedEndpoint = (StandardEdgeIPCEndpoint)currentEndpoint;
                    getLogger().info(".deriveIPCTopologyEndpoint(): Exit, found IPCTopologyEndpoint and assigned it, resolvedEndpoint->{}", resolvedEndpoint);
                    return(resolvedEndpoint);
                }
            }
        }
        getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, Could not find appropriate Endpoint");
        return(null);
    }

    //
    // Build a Participant
    //

    protected PubSubParticipant buildParticipant(PetasosEndpoint petasosEndpoint){
        getLogger().info(".buildParticipant(): Entry, petasosEndpoint->{}", petasosEndpoint);
        if(petasosEndpoint == null){
            getLogger().info(".buildParticipant(): Exit, petasosEndpoint is null");
            return(null);
        }
        // 1st, the IntraSubsystem Pub/Sub Participant} component
        getLogger().info(".initialise(): Now create my intraSubsystemParticipant (LocalPubSubPublisher)");
        TopologyNodeFDNToken topologyNodeFDNToken = deriveAssociatedForwarderFDNToken(petasosEndpoint.getEndpointScope());
        if(topologyNodeFDNToken == null){
            getLogger().info(".buildParticipant(): Exit, unable to resolve associatedForwarderFDNToken");
            return(null);
        }
        getLogger().info(".initialise(): localPublisher TopologyNodeFDNToken is ->{}", topologyNodeFDNToken);
        IntraSubsystemPubSubParticipant intraSubsystemParticipant = new IntraSubsystemPubSubParticipant(topologyNodeFDNToken);
        getLogger().info(".initialise(): intraSubsystemParticipant created -->{}", intraSubsystemParticipant);
        getLogger().info(".initialise(): Now create my PubSubParticipant");
        PubSubParticipant participant = new PubSubParticipant();
        getLogger().info(".initialise(): Add the intraSubsystemParticipant aspect to the participant");
        participant.setIntraSubsystemParticipant(intraSubsystemParticipant);

        // Now the InterSubsystem Pub/Sub Participant component
        getLogger().info(".initialise(): Create my interSubsystemParticipant aspect");
        InterSubsystemPubSubParticipant distributedPublisher = new InterSubsystemPubSubParticipant(petasosEndpoint);
        distributedPublisher.setUtilisationStatus(PubSubParticipantUtilisationStatusEnum.PUB_SUB_PARTICIPANT_NO_SUBSCRIBERS);
        distributedPublisher.setUtilisationUpdateDate(Date.from(Instant.now()));
        getLogger().info(".initialise(): distributedPublisher (DistributedPubSubPublisher) created ->{}", distributedPublisher);

        // Now assemble the "Participant"
        getLogger().info(".initialise(): Add the distributedPublisher aspect to the participant");
        participant.setInterSubsystemParticipant(distributedPublisher);
        getLogger().info(".initialise(): Exit, participant created, participant->{}", participant);
        return(participant);
    }

    public String deriveIPCEndpointNameFromPubSubEndpointName(String pubsubEndpointName){
        if(StringUtils.isEmpty(pubsubEndpointName)){
            return(null);
        }
        String ipcEndpointName = StringUtils.replace(pubsubEndpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName());
        return(ipcEndpointName);
    }

    public String derivePubSubEndpointNameFromIPCEndpointName(String ipcEndpointName){
        if(StringUtils.isEmpty(ipcEndpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(ipcEndpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName());
        return(pubsubEndpointName);
    }

    public String derivePubSubEndpointNameFromDiscoveryEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(endpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName());
        return(pubsubEndpointName);
    }

    public String deriveDiscoveryEndpointNameFromPubSubEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(endpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName());
        return(pubsubEndpointName);
    }

    public String deriveIPCEndpointNameFromDiscoveryEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(endpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName());
        return(pubsubEndpointName);
    }

    public String deriveDiscoveryEndpointNameFromIPCEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(endpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName());
        return(pubsubEndpointName);
    }

    //
    // Building the FDN
    //

    private TopologyNodeFDN deriveWorkshopFDN() {
        TopologyNodeFDN processingPlantFDN = getProcessingPlant().getProcessingPlantNode().getNodeFDN();
        TopologyNodeFDN futureWorkshopFDN = SerializationUtils.clone(processingPlantFDN);
        TopologyNodeRDN newRDN = new TopologyNodeRDN(TopologyNodeTypeEnum.WORKSHOP, DefaultWorkshopSetEnum.EDGE_WORKSHOP.getWorkshop(), getProcessingPlant().getProcessingPlantNode().getNodeRDN().getNodeVersion());
        futureWorkshopFDN.appendTopologyNodeRDN(newRDN);
        return(futureWorkshopFDN);
    }
}
