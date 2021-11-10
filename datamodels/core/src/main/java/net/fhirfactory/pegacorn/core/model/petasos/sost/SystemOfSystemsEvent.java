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
package net.fhirfactory.pegacorn.core.model.petasos.sost;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;

import java.io.Serializable;
import java.time.Instant;

public abstract class SystemOfSystemsEvent implements Serializable {
    private Instant eventInstant;
    private SystemOfSystemsEventTypeEnum eventType;
    private ComponentIdType associatedEndpoint;

    //
    // Constructor(s)
    //

    public SystemOfSystemsEvent(SystemOfSystemsEventTypeEnum newEventType, ComponentIdType endpointId){
        this.associatedEndpoint = endpointId;
        this.eventInstant = Instant.now();
        this.eventType = newEventType;
    }

    //
    // Getters and Setters
    //

    public Instant getEventInstant() {
        return eventInstant;
    }

    public void setEventInstant(Instant eventInstant) {
        this.eventInstant = eventInstant;
    }

    public SystemOfSystemsEventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(SystemOfSystemsEventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public ComponentIdType getAssociatedEndpoint() {
        return associatedEndpoint;
    }

    public void setAssociatedEndpoint(ComponentIdType associatedEndpoint) {
        this.associatedEndpoint = associatedEndpoint;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "SystemOfSystemsEvent{" +
                "eventInstant=" + eventInstant +
                ", eventType=" + eventType +
                ", associatedEndpoint=" + associatedEndpoint +
                '}';
    }
}
