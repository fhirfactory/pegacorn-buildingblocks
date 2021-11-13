/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.codesystems;

public enum PegacornIdentifierCodeEnum {
    IDENTIFIER_CODE_AUDIT_EVENT("AuditEvent Identifier", "petasos.fhir.identifier.audit-event"),
    IDENTIFIER_CODE_COMMUNICATE_ROOM_ID("Communicate RoomID", "petasos.fhir.identifier.communicate_room_id"),
    IDENTIFIER_CODE_FHIR_ENDPOINT_SYSTEM("Endpoint Common Name Identifier", "petasos.fhir.identifier.endpoint.common-name"),
    IDENTIFIER_CODE_SOURCE_OF_TRUTH_RECORD_ID("Endoint Source of Truth Identifier", "petasos.fhir.identifier.endpoint.sot_rid"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_SHORT_NAME("PractitionerRole Short Name", "petasos.fhir.identifier.practitioner_role.role-short-name"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_LONG_NAME("PractitionerRole Long Name", "petasos.fhir.identifier.practitioner_role.role-long-name"),
    IDENTIFIER_CODE_PRACTITIONER_EMAIL("Practitioner Email", "petasos.fhir.identifier.practitioner_role.email"),
    IDENTIFIER_CODE_BUSINESS_UNIT("Business Unit", "petasos.fhir.identifier.organization.business-unit"),
    IDENTIFIER_CODE_CONTAINMENT_BASED_LOCATION("Containment Based Location Id", "petasos.fhir.identifier.location.containment-based-location-id"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_GROUP("Practitioner Role Group Name", "petasos.fhir.identifier.group.containing-practitioner-roles"),
    IDENTIFIER_CODE_PRACTITIONER_GROUP("Practitioner Group Name", "petasos.fhir.identifier.group.containing-practitioners"),
    IDENTIFIER_CODE_HL7V2_COMMUNICATION_CONTAINER("Communication HL7v2x Message Id", "petasos.fhir.identifier.communication.HL7v2-container"),
    IDENTIFIER_CODE_ACTIONABLE_TASK("Actionable Task Id", "petasos.fhir.identifier.task.actionable_task"),
    IDENTIFIER_CODE_FULFILLMENT_TASK("Fulfillment Task Id", "petasos.fhir.identifier.task.fulfillment_task");

    private String token;
    private String displayName;

    private PegacornIdentifierCodeEnum(String name, String code){
        this.displayName = name;
        this.token = code;
    }

    public String getToken(){return(this.token);}
}
