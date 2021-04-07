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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitioner.PractitionerResourceHelpers;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitionerrole.PractitionerRoleResourceHelper;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.InformationCompartmentSecurityCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.InformationConfidentialitySecurityCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.SecurityLabelFactory;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GroupFactory {
    private static final Logger LOG = LoggerFactory.getLogger(GroupFactory.class);

    private static String PRACTITIONER_GROUP_NAME_SUFFIX = "- Practitioner Group";

    @Inject
    private GroupResourceHelpers groupResourceHelpers;

    @Inject
    private PractitionerRoleResourceHelper practitionerRoleResourceHelper;

    @Inject
    private PractitionerResourceHelpers practitionerResourceHelpers;

    @Inject
    private SecurityLabelFactory securityLabelFactory;

    public Group buildGroup(String groupName, Identifier groupIdentifier, Group.GroupType groupType){
        LOG.debug(".buildGroup(): Entry");
        Group newGroup = new Group();
        newGroup.addIdentifier(groupIdentifier);
        newGroup.setName(groupName);
        newGroup.setType(groupType);
        newGroup.setActive(true);
        LOG.debug(".buildGroup(): Exit");
        return(newGroup);
    }

    public Group buildPractitionerGroupForPractitionerRole(String practitionerRoleShortName){
        LOG.debug(".buildPractitionerGroupForPractitionerRole(): Entry");
        Identifier practitionerGroupIdentifier = groupResourceHelpers.buildPractitionerGroupIdentifier(practitionerRoleShortName);
        String groupName = practitionerRoleShortName + PRACTITIONER_GROUP_NAME_SUFFIX;
        Group practitionerGroup = buildGroup(groupName, practitionerGroupIdentifier, Group.GroupType.PRACTITIONER);
        Reference practitionerRoleReference = practitionerRoleResourceHelper.buildPractitionerRoleReference(practitionerRoleShortName);
        practitionerGroup.setManagingEntity(practitionerRoleReference);
        Coding confidentialitySecurityLabel = securityLabelFactory.constructConfidentialitySecurityLabel(InformationConfidentialitySecurityCodeEnum.L);
        Coding compartmentSecurityLabel = securityLabelFactory.constructSecurityLabelCompartment(InformationCompartmentSecurityCodeEnum.HRCOMPT);
        Meta groupMetadata = new Meta();
        groupMetadata.addSecurity(confidentialitySecurityLabel);
        groupMetadata.addSecurity(compartmentSecurityLabel);
        practitionerGroup.setMeta(groupMetadata);
        CodeableConcept groupCode = groupResourceHelpers.buildGroupCode("PractitionerSet", "The set of Practitioners fulfilling a PractitionerRole");
        practitionerGroup.setCode(groupCode);
        LOG.debug(".buildPractitionerGroupForPractitionerRole(): Exit");
        return(practitionerGroup);
    }

    public Group buildPractitionerRoleGroupForPractitioner(String practitionerEmail){
        LOG.debug(".buildPractitionerRoleGroupForPractitioner(): Entry");
        Identifier practitionerGroupIdentifier = groupResourceHelpers.buildPractitionerGroupIdentifier(practitionerEmail);
        Group practitionerRoleGroup = buildGroup(practitionerEmail, practitionerGroupIdentifier, Group.GroupType.PRACTITIONER);
        Reference practitionerReference = practitionerResourceHelpers.buildPractitionerReferenceUsingEmail(practitionerEmail);
        practitionerRoleGroup.setManagingEntity(practitionerReference);
        Coding confidentialitySecurityLabel = securityLabelFactory.constructConfidentialitySecurityLabel(InformationConfidentialitySecurityCodeEnum.L);
        Coding compartmentSecurityLabel = securityLabelFactory.constructSecurityLabelCompartment(InformationCompartmentSecurityCodeEnum.HRCOMPT);
        Meta groupMetadata = new Meta();
        groupMetadata.addSecurity(confidentialitySecurityLabel);
        groupMetadata.addSecurity(compartmentSecurityLabel);
        practitionerRoleGroup.setMeta(groupMetadata);
        CodeableConcept groupCode = groupResourceHelpers.buildGroupCode("PractitionerRoleSet", "The set of PractitionerRoles fulfilled by a Practitioner");
        practitionerRoleGroup.setCode(groupCode);
        LOG.debug(".buildPractitionerRoleGroupForPractitioner(): Exit");
        return(practitionerRoleGroup);
    }
}
