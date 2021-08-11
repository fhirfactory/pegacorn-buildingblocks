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
package net.fhirfactory.pegacorn.core.deployment.common.valuesets;

public enum NetworkSecurityZoneEnum {
    ZONE_PRIVATE_NETWORK("private-network", "PrivateNetwork"),
    ZONE_PRIVATE_DMZ("private-dmz", "PrivateDMZ"),
    ZONE_PUBLIC_DMZ("public-dmz", "PublicDMZ"),
    ZONE_PUBLIC_INTERNET("public-internet", "Internet");

    private String networkSecurityZone;
    private String networkSecurityZoneCamelCase;

    private NetworkSecurityZoneEnum(String zone, String camelCase) {
        this.networkSecurityZone = zone;
        this.networkSecurityZoneCamelCase = camelCase;
    }

    public String getNetworkSecurityZone(){
        return(this.networkSecurityZone);
    }

    public String getNetworkSecurityZoneCamelCase(){
        return(networkSecurityZoneCamelCase);
    }

    public static NetworkSecurityZoneEnum fromSecurityZoneString(String zoneString){
        for (NetworkSecurityZoneEnum b : NetworkSecurityZoneEnum.values()) {
            if (b.getNetworkSecurityZone().equalsIgnoreCase(zoneString)) {
                return b;
            }
        }
        return null;
    }

    public static NetworkSecurityZoneEnum fromSecurityZoneCamelCaseString(String zoneCamelCaseString){
        for (NetworkSecurityZoneEnum b : NetworkSecurityZoneEnum.values()) {
            if (b.getNetworkSecurityZoneCamelCase().equalsIgnoreCase(zoneCamelCaseString)) {
                return b;
            }
        }
        return null;
    }
}
