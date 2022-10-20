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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CommonComponentMetricsData implements Serializable {
    private ComponentIdType componentID;
    private String participantName;
    private PetasosMonitoredComponentTypeEnum componentType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastActivityInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant componentStartupInstant;
    private String componentStatus;

    private int ingresMessageCount;
    private int egressMessageAttemptCount;
    private int egressMessageSuccessCount;
    private int egressMessageFailureCount;
    private int internalDistributedMessageCount;
    private int internalReceivedMessageCount;
    private Map<String, Integer> rpcRequestsCount;
    private Map<String, Integer> rpcInvocationsCount;
    private Map<String, Integer> internalDistributionCountMap;

    //
    // Constructor(s)
    //

    public CommonComponentMetricsData(){
        this.ingresMessageCount = 0;
        this.egressMessageAttemptCount = 0;
        this.internalDistributedMessageCount = 0;
        this.internalDistributionCountMap = new HashMap<>();
        this.rpcInvocationsCount = new HashMap<>();
        this.rpcRequestsCount = new HashMap<>();
        this.componentID = null;
        this.componentType = null;
        this.lastActivityInstant = null;
        this.componentStartupInstant = null;
        this.componentStatus = null;
        this.participantName = null;
        this.internalReceivedMessageCount = 0;
        this.egressMessageFailureCount = 0;
        this.egressMessageSuccessCount = 0;
    }

    public CommonComponentMetricsData(ComponentIdType componentId){
        this.ingresMessageCount = 0;
        this.egressMessageAttemptCount = 0;
        this.internalDistributedMessageCount = 0;
        this.internalDistributionCountMap = new HashMap<>();
        this.rpcInvocationsCount = new HashMap<>();
        this.rpcRequestsCount = new HashMap<>();
        this.componentID = componentId;
        this.componentType = null;
        this.lastActivityInstant = null;
        this.componentStartupInstant = null;
        this.componentStatus = null;
        this.participantName = null;
        this.internalReceivedMessageCount = 0;
        this.egressMessageFailureCount = 0;
        this.egressMessageSuccessCount = 0;
    }

    //
    // Getters and Setters
    //


    public int getEgressMessageSuccessCount() {
        return egressMessageSuccessCount;
    }

    public void setEgressMessageSuccessCount(int egressMessageSuccessCount) {
        this.egressMessageSuccessCount = egressMessageSuccessCount;
    }

    public int getEgressMessageFailureCount() {
        return egressMessageFailureCount;
    }

    public void setEgressMessageFailureCount(int egressMessageFailureCount) {
        this.egressMessageFailureCount = egressMessageFailureCount;
    }

    public int getIngresMessageCount() {
        return ingresMessageCount;
    }

    public void setIngresMessageCount(int ingresMessageCount) {
        this.ingresMessageCount = ingresMessageCount;
    }

    public int getEgressMessageAttemptCount() {
        return egressMessageAttemptCount;
    }

    public void setEgressMessageAttemptCount(int egressMessageAttemptCount) {
        this.egressMessageAttemptCount = egressMessageAttemptCount;
    }

    public int getInternalDistributedMessageCount() {
        return internalDistributedMessageCount;
    }

    public void setInternalDistributedMessageCount(int internalDistributedMessageCount) {
        this.internalDistributedMessageCount = internalDistributedMessageCount;
    }

    public Map<String, Integer> getInternalDistributionCountMap() {
        return internalDistributionCountMap;
    }

    public void setInternalDistributionCountMap(Map<String, Integer> internalDistributionCountMap) {
        this.internalDistributionCountMap = internalDistributionCountMap;
    }

    public ComponentIdType getComponentID() {
        return componentID;
    }

    public void setComponentID(ComponentIdType componentID) {
        this.componentID = componentID;
    }

    public PetasosMonitoredComponentTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(PetasosMonitoredComponentTypeEnum componentType) {
        this.componentType = componentType;
    }

    public Instant getLastActivityInstant() {
        return lastActivityInstant;
    }

    public void setLastActivityInstant(Instant lastActivityInstant) {
        this.lastActivityInstant = lastActivityInstant;
    }

    public Instant getComponentStartupInstant() {
        return componentStartupInstant;
    }

    public void setComponentStartupInstant(Instant componentStartupInstant) {
        this.componentStartupInstant = componentStartupInstant;
    }

    public String getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(String componentStatus) {
        this.componentStatus = componentStatus;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public int getInternalReceivedMessageCount() {
        return internalReceivedMessageCount;
    }

    public void setInternalReceivedMessageCount(int internalReceivedMessageCount) {
        this.internalReceivedMessageCount = internalReceivedMessageCount;
    }

    public Map<String, Integer> getRpcRequestsCount() {
        return rpcRequestsCount;
    }

    public void setRpcRequestsCount(Map<String, Integer> rpcRequestsCount) {
        this.rpcRequestsCount = rpcRequestsCount;
    }

    public Map<String, Integer> getRpcInvocationsCount() {
        return rpcInvocationsCount;
    }

    public void setRpcInvocationsCount(Map<String, Integer> rpcInvocationsCount) {
        this.rpcInvocationsCount = rpcInvocationsCount;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommonComponentMetricsData{");
        sb.append("componentID=").append(componentID);
        sb.append(", participantName='").append(participantName).append('\'');
        sb.append(", componentType=").append(componentType);
        sb.append(", lastActivityInstant=").append(lastActivityInstant);
        sb.append(", componentStartupInstant=").append(componentStartupInstant);
        sb.append(", componentStatus='").append(componentStatus).append('\'');
        sb.append(", ingresMessageCount=").append(ingresMessageCount);
        sb.append(", egressMessageAttemptCount=").append(egressMessageAttemptCount);
        sb.append(", egressMessageSuccessCount=").append(egressMessageSuccessCount);
        sb.append(", egressMessageFailureCount=").append(egressMessageFailureCount);
        sb.append(", internalDistributedMessageCount=").append(internalDistributedMessageCount);
        sb.append(", internalReceivedMessageCount=").append(internalReceivedMessageCount);
        sb.append(", rpcRequestsCount=").append(rpcRequestsCount);
        sb.append(", rpcInvocationsCount=").append(rpcInvocationsCount);
        sb.append(", internalDistributionCountMap=").append(internalDistributionCountMap);
        sb.append('}');
        return sb.toString();
    }
}
