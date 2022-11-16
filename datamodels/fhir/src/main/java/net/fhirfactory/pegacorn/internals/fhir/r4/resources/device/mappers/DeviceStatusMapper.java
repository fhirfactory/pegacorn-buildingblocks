/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.mappers;

import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.codesystems.DeviceStatusReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DeviceStatusMapper {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceStatusMapper.class);


    public DeviceStatusReason mapParticipantStatusToDeviceStatusReason(PetasosParticipantStatusEnum participantStatus){

        if(participantStatus == null){
            return(DeviceStatusReason.OFFLINE);
        }

        switch(participantStatus){

            case PARTICIPANT_IS_ACTIVE:
                return(DeviceStatusReason.ONLINE);
            case PARTICIPANT_IS_IDLE:
                return(DeviceStatusReason.STANDBY);
            case PARTICIPANT_IS_NOT_READY:
                return(DeviceStatusReason.NOTREADY);
            case PARTICIPANT_IS_STOPPING:
                return(DeviceStatusReason.OFFLINE);
            case PARTICIPANT_HAS_FAILED:
                return(DeviceStatusReason.OFFLINE);
        }
        return(DeviceStatusReason.OFF);
    }

    public CodeableConcept mapParticipantStatusToDeviceStatusReasonCC(PetasosParticipantStatusEnum participantStatusEnum){
        DeviceStatusReason statusReason = mapParticipantStatusToDeviceStatusReason(participantStatusEnum);
        Coding coding = new Coding();
        coding.setDisplay(statusReason.getDisplay());
        coding.setSystem(statusReason.getSystem());
        coding.setCode(statusReason.toCode());
        CodeableConcept statusReasonCC = new CodeableConcept();
        statusReasonCC.setText(statusReason.getDefinition());
        statusReasonCC.addCoding(coding);
        return(statusReasonCC);
    }

    public Device.FHIRDeviceStatus mapParticipantStatusToDeviceStatus(PetasosParticipantStatusEnum participantStatus){

        if(participantStatus == null){
            return(Device.FHIRDeviceStatus.UNKNOWN);
        }

        switch(participantStatus){

            case PARTICIPANT_IS_ACTIVE:
                return(Device.FHIRDeviceStatus.ACTIVE);
            case PARTICIPANT_IS_IDLE:
                return(Device.FHIRDeviceStatus.INACTIVE);
            case PARTICIPANT_IS_NOT_READY:
            case PARTICIPANT_IS_STOPPING:
            case PARTICIPANT_HAS_FAILED:
            default:
                return(Device.FHIRDeviceStatus.UNKNOWN);
        }
    }

    public PetasosParticipantStatusEnum mapDeviceStatusToParticipantStatus(Device.FHIRDeviceStatus deviceStatus, DeviceStatusReason statusReason){
        if(deviceStatus == null){
            return(PetasosParticipantStatusEnum.PARTICIPANT_IS_NOT_READY);
        }
        if(statusReason == null){
            return(PetasosParticipantStatusEnum.PARTICIPANT_IS_NOT_READY);
        }
        switch(deviceStatus){
            case ACTIVE:
                return(PetasosParticipantStatusEnum.PARTICIPANT_IS_ACTIVE);
            case INACTIVE:
                return(PetasosParticipantStatusEnum.PARTICIPANT_IS_IDLE);
            case UNKNOWN:
                switch(statusReason){
                    case ONLINE:
                        return(PetasosParticipantStatusEnum.PARTICIPANT_IS_ACTIVE);
                    case PAUSED:
                    case STANDBY:
                        return(PetasosParticipantStatusEnum.PARTICIPANT_IS_IDLE);
                    case NULL:
                    case OFF:
                        return(PetasosParticipantStatusEnum.PARTICIPANT_HAS_FAILED);
                    case OFFLINE:
                    case NOTREADY:
                    case TRANSDUCDISCON:
                    case HWDISCON:
                    default:
                        return(PetasosParticipantStatusEnum.PARTICIPANT_IS_NOT_READY);
                }
            case ENTEREDINERROR:
            case NULL:
            default:
                return(PetasosParticipantStatusEnum.PARTICIPANT_IS_NOT_READY);
        }
    }
}
