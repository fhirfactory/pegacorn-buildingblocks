/*
 * Copyright (c) 2022 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.core.participants.management;

import net.fhirfactory.pegacorn.core.interfaces.participant.ParticipantSerivcesAgentInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.petasos.core.participants.cache.LocalParticipantCache;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class LocalParticipantManager {
    private static final Logger LOG = LoggerFactory.getLogger(LocalParticipantManager.class);

    private Instant startupInstant;

    private static final Long PARTICIPANT_MANAGEMENT_DAEMON_STARTUP_DELAY = 60000L;
    private static final Long PARTICIPANT_MANAGEMENT_DEAMON_EXECUTIION_PERIOD = 30000L;
    private boolean initialised;

    @Inject
    private ParticipantSerivcesAgentInterface globalParticipantManagementService;

    @Inject
    private LocalParticipantCache localRegistrationCache;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private ProcessingPlantMetricsAgentAccessor metricsAgentAccessor;

    //
    // Constructor(s)
    //

    public LocalParticipantManager(){
        this.initialised = false;
        this.startupInstant = Instant.now();
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(initialised){
            getLogger().debug(".initialise(): Exit, already initialised, nothing to do!");
            return;
        }
        getLogger().info(".initialise(): Initialisation Start...");
        scheduleParticipantManagementDaemon();
        setInitialised(true);
        getLogger().info(".initialise(): Initialisation Finish...");
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(this.processingPlant);
    }

    protected ParticipantSerivcesAgentInterface getGlobalParticipantManagementService(){
        return(this.globalParticipantManagementService);
    }

    protected LocalParticipantCache getLocalRegistrationCache(){
        return(localRegistrationCache);
    }

    protected ProcessingPlantMetricsAgent getProcessingPlantMetricsAgent(){
        return(metricsAgentAccessor.getMetricsAgent());
    }

    public Instant getStartupInstant() {
        return startupInstant;
    }

    //
    // Daemon
    //

    public void scheduleParticipantManagementDaemon(){
        getLogger().debug(".scheduleParticipantManagementDaemon(): Entry");
        TimerTask participantManagementDaemonTask = new TimerTask() {
            public void run() {
                getLogger().debug(".participantManagementDaemonTask(): Entry");
                participantManagementDaemon();
                getLogger().debug(".participantManagementDaemonTask(): Exit");
            }
        };
        Timer timer = new Timer("participantManagementDaemonTask");
        timer.schedule(participantManagementDaemonTask, PARTICIPANT_MANAGEMENT_DAEMON_STARTUP_DELAY, PARTICIPANT_MANAGEMENT_DEAMON_EXECUTIION_PERIOD);
        getLogger().debug(".scheduleParticipantManagementDaemon(): Exit");
    }

    /**
     * This daemon essentially performs 2 tasks -> 1) to receive updates (via polling) of the Participant
     * "suspend-state"/"queued-message-state" from Ponos and 2) to communicate operational readiness state
     * of the local Participants to Ponos.
     */
    public void participantManagementDaemon(){
        getLogger().debug(".participantManagementDaemon(): Entry");
        try {

            getProcessingPlantMetricsAgent().touchWatchDogActivityIndicator("ParticipantManagementDaemon");

            // Synchronize Local with the Global Manager
            getLogger().debug(".participantManagementDaemon(): [Synchronise Participant Globally (within Ponos)] Start");
            Set<String> allRegisteredComponentIds = getLocalRegistrationCache().getAllRegisteredComponentIds();
            for (String currentLocalRegisteredComponentIdValue : allRegisteredComponentIds) {
                getLogger().trace(".participantManagementDaemon(): [Synchronise Participant Globally (within Ponos)] processing->{}", currentLocalRegisteredComponentIdValue);
                PetasosParticipant currentLocalParticipant = getLocalRegistrationCache().getParticipantCloneForComponentIdValue(currentLocalRegisteredComponentIdValue);
                if(getLogger().isTraceEnabled()) {
                    getLogger().trace(".participantManagementDaemon(): [Synchronise Participant Globally (within Ponos)] processing->{}", currentLocalParticipant.getParticipantId().getName());
                }
                if(currentLocalParticipant.getParticipantId().getSubsystemName().equals(getProcessingPlant().getTopologyNode().getParticipantId().getSubsystemName())) {
                    PetasosParticipant globalParticipantRegistration = getGlobalParticipantManagementService().synchroniseRegistration(currentLocalParticipant);
                    getLogger().trace(".participantManagementDaemon(): [Synchronise Participant Globally (within Ponos)] centralRegistration->{}", globalParticipantRegistration);
                    if (globalParticipantRegistration != null) {
                        getLogger().trace(".participantManagementDaemon(): [Synchronise Participant Globally (within Ponos)] Updating local registration");
                        getLocalRegistrationCache().updateParticipantFromCentral(globalParticipantRegistration);
                    }
                }
            }
            getLogger().trace(".participantManagementDaemon(): [Synchronise Participant Globally (within Ponos)] Finish");

            // Now Synchronise any "Downstream" (Subscribers)
            getLogger().trace(".participantManagementDaemon(): [Synchronise Downstream Systems Participants (from Ponos)] Start");
            synchroniseExternalSubscriberParticipants();
            getLogger().trace(".participantManagementDaemon(): [Synchronise Downstream Systems Participants (from Ponos)] Finish");
            // Now Synchronise any "Downstream" (Subscribers)
        } catch(Exception ex){
            getLogger().warn(".participantManagementDaemon(): Exception ->", ex);
        }

        getLogger().debug(".participantManagementDaemon(): Exit");
    }

    //
    // Business Methods
    //

    public Set<String> getAllRegisteredComponentIds(){
        Set<String> allRegisteredComponentIds = getLocalRegistrationCache().getAllRegisteredComponentIds();
        return(allRegisteredComponentIds);
    }

    public PetasosParticipant registerParticipant(PetasosParticipant participant){
        getLogger().debug(".registerParticipant(): Entry, participant->{}", participant);
        if(participant == null ){
            getLogger().debug(".registerParticipant(): Exit, publisherId or publishedTopics is null, not registering anything");
            return(null);
        }

        //
        // 1st, some basic defensive programming and parameter checking
        if(participant.getComponentId() == null){
            throw(new IllegalArgumentException("participant.GetComponentId() cannot be null, participant->" + participant));
        }
        if(participant.getParticipantId() == null){
            throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, participant->" + participant));
        }
        if(StringUtils.isEmpty(participant.getParticipantId().getName())){
            throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, participant->" + participant));
        }

        // Register with local Cache/Administrator
        getLogger().trace(".registerParticipant(): [Register Participant Locally] Start");
        PetasosParticipant localParticipant = getLocalRegistrationCache().addLocalParticipant(participant);
        getLogger().trace(".registerParticipant(): [Register Participant Locally] Finish");

        // Register with the Global Manager
        getLogger().trace(".registerParticipant(): [Register Participant Globally (within Ponos)] Start");
        PetasosParticipant centrallyRegisteredParticipant = null;
        if(Instant.now().isAfter(getStartupInstant().plusSeconds(PARTICIPANT_MANAGEMENT_DAEMON_STARTUP_DELAY))) {
            centrallyRegisteredParticipant = getGlobalParticipantManagementService().synchroniseRegistration(participant);
        } else {
            centrallyRegisteredParticipant = SerializationUtils.clone(participant);
            centrallyRegisteredParticipant.setCentralRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_UNREGISTERED);
            centrallyRegisteredParticipant.setControlStatus(PetasosParticipantControlStatusEnum.PARTICIPANT_IS_SUSPENDED);
            centrallyRegisteredParticipant.setCentralRegistrationInstant(Instant.now());
        }
        getLogger().trace(".registerParticipant(): [Register Participant Globally (within Ponos)] Finish");

        // Update local Participant with information from global registration
        getLogger().trace(".registerParticipant(): [Synchronise Local Participant] Start");
        PetasosParticipant updatedParticipant = getLocalRegistrationCache().updateParticipantFromCentral(centrallyRegisteredParticipant);
        getLogger().trace(".registerParticipant(): [Synchronise Local Participant] Finish");

        //
        // Our work is done
        getLogger().debug(".registerParticipant(): Exit, updatedParticipant->{}", updatedParticipant);
        return(updatedParticipant);
    }

    public Set<PetasosParticipant> getDownstreamParticipants(){
        Set<PetasosParticipant> downstreamParticipantSet = getLocalRegistrationCache().getDownstreamParticipantSet();
        return(downstreamParticipantSet);
    }

    //
    // External (to this ProcessingPlant) Subscriber Synchronisation
    //

    public void synchroniseExternalSubscriberParticipants(){
        getLogger().debug(".synchroniseExternalSubscriberParticipants(): [Synchronise My Participant/Publishing] Start");
        String processingPlantParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Set<PetasosParticipant> subscribers = getGlobalParticipantManagementService().getDownstreamSubscribers(processingPlantParticipantName);
        if (!subscribers.isEmpty()) {
            getLogger().trace(".synchroniseExternalSubscriberParticipants(): [Synchronise My Participant/Publishing] There are downstream subscribers!");
            for (PetasosParticipant currentSubscriber : subscribers) {
                if (getLogger().isTraceEnabled()) {
                    getLogger().trace(".synchroniseExternalSubscriberParticipants(): [Synchronise My Participant/Publishing] downstream subsystem participantName->{}", currentSubscriber.getParticipantId().getSubsystemName());
                }
                if(getLocalRegistrationCache().getParticipantClone(currentSubscriber.getParticipantId().getName()) == null) {
                    getLocalRegistrationCache().addCentrallyStoredParticipant(currentSubscriber);
                } else {
                    getLocalRegistrationCache().updateParticipantFromCentral(currentSubscriber);
                }
            }
        }
        getLogger().debug(".synchroniseExternalSubscriberParticipants(): [Synchronise My Participant/Publishing] Finish");
    }

    public PetasosParticipant getLocalParticipantForComponentId(ComponentIdType componentId){
        getLogger().debug(".getLocalParticipantForComponentId(): Entry, componentId->{}", componentId);
        PetasosParticipant registration = getLocalRegistrationCache().getParticipantCloneForComponentId(componentId);
        getLogger().debug(".getLocalParticipantForComponentId(): Exit, registration->{}", registration);
        return(registration);
    }

    public PetasosParticipant getLocalParticipantForComponentIdValue(String componentIdValue){
        getLogger().debug(".getLocalParticipantForComponentIdValue(): Entry, componentIdValue->{}", componentIdValue);
        PetasosParticipant registration = getLocalRegistrationCache().getParticipantCloneForComponentIdValue(componentIdValue);
        getLogger().debug(".getLocalParticipantForComponentIdValue(): Exit, registration->{}", registration);
        return(registration);
    }

    public void synchroniseLocalWithCentralCacheDetail( PetasosParticipant centralRegistration){
        getLogger().debug(".synchroniseLocalWithCentralCacheDetail(): Entry, centralRegistration->{}", centralRegistration);
        if(centralRegistration != null) {
            //
            // Update local DM with Gobal details
            getLogger().trace(".synchroniseLocalWithCentralCacheDetail(): [Synchronise Local Cache] Start");
            getLocalRegistrationCache().updateParticipantFromCentral(centralRegistration);
            getLogger().trace(".synchroniseLocalWithCentralCacheDetail(): [Synchronise Local Cache] Finish");
        }
        getLogger().debug(".synchroniseLocalWithCentralCacheDetail(): Exit");
    }

    public boolean isParticipantSuspended(String participantName){
        boolean isSuspended = getLocalRegistrationCache().isParticipantSuspended(participantName);
        return(isSuspended);
    }

    public boolean isParticipantSuspended(TaskPerformerTypeType performer){
        if(performer == null){
            return(false);
        }
        if(performer.getKnownTaskPerformer() != null) {
            if(StringUtils.isNotEmpty(performer.getKnownTaskPerformer().getName())) {
                boolean isSuspended = getLocalRegistrationCache().isParticipantSuspended(performer.getKnownTaskPerformer().getName());
                return (isSuspended);
            }
        }
        return(false);
    }

    public boolean isParticipantDisabled(String participantName){
        boolean isSuspended = getLocalRegistrationCache().isParticipantDisabled(participantName);
        return(isSuspended);
    }

    public boolean isParticipantDisabled(TaskPerformerTypeType performer){
        if(performer == null){
            return(false);
        }
        if(performer.getKnownTaskPerformer() != null) {
            if(StringUtils.isNotEmpty(performer.getKnownTaskPerformer().getName())) {
                boolean isDisabled = getLocalRegistrationCache().isParticipantDisabled(performer.getKnownTaskPerformer().getName());
                return (isDisabled);
            }
        }
        return(false);
    }

    public boolean participantHasOffloadedTasks(TaskPerformerTypeType performer){
        if(performer == null){
            return(false);
        }
        if(performer.getKnownTaskPerformer() != null) {
            if(StringUtils.isNotEmpty(performer.getKnownTaskPerformer().getName())) {
                boolean hasOffloadedTasks = participantHasOffloadedTasks(performer.getKnownTaskPerformer().getName());
                return (hasOffloadedTasks);
            }
        }
        return(false);
    }
    public boolean participantHasOffloadedTasks(String participantName){
        if(StringUtils.isEmpty(participantName)){
            return(false);
        }
        boolean hasOffloadedTasks =  getLocalRegistrationCache().participantHasOffloadedTasks(participantName);
        return(hasOffloadedTasks);
    }

    public void updateParticipantStatus(String participantName, PetasosParticipantStatusEnum status){
        getLocalRegistrationCache().updateParticipantStatus(participantName, status);
    }

    public PetasosParticipantStatusEnum getParticipantStatus(String participantName){
        PetasosParticipantStatusEnum participantStatus = getLocalRegistrationCache().getParticipantStatus(participantName);
        return(participantStatus);
    }

    public void touchParticipantUtilisationInstant(String participantName){
        getLocalRegistrationCache().touchParticipantUtilisationInstant(participantName);
    }

    public Instant getParticipantLastUtilisationInstant(String participantName){
        Instant participantLastUtilisationInstant = getLocalRegistrationCache().getParticipantLastUtilisationInstant(participantName);
        return(participantLastUtilisationInstant);
    }

    public PetasosParticipantId getParticipantId(String participantName){
        PetasosParticipantId participantId = getLocalRegistrationCache().getParticipantId(participantName);
        return(participantId);
    }
}
