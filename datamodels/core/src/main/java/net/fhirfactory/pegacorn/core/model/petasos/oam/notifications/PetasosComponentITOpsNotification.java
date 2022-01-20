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
    private String participantName;
    private PetasosMonitoredComponentTypeEnum componentType;
    private String content;
    private String formattedContent;

    //
    // Constructor
    //

    public PetasosComponentITOpsNotification(){
        this.componentId = null;
        this.participantName = null;
        this.componentType = null;
        this.content = null;
        this.formattedContent = null;
    }

    public PetasosComponentITOpsNotification(ComponentIdType componentId, String name, PetasosMonitoredComponentTypeEnum componentType, String content){
        this.componentType = componentType;
        this.participantName = name;
        this.componentId = componentId;
        this.content = content;
        this.formattedContent = null;
    }

    public PetasosComponentITOpsNotification(ComponentIdType componentId, String name, PetasosMonitoredComponentTypeEnum componentType, String content, String formattedContent){
        this.componentType = componentType;
        this.participantName = name;
        this.componentId = componentId;
        this.content = content;
        this.formattedContent = formattedContent;
    }

    //
    // Getters and Setters
    //

    public String getFormattedContent() {
        return formattedContent;
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public ComponentIdType getComponentId() {
        return componentId;
    }

    public void setComponentId(ComponentIdType componentId) {
        this.componentId = componentId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
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

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosComponentITOpsNotification{" +
                "componentId=" + componentId +
                ", participantName='" + participantName + '\'' +
                ", componentType=" + componentType +
                ", content='" + content + '\'' +
                ", formattedContent='" + formattedContent + '\'' +
                '}';
    }
}
