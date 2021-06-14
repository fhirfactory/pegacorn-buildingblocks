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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets;

public enum GroupMemberContextualTypeExtensionEnum {
    CONTEXTUAL_USER_TYPE_GENERAL("communicate-contextual-user-type.general"),
    CONTEXTUAL_USER_TYPE_PATIENT_CENTRIC_TASK_REQUESTER("communicate-contextual-user-type.patient-centric-task-requester"),
    CONTEXTUAL_USER_TYPE_PATIENT_CENTRIC_TASK_FULFILLER("communicate-contextual-user-type.patient-centric-task-fulfiller"),
    CONTEXTUAL_USER_TYPE_CARETEAM_OBSERVER("communicate-contextual-user-type.care-team-observer"),
    CONTEXTUAL_USER_TYPE_CARETEAM_FULFILLER("communicate-contextual-user-type.care-team-fulfiller"),
    CONTEXTUAL_USER_TYPE_PRACTITIONER_ROLE_CLIENT("communicate-contextual-user-type.practitioner-role-client"),
    CONTEXTUAL_USER_TYPE_PRACTITIONER_ROLE_FULFILLER("communicate-contextual-user-type.practitioner-role-fulfiller");

    private String contextualUserType;

    private GroupMemberContextualTypeExtensionEnum(String userType){
        this.contextualUserType = userType;
    }

    public String getContextualUserType() {
        return contextualUserType;
    }

    public static GroupMemberContextualTypeExtensionEnum fromRoomTypeString(String userTypeString){
        for (GroupMemberContextualTypeExtensionEnum b : GroupMemberContextualTypeExtensionEnum.values()) {
            if (b.getContextualUserType().equalsIgnoreCase(userTypeString)) {
                return b;
            }
        }
        return null;
    }

}
