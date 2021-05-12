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
package net.fhirfactory.pegacorn.internals.esr.resources.common;

public enum ExtremelySimplifiedResourceTypeEnum {
    ESR_CARE_TEAM("care-team-extremely-simplified-resource"),
    ESR_GROUP("group-extremely-simplified-resource"),
    ESR_HEALTHCARE_SERVICE("healthcare-service-extremely-simplified-resource"),
    ESR_LOCATION("location-extremely-simplified-resource"),
    ESR_MATRIX_ROOM("matrix-room-extremely-simplified-resource"),
    ESR_ORGANIZATION("organization-extremely-simplified-resource"),
    ESR_PATIENT("patient-extremely-simplified-resource"),
    ESR_PERSON("person-extremely-simplified-resource"),
    ESR_PRACTITIONER("practitioner-extremely-simplified-resource"),
    ESR_PRACTITIONER_ROLE("practitioner-role-extremely-simplified-resource"),
    ESR_ROLE_CATEGORY("role-category-extremely-simplified-resource"),
    ESR_ROLE("role-extremely-simplified-resource");

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
