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
package net.fhirfactory.pegacorn.core.model.tasks.valuesets;

/**
 * This enum is used to represent the status of the PetasosCapabilityDeliveryNode from an operational perspective.
 * It is (typically) updated via a probe of the PetasosEndpoint acting as a PetasosCapabilityEndpointRouter and should
 * reflect to the operational status of the target PetasosCapabilityDeliveryNode in which it is encapsulated.
 */
public enum PetasosCapabilityDeliveryNodeStatusEnum {
    /**
     * The PetasosCapabilityDeliveryNode is operational and can accept "Tasks" that the associated Capability will act
     * upon.
     */
    FULFILLMENT_CAPABILITY_OPERATIONAL,
    /**
     * The PetasosCapabilityDeliveryNode is still starting, and thus cannot accept "Tasks". It is expected that a
     * subsequent probe of the node will yield an operational (see above) or failed (see below) state.
     */
    FULFILLMENT_CAPABILITY_STARTUP,
    /**
     * PetasosCapabilityDeliveryNode has failed, and it should be removed from all Lists and not considered for
     * any future "Tasks" or scaning/probes. It is expected that the PetasosCapabilityDeliveryNode will either
     * be terminated by its' Watchdog or the underlying IT/Ops (Kubernetes) services.
     */
    FULFILLMENT_CAPABILITY_FAILED
}
