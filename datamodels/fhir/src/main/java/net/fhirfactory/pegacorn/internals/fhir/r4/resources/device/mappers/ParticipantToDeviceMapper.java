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

import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.DRICaTSIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.valuesets.DeviceNameTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.DRICaTSIdentifierFactory;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.codesystems.DeviceStatusReason;
import org.hl7.fhir.r4.model.codesystems.DeviceStatusReasonEnumFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ParticipantToDeviceMapper {
    private static final Logger LOG = LoggerFactory.getLogger(ParticipantToDeviceMapper.class);

    @Inject
    private DRICaTSIdentifierFactory identifierFactory;

    @Inject
    private DeviceStatusMapper statusMapper;

    //
    // Constructor(s)
    //

    //
    // Post Construct
    //

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected DRICaTSIdentifierFactory getIdentifierFactory(){
        return(identifierFactory);
    }

    protected DeviceStatusMapper getStatusMapper(){
        return(statusMapper);
    }

    //
    // Mappers / Transformers
    //

    public Device mapPetasosParticipantToDevice(PetasosParticipant participant){

        Device device = new Device();

        getLogger().trace(".mapPetasosParticipantToDevice(): [Create Identifier] Start");
        Identifier identifier = getIdentifierFactory().newIdentifier(DRICaTSIdentifierCodeEnum.IDENTIFIER_CODE_PARTICIPANT_ID, participant.getParticipantId().getName(),null);
        device.addIdentifier(identifier);
        getLogger().trace(".mapPetasosParticipantToDevice(): [Create Identifier] Finish");

        getLogger().trace(".mapPetasosParticipantToDevice(): [Create Name Set] Start");
        if(StringUtils.isNotEmpty(participant.getParticipantId().getFullName())) {
            Device.DeviceDeviceNameComponent fullNameComponent = newNameComponent(participant.getParticipantId().getFullName(), DeviceNameTypeEnum.DEVICE_FULL_NAME.getToken());
            device.addDeviceName(fullNameComponent);
        }
        if(StringUtils.isNotEmpty(participant.getParticipantId().getFullName())) {
            Device.DeviceDeviceNameComponent displayName = newNameComponent(participant.getParticipantId().getDisplayName(), DeviceNameTypeEnum.DEVICE_DISPLAY_NAME.getToken());
            device.addDeviceName(displayName);
        }
        if(StringUtils.isNotEmpty(participant.getParticipantId().getSubsystemName())) {
            Device.DeviceDeviceNameComponent subsystemName = newNameComponent(participant.getParticipantId().getSubsystemName(), DeviceNameTypeEnum.DEVICE_SUBSYSTEM_NAME.getToken());
            device.addDeviceName(subsystemName);
        }
        getLogger().trace(".mapPetasosParticipantToDevice(): [Create Name Set] Finish");

        getLogger().trace(".mapPetasosParticipantToDevice(): [Map Device Status/Status-Reason] Start");
        CodeableConcept deviceStatusReason = getStatusMapper().mapParticipantStatusToDeviceStatusReasonCC(participant.getParticipantStatus());
        Device.FHIRDeviceStatus deviceStatus = getStatusMapper().mapParticipantStatusToDeviceStatus(participant.getParticipantStatus());
        device.addStatusReason(deviceStatusReason);
        device.setStatus(deviceStatus);
        getLogger().trace(".mapPetasosParticipantToDevice(): [Map Device Status/Status-Reason] Finish");

        participant.getTaskQueueStatus();
        participant.getOutputs();
        participant.getFulfillmentState();
        participant.getSubscriptions();
        participant.get
    }

    private Device.DeviceDeviceNameComponent newNameComponent(String name, Device.DeviceNameType nameType){
        Device.DeviceDeviceNameComponent nameComponent = new Device.DeviceDeviceNameComponent();
        nameComponent.setName(name);
        nameComponent.setType(nameType);
        return(nameComponent);
    }
}
