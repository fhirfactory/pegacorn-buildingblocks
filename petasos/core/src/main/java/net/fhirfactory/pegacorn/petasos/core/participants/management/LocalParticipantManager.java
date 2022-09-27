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

import net.fhirfactory.pegacorn.core.interfaces.participant.PetasosParticipantManagementServiceInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.petasos.core.participants.administration.LocalParticipantAdministrator;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class LocalParticipantManager {
    private static final Logger LOG = LoggerFactory.getLogger(LocalParticipantManager.class);

    private static final Long PARTICIPANT_MANAGEMENT_DAEMON_STARTUP_DELAY = 45000L;
    private static final Long PARTICIPANT_MANAGEMENT_DEAMON_EXECUTIION_PERIOD = 15000L;
    private boolean initialised;

    @Inject
    private PetasosParticipantManagementServiceInterface globalParticipantManagementService;

    @Inject
    private LocalParticipantAdministrator localParticipantAdministrator;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private ProcessingPlantMetricsAgentAccessor metricsAgentAccessor;

    //
    // Constructor(s)
    //

    public LocalParticipantManager(){
        this.initialised = false;
    }

    //
    // Post Construct
    //

    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(initialised){
            getLogger().debug(".initialise(): Exit, already initialised, nothing to do!");
            return;
        }
        getLogger().info(".initialise(): Initialisation Start...");

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

    protected PetasosParticipantManagementServiceInterface getGlobalParticipantManagementService(){
        return(this.globalParticipantManagementService);
    }

    protected LocalParticipantAdministrator getLocalParticipantAdministrator(){
        return(localParticipantAdministrator);
    }

    protected ProcessingPlantMetricsAgent getProcessingPlantMetricsAgent(){
        return(metricsAgentAccessor.getMetricsAgent());
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

        getProcessingPlantMetricsAgent().touchWatchDogActivityIndicator("ParticipantManagementDaemon");
        getLogger().debug(".participantManagementDaemon(): Exit");
    }

    //
    // Business Methods
    //

    public PetasosParticipantRegistration registerPetasosParticipant(SoftwareComponent participantSoftwareComponent, Set<TaskWorkItemManifestType> publishedTopics, Set<TaskWorkItemManifestType> subscribedTopics) {
        getLogger().debug(".registerPetasosParticipant(): Entry, participantSoftwareComponent->{}", participantSoftwareComponent);
        if (participantSoftwareComponent == null || publishedTopics == null) {
            getLogger().debug(".registerPetasosParticipant(): Exit, publisherId or publishedTopics is null, not registering anything");
            return (null);
        }
        PetasosParticipant participant = new PetasosParticipant(participantSoftwareComponent);
        if (!publishedTopics.isEmpty()) {
            getLogger().trace(".registerPetasosParticipant(): Has published topics");
            for (TaskWorkItemManifestType currentParcelManifest : publishedTopics) {
                participant.getPublishedWorkItemManifests().add(currentParcelManifest);
            }
        }
        if(!subscribedTopics.isEmpty()) {
            getLogger().trace(".registerPetasosParticipant(): Has topics to subscribe to!");
            for (TaskWorkItemManifestType currentParcelManifest : subscribedTopics) {
                TaskWorkItemSubscriptionType currentFilter = new TaskWorkItemSubscriptionType(currentParcelManifest);
                participant.getSubscriptions().add(currentFilter);
            }
        }
        PetasosParticipantRegistration registeredParticipant = registerPetasosParticipant(participant);
        getLogger().debug(".registerPetasosParticipant(): Exit, registeredParticipant->{}", registeredParticipant);
        return(registeredParticipant);
    }


    public PetasosParticipantRegistration registerPetasosParticipant(PetasosParticipant participant){
        getLogger().debug(".registerPetasosParticipant(): Entry, participant->{}", participant);
        if(participant == null ){
            getLogger().debug(".registerPetasosParticipant(): Exit, publisherId or publishedTopics is null, not registering anything");
            return(null);
        }

        // Register with the Global Manager
        getLogger().trace(".registerPetasosParticipant(): [Register Participant Globally (within Ponos)] Start");
        PetasosParticipantRegistration globalParticipantRegistration = getGlobalParticipantManagementService().registerPetasosParticipant(participant);
        getLogger().trace(".registerPetasosParticipant(): [Register Participant Globally (within Ponos)] Finish");

        // Update local Participant with information from global registration
        getLogger().trace(".registerPetasosParticipant(): [Synchronise Local Participant] Start");
        participant.setParticipantStatus(globalParticipantRegistration.getParticipant().getParticipantStatus());
        participant.setSubscriptions(globalParticipantRegistration.getParticipant().getSubscriptions());
        getLogger().trace(".registerPetasosParticipant(): [Synchronise Local Participant] Finish");

        // Register with local Cache/Administrator
        getLogger().trace(".registerPetasosParticipant(): [Register Participant Locally] Start");
        PetasosParticipantRegistration registration = getLocalParticipantAdministrator().registerLocalParticipant(participant);
        getLogger().trace(".registerPetasosParticipant(): [Register Participant Locally] Finish");

        //
        // Our work is done
        getLogger().debug(".registerPetasosParticipant(): Exit, registration->{}", registration);
        return(registration);
    }

    /**
     *
     * @param participant
     */
    public PetasosParticipantRegistration updatePetasosParticipant(PetasosParticipant participant){
        getLogger().debug(".updatePetasosParticipant(): Entry, participant->{}", participant);
        if(participant == null ){
            getLogger().debug(".updatePetasosParticipant(): Exit, participant is null, not updating anything");
            return(null);
        }

        getLogger().trace(".registerPetasosParticipant(): [Update Participant Globally (within Ponos)] Start");
        PetasosParticipantRegistration globalRegistration = getGlobalParticipantManagementService().updatePetasosParticipant(participant);
        getLogger().trace(".registerPetasosParticipant(): [Update Participant Globally (within Ponos)] Finish");

        // Update local Participant with information from global globalRegistration
        getLogger().trace(".registerPetasosParticipant(): [Synchronise Local Participant] Start");
        participant.setParticipantStatus(globalRegistration.getParticipant().getParticipantStatus());
        participant.setSubscriptions(globalRegistration.getParticipant().getSubscriptions());
        getLogger().trace(".registerPetasosParticipant(): [Synchronise Local Participant] Finish");

        //
        // Update local Cache/Administrator
        getLocalParticipantAdministrator().synchroniseLocalWithCentralCacheDetail(globalRegistration);
        if(globalRegistration.getParticipant().getComponentID().equals(myProcessingPlant.getMeAsASoftwareComponent().getComponentID())){
            participantHolder.setMyProcessingPlantPetasosParticipant(globalRegistration.getParticipant());
        }
        getLogger().debug(".updatePetasosParticipant(): Exit, globalRegistration->{}", globalRegistration);
        return(globalRegistration);
    }

    //
    // External (to this ProcessingPlant) Subscriber Synchronisation
    //

    public void synchroniseExternalSubscriberParticipants(){
        getLogger().trace(".synchroniseExternalSubscriberParticipants(): [Synchronise My Participant/Publishing] Start");
        String processingPlantParticipantName = getProcessingPlant().getMeAsASoftwareComponent().getParticipantName();
        Set<PetasosParticipant> downstreamTaskPerformers = getGlobalParticipantManagementService().getDownstreamTaskPerformersForTaskProducer(processingPlantParticipantName);
        if (!downstreamTaskPerformers.isEmpty()) {
            getLogger().trace(".synchroniseExternalSubscriberParticipants(): [Synchronise My Participant/Publishing] There are downstream subscribers!");
            for (PetasosParticipant currentDownstreamPerformer : downstreamTaskPerformers) {
                if (getLogger().isTraceEnabled()) {
                    getLogger().trace(".synchroniseExternalSubscriberParticipants(): [Synchronise My Participant/Publishing] downstream subsystem participantName->{}", currentDownstreamPerformer.getSubsystemParticipantName());
                }
                getLocalParticipantAdministrator().synchroniseLocalWithCentralCacheDetail(currentDownstreamPerformer);
            }
        }
        getLogger().trace(".synchroniseExternalSubscriberParticipants(): [Synchronise My Participant/Publishing] Finish");
    }
}
