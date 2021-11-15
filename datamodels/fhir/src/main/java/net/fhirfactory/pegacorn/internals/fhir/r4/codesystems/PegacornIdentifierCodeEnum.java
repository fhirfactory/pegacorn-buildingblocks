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
    IDENTIFIER_CODE_AUDIT_EVENT("AuditEvent Identifier", "pegacorn.fhir.identifier.audit-event", "FHIR::AuditEvent (Generated Content from Pegacorn Internal Systems)"),
    IDENTIFIER_CODE_COMMUNICATE_ROOM_ID("Communicate RoomID", "pegacorn.fhir.identifier.group.communicate_room_id", "FHIR::Group (Group Identifier representing a Room within Communicate/Matrix Server)"),
    IDENTIFIER_CODE_FHIR_ENDPOINT_SYSTEM("Endpoint Common Name Identifier", "pegacorn.fhir.identifier.endpoint.common-name", "FHIR::Endpoint (Common, but Unique, Name for the Endpoint)"),
    IDENTIFIER_CODE_SOURCE_OF_TRUTH_RECORD_ID("Endoint Source of Truth Identifier", "pegacorn.fhir.identifier.endpoint.sot_rid", "FHIR::Endpoint (Source-of-Truth Endpoint for FHIR Data)"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_SHORT_NAME("PractitionerRole Short Name", "pegacorn.fhir.identifier.practitioner_role.role-short-name", "FHIR::PractitionerRole (Short, but Unique, Name for a PractitionerRole)"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_LONG_NAME("PractitionerRole Long Name", "pegacorn.fhir.identifier.practitioner_role.role-long-name", "FHIR::PractitionerRole (Long, and Unique, Name for a PractitionerRole"),
    IDENTIFIER_CODE_PRACTITIONER_EMAIL("Practitioner Email", "pegacorn.fhir.identifier.practitioner_role.email", "FHIR::PractitionerRole (FHIR::PractitionerRole (Unique Internal Email Address for the PractitionerRole)"),
    IDENTIFIER_CODE_BUSINESS_UNIT("Business Unit", "pegacorn.fhir.identifier.organization.business-unit", "FHIR::Organization (A unique identifier for a Business Unit within the Organization)"),
    IDENTIFIER_CODE_CONTAINMENT_BASED_LOCATION("Containment Based Location Id", "pegacorn.fhir.identifier.location.containment-based-location-id", "FHIR::Location (A containment based identifier for a Location)"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_GROUP("Practitioner Role Group Name", "pegacorn.fhir.identifier.group.containing-practitioner-roles", "FHIR::Group (A Unique Identifier for a Grouping of PractitionerRoles used for Collaboration/Messaging Services)"),
    IDENTIFIER_CODE_PRACTITIONER_GROUP("Practitioner Group Name", "pegacorn.fhir.identifier.group.containing-practitioners", "FHIR::Group (A Unique Identifier for a Grouping of Practitioners used for Collaboration/Messaging Services)"),
    IDENTIFIER_CODE_HL7V2_COMMUNICATION_CONTAINER("Communication HL7v2x Message Id", "pegacorn.fhir.identifier.communication.HL7v2-container", "FHIR::Communication (A Unique Identifier for Communication Resources used to Transport HL7 version 2.x Messages)"),
    IDENTIFIER_CODE_ACTIONABLE_TASK("Actionable Task Id", "pegacorn.fhir.identifier.task.actionable_task", "FHIR::Task (A Unique Identifier for a Task representing a piece of work for the Pegacorn System)"),
    IDENTIFIER_CODE_FULFILLMENT_TASK("Fulfillment Task Id", "pegacorn.fhir.identifier.task.fulfillment_task", "FHIR::Task (A Unique Identifier for a Task representing the execution of a piece of work within the Pegacorn System)"),
    IDENTIFIER_CODE_SOFTWARE_COMPONENT("Software Component Id", "pegacorn.fhir.identifier.device.software_component", "FHIR:Device (A Unique Identifier for a Software Component within the Pegacorn System");

    private String token;
    private String displayName;
    private String displayText;

    private PegacornIdentifierCodeEnum(String name, String code, String text){
        this.displayName = name;
        this.token = code;
        this.displayText = text;
    }

    public String getToken(){return(this.token);}

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getDisplayText(){
        return(this.displayText);
    }
}
