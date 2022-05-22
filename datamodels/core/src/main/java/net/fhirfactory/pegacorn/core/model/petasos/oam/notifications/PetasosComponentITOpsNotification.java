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
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.valuesets.PetasosComponentITOpsNotificationTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import org.apache.commons.lang3.StringUtils;

public class PetasosComponentITOpsNotification extends ITOpsNotificationContent {
    private ComponentIdType componentId;
    private String participantName;
    private PetasosMonitoredComponentTypeEnum componentType;
    private PetasosComponentITOpsNotificationTypeEnum notificationType;

    //
    // Constructor
    //

    public PetasosComponentITOpsNotification(){
        super();
        this.componentId = null;
        this.participantName = null;
        this.componentType = null;
        this.notificationType = PetasosComponentITOpsNotificationTypeEnum.NORMAL_NOTIFICATION_TYPE;
    }

    public PetasosComponentITOpsNotification(ComponentIdType componentId, String name, PetasosMonitoredComponentTypeEnum componentType, String content){
        super(content);
        this.componentType = componentType;
        this.participantName = name;
        this.componentId = componentId;
        this.notificationType = PetasosComponentITOpsNotificationTypeEnum.NORMAL_NOTIFICATION_TYPE;
    }

    public PetasosComponentITOpsNotification(ComponentIdType componentId, String name, PetasosMonitoredComponentTypeEnum componentType, String content, String formattedContent){
        super(content, formattedContent);
        this.componentType = componentType;
        this.participantName = name;
        this.componentId = componentId;
        this.notificationType = PetasosComponentITOpsNotificationTypeEnum.NORMAL_NOTIFICATION_TYPE;
    }

    public PetasosComponentITOpsNotification(PetasosComponentITOpsNotification ori){
        super(ori);
        this.componentId = null;
        this.participantName = null;
        this.componentType = null;
        if(ori.getComponentId() != null){
            setComponentId(ori.getComponentId());
        }
        if(StringUtils.isNotEmpty(ori.getParticipantName())){
            setParticipantName(ori.getParticipantName());
        }
        setComponentType(ori.getComponentType());
    }

    //
    // Getters and Setters
    //

    public PetasosComponentITOpsNotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(PetasosComponentITOpsNotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
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

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosComponentITOpsNotification{" +
                "componentId=" + componentId +
                ", participantName='" + participantName + '\'' +
                ", componentType=" + componentType +
                ", content='" + getContent() + '\'' +
                ", formattedContent='" + getFormattedContent() + '\'' +
                ", contentHeading=" + getContentHeading() +
                ", notificationType=" + notificationType +
                '}';
    }
}
