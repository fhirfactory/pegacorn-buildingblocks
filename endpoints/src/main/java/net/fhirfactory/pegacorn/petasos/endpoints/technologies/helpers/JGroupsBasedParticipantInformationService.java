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

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.PegacornSystemComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.petasos.ipc.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.IntraSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipantUtilisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.answer.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.DefaultWorkshopSetEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.endpoints.CoreSubsystemPetasosEndpointsWatchdog;
import net.fhirfactory.pegacorn.petasos.endpoints.map.PetasosEndpointMap;
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

    @Inject
    private EndpointNameUtilities endpointNameUtilities;

    private PubSubParticipant myPetasosParticipantRole;
    private StandardEdgeIPCEndpoint myPetasosTopologyEndpoint;
    private boolean initialised;

    private String myPetasosIPCEndpointName;
    private String myPetasosSubscriptionsEndpointName;
    private String myPetasosTopologyEndpointName;
    private String myPetasosAuditEndpointName;
    private String myPetasosInterceptionEndpointName;
    private String myPetasosTaskingEndpointName;
    private String myPetasosMetricsEndpointName;

    private String myPetasosIPCEndpointAddressName;
    private String myPetasosSubscriptionsEndpointAddressName;
    private String myPetasosTopologyEndpointAddressName;
    private String myPetasosAuditEndpointAddressName;
    private String myPetasosInterceptionEndpointAddressName;
    private String myPetasosTaskingEndpointAddressName;
    private String myPetasosMetricsEndpointAddressName;

    private String instanceQualifier;

    private static String PETASOS_EDGE_MESSAGE_FORWARDER_WUP_NAME = "PetasosIPCMessageForwardWUP";
    private static String EDGE_FORWARDER_WUP_VERSION = "1.0.0";

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
        //
        // IPC endpoints
        getLogger().info(".initialise(): [build myPetasosIPCEndpointName] start");
        String petasosIPCEndpointAddressName = getProcessingPlant().getIPCServiceName() + PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosIPCEndpointAddressName = petasosIPCEndpointAddressName;
        String petasosIPCName = getProcessingPlant().getIPCServiceName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosIPCEndpointName = petasosIPCName;
        getLogger().info(".initialise(): [build myPetasosIPCEndpointName] finish, myPetasosIPCEndpointName->{}", this.myPetasosIPCEndpointName);

        //
        // Subscription endpoints
        getLogger().info(".initialise(): [build myPetasosSubscriptionsEndpointName] start");
        String petasosOAMPubSubKey = getProcessingPlant().getIPCServiceName() + PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosSubscriptionsEndpointAddressName = petasosOAMPubSubKey;
        String petasosOAMPubSubName = getProcessingPlant().getIPCServiceName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosSubscriptionsEndpointName = petasosOAMPubSubName;
        getLogger().info(".initialise(): [build myPetasosSubscriptionsEndpointName] finish, myPetasosSubscriptionsEndpointName->{}", this.myPetasosSubscriptionsEndpointName);

        //
        // Topology Endpoints
        getLogger().info(".initialise(): [build myPetasosTopologyEndpointName] start");
        String petasosOAMTopologyKey = getProcessingPlant().getIPCServiceName() + PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosTopologyEndpointAddressName = petasosOAMTopologyKey;
        String petasosOAMTopologyName = getProcessingPlant().getIPCServiceName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosTopologyEndpointName = petasosOAMTopologyName;
        getLogger().info(".initialise(): [build myPetasosTopologyEndpointName] finish, myPetasosTopologyEndpointName->{}", this.myPetasosTopologyEndpointName);

        //
        // Audit endpoints
        getLogger().info(".initialise(): [build myPetasosAuditEndpointName] start");
        String petasosAuditKey = getProcessingPlant().getIPCServiceName() + PetasosEndpointFunctionTypeEnum.PETASOS_AUDIT_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosAuditEndpointAddressName = petasosAuditKey;
        String petasosAuditName = getProcessingPlant().getIPCServiceName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosAuditEndpointName = petasosAuditName;
        getLogger().info(".initialise(): [build myPetasosAuditEndpointName] finish, myPetasosAuditEndpointName->{}", this.myPetasosAuditEndpointAddressName);

        //
        // Interception endpoints
        getLogger().info(".initialise(): [build petasosInterceptionName] start");
        String petasosInterceptionKey = getProcessingPlant().getIPCServiceName() + PetasosEndpointFunctionTypeEnum.PETASOS_INTERCEPTION_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosInterceptionEndpointAddressName = petasosInterceptionKey;
        String petasosInterceptionName = getProcessingPlant().getIPCServiceName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosInterceptionEndpointName = petasosInterceptionName;
        getLogger().info(".initialise(): [build petasosInterceptionName] finish, petasosInterceptionName->{}", this.myPetasosInterceptionEndpointAddressName);

        //
        // Metrics endpoints
        getLogger().info(".initialise(): [build myIntraZoneMetricsEndpointAddressName] start");
        String petasosMetricsKey = getProcessingPlant().getIPCServiceName() + PetasosEndpointFunctionTypeEnum.PETASOS_METRICS_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosMetricsEndpointAddressName = petasosMetricsKey;
        String petasosMetricsName = getProcessingPlant().getIPCServiceName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosMetricsEndpointName = petasosMetricsName;
        getLogger().info(".initialise(): [build myIntraZoneMetricsEndpointAddressName] finish, myIntraZoneMetricsEndpointAddressName->{}", this.myPetasosMetricsEndpointAddressName);

        //
        // Tasking endpoints
        getLogger().info(".initialise(): [build myIntraZoneTaskingEndpointAddressName] start");
        String petasosTaskingKey = getProcessingPlant().getIPCServiceName() + PetasosEndpointFunctionTypeEnum.PETASOS_TASKING_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosTaskingEndpointAddressName = petasosTaskingKey;
        String petasosTaskingName = getProcessingPlant().getIPCServiceName() + "(" + getInstanceQualifier() + ")";
        this.myPetasosTaskingEndpointName = petasosTaskingName;
        getLogger().info(".initialise(): [build myIntraZoneTaskingEndpointAddressName] finish, myIntraZoneTaskingEndpointAddressName->{}", this.myPetasosTaskingEndpointAddressName);


        // Derive the Reference Endpoints
        getLogger().info(".initialise(): [resolving topology endpoint] Start");
        this.myPetasosTopologyEndpoint = deriveTopologyEndpoint(PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_MESSAGING_SERVICE, getInterfaceNames().getPetasosTopologyServicesEndpointName());
        getLogger().info(".initialise(): [resolving topology endpoint] finish, myInterZoneTopologyEndpoint->{}", this.myPetasosTopologyEndpoint);
        // Create the Participants
        getLogger().info(".initialise(): [create the interzone participant] Start");
        this.myPetasosParticipantRole = buildParticipant(coreSubsystemPetasosEndpointsWatchdog.getPetasosIPCMessagingEndpoint());
        getLogger().info(".initialise(): [create the interzone participant] Finish, myInterZoneParticipantRole->{}", this.myPetasosParticipantRole);

    }

    //
    // Address a Race Condition
    //

    public PubSubParticipant buildMyPetasosParticipantRole(PetasosEndpoint endpoint){
        if(this.myPetasosParticipantRole == null){
            this.myPetasosParticipantRole = buildParticipant(endpoint);
        }
        return(this.myPetasosParticipantRole);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public PubSubParticipant getMyPetasosParticipantRole() {
        if(this.myPetasosParticipantRole == null){
            this.myPetasosParticipantRole = buildParticipant(coreSubsystemPetasosEndpointsWatchdog.getPetasosIPCMessagingEndpoint());
        }
        return myPetasosParticipantRole;
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    protected TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    public StandardEdgeIPCEndpoint getMyPetasosTopologyEndpoint() {
        return (myPetasosTopologyEndpoint);
    }

    protected PegacornCommonInterfaceNames getInterfaceNames(){
        return(interfaceNames);
    }

    public String getMyPetasosIPCEndpointName() {
        return myPetasosIPCEndpointName;
    }


    public String getMyPetasosSubscriptionsEndpointName() {
        return myPetasosSubscriptionsEndpointName;
    }

    public String getMyPetasosTopologyEndpointName() {
        return myPetasosTopologyEndpointName;
    }

    public String getMyPetasosIPCEndpointAddressName() {
        return myPetasosIPCEndpointAddressName;
    }

    public String getMyPetasosSubscriptionsEndpointAddressName() {
        return myPetasosSubscriptionsEndpointAddressName;
    }

    public String getMyPetasosTopologyEndpointAddressName() {
        return myPetasosTopologyEndpointAddressName;
    }

    public String getMyPetasosAuditEndpointName() {
        return myPetasosAuditEndpointName;
    }

    public String getMyPetasosInterceptionEndpointName() {
        return myPetasosInterceptionEndpointName;
    }

    public String getMyPetasosTaskingEndpointName() {
        return myPetasosTaskingEndpointName;
    }

    public String getMyPetasosMetricsEndpointName() {
        return myPetasosMetricsEndpointName;
    }

    public String getMyPetasosAuditEndpointAddressName() {
        return myPetasosAuditEndpointAddressName;
    }

    public String getMyPetasosInterceptionEndpointAddressName() {
        return myPetasosInterceptionEndpointAddressName;
    }

    public String getMyPetasosTaskingEndpointAddressName() {
        return myPetasosTaskingEndpointAddressName;
    }

    public String getMyPetasosMetricsEndpointAddressName() {
        return myPetasosMetricsEndpointAddressName;
    }

    public String getPetasosTopologyServicesGroupName() {
        return endpointNameUtilities.getPetasosTopologyServicesGroupName();
    }

    public String getPetasosSubscriptionServicesGroupName() {
        return (endpointNameUtilities.getPetasosSubscriptionsServicesGroupName());
    }

    public String getPetasosTaskServicesGroupName() {
        return (endpointNameUtilities.getPetasosTaskServicesGroupName());
    }

    public String getPetasosInterceptionGroupName() {
        return (endpointNameUtilities.getPetasosInterceptionGroupName());
    }

    public String getPetasosIpcMessagingGroupName(){
        return(endpointNameUtilities.getPetasosIpcMessagingGroupName());
    }

    public String getPetasosMetricsGroupName() {
        return (endpointNameUtilities.getPetasosMetricsGroupName());
    }

    public String getPetasosAuditGroupName() {
        return (endpointNameUtilities.getPetasosAuditServicesGroupName());
    }

    public String getPetaosIPCMessagingGroupName() {
        return (endpointNameUtilities.getPetasosIpcMessagingGroupName());
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

    //
    // Deriving My Role
    //

    protected TopologyNodeFDNToken deriveAssociatedForwarderFDNToken(){
        getLogger().info(".deriveAssociatedForwarderFDNToken(): Entry");
        TopologyNodeFDN workshopNodeFDN = deriveWorkshopFDN();
        TopologyNodeFDN wupNodeFDN = SerializationUtils.clone(workshopNodeFDN);
        wupNodeFDN.appendTopologyNodeRDN(new TopologyNodeRDN(PegacornSystemComponentTypeTypeEnum.WUP, PETASOS_EDGE_MESSAGE_FORWARDER_WUP_NAME, EDGE_FORWARDER_WUP_VERSION));
        TopologyNodeFDNToken associatedForwarderWUPToken = wupNodeFDN.getToken();
        return(associatedForwarderWUPToken);
    }

    //
    // Resolve my Endpoint Details
    //

    protected StandardEdgeIPCEndpoint deriveTopologyEndpoint(PetasosEndpointTopologyTypeEnum requiredEndpointType, String interfaceName){
        getLogger().info(".deriveIPCTopologyEndpoint(): Entry, requiredEndpointType->{}, interfaceName->{}", requiredEndpointType, interfaceName);
        for(TopologyNodeFDN currentEndpointFDN: getProcessingPlant().getProcessingPlantNode().getEndpoints()){
            IPCTopologyEndpoint currentEndpoint = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
            getLogger().info(".deriveIPCTopologyEndpoint(): currentEndpoint->{}",currentEndpoint);
            PetasosEndpointTopologyTypeEnum endpointType = currentEndpoint.getEndpointType();
            boolean endpointTypeMatches = endpointType.equals(requiredEndpointType);
            if(endpointTypeMatches){
                if(currentEndpoint.getEndpointConfigurationName().contentEquals(interfaceName)) {
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
        TopologyNodeFDNToken topologyNodeFDNToken = deriveAssociatedForwarderFDNToken();
        if(topologyNodeFDNToken == null){
            getLogger().info(".buildParticipant(): Exit, unable to resolve associatedForwarderFDNToken");
            return(null);
        }
        getLogger().info(".initialise(): localPublisher TopologyNodeFDNToken is ->{}", topologyNodeFDNToken);
        IntraSubsystemPubSubParticipant intraSubsystemParticipant = new IntraSubsystemPubSubParticipant(petasosEndpoint.getEndpointID().getEndpointComponentID());
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
                PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT.getDisplayName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getDisplayName());
        return(ipcEndpointName);
    }

    public String derivePubSubEndpointNameFromIPCEndpointName(String ipcEndpointName){
        if(StringUtils.isEmpty(ipcEndpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(ipcEndpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getDisplayName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT.getDisplayName());
        return(pubsubEndpointName);
    }

    public String derivePubSubEndpointNameFromDiscoveryEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(endpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT.getDisplayName());
        return(pubsubEndpointName);
    }

    public String deriveDiscoveryEndpointNameFromPubSubEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(endpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT.getDisplayName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName());
        return(pubsubEndpointName);
    }

    public String deriveIPCEndpointNameFromDiscoveryEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(endpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getDisplayName());
        return(pubsubEndpointName);
    }

    public String deriveDiscoveryEndpointNameFromIPCEndpointName(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String pubsubEndpointName = StringUtils.replace(endpointName,
                PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getDisplayName(),
                PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName());
        return(pubsubEndpointName);
    }

    //
    // Building the FDN
    //

    private TopologyNodeFDN deriveWorkshopFDN() {
        TopologyNodeFDN processingPlantFDN = getProcessingPlant().getProcessingPlantNode().getComponentFDN();
        TopologyNodeFDN futureWorkshopFDN = SerializationUtils.clone(processingPlantFDN);
        TopologyNodeRDN newRDN = new TopologyNodeRDN(PegacornSystemComponentTypeTypeEnum.WORKSHOP, DefaultWorkshopSetEnum.EDGE_WORKSHOP.getWorkshop(), getProcessingPlant().getProcessingPlantNode().getComponentRDN().getNodeVersion());
        futureWorkshopFDN.appendTopologyNodeRDN(newRDN);
        return(futureWorkshopFDN);
    }
}
