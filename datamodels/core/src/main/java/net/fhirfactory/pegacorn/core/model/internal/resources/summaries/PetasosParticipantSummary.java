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
package net.fhirfactory.pegacorn.core.model.internal.resources.summaries;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantFulfillment;
import net.fhirfactory.pegacorn.core.model.internal.resources.summaries.common.ResourceSummaryBase;

public class PetasosParticipantSummary extends ResourceSummaryBase {
    private String participantName;
    private String nodeVersion;
    private PetasosParticipantFulfillment fulfillmentState;
    private TopologyNodeFunctionFDN topologyNodeFunctionFDN;
    private PetasosMonitoredComponentTypeEnum nodeType;

    //
    // Constructor(s)
    //

    public PetasosParticipantSummary(){
        this.participantName = null;
        this.nodeVersion = null;
        this.fulfillmentState = null;
        this.topologyNodeFunctionFDN = null;
        this.nodeType = null;
    }

    //
    // Getters and Setters
    //

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getNodeVersion() {
        return nodeVersion;
    }

    public void setNodeVersion(String nodeVersion) {
        this.nodeVersion = nodeVersion;
    }

    public PetasosMonitoredComponentTypeEnum getNodeType() {
        return nodeType;
    }

    public void setNodeType(PetasosMonitoredComponentTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    public PetasosParticipantFulfillment getFulfillmentState() {
        return fulfillmentState;
    }

    public void setFulfillmentState(PetasosParticipantFulfillment fulfillmentState) {
        this.fulfillmentState = fulfillmentState;
    }

    public TopologyNodeFunctionFDN getTopologyNodeFunctionFDN() {
        return topologyNodeFunctionFDN;
    }

    public void setTopologyNodeFunctionFDN(TopologyNodeFunctionFDN topologyNodeFunctionFDN) {
        this.topologyNodeFunctionFDN = topologyNodeFunctionFDN;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosParticipantSummary{" +
                "participantName='" + participantName + '\'' +
                ", nodeVersion='" + nodeVersion + '\'' +
                ", fulfillmentState=" + fulfillmentState +
                ", topologyNodeFunctionFDN=" + topologyNodeFunctionFDN +
                ", nodeType=" + nodeType +
                ", lastSynchronisationInstant=" + getLastSynchronisationInstant() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", resourceId='" + getResourceId() + '\'' +
                '}';
    }
}
