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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.execution;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.deployment.properties.reference.petasos.PetasosDefaultProperties;
import net.fhirfactory.pegacorn.petasos.core.participants.cache.LocalParticipantCache;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalTaskJobCardCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LocalParticipantExecutionManager {
    private static final Logger LOG = LoggerFactory.getLogger(LocalParticipantExecutionManager.class);

    private boolean initialised;

    @Inject
    private LocalTaskJobCardCache jobCardCache;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosDefaultProperties petasosDefaultProperties;

    @Inject
    private LocalParticipantCache participantRegistrationCache;

    @Inject
    private LocalParticipantManager participantManager;

    //
    // Constructor(s)
    //

    public LocalParticipantExecutionManager(){
        this.initialised = false;
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){

    }

    //
    // Business Methods
    //

    public TaskExecutionCommandEnum checkExecutionPrivilege(PetasosParticipantId participantId, TaskIdType actionableTaskId){
        getLogger().debug(".checkExecutionPrivilege(): Entry, participantId->{}, actionableTaskId->{}", participantId, actionableTaskId);
        if(participantId == null){
            getLogger().debug(".checkExecutionPrivilege(): Entry, participantId is empty, returning null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL) ;
        }
        if(actionableTaskId == null){
            getLogger().debug(".checkExecutionPrivilege(): Entry, participantId is empty, returning null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
        }

        getLogger().trace(".checkExecutionPrivilege(): [Checking if Participant is -Paused-] Start");
        boolean isPaused = getParticipantRegistrationCache().isParticipantSuspended(participantId.getName());
        if(isPaused){
            getLogger().trace(".checkExecutionPrivilege(): [Checking if Participant is -Paused-] Finish");
            getLogger().debug(".checkExecutionPrivilege(): Exit, participant is -Paused-");
            return(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        } else {
            getLogger().trace(".checkExecutionPrivilege(): [Checking if Participant is -Paused-] Not -Paused-");
        }
        getLogger().trace(".checkExecutionPrivilege(): [Checking if Participant is -Paused-] Finish");

        getLogger().trace(".checkExecutionPrivilege(): [Checking JobCard] Start");
        PetasosTaskJobCard jobCard = getJobCardCache().getJobCard(actionableTaskId);
        if(jobCard == null) {
            getLogger().trace(".checkExecutionPrivilege(): [Checking JobCard] Finish");
            getLogger().debug(".checkExecutionPrivilege(): Exit, no jobcard, returning -CANCEL-");
            return (TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
        }
        getLogger().trace(".checkExecutionPrivilege(): [Checking JobCard] Finish");
        getLogger().debug(".checkExecutionPrivilege(): Exit, value->{}", jobCard.getGrantedStatus());
        return(jobCard.getGrantedStatus());
    }

    public boolean isTaskPerformerIdle(TaskPerformerTypeType performerType) {
        getLogger().debug(".isTaskPerformerIdle(): Entry, performerType->{}", performerType);
        if (performerType == null) {
            getLogger().debug(".isTaskPerformerIdle(): Exit, performerType is null, returning false");
            return (false);
        }
        if (performerType.getKnownTaskPerformer() == null) {
            getLogger().debug(".isTaskPerformerIdle(): Exit, performerType.getKnownTaskPerformer() is null, returning false");
            return (false);
        }
        if (StringUtils.isEmpty(performerType.getKnownTaskPerformer().getName())) {
            getLogger().debug(".isTaskPerformerIdle(): Exit, performerType.getKnownTaskPerformer().getName() is empty, returning false");
            return (false);
        }
        boolean isPerformerIdle = isTaskPerformerIdle(performerType.getKnownTaskPerformer().getName());
        getLogger().debug(".isTaskPerformerIdle(): Exit, isPerformerIdle->{}", isPerformerIdle);
        return(isPerformerIdle);
    }

    public boolean isTaskPerformerIdle(String performerName){
        getLogger().debug(".isTaskPerformerIdle(): Entry, performerName->{}", performerName);
        switch(getParticipantManager().getParticipantStatus(performerName)) {
            case PARTICIPANT_IS_IDLE:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is IDLE, returning true");
                return(true);
            case PARTICIPANT_IS_ACTIVE:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is ACTIVE, returning false");
                return(false);
            case PARTICIPANT_IS_NOT_READY:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is NOT_READY, returning false");
                return(false);
            case PARTICIPANT_IS_STOPPING:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is STOPPING, returning false");
                return(false);
            case PARTICIPANT_HAS_FAILED:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is FAILED, returning false");
                return(false);
            default:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer has no status, returning false");
                return(false);
        }
    }

    public boolean isTaskPerformerEnabled(TaskPerformerTypeType performerType) {
        getLogger().debug(".isTaskPerformerEnabled(): Entry, performerType->{}", performerType);
        if (performerType == null) {
            getLogger().debug(".isTaskPerformerEnabled(): Exit, performerType is null, returning false");
            return (false);
        }
        if (performerType.getKnownTaskPerformer() == null) {
            getLogger().debug(".isTaskPerformerEnabled(): Exit, performerType.getKnownTaskPerformer() is null, returning false");
            return (false);
        }
        if (StringUtils.isEmpty(performerType.getKnownTaskPerformer().getName())) {
            getLogger().debug(".isTaskPerformerEnabled(): Exit, performerType.getKnownTaskPerformer().getName() is empty, returning false");
            return (false);
        }
        boolean performerEnabled = isTaskPerformerEnabled(performerType.getKnownTaskPerformer().getName());
        getLogger().debug(".isTaskPerformerEnabled(): Exit, performerEnabled->{}", performerEnabled);
        return(performerEnabled);
    }

    public boolean isTaskPerformerEnabled(String performerName){
        getLogger().debug(".isTaskPerformerEnabled(): Entry, performerName->{}", performerName);
        boolean isSuspended = getParticipantManager().isParticipantSuspended(performerName);
        getLogger().trace(".isTaskPerformerEnabled(): isSuspended->{}", isSuspended);
        boolean isDisabled = getParticipantManager().isParticipantDisabled(performerName);
        getLogger().trace(".isTaskPerformerEnabled(): isDisabled->{}", isDisabled);
        if(isDisabled || isSuspended){
            getLogger().debug(".isTaskPerformerEnabled(): Exit, is Disabled or Suspended, returning false");
            return(false);
        } else {
            getLogger().debug(".isTaskPerformerEnabled(): Exit, neither Disabled nor Suspended, returning true");
            return (true);
        }
    }

    //
    // Getters (and Setters)
    //

    public boolean isInitialised() {
        return initialised;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(this.processingPlant);
    }

    protected PetasosDefaultProperties getPetasosDefaultProperties(){
        return(this.petasosDefaultProperties);
    }

    protected LocalTaskJobCardCache getJobCardCache(){
        return(jobCardCache);
    }

    protected LocalParticipantCache getParticipantRegistrationCache(){
        return(participantRegistrationCache);
    }

    protected LocalParticipantManager getParticipantManager(){
        return(participantManager);
    }
}
