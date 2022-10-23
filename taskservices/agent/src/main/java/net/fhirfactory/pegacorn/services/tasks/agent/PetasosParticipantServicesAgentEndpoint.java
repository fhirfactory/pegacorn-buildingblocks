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
package net.fhirfactory.pegacorn.services.tasks.agent;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskRepositoryServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.interfaces.ui.resources.ParticipantUIServicesAPI;
import net.fhirfactory.pegacorn.core.interfaces.ui.resources.TaskUIServicesAPI;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.ITOpsNotificationContent;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PetasosParticipantESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.PetasosParticipantSummary;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.TaskSummary;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.endpoints.services.participants.PetasosParticipantServicesEndpointBase;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class PetasosParticipantServicesAgentEndpoint extends PetasosParticipantServicesEndpointBase implements ParticipantUIServicesAPI, TaskUIServicesAPI {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosParticipantServicesAgentEndpoint.class);

    private static Long PETASOS_PARTICIPANT_MAP_CACHE_SYNCHRONISATION_INITIAL_WAIT = 60000L;
    private static Long PETASOS_PARTICIPANT_MAP_CACHE_SYNCHRONISATION_PERIOD = 30000L;

    private static Long EXECUTION_STATUS_SYNCHRONISATION_INITIAL_WAIT = 60000L;
    private static Long EXECUTION_STATUS_SYNCHRONISATION_PERIOD = 30000L;

    private boolean synchronisationDaemonInitialised;

    @Inject
    private PetasosTaskRepositoryServiceProviderNameInterface taskServiceProviderName;

    @Inject
    private LocalParticipantManager participantManager;

    //
    // Constructor(s)
    //

    public PetasosParticipantServicesAgentEndpoint(){
        super();
        this.synchronisationDaemonInitialised = false;
    }

    //
    // Post Construct
    //

    @Override
    protected void initialiseCacheSynchronisationDaemon() {
        getLogger().debug(".initialiseCacheSynchronisationDaemon(): Entry");
        scheduleSynchronisationDaemons();
        getLogger().debug(".initialiseCacheSynchronisationDaemon(): Exit");
    }



    //
    // Synchronisation Daemon
    //

    /**
     *
     */
    public void scheduleSynchronisationDaemons() {
        getLogger().debug(".scheduleSynchronisationDaemons(): Entry");
        if (synchronisationDaemonInitialised) {
            // do nothing, it is already scheduled
        } else {
            TimerTask petasosParticipantCacheSynchronisationDaemonScheduler = new TimerTask() {
                @Override
				public void run() {
                    getLogger().debug(".petasosParticipantCacheSynchronisationDaemonScheduler(): Entry");
                    participantSynchronisationDaemon();
                    getLogger().debug(".petasosParticipantCacheSynchronisationDaemonScheduler(): Exit");
                }
            };
            Timer petasosParticipantCacheSynchronisationTimer = new Timer("PetasosParticipantCacheSynchronisationDaemonSchedule");
            petasosParticipantCacheSynchronisationTimer.schedule(petasosParticipantCacheSynchronisationDaemonScheduler, PETASOS_PARTICIPANT_MAP_CACHE_SYNCHRONISATION_INITIAL_WAIT, PETASOS_PARTICIPANT_MAP_CACHE_SYNCHRONISATION_PERIOD);

            TimerTask participantExecutionStatusSynchronisationDaemonScheduler = new TimerTask() {
                @Override
				public void run() {
                    getLogger().debug(".participantExecutionStatusSynchronisationDaemonScheduler(): Entry");
                    participantExecutionStatusSynchronisationDaemon();
                    getLogger().debug(".participantExecutionStatusSynchronisationDaemonScheduler(): Exit");
                }
            };
            Timer participantExecutionStatusSynchronisationDaemonTimer = new Timer("ParticipantExecutionStatusSynchronisationDaemonSchedule");
            participantExecutionStatusSynchronisationDaemonTimer.schedule(participantExecutionStatusSynchronisationDaemonScheduler, EXECUTION_STATUS_SYNCHRONISATION_INITIAL_WAIT, EXECUTION_STATUS_SYNCHRONISATION_PERIOD);
            synchronisationDaemonInitialised = true;
        }
        getLogger().debug(".scheduleSynchronisationDaemons(): Exit");
    }

    public void participantSynchronisationDaemon(){
        getLogger().debug(".participantSynchronisationDaemon(): Entry");

        try {
            //
            // 1st, let's synchronise my ProcessingPlant
            getLogger().trace(".participantSynchronisationDaemon(): [Synchronise My Participant/Subscriptions] Start");
            PetasosParticipant myProcessingPlantPetasosParticipant = getParticipantHolder().getParticipant();
            getLogger().trace(".participantSynchronisationDaemon(): [Synchronise My Participant/Subscriptions] myProcessingPlantPetasosParticipant->{}", myProcessingPlantPetasosParticipant);
            PetasosParticipantRegistration localRegistration = participantManager.getLocalParticipantRegistration(myProcessingPlantPetasosParticipant.getComponentId());
            getLogger().trace(".participantSynchronisationDaemon(): [Synchronise My Participant/Subscriptions] localRegistration->{}", localRegistration);
            if (localRegistration == null) {
                getLogger().error(".participantSynchronisationDaemon(): My PetasosParticipantRegistration is all wrong!");
            } else {
                getLogger().trace(".participantSynchronisationDaemon(): localRegistration->{}", localRegistration);
                if (localRegistration.getCentralRegistrationStatus().equals(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTERED)
                    || localRegistration.getCentralRegistrationStatus().equals(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTRATION_FAILED)) {
                    PetasosParticipantRegistration centralRegistration = synchroniseRegistration(localRegistration);
                    participantManager.synchroniseLocalWithCentralCacheDetail(centralRegistration);
                }
            }
            getLogger().trace(".participantSynchronisationDaemon(): [Synchronise My Participant/Subscriptions] Finish");
            //
            // 2nd, let's synchronise my publishing
            getLogger().trace(".participantSynchronisationDaemon(): [Synchronise My Participant/Publishing] Start");
            String myProcessingPlantParticipantName = getSubsystemParticipantName();
            Set<PetasosParticipantRegistration> downstreamTaskPerformers = this.getDownstreamSubscribers(myProcessingPlantParticipantName);
            if (!downstreamTaskPerformers.isEmpty()) {
                getLogger().trace(".participantSynchronisationDaemon(): [Synchronise My Participant/Publishing] There are downstream subscribers!");
                for (PetasosParticipantRegistration currentDownstreamPerformer : downstreamTaskPerformers) {
                    if (getLogger().isTraceEnabled()) {
                        getLogger().trace(".participantSynchronisationDaemon(): [Synchronise My Participant/Publishing] downstream subsystem participantName->{}", currentDownstreamPerformer.getParticipantId().getSubsystemName());
                    }
                    participantManager.synchroniseLocalWithCentralCacheDetail(currentDownstreamPerformer);
                }
            }
            getProcessingPlantMetricsAgent().touchWatchDogActivityIndicator("ParticipantCacheSynchronisationDaemon");
            getLogger().trace(".participantSynchronisationDaemon(): [Synchronise My Participant/Publishing] Finish");
        } catch (Exception daemonException){
            getLogger().error(".participantSynchronisationDaemon(): Exception, message->{}, stackTrace->{}", daemonException.getMessage(), daemonException.getStackTrace());
        }
        getLogger().debug(".participantSynchronisationDaemon(): Exit");
    }

    public void participantExecutionStatusSynchronisationDaemon(){
        getLogger().debug(".participantExecutionStatusSynchronisationDaemon(): Entry");

        getLogger().debug(".participantExecutionStatusSynchronisationDaemon(): Exit");
    }

    //
    // Endpoint Changes
    //

    //
    // Business Methods
    //

    @Override
    public PetasosParticipantRegistration synchroniseRegistration(PetasosParticipantRegistration localRegistration) {
        getLogger().debug(".registerParticipant(): Entry, localRegistration->{}", localRegistration);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        if(localRegistration == null ){
            getLogger().trace(".registerParticipant: localRegistration is null");
            getLogger().debug(".registerParticipant(): Exit");
            return(null);
        }
        if(StringUtils.isEmpty(localRegistration.getParticipantId().getSubsystemName())){
            localRegistration.getParticipantId().setSubsystemName(getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
        }
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".registerParticipant(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null){
            getLogger().warn(".registerParticipant(): Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            getLogger().debug(".registerParticipant(): Exit, (Wrong endpoint technology (should be JGroups))");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, localRegistration};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantRegistration participantStatus = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "synchroniseRegistrationHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".registerParticipant(): Exit, participantStatus->{}", participantStatus);
            return(participantStatus);
        } catch (Exception e) {
            getLogger().warn(".registerParticipant(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            PetasosParticipantRegistration clonedRegistration = new PetasosParticipantRegistration(localRegistration);
            clonedRegistration.setCentralRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTRATION_FAILED);
            clonedRegistration.setCentralRegistrationInstant(Instant.now());
            return(clonedRegistration);
        }
    }

    @Override
    public Set<PetasosParticipantRegistration> getDownstreamSubscribers(String producerServiceName) {
        getLogger().debug(".getDownstreamTaskPerformersForTaskProducer(): Entry, producerServiceName->{}", producerServiceName);
        if(StringUtils.isEmpty(producerServiceName)){
            return(new HashSet<>());
        }
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".getDownstreamTaskPerformersForTaskProducer(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".getDownstreamTaskPerformersForTaskProducer: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (new HashSet<>());
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, producerServiceName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Set<PetasosParticipantRegistration> participantSet = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "getDownstreamSubscribersHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".getDownstreamTaskPerformersForTaskProducer(): Exit, participantSet->{}", participantSet);
            return(participantSet);
        } catch (NoSuchMethodException e) {
            getLogger().debug(".getDownstreamTaskPerformersForTaskProducer(): Error (NoSuchMethodException)->", e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(new HashSet<>());
        } catch (Exception e) {
            getLogger().debug(".getDownstreamTaskPerformersForTaskProducer(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(new HashSet<>());
        }
    }

    //
    // Command API
    //

    @Override
    public List<PetasosParticipantSummary> listSubsystems() {
        getLogger().debug(".listSubsystems(): Entry");
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".listSubsystems(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".listSubsystems: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            List<PetasosParticipantSummary> answer = new ArrayList<>();
            return (answer);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            List<PetasosParticipantSummary> answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "listSubsystemsHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listSubsystems(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().debug(".listSubsystems(): Error (GeneralException) ->",e);
            List<PetasosParticipantSummary> answer = new ArrayList<>();
            return(answer);
        }
    }

    @Override
    public List<PetasosParticipantSummary> listParticipants(String subsystemName) {
        getLogger().debug(".listParticipants(): Entry, subsystemName->{}", subsystemName);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".listParticipants(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".listParticipants: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            List<PetasosParticipantSummary> answer =new ArrayList<>();
            return (answer);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, subsystemName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            List<PetasosParticipantSummary> answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "listParticipantsHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listParticipants(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().debug(".listParticipants(): Error (GeneralException) ->",e);
            List<PetasosParticipantSummary> answer = new ArrayList<>();
            return(answer);
        }
    }

    @Override
    public PetasosParticipantSummary setControlStatus(String participantName, PetasosParticipantControlStatusEnum controlStatus) {
        getLogger().debug(".setControlStatus(): Entry, participantName->{}", participantName);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".setControlStatus(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".setControlStatus: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantName, controlStatus};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantSummary answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "setControlStatusHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".setControlStatus(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().debug(".setControlStatus(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    @Override
    public List<TaskSummary> listTasks(String participantName, TaskOutcomeStatusEnum status, boolean order, Instant startTime, Instant endTime) {
        getLogger().debug(".listTasks(): Entry, participantName->{}, status->{}, order->{}, startTime->{}, endTime->{}", participantName, status, order, startTime, endTime);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".listTasks(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".listTasks: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (new ArrayList<>());
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantName, status, order, startTime, endTime};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            List<TaskSummary> answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "listTasksHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listTasks(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().debug(".listTasks(): Error (GeneralException) ->",e);
            return(new ArrayList<>());
        }
    }

    @Override
    public TaskSummary initiateTaskRedo(String taskId) {
        getLogger().debug(".initiateTaskRedo(): Entry, taskId->{}}", taskId);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".initiateTaskRedo(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".initiateTaskRedo: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, taskId};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskSummary answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "redoTaskHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".initiateTaskRedo(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().debug(".redoTask(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    @Override
    public TaskSummary initiateTaskCancellation(String taskId) {
        getLogger().debug(".initiateTaskCancellation(): Entry, taskId->{}}", taskId);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".initiateTaskCancellation(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".initiateTaskCancellation: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, taskId};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskSummary answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "cancelTaskHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".initiateTaskCancellation(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().debug(".initiateTaskCancellation(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    @Override
    public PetasosParticipantSummary getParticipantSummary(String participantName) {
        getLogger().debug(".getParticipantSummary(): Entry, participantName->{}", participantName);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".getParticipantSummary(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".getParticipantSummary: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantSummary answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "getParticipantSummaryHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".getParticipantSummary(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().debug(".getParticipantSummary(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    @Override
    public PetasosParticipantESR getParticipantESR(String participantName) {
        getLogger().debug(".getParticipantESR(): Entry, participantName->{}", participantName);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".getParticipantESR(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".getParticipantESR: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantESR answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "getParticipantESRHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getProcessingPlantMetricsAgent().touchPathwaySynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".getParticipantESR(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().debug(".getParticipantESR(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    protected ITOpsNotificationContent makeErrorResponse(String content){
        ITOpsNotificationContent response = new ITOpsNotificationContent();
        if(StringUtils.isEmpty(content)){
            content = "Unknown Error";
        }
        response.setContent(content);
        response.setContentHeading(content);
        response.setFormattedContent("<br>"+content+"</br>");
        return(response);
    }


    //
    // Getters (and Setters)
    //

    @Override
    protected Logger specifyLogger() {
        return LOG;
    }

 }
