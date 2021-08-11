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
package net.fhirfactory.pegacorn.core.model.tasks;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.core.model.tasks.base.PetasosCapabilityDefinition;
import net.fhirfactory.pegacorn.core.model.tasks.valuesets.PetasosCapabilityDeliveryNodeStatusEnum;

import java.util.List;
import java.util.Objects;

public class PetasosCapabilityDeliveryNode {
    private List<PetasosCapabilityDefinition> supportedCapabilities;
    private PetasosEndpointIdentifier routingEndpointID;
    private TopologyNodeFDNToken deliveryNodeID;
    private PetasosCapabilityDeliveryNodeStatusEnum capabilityDeliveryNodeStatus;

    public List<PetasosCapabilityDefinition> getSupportedCapabilities() {
        return supportedCapabilities;
    }

    public void setSupportedCapabilities(List<PetasosCapabilityDefinition> supportedCapabilities) {
        this.supportedCapabilities = supportedCapabilities;
    }

    public PetasosEndpointIdentifier getRoutingEndpointID() {
        return routingEndpointID;
    }

    public void setRoutingEndpointID(PetasosEndpointIdentifier routingEndpointID) {
        this.routingEndpointID = routingEndpointID;
    }

    public TopologyNodeFDNToken getDeliveryNodeID() {
        return deliveryNodeID;
    }

    public void setDeliveryNodeID(TopologyNodeFDNToken deliveryNodeID) {
        this.deliveryNodeID = deliveryNodeID;
    }

    @Override
    public String toString() {
        return "TaskCompletionEngine{" +
                "supportedCapabilities=" + supportedCapabilities +
                ", routingEndpointID=" + routingEndpointID +
                ", taskCompletionEngineWUP=" + deliveryNodeID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosCapabilityDeliveryNode)) return false;
        PetasosCapabilityDeliveryNode that = (PetasosCapabilityDeliveryNode) o;
        return Objects.equals(getSupportedCapabilities(), that.getSupportedCapabilities()) && Objects.equals(getRoutingEndpointID(), that.getRoutingEndpointID()) && Objects.equals(getDeliveryNodeID(), that.getDeliveryNodeID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSupportedCapabilities(), getRoutingEndpointID(), getDeliveryNodeID());
    }
}
