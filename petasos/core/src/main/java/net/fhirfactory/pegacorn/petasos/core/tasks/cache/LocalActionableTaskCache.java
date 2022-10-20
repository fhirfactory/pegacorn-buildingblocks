/*
 * Copyright (c) 2020 Mark A. Hunter
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

package net.fhirfactory.pegacorn.petasos.core.tasks.cache;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskStorageType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.valuesets.TaskStorageStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is the Cache Data Manager (CacheDM) for the ServiceModule WorkUnitActivity Episode ID
 * finalisation map. This map essentially allows for registration of WUPs that have registered interest
 * in the output UoW from a particular Episode. It then tracks when those "downstream" WUPs register a
 * new Episode ID for the processing out the output UoW from this "upstream" WorkUnitAcitivity Episode.
 * <p>
 * It uses a ConcurrentHasMap to store a full list of all downstream WUP Registered instances:
 * ConcurrentHashMap<FDNToken, WUAEpisodeFinalisationRegistrationStatus> downstreamRegistrationStatusSet
 where the FDNToken is the WUPInstanceID and the WUAEpisodeFinalisationRegistrationStatus is their
 registration status.
 <p>
 * It also uses a ConcurrentHashMap to store a list of WUPs that have registered to consume the specific
 * UoW of the current WUAEpisodeID
 * ConcurrentHashMap<FDNToken, FDNTokenSet> downstreamWUPRegistrationMap
 where the FDNToken is the EpisodeID and the FDNTokenSet is the list of WUPInstanceIDs for the downstream WUPS.
 *
 * @author Mark A. Hunter
 * @since 2020.07.01
 */

@ApplicationScoped
public class LocalActionableTaskCache {
    private static final Logger LOG = LoggerFactory.getLogger(LocalActionableTaskCache.class);

    private ConcurrentHashMap<TaskIdType, PetasosActionableTask> taskDirectory;
    private Object taskDirectoryLock;

    @Inject
    private ProcessingPlantInterface processingPlant;

    //
    // Constructor(s)
    //
    public LocalActionableTaskCache() {
        taskDirectory = new ConcurrentHashMap<>();
        taskDirectoryLock = new Object();
    }

    //
    // Post Construct
    //


    //
    // Business Methods
    //

    public int getCacheSize(){
        int size = taskDirectory.size();
        return(size);
    }

    public boolean addToCache(PetasosActionableTask actionableTask){
        getLogger().debug(".addToDirectory(): Entry, actionableTask->{}", actionableTask);
        if(actionableTask == null){
            getLogger().debug(".addToDirectory(): Exit, actionableTask is null, returning null");
            return(false);
        }
        synchronized (getTaskDirectoryLock()){
            if(getTaskDirectory().containsKey(actionableTask.getTaskId())){
                getTaskDirectory().remove(actionableTask.getTaskId());
            }
            getTaskDirectory().put(actionableTask.getTaskId(), actionableTask);
            if(!actionableTask.hasTaskTraceability()){
                actionableTask.setTaskTraceability(new TaskTraceabilityType());
            }
            if(!actionableTask.getTaskTraceability().hasPersistenceStatus()){
                actionableTask.getTaskTraceability().setPersistenceStatus(new TaskStorageType());
            }
            actionableTask.getTaskTraceability().getPersistenceStatus().setLocalStorageLocation(getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName());
            actionableTask.getTaskTraceability().getPersistenceStatus().setLocalStorageStatus(TaskStorageStatusEnum.TASK_SAVED);
            actionableTask.getTaskTraceability().getPersistenceStatus().setLocalStorageInstant(Instant.now());
        }
        getLogger().debug(".addToDirectory(): Exit, actionableTask->{}", actionableTask);
        return(true);
    }

    public PetasosActionableTask removeTaskFromDirectory(PetasosActionableTask actionableTask){
        getLogger().debug(".removeTaskFromDirectory(): Entry, actionableTask->{}", actionableTask);
        if(actionableTask == null){
            getLogger().debug(".removeTaskFromDirectory(): Exit, actionableTask is null, returning null");
            return(null);
        }
        PetasosActionableTask unregisteredActionableTask = removeTaskFromDirectory(actionableTask.getTaskId());
        getLogger().debug(".removeTaskFromDirectory(): Exit, unregisteredActionableTask->{}", unregisteredActionableTask);
        return(unregisteredActionableTask);
    }

    public PetasosActionableTask removeTaskFromDirectory(TaskIdType taskId){
        getLogger().debug(".removeTaskFromDirectory(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".removeTaskFromDirectory(): Exit, taskId is null, returning null");
            return(null);
        }
        PetasosActionableTask unregisteredActionableTask = null;
        synchronized(getTaskDirectoryLock()){
            if(getTaskDirectory().containsKey(taskId)){
                getLogger().trace(".removeTaskFromDirectory(): actionableTask exists in cache, removing");
                unregisteredActionableTask = getTaskDirectory().get(taskId);
                getTaskDirectory().remove(taskId);
            }
        }
        getLogger().debug(".removeTaskFromDirectory(): Exit, unregisteredActionableTask->{}", unregisteredActionableTask);
        return(unregisteredActionableTask);
    }

    public PetasosActionableTask getTask(TaskIdType taskId){
        getLogger().debug(".getTask(): Entry, taskId->{}", taskId);
        if(taskDirectory.isEmpty()){
            getLogger().debug(".getTask(): Exit, taskId is empty");
            return(null);
        }
        if(taskDirectory.containsKey(taskId)){
            PetasosActionableTask task = taskDirectory.get(taskId);
            getLogger().debug(".getTask(): Exit, task->{}", task);
            return(task);
        }
        getLogger().debug(".getTask(): Exit, Task with TaskId({}) is not within the cache", taskId);
        return(null);
    }

    public Set<TaskIdType> getAllTaskIds(){
        getLogger().debug(".getAllTaskIds(): Entry");
        HashSet<TaskIdType> idSet = new HashSet<>();
        if(getTaskDirectory().isEmpty()){
            getLogger().debug(".getAllTaskIds(): Exit, cache is empty, returning empty set");
            return(idSet);
        }
        synchronized (getTaskDirectoryLock()){
            idSet.addAll(getTaskDirectory().keySet());
        }
        getLogger().debug(".getAllTaskIds(): Exit");
        return(idSet);
    }

    //
    // Getters (and Setters)
    //

    protected Map<TaskIdType, PetasosActionableTask> getTaskDirectory(){
        return(this.taskDirectory);
    }

    protected Object getTaskDirectoryLock(){
        return(this.taskDirectoryLock);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }
}
