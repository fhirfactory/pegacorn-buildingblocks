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
package net.fhirfactory.pegacorn.core.model.petasos.oam.notifications;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;

import java.io.Serializable;

public class PetasosComponentITOpsNotification implements Serializable {
    private ComponentIdType componentId;
    private String processingPlantParticipantName;
    private String workshopParticipantName;
    private String workUnitProcessorParticipantName;
    private PetasosMonitoredComponentTypeEnum componentType;
    private String content;

    //
    // Constructor
    //

    public PetasosComponentITOpsNotification(){
        this.componentId = null;
        this.processingPlantParticipantName = null;
        this.componentType = null;
        this.content = null;
        this.workshopParticipantName = null;
        this.workUnitProcessorParticipantName = null;
    }

    public PetasosComponentITOpsNotification(ComponentIdType componentId, String name, PetasosMonitoredComponentTypeEnum componentType, String content){
        this.componentType = componentType;
        this.processingPlantParticipantName = name;
        this.componentId = componentId;
        this.content = content;
        this.workshopParticipantName = null;
        this.workUnitProcessorParticipantName = null;
    }

    //
    // Getters and Setters
    //

    public ComponentIdType getComponentId() {
        return componentId;
    }

    public void setComponentId(ComponentIdType componentId) {
        this.componentId = componentId;
    }

    public String getProcessingPlantParticipantName() {
        return processingPlantParticipantName;
    }

    public void setProcessingPlantParticipantName(String processingPlantParticipantName) {
        this.processingPlantParticipantName = processingPlantParticipantName;
    }

    public PetasosMonitoredComponentTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(PetasosMonitoredComponentTypeEnum componentType) {
        this.componentType = componentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWorkshopParticipantName() {
        return workshopParticipantName;
    }

    public void setWorkshopParticipantName(String workshopParticipantName) {
        this.workshopParticipantName = workshopParticipantName;
    }

    public String getWorkUnitProcessorParticipantName() {
        return workUnitProcessorParticipantName;
    }

    public void setWorkUnitProcessorParticipantName(String workUnitProcessorParticipantName) {
        this.workUnitProcessorParticipantName = workUnitProcessorParticipantName;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosComponentITOpsNotification{" +
                "componentId=" + componentId +
                ", processingPlantParticipantName='" + processingPlantParticipantName + '\'' +
                ", workshopParticipantName='" + workshopParticipantName + '\'' +
                ", workUnitProcessorParticipantName='" + workUnitProcessorParticipantName + '\'' +
                ", componentType=" + componentType +
                ", content='" + content + '\'' +
                '}';
    }
}
