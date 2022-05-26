/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.petasos.participant;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PetasosParticipantFulfillment implements Serializable {
    private Set<ComponentIdType> fulfillerComponents;
    private PetasosParticipantFulfillmentStatusEnum fulfillmentStatus;
    private Integer numberOfFulfillersExpected;
    private Integer numberOfActualFulfillers;

    //
    // Constructor(s)
    //

    public PetasosParticipantFulfillment(){
        this.fulfillerComponents = new HashSet<>();
        this.fulfillmentStatus = PetasosParticipantFulfillmentStatusEnum.PETASOS_PARTICIPANT_UNFULFILLED;
        this.numberOfActualFulfillers = 0;
        this.numberOfFulfillersExpected = 0;
    }

    //
    // Getters and Setters
    //

    public Set<ComponentIdType> getFulfillerComponents() {
        return fulfillerComponents;
    }

    public void setFulfillerComponents(Set<ComponentIdType> fulfillerComponents) {
        this.fulfillerComponents = fulfillerComponents;
    }

    public PetasosParticipantFulfillmentStatusEnum getFulfillmentStatus() {
        return fulfillmentStatus;
    }

    public void setFulfillmentStatus(PetasosParticipantFulfillmentStatusEnum fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public Integer getNumberOfFulfillersExpected() {
        return numberOfFulfillersExpected;
    }

    public void setNumberOfFulfillersExpected(Integer numberOfFulfillersExpected) {
        this.numberOfFulfillersExpected = numberOfFulfillersExpected;
    }

    public Integer getNumberOfActualFulfillers() {
        return numberOfActualFulfillers;
    }

    public void setNumberOfActualFulfillers(Integer numberOfActualFulfillers) {
        this.numberOfActualFulfillers = numberOfActualFulfillers;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosParticipantFulfillment{" +
                "fulfillerComponents=" + fulfillerComponents +
                ", fulfillmentStatus=" + fulfillmentStatus +
                ", numberOfFulfillersExpected=" + numberOfFulfillersExpected +
                ", numberOfActualFulfillers=" + numberOfActualFulfillers +
                '}';
    }
}
