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
package net.fhirfactory.pegacorn.core.model.capabilities.valuesets;

public enum CapabilityProviderTitlesEnum {
    CAPABILITY_INFORMATION_MANAGEMENT_IT_OPS("IT-Ops-IM", "pegacorn.capability.im.itops"),
    CAPABILITY_INFORMATION_MANAGEMENT_AUDIT_EVENTS("AuditEvent-IM", "pegacorn.capability.im.audit-event"),
    CAPABILITY_DATA_MANAGEMENT_AUDIT_EVENTS("AuditEvent-DM","pegacorn.capability.dm.audit-event"),
    CAPABILITY_SERVICE_PROVIDER_A19QRY("A19Query-Task", "pegacorn.capability.task.a19query");

    private String displayName;
    private String token;

    private CapabilityProviderTitlesEnum(String name, String token){
        this.displayName = name;
        this.token = token;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getToken(){
        return(this.token);
    }
}
