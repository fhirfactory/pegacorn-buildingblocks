/*
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter (ACT Health).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets;

public enum GroupMemberActionExtensionEnum {
    MEMBERSHIP_STATUS_JOINED("Pegacorn.FHIR.R1.ReferenceValues.Group.MembershipStatusEnum.JOINED"),
    MEMBERSHIP_STATUS_LEFT("Pegacorn.FHIR.R1.ReferenceValues.Group.MembershipStatusEnum.LEFT"),
    MEMBERSHIP_STATUS_KNOCK("Pegacorn.FHIR.R1.ReferenceValues.Group.MembershipStatusEnum.KNOCK"),
    MEMBERSHIP_STATUS_BANNED("Pegacorn.FHIR.R1.ReferenceValues.Group.MembershipStatusEnum.BANNED"),
    MEMBERSHIP_STATUS_INVITED("Pegacorn.FHIR.R1.ReferenceValues.Group.MembershipStatusEnum.INVITED"),
    MEMBERSHIP_STATUS_NOCHANGE("Pegacorn.FHIR.R1.ReferenceValues.Group.MembershipStatusEnum.NOCHANGE");

    private String membershipStatus;

    private GroupMemberActionExtensionEnum(String newNembershipStatus ){
        this.membershipStatus = newNembershipStatus;
    }

    public String getMembershipStatus(){
        return(this.membershipStatus);
    }
}
