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
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointChannelScopeEnum;
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

    private PubSubParticipant myInterZoneParticipantRole;
    private PubSubParticipant myIntraZoneParticipantRole;
    private StandardEdgeIPCEndpoint myIntraZoneTopologyEndpoint;
    private StandardEdgeIPCEndpoint myInterZoneTopologyEndpoint;
    private boolean initialised;
    private String myIntraZoneIPCEndpointName;
    private String myInterZoneIPCEndpointName;
    private String myIntraZoneSubscriptionsEndpointName;
    private String myInterZoneSubscriptionsEndpointName;
    private String myIntraZoneTopologyEndpointName;
    private String myInterZoneTopologyEndpointName;
    private String myIntraZoneAuditEndpointName;
    private String myInterZoneAuditEndpointName;
    private String myIntraZoneInterceptionEndpointName;
    private String myInterZoneInterceptionEndpointName;
    private String myIntraZoneTaskingEndpointName;
    private String myInterZoneTaskingEndpointName;
    private String myIntraZoneMetricsEndpointName;
    private String myInterZoneMetricsEndpointName;
    private String myIntraZoneIPCEndpointAddressName;
    private String myInterZoneIPCEndpointAddressName;
    private String myIntraZoneSubscriptionsEndpointAddressName;
    private String myInterZoneSubscriptionsEndpointAddressName;
    private String myIntraZoneTopologyEndpointAddressName;
    private String myInterZoneTopologyEndpointAddressName;
    private String myIntraZoneAuditEndpointAddressName;
    private String myInterZoneAuditEndpointAddressName;
    private String myIntraZoneInterceptionEndpointAddressName;
    private String myInterZoneInterceptionEndpointAddressName;
    private String myIntraZoneTaskingEndpointAddressName;
    private String myInterZoneTaskingEndpointAddressName;
    private String myIntraZoneMetricsEndpointAddressName;
    private String myInterZoneMetricsEndpointAddressName;

    private String instanceQualifier;

    private static String INTRAZONE_EDGE_FORWARDER_WUP_NAME = "EdgeIntraZoneMessageForwardWUP";
    private static String INTERZONE_EDGE_FORWARDER_WUP_NAME = "EdgeInterZoneMessageForwardWUP";
    private static String EDGE_FORWARDER_WUP_VERSION = "1.0.0";

    private static String INTERSITE_PREFIX = ".InterSite";
    private static String INTRAZONE_PREFIX = ".IntraZone";
    private static String INTERZONE_PREFIX = ".InterZone";

    private static String INTERZONE_TOPOLOGY_GROUP_NAME = "InterZone.Topology";
    private static String INTRAZONE_TOPOLOGY_GROUP_NAME = "IntraZone.Topology";
    private static String INTERZONE_SUBSCRIPTIONS_GROUP_NAME = "InterZone.Subscriptions";
    private static String INTRAZONE_SUBSCRIPTIONS_GROUP_NAME = "IntraZone.Subscriptions";
    private static String INTERZONE_TASKING_GROUP_NAME = "InterZone.Tasking";
    private static String INTRAZONE_TASKING_GROUP_NAME = "IntraZone.Tasking";
    private static String INTERZONE_INTERCEPTION_GROUP_NAME = "InterZone.Interception";
    private static String INTRAZONE_INTERCEPTION_GROUP_NAME = "IntraZone.Interception";
    private static String INTERZONE_METRICS_GROUP_NAME = "InterZone.Metrics";
    private static String INTRAZONE_METRICS_GROUP_NAME = "IntraZone.Metrics";
    private static String INTERZONE_AUDIT_GROUP_NAME = "InterZone.Audit";
    private static String INTRAZONE_AUDIT_GROUP_NAME = "IntraZone.Audit";
    private static String INTERZONE_IPC_GROUP_NAME = "InterZone.IPC";
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
        //
        // IPC endpoints
        getLogger().info(".initialise(): [build myIntraZoneIPCEndpointName] start");
        String intraZoneIPCKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneIPCEndpointAddressName = intraZoneIPCKey;
        String intraZoneIPCName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneIPCEndpointName = intraZoneIPCName;
        getLogger().info(".initialise(): [build myIntraZoneIPCEndpointName] finish, myIntraZoneIPCEndpointName->{}", this.myIntraZoneIPCEndpointName);

        getLogger().info(".initialise(): [build myInterZoneIPCEndpointName] start");
        String interZoneIPCKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX +  PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneIPCEndpointAddressName = interZoneIPCKey;
        String interZoneIPCName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX +  "(" + getInstanceQualifier() + ")";
        this.myInterZoneIPCEndpointName = interZoneIPCName;
        getLogger().info(".initialise(): [build myInterZoneIPCEndpointName] finish, myInterZoneIPCEndpointName->{}", this.myInterZoneIPCEndpointName);

        //
        // Subscription endpoints
        getLogger().info(".initialise(): [build myIntraZoneSubscriptionsEndpointName] start");
        String intraZoneOAMPubSubKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX +  PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneSubscriptionsEndpointAddressName = intraZoneOAMPubSubKey;
        String intraZoneOAMPubSubName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneSubscriptionsEndpointName = intraZoneOAMPubSubName;
        getLogger().info(".initialise(): [build myIntraZoneSubscriptionsEndpointName] finish, myIntraZoneSubscriptionsEndpointName->{}", this.myIntraZoneSubscriptionsEndpointName);

        getLogger().info(".initialise(): [build myInterZoneSubscriptionsEndpointName] start");
        String interZoneOAMPubSubKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneSubscriptionsEndpointAddressName = interZoneOAMPubSubKey;
        String interZoneOAMPubSubName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myInterZoneSubscriptionsEndpointName = interZoneOAMPubSubName;
        getLogger().info(".initialise(): [build myInterZoneSubscriptionsEndpointName] finish, myInterZoneSubscriptionsEndpointName->{}", this.myInterZoneSubscriptionsEndpointName);

        //
        // Topology Endpoints
        getLogger().info(".initialise(): [build myIntraZoneTopologyEndpointAddressName] start");
        String intraZoneOAMTopologyKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneTopologyEndpointAddressName = intraZoneOAMTopologyKey;
        String intraZoneOAMTopologyName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneTopologyEndpointName = intraZoneOAMTopologyName;
        getLogger().info(".initialise(): [build myIntraZoneTopologyEndpointAddressName] finish, myIntraZoneTopologyEndpointAddressName->{}", this.myIntraZoneTopologyEndpointName);

        getLogger().info(".initialise(): [build myInterZoneTopologyEndpointAddressName] start");
        String interZoneOAMTopologyKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneTopologyEndpointAddressName = interZoneOAMTopologyKey;
        String interZoneOAMTopologyName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myInterZoneTopologyEndpointName = interZoneOAMTopologyName;
        getLogger().info(".initialise(): [build myInterZoneTopologyEndpointAddressName] finish, myInterZoneTopologyEndpointAddressName->{}", this.myInterZoneTopologyEndpointName);

        //
        // Audit endpoints
        getLogger().info(".initialise(): [build myIntraZoneAuditEndpointAddressName] start");
        String intraZoneAuditKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_AUDIT_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneAuditEndpointAddressName = intraZoneAuditKey;
        String intraZoneAuditName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneAuditEndpointName = intraZoneAuditName;
        getLogger().info(".initialise(): [build myIntraZoneAuditEndpointAddressName] finish, myIntraZoneAuditEndpointAddressName->{}", this.myIntraZoneAuditEndpointAddressName);

        getLogger().info(".initialise(): [build myInterZoneAuditEndpointAddressName] start");
        String interZoneAuditKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_AUDIT_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneAuditEndpointAddressName = interZoneAuditKey;
        String interZoneAuditName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myInterZoneAuditEndpointName = interZoneAuditName;
        getLogger().info(".initialise(): [build myInterZoneAuditEndpointAddressName] finish, myInterZoneAuditEndpointAddressName->{}", this.myInterZoneAuditEndpointAddressName);

        //
        // Interception endpoints
        getLogger().info(".initialise(): [build myIntraZoneInterceptionEndpointAddressName] start");
        String intraZoneInterceptionKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_INTERCEPTION_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneInterceptionEndpointAddressName = intraZoneInterceptionKey;
        String intraZoneInterceptionName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneInterceptionEndpointName = intraZoneInterceptionName;
        getLogger().info(".initialise(): [build myIntraZoneInterceptionEndpointAddressName] finish, myIntraZoneInterceptionEndpointAddressName->{}", this.myIntraZoneInterceptionEndpointAddressName);

        getLogger().info(".initialise(): [build myInterZoneInterceptionEndpointAddressName] start");
        String interZoneInterceptionKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_INTERCEPTION_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneInterceptionEndpointAddressName = interZoneInterceptionKey;
        String interZoneInterceptionName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myInterZoneInterceptionEndpointName = interZoneInterceptionName;
        getLogger().info(".initialise(): [build myInterZoneInterceptionEndpointAddressName] finish, myInterZoneInterceptionEndpointAddressName->{}", this.myInterZoneInterceptionEndpointAddressName);

        //
        // Metrics endpoints
        getLogger().info(".initialise(): [build myIntraZoneMetricsEndpointAddressName] start");
        String intraZoneMetricsKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_METRICS_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneMetricsEndpointAddressName = intraZoneMetricsKey;
        String intraZoneMetricsName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneMetricsEndpointName = intraZoneMetricsName;
        getLogger().info(".initialise(): [build myIntraZoneMetricsEndpointAddressName] finish, myIntraZoneMetricsEndpointAddressName->{}", this.myIntraZoneMetricsEndpointAddressName);

        getLogger().info(".initialise(): [build myInterZoneMetricsEndpointAddressName] start");
        String interZoneMetricsKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_METRICS_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneMetricsEndpointAddressName = interZoneMetricsKey;
        String interZoneMetricsName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myInterZoneMetricsEndpointName = interZoneMetricsName;
        getLogger().info(".initialise(): [build myInterZoneMetricsEndpointAddressName] finish, myInterZoneMetricsEndpointAddressName->{}", this.myInterZoneMetricsEndpointAddressName);

        //
        // Tasking endpoints
        getLogger().info(".initialise(): [build myIntraZoneTaskingEndpointAddressName] start");
        String intraZoneTaskingKey = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_TASKING_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneTaskingEndpointAddressName = intraZoneTaskingKey;
        String intraZoneTaskingName = getProcessingPlant().getIPCServiceName() + INTRAZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myIntraZoneTaskingEndpointName = intraZoneTaskingName;
        getLogger().info(".initialise(): [build myIntraZoneTaskingEndpointAddressName] finish, myIntraZoneTaskingEndpointAddressName->{}", this.myIntraZoneTaskingEndpointAddressName);

        getLogger().info(".initialise(): [build myInterZoneTaskingEndpointAddressName] start");
        String interZoneTaskingKey = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + PetasosEndpointFunctionTypeEnum.PETASOS_TASKING_ENDPOINT.getDisplayName() + "(" + getInstanceQualifier() + ")";
        this.myInterZoneTaskingEndpointAddressName = interZoneTaskingKey;
        String interZoneTaskingName = getProcessingPlant().getIPCServiceName() + INTERZONE_PREFIX + "(" + getInstanceQualifier() + ")";
        this.myInterZoneTaskingEndpointName = interZoneTaskingName;
        getLogger().info(".initialise(): [build myInterZoneTaskingEndpointAddressName] finish, myInterZoneTaskingEndpointAddressName->{}", this.myInterZoneTaskingEndpointAddressName);



        // Derive the Reference Endpoints
        getLogger().info(".initialise(): [resolving interzone topology endpoint] Start");
        this.myInterZoneTopologyEndpoint = deriveTopologyEndpoint(PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_INTERZONE_SERVICE, getInterfaceNames().getInterZoneJGroupsTopologyEndpointName());
        getLogger().info(".initialise(): [resolving interzone topology endpoint] finish, myInterZoneTopologyEndpoint->{}", this.myInterZoneTopologyEndpoint);
        getLogger().info(".initialise(): [resolving intrazone topology endpoint] Start");
        this.myIntraZoneTopologyEndpoint = deriveTopologyEndpoint(PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_INTRAZONE_SERVICE, getInterfaceNames().getIntraZoneJGroupsTopologyEndpointName());
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

    public String getMyIntraZoneSubscriptionsEndpointName() {
        return myIntraZoneSubscriptionsEndpointName;
    }

    public String getMyInterZoneSubscriptionsEndpointName() {
        return myInterZoneSubscriptionsEndpointName;
    }

    public String getMyIntraZoneTopologyEndpointName() {
        return myIntraZoneTopologyEndpointName;
    }

    public String getMyInterZoneTopologyEndpointName() {
        return myInterZoneTopologyEndpointName;
    }

    public String getMyIntraZoneIPCEndpointAddressName() {
        return myIntraZoneIPCEndpointAddressName;
    }

    public String getMyInterZoneIPCEndpointAddressName() {
        return myInterZoneIPCEndpointAddressName;
    }

    public String getMyIntraZoneSubscriptionsEndpointAddressName() {
        return myIntraZoneSubscriptionsEndpointAddressName;
    }

    public String getMyInterZoneSubscriptionsEndpointAddressName() {
        return myInterZoneSubscriptionsEndpointAddressName;
    }

    public String getMyIntraZoneTopologyEndpointAddressName() {
        return myIntraZoneTopologyEndpointAddressName;
    }

    public String getMyInterZoneTopologyEndpointAddressName() {
        return myInterZoneTopologyEndpointAddressName;
    }

    public String getInterZoneTopologyGroupName() {
        return INTERZONE_TOPOLOGY_GROUP_NAME;
    }

    public String getInterZoneIPCGroupName() {
        return INTERZONE_IPC_GROUP_NAME;
    }

    public String getIntraZoneTopologyGroupName() {
        return INTRAZONE_TOPOLOGY_GROUP_NAME;
    }

    public String getIntraZoneIPCGroupName() {
        return INTRAZONE_IPC_GROUP_NAME;
    }


    public String getMyIntraZoneAuditEndpointName() {
        return myIntraZoneAuditEndpointName;
    }

    public String getMyInterZoneAuditEndpointName() {
        return myInterZoneAuditEndpointName;
    }

    public String getMyIntraZoneInterceptionEndpointName() {
        return myIntraZoneInterceptionEndpointName;
    }

    public String getMyInterZoneInterceptionEndpointName() {
        return myInterZoneInterceptionEndpointName;
    }

    public String getMyIntraZoneTaskingEndpointName() {
        return myIntraZoneTaskingEndpointName;
    }

    public String getMyInterZoneTaskingEndpointName() {
        return myInterZoneTaskingEndpointName;
    }

    public String getMyIntraZoneMetricsEndpointName() {
        return myIntraZoneMetricsEndpointName;
    }

    public String getMyInterZoneMetricsEndpointName() {
        return myInterZoneMetricsEndpointName;
    }

    public String getMyIntraZoneAuditEndpointAddressName() {
        return myIntraZoneAuditEndpointAddressName;
    }

    public String getMyInterZoneAuditEndpointAddressName() {
        return myInterZoneAuditEndpointAddressName;
    }

    public String getMyIntraZoneInterceptionEndpointAddressName() {
        return myIntraZoneInterceptionEndpointAddressName;
    }

    public String getMyInterZoneInterceptionEndpointAddressName() {
        return myInterZoneInterceptionEndpointAddressName;
    }

    public String getMyIntraZoneTaskingEndpointAddressName() {
        return myIntraZoneTaskingEndpointAddressName;
    }

    public String getMyInterZoneTaskingEndpointAddressName() {
        return myInterZoneTaskingEndpointAddressName;
    }

    public String getMyIntraZoneMetricsEndpointAddressName() {
        return myIntraZoneMetricsEndpointAddressName;
    }

    public String getMyInterZoneMetricsEndpointAddressName() {
        return myInterZoneMetricsEndpointAddressName;
    }

    public String getInterzoneTopologyGroupName() {
        return INTERZONE_TOPOLOGY_GROUP_NAME;
    }

    public String getIntrazoneTopologyGroupName() {
        return INTRAZONE_TOPOLOGY_GROUP_NAME;
    }

    public String getInterzoneSubscriptionsGroupName() {
        return INTERZONE_SUBSCRIPTIONS_GROUP_NAME;
    }

    public String getIntrazoneSubscriptionsGroupName() {
        return INTRAZONE_SUBSCRIPTIONS_GROUP_NAME;
    }

    public String getInterzoneTaskingGroupName() {
        return INTERZONE_TASKING_GROUP_NAME;
    }

    public String getIntrazoneTaskingGroupName() {
        return INTRAZONE_TASKING_GROUP_NAME;
    }

    public String getInterzoneInterceptionGroupName() {
        return INTERZONE_INTERCEPTION_GROUP_NAME;
    }

    public String getIntrazoneInterceptionGroupName() {
        return INTRAZONE_INTERCEPTION_GROUP_NAME;
    }

    public String getInterzoneMetricsGroupName() {
        return INTERZONE_METRICS_GROUP_NAME;
    }

    public String getIntrazoneMetricsGroupName() {
        return INTRAZONE_METRICS_GROUP_NAME;
    }

    public String getInterzoneAuditGroupName() {
        return INTERZONE_AUDIT_GROUP_NAME;
    }

    public String getIntrazoneAuditGroupName() {
        return INTRAZONE_AUDIT_GROUP_NAME;
    }

    public String getInterzoneIpcGroupName() {
        return INTERZONE_IPC_GROUP_NAME;
    }

    public String getIntrazoneIpcGroupName() {
        return INTRAZONE_IPC_GROUP_NAME;
    }

    public String getIntrasiteOamGroupName() {
        return INTRASITE_OAM_GROUP_NAME;
    }

    public String getIntrasiteIpcGroupName() {
        return INTRASITE_IPC_GROUP_NAME;
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
        wupNodeFDN.appendTopologyNodeRDN(new TopologyNodeRDN(PegacornSystemComponentTypeTypeEnum.WUP, forwarderName, EDGE_FORWARDER_WUP_VERSION));
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
        TopologyNodeFDNToken topologyNodeFDNToken = deriveAssociatedForwarderFDNToken(petasosEndpoint.getEndpointScope());
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
