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

import net.fhirfactory.pegacorn.core.interfaces.oam.metrics.PetasosTaskCacheStatusInterface;
import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskCacheServiceInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
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
public class ParticipantSharedActionableTaskCache implements PetasosTaskCacheServiceInterface, PetasosTaskCacheStatusInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipantSharedActionableTaskCache.class);

    // <Action Task Id, Actionable Task> This will be shared across infinispan
    private ConcurrentHashMap<String, PetasosActionableTask> taskCache;
    private ConcurrentHashMap<String, Object> taskSpecificLockMap;
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
            getTaskSpecificLockMap().put(actionableTask.getTaskId().getId(), new Object());
            getTaskCache().put(actionableTask.getTaskId().getId(), actionableTask);
        }
        PetasosActionableTask clonedTask = SerializationUtils.clone(actionableTask);
        getLogger().debug(".registerActionableTask(): Exit, actionableTask->{}", clonedTask);
        return(clonedTask);
    }

    @Override
    public PetasosActionableTask removeTask(PetasosTask actionableTask){
        getLogger().debug(".unregisterActionableTask(): Entry, actionableTask->{}", actionableTask);
        if(actionableTask == null){
            getLogger().debug(".unregisterActionableTask(): Exit, actionableTask is null, returning null");
            return(null);
        }
        PetasosActionableTask unregisteredActionableTask = removeTask(actionableTask.getTaskId().getId());
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
        PetasosActionableTask unregisteredActionableTask = removeTask(taskId.getId());
        getLogger().debug(".unregisterActionableTask(): Exit, unregisteredActionableTask->{}", unregisteredActionableTask);
        return(unregisteredActionableTask);
    }

    public PetasosActionableTask removeTask(String taskIdValue){
        getLogger().debug(".unregisterActionableTask(): Entry, taskIdValue->{}", taskIdValue);
        if(taskIdValue == null){
            getLogger().debug(".unregisterActionableTask(): Exit, taskIdValue is null, returning null");
            return(null);
        }
        PetasosActionableTask unregisteredActionableTask = null;
        synchronized(getTaskCacheLock()){
            if(getTaskCache().containsKey(taskIdValue)){
                getLogger().trace(".unregisterActionableTask(): actionableTask exists in cache, removing");
                unregisteredActionableTask = getTaskCache().get(taskIdValue);
                getTaskCache().remove(taskIdValue);
                getTaskSpecificLockMap().remove(taskIdValue);
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
        PetasosActionableTask task = getTask(taskId.getId());
        getLogger().debug(".getTask(): Exit, task->{}", task);
        return(task);
    }

    public PetasosActionableTask getTask(String taskIdValue){
        getLogger().debug(".getTask(): Entry, taskIdValue->{}", taskIdValue);
        if(taskCache.isEmpty()){
            getLogger().debug(".getTask(): Exit, taskIdValue is empty");
            return(null);
        }
        PetasosActionableTask task = taskCache.get(taskIdValue);
        getLogger().debug(".getTask(): Exit, task->{}", task);
        return(task);
    }


    @Override
    public Object getTaskLock(TaskIdType taskId) {
        getLogger().debug(".getTaskLock(): Entry, taskId->{}", taskId);
        if (taskId == null) {
            getLogger().debug(".getTaskLock(): Exit, taskId is null, returning -null-");
            return (null);
        }
        Object taskLock = getTaskLock(taskId.getId());
        return(taskLock);
    }
    @Override
    public Object getTaskLock(String TaskIdValue){
        getLogger().debug(".getTaskLock(): Entry, TaskIdValue->{}", TaskIdValue);
        if(TaskIdValue == null){
            getLogger().debug(".getTaskLock(): Exit, TaskIdValue is null, returning -null-");
            return(null);
        }
        Object lockObject = getTaskSpecificLockMap().get(TaskIdValue);
        getLogger().debug(".getTaskLock(): Exit, lockObject->{}", lockObject);
        return(lockObject);
    }

    @Override
    public void addTaskLock(TaskIdType taskId){
        if(taskId != null){
            getTaskSpecificLockMap().put(taskId.getId(), new Object());
        }
    }

    public Set<String> getAllTaskIds(){
        getLogger().debug(".getAllTaskIds(): Entry");
        HashSet<String> idSet = new HashSet<>();
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
        synchronized (getTaskCacheLock()) {
            getTaskCache().put(actionableTask.getTaskId().getId(), actionableTask);
            getTaskSpecificLockMap().putIfAbsent(actionableTask.getTaskId().getId(), new Object());
        }
        PetasosActionableTask clonedTask = SerializationUtils.clone(actionableTask);
        return(clonedTask);
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
        if(!getTaskCache().containsKey(actionableTask.getTaskId().getId())){
            synchronized (getTaskCacheLock()) {
                getTaskCache().put(actionableTask.getTaskId().getId(), actionableTask);
                getTaskSpecificLockMap().putIfAbsent(actionableTask.getTaskId().getId(), new Object());
                clonedTask = SerializationUtils.clone(actionableTask);
            }
        } else {
            TaskIdType taskId = actionableTask.getTaskId();
            getTaskSpecificLockMap().putIfAbsent(actionableTask.getTaskId().getId(), new Object());
            synchronized (getTaskSpecificLockMap().get(taskId.getId())) {
                PetasosActionableTask cacheTaskInstance = getTaskCache().get(taskId.getId());
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

    protected Map<String, PetasosActionableTask> getTaskCache(){
        return(this.taskCache);
    }

    protected Map<String, Object> getTaskSpecificLockMap(){
        return(this.taskSpecificLockMap);
    }

    protected Object getTaskCacheLock(){
        return(this.taskCacheLock);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    public Integer getActionableTaskCacheSize() {
        return (getTaskCache().size());
    }
}
