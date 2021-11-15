/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.factories;

import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.valuesets.DevicePropertyTypeEnum;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DevicePropertyFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DevicePropertyFactory.class);

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    private static final String PEGACORN_DEVICE_PROPERTY_CODE_SYSTEM = "/device-property-type";
    private static final String PEGACORN_DEVICE_RESILIENCE_MODE_CODE_SYSTEM = "/device-resilience-mode-type";
    private static final String PEGACORN_DEVICE_CONCURRENCY_MODE_CODE_SYSTEM = "/device-concurrency-mode-type";

    //
    // Business Methods
    //

    public String getPegacornDevicePropertyCodeSystem() {
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_DEVICE_PROPERTY_CODE_SYSTEM;
        return (codeSystem);
    }

    public String getPegacornDeviceResilienceModeCodeSystem(){
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_DEVICE_RESILIENCE_MODE_CODE_SYSTEM;
        return (codeSystem);
    }

    public String getPegacornDeviceConcurrencyModeCodeSystem(){
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_DEVICE_CONCURRENCY_MODE_CODE_SYSTEM;
        return (codeSystem);
    }

    public Device.DevicePropertyComponent newDeviceProperty(ResilienceModeEnum resilienceMode){
        Device.DevicePropertyComponent propertyComponent = new Device.DevicePropertyComponent();
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem(getPegacornDevicePropertyCodeSystem());
        coding.setCode(DevicePropertyTypeEnum.DEVICE_PROPERTY_RESILIENCE_MODE.getToken());
        coding.setDisplay(DevicePropertyTypeEnum.DEVICE_PROPERTY_RESILIENCE_MODE.getDisplayName());
        codeableConcept.setText(DevicePropertyTypeEnum.DEVICE_PROPERTY_RESILIENCE_MODE.getDisplayText());
        codeableConcept.addCoding(coding);

        propertyComponent.setType(codeableConcept);

        CodeableConcept resilienceModeConcept = new CodeableConcept();
        Coding resilienceModeCoding = new Coding();

        resilienceModeCoding.setCode(resilienceMode.getResilienceMode());
        resilienceModeCoding.setDisplay(resilienceMode.getDisplayName());
        resilienceModeCoding.setSystem(getPegacornDeviceResilienceModeCodeSystem());
        resilienceModeConcept.addCoding(resilienceModeCoding);
        resilienceModeConcept.setText(resilienceMode.getDisplayName());
        propertyComponent.addValueCode(resilienceModeConcept);

        return(propertyComponent);
    }

    public Device.DevicePropertyComponent newDeviceProperty(ConcurrencyModeEnum concurrencyMode){
        Device.DevicePropertyComponent propertyComponent = new Device.DevicePropertyComponent();
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem(getPegacornDevicePropertyCodeSystem());
        coding.setCode(DevicePropertyTypeEnum.DEVICE_PROPERTY_CONCURRENCY_MODE.getToken());
        coding.setDisplay(DevicePropertyTypeEnum.DEVICE_PROPERTY_CONCURRENCY_MODE.getDisplayName());
        codeableConcept.setText(DevicePropertyTypeEnum.DEVICE_PROPERTY_CONCURRENCY_MODE.getDisplayText());
        codeableConcept.addCoding(coding);

        propertyComponent.setType(codeableConcept);

        CodeableConcept concurrencyModeCodeableConcept = new CodeableConcept();
        Coding concurrencyModeCoding = new Coding();

        concurrencyModeCoding.setCode(concurrencyMode.getConcurrencyMode());
        concurrencyModeCoding.setDisplay(concurrencyMode.getDisplayName());
        concurrencyModeCoding.setSystem(getPegacornDeviceResilienceModeCodeSystem());
        concurrencyModeCodeableConcept.addCoding(concurrencyModeCoding);
        concurrencyModeCodeableConcept.setText(concurrencyMode.getDisplayName());
        propertyComponent.addValueCode(concurrencyModeCodeableConcept);

        return(propertyComponent);
    }


    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
