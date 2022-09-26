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
package net.fhirfactory.pegacorn.services.tasks.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.datagrid.valuesets.DatagridPersistenceResourceStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class PetasosActionableTaskRegistrationType implements Serializable {
    private TaskIdType actionableTaskId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant registrationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant checkInstant;
    private DatagridPersistenceResourceStatusEnum resourceStatus;
    private Set<ComponentIdType> fulfillerComponentIdSet;
    private Set<PetasosParticipantId> fulfillerParticipantIdSet;
    private Set<TaskPerformerTypeType> fulfillerCapabilities;

    //
    // Constructor(s)
    //

    public PetasosActionableTaskRegistrationType(){
        this.actionableTaskId = null;
        this.registrationInstant = null;
        this.checkInstant = null;
        this.fulfillerComponentIdSet = new HashSet<>();
        this.fulfillerCapabilities = new HashSet<>();
        this.fulfillerParticipantIdSet = new HashSet<>();
        this.resourceStatus = null;
    }

    //
    // Getters and Setters
    //


    public DatagridPersistenceResourceStatusEnum getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(DatagridPersistenceResourceStatusEnum resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    public TaskIdType getActionableTaskId() {
        return actionableTaskId;
    }

    public void setActionableTaskId(TaskIdType actionableTask) {
        this.actionableTaskId = actionableTask;
    }

    public Instant getRegistrationInstant() {
        return registrationInstant;
    }

    public void setRegistrationInstant(Instant registrationInstant) {
        this.registrationInstant = registrationInstant;
    }

    public Instant getCheckInstant() {
        return checkInstant;
    }

    public void setCheckInstant(Instant checkInstant) {
        this.checkInstant = checkInstant;
    }

    public Set<ComponentIdType> getFulfillerComponentIdSet() {
        return fulfillerComponentIdSet;
    }

    public void setFulfillerComponentIdSet(Set<ComponentIdType> fulfillerComponentIdSet) {
        this.fulfillerComponentIdSet.clear();
        this.fulfillerComponentIdSet.addAll(fulfillerComponentIdSet);
    }

    public Set<PetasosParticipantId> getFulfillerParticipantIdSet() {
        return fulfillerParticipantIdSet;
    }

    public void setFulfillerParticipantIdSet(Set<PetasosParticipantId> fulfillerParticipantIdSet) {
        this.fulfillerParticipantIdSet.clear();
        this.fulfillerParticipantIdSet.addAll(fulfillerParticipantIdSet);
    }

    public Set<TaskPerformerTypeType> getFulfillerCapabilities() {
        return fulfillerCapabilities;
    }

    public void setFulfillerCapabilities(Set<TaskPerformerTypeType> fulfillerCapabilities) {
        this.fulfillerCapabilities.clear();
        this.fulfillerCapabilities.addAll(fulfillerCapabilities);
    }

    //
    // Business Methods
    //

    @JsonIgnore
    public void addPerformerComponentId(ComponentIdType id){
        if(id == null){
            return;
        }
        if(!getFulfillerComponentIdSet().contains(id)){
            getFulfillerComponentIdSet().add(id);
        }
    }

    @JsonIgnore
    public void addPerformerType(TaskPerformerTypeType performerType){
        if(performerType == null){
            return;
        }
        if(!getFulfillerCapabilities().contains(performerType)){
            getFulfillerCapabilities().add(performerType);
        }
    }

    @JsonIgnore
    public void addPerformerTypes(Collection<TaskPerformerTypeType> taskPerformers){
        if(taskPerformers == null){
            return;
        }
        if(taskPerformers.isEmpty()){
            return;
        }
        for(TaskPerformerTypeType currentPerformerType: taskPerformers){
            addPerformerType(currentPerformerType);
        }
    }

    @JsonIgnore
    public void addPerformerParticipantId(PetasosParticipantId serviceName){
        if(serviceName == null){
            return;
        }
        if(!getFulfillerParticipantIdSet().contains(serviceName)){
            getFulfillerParticipantIdSet().add(serviceName);
        }
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosActionableTaskRegistrationType{" +
                "actionableTaskId=" + actionableTaskId +
                ", registrationInstant=" + registrationInstant +
                ", checkInstant=" + checkInstant +
                ", fulfillerComponents=" + fulfillerComponentIdSet +
                ", fulfillerParticipants=" + fulfillerParticipantIdSet +
                ", fulfillerCapabilities=" + fulfillerCapabilities +
                ", resourceStatus=" + resourceStatus +
                '}';
    }
}
