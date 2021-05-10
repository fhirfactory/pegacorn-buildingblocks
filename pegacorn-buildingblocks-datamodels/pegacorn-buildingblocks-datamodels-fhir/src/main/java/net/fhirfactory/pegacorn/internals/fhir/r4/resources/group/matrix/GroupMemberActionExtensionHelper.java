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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.matrix;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;

public class GroupMemberActionExtensionHelper {
    private static UriType MEMBERSHIP_STATUS_MEANING = new UriType("http://www.fhirfactory.net/pegacorn/FHIR/R4/Group/group_membership_status");

    public boolean hasMembershipStatusExention(Group.GroupMemberComponent membership)
    {
        if (membership.hasExtension(this.MEMBERSHIP_STATUS_MEANING.asStringValue())) {
            return (true);
        }
        return (false);
    }

    public GroupMemberActionExtensionEnum extractGroupMemberStatusExtension(Group.GroupMemberComponent membership)
            throws GroupExtensionSetException
    {
        if (!membership.hasExtension(this.MEMBERSHIP_STATUS_MEANING.asStringValue())) {
            throw (new GroupExtensionSetException("extractGroupMemberStatusExtension(): Group::member does not contain the GroupMemberStatus extension"));
        }
        Extension extractedStatusExtension = membership.getExtensionByUrl(MEMBERSHIP_STATUS_MEANING.asStringValue());
        if( !(extractedStatusExtension.getValue() instanceof StringType)){
            throw (new GroupExtensionSetException("extractGroupMemberStatusExtension(): Group::member contains the GroupMemberStatus extension value type"));
        }
        StringType extractedStatusStringType = (StringType) (extractedStatusExtension.getValue());
        GroupMemberActionExtensionEnum statusEnum = GroupMemberActionExtensionEnum.valueOf(extractedStatusStringType.getValue());
        return (statusEnum);
    }

    public void injectGroupMemberStatusExtension(Group.GroupMemberComponent membership, GroupMemberActionExtensionEnum newStatus){
        if (membership.hasExtension(this.MEMBERSHIP_STATUS_MEANING.asStringValue())) {
            membership.removeExtension(this.MEMBERSHIP_STATUS_MEANING.asStringValue());
        }
        Extension newStatusExtension = new Extension();
        newStatusExtension.setUrlElement(MEMBERSHIP_STATUS_MEANING);
        newStatusExtension.setValue(new StringType(newStatus.getMembershipStatus()));
    }
}
