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
    IDENTIFIER_CODE_COMMUNICATE_ROOM_ID("idcode:group.communicate-room-id"),
    IDENTIFIER_CODE_FHIR_ENDPOINT_SYSTEM("idcode:endpoint.common-name"),
    IDENTIFIER_CODE_SOURCE_OF_TRUTH_RECORD_ID("idcode:sot.rid"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_SHORT_NAME("idcode:practitioner.role-short-name"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_LONG_NAME("idcode:practitioner.role-long-name"),
    IDENTIFIER_CODE_PRACTITIONER_EMAIL("idcode:practitioner.email"),
    IDENTIFIER_CODE_BUSINESS_UNIT("idcode:organization.business-unit"),
    IDENTIFIER_CODE_CONTAINMENT_BASED_LOCATION("idcode:location.containment-based-location-id"),
    IDENTIFIER_CODE_PRACTITIONER_ROLE_GROUP("idcode:group.containing-practitioner-roles"),
    IDENTIFIER_CODE_PRACTITIONER_GROUP("idcode:group.containing-practitioners"),
    IDENTIFIER_CODE_HL7V2_COMMUNICATION_CONTAINER("idcode:communication.HL7v2-container");

    private String identifierCode;
    private PegacornIdentifierCodeEnum(String code){ this.identifierCode = code;}

    public String getIdentifierCode(){return(this.identifierCode);}
}
