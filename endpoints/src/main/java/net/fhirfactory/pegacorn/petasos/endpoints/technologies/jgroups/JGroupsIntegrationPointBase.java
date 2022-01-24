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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups;

import net.fhirfactory.pegacorn.core.constants.petasos.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantRoleSupportInterface;
import net.fhirfactory.pegacorn.core.interfaces.edge.PetasosServicesEndpointRegistrationService;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.ipc.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.ProcessingPlantPetasosParticipantHolder;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointProbeQuery;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointProbeReport;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.map.JGroupsIntegrationPointCheckScheduleMap;
import net.fhirfactory.pegacorn.petasos.endpoints.map.JGroupsIntegrationPointSharedMap;
import net.fhirfactory.pegacorn.petasos.endpoints.map.datatypes.JGroupsIntegrationPointCheckScheduleElement;
import net.fhirfactory.pegacorn.petasos.endpoints.services.common.ProcessingPlantJGroupsIntegrationPointInformationService;
import net.fhirfactory.pegacorn.petasos.endpoints.services.common.ProcessingPlantJGroupsIntegrationPointSet;
import net.fhirfactory.pegacorn.petasos.endpoints.services.common.ProcessingPlantJGroupsIntegrationPointWatchdog;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.oam.metrics.PetasosMetricAgentFactory;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.EndpointMetricsAgent;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class JGroupsIntegrationPointBase extends JGroupsIntegrationPointAdapterBase {

    private boolean endpointCheckScheduled;
    private JGroupsIntegrationPointCheckScheduleMap integrationPointCheckScheduleMap;
    private EndpointMetricsAgent metricsAgent;

    private int MAX_PROBE_RETRIES = 5;

    @Inject
    private JGroupsIntegrationPointSharedMap endpointMap;

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    @Inject
    private ProcessingPlantJGroupsIntegrationPointInformationService jgroupsParticipantInformationService;

    @Inject
    private ProcessingPlantJGroupsIntegrationPointWatchdog coreSubsystemPetasosEndpointsWatchdog;

    @Inject
    private ProcessingPlantPetasosParticipantHolder participantHolder;

    @Inject
    private ProcessingPlantJGroupsIntegrationPointSet jgroupsIPSet;

    @Inject
    private PetasosServicesEndpointRegistrationService endpointRegistrationService;

    @Inject
    private PetasosMetricAgentFactory metricsFactory;

    @Inject
    private ProcessingPlantRoleSupportInterface processingPlantCapabilityStatement;

    //
    // Constructor
    //

    public JGroupsIntegrationPointBase(){
        super();
        endpointCheckScheduled = false;
        integrationPointCheckScheduleMap = new JGroupsIntegrationPointCheckScheduleMap();
    }

    //
    // Abstract Methods
    //

    abstract protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType();
    abstract protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType();
    abstract protected void addIntegrationPointToJGroupsIntegrationPointSet();
    abstract protected void executePostConstructActivities();

    abstract protected void doIntegrationPointBusinessFunctionCheck(JGroupsIntegrationPointSummary integrationPointSummary, boolean isRemoved, boolean isAdded);

    //
    // Implemented Abstract Methods
    //

    @Override
    protected String specifyJGroupsChannelName() {
        getLogger().debug(".specifyJGroupsChannelName(): Entry, getJGroupsIntegrationPoint()->", getJGroupsIntegrationPoint());
        String channelName = getJGroupsIntegrationPoint().getChannelName();
        return(channelName);
    }

    //
    // PostConstruct Initialisation
    //

    @PostConstruct
    public void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (isInitialised()) {
            getLogger().debug(".initialise(): Exit, already initialised!");
            return;
        }
        // 1st, Derive my Endpoint (Topology)
        getLogger().info(".initialise(): Step 1: Start ==> Get my IPCEndpoint Detail");
        setJGroupsIntegrationPoint(resolveTopologyNodeForJGroupsIntegrationPoint());
        getLogger().info(".initialise(): Step 1: Complete ==> IPCEndpoint derived ->{}", getJGroupsIntegrationPoint());

        // 2nd, the Register/Update the IntegrationPoint Set
        getLogger().info(".initialise(): Step 2: Start ==> Add IntegrationPoint to ProcessingPlant's IntegrationPoint Set");
        addIntegrationPointToJGroupsIntegrationPointSet();
        getLogger().info(".initialise(): Step 2: Completed ==> Add IntegrationPoint to ProcessingPlant's IntegrationPoint Set");

        // 3rd, Initialise my JChannel
        getLogger().info(".initialise(): Step 3: Start ==> Initialise my JChannel Connection & Join Cluster/Group");
        establishJChannel();
        getLogger().info(".initialise(): Step 3: Completed ==> ipcChannel ->{}", getIPCChannel());

        //
        // 4th, Our Endpoint is Operational, So Assign Status
        //
        getLogger().info(".initialise(): Step 4: Start ==> Update my JGroupsIntegrationPoint status to OPERATIONAL");
        getJGroupsIntegrationPoint().setEndpointStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
        getLogger().info(".initialise(): Step 4: Completed ==> Update my JGroupsIntegrationPoint status to OPERATIONAL");
        //
        // 5th, Do an initial endpoint scan
        //
        getLogger().info(".initialise(): Step 5: Start ==> Schedule a general IntegrationPoint scan");
        scheduleEndpointScan();
        getLogger().info(".initialise(): Step 5: Completed ==> Schedule a general IntegrationPoint scan");
        //
        // 6th, Now kickstart the ongoing Endpoint Validation Process
        //
        getLogger().info(".initialise(): Step 6: Start ==> Schedule general IntegrationPoint validation watchdog");
        scheduleEndpointValidation();
        getLogger().info(".initialise(): Step 6: Completed ==> Schedule general IntegrationPoint validation watchdog");
        //
        // 7th, Call any subclass PostConstruct methods.
        //
        getLogger().info(".initialise(): Step 7: Start ==> Executing subclass PostConstruct activities");
        executePostConstructActivities();
        getLogger().info(".initialise(): Step 7: Completed ==> Executing subclass PostConstruct activities");
        //
        // Register myself with a WUP for Metrics Reporting
        //
        getLogger().info(".initialise(): Step 8: Start ==> Registering with WUP for Metrics");
        endpointRegistrationService.registerEndpoint(specifyPetasosEndpointFunctionType(), getJGroupsIntegrationPoint());
        getLogger().info(".initialise(): Step 8: Finish ==> Registering with WUP for Metrics");
        //
        // Create my Metrics Agent
        //
        getLogger().info(".initialise(): Step 8: Start ==> Registering with WUP for Metrics");
        String participantName = getProcessingPlant().getSubsystemParticipantName()+"."+specifyPetasosEndpointFunctionType().getEndpointParticipantName();
        getLogger().info(".initialise(): Step 8: participantName->{}", participantName);
        this.metricsAgent = metricsFactory.newEndpointMetricsAgent(processingPlantCapabilityStatement, getJGroupsIntegrationPoint().getComponentID(),participantName, "Internal", specifyJGroupsChannelName());
        getLogger().info(".initialise(): Step 8: Finish ==> Registering with WUP for Metrics");
        // We're done!
        setInitialised(true);

    }

    //
    // Getters (and Setters)
    //

    protected EndpointMetricsAgent getMetricsAgent(){
        return(this.metricsAgent);
    }

    protected JGroupsIntegrationPointSharedMap getIntegrationPointMap(){
        return(endpointMap);
    }

    protected PetasosEndpointFunctionTypeEnum getPetasosEndpointFunctionType(){
        return(specifyPetasosEndpointFunctionType());
    }

    protected EndpointPayloadTypeEnum getPetasosEndpointPayloadType(){
        return(specifyPetasosEndpointPayloadType());
    }

    protected PegacornCommonInterfaceNames getInterfaceNames(){
        return(interfaceNames);
    }

    protected PegacornIPCCommonValues getIPCComponentNames() {
        return ipcFunctionalityNames;
    }

    protected ProcessingPlantJGroupsIntegrationPointInformationService getJgroupsParticipantInformationService() {
        return jgroupsParticipantInformationService;
    }

    protected ProcessingPlantJGroupsIntegrationPointWatchdog getCoreSubsystemPetasosEndpointsWatchdog(){
        return(coreSubsystemPetasosEndpointsWatchdog);
    }

    protected ProcessingPlantPetasosParticipantHolder getParticipantHolder(){
        return(participantHolder);
    }

    protected ProcessingPlantJGroupsIntegrationPointSet getJgroupsIPSet() {
        return jgroupsIPSet;
    }

    //
    // JGroups Integration Point Probe (rpc and handler)
    //

    /**
     *
     * @param targetIntegrationPoint
     * @return
     */
    public JGroupsIntegrationPointProbeReport probeJGroupsIntegrationPoint(JGroupsIntegrationPointSummary targetIntegrationPoint){
        getLogger().debug(".probeJGroupsIntegrationPoint(): Entry, targetIntegrationPoint->{}", targetIntegrationPoint);
        JGroupsIntegrationPointProbeQuery query = createJGroupsIPQuery(getJGroupsIntegrationPoint());
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = query;
            classSet[0] = JGroupsIntegrationPointProbeQuery.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Address endpointAddress = getTargetMemberAddress(targetIntegrationPoint.getChannelName());
            JGroupsIntegrationPointProbeReport report = null;
            synchronized(getIPCChannelLock()) {
                report = getRPCDispatcher().callRemoteMethod(endpointAddress, "probeJGroupsIntegrationPointHandler", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".probeJGroupsIntegrationPoint(): Exit, report->{}", report);
            return(report);
        } catch (NoSuchMethodException e) {
            getLogger().error(".probeJGroupsIntegrationPoint(): Error (NoSuchMethodException)->", e);
            return(null);
        } catch (Exception e) {
            getLogger().error(".probeJGroupsIntegrationPoint: Error (GeneralException) ->",e);
            return(null);
        }
    }

    /**
     *
     * @param sourceIntegrationPoint
     * @return
     */
    public JGroupsIntegrationPointProbeReport probeJGroupsIntegrationPointHandler(JGroupsIntegrationPointProbeQuery sourceIntegrationPoint){
        getLogger().debug(".probeJGroupsIntegrationPointHandler(): Entry, sourceIntegrationPoint->{}", sourceIntegrationPoint);
        getIntegrationPointMap().addJGroupsIntegrationPoint(sourceIntegrationPoint);
        JGroupsIntegrationPointProbeReport report = createJGroupsIPReport(getJGroupsIntegrationPoint());
        getLogger().debug(".probeJGroupsIntegrationPointHandler(): Exit, report->{}", report);
        return(report);
    }

    //
    // Subsystem Name Derivation
    //

    @Override
    protected String deriveIntegrationPointSubsystemName(String endpointName) {
        getLogger().debug(".deriveIntegrationPointSubsystemName(): Entry, endpointName->{}", endpointName);
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String serviceName = getComponentNameUtilities().getEndpointSubsystemNameFromChannelName(endpointName);
        getLogger().debug(".deriveIntegrationPointSubsystemName(): Exit, serviceName->{}", serviceName);
        return(serviceName);
    }

    //
    // JGroups Integration Point Status Check
    //

    public PetasosParticipantStatusEnum checkJGroupsIntegrationPoint(JGroupsIntegrationPointSummary targetJGroupsIP) {
        getLogger().debug(".checkJGroupsIntegrationPoint(): Entry, targetJGroupsIP->{}", targetJGroupsIP);
        if(targetJGroupsIP == null){
            getLogger().debug(".checkJGroupsIntegrationPoint(): Exit, targetJGroupsIP is null");
            return(PetasosParticipantStatusEnum.PETASOS_PARTICIPANT_FAILED);
        }
        if(StringUtils.isEmpty(targetJGroupsIP.getSubsystemParticipantName())){
            getLogger().debug(".checkJGroupsIntegrationPoint(): Exit, targetJGroupsIP.getEndpointName() is empty");
            return(PetasosParticipantStatusEnum.PETASOS_PARTICIPANT_FAILED);
        }
        String targetSubsystemName = getComponentNameUtilities().getSubsystemNameFromEndpointName(targetJGroupsIP.getSubsystemParticipantName());
        String mySubsystemName = getProcessingPlant().getSubsystemParticipantName();
        if(targetSubsystemName.contentEquals(mySubsystemName)){
            getLogger().debug(".checkJGroupsIntegrationPoint(): Exit, Endpoint is one of mine!");
            return(getParticipantHolder().getMyProcessingPlantPetasosParticipant().getParticipantStatus());
        }
        JGroupsIntegrationPointProbeReport report = probeJGroupsIntegrationPoint(targetJGroupsIP);
        PetasosParticipantStatusEnum endpointStatus = null;
        if(report != null){
            endpointStatus = report.getParticipantStatus();
        } else {
            endpointStatus = PetasosParticipantStatusEnum.PETASOS_PARTICIPANT_FAILED;
        }
        getLogger().debug(".checkJGroupsIntegrationPoint(): Exit, Endpoint endpointStatus->{}", endpointStatus);
        return(endpointStatus);
    }

    //
    // Topology Endpoint Resolution (i.e. resolution of the JGroupsIntegrationPoint from the TopologyIM)
    //

    protected JGroupsIntegrationPoint resolveTopologyNodeForJGroupsIntegrationPoint(){
        getLogger().debug(".resolveTopologyNodeForJGroupsIntegrationPoint(): Entry, endpointFunction->{}", specifyPetasosEndpointFunctionType());
        String name = getInterfaceNames().getEndpointName(PetasosEndpointTopologyTypeEnum.JGROUPS_INTEGRATION_POINT, specifyPetasosEndpointFunctionType().getDisplayName());
        getLogger().trace(".resolveTopologyNodeForJGroupsIntegrationPoint(): Required TopologyNodeRDN.nodeName->{}", name);
        for(TopologyNodeFDN currentEndpointFDN: getProcessingPlant().getMeAsASoftwareComponent().getEndpoints()){
            getLogger().trace(".resolveTopologyNodeForJGroupsIntegrationPoint(): currentEndpointFDN->{}",currentEndpointFDN);
            IPCTopologyEndpoint currentEndpoint = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
            getLogger().trace(".resolveTopologyNodeForJGroupsIntegrationPoint(): currentEndpoint->{}",currentEndpoint);
            PetasosEndpointTopologyTypeEnum endpointType = currentEndpoint.getEndpointType();
            boolean endpointTypeMatches = endpointType.equals(PetasosEndpointTopologyTypeEnum.JGROUPS_INTEGRATION_POINT);
            if(endpointTypeMatches){
                getLogger().trace(".resolveTopologyNodeForJGroupsIntegrationPoint(): endpointTypeMatches!!!");
                if(currentEndpoint.getComponentRDN().getNodeName().contentEquals(name)) {
                    JGroupsIntegrationPoint resolvedEndpoint = (JGroupsIntegrationPoint)currentEndpoint;
                    getLogger().debug(".resolveTopologyNodeForJGroupsIntegrationPoint(): Exit, found IPCTopologyEndpoint and assigned it, resolvedEndpoint->{}", resolvedEndpoint);
                    return(resolvedEndpoint);
                }
            }
        }
        getLogger().error(".deriveIPCTopologyEndpoint(): Exit, Could not find appropriate Endpoint for {}",specifyPetasosEndpointFunctionType().getDisplayName());
        return(null);
    }

    //
    // JGroupsIntegrationPointSummary Helpers
    //

    public JGroupsIntegrationPointSummary buildFromChannelName(String channelName){
        getLogger().debug(".buildFromChannelName(): Entry, channelName->{}", channelName);
        JGroupsIntegrationPointSummary jgroupsIPSummary = new JGroupsIntegrationPointSummary();
        jgroupsIPSummary.setSubsystemParticipantName(getComponentNameUtilities().getEndpointSubsystemNameFromChannelName(channelName));
        jgroupsIPSummary.setChannelName(channelName);
        String functionName = getComponentNameUtilities().getEndpointFunctionFromChannelName(channelName);
        PetasosEndpointFunctionTypeEnum functionEnum = PetasosEndpointFunctionTypeEnum.getFunctionEnumFromDisplayName(functionName);
        jgroupsIPSummary.setFunction(functionEnum);
        String site = getComponentNameUtilities().getEndpointSiteFromChannelName(channelName);
        jgroupsIPSummary.setSite(site);
        String endpointZoneName = getComponentNameUtilities().getEndpointZoneFromChannelName(channelName);
        NetworkSecurityZoneEnum networkSecurityZoneEnum = NetworkSecurityZoneEnum.fromSecurityZoneCamelCaseString(endpointZoneName);
        jgroupsIPSummary.setZone(networkSecurityZoneEnum);
        getLogger().debug(".buildFromChannelName(): Exit, jgroupsIPSummary->{}", jgroupsIPSummary);
        return(jgroupsIPSummary);
    }

    //
    // Query / Report Entity Creation Methods
    //

    protected JGroupsIntegrationPointSummary createSummary(JGroupsIntegrationPoint integrationPoint){
        JGroupsIntegrationPointSummary summary = integrationPoint.toSummary();
        summary.setParticipantStatus(getParticipantHolder().getMyProcessingPlantPetasosParticipant().getParticipantStatus());
        summary.setUniqueIdQualifier(getComponentNameUtilities().getCurrentUUID());
        return(summary);
    }

    protected JGroupsIntegrationPointProbeQuery createJGroupsIPQuery(JGroupsIntegrationPoint integrationPoint){
        JGroupsIntegrationPointProbeQuery query = new JGroupsIntegrationPointProbeQuery(integrationPoint.toSummary());
        query.setParticipantStatus(getParticipantHolder().getMyProcessingPlantPetasosParticipant().getParticipantStatus());
        query.setUniqueIdQualifier(getComponentNameUtilities().getCurrentUUID());
        return(query);
    }

    protected JGroupsIntegrationPointProbeReport createJGroupsIPReport(JGroupsIntegrationPoint integrationPoint){
        JGroupsIntegrationPointProbeReport report = new JGroupsIntegrationPointProbeReport(integrationPoint.toSummary());
        report.setParticipantStatus(getParticipantHolder().getMyProcessingPlantPetasosParticipant().getParticipantStatus());
        report.setUniqueIdQualifier(getComponentNameUtilities().getCurrentUUID());
        report.setReportInstant(Instant.now());
        report.setFunction(integrationPoint.getInterfaceFunction());
        report.setChannelName(integrationPoint.getChannelName());
        report.setComponentId(integrationPoint.getComponentID());
        report.setProcessingPlantInstanceId(getProcessingPlant().getMeAsASoftwareComponent().getComponentID());
        report.setParticipantStatus(getParticipantHolder().getMyProcessingPlantPetasosParticipant().getParticipantStatus());
        report.setSite(getProcessingPlant().getDeploymentSite());
        report.setZone(getProcessingPlant().getNetworkZone());
        report.setIntegrationPointStatus(integrationPoint.getEndpointStatus());
        report.setSubsystemParticipantName(getSubsystemParticipantName());
        return(report);
    }

    //
    // Endpoint (JGroupsIntegrationPoint) Lifecycle Watchdog Services
    //

    @Override
    public void processInterfaceSuspect(PetasosAdapterAddress suspectInterface) {

    }

    /**
     * This method parses the list of "interfaces" ADDED (exposed/visible) to a JChannel instance (i.e. visible within the
     * same JGroups cluster) and works out if a scan of the enpoint is (a) not another instance (different POD) of
     * this service and is implementing the same "function".
     *
     * Note, it has to check the "name" quality/validity/structure - as sometimes JGroups can pass some wacky values
     * to us...
     *
     * @param addedInterface
     */
    @Override
    public void processInterfaceAddition(PetasosAdapterAddress addedInterface){
        getLogger().info(".interfaceAdded(): Entry, addedInterface->{}", addedInterface);
        String endpointSubsystemName = getComponentNameUtilities().getSubsystemNameFromEndpointName(addedInterface.getAddressName());
        String endpointFunctionName = getComponentNameUtilities().getEndpointFunctionFromChannelName(addedInterface.getAddressName());
        if(StringUtils.isNotEmpty(endpointSubsystemName) && StringUtils.isNotEmpty(endpointFunctionName)) {
            boolean itIsAnotherInstanceOfMe = endpointSubsystemName.contentEquals(getSubsystemParticipantName());
            boolean itIsSameType = endpointFunctionName.contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName());
            if (!itIsAnotherInstanceOfMe && itIsSameType) {
                getLogger().debug(".interfaceAdded(): itIsAnotherInstanceOfMe && !itIsSameType");
                String endpointChannelName = addedInterface.getAddressName();
                JGroupsIntegrationPointSummary jgroupsIP = buildFromChannelName(endpointChannelName);
                integrationPointCheckScheduleMap.scheduleJGroupsIntegrationPointCheck(jgroupsIP, false, true);
                scheduleEndpointValidation();
            }
        }
        getLogger().debug(".interfaceAdded(): Exit");
    }

    /**
     * This method parses the list of "interfaces" REMOVED (exposed/visible) to a JChannel instance (i.e. visible within the
     * same JGroups cluster) and works out if a scan of the enpoint is (a) not another instance (different POD) of
     * this service and is implementing the same "function".
     *
     * Note, it has to check the "name" quality/validity/structure - as sometimes JGroups can pass some wacky values
     * to us...
     *
     * @param removedInterface
     */
    @Override
    public void processInterfaceRemoval(PetasosAdapterAddress removedInterface){
        getLogger().debug(".interfaceRemoved(): Entry, removedInterface->{}", removedInterface);
        String endpointSubsystemName = getComponentNameUtilities().getSubsystemNameFromEndpointName(removedInterface.getAddressName());
        String endpointFunctionName = getComponentNameUtilities().getEndpointFunctionFromChannelName(removedInterface.getAddressName());
        if(StringUtils.isNotEmpty(endpointSubsystemName) && StringUtils.isNotEmpty(endpointFunctionName)) {
            boolean itIsAnotherInstanceOfMe = endpointSubsystemName.contentEquals(getSubsystemParticipantName());
            boolean itIsSameType = endpointFunctionName.contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName());
            if (!itIsAnotherInstanceOfMe && itIsSameType) {
                getLogger().trace(".interfaceRemoved(): !itIsAnotherInstanceOfMe && itIsSameType");
                String endpointChannelName = removedInterface.getAddressName();
                JGroupsIntegrationPointSummary jgroupsIP = buildFromChannelName(endpointChannelName);
                integrationPointCheckScheduleMap.scheduleJGroupsIntegrationPointCheck(jgroupsIP, true, false);
                scheduleEndpointValidation();
            }
        }
        getLogger().debug(".interfaceRemoved(): Exit");
    }

    public void scheduleEndpointScan(){
        getLogger().debug(".scheduleEndpointScan(): Entry");
        List<PetasosAdapterAddress> groupMembers = getAllClusterMemberAdapterAddresses();
        for(PetasosAdapterAddress currentGroupMember: groupMembers){
            if(currentGroupMember.getAddressName().contains(PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName())) {
                String endpointChannelName = currentGroupMember.getAddressName();
                JGroupsIntegrationPointSummary jgroupsIP = buildFromChannelName(endpointChannelName);
                integrationPointCheckScheduleMap.scheduleJGroupsIntegrationPointCheck(jgroupsIP, false, true);
                getLogger().trace(".scheduleEndpointScan(): Added ->{} to scan", jgroupsIP);
            }
        }
        getLogger().debug(".scheduleEndpointScan(): Exit");
    }

    public void scheduleEndpointValidation() {
        getLogger().debug(".scheduleEndpointValidation(): Entry (isEndpointCheckScheduled->{})", endpointCheckScheduled);
        if (endpointCheckScheduled) {
            // do nothing, it is already scheduled
        } else {
            TimerTask endpointValidationTask = new TimerTask() {
                public void run() {
                    getLogger().debug(".endpointValidationTask(): Entry");
                    boolean doAgain = performEndpointValidationCheck();
                    getLogger().debug(".endpointValidationTask(): doAgain ->{}", doAgain);
                    if (!doAgain) {
                        cancel();
                        endpointCheckScheduled = false;
                    }
                    getLogger().debug(".endpointValidationTask(): Exit");
                }
            };
            String timerName = "EndpointValidationWatchdogTask";
            Timer timer = new Timer(timerName);
            timer.schedule(endpointValidationTask, getJgroupsParticipantInformationService().getEndpointValidationStartDelay(), getJgroupsParticipantInformationService().getEndpointValidationPeriod());
            endpointCheckScheduled = true;
        }
        getLogger().debug(".scheduleEndpointValidation(): Exit");
    }

    /**
     * This method retrieves the list of "JGroupsIntegrationPoints" to be "Probed" from the IntegrationPointMap.EndpointsToCheck
     * (ConcurrentHashMap) and attempts to retrieve their JGroupsIntegrationPointSummary instance.
     *
     * It then uses this JGroupsIntegrationPointSummary instance (returnedEndpointFromTarget) to update the IntegrationPointMap with
     * the current details (from the source, so to speak).
     *
     * It keeps a list of endpoints that it couldn't check and re-schedules their validation check.
     *
     * It also checks the Subsystem-to-IntegrationPoint map and ensures this aligns with the information provided.
     *
     * It then checks to see if there is a need to do another check/validation iteration and returns the result.
     *
     * @return True if another validation is required, false otherwise.
     */
    public boolean performEndpointValidationCheck(){
        getLogger().debug(".performEndpointValidationCheck(): Entry");
        List<JGroupsIntegrationPointCheckScheduleElement> endpointsToCheck = integrationPointCheckScheduleMap.getEndpointsToCheck();
        List<JGroupsIntegrationPointCheckScheduleElement> redoList = new ArrayList<>();
        getLogger().trace(".performEndpointValidationCheck(): Iterate through...");
        for(JGroupsIntegrationPointCheckScheduleElement currentScheduleElement: endpointsToCheck) {
            getLogger().trace(".performEndpointValidationCheck(): currentScheduleElement->{}", currentScheduleElement);
            if(currentScheduleElement.isEndpointAdded()) {
                boolean wasProcessed = checkEndpointAddition(currentScheduleElement);
                if(wasProcessed) {
                    getLogger().trace(".performEndpointValidationCheck(): item was processed!");
                } else {
                    getLogger().trace(".performEndpointValidationCheck(): item was NOT processed, adding to redo list");
                    redoList.add(currentScheduleElement);
                }
            }
            if(currentScheduleElement.isEndpointRemoved()){
                checkEndpointRemoval(currentScheduleElement);
            }
        }
        for(JGroupsIntegrationPointCheckScheduleElement redoItem: redoList){
            getLogger().trace(".performEndpointValidationCheck(): Re-Adding to schedule the redoItem->{}", redoItem);
            integrationPointCheckScheduleMap.scheduleJGroupsIntegrationPointCheck(redoItem.getJgroupsIPSummary(), false, true);
        }
        if(integrationPointCheckScheduleMap.isCheckScheduleIsEmpty()){
            getLogger().debug(".performEndpointValidationCheck(): Exit, perform again->false");
            return(false);
        } else {
            if(getLogger().isTraceEnabled()){
                for(JGroupsIntegrationPointCheckScheduleElement currentScheduledElement: integrationPointCheckScheduleMap.getEndpointsToCheck()){
                    getLogger().trace(".performEndpointValidationCheck(): Will Check Endpoint->{}", currentScheduledElement.getJgroupsIPSummary().getChannelName());
                }
            }
            getLogger().debug(".performEndpointValidationCheck(): Exit, perform again->true");
            return(true);
        }
    }

    /**
     * This method checks (using the supplied the provided PetasosEndpointCheckScheduleElement) the JGroupsIntegrationPoint
     * and ascertains its operational state. Depending on the status, it either schedules a follow-up check or marks
     * the JGroupsIntegrationPoint as Operational or Failed...
     *
     * @param currentScheduleElement
     * @return
     */
    protected boolean checkEndpointAddition(JGroupsIntegrationPointCheckScheduleElement currentScheduleElement){
        getLogger().debug(".checkEndpointAddition(): Entry, currentScheduleElement->{}", currentScheduleElement);
        String subsystemParticipantName = currentScheduleElement.getJgroupsIPSummary().getSubsystemParticipantName();
        getLogger().trace(".checkEndpointAddition(): check to see if scheduled element is another instance of me! my subsystemParticipantName->{},", getSubsystemParticipantName());
        if(subsystemParticipantName.equalsIgnoreCase(getSubsystemParticipantName())){
            getLogger().debug(".checkEndpointAddition(): Endpoint is for same subsystem as me, do nothing! ");
            return(true);
        }
        String endpointChannelName = currentScheduleElement.getJgroupsIPSummary().getChannelName();
        JGroupsIntegrationPointSummary synchronisedJGroupsIP = synchroniseEndpointCache(currentScheduleElement);
        if(synchronisedJGroupsIP != null){
            switch(synchronisedJGroupsIP.getIntegrationPointStatus()){
                case PETASOS_ENDPOINT_STATUS_OPERATIONAL:{
                    getIntegrationPointMap().updateSubsystemIntegrationPointMembership(synchronisedJGroupsIP.getSubsystemParticipantName(), currentScheduleElement.getJgroupsIPSummary().getChannelName());
                    doIntegrationPointBusinessFunctionCheck(synchronisedJGroupsIP, false, true);
                    getLogger().debug(".checkEndpointAddition(): Does not need re-checking, returning -true- (was processed)");
                    return(true);
                }
                case PETASOS_ENDPOINT_STATUS_SUSPECT:
                case PETASOS_ENDPOINT_STATUS_REACHABLE:
                case PETASOS_ENDPOINT_STATUS_STARTED:
                case PETASOS_ENDPOINT_STATUS_DETECTED:{
                    getLogger().debug(".checkEndpointAddition(): Needs re-checking, returning -false- (wasn't completely processed)");
                    return (false);
                }
                case PETASOS_ENDPOINT_STATUS_SAME:
                case PETASOS_ENDPOINT_STATUS_UNREACHABLE:
                case PETASOS_ENDPOINT_STATUS_FAILED:
                default:{
                    doIntegrationPointBusinessFunctionCheck(synchronisedJGroupsIP, true, false);
                    getLogger().debug(".checkEndpointAddition(): We've rescheduled the removal of this endpoint returning -true- (was processed)");
                    return (true);
                }
            }
        }
        getLogger().debug(".checkEndpointAddition(): there is nothing to check, so returning->true (was processed)");
        return (true);
    }

    protected void checkEndpointRemoval(JGroupsIntegrationPointCheckScheduleElement currentScheduleElement){
        getLogger().debug(".checkEndpointRemoval(): Entry, currentScheduleElement->{}", currentScheduleElement);
        String subsystemParticipantName = currentScheduleElement.getJgroupsIPSummary().getSubsystemParticipantName();
        if(subsystemParticipantName.equalsIgnoreCase(getSubsystemParticipantName())){
            getLogger().debug(".checkEndpointRemoval(): Endpoint is for same subsystem as me, do nothing! ");
            return;
        }
        getIntegrationPointMap().deleteSubsystemIntegrationPoint(currentScheduleElement.getJgroupsIPSummary().getChannelName());
        getLogger().debug(".checkEndpointRemoval(): Endpoint removed");
    }

    private JGroupsIntegrationPointSummary synchroniseEndpointCache(JGroupsIntegrationPointCheckScheduleElement currentScheduleElement){
        getLogger().debug(".synchroniseEndpointCache: Entry, currentScheduleElement->{}", currentScheduleElement);
        if(currentScheduleElement == null){
            getLogger().debug(".synchroniseEndpointCache: Exit, currentScheduleElement is null");
            return(null);
        }
        String subsystemParticipantName = currentScheduleElement.getJgroupsIPSummary().getSubsystemParticipantName();
        if(subsystemParticipantName.equalsIgnoreCase(getSubsystemParticipantName())){
            getLogger().debug(".synchroniseEndpointCache(): Endpoint is for same subsystem as me, do nothing! ");
            return(null);
        }
        String channelName = currentScheduleElement.getJgroupsIPSummary().getChannelName();
        getLogger().trace(".synchroniseEndpointCache: Checking to see if endpoint is already in EndpointMap");
        JGroupsIntegrationPointSummary cachedEndpoint = getIntegrationPointMap().getJGroupsIntegrationPointSummary(channelName);
        getLogger().trace(".synchroniseEndpointCache: Retrieved PetasosEndpoint->{}", cachedEndpoint);
        boolean doProbe = true;
        boolean isToBeRemoved = false;
        if(cachedEndpoint != null){
            switch(cachedEndpoint.getIntegrationPointStatus()) {
                case PETASOS_ENDPOINT_STATUS_SUSPECT:
                case PETASOS_ENDPOINT_STATUS_REACHABLE:
                case PETASOS_ENDPOINT_STATUS_STARTED:
                case PETASOS_ENDPOINT_STATUS_SAME:
                case PETASOS_ENDPOINT_STATUS_DETECTED:{
                    getLogger().trace(".synchroniseEndpointCache: Endpoint is ok, but not operational, going to have to Probe it!!");
                    doProbe = true;
                    break;
                }
                case PETASOS_ENDPOINT_STATUS_OPERATIONAL:
                {
                    getLogger().debug(".synchroniseEndpointCache(): Endpoint is operational, do nothing! ");
                    return(cachedEndpoint);
                }
                case PETASOS_ENDPOINT_STATUS_UNREACHABLE:
                case PETASOS_ENDPOINT_STATUS_FAILED:
                default:{
                    getLogger().trace(".synchroniseEndpointCache(): Endpoint is in a poor state, remove it from our cache! ");
                    doProbe = false;
                    isToBeRemoved = true;
                }
            }
        }
        if(doProbe) {
            getLogger().debug(".synchroniseEndpointCache(): [Performing Endpoint Probe] Start");
            if (isTargetAddressActive(currentScheduleElement.getJgroupsIPSummary().getChannelName())) {
                getLogger().trace(".synchroniseEndpointCache(): Probing (or attempting to Probe) the Endpoint");
                JGroupsIntegrationPointProbeReport report = probeJGroupsIntegrationPoint(currentScheduleElement.getJgroupsIPSummary());
                getLogger().trace(".synchroniseEndpointCache(): report->{}", report);
                if (report != null) {
                    getLogger().trace(".synchroniseEndpointCache(): Probe succeded, so let's synchronise/update local cache");
                    if (cachedEndpoint == null) {
                        cachedEndpoint = report;
                        getLogger().trace(".synchroniseEndpointCache(): addedPetasosEndpoint->{}", cachedEndpoint);
                        if (!StringUtils.isEmpty(report.getSubsystemParticipantName())) {
                            getIntegrationPointMap().updateSubsystemIntegrationPointMembership(report.getSubsystemParticipantName(), currentScheduleElement.getJgroupsIPSummary().getSubsystemParticipantName());
                        }
                    } else {
                        cachedEndpoint.setParticipantStatus(report.getParticipantStatus());
                        cachedEndpoint.setLastRefreshInstant(report.getReportInstant());
                    }
                    getLogger().debug(".synchroniseEndpointCache(): [Performing Endpoint Probe] Finish");
                    getLogger().debug(".synchroniseEndpointCache(): Exit, Endpoint Probed and local Endpoint Registry updated");
                    return(cachedEndpoint);
                } else {
                    getLogger().trace(".synchroniseEndpointCache(): Probe failed, we should consider removing it!");
                    isToBeRemoved = true;
                }
            } else {
                getLogger().trace(".synchroniseEndpointCache(): Couldn't even find the endpoint, we should consider removing it!");
                isToBeRemoved = true;
            }
            getLogger().debug(".synchroniseEndpointCache(): [Performing Endpoint Probe] Finish");
        }
        if(isToBeRemoved){
            getLogger().trace(".synchroniseEndpointCache(): We should remove the Endpoint from our Cache and ToDo schedule!");
            int retryCountSoFar = currentScheduleElement.getRetryCount();
            if(retryCountSoFar > MAX_PROBE_RETRIES){
                getLogger().trace(".synchroniseEndpointCache(): we've tried to probe endpoint MAX_PROBE_RETRIES ({}) times and failed, so delete it", MAX_PROBE_RETRIES);
                integrationPointCheckScheduleMap.scheduleJGroupsIntegrationPointCheck(currentScheduleElement.getJgroupsIPSummary(), true, false);
            } else {
                getLogger().trace(".synchroniseEndpointCache(): probe has failed ({}) times, but we will try again", retryCountSoFar);
                retryCountSoFar += 1;
                integrationPointCheckScheduleMap.scheduleJGroupsIntegrationPointCheck(currentScheduleElement.getJgroupsIPSummary(), false, true, retryCountSoFar);
            }
            getLogger().debug(".synchroniseEndpointCache(): Could not find Endpoint, have rescheduled endpoint check");
            return(null);
        }
        getLogger().debug(".synchroniseEndpointCache(): Exit");
        return(cachedEndpoint);
    }

}
