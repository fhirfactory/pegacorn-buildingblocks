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
package net.fhirfactory.buildingblocks.esr.resources.datatypes;

public enum SystemManagedGroupTypesEnum {
    PRACTITIONERS_FULFILLING_PRACTITIONER_ROLE_GROUP("SystemManaged-PractitionerRoleMap-PractitionerGroup",""),
    PRACTITONER_ROLES_FULFILLED_BY_PRACTITIONER_GROUP("SystemManaged-PractitionerRoleMap-PractitionerRoleGroup",""),
    GENERAL("SystemManaged-GeneralGroup",""),
	CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP("SystemManaged-CareTeams-containing-PractitionerRole-Group","CareTeams-containing-PractitionerRole-"),
	PRACTITIONER_ROLES_IN_CARE_TEAM_GROUP("SystemManaged-PractitionerRoles_in_CareTeam-Group","PractitionerRoles-in-Care-Team-");

    private String typeCode;
    private String groupPrefix;

    private SystemManagedGroupTypesEnum(String typeValue, String groupPrefix){
        this.typeCode = typeValue;
        this.groupPrefix = groupPrefix;
    }

    public String getTypeCode(){
        return(typeCode);
    }
    
    public String getGroupPrefix() {
    	return groupPrefix;
    }

    public static SystemManagedGroupTypesEnum fromTypeCode(String code){
        if(code.contentEquals("SystemManaged-PractitionerRoleMap-PractitionerGroup")){
            return(SystemManagedGroupTypesEnum.PRACTITIONERS_FULFILLING_PRACTITIONER_ROLE_GROUP);
        }
        
        if(code.contentEquals("SystemManaged-PractitionerRoleMap-PractitionerRoleGroup")){
            return(SystemManagedGroupTypesEnum.PRACTITONER_ROLES_FULFILLED_BY_PRACTITIONER_GROUP);
        }
        
        if(code.contentEquals("SystemManaged-CareTeams-containing-PractitionerRole-Group")){
            return(SystemManagedGroupTypesEnum.CARE_TEAMS_CONTAINING_PRACTITIONER_ROLE_GROUP);
        }
        
        if(code.contentEquals("SystemManaged-PractitionerRoles_in_CareTeam-Group")){
            return(SystemManagedGroupTypesEnum.PRACTITIONER_ROLES_IN_CARE_TEAM_GROUP);
        }
        
        return(GENERAL);
    }
}
