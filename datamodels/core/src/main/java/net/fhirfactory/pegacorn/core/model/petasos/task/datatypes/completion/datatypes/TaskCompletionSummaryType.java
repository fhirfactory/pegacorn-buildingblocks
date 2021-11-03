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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.TaskFinalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskCompletionSummaryType implements Serializable {
    private Map<TaskIdType, DownstreamTaskStatusType> downstreamTaskMap;
    private boolean end;
    private boolean finalised;

    //
    // Constructor(s)
    //

    public TaskCompletionSummaryType(){
        this.downstreamTaskMap = new ConcurrentHashMap<TaskIdType, DownstreamTaskStatusType>();
        this.end = false;
        this.finalised = false;
    }

    //
    // Helper Methods
    //

    public void addDownstreamTask(TaskIdType downstreamActionableTaskId){
        if(downstreamActionableTaskId == null){
            return;
        }
        if(getDownstreamTaskMap().containsKey(downstreamActionableTaskId)){
            return;
        }
        DownstreamTaskStatusType finalisationStatus = new DownstreamTaskStatusType();
        finalisationStatus.setDownstreamActionableTaskId(downstreamActionableTaskId);
        finalisationStatus.setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_NOT_BEING_FULFILLED);
        finalisationStatus.setRegistrationInstant(Instant.now());
        getDownstreamTaskMap().put(downstreamActionableTaskId, finalisationStatus);
    }

    public void notifyDownstreamTaskBeingFulfilled(TaskIdType downstreamActionableTaskId){
        if(downstreamActionableTaskId == null){
            return;
        }
        if(!getDownstreamTaskMap().containsKey(downstreamActionableTaskId)){
            addDownstreamTask(downstreamActionableTaskId);
        }
        getDownstreamTaskMap().get(downstreamActionableTaskId).setFinalisationStatus(TaskFinalisationStatusEnum.DOWNSTREAM_TASK_BEING_FULFILLED);
    }

    //
    // Getters and Setters
    //


    public boolean isFinalised() {
        return finalised;
    }

    public void setFinalised(boolean finalised) {
        this.finalised = finalised;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    @JsonIgnore
    public boolean hasFinalisationMap(){
        boolean hasValue = this.downstreamTaskMap != null;
        return(hasValue);
    }

    public Map<TaskIdType, DownstreamTaskStatusType> getDownstreamTaskMap() {
        return downstreamTaskMap;
    }

    public void setDownstreamTaskMap(Map<TaskIdType, DownstreamTaskStatusType> downstreamTaskMap) {
        this.downstreamTaskMap = downstreamTaskMap;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskFinalisationStatusType{" +
                "finalisationMap=" + downstreamTaskMap +
                ", hasFinalisationMap=" + hasFinalisationMap() +
                '}';
    }
}
