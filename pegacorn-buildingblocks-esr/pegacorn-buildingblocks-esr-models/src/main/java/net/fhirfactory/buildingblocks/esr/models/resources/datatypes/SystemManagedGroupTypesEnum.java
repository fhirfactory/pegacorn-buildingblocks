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
package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

public enum SystemManagedGroupTypesEnum {
    PRACTITIONEROLE_MAP_PRACTITIONER_GROUP("SystemManaged-PractitionerRoleMap-PractitionerGroup"),
    PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP("SystemManaged-PractitionerRoleMap-PractitionerRoleGroup"),
    GENERAL("SystemManaged-GeneralGroup");

    private String typeCode;

    private SystemManagedGroupTypesEnum(String typeValue){
        this.typeCode = typeValue;
    }

    public String getTypeCode(){
        return(typeCode);
    }

    public static SystemManagedGroupTypesEnum fromTypeCode(String code){
        if(code.contentEquals("SystemManaged-PractitionerRoleMap-PractitionerGroup")){
            return(SystemManagedGroupTypesEnum.PRACTITIONEROLE_MAP_PRACTITIONER_GROUP);
        }
        if(code.contentEquals("SystemManaged-PractitionerRoleMap-PractitionerRoleGroup")){
            return(SystemManagedGroupTypesEnum.PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP);
        }
        return(GENERAL);
    }
}
