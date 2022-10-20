/*
 * Copyright (c) 2021 Mark A. Hunter
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

import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class LocalTaskJobCardCache {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskJobCardCache.class);

    // ActionableTaskId, FulfillmentTask JobCard Set (will be across ALL processing plants) Map
    private ConcurrentHashMap<TaskIdType, PetasosTaskJobCard> actionableTaskJobCardMap;
    //
    // Whole cache lock
    private Object cacheLock;

    //
    // Constructor(s)
    //

    public LocalTaskJobCardCache(){
        this.actionableTaskJobCardMap = new ConcurrentHashMap<>();
        this.cacheLock = new Object();
    }

    //
    // Business methods
    //

    public void addJobCard(PetasosTaskJobCard jobCard){
        getLogger().debug(".addJobCard(): Entry, jobCard->{}", jobCard);

        //
        // Defensive Programming
        if(jobCard == null){
            getLogger().debug(".addJobCard(): Exit, jobCard is null, exiting");
        }
        if(!jobCard.hasTaskId()){
            getLogger().debug(".addJobCard(): Exit, jobCard has no Actionable Task Id, exiting");
        }

        //
        // Grab the main mapping values
        TaskIdType actionableTaskId = jobCard.getTaskId();

        //
        // If it is already there - we are going to clobber is... use this method with discretion
        synchronized (getCacheLock()) {
            if (getJobCardCache().containsKey(actionableTaskId)) {
                getJobCardCache().remove(actionableTaskId);
            }
            jobCard.setUpdateInstant(Instant.now());
            getJobCardCache().put(jobCard.getTaskId(), jobCard);
        }
        getLogger().debug(".addJobCard(): Exit");
    }

    public void removeJobCard(PetasosTaskJobCard jobCard){
        getLogger().debug(".removeJobCard(): Entry, jobCard->{}", jobCard);

        //
        // Defensive Programming
        if(jobCard == null){
            getLogger().debug(".removeJobCard(): Exit, jobCard is null, exiting");
        }
        if(!jobCard.hasTaskId()){
            getLogger().debug(".removeJobCard(): Exit, jobCard has no Actionable Task Id, exiting");
        }

        //
        // Delete the values
        synchronized (getCacheLock()) {
            if (getJobCardCache().containsKey(jobCard.getTaskId())) {
                getJobCardCache().remove(jobCard.getTaskId());
            }
        }

        getLogger().debug(".removeJobCard(): Exit");
    }

    public PetasosTaskJobCard getJobCard(TaskIdType taskId){
        getLogger().debug(".getJobCard(): Entry, taskId->{}", taskId);

        //
        // Defensive Programming
        if(taskId == null){
            getLogger().debug(".getJobCard(): Exit, taskId is null, returning null");
            return(null);
        }

        //
        // Try and retrieve the Job Card
        PetasosTaskJobCard petasosTaskJobCard = getJobCardCache().get(taskId);

        getLogger().debug(".getJobCard(): Exit, retrieved JobCard ->{}", petasosTaskJobCard);
        return(petasosTaskJobCard);
    }

    public PetasosTaskJobCard getJobCard(String taskIdValue){
        getLogger().debug(".getJobCard(): Entry, taskIdValue->{}", taskIdValue);

        //
        // Defensive Programming
        if(StringUtils.isNotEmpty(taskIdValue)){
            getLogger().debug(".getJobCard(): Exit, taskId is null, returning null");
            return(null);
        }

        //
        // Try and retrieve the Job Card
        PetasosTaskJobCard petasosTaskJobCard = null;
        synchronized (getCacheLock()) {
            TaskIdType taskId = null;
            boolean found = false;
            Enumeration<TaskIdType> taskIdEnumeration = getJobCardCache().keys();
            while(taskIdEnumeration.hasMoreElements()){
                taskId = taskIdEnumeration.nextElement();
                if(taskId.getId().contentEquals(taskIdValue)){
                    found = true;
                    break;
                }
            }
            if(found) {
                petasosTaskJobCard = getJobCardCache().get(taskId);
            }
        }

        getLogger().debug(".getJobCard(): Exit, retrieved JobCard ->{}", petasosTaskJobCard);
        return(petasosTaskJobCard);
    }

    public List<TaskIdType> getJobCardTaskIdList(){
        getLogger().debug(".getJobCardTaskIdList(): Entry");
        List<TaskIdType> taskIdList = new ArrayList<>();
        if(getJobCardCache().isEmpty()){
            getLogger().debug(".getJobCardTaskIdList(): Exit, empty list... ");
            return(taskIdList);
        }
        synchronized (getCacheLock()){
            taskIdList.addAll(getJobCardCache().keySet());
        }
        getLogger().debug(".getJobCardTaskIdList(): Exit");
        return(taskIdList);
    }

    //
    // Getters (and Setters)
    //

    protected ConcurrentHashMap<TaskIdType, PetasosTaskJobCard> getJobCardCache(){
        return(actionableTaskJobCardMap);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    public Object getCacheLock(){
        return(this.cacheLock);
    }
}
