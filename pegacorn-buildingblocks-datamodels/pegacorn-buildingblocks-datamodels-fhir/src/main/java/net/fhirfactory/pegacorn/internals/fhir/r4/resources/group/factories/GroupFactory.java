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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitioner.factories.PractitionerResourceHelpers;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitionerrole.factories.PractitionerRoleResourceHelper;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.SecurityLabelFactory;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GroupFactory {
    private static final Logger LOG = LoggerFactory.getLogger(GroupFactory.class);

    @Inject
    private PractitionerRoleResourceHelper practitionerRoleResourceHelper;

    @Inject
    private PractitionerResourceHelpers practitionerResourceHelpers;

    @Inject
    private SecurityLabelFactory securityLabelFactory;

    @Inject
    private GroupIdentifierFactory identifierFactory;

    public Group newGroup(String groupName, Identifier groupIdentifier, Group.GroupType groupType, CodeableConcept groupMemberKind){
        LOG.debug(".newGroup(): Entry, groupName->{}, groupIdentifier->{}, groupType->{}", groupName, groupIdentifier, groupType);
        Group newGroup = new Group();
        newGroup.addIdentifier(groupIdentifier);
        newGroup.setName(groupName);
        newGroup.setType(groupType);
        newGroup.setCode(groupMemberKind);
        LOG.debug(".newGroup(): Exit");
        return(newGroup);
    }

    public Group newGroup(String groupName, Identifier groupIdentifier, CodeableConcept groupMemberKind){
        LOG.debug(".newGroup(): Entry, groupName->{}, groupIdentifier->{}", groupName, groupIdentifier);
        Group.GroupTypeEnumFactory groupTypeFactory = new Group.GroupTypeEnumFactory();
        Group.GroupType groupType = groupTypeFactory.fromCode(Group.GroupType.PRACTITIONER.toCode());
        Group newGroup = newGroup(groupName, groupIdentifier, groupType, groupMemberKind);
        LOG.debug(".newGroup(): Exit, newGroup->{}", newGroup);
        return(newGroup);
    }

}
