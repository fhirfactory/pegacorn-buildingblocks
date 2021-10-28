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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets;

import org.hl7.fhir.r4.model.Coding;

public enum AuditEventSourceTypeEnum {
    HL7_USER_DEVICE("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "1", "User Device", false),
    HL7_DATA_INTERFACE("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "2", "Data Interface", false),
    HL7_WEB_SERVER("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "3", "Web Server", false),
    HL7_APPLICATION_SERVER("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "4", "Application Server", false),
    HL7_DATABASE_SERVER("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "5", "Database Server", false),
    HL7_SECURITY_SERVER("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "6", "Security Server", false),
    HL7_NETWORK_DEVICE("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "7", "Network Device", false),
    HL7_NETWORK_ROUTER("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "8", "Network Router", false),
    HL7_OTHER("http://terminology.hl7.org/CodeSystem/security-source-type", "4.0.1", "9", "Other", false);

    private String system;
    private String version;
    private String code;
    private String display;
    private boolean userSelected;

    private AuditEventSourceTypeEnum(String system, String version, String code, String display, boolean userSelected){
        this.system = system;
        this.version = version;
        this.code = code;
        this.display = display;
        this.userSelected = userSelected;
    }

    public Coding getSourceTypeCoding(){
        Coding coding = new Coding();
        coding.setSystem(this.system);
        coding.setVersion(this.version);
        coding.setCode(this.code);
        coding.setDisplay(this.display);
        coding.setUserSelected(this.userSelected);
        return(coding);
    }
}
