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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.extensions;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets.GroupMemberContextualTypeExtensionEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets.GroupMemberActionExtensionEnum;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GroupMemberTypeExtensionEnricher {
    private static final Logger LOG = LoggerFactory.getLogger(GroupMemberTypeExtensionEnricher.class);

    private static UriType MEMBERSHIP_TYPE_MEANING = new UriType("http://www.fhirfactory.net/pegacorn/FHIR/R4/Group/group_membership_type");

    public void injectMemberType(Group.GroupMemberComponent membership, GroupMemberContextualTypeExtensionEnum userType){
        LOG.debug(".injectMemberType(): Entry, membership->{}, userType->{}", membership, userType);
        if (membership.hasExtension(this.MEMBERSHIP_TYPE_MEANING.asStringValue())) {
            membership.removeExtension(this.MEMBERSHIP_TYPE_MEANING.asStringValue());
        }
        Extension newStatusExtension = new Extension();
        newStatusExtension.setUrlElement(MEMBERSHIP_TYPE_MEANING);
        newStatusExtension.setValue(new StringType(userType.getContextualUserType()));
        membership.addExtension(newStatusExtension);
        LOG.debug(".injectMemberType(): Exit, membership->{}", membership);
    }

    public GroupMemberActionExtensionEnum extractGroupMemberTypeExtension(Group.GroupMemberComponent membership) {
        LOG.debug(".extractGroupMemberTypeExtension(): Entry, membership->{}", membership);
        if (!membership.hasExtension(this.MEMBERSHIP_TYPE_MEANING.asStringValue())) {
            LOG.debug(".extractGroupMemberTypeExtension(): Group::member does not contain the GroupMemberType extension");
            return(null);
        }
        Extension extractedStatusExtension = membership.getExtensionByUrl(MEMBERSHIP_TYPE_MEANING.asStringValue());
        if( !(extractedStatusExtension.getValue() instanceof StringType)){
            LOG.debug(".extractGroupMemberTypeExtension(): Group::member contains the GroupMemberType extension value type");
            return(null);
        }
        StringType extractedStatusStringType = (StringType) (extractedStatusExtension.getValue());
        GroupMemberActionExtensionEnum statusEnum = GroupMemberActionExtensionEnum.valueOf(extractedStatusStringType.getValue());
        LOG.debug(".extractGroupMemberTypeExtension(): Exit, statusEnum->{}", statusEnum);
        return (statusEnum);
    }
}
