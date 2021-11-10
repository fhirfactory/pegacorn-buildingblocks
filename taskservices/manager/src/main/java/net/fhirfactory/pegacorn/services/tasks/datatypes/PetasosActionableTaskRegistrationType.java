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
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PetasosActionableTaskRegistrationType implements Serializable {
    private PetasosActionableTask actionableTask;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant registrationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant checkInstant;
    private List<ComponentIdType> fulfillmentProcessingPlants;
    private List<String> fulfillmentServiceNames;
    private List<TaskPerformerTypeType> performerType;

    //
    // Constructor(s)
    //

    public PetasosActionableTaskRegistrationType(){
        this.actionableTask = null;
        this.registrationInstant = null;
        this.checkInstant = null;
        this.fulfillmentProcessingPlants = new ArrayList<>();
        this.performerType = new ArrayList<>();
        this.fulfillmentServiceNames = new ArrayList<>();
    }

    //
    // Getters and Setters
    //

    public PetasosActionableTask getActionableTask() {
        return actionableTask;
    }

    public void setActionableTask(PetasosActionableTask actionableTask) {
        this.actionableTask = actionableTask;
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

    public List<ComponentIdType> getFulfillmentProcessingPlants() {
        return fulfillmentProcessingPlants;
    }

    public void setFulfillmentProcessingPlants(List<ComponentIdType> fulfillmentProcessingPlants) {
        this.fulfillmentProcessingPlants = fulfillmentProcessingPlants;
    }

    public List<String> getFulfillmentServiceNames() {
        return fulfillmentServiceNames;
    }

    public void setFulfillmentServiceNames(List<String> fulfillmentServiceNames) {
        this.fulfillmentServiceNames = fulfillmentServiceNames;
    }

    public List<TaskPerformerTypeType> getPerformerType() {
        return performerType;
    }

    public void setPerformerType(List<TaskPerformerTypeType> performerType) {
        this.performerType = performerType;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosActionableTaskRegistrationType{" +
                "actionableTask=" + actionableTask +
                ", registrationInstant=" + registrationInstant +
                ", checkInstant=" + checkInstant +
                ", fulfillmentProcessingPlants=" + fulfillmentProcessingPlants +
                ", fulfillmentServiceNames=" + fulfillmentServiceNames +
                ", performerType=" + performerType +
                '}';
    }
}
