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
package net.fhirfactory.pegacorn.internals.matrix.lingo.activity;

public enum LingoTaskOutcomeElementTypeEnum {
    LINGO_CARE_TEAM_ROOM ("lingo-care-team-room"),
    LINGO_CARE_TEAM_USER ("lingo-care-team-user"),
    LINGO_HEALTHCARE_SERVICE_ROOM ("lingo-healthcare-service-room"),
    LINGO_HEALTHCARE_SERVICE_USER ("lingo-healthcare-service-user"),
    LINGO_CODE_RESPONSE_TEAM_USER ("lingo-code-response-team-user"),
    LINGO_CODE_RESPONSE_TEAM_ROOM ("lingo-code-response-team-room"),
    LINGO_PRACTITIONER_ROOM ("lingo-practitioner-room"),
    LINGO_PRACTITIONER_USER ("lingo-practitioner-user"),
    LINGO_PRACTITIONER_ROLE_ROOM ("lingo-practitioner-role-room"),
    LINGO_PRACTITIONER_ROLE_USER ("lingo-practitioner-role-user");

    private String activityElementType;

    private LingoTaskOutcomeElementTypeEnum(String activityType){
        this.activityElementType = activityType;
    }

    public String getActivityElementType() {
        return activityElementType;
    }
}
