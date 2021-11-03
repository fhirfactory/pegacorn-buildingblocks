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
package net.fhirfactory.pegacorn.petasos.oam.metrics.agents.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;

import java.io.Serializable;
import java.time.Instant;

public class ComponentMetricsAgentBase implements Serializable {
    private ComponentIdType componentID;
    private String metricsType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastActivityInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant componentStartupInstant;
    private String componentStatus;

    public ComponentMetricsAgentBase(){
        this.componentID = null;
        this.lastActivityInstant = Instant.EPOCH;
        this.componentStatus = null;
        this.componentStartupInstant = Instant.now();
        this.metricsType = null;
    }

    public ComponentMetricsAgentBase(ComponentIdType componentID){
        this.componentID = componentID;
        this.lastActivityInstant = Instant.EPOCH;
        this.componentStatus = null;
        this.componentStartupInstant = Instant.now();
        this.metricsType = null;
    }

    //
    // Some Helper Methods
    //

    @JsonIgnore
    public void touchLastActivityInstant(){
        this.lastActivityInstant = Instant.now();
    }

    //
    // Getters and Setters
    //


    public ComponentIdType getComponentID() {
        return componentID;
    }

    public void setComponentID(ComponentIdType componentID) {
        this.componentID = componentID;
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

    public String getMetricsType() {
        return metricsType;
    }

    public void setMetricsType(String metricsType) {
        this.metricsType = metricsType;
    }

    @Override
    public String toString() {
        return "net.fhirfactory.pegacorn.petasos.model.itops.metrics.common.NodeMetricsBase{" +
                "componentID='" + componentID + '\'' +
                ", metricsType='" + metricsType + '\'' +
                ", lastActivityInstant=" + lastActivityInstant +
                ", nodeStartupInstant=" + componentStartupInstant +
                ", nodeStatus='" + componentStatus + '\'' +
                '}';
    }
}
