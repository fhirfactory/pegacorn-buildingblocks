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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.queue;

import io.reactivex.rxjava3.internal.operators.single.SingleDoAfterTerminate;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosTaskExecutionStatusEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalPetasosParticipantCacheIM;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosActionableTaskSharedInstance;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosActionableTaskSharedInstanceAccessorFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosActionableTaskActivityController;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class LocalTaskQueueManager {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskQueueManager.class);

    private ConcurrentHashMap<String, LocalParticipantTaskQueue> participantQueueMap;
    private Object participantQueueMapLock;
    private boolean initialised;

    private static final Long TASK_QUEUE_MANAGER_STARTUP_DELAY = 30000L;
    private static final Long TASK_QUEUE_MANAGER_PERIOD = 15000L;
    private static final Long DEFAULT_TASK_GAPPING_PERIOD = 10L;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private LocalPetasosParticipantCacheIM participantIM;

    @Produce
    private ProducerTemplate camelRouteInjectorService;

    @Inject
    private LocalPetasosActionableTaskActivityController actionableTaskActivityController;

    @Inject
    private PetasosActionableTaskSharedInstanceAccessorFactory actionableTaskSharedInstanceFactory;

    //
    // Constructor(s)
    //

    public LocalTaskQueueManager(){
        participantQueueMap = new ConcurrentHashMap<>();
        participantQueueMapLock = new Object();
        initialised = false;
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(initialised){
            getLogger().debug(".initialise(): Exit, already initialised");
            return;
        }
        getLogger().info(".initialise(): Initialising...");


        setInitialised(true);
        getLogger().info(".initialise(): Initialisation complete...");
        getLogger().debug("initialise(): Exit");
    }

    //
    // Getters (and Setters)
    //

    protected PetasosActionableTaskSharedInstanceAccessorFactory getActionableTaskSharedInstanceFactory(){
        return(actionableTaskSharedInstanceFactory);
    }

    protected LocalPetasosActionableTaskActivityController getActionableTaskActivityController(){
        return(actionableTaskActivityController);
    }

    protected ConcurrentHashMap<String, LocalParticipantTaskQueue> getParticipantQueueMap(){
        return(participantQueueMap);
    }

    protected Object getParticipantQueueMapLock(){
        return(participantQueueMapLock);
    }

    protected boolean isInitialised(){
        return(initialised);
    }

    protected void setInitialised(boolean initialised){
        this.initialised = initialised;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    protected LocalPetasosParticipantCacheIM getParticipantIM(){
        return(participantIM);
    }

    protected ProducerTemplate getCamelRouteInjectionService(){
        return(camelRouteInjectorService);
    }

    //
    // Daemons
    //

    public void scheduleTaskQueueManagerDaemon(){
        getLogger().debug(".scheduleTaskQueueManagerDaemon(): Entry");

        getLogger().debug(".scheduleTaskQueueManagerDaemon(): Exit");
    }

    public void taskQueueManagerDaemon(){
        getLogger().debug(".taskQueueManagerDaemon(): Entry");

        getLogger().debug(".taskQueueManagerDaemon(): Exit");
    }

    //
    // Participant Processing Tasks
    //

    public boolean isParticipantSuspended(String participantName){
        boolean participantSuspended = getParticipantIM().isParticipantSuspended(participantName);
        return(participantSuspended);
    }

    public void suspendParticipant(String participantName){
        getParticipantIM().suspendParticipant(participantName);
    }

    //
    // Participant Task Queueing
    //

    public void queueTask(PetasosActionableTask actionableTask){
        getLogger().debug(".queueTask(): Entry, actionableTask->{}", actionableTask);
        if(actionableTask != null){
            getLogger().debug(".queueTask(): Exit, actionableTask is null");
            return;
        }
        if(!actionableTask.hasTaskPerformerTypes()){
            getLogger().debug(".queueTask(): Exit, actionableTask has no performers!");
            return;
        }
        if(actionableTask.getTaskPerformerTypes().isEmpty()){
            getLogger().debug(".queueTask(): Exit, actionableTask has no performers!");
            return;
        }
        for(TaskPerformerTypeType performerType: actionableTask.getTaskPerformerTypes()){
            if(performerType.isCapabilityBased()){
                // do nothing
            } else {
                if(performerType.getKnownTaskPerformer() != null){
                    if(StringUtils.isNotEmpty(performerType.getKnownTaskPerformer().getName())){
                        queueTask(performerType.getKnownTaskPerformer().getName(), actionableTask);
                    }
                }
            }
        }
        getLogger().debug(".queueTask(): Exit");
    }

    public void queueTask(String participantName, PetasosActionableTask actionableTask){
        getLogger().debug(".queueTask(): Entry, participantName->{}, actionableTask->{}", participantName, actionableTask);
        boolean inserted = false;
        String errorMessage = null;
        if(StringUtils.isNotEmpty(participantName)){
            synchronized (getParticipantQueueMapLock()){
                LocalParticipantTaskQueue taskQueue = null;
                if(getParticipantQueueMap().containsKey(participantName)){
                    taskQueue = getParticipantQueueMap().get(participantName);
                } else {
                    taskQueue = new LocalParticipantTaskQueue();
                    getParticipantQueueMap().put(participantName, taskQueue);
                }
                if(actionableTask.hasTaskId() && actionableTask.hasSequenceNumber()) {
                    ParticipantTaskQueueEntry taskQueueEntry = new ParticipantTaskQueueEntry();
                    taskQueueEntry.setTaskId(actionableTask.getTaskId());
                    taskQueueEntry.setSequenceNumber(actionableTask.getSequenceNumber());
                    taskQueue.insertEntry(taskQueueEntry);
                    inserted = true;
                } else {
                    inserted = false;
                    errorMessage = "actionableTask does not have taskId or sequenceNumber";
                }
            }
        } else {
            inserted = false;
            errorMessage = "participantName is emtpy";
        }
        if(inserted){
            getLogger().debug(".queueTask(): Exit, task inserted!");
        } else {
            getLogger().debug(".queueTask(): Exit, task not inserted, reason = {}", errorMessage);
        }
    }

    public void registerLocallyCreatedTask(PetasosActionableTask actionableTask){
        if(actionableTask != null) {
            PetasosActionableTaskSharedInstance petasosActionableTaskSharedInstance = getActionableTaskSharedInstanceFactory().newActionableTaskSharedInstance(actionableTask);
            PetasosTaskExecutionStatusEnum petasosTaskExecutionStatusEnum = getActionableTaskActivityController().notifyTaskWaiting(petasosActionableTaskSharedInstance.getTaskId());
        }
    }

    public void registerPonosOriginatedTask(PetasosActionableTask actionableTask){
        if(actionableTask != null) {
            PetasosActionableTaskSharedInstance petasosActionableTaskSharedInstance = getActionableTaskSharedInstanceFactory().newActionableTaskSharedInstance(actionableTask);
        }
    }

    public void routeTask(String participantName, PetasosActionableTask actionableTask){


    }

}
