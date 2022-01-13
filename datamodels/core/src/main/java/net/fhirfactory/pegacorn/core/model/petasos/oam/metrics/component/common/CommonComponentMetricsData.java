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
    private int egressMessageCount;
    private int distributedMessageCount;
    private Map<String, Integer> distributionCountMap;

    //
    // Constructor(s)
    //

    public CommonComponentMetricsData(){
        this.ingresMessageCount = 0;
        this.egressMessageCount = 0;
        this.distributedMessageCount = 0;
        this.distributionCountMap = new HashMap<>();
        this.componentID = null;
        this.componentType = null;
        this.lastActivityInstant = null;
        this.componentStartupInstant = null;
        this.componentStatus = null;
        this.participantName = null;
    }

    public CommonComponentMetricsData(ComponentIdType componentId){
        this.ingresMessageCount = 0;
        this.egressMessageCount = 0;
        this.distributedMessageCount = 0;
        this.distributionCountMap = new HashMap<>();
        this.componentID = componentId;
        this.componentType = null;
        this.lastActivityInstant = null;
        this.componentStartupInstant = null;
        this.componentStatus = null;
        this.participantName = null;
    }

    //
    // Getters and Setters
    //

    public int getIngresMessageCount() {
        return ingresMessageCount;
    }

    public void setIngresMessageCount(int ingresMessageCount) {
        this.ingresMessageCount = ingresMessageCount;
    }

    public int getEgressMessageCount() {
        return egressMessageCount;
    }

    public void setEgressMessageCount(int egressMessageCount) {
        this.egressMessageCount = egressMessageCount;
    }

    public int getDistributedMessageCount() {
        return distributedMessageCount;
    }

    public void setDistributedMessageCount(int distributedMessageCount) {
        this.distributedMessageCount = distributedMessageCount;
    }

    public Map<String, Integer> getDistributionCountMap() {
        return distributionCountMap;
    }

    public void setDistributionCountMap(Map<String, Integer> distributionCountMap) {
        this.distributionCountMap = distributionCountMap;
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

    //
    // To String
    //

    @Override
    public String toString() {
        return "CommonMetricsData{" +
                "componentID=" + componentID +
                ", participantName=" + participantName +
                ", componentType='" + componentType + '\'' +
                ", lastActivityInstant=" + lastActivityInstant +
                ", componentStartupInstant=" + componentStartupInstant +
                ", componentStatus='" + componentStatus + '\'' +
                ", ingresMessageCount=" + ingresMessageCount +
                ", egressMessageCount=" + egressMessageCount +
                ", distributedMessageCount=" + distributedMessageCount +
                ", distributionCountMap=" + distributionCountMap +
                '}';
    }
}
