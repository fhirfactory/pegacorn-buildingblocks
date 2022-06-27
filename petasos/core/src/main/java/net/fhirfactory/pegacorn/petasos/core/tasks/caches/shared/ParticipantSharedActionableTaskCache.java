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

package net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskCacheServiceInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

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
public class ParticipantSharedActionableTaskCache implements PetasosTaskCacheServiceInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipantSharedActionableTaskCache.class);

    // <Action Task Id, Actionable Task> This will be shared across infinispan
    private ConcurrentHashMap<TaskIdType, PetasosActionableTask> taskCache;
    private ConcurrentHashMap<TaskIdType, Object> taskSpecificLockMap;
    private Object taskCacheLock;

    //
    // Constructor(s)
    //
    public ParticipantSharedActionableTaskCache() {
        taskCache = new ConcurrentHashMap<>();
        taskSpecificLockMap = new ConcurrentHashMap<>();
        taskCacheLock = new Object();
    }

    //
    // Post Construct
    //


    //
    // Business Methods
    //

    public int getCacheSize(){
        int size = taskCache.size();
        return(size);
    }

    @Override
    public PetasosTask registerTask(PetasosTask task){
        getLogger().debug(".registerActionableTask(): Entry, task->{}", task);
        if(task == null){
            getLogger().debug(".registerActionableTask(): Exit, actionableTask is null, returning null");
            return(null);
        }
        PetasosActionableTask actionableTask = (PetasosActionableTask) task;
        synchronized (getTaskCacheLock()){
            if(getTaskCache().containsKey(actionableTask.getTaskId())){
                getTaskCache().remove(actionableTask.getTaskId());
                getTaskSpecificLockMap().remove(actionableTask.getTaskId());
            }
            getTaskSpecificLockMap().put(actionableTask.getTaskId(), new Object());
            getTaskCache().put(actionableTask.getTaskId(), actionableTask);
        }
        getLogger().debug(".registerActionableTask(): Exit, actionableTask->{}", actionableTask);
        return(actionableTask);
    }

    @Override
    public PetasosActionableTask removeTask(PetasosTask actionableTask){
        getLogger().debug(".unregisterActionableTask(): Entry, actionableTask->{}", actionableTask);
        if(actionableTask == null){
            getLogger().debug(".unregisterActionableTask(): Exit, actionableTask is null, returning null");
            return(null);
        }
        PetasosActionableTask unregisteredActionableTask = removeTask(actionableTask.getTaskId());
        getLogger().debug(".unregisterActionableTask(): Exit, unregisteredActionableTask->{}", unregisteredActionableTask);
        return(unregisteredActionableTask);
    }

    @Override
    public PetasosActionableTask removeTask(TaskIdType taskId){
        getLogger().debug(".unregisterActionableTask(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".unregisterActionableTask(): Exit, taskId is null, returning null");
            return(null);
        }
        PetasosActionableTask unregisteredActionableTask = null;
        synchronized(getTaskCacheLock()){
            if(getTaskCache().containsKey(taskId)){
                getLogger().trace(".unregisterActionableTask(): actionableTask exists in cache, removing");
                unregisteredActionableTask = getTaskCache().get(taskId);
                getTaskCache().remove(taskId);
                getTaskSpecificLockMap().remove(taskId);
            }
        }
        getLogger().debug(".unregisterActionableTask(): Exit, unregisteredActionableTask->{}", unregisteredActionableTask);
        return(unregisteredActionableTask);
    }

    @Override
    public PetasosActionableTask getTask(TaskIdType taskId){
        getLogger().debug(".getTask(): Entry, taskId->{}", taskId);
        if(taskCache.isEmpty()){
            getLogger().debug(".getTask(): Exit, taskId is empty");
            return(null);
        }
        if(taskCache.containsKey(taskId)){
            PetasosActionableTask task = taskCache.get(taskId);
            getLogger().debug(".getTask(): Exit, task->{}", task);
            return(task);
        }
        getLogger().debug(".getTask(): Exit, Task with TaskId({}) is not within the cahce", taskId);
        return(null);
    }

    @Override
    public Object getTaskLock(TaskIdType taskId){
        getLogger().debug(".getTaskLock(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".getTaskLock(): Exit, taskId is null, returning -null-");
            return(null);
        }
        if(getTaskSpecificLockMap().containsKey(taskId)){
            Object lockObject = getTaskSpecificLockMap().get(taskId);
            getLogger().debug(".getTaskLock(): Exit, lockObject->{}", lockObject);
            return(lockObject);
        } else {
            getLogger().debug(".getTaskLock(): Exit, no lock object for taskId, returning -null-");
            return(null);
        }
    }

    @Override
    public void addTaskLock(TaskIdType taskId){
        if(taskId != null){
            Object lockObject = getTaskSpecificLockMap().get(taskId);
            if(lockObject == null){
                getTaskSpecificLockMap().put(taskId, new Object());
            }
        }
    }

    public Set<TaskIdType> getAllTaskIds(){
        getLogger().debug(".getAllTaskIds(): Entry");
        HashSet<TaskIdType> idSet = new HashSet<>();
        if(getTaskCache().isEmpty()){
            getLogger().debug(".getAllTaskIds(): Exit, cache is empty, returning empty set");
            return(idSet);
        }
        synchronized (getTaskCacheLock()){
            idSet.addAll(getTaskCache().keySet());
        }
        getLogger().debug(".getAllTaskIds(): Exit");
        return(idSet);
    }

    /**
     * A destructive READ operation (it returns a version of the passed in object that has been updated with the
     * contents of the instance in the cache).
     *
     * @param task
     * @return
     */
    @Override
    public PetasosTask refreshTask(PetasosTask task) {
        PetasosActionableTask actionableTask = (PetasosActionableTask) task;
        if(!getTaskCache().containsKey(actionableTask.getTaskId())){
            synchronized (getTaskCacheLock()) {
                getTaskCache().put(actionableTask.getTaskId(), actionableTask);
                getTaskSpecificLockMap().put(actionableTask.getTaskId(), new Object());
            }
            PetasosActionableTask clonedTask = SerializationUtils.clone(actionableTask);
            return(clonedTask);
        } else {
            PetasosActionableTask cacheTask = SerializationUtils.clone(getTask(task.getTaskId()));
            return (cacheTask);
        }
    }

    /**
     * A complex "PUT" into the cache, using business logic to ascertain what values should be used to update
     * the cache object.
     *
     * @param task
     * @return
     */
    @Override
    public PetasosTask synchroniseTask(PetasosTask task) {
        getLogger().debug(".synchroniseTask(): Entry, task->{}", task);
        PetasosActionableTask actionableTask = (PetasosActionableTask) task;
        PetasosActionableTask clonedTask = null;
        if(!getTaskCache().containsKey(actionableTask.getTaskId())){
            synchronized (getTaskCacheLock()) {
                getTaskCache().put(actionableTask.getTaskId(), actionableTask);
                getTaskSpecificLockMap().put(actionableTask.getTaskId(), new Object());
                clonedTask = SerializationUtils.clone(actionableTask);
            }
        } else {
            TaskIdType taskId = actionableTask.getTaskId();
            synchronized (getTaskSpecificLockMap().get(taskId)) {
                PetasosActionableTask cacheTaskInstance = getTaskCache().get(taskId);
                PetasosActionableTask updatedTask = (PetasosActionableTask) cacheTaskInstance.update(actionableTask);
                clonedTask = SerializationUtils.clone( updatedTask);
            }
        }
        getLogger().debug(".synchroniseTask(): Exit, clonedTask->{}", clonedTask);
        return(clonedTask);
    }

    //
    // Getters (and Setters)
    //

    protected Map<TaskIdType, PetasosActionableTask> getTaskCache(){
        return(this.taskCache);
    }

    protected Map<TaskIdType, Object> getTaskSpecificLockMap(){
        return(this.taskSpecificLockMap);
    }

    protected Object getTaskCacheLock(){
        return(this.taskCacheLock);
    }

    protected Logger getLogger(){
        return(LOG);
    }
}
