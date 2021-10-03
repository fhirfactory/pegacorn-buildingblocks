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

public enum AuditEventTypeEnum {
    DICOM_APPLICATION_ACTIVITY("http://dicom.nema.org/resources/ontology/DCM", "4.0.1", "110100", "Application Activity", false),
    DICOM_AUDIT_LOG_USED("http://dicom.nema.org/resources/ontology/DCM", "4.0.1","110100", "Audit Log Used", false ),
    HL7_TERMINOLOGY_REST("http://terminology.hl7.org/CodeSystem/audit-event-type", "4.0.1", "rest", "RESTful Operation", false),
    HL7_TERMINOLOGY_TRANSFORM("http://terminology.hl7.org/CodeSystem/audit-event-type", "4.0.1", "transform", "Transform/Translate Record Lifecycle Event", false),
    HL7_TERMINOLOGY_TRANSMIT("http://terminology.hl7.org/CodeSystem/audit-event-type", "4.0.1", "transmit", "Transmit Record Lifecycle Event", false);
    // TODO Add more entries from https://www.hl7.org/fhir/valueset-audit-event-type.html

    private String system;
    private String version;
    private String code;
    private String display;
    private boolean userSelected;

    private AuditEventTypeEnum(String system, String version, String code, String display, boolean userSelected){
        this.system = system;
        this.version = version;
        this.code = code;
        this.display = display;
        this.userSelected = userSelected;
    }

    public Coding getTypeCoding(){
        Coding coding = new Coding();
        coding.setSystem(this.system);
        coding.setVersion(this.version);
        coding.setCode(this.code);
        coding.setDisplay(this.display);
        coding.setUserSelected(this.userSelected);
        return(coding);
    }
}
