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

import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.valuesets.DeviceSecurityZoneEnum;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.codesystems.DeviceStatus;
import org.hl7.fhir.r4.model.codesystems.DeviceStatusReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@ApplicationScoped
public class DeviceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceFactory.class);

    @Inject
    private DeviceMetaTagFactory metaTagFactory;

    @Inject
    private DevicePropertyFactory propertyFactory;

    //
    // Business Methods
    //

    public Device newDevice(
            PegacornIdentifierCodeEnum identifierCode, String identifierValue,
            DeviceStatus deviceStatus,
            DeviceStatusReason deviceStatusReason,
            String deviceName,
            Device.DeviceNameType deviceNameType,
            Set<Device.DevicePropertyComponent> properties,
            ResilienceModeEnum resilienceMode,
            ConcurrencyModeEnum concurrencyMode,
            DeviceSecurityZoneEnum securityZone
            ){

        Coding deviceSecurityZoneTag = getMetaTagFactory().newSecurityTag(securityZone);
        Device.DevicePropertyComponent resiliencePropertyComponent = getPropertyFactory().newDeviceProperty(resilienceMode);
        Device.DevicePropertyComponent concurrencyPropertyComponent = getPropertyFactory().newDeviceProperty(concurrencyMode);

        Device device = new Device();

        device.getMeta().addTag(deviceSecurityZoneTag);
        device.getMeta().setLastUpdated(Date.from(Instant.now()));
        device.addProperty(resiliencePropertyComponent);
        device.addProperty(concurrencyPropertyComponent);


        return(device);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected DeviceMetaTagFactory getMetaTagFactory(){
        return(metaTagFactory);
    }

    protected DevicePropertyFactory getPropertyFactory(){
        return(propertyFactory);
    }
}
