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
package net.fhirfactory.pegacorn.core.model.ui.resources.summaries;

import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantFulfillment;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.common.ResourceSummaryBase;

public class PetasosParticipantSummary extends ResourceSummaryBase {
    private PetasosParticipantId participantId;
    private PetasosParticipantFulfillment fulfillmentState;
    private PetasosMonitoredComponentTypeEnum nodeType;
    private PetasosParticipantStatusEnum status;
    private PetasosParticipantControlStatusEnum controlStatus;

    //
    // Constructor(s)
    //

    public PetasosParticipantSummary(){
        this.participantId = new PetasosParticipantId();
        this.fulfillmentState = null;
        this.nodeType = null;
        this.status = PetasosParticipantStatusEnum.PARTICIPANT_IS_IDLE;
        this.controlStatus = PetasosParticipantControlStatusEnum.PARTICIPANT_IS_SUSPENDED;
    }

    //
    // Getters and Setters
    //


    public PetasosParticipantControlStatusEnum getControlStatus() {
        return controlStatus;
    }

    public void setControlStatus(PetasosParticipantControlStatusEnum controlStatus) {
        this.controlStatus = controlStatus;
    }

    public PetasosParticipantStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PetasosParticipantStatusEnum status) {
        this.status = status;
    }

    public PetasosParticipantId getParticipantId() {
        return participantId;
    }

    public void setParticipantId(PetasosParticipantId participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return getParticipantId().getName();
    }

    public void setParticipantName(String participantName) {
        this.getParticipantId().setName(participantName);
    }

    public String getNodeVersion() {
        return getParticipantId().getVersion();
    }

    public void setNodeVersion(String nodeVersion) {
        this.getParticipantId().setVersion(nodeVersion);
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

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosParticipantSummary{");
        sb.append("participantId=").append(participantId);
        sb.append(", fulfillmentState=").append(fulfillmentState);
        sb.append(", status=").append(status);
        sb.append(", controlStatus=").append(controlStatus);
        sb.append(", nodeType=").append(nodeType);
        sb.append(", lastSynchronisationInstant=").append(getLastSynchronisationInstant());
        sb.append(", lastActivityInstant=").append(getLastActivityInstant());
        sb.append(", resourceId='").append(getResourceId()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
