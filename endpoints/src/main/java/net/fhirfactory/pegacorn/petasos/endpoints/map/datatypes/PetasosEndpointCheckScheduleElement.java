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
package net.fhirfactory.pegacorn.petasos.endpoints.map.datatypes;


import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;

import java.time.Instant;

public class PetasosEndpointCheckScheduleElement {
    private Instant targetTime;
    private PetasosEndpointIdentifier petasosEndpointID;
    private boolean endpointRemoved;
    private boolean endpointAdded;
    private int retryCount;

    public PetasosEndpointCheckScheduleElement(){
        this.targetTime = null;
        this.petasosEndpointID = null;
        this.retryCount = 0;
    }

    public PetasosEndpointCheckScheduleElement(PetasosEndpointIdentifier petasosEndpointID, boolean endpointRemoved, boolean endpointAdded){
        this.petasosEndpointID = petasosEndpointID;
        this.targetTime = Instant.now().plusSeconds(10);
        this.endpointAdded = endpointAdded;
        this.endpointRemoved = endpointRemoved;
        this.retryCount = 0;
    }

    public PetasosEndpointCheckScheduleElement(PetasosEndpointIdentifier petasosEndpointID, boolean endpointRemoved, boolean endpointAdded, int currentRetries){
        this.petasosEndpointID = petasosEndpointID;
        this.targetTime = Instant.now().plusSeconds(10);
        this.endpointAdded = endpointAdded;
        this.endpointRemoved = endpointRemoved;
        this.retryCount = currentRetries;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(Instant targetTime) {
        this.targetTime = targetTime;
    }

    public PetasosEndpointIdentifier getPetasosEndpointID() {
        return petasosEndpointID;
    }

    public void setPetasosEndpointID(PetasosEndpointIdentifier petasosEndpointID) {
        this.petasosEndpointID = petasosEndpointID;
    }

    public boolean isEndpointRemoved() {
        return endpointRemoved;
    }

    public void setEndpointRemoved(boolean endpointRemoved) {
        this.endpointRemoved = endpointRemoved;
    }

    public boolean isEndpointAdded() {
        return endpointAdded;
    }

    public void setEndpointAdded(boolean endpointAdded) {
        this.endpointAdded = endpointAdded;
    }

    @Override
    public String toString() {
        return "PetasosEndpointCheckScheduleElement{" +
                "targetTime=" + targetTime +
                ", petasosEndpointID=" + petasosEndpointID +
                ", endpointRemoved=" + endpointRemoved +
                ", endpointAdded=" + endpointAdded +
                ", retryCount=" + retryCount +
                '}';
    }
}
