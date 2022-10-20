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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.queue;

import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalTaskActivityManager;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class LocalTaskQueueCache {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskQueueCache.class);

    private ConcurrentHashMap<String, ParticipantTaskQueue> participantQueueMap;
    private Object participantQueueMapLock;
    private boolean initialised;

    private static final int MAXIMUM_QUEUE_SIZE = 500;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private LocalParticipantManager participantIM;

    @Produce
    private ProducerTemplate camelRouteInjectorService;

    @Inject
    private LocalTaskActivityManager actionableTaskActivityController;

    //
    // Constructor(s)
    //

    public LocalTaskQueueCache(){
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

    protected LocalTaskActivityManager getActionableTaskActivityController(){
        return(actionableTaskActivityController);
    }

    protected ConcurrentHashMap<String, ParticipantTaskQueue> getParticipantQueueMap(){
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

    protected LocalParticipantManager getParticipantIM(){
        return(participantIM);
    }

    protected ProducerTemplate getCamelRouteInjectionService(){
        return(camelRouteInjectorService);
    }

    //
    // Participant Processing Tasks
    //

    public boolean isParticipantSuspended(String participantName){
        boolean participantSuspended = getParticipantIM().isParticipantSuspended(participantName);
        return(participantSuspended);
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
                ParticipantTaskQueue taskQueue = null;
                if(getParticipantQueueMap().containsKey(participantName)){
                    taskQueue = getParticipantQueueMap().get(participantName);
                } else {
                    taskQueue = new ParticipantTaskQueue();
                    getParticipantQueueMap().put(participantName, taskQueue);
                }
                if(actionableTask.hasTaskId()) {
                    ParticipantTaskQueueEntry taskQueueEntry = new ParticipantTaskQueueEntry();
                    taskQueueEntry.setTaskId(actionableTask.getTaskId());
                    taskQueueEntry.setSequenceNumber(actionableTask.getTaskId().getTaskSequenceNumber());
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

    public boolean isFull(String participantName){
        if(StringUtils.isEmpty(participantName)){
            return(false);
        }
        int size = getParticipantQueueSize(participantName);
        if(size > MAXIMUM_QUEUE_SIZE){
            return(true);
        } else {
            return (false);
        }
    }

    public int getParticipantQueueSize(String participantName){
        if(StringUtils.isEmpty(participantName)){
            return(0);
        }
        if(getParticipantQueueMap().containsKey(participantName)){
            int size = getParticipantQueueMap().get(participantName).getSize();
            return(size);
        }
        return(0);
    }

    public ParticipantTaskQueueEntry peekNextTask(String participantName){
        getLogger().debug(".peekNextTask(): Entry, participantName->{}", participantName);
        ParticipantTaskQueueEntry taskQueueEntry = null;
        if(StringUtils.isNotEmpty(participantName)){
            synchronized (getParticipantQueueMapLock()) {
                if (getParticipantQueueMap().containsKey(participantName)) {
                    if (getParticipantQueueMap().get(participantName).hasEntries()) {
                        taskQueueEntry = getParticipantQueueMap().get(participantName).peek();
                    }
                }
            }
        }
        getLogger().debug(".peekNextTask(): Exit, taskQueueEntry->{}", taskQueueEntry);
        return(taskQueueEntry);
    }

    public ParticipantTaskQueueEntry pollNextTask(String participantName){
        getLogger().debug(".pollNextTask(): Entry, participantName->{}", participantName);
        ParticipantTaskQueueEntry taskQueueEntry = null;
        if(StringUtils.isNotEmpty(participantName)){
            synchronized (getParticipantQueueMapLock()) {
                if (getParticipantQueueMap().containsKey(participantName)) {
                    if (getParticipantQueueMap().get(participantName).hasEntries()) {
                        taskQueueEntry = getParticipantQueueMap().get(participantName).poll();
                    }
                }
            }
        }
        getLogger().debug(".pollNextTask(): Exit, taskQueueEntry->{}", taskQueueEntry);
        return(taskQueueEntry);
    }

}
