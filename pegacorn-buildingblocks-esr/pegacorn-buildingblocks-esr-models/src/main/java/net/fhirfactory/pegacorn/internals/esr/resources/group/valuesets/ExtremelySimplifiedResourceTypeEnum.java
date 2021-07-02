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
package net.fhirfactory.pegacorn.internals.esr.resources.valuesets;

public enum ExtremelySimplifiedResourceTypeEnum {
    ESR_CARE_TEAM("esr.resource-type.care-team"),
    ESR_GROUP("esr.resource-type.group"),
    ESR_HEALTHCARE_SERVICE("esr.resource-type.healthcare-service"),
    ESR_LOCATION("esr.resource-type.location"),
    ESR_MATRIX_ROOM("esr.resource-type.matrix-room"),
    ESR_MATRIX_USER("esr.resource-type.matrix-user"),
    ESR_ORGANIZATION("esr.resource-type.organization"),
    ESR_PATIENT("esr.resource-type.patient"),
    ESR_PERSON("esr.resource-type.person"),
    ESR_PRACTITIONER("esr.resource-type.practitioner"),
    ESR_PRACTITIONER_ROLE("esr.resource-type.practitioner-role"),
    ESR_ROLE_CATEGORY("esr.resource-type.role-category"),
    ESR_ROLE("esr.resource-type.role");

    private String resourceType;

    private ExtremelySimplifiedResourceTypeEnum(String resourceType){
        this.resourceType = resourceType;
    }

    public String getResourceType(){
        return(this.resourceType);
    }

    public static ExtremelySimplifiedResourceTypeEnum fromString(String text) {
        for (ExtremelySimplifiedResourceTypeEnum b : ExtremelySimplifiedResourceTypeEnum.values()) {
            if (b.resourceType.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
