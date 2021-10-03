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
package net.fhirfactory.pegacorn.internals.communicate.activity;

public enum CommunicateActivityOutcomeElementTypeEnum {
    COMMUNICATE_CARE_TEAM_ROOM ("communicate-care-team-room"),
    COMMUNICATE_CARE_TEAM_USER ("communicate-care-team-user"),
    COMMUNICATE_HEALTHCARE_SERVICE_ROOM ("communicate-healthcare-service-room"),
    COMMUNICATE_HEALTHCARE_SERVICE_USER ("communicate-healthcare-service-user"),
    COMMUNICATE_CODE_RESPONSE_TEAM_USER ("communicate-code-response-team-user"),
    COMMUNICATE_CODE_RESPONSE_TEAM_ROOM ("communicate-code-response-team-room"),
    COMMUNICATE_PRACTITIONER_ROOM ("communicate-practitioner-room"),
    COMMUNICATE_PRACTITIONER_USER ("communicate-practitioner-user"),
    COMMUNICATE_PRACTITIONER_ROLE_ROOM ("communicate-practitioner-role-room"),
    COMMUNICATE_PRACTITIONER_ROLE_USER ("communicate-practitioner-role-user");

    private String activityElementType;

    private CommunicateActivityOutcomeElementTypeEnum(String activityType){
        this.activityElementType = activityType;
    }

    public String getActivityElementType() {
        return activityElementType;
    }
}
