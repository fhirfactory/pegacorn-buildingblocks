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
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeviceSpecialisationFactory {

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    private static final String PEGACORN_COMPONENT_ROLE_AS_SPECIALISATION_SYSTEM = "/device-specialisation-component-role";

    //
    // Business Methods
    //

    public String getPegacornComponentRoleAsSpecialisationSystem() {
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_COMPONENT_ROLE_AS_SPECIALISATION_SYSTEM;
        return (codeSystem);
    }

    public Device.DeviceSpecializationComponent newDeviceSpecialisation(SoftwareComponentConnectivityContextEnum role){
        Device.DeviceSpecializationComponent specialisation = new Device.DeviceSpecializationComponent();
        CodeableConcept specialisationCC = new CodeableConcept();
        Coding specialisationCoding = new Coding();
        specialisationCoding.setSystem(getPegacornComponentRoleAsSpecialisationSystem());
        specialisationCoding.setCode(role.getToken());
        specialisationCoding.setDisplay(role.getDisplayName());
        specialisationCC.addCoding(specialisationCoding);
        specialisationCC.setText(role.getDisplayText());
        specialisation.setSystemType(specialisationCC);
        return(specialisation);
    }
}
