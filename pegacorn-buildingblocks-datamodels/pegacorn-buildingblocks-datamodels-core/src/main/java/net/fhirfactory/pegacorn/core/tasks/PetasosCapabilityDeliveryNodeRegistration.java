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
package net.fhirfactory.pegacorn.core.tasks;

import net.fhirfactory.pegacorn.core.tasks.valuesets.PetasosCapabilityDeliveryNodeRegistrationStatusEnum;

import java.time.Instant;
import java.util.Objects;

public class PetasosCapabilityDeliveryNodeRegistration {
    private PetasosCapabilityDeliveryNode deliveryNode;
    private Instant registrationDate;
    private Instant lastActivityDate;
    private PetasosCapabilityDeliveryNodeRegistrationStatusEnum registrationStatus;

    public PetasosCapabilityDeliveryNode getDeliveryNode() {
        return deliveryNode;
    }

    public void setDeliveryNode(PetasosCapabilityDeliveryNode deliveryNode) {
        this.deliveryNode = deliveryNode;
    }

    public PetasosCapabilityDeliveryNodeRegistrationStatusEnum getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(PetasosCapabilityDeliveryNodeRegistrationStatusEnum registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public Instant getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Instant getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Instant lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosCapabilityDeliveryNodeRegistration)) return false;
        PetasosCapabilityDeliveryNodeRegistration that = (PetasosCapabilityDeliveryNodeRegistration) o;
        return Objects.equals(getDeliveryNode(), that.getDeliveryNode()) && Objects.equals(getRegistrationDate(), that.getRegistrationDate()) && Objects.equals(getLastActivityDate(), that.getLastActivityDate()) && getRegistrationStatus() == that.getRegistrationStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDeliveryNode(), getRegistrationDate(), getLastActivityDate(), getRegistrationStatus());
    }

    @Override
    public String toString() {
        return "PetasosTaskCompletionEngineRegistration{" +
                "taskCompletionEngineInstance=" + deliveryNode +
                ", registrationDate=" + registrationDate +
                ", lastActivityDate=" + lastActivityDate +
                ", tasksCompletionEngineStatus=" + registrationStatus +
                '}';
    }
}
