/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.core.tasks.caches.processingplant;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.core.model.petasos.resilience.parcel.ResilienceParcelProcessingStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.datatypes.WUPIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class acts as the Data Manager for the PetasosFulfillmentTask set within the local
 * ProcessingPlant. That is, it is the single management point for the
 * task element set itself. It does not implement business logic associated
 * with the surrounding activity associated with each Parcel beyond provision
 * of helper methods associated with search-set and status-set collection
 * methods.
 *
 * @author Mark A. Hunter
 * @since 2020-06-01
 */
@ApplicationScoped
public class LocalPetasosFulfillmentTaskDM {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPetasosFulfillmentTaskDM.class);

    private ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask> fulfillmentTaskCache;

    public LocalPetasosFulfillmentTaskDM() {
        fulfillmentTaskCache = new ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask>();
    }

    public PetasosFulfillmentTask registerFulfillmentTask(PetasosFulfillmentTask task) {
        getLogger().debug(".registerFulfillmentTask(): Entry, task --> {}", task);
        if (task == null) {
            getLogger().debug(".registerFulfillmentTask(): Exit, task is null, returning null");
            return(null);
        }
        if (!task.hasTaskId()) {
            getLogger().debug(".registerFulfillmentTask(): Exit, task as no id, returning null");
            return(null);
        }
        TaskIdType taskId = task.getTaskId();
        if(fulfillmentTaskCache.containsKey(taskId)){
            fulfillmentTaskCache.remove(taskId);
        }
        fulfillmentTaskCache.put(taskId, task);
        synchronized (task.getTaskFulfillmentLock()) {
            task.getTaskFulfillment().setRegistrationInstant(Instant.now());
            task.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED);
        }
        synchronized (task.getTaskJobCardLock()){
            task.getTaskJobCard().setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED);
        }
        getLogger().debug(".registerFulfillmentTask(): Exit, task->{}", task);
        return(task);
    }

    public PetasosFulfillmentTask getFulfillmentTask(TaskIdType taskId) {
        getLogger().debug(".getFulfillmentTask(): Entry, taskId --> {}", taskId);
        PetasosFulfillmentTask task = null;
        if (fulfillmentTaskCache.containsKey(taskId)) {
            task = fulfillmentTaskCache.get(taskId);
        }
        getLogger().debug(".getFulfillmentTask(): Exit, task->{}", task);
        return (task);
    }

    public void removeFulfillmentTask(TaskIdType taskId) {
        getLogger().debug(".removeFulfillmentTask(): Entry, taskId->{}", taskId);
        if (taskId == null) {
            return;
        }
        if(this.getFulfillmentTaskCache().containsKey(taskId)){
            getLogger().trace(".removeFulfillmentTask(): Removing task from map/cache");
            this.getFulfillmentTaskCache().remove(taskId);
        } else {
            getLogger().trace(".removeFulfillmentTask(): taks is not in map/cache, cannot remove it!");
        }
        getLogger().debug(".removeFulfillmentTask(): Exit");
    }

    public void updateFulfillmentTask(PetasosFulfillmentTask fulfillmentTask) {
        getLogger().debug(".updateFulfillmentTask() Entry, task --> {}", fulfillmentTask);
        if (fulfillmentTask == null) {
            throw (new IllegalArgumentException("fulfillmentTask is null"));
        }
        if (fulfillmentTaskCache.containsKey(fulfillmentTask.getTaskId())) {
            fulfillmentTaskCache.remove(fulfillmentTask.getTaskId());
        }
        fulfillmentTaskCache.put(fulfillmentTask.getTaskId(), fulfillmentTask);
        getLogger().debug(".updateFulfillmentTask(): Exit");
    }


    public List<PetasosFulfillmentTask> getFulfillmentTaskList() {
        getLogger().debug(".getFulfillmentTaskList(): Entry");
        List<PetasosFulfillmentTask> parcelList = new LinkedList<PetasosFulfillmentTask>();
        fulfillmentTaskCache.entrySet().forEach(entry -> parcelList.add(entry.getValue()));
        getLogger().debug(".getFulfillmentTaskList(): Exit");
        return (parcelList);
    }

    public List<PetasosFulfillmentTask> getFulfillmentTaskByExecutionStatus(FulfillmentExecutionStatusEnum status) {
        getLogger().debug(".getFulfillmentTaskByStatus(): Entry, status->{}", status);
        List<PetasosFulfillmentTask> taskList = new LinkedList<>();
        Iterator<PetasosFulfillmentTask> parcelListIterator = getFulfillmentTaskList().iterator();
        while (parcelListIterator.hasNext()) {
            PetasosFulfillmentTask currentParcel = parcelListIterator.next();
            if (currentParcel.hasTaskFulfillment()) {
                if(currentParcel.getTaskFulfillment().hasStatus()){
                    if(currentParcel.getTaskFulfillment().getStatus().equals(status)){
                        taskList.add(currentParcel);
                    }
                }
            }
        }
        getLogger().debug(".getFulfillmentTaskByStatus(): Exit");
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getActiveFulfillmentTasks() {
        List<PetasosFulfillmentTask> taskList = getFulfillmentTaskByExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getFinishedFulfillmentTasks() {
        List<PetasosFulfillmentTask> taskList = getFulfillmentTaskByExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FINISHED);
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getFailedFulfillmentTasks() {
        List<PetasosFulfillmentTask> taskList = getFulfillmentTaskByExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getCancelledFulfillmentTasks() {
        List<PetasosFulfillmentTask> taskList = getFulfillmentTaskByExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
        return (taskList);
    }

    public List<PetasosFulfillmentTask> getLocalInProgressFulfillmentTasks() {
        getLogger().debug(".getInProgressParcelSet(): Entry");
        List<PetasosFulfillmentTask> parcelList = new LinkedList<PetasosFulfillmentTask>();
        parcelList.addAll(getFulfillmentTaskByExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE));
        parcelList.addAll(getFulfillmentTaskByExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_INITIATED));
        parcelList.addAll(getFulfillmentTaskByExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED));
        getLogger().debug(".getInProgressParcelSet(): Exit");
        return (parcelList);
    }

    public List<PetasosFulfillmentTask> getFulfillmentTaskByActionableTaskId(TaskIdType taskId) {
        getLogger().debug(".getFulfillmentTaskByActionableTaskId(): Entry, taskId --> {}" + taskId);
        List<PetasosFulfillmentTask> parcelList = new LinkedList<PetasosFulfillmentTask>();
        Iterator<PetasosFulfillmentTask> parcelListIterator = getFulfillmentTaskList().iterator();
        while (parcelListIterator.hasNext()) {
            PetasosFulfillmentTask currentParcel = parcelListIterator.next();
             if (currentParcel.hasActionableTaskId()) {
                if (currentParcel.getActionableTaskId().equals(taskId)) {
                    parcelList.add(currentParcel);
                }
            }
        }
        getLogger().debug(".getFulfillmentTaskByActionableTaskId(): Exit");
        return (parcelList);
    }

    public PetasosFulfillmentTask getCurrentFulfillmetTaskForWUP(ComponentIdType wupComponentId) {
        getLogger().debug(".getCurrentFulfillmetTaskForWUP(): Entry, wupComponentId->{}",wupComponentId);
        List<PetasosFulfillmentTask> taskList = new LinkedList<>();
        Iterator<PetasosFulfillmentTask> taskListIterator = getFulfillmentTaskList().iterator();
        while (taskListIterator.hasNext()) {
            PetasosFulfillmentTask currentParcel = taskListIterator.next();
            if (currentParcel.hasTaskFulfillment()) {
                if (currentParcel.getTaskFulfillment().hasFulfillerComponent()){
                    if(currentParcel.getTaskFulfillment().getFulfillerComponent().getComponentID().equals(wupComponentId)){
                        return (currentParcel);
                    }
                }
            }
        }
        getLogger().debug(".getCurrentFulfillmetTaskForWUP(): Exit");
        return (null);
    }
    
    //
    // Getters (and Setters)
    //
    
    protected Logger getLogger(){
        return(LOG);
    }

    protected ConcurrentHashMap<TaskIdType, PetasosFulfillmentTask> getFulfillmentTaskCache() {
        return fulfillmentTaskCache;
    }
}
