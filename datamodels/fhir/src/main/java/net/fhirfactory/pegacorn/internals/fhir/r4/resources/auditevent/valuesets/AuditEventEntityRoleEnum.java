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

public enum AuditEventEntityRoleEnum {
    HL7_PATIENT("http://terminology.hl7.org/CodeSystem/object-role", "4.0.1", "1", "Patient", false),
    HL7_LOCATION("http://terminology.hl7.org/CodeSystem/object-role", "4.0.1", "2", "Location", false),
    HL7_REPORT("http://terminology.hl7.org/CodeSystem/object-role", "4.0.1", "3", "Report", false),
    HL7_DOMAIN_RESOURCE("http://terminology.hl7.org/CodeSystem/object-role", "4.0.1", "4", "Domain Resource", false),
    HL7_MASTER_FILE("http://terminology.hl7.org/CodeSystem/object-role", "4.0.1", "5", "Master File", false),
    HL7_USER("http://terminology.hl7.org/CodeSystem/object-role", "4.0.1", "6", "User", false),
    HL7_JOB("http://terminology.hl7.org/CodeSystem/object-role", "4.0.1", "20", "Job", false),
    HL7_JOB_STREAM("http://terminology.hl7.org/CodeSystem/object-role", "4.0.1", "21", "Job Stream", false);
    // TODO Add more values from https://www.hl7.org/fhir/valueset-object-role.html

    private String system;
    private String version;
    private String code;
    private String display;
    private boolean userSelected;

    private AuditEventEntityRoleEnum(String system, String version, String code, String display, boolean userSelected){
        this.system = system;
        this.version = version;
        this.code = code;
        this.display = display;
        this.userSelected = userSelected;
    }

    public Coding getEntityRoleCoding(){
        Coding coding = new Coding();
        coding.setSystem(this.system);
        coding.setVersion(this.version);
        coding.setCode(this.code);
        coding.setDisplay(this.display);
        coding.setUserSelected(this.userSelected);
        return(coding);
    }
}
