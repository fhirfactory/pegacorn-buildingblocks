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
package net.fhirfactory.pegacorn.core.model.topology.mode;

public enum NetworkSecurityZoneEnum {
    DATA_CENTRE_PRIVATE_NETWORK("PrivateNetwork","pegacorn.fhir.device.metadata.network-security-zone.private-network","Data Centre Private Network"),
    DATA_CENTRE_PRIVATE_DMZ("PrivateDMZ","pegacorn.fhir.device.metadata.network-security-zone.private-dmz","Data Centre Private DMZ"),
    DATA_CENTRE_PUBLIC_DMZ("PublicDMZ","pegacorn.fhir.device.metadata.network-security-zone.public-dmz","Data Centre Public DMZ"),
    ENTERPRISE_GUEST_NETWORK("EnterpriseGuest","pegacorn.fhir.device.metadata.network-security-zone.enterprise-guest","Enterprise Guest Network"),
    ENTERPRISE_RESTRICTED_NETWORK("EnterpriseRestricted","pegacorn.fhir.device.metadata.network-security-zone.enterprise-restricted","Enterprise Restricted (or Authenticated) Network"),
    INTERNET("Internet","pegacorn.fhir.device.metadata.network-security-zone.internet","Internet (unrestricted, hostile)");


    private String token;
    private String displayName;
    private String displayText;

    private NetworkSecurityZoneEnum(String name, String token, String text) {
        this.token = token;
        this.displayName = name;
        this.displayText = text;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(displayName);
    }

    public String getDisplayText(){
        return(displayText);
    }

    public static NetworkSecurityZoneEnum fromDisplayName(String zoneString){
        for (NetworkSecurityZoneEnum b : NetworkSecurityZoneEnum.values()) {
            if (b.getDisplayName().equalsIgnoreCase(zoneString)) {
                return b;
            }
        }
        return null;
    }

    public static NetworkSecurityZoneEnum fromSecurityZoneCamelCaseString(String zoneCamelCaseString){
        for (NetworkSecurityZoneEnum b : NetworkSecurityZoneEnum.values()) {
            if (b.getDisplayName().equalsIgnoreCase(zoneCamelCaseString)) {
                return b;
            }
        }
        return null;
    }
}
