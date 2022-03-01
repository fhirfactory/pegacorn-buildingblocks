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
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.datagrid.valuesets.DatagridPersistenceResourceStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class PetasosActionableTaskRegistrationType implements Serializable {
    private TaskIdType actionableTaskId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant registrationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant persistenceInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant checkInstant;
    private DatagridPersistenceResourceStatusEnum resourceStatus;
    private Set<ComponentIdType> fulfillmentProcessingPlants;
    private Set<TaskPerformerTypeType> performerType;

    //
    // Constructor(s)
    //

    public PetasosActionableTaskRegistrationType(){
        this.actionableTaskId = null;
        this.registrationInstant = null;
        this.checkInstant = null;
        this.fulfillmentProcessingPlants = new HashSet<>();
        this.performerType = new HashSet<>();
        this.resourceStatus = null;
        this.persistenceInstant = null;
    }

    //
    // Getters and Setters
    //


    public Instant getPersistenceInstant() {
        return persistenceInstant;
    }

    public void setPersistenceInstant(Instant persistenceInstant) {
        this.persistenceInstant = persistenceInstant;
    }

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

    public Set<ComponentIdType> getFulfillmentProcessingPlants() {
        return fulfillmentProcessingPlants;
    }

    public void setFulfillmentProcessingPlants(Set<ComponentIdType> fulfillmentProcessingPlants) {
        this.fulfillmentProcessingPlants.clear();
        this.fulfillmentProcessingPlants.addAll(fulfillmentProcessingPlants);
    }

    public Set<TaskPerformerTypeType> getPerformerType() {
        return performerType;
    }

    public void setPerformerType(Set<TaskPerformerTypeType> performerType) {
        this.performerType.clear();
        this.performerType.addAll(performerType);
    }

    //
    // Business Methods
    //

    public void addPerformerType(TaskPerformerTypeType performerType){
        if(performerType == null){
            return;
        }
        if(!getPerformerType().contains(performerType)){
            getPerformerType().add(performerType);
        }
    }

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

    public void addFulfillmentProcessingPlant(ComponentIdType processingPlantId){
        if(processingPlantId == null){
            return;
        }
        if(!getFulfillmentProcessingPlants().contains(processingPlantId)){
            getFulfillmentProcessingPlants().add(processingPlantId);
        }
    }

    public void addFulfillmentProcessingPlants(Collection<ComponentIdType> processingPlants){
        if(processingPlants == null){
            return;
        }
        if(processingPlants.isEmpty()){
            return;
        }
        for(ComponentIdType currentProcessingPlant: processingPlants){
            addFulfillmentProcessingPlant(currentProcessingPlant);
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
                ", persistenceInstant=" + persistenceInstant +
                ", fulfillmentProcessingPlants=" + fulfillmentProcessingPlants +
                ", performerType=" + performerType +
                ", resourceStatus=" + resourceStatus +
                '}';
    }
}
