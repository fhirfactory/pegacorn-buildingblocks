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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class TaskTraceabilityType implements Serializable {
    private ConcurrentHashMap<Integer, TaskTraceabilityElementType> taskJourney;
    private TaskStorageType persistenceStatus;

    private boolean aRetry;
    private TaskIdType retryOrigin;

    //
    // Constructor(s)
    //

    public TaskTraceabilityType(){
        this.taskJourney = new ConcurrentHashMap<>();
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasPersistenceStatus(){
        boolean hasValue = this.persistenceStatus != null;
        return(hasValue);
    }

    public TaskStorageType getPersistenceStatus() {
        if(this.persistenceStatus == null){
            this.persistenceStatus = new TaskStorageType();
        }
        return persistenceStatus;
    }

    public boolean isaRetry() {
        return aRetry;
    }

    public void setaRetry(boolean aRetry) {
        this.aRetry = aRetry;
    }

    public TaskIdType getRetryOrigin() {
        return retryOrigin;
    }

    public void setRetryOrigin(TaskIdType retryOrigin) {
        this.retryOrigin = retryOrigin;
    }

    public void setPersistenceStatus(TaskStorageType persistenceStatus) {
        this.persistenceStatus = persistenceStatus;
    }

    public ConcurrentHashMap<Integer, TaskTraceabilityElementType> getTaskJourney() {
        return taskJourney;
    }

    public void setTaskJourney(ConcurrentHashMap<Integer, TaskTraceabilityElementType> taskJourney) {
        this.taskJourney = taskJourney;
    }

    //
    // Business / Helper Methods
    //

    public void addToTaskJourney(TaskTraceabilityElementType traceabilityElementType){
        if(traceabilityElementType == null){
            return;
        }
        int count = taskJourney.size();
        taskJourney.put(count, traceabilityElementType);
    }

    @JsonIgnore
    public TaskIdType getUpstreamActionableTaskId(){
        if(taskJourney.isEmpty()){
            return(null);
        }
        int count = taskJourney.size();
        TaskTraceabilityElementType mostRecentTraceabilityElement = taskJourney.get(count - 1);
        TaskIdType upstreamTaskId = mostRecentTraceabilityElement.getActionableTaskId();
        return(upstreamTaskId);
    }

    @JsonIgnore
    public TaskIdType getUpstreamFulfillmentTaskId(){
        if(taskJourney.isEmpty()){
            return(null);
        }
        int count = taskJourney.size();
        TaskTraceabilityElementType mostRecentTraceabilityElement = taskJourney.get(count - 1);
        TaskIdType upstreamTaskId = mostRecentTraceabilityElement.getFulfillerTaskId();
        return(upstreamTaskId);
    }

    @JsonIgnore
    public boolean hasUpstreamActionableTaskId(){
        if(taskJourney.isEmpty()){
            return(false);
        } else {
            return(true);
        }
    }

    //
    // To String
    //


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskTraceabilityType{");
        sb.append("taskJourney=").append(taskJourney);
        sb.append(", persistenceStatus=").append(persistenceStatus);
        sb.append(", upstreamActionableTaskId=").append(getUpstreamActionableTaskId());
        sb.append(", upstreamFulfillmentTaskId=").append(getUpstreamFulfillmentTaskId());
        sb.append(", aRetry=").append(isaRetry());
        sb.append(", retryOrigin=").append(getRetryOrigin());
        sb.append('}');
        return sb.toString();
    }
}
