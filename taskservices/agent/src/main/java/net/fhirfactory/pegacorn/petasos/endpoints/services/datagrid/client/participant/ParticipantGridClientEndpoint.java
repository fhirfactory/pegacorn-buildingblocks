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
package net.fhirfactory.pegacorn.petasos.endpoints.services.datagrid.client.participant;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskRepositoryServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.model.petasos.participant.*;
import net.fhirfactory.pegacorn.petasos.endpoints.services.subscriptions.ParticipantServicesEndpointBase;
import net.fhirfactory.pegacorn.petasos.participants.manager.LocalParticipantManager;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class ParticipantGridClientEndpoint extends ParticipantServicesEndpointBase {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipantGridClientEndpoint.class);

    private static Long PETASOS_PARTICIPANT_MAP_CACHE_SYNCHRONISATION_INITIAL_WAIT = 60000L;
    private static Long PETASOS_PARTICIPANT_MAP_CACHE_SYNCHRONISATION_PERIOD = 300000L;

    private static Long EXECUTION_STATUS_SYNCHRONISATION_INITIAL_WAIT = 60000L;
    private static Long EXECUTION_STATUS_SYNCHRONISATION_PERIOD = 30000L;

    private boolean synchronisationDaemonInitialised;

    @Inject
    private PetasosTaskRepositoryServiceProviderNameInterface taskServiceProviderName;

    @Inject
    private LocalParticipantManager localParticipantManager;

    //
    // Constructor(s)
    //

    public ParticipantGridClientEndpoint(){
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
                public void run() {
                    getLogger().debug(".petasosParticipantCacheSynchronisationDaemonScheduler(): Entry");
                    petasosParticipantCacheSynchronisationDaemon();
                    getLogger().debug(".petasosParticipantCacheSynchronisationDaemonScheduler(): Exit");
                }
            };
            Timer petasosParticipantCacheSynchronisationTimer = new Timer("PetasosParticipantCacheSynchronisationDaemonSchedule");
            petasosParticipantCacheSynchronisationTimer.schedule(petasosParticipantCacheSynchronisationDaemonScheduler, PETASOS_PARTICIPANT_MAP_CACHE_SYNCHRONISATION_INITIAL_WAIT, PETASOS_PARTICIPANT_MAP_CACHE_SYNCHRONISATION_PERIOD);

            TimerTask participantExecutionStatusSynchronisationDaemonScheduler = new TimerTask() {
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

    public void petasosParticipantCacheSynchronisationDaemon(){
        getLogger().debug(".petasosParticipantCacheSynchronisationDaemon(): Entry");

        try {
            getLogger().trace(".petasosParticipantCacheSynchronisationDaemon(): [Synchronise My Participant/Subscriptions] Start");
            PetasosParticipantRegistrationSet localParticipantSet = localParticipantManager.getParticipantRegistrationSet();
            PetasosParticipantRegistrationSet updatedRegistrationSet = updateParticipantRegistrationSet(getProcessingPlant().getSubsystemParticipantName(), localParticipantSet);
            getLogger().trace(".petasosParticipantCacheSynchronisationDaemon(): [Synchronise My Participant/Subscriptions] Finish");
            getProcessingPlantMetricsAgent().touchWatchDogActivityIndicator("ParticipantCacheSynchronisationDaemon");
            getLogger().trace(".petasosParticipantCacheSynchronisationDaemon(): [Synchronise My Participant/Publishing] Finish");
        } catch (Exception daemonException){
            getLogger().error(".petasosParticipantCacheSynchronisationDaemon(): Exception, message->{}, stackTrace->{}", daemonException.getMessage(), daemonException.getStackTrace());
        }
        getLogger().debug(".petasosParticipantCacheSynchornisationDaemon(): Exit");
    }

    public void participantExecutionStatusSynchronisationDaemon(){
        getLogger().debug(".participantExecutionStatusSynchronisationDaemon(): Entry");
        try {
            getLogger().trace(".petasosParticipantCacheSynchronisationDaemon(): [Synchronise My Participant Status] Start");
            PetasosParticipantRegistrationSet localParticipantSet = localParticipantManager.getParticipantRegistrationSet();
            if(localParticipantSet != null){
                PetasosParticipantStatusSet statusSet = new PetasosParticipantStatusSet();
                for(PetasosParticipantRegistration currentRegistration: localParticipantSet.getRegistrationSet().values()){
                    PetasosParticipantStatus currentStatus = new PetasosParticipantStatus();
                    currentStatus.setControlStatus(currentRegistration.getParticipant().getParticipantControlStatus());
                    currentStatus.setOperationalStatus(currentRegistration.getParticipant().getParticipantStatus());
                    currentStatus.setParticipantName(currentRegistration.getParticipant().getParticipantName());
                    statusSet.addStatus(currentStatus);
                }
                PetasosParticipantStatusSet updatedStatusSet = updateParticipantStatusSet(getProcessingPlant().getSubsystemParticipantName(), statusSet);
            }
            getLogger().trace(".participantExecutionStatusSynchronisationDaemon(): [Synchronise My Participant Status] Finish");
        } catch (Exception daemonException){
            getLogger().error(".participantExecutionStatusSynchronisationDaemon(): Exception, message->{}, stackTrace->{}", daemonException.getMessage(), daemonException.getStackTrace());
        }
        getLogger().debug(".participantExecutionStatusSynchronisationDaemon(): Exit");
    }

    //
    // Endpoint Changes
    //

    //
    // Business Methods
    //

    @Override
    public boolean isPetasosParticipantRegistered(PetasosParticipant publisher) {
        return false;
    }


    @Override
    public PetasosParticipantRegistration registerPetasosParticipant(PetasosParticipant participant) {
        getLogger().debug(".registerPetasosParticipant(): Entry, participant->{}", participant);
        if(participant == null ){
            getLogger().trace(".registerPetasosParticipant: participant is null");
            PetasosParticipantRegistration registration = new PetasosParticipantRegistration();
            registration.setRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PETASOS_PARTICIPANT_REGISTRATION_FAILED);
            registration.setRegistrationCommentary("participant (PetasosParticipant) is null");
            registration.setRegistrationInstant(Instant.now());
            getLogger().debug(".registerPetasosParticipant(): Exit");
            return(registration);
        }
        if(participant.getSubsystemParticipantName() == null){
            participant.setSubsystemParticipantName(getProcessingPlant().getSubsystemParticipantName());
        }
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".registerPetasosParticipant(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null){
            getLogger().trace(".registerPetasosParticipant(): Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            PetasosParticipantRegistration registration = new PetasosParticipantRegistration();
            registration.setRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PETASOS_PARTICIPANT_REGISTRATION_FAILED);
            registration.setRegistrationCommentary(" Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            registration.setRegistrationInstant(Instant.now());
            getLogger().debug(".registerPetasosParticipant(): Exit, (Wrong endpoint technology (should be JGroups))");
            return(registration);
        }
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = participant;
            classSet[0] = PetasosParticipant.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantRegistration registration = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "registerPetasosParticipant", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".registerPetasosParticipant(): Exit, registration->{}", registration);
            return(registration);
        } catch (NoSuchMethodException e) {
            getLogger().debug(".registerPetasosParticipant(): Error (NoSuchMethodException)->", e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            PetasosParticipantRegistration registration = new PetasosParticipantRegistration();
            registration.setRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PETASOS_PARTICIPANT_REGISTRATION_FAILED);
            registration.setRegistrationInstant(Instant.now());
            registration.setRegistrationCommentary("Error (NoSuchMethodException)" + e.getMessage());
            return(registration);
        } catch (Exception e) {
            getLogger().debug(".registerPetasosParticipant(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            PetasosParticipantRegistration registration = new PetasosParticipantRegistration();
            registration.setRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PETASOS_PARTICIPANT_REGISTRATION_FAILED);
            registration.setRegistrationInstant(Instant.now());
            registration.setRegistrationCommentary("Error (NoSuchMethodException)" + e.getMessage());
            return(registration);
        }
    }

    @Override
    public PetasosParticipantRegistration updatePetasosParticipant(PetasosParticipant participant) {
        getLogger().debug(".updatePetasosParticipant(): Entry, participant->{}", participant);
        if(participant == null){
            return(null);
        }
        if(participant.getSubsystemParticipantName() == null){
            participant.setSubsystemParticipantName(getProcessingPlant().getSubsystemParticipantName());
        }
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".updatePetasosParticipant(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".updatePetasosParticipant: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = participant;
            classSet[0] = PetasosParticipant.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantRegistration registration = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "updatePetasosParticipant", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".getTaskPerformersForTaskProducer(): Exit, registration->{}", registration);
            return(registration);
        } catch (NoSuchMethodException e) {
            getLogger().error(".getTaskPerformersForTaskProducer(): Error (NoSuchMethodException)->", e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            PetasosParticipantRegistration registration = new PetasosParticipantRegistration();
            registration.setRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PETASOS_PARTICIPANT_REGISTRATION_FAILED);
            registration.setRegistrationCommentary("Error (NoSuchMethodException)");
            registration.setRegistrationInstant(Instant.now());
            getLogger().debug(".getTaskPerformersForTaskProducer(): Exit, registration->{}", registration);
            return(registration);
        } catch (Exception e) {
            getLogger().debug(".getTaskPerformersForTaskProducer(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            PetasosParticipantRegistration registration = new PetasosParticipantRegistration();
            registration.setRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PETASOS_PARTICIPANT_REGISTRATION_FAILED);
            registration.setRegistrationCommentary("Error (GeneralException)");
            registration.setRegistrationInstant(Instant.now());
            getLogger().debug(".getTaskPerformersForTaskProducer(): Exit, registration->{}", registration);
            return(null);
        }
    }

    @Override
    public PetasosParticipantRegistration deregisterPetasosParticipant(PetasosParticipant participant) {
        getLogger().debug(".deregisterPetasosParticipant(): Entry, participant->{}", participant);
        if(participant == null){
            return(null);
        }
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".deregisterPetasosParticipant(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".deregisterPetasosParticipant: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = participant;
            classSet[0] = PetasosParticipant.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantRegistration registration = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "deregisterPetasosParticipant", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".deregisterPetasosParticipant(): Exit, registration->{}", registration);
            return(registration);
        } catch (NoSuchMethodException e) {
            getLogger().error(".deregisterPetasosParticipant(): Error (NoSuchMethodException)->", e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(null);
        } catch (Exception e) {
            getLogger().error(".deregisterPetasosParticipant(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(null);
        }
    }

    @Override
    public PetasosParticipantRegistration getPetasosParticipantRegistration(String participantName) {
        getLogger().debug(".getPetasosParticipantRegistration(): Entry, participantName->{}", participantName);
        if(participantName == null){
            return(null);
        }
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".getPetasosParticipantRegistration(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".getPetasosParticipantRegistration(): Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantRegistration registration = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "getPetasosParticipantRegistration", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".getPetasosParticipantRegistration(): Exit, registration->{}", registration);
            return(registration);
        } catch (NoSuchMethodException e) {
            getLogger().error(".getPetasosParticipantRegistration(): Error (NoSuchMethodException)->", e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(null);
        } catch (Exception e) {
            getLogger().error(".getPetasosParticipantRegistration(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(null);
        }
    }

    @Override
    public PetasosParticipantRegistrationSet updateParticipantRegistrationSet(String participantName, PetasosParticipantRegistrationSet registrationSet) {
        getLogger().debug(".updateParticipantRegistrationSet(): Entry, participantName->{}", participantName);
        if(participantName == null){
            return(null);
        }
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".updateParticipantRegistrationSet(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".updateParticipantRegistrationSet(): Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, registrationSet};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantRegistrationSet registration = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "updateParticipantRegistrationSet", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".updateParticipantRegistrationSet(): Exit, registration->{}", registration);
            return(registration);
        } catch (NoSuchMethodException e) {
            getLogger().error(".updateParticipantRegistrationSet(): Error (NoSuchMethodException)->", e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(null);
        } catch (Exception e) {
            getLogger().error(".updateParticipantRegistrationSet(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(null);
        }
    }

    @Override
    public PetasosParticipantStatusSet updateParticipantStatusSet(String participantName, PetasosParticipantStatusSet statusSet) {
        getLogger().debug(".updateParticipantStatusSet(): Entry, participantName->{}", participantName);
        if(participantName == null){
            return(null);
        }
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".updateParticipantStatusSet(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".updateParticipantStatusSet(): Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, statusSet};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantStatusSet updatedStatusSet = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "updateParticipantStatusSet", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".updateParticipantStatusSet(): Exit, updatedStatusSet->{}", updatedStatusSet);
            return(updatedStatusSet);
        } catch (NoSuchMethodException e) {
            getLogger().error(".updateParticipantStatusSet(): Error (NoSuchMethodException)->", e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(null);
        } catch (Exception e) {
            getLogger().error(".updateParticipantStatusSet(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(null);
        }
    }

    @Override
    public Set<PetasosParticipantRegistration> getAllRegistrations() {
        getLogger().debug(".getAllRegistrations(): Entry");
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".getAllRegistrations(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".getAllRegistrations: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (new HashSet<>());
        }
        try {
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Set<PetasosParticipantRegistration> participantSet = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "getAllRegistrations", null, null, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".getTaskPerformerServiceRegistration(): Exit, participantSet->{}", participantSet);
            return(participantSet);
        } catch (NoSuchMethodException e) {
            getLogger().error(".getTaskPerformerServiceRegistration(): Error (NoSuchMethodException)->", e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(new HashSet<>());
        } catch (Exception e) {
            getLogger().error(".getTaskPerformerServiceRegistration(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(new HashSet<>());
        }
    }

    @Override
    protected Logger specifyLogger() {
        return LOG;
    }

    @Override
    public PetasosParticipant getMyPetasosParticipant() {
        return (getParticipantHolder().getMyProcessingPlantPetasosParticipant());
    }
}
