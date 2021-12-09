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
package net.fhirfactory.pegacorn.petasos.endpoints;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.core.interfaces.pubsub.PetasosPubSubEndpointChangeCallbackRegistrationInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.base.PetasosHealthCheckCallBackInterface;
import net.fhirfactory.pegacorn.core.interfaces.pubsub.PetasosPubSubEndpointChangeInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.map.PetasosEndpointMap;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.InterSubsystemPubSubParticipant;
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
public class CoreSubsystemPetasosEndpointsWatchdog
        implements PetasosHealthCheckCallBackInterface,
        PetasosPubSubEndpointChangeInterface,
        PetasosPubSubEndpointChangeCallbackRegistrationInterface {
    private static final Logger LOG = LoggerFactory.getLogger(CoreSubsystemPetasosEndpointsWatchdog.class);

    private PetasosEndpoint intrazoneIPC;
    private PetasosEndpoint intrazoneOAMPubSub;
    private PetasosEndpoint intrazoneOAMDiscovery;
    private PetasosEndpoint intraZoneAudit;
    private PetasosEndpoint intraZoneTask;
    private PetasosEndpoint intraZoneInterception;
    private PetasosEndpoint intraZoneMetrics;

    private PetasosEndpoint interzoneIPC;
    private PetasosEndpoint interzoneOAMPubSub;
    private PetasosEndpoint interzoneOAMDiscovery;
    private PetasosEndpoint interZoneAudit;
    private PetasosEndpoint interZoneTask;
    private PetasosEndpoint interZoneInterception;
    private PetasosEndpoint interZoneMetrics;

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
    PetasosEndpointMap endpointMap;

    //
    // Constructor
    //

    public CoreSubsystemPetasosEndpointsWatchdog(){
        this.intrazoneIPC = null;
        this.intrazoneOAMDiscovery = null;
        this.intrazoneOAMPubSub = null;
        this.intraZoneAudit = null;
        this.intraZoneTask = null;
        this.intraZoneInterception = null;
        this.intraZoneMetrics = null;

        this.interzoneIPC = null;
        this.interzoneOAMDiscovery = null;
        this.interzoneOAMPubSub = null;
        this.interZoneAudit = null;
        this.interZoneTask = null;
        this.interZoneInterception = null;
        this.interZoneMetrics = null;

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

        if (!existsIntrazoneIPC()) {
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!existsIntraZoneOAMDiscovery()){
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!existsIntraZoneOAMPubSub()) {
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!existsIntraZoneMetrics()){
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!existsInterZoneIPC()) {
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!existsInterZoneOAMDiscovery()) {
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!existsInterZoneOAMPubSub()) {
            singleSitePortsLaunched = singleSitePortsLaunched && false;
        }
        if (!existsInterZoneMetrics()){
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
        if (existsIntrazoneIPC()) {
            PetasosEndpointStatusEnum intrazoneIPCStatus = getIntrazoneIPC().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getIntrazoneIPC().getEndpointStatus()->{}", intrazoneIPCStatus);
            statusList.add(resolveStatusValue(startingStatus, intrazoneIPCStatus));
        }
        if (existsIntraZoneOAMDiscovery()){
            PetasosEndpointStatusEnum intrazoneTopologyStatus = getIntrazoneOAMDiscovery().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getIntrazoneOAMDiscovery().getEndpointStatus()->{}", intrazoneTopologyStatus);
            statusList.add(resolveStatusValue(startingStatus, intrazoneTopologyStatus));
        }
        if (existsIntraZoneOAMPubSub()) {
            PetasosEndpointStatusEnum intrazoneSubscriptionStatus = getIntrazoneOAMPubSub().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getIntrazoneOAMPubSub().getEndpointStatus()->{}", intrazoneSubscriptionStatus);
            statusList.add(resolveStatusValue(startingStatus, intrazoneSubscriptionStatus));
        }
        if (existsIntraZoneAudit()){
            PetasosEndpointStatusEnum intrazoneAuditStatus = getIntraZoneAudit().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getIntraZoneAudit().getEndpointStatus()->{}", intrazoneAuditStatus);
            statusList.add(resolveStatusValue(startingStatus, intrazoneAuditStatus));
        }
        if (existsIntraZoneTask()){
            PetasosEndpointStatusEnum intrazoneTaskingStatus = getIntraZoneTask().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getIntraZoneTask().getEndpointStatus()->{}", intrazoneTaskingStatus);
            statusList.add(resolveStatusValue(startingStatus, intrazoneTaskingStatus));
        }
        if (existsIntraZoneInterception()){
            PetasosEndpointStatusEnum intrazoneInterceptionStatus = getIntraZoneInterception().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getIntraZoneInterception().getEndpointStatus()->{}", intrazoneInterceptionStatus);
            statusList.add(resolveStatusValue(startingStatus, intrazoneInterceptionStatus));
        }
        if (existsIntraZoneMetrics()){
            PetasosEndpointStatusEnum intrazoneMetricsStatus = getIntraZoneMetrics().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getIntraZoneMetrics().getEndpointStatus()->{}", intrazoneMetricsStatus);
            statusList.add(resolveStatusValue(startingStatus, intrazoneMetricsStatus));
        }
        if (existsInterZoneIPC()) {
            PetasosEndpointStatusEnum interzoneIPCStatus = getInterzoneIPC().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterzoneIPC().getEndpointStatus()->{}", interzoneIPCStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneIPCStatus));
        }
        if (existsInterZoneOAMDiscovery()) {
            PetasosEndpointStatusEnum interzoneTopologyStatus = getInterzoneOAMDiscovery().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneOAMDiscovery().getEndpointStatus()->{}", interzoneTopologyStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneTopologyStatus));
        }
        if (existsInterZoneOAMPubSub()) {
            PetasosEndpointStatusEnum interzoneSubscriptionStatus = getInterzoneOAMPubSub().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterzoneOAMPubSub().getEndpointStatus()->{}", interzoneSubscriptionStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneSubscriptionStatus));
        }
        if (existsInterZoneAudit()){
            PetasosEndpointStatusEnum interzoneAuditStatus = getInterZoneAudit().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneAudit().getEndpointStatus()->{}", interzoneAuditStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneAuditStatus));
        }
        if (existsInterZoneTask()){
            PetasosEndpointStatusEnum interzoneTaskingStatus = getInterZoneTask().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneTask().getEndpointStatus()->{}", interzoneTaskingStatus);
            statusList.add(resolveStatusValue(startingStatus, interzoneTaskingStatus));
        }
        if (existsInterZoneInterception()){
            PetasosEndpointStatusEnum interzoneInterceptionStatus = getInterZoneInterception().getEndpointStatus();
            getLogger().debug(".deriveAggregateStatus(): Entry, getInterZoneInterception().getEndpointStatus()->{}", interzoneInterceptionStatus);
            statusList.add(resolveStatusValue(startingStatus,interzoneInterceptionStatus));
        }
        if(existsInterZoneMetrics()){
            PetasosEndpointStatusEnum interzoneMetricsStatus = getInterZoneMetrics().getEndpointStatus();
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
    public void notifyNewPublisher(InterSubsystemPubSubParticipant newPublisher) {
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

    public boolean existsIntraZoneInterception(){
        boolean hasValue = this.intraZoneInterception != null;
        return(hasValue);
    }

    public PetasosEndpoint getIntraZoneInterception() {
        return intraZoneInterception;
    }

    public void setIntraZoneInterception(PetasosEndpoint intraZoneInterception) {
        this.intraZoneInterception = intraZoneInterception;
    }

    public boolean existsInterZoneInterception(){
        boolean hasValue = this.interZoneInterception != null;
        return(hasValue);
    }

    public PetasosEndpoint getInterZoneInterception() {
        return interZoneInterception;
    }

    public void setInterZoneInterception(PetasosEndpoint interZoneInterception) {
        this.interZoneInterception = interZoneInterception;
    }

    public boolean existsIntraZoneAudit(){
        boolean hasValue = this.intraZoneAudit != null;
        return(hasValue);
    }

    public PetasosEndpoint getIntraZoneAudit() {
        return intraZoneAudit;
    }

    public void setIntraZoneAudit(PetasosEndpoint intraZoneAudit) {
        this.intraZoneAudit = intraZoneAudit;
    }

    public boolean existsIntraZoneTask(){
        boolean hasValue = this.intraZoneTask != null;
        return(hasValue);
    }

    public PetasosEndpoint getIntraZoneTask() {
        return intraZoneTask;
    }

    public void setIntraZoneTask(PetasosEndpoint intraZoneTask) {
        this.intraZoneTask = intraZoneTask;
    }

    public boolean existsInterZoneAudit(){
        boolean hasValue = this.interZoneAudit != null;
        return(hasValue);
    }

    public PetasosEndpoint getInterZoneAudit() {
        return interZoneAudit;
    }

    public void setInterZoneAudit(PetasosEndpoint interZoneAudit) {
        this.interZoneAudit = interZoneAudit;
    }

    public boolean existsInterZoneTask(){
        boolean hasValue = this.interZoneTask != null;
        return(hasValue);
    }

    public PetasosEndpoint getInterZoneTask() {
        return interZoneTask;
    }

    public void setInterZoneTask(PetasosEndpoint interZoneTask) {
        this.interZoneTask = interZoneTask;
    }


    public boolean existsIntrazoneIPC(){
        boolean exists = this.intrazoneIPC != null;
        return(exists);
    }

    public PetasosEndpoint getIntrazoneIPC() {
        return intrazoneIPC;
    }

    public void setIntrazoneIPC(PetasosEndpoint intrazoneIPC) {
        this.intrazoneIPC = intrazoneIPC;
    }

    public boolean existsIntraZoneOAMPubSub(){
        boolean exists = this.intrazoneOAMPubSub != null;
        return(exists);
    }

    public PetasosEndpoint getIntrazoneOAMPubSub() {
        return intrazoneOAMPubSub;
    }

    public void setIntrazoneOAMPubSub(PetasosEndpoint intrazoneOAMPubSub) {
        this.intrazoneOAMPubSub = intrazoneOAMPubSub;
    }

    public boolean existsIntraZoneOAMDiscovery(){
        boolean exists = this.intrazoneOAMDiscovery != null;
        return(exists);
    }

    public PetasosEndpoint getIntrazoneOAMDiscovery() {
        return intrazoneOAMDiscovery;
    }

    public void setIntrazoneOAMDiscovery(PetasosEndpoint intrazoneOAMDiscovery) {
        this.intrazoneOAMDiscovery = intrazoneOAMDiscovery;
    }

    public boolean existsInterZoneIPC(){
        boolean exists = this.interzoneIPC != null;
        return(exists);
    }

    public PetasosEndpoint getInterzoneIPC() {
        return interzoneIPC;
    }

    public void setInterzoneIPC(PetasosEndpoint interzoneIPC) {
        this.interzoneIPC = interzoneIPC;
    }

    public boolean existsInterZoneOAMPubSub(){
        boolean exists = this.interzoneOAMPubSub != null;
        return(exists);
    }

    public PetasosEndpoint getInterzoneOAMPubSub() {
        return interzoneOAMPubSub;
    }

    public void setInterzoneOAMPubSub(PetasosEndpoint interzoneOAMPubSub) {
        this.interzoneOAMPubSub = interzoneOAMPubSub;
    }

    public boolean existsInterZoneOAMDiscovery(){
        boolean exists = this.interzoneOAMDiscovery != null;
        return(exists);
    }

    public PetasosEndpoint getInterzoneOAMDiscovery() {
        return interzoneOAMDiscovery;
    }

    public void setInterzoneOAMDiscovery(PetasosEndpoint interzoneOAMDiscovery) {
        this.interzoneOAMDiscovery = interzoneOAMDiscovery;
    }

    public PetasosEndpointStatusEnum getAggregateStatus() {
        return aggregateStatus;
    }

    public void setAggregateStatus(PetasosEndpointStatusEnum aggregateStatus) {
        this.aggregateStatus = aggregateStatus;
    }

    public PetasosEndpointMap getEndpointMap() {
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

    public boolean existsIntraZoneMetrics(){
        boolean exits = this.intraZoneMetrics != null;
        return(exits);
    }

    public PetasosEndpoint getIntraZoneMetrics() {
        return intraZoneMetrics;
    }

    public void setIntraZoneMetrics(PetasosEndpoint intraZoneMetrics) {
        this.intraZoneMetrics = intraZoneMetrics;
    }

    public boolean existsInterZoneMetrics(){
        boolean exits = this.interZoneMetrics != null;
        return(exits);
    }

    public PetasosEndpoint getInterZoneMetrics() {
        return interZoneMetrics;
    }

    public void setInterZoneMetrics(PetasosEndpoint interZoneMetrics) {
        this.interZoneMetrics = interZoneMetrics;
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
