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
package net.fhirfactory.pegacorn.petasos.endpoints.services.common;

import net.fhirfactory.pegacorn.core.interfaces.pathway.PetasosPubSubEndpointChangeCallbackRegistrationInterface;
import net.fhirfactory.pegacorn.core.interfaces.pathway.PetasosPubSubEndpointChangeInterface;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.PetasosEndpoint;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.petasos.endpoints.base.PetasosHealthCheckCallBackInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.map.JGroupsIntegrationPointSharedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class ProcessingPlantJGroupsIntegrationPointWatchdog
        implements PetasosHealthCheckCallBackInterface,
        PetasosPubSubEndpointChangeInterface,
        PetasosPubSubEndpointChangeCallbackRegistrationInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantJGroupsIntegrationPointWatchdog.class);



    private PetasosEndpointStatusEnum aggregateStatus;
    private PetasosEndpoint edgeAnswerHTTP;
    private PetasosEndpoint edgeAnswerRPC;

    private boolean initialised;

    private Long STARTUP_CHECK_INITIAL_DELAY = 5000L;
    private Long STARTUP_CHECK_PERIOD = 5000L;
    private boolean startupCheckRequired;
    private Long MAX_STARTUP_DURATION = 90L;

    private Long WATCHDOG_INITIAL_START_DELAY = 10000L;
    private Long WATCHDOG_SCAN_PERIOD = 30000L;
    private boolean watchdogCheckRequired;

    private Instant startupTime;
    private Instant lastCheckTime;
    private Instant lastOperationalTime;
    private int suspectIterationCount;

    private int FAILED_ITERATION_MAX = 3;

    private List<PetasosPubSubEndpointChangeInterface> publisherChangeCallbacks;

    @Inject
    JGroupsIntegrationPointSharedMap endpointMap;

    @Inject
    private ProcessingPlantJGroupsIntegrationPointSet processingPlantIntegrationPoints;

    //
    // Constructor
    //

    public ProcessingPlantJGroupsIntegrationPointWatchdog(){

        this.edgeAnswerHTTP = null;
        this.edgeAnswerRPC = null;
        this.aggregateStatus = PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED;

        this.initialised = false;

        this.startupCheckRequired = true;
        this.watchdogCheckRequired = false;

        this.startupTime = Instant.now();

        this.publisherChangeCallbacks = new ArrayList<>();
    }

    //
    // PostConstruct(or)
    //

    @PostConstruct
    public void initialise(){
        if(this.initialised){
            return;
        }
        scheduleStartupWatchdog();
        initialised = true;
    }

    //
    // Watchdog (Startup)
    //

    public void scheduleStartupWatchdog() {
        getLogger().debug(".scheduleStartupWatchdog(): Entry");
        TimerTask startupWatchdogTask = new TimerTask() {
            public void run() {
                getLogger().debug(".startupWatchdogTask(): Entry");
                startupWatchdog();
                if (!getAggregateStatus().equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED)) {
                    cancel();
                    scheduleOngoingStatusWatchdog();
                    startupCheckRequired = false;
                }
                getLogger().debug(".startupWatchdogTask(): Exit");
            }
        };
        if(startupCheckRequired) {
            Timer timer = new Timer("scheduleStartupWatchdog");
            timer.schedule(startupWatchdogTask, STARTUP_CHECK_INITIAL_DELAY, STARTUP_CHECK_PERIOD);
        }
        getLogger().debug(".scheduleStartupWatchdog(): Exit");
    }

    public void startupWatchdog(){
        getLogger().debug(".startupWatchdog(): Entry");
        Instant timeRightNow = Instant.now();
        setLastCheckTime(timeRightNow);
        Long startupTimeSoFar = timeRightNow.getEpochSecond() - startupTime.getEpochSecond();
        if(startupTimeSoFar > MAX_STARTUP_DURATION){
            setAggregateStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
            getLogger().error(".startupWatchdog(): Core Petasos Endpoints have failed to startup (within defined startup period)!!!!");
            return;
        }
        PetasosEndpointStatusEnum currentStatus = deriveAggregateStatus(this.getAggregateStatus());
        if(currentStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED)){
            setAggregateStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED);
            getLogger().debug(".startupWatchdog(): Exit, Startup not completed, awaiting ports");
            return;
        }
        if(currentStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL)){
            if(checkMinimumViablePortSetHasLaunched()) {
                setAggregateStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
                getLogger().debug(".startupWatchdog(): Exit, Core Petasos Endpoints Startup Completed");
            } else {
                setAggregateStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED);
                getLogger().debug(".startupWatchdog(): Exit, Startup not completed, awaiting ports");
            }
            return;
        }
        if(currentStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED)){
            getLogger().debug(".startupWatchdog(): Exit, Core Petasos Endpoints Startup Continuing");
            return;
        }
        if(currentStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED)) {
            setAggregateStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
            getLogger().error(".startupWatchdog(): Core Petasos Endpoints Startup Failed!!!!");
            return;
        }
    }

    /**
     * This function is used to "qualify" which systems (ports) need to be up and running in-order to
     * assuming the CorePetasosEndpoint set is functional. It is split into 3: (1) the core single-site ports,
     * (2) multi-site communication ports and (3) the edgeAnswer ports. Change as deployment dictates.
     *
     * @return A conditional report on whether a MinimumViableProduct set of ports has launched.
     */
    private boolean checkMinimumViablePortSetHasLaunched(){
        boolean singleSitePortsLaunched = true;
        boolean multiSitePortsLaunched = true;
        boolean edgeAnswerPortsLaunched = true;

        if (!processingPlantIntegrationPoints.hasPetasosAuditServicesEndpoint()) {
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!processingPlantIntegrationPoints.hasPetasosInterceptionServicesEndpoint()) {
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!processingPlantIntegrationPoints.hasPetasosMessagingServicesEndpoint()) {
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!processingPlantIntegrationPoints.hasPetasosMetricsServicesEndpoint()){
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!processingPlantIntegrationPoints.hasPetasosSubscriptionServicesEndpoint()){
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!processingPlantIntegrationPoints.hasPetasosTaskServicesEndpoint()){
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!processingPlantIntegrationPoints.hasPetasosTopologyServicesEndpoint()){
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!existsEdgeAnswerHTTP()) {
            edgeAnswerPortsLaunched = edgeAnswerPortsLaunched && false;
        }
        if (!existsEdgeAnswerRPC()) {
            edgeAnswerPortsLaunched = edgeAnswerPortsLaunched && false;
        }
        // TODO we should link the decision about whether multi-site is required with the configuration parameter(s) from the startup config.
        // Single Site, no EdgeAnswer
        if(singleSitePortsLaunched){
            return(true);
        }
        // Multi-Site, no EdgeAnswer
        if(singleSitePortsLaunched && multiSitePortsLaunched){
            return(true);
        }
        // Single Site, with EdgeAnswer
        if(singleSitePortsLaunched && edgeAnswerPortsLaunched){
            return(true);
        }
        // All
        if(singleSitePortsLaunched && multiSitePortsLaunched && edgeAnswerPortsLaunched){
            return(true);
        }
        return(false);
    }

    //
    // Watchdog (Ongoing)
    //

    public void scheduleOngoingStatusWatchdog() {
        getLogger().debug(".scheduleOngoingStatusWatchdog(): Entry");
        TimerTask ongoingWatchdogTask = new TimerTask() {
            public void run() {
                getLogger().debug(".ongoingWatchdogTask(): Entry");
                statusWatchDog();
                getLogger().debug(".ongoingWatchdogTask(): Exit");
            }
        };
        Timer timer = new Timer("scheduleOngoingStatusWatchdog");
        timer.schedule(ongoingWatchdogTask, WATCHDOG_INITIAL_START_DELAY, WATCHDOG_SCAN_PERIOD);

        getLogger().debug(".scheduleOngoingStatusWatchdog(): Exit");
    }

    public void statusWatchDog(){
        getLogger().debug(".statusWatchDog(): Entry");
        Instant timeRightNow = Instant.now();
        setLastCheckTime(timeRightNow);
        PetasosEndpointStatusEnum currentStatus = deriveAggregateStatus(this.getAggregateStatus());
        switch(currentStatus){
            case PETASOS_ENDPOINT_STATUS_STARTED: {
                // Shouldn't be here....
                setAggregateStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
                getLogger().error(".statusWatchDog(): Core Petasos Endpoints have failed!!!!");
                return;
            }
            case PETASOS_ENDPOINT_STATUS_OPERATIONAL:{
                // Do nothing
                return;
            }
            case PETASOS_ENDPOINT_STATUS_SUSPECT:{
                if(getSuspectIterationCount() > FAILED_ITERATION_MAX){
                    setAggregateStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
                    getLogger().error(".statusWatchDog(): Core Petasos Endpoints have failed!!!!");
                    return;
                } else {
                    int updatedSuspectCount = getSuspectIterationCount() + 1;
                    setSuspectIterationCount(updatedSuspectCount);
                    return;
                }
            }
            case PETASOS_ENDPOINT_STATUS_FAILED:
            case PETASOS_ENDPOINT_STATUS_DETECTED:
            case PETASOS_ENDPOINT_STATUS_UNREACHABLE:
            case PETASOS_ENDPOINT_STATUS_REACHABLE:
            default:{
                setAggregateStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
                getLogger().warn(".statusWatchDog(): Core Petasos Endpoints have failed!!!!");
                return;
            }
        }
    }

    private PetasosEndpointStatusEnum deriveAggregateStatus(PetasosEndpointStatusEnum startingStatus){
        getLogger().debug(".deriveAggregateStatus(): Entry, startingStatus->{}", startingStatus);
        List<PetasosEndpointStatusEnum> statusList = new ArrayList<>();
        getLogger().trace(".deriveAggregateStatus(): Assembling the list of status'es");
        if (processingPlantIntegrationPoints.hasPetasosMessagingServicesEndpoint()) {
            PetasosEndpointStatusEnum interzoneIPCStatus = processingPlantIntegrationPoints.getPetasosMessagingServicesEndpoint().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterzoneIPC().getEndpointStatus()->{}", interzoneIPCStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneIPCStatus));
        }
        if (processingPlantIntegrationPoints.hasPetasosTopologyServicesEndpoint()) {
            PetasosEndpointStatusEnum interzoneTopologyStatus = processingPlantIntegrationPoints.getPetasosTopologyServicesEndpoint().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneOAMDiscovery().getEndpointStatus()->{}", interzoneTopologyStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneTopologyStatus));
        }
        if (processingPlantIntegrationPoints.hasPetasosSubscriptionServicesEndpoint()) {
            PetasosEndpointStatusEnum interzoneSubscriptionStatus = processingPlantIntegrationPoints.getPetasosSubscriptionServicesEndpoint().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterzoneOAMPubSub().getEndpointStatus()->{}", interzoneSubscriptionStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneSubscriptionStatus));
        }
        if (processingPlantIntegrationPoints.hasPetasosAuditServicesEndpoint()){
            PetasosEndpointStatusEnum interzoneAuditStatus = processingPlantIntegrationPoints.getPetasosAuditServicesEndpoint().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneAudit().getEndpointStatus()->{}", interzoneAuditStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneAuditStatus));
        }
        if (processingPlantIntegrationPoints.hasPetasosTaskServicesEndpoint()){
            PetasosEndpointStatusEnum interzoneTaskingStatus = processingPlantIntegrationPoints.getPetasosTaskServicesEndpoint().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneTask().getEndpointStatus()->{}", interzoneTaskingStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneTaskingStatus));
        }
        if (processingPlantIntegrationPoints.hasPetasosInterceptionServicesEndpoint()){
            PetasosEndpointStatusEnum interzoneInterceptionStatus = processingPlantIntegrationPoints.getPetasosInterceptionServicesEndpoint().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneInterception().getEndpointStatus()->{}", interzoneInterceptionStatus);
            statusList.add(resolveStatusValue(startingStatus,interzoneInterceptionStatus));
        }
        if(processingPlantIntegrationPoints.hasPetasosMetricsServicesEndpoint()){
            PetasosEndpointStatusEnum interzoneMetricsStatus = processingPlantIntegrationPoints.getPetasosMetricsServicesEndpoint().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneMetrics().getEndpointStatus()->{}", interzoneMetricsStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneMetricsStatus));
        }
/*
        if (existsEdgeAnswerHTTP()) {
            statusList.add(resolveStatusValue(startingStatus, getEdgeAnswerHTTP().getEndpointStatus()));
        }
        if (existsEdgeAnswerRPC()) {
            statusList.add(resolveStatusValue(startingStatus, getEdgeAnswerRPC().getEndpointStatus()));
        }

 */
        getLogger().trace(".deriveAggregateStatus(): If any of the PetasosEndpoints are FAILED, then they have ALL FAILED");
        for(PetasosEndpointStatusEnum currentStatus: statusList){
            if(currentStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED)){
                getLogger().debug(".deriveAggregateStatus(): Exit, returning->{}", PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
                return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
            }
        }
        getLogger().trace(".deriveAggregateStatus(): If any of the PetasosEndpoints are SUSPECT, then the the aggregate is SUSPECT");
        for(PetasosEndpointStatusEnum currentStatus: statusList){
            if(currentStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_SUSPECT)){
                getLogger().debug(".deriveAggregateStatus(): Exit, returning->{}", PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_SUSPECT);
                return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_SUSPECT);
            }
        }
        getLogger().trace(".deriveAggregateStatus(): If any of the PetasosEndpoints are STARTED, then the the aggregate is STARTED");
        for(PetasosEndpointStatusEnum currentStatus: statusList){
            if(currentStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED)){
                getLogger().debug(".deriveAggregateStatus(): Exit, returning->{}", PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED);
                return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED);
            }
        }
        getLogger().trace(".deriveAggregateStatus(): Checking to see if that are ALL OPERATIONAL");
        boolean allOperational = true;
        for(PetasosEndpointStatusEnum currentStatus: statusList){
            if(!currentStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL)){
                allOperational = false;
            }
        }
        if(allOperational){
            getLogger().debug(".deriveAggregateStatus(): Exit, returning->{}", PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
            return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
        } else {
            getLogger().debug(".deriveAggregateStatus(): Exit, returning->{}", PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
            return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
        }
    }

    private PetasosEndpointStatusEnum resolveStatusValue(PetasosEndpointStatusEnum currentStatus, PetasosEndpointStatusEnum newStatus){
        switch(currentStatus){
            case PETASOS_ENDPOINT_STATUS_STARTED:{
                switch(newStatus){
                    case PETASOS_ENDPOINT_STATUS_STARTED:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED);
                    case PETASOS_ENDPOINT_STATUS_OPERATIONAL:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
                    case PETASOS_ENDPOINT_STATUS_SUSPECT:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_SUSPECT);
                    default:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
                }
            }
            case PETASOS_ENDPOINT_STATUS_OPERATIONAL:{
                switch(newStatus){
                    case PETASOS_ENDPOINT_STATUS_STARTED:
                    case PETASOS_ENDPOINT_STATUS_SUSPECT:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_SUSPECT);
                    case PETASOS_ENDPOINT_STATUS_OPERATIONAL:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
                    default:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
                }
            }
            case PETASOS_ENDPOINT_STATUS_SUSPECT:{
                switch(newStatus){
                    case PETASOS_ENDPOINT_STATUS_OPERATIONAL:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
                    case PETASOS_ENDPOINT_STATUS_STARTED:
                    case PETASOS_ENDPOINT_STATUS_SUSPECT:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_SUSPECT);
                    default:
                        return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
                }
            }
            default: {
                return (PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_FAILED);
            }
        }
    }

    //
    // New publisher Status
    //

    @Override
    public void notifyNewPublisher(PetasosParticipant newPublisher) {
        if(newPublisher == null){
            return;
        }
        for(PetasosPubSubEndpointChangeInterface currentCallback: this.publisherChangeCallbacks){
            currentCallback.notifyNewPublisher(newPublisher);
        }
    }

    @Override
    public void registerPubSubCallbackChange(PetasosPubSubEndpointChangeInterface publisherChangeCallback) {
        this.publisherChangeCallbacks.add(publisherChangeCallback);
    }

    //
    // Getters (and Setters)
    //




    public PetasosEndpointStatusEnum getAggregateStatus() {
        return aggregateStatus;
    }

    public void setAggregateStatus(PetasosEndpointStatusEnum aggregateStatus) {
        this.aggregateStatus = aggregateStatus;
    }

    public JGroupsIntegrationPointSharedMap getEndpointMap() {
        return endpointMap;
    }

    public boolean existsEdgeAnswerHTTP(){
        boolean exists = this.edgeAnswerHTTP != null;
        return(exists);
    }

    public PetasosEndpoint getEdgeAnswerHTTP() {
        return edgeAnswerHTTP;
    }

    public void setEdgeAnswerHTTP(PetasosEndpoint edgeAnswerHTTP) {
        this.edgeAnswerHTTP = edgeAnswerHTTP;
    }

    public boolean existsEdgeAnswerRPC(){
        boolean exists = this.edgeAnswerRPC != null;
        return(exists);
    }

    public PetasosEndpoint getEdgeAnswerRPC() {
        return edgeAnswerRPC;
    }

    public void setEdgeAnswerRPC(PetasosEndpoint edgeAnswerRPC) {
        this.edgeAnswerRPC = edgeAnswerRPC;
    }

    protected Logger getLogger() {
        return (LOG);
    }

    public Instant getStartupTime() {
        return startupTime;
    }

    public void setStartupTime(Instant startupTime) {
        this.startupTime = startupTime;
    }

    public Instant getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Instant lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public Instant getLastOperationalTime() {
        return lastOperationalTime;
    }

    public void setLastOperationalTime(Instant lastOperationalTime) {
        this.lastOperationalTime = lastOperationalTime;
    }

    public int getSuspectIterationCount() {
        return suspectIterationCount;
    }

    public void setSuspectIterationCount(int suspectIterationCount) {
        this.suspectIterationCount = suspectIterationCount;
    }

    @Override
    public PetasosEndpointStatusEnum getAggregatePetasosEndpointStatus() {
        return (getAggregateStatus());
    }
}
