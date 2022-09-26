/*
 * The MIT License
 *
 * Copyright 2022 Mark A. Hunter.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.core.model.capabilities.definition.datatypes;

public enum CapabilityKeyEnum {
    CAPABILITY_BUSINESS_DOMAIN("Domain", "dricats.capability.descriptor.business_domain", "Business Domain"),
    CAPABILITY_BUSINESS_SECTOR("Sector", "dricats.capability.descriptor.business_sector", "Business Sector"),
    CAPABILITY_BUSINESS_AREA("Area", "dricats.capability.descriptor.business_area", "Business Area"),
    CAPABILITY_LEVEL_1("Level1", "dricats.capability.descriptor.level_1_capability", "Level 1 Capability"),
    CAPABILITY_LEVEL_2("Level2", "dricats.capability.descriptor.level_2_capability", "Level 2 Capability"),
    CAPABILITY_LEVEL_3("Level3", "dricats.capability.descriptor.level_3_capability", "Level 3 Capability"),
    CAPABILITY_SERVICE("Service", "dricats.capability.descriptor.service", "Business Service"),
    CAPABILITY_FUNCTION("Function", "dricats.capability.descriptor.function", "Business Function"),
    CAPABILITY_VERSION("Version", "dricats.capability.descriptor.version", "Business Capability Version");

    private String token;
    private String displayName;
    private String displayText;

    private CapabilityKeyEnum(String displayName, String token, String text){
        this.token = token;
        this.displayName = displayName;
        this.displayText = text;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getDisplayText(){
        return(this.displayText);
    }

    public static CapabilityKeyEnum fromToken(String keyString){
        for (CapabilityKeyEnum b : CapabilityKeyEnum.values()) {
            if (b.getToken().equalsIgnoreCase(keyString)) {
                return b;
            }
        }
        return null;
    }

    public static CapabilityKeyEnum fromDisplayName(String keyString){
        for (CapabilityKeyEnum b : CapabilityKeyEnum.values()) {
            if (b.getDisplayName().equalsIgnoreCase(keyString)) {
                return b;
            }
        }
        return null;
    }
}
