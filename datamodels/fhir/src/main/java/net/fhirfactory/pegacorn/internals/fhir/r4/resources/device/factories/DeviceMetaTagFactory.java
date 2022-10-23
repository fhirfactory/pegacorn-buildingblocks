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

import net.fhirfactory.pegacorn.core.constants.systemwide.DRICaTSReferenceProperties;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import org.hl7.fhir.r4.model.Coding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeviceMetaTagFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceMetaTagFactory.class);


    @Inject
    private DRICaTSReferenceProperties systemWideProperties;

    private static final String PEGACORN_DEVICE_SECURITY_ZONE_META_TAG_SYSTEM = "/device-meta-tag-security-zone";
    private static final String PEGACORN_DEVICE_DEPLOYMENT_POD_ID = "/device-meta-tag-pod-id";
    private static final String PEGACORN_DEVICE_DEPLOYMENT_POD_IP_ADDRESS = "/device-meta-tag-pod-ip";
    private static final String PEGACORN_DEVICE_DEPLOYMENT_HOST_IP_ADDRESS = "/device-meta-tag-host-ip";

    //
    // Business Methods
    //

    public String getPegacornDeviceSecurityZoneMetaTagSystem() {
        String codeSystem = systemWideProperties.getDRICaTSCodeSystemSite() + PEGACORN_DEVICE_SECURITY_ZONE_META_TAG_SYSTEM;
        return (codeSystem);
    }

    public String getPegacornDeviceDeploymentPodId() {
        String codeSystem = systemWideProperties.getDRICaTSCodeSystemSite() + PEGACORN_DEVICE_DEPLOYMENT_POD_ID;
        return(codeSystem);
    }

    public String getPegacornDeviceDeploymentPodIpAddress() {
        String codeSystem = systemWideProperties.getDRICaTSCodeSystemSite() + PEGACORN_DEVICE_DEPLOYMENT_POD_IP_ADDRESS;
        return(codeSystem);
    }

    public String getPegacornDeviceDeploymentHostIpAddress() {
        String codeSystem = systemWideProperties.getDRICaTSCodeSystemSite() + PEGACORN_DEVICE_DEPLOYMENT_HOST_IP_ADDRESS;
        return(codeSystem);
    }

    public Coding newSecurityTag(NetworkSecurityZoneEnum securityZone){
        Coding securityZoneCoding = new Coding();
        securityZoneCoding.setSystem(getPegacornDeviceSecurityZoneMetaTagSystem());
        securityZoneCoding.setCode(securityZone.getToken());
        securityZoneCoding.setDisplay(securityZone.getDisplayName());
        return(securityZoneCoding);
    }

    public Coding newPodIdTag(String podId){
        Coding podIdCoding = new Coding();
        podIdCoding.setSystem(getPegacornDeviceDeploymentPodId());
        podIdCoding.setCode(podId);
        return(podIdCoding);
    }

    public Coding newPodIPAddressTag(String ipAddress){
        Coding podIPAddressCoding = new Coding();
        podIPAddressCoding.setSystem(getPegacornDeviceDeploymentPodIpAddress());
        podIPAddressCoding.setCode(ipAddress);
        return(podIPAddressCoding);
    }

    public Coding newHostIPAddressTag(String ipAddress){
        Coding hostIPAddressCoding = new Coding();
        hostIPAddressCoding.setSystem(getPegacornDeviceDeploymentHostIpAddress());
        hostIPAddressCoding.setCode(ipAddress);
        return(hostIPAddressCoding);
    }
}
