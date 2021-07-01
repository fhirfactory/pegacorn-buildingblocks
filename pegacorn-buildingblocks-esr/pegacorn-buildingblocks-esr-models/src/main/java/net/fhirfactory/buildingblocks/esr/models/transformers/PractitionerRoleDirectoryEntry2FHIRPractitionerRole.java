/*
 * Copyright (c) 2021 Mark Hunter
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
package net.fhirfactory.buildingblocks.esr.models.transformers;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.GroupFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.location.LocationResourceHelper;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.organization.OrganizationResourceHelpers;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitionerrole.PractitionerRoleFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitionerrole.RoleFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.InformationConfidentialitySecurityCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.SecurityLabelFactory;

@ApplicationScoped
public class PractitionerRoleDirectoryEntry2FHIRPractitionerRole {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleDirectoryEntry2FHIRPractitionerRole.class);

    @Inject
    private PractitionerRoleFactory prFactory;

    @Inject
    private OrganizationResourceHelpers organizationResourceHelpers;

    @Inject
    private LocationResourceHelper locationResourceHelper;

    @Inject
    private RoleFactory roleFactory;

    @Inject
    private SecurityLabelFactory securityLabelFactory;

    @Inject
    private GroupFactory groupFactory;

    @Inject
    private DataTypeTransformers dataTypeTransformers;

    public PractitionerRoleDirectoryEntry2FHIRPractitionerRole() {
    }

    /**
     *
     * @param practitionerRoleDE
     * @return
     */
    public org.hl7.fhir.r4.model.PractitionerRole convertToPractitionerRole(PractitionerRoleESR practitionerRoleDE){
        String prCode = practitionerRoleDE.getIdentifierWithType(IdentifierType.SHORT_NAME).getValue();
        String prName = practitionerRoleDE.getIdentifierWithType(IdentifierType.LONG_NAME).getValue();
        ArrayList<CodeableConcept> roleSet = new ArrayList<>();
        CodeableConcept role = roleFactory.buildRoleWithCategory(practitionerRoleDE.getPrimaryRoleCategoryID(), practitionerRoleDE.getPrimaryRoleID());
        roleSet.add(role);
        org.hl7.fhir.r4.model.PractitionerRole practitionerRole = prFactory.buildPractitionerRole(prCode, prName, roleSet);
        List<ContactPoint> contactPoints = dataTypeTransformers.createContactPoints(practitionerRoleDE.getContactPoints());
        practitionerRole.getTelecom().addAll(contactPoints);
        Reference organizationReference = organizationResourceHelpers.buildOrganizationReference(practitionerRoleDE.getPrimaryOrganizationID(), Identifier.IdentifierUse.USUAL);
        practitionerRole.setOrganization(organizationReference);
        Reference locationReference = locationResourceHelper.buildLocationReference(practitionerRoleDE.getPrimaryLocationID(), Identifier.IdentifierUse.USUAL);
        practitionerRole.addLocation(locationReference);
        Coding confidentialitySecurityLabel = securityLabelFactory.constructConfidentialitySecurityLabel(InformationConfidentialitySecurityCodeEnum.U);
        Meta prMetadata = new Meta();
        prMetadata.addSecurity(confidentialitySecurityLabel);
        practitionerRole.setMeta(prMetadata);
        return(practitionerRole);
    }
}
