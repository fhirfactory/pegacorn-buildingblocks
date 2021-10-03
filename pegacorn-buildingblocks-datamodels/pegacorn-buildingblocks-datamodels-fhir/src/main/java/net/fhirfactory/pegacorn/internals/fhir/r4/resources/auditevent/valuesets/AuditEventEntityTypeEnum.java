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

public enum AuditEventEntityTypeEnum {
    HL7_PERSON("http://terminology.hl7.org/CodeSystem/audit-entity-type", "4.0.1", "1", "Person", false),
    HL7_SYSTEM_OBJECT("http://terminology.hl7.org/CodeSystem/audit-entity-type", "4.0.1", "2", "System Object", false),
    HL7_ORGANIZATION("http://terminology.hl7.org/CodeSystem/audit-entity-type", "4.0.1", "3", "Organization", false),
    HL7_OTHER("http://terminology.hl7.org/CodeSystem/audit-entity-type", "4.0.1", "4", "Other", false),
    FHIR_BUNDLE("http://hl7.org/fhir/resource-types", "4.0.1", "Bundle", "Bundle", false),
    FHIR_COMMUNICATION("http://hl7.org/fhir/resource-types", "4.0.1", "Communication", "Communication", false),
    FHIR_DEVICE("http://hl7.org/fhir/resource-types", "4.0.1", "Device", "Device", false),
    FHIR_ENDPOINT("http://hl7.org/fhir/resource-types", "4.0.1", "Endpoint", "Endpoint", false),
    FHIR_TASK("http://hl7.org/fhir/resource-types", "4.0.1", "Task", "Task", false),
    // TODO Add more from https://www.hl7.org/fhir/valueset-audit-entity-type.html
    PEGACORN_MLLP_MSG("http://net.fhirfactory.pegacorn/fhir/audit-resource-types", "1.0.0", "MLLP-MSG", "MLLP Message", true);


    private String system;
    private String version;
    private String code;
    private String display;
    private boolean userSelected;

    private AuditEventEntityTypeEnum(String system, String version, String code, String display, boolean userSelected){
        this.system = system;
        this.version = version;
        this.code = code;
        this.display = display;
        this.userSelected = userSelected;
    }

    public Coding getEntityTypeCoding(){
        Coding coding = new Coding();
        coding.setSystem(this.system);
        coding.setVersion(this.version);
        coding.setCode(this.code);
        coding.setDisplay(this.display);
        coding.setUserSelected(this.userSelected);
        return(coding);
    }
}
