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

import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems.DeploymentInstanceDetailInterface;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitioner.PractitionerResourceHelpers;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitionerrole.PractitionerRoleResourceHelper;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class GroupResourceHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(GroupResourceHelpers.class);

    private static String PRACTITIONER_GROUP_ID = "PractitionerGroupFor:";
    private static String PRACTITIONER_ROLE_GROUP_ID = "PractitionerRoleGroupFor:";

    @Inject
    private DeploymentInstanceDetailInterface deploymentInstanceDetailInterface;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    @Inject
    private PractitionerRoleResourceHelper practitionerRoleResourceHelper;

    @Inject
    private PractitionerResourceHelpers practitionerResourceHelpers;

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    /**
     * The Identifier for a PractitionerRole's Practitioner Group (which captures the set of Practitioners
     * fulfilling a single PractitionerRole at any single point in time) has a value that is constructed by
     * concatenating the PractitionerRole's primary identifier value to "PractitionerGroupFor:".
     * @param practitionerRoleIdentifierValue
     * @return
     */
    public Identifier buildPractitionerGroupIdentifier(String practitionerRoleIdentifierValue){
        LOG.debug(".buildPractitionerGroupIdentifier(): Entry");
        Identifier groupIdentifier = new Identifier();
        groupIdentifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        CodeableConcept idType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_PRACTITIONER_GROUP);
        groupIdentifier.setType(idType);
        groupIdentifier.setSystem(deploymentInstanceDetailInterface.getDeploymentInstanceSystemEndpointSystem());
        groupIdentifier.setValue(PRACTITIONER_GROUP_ID+practitionerRoleIdentifierValue);
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        groupIdentifier.setPeriod(validPeriod);
        groupIdentifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceSystemOwnerOrganization());
        LOG.debug(".buildPractitionerGroupIdentifier(): Exit, created Identifier --> {}", groupIdentifier);
        return(groupIdentifier);
    }

    /**
     * The Identifier for a Practitioner's PractitionerRole Group (which captures the set of PractitionerRoles
     * fulfilled by a Practitioner at any single point in time) has a value that is constructed by
     * concatenating the Practitioner's primary identifier value to "PractitionerGroupFor:".
     * @param practitionerIdentifierValue
     * @return
     */
    public Identifier buildPractitionerRoleGroupIdentifier(String practitionerIdentifierValue){
        LOG.debug(".buildPractitionerRoleGroupIdentifier(): Entry");
        Identifier groupIdentifier = new Identifier();
        groupIdentifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        CodeableConcept idType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_PRACTITIONER_ROLE_GROUP);
        groupIdentifier.setType(idType);
        groupIdentifier.setSystem(deploymentInstanceDetailInterface.getDeploymentInstanceSystemEndpointSystem());
        groupIdentifier.setValue(PRACTITIONER_ROLE_GROUP_ID+practitionerIdentifierValue);
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        groupIdentifier.setPeriod(validPeriod);
        groupIdentifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceSystemOwnerOrganization());
        LOG.debug(".buildPractitionerRoleGroupIdentifier(): Exit, created Identifier --> {}", groupIdentifier);
        return(groupIdentifier);
    }

    public CodeableConcept buildGroupCode(String typeCode, String displayText){
        CodeableConcept idType = new CodeableConcept();
        Coding idTypeCoding = new Coding();
        idTypeCoding.setCode("grouptypecode:" + typeCode);
        idTypeCoding.setSystem(getPegacornGroupTypeCodeSystem());
        StringType displayElement = new StringType("Group Type Code ("+typeCode+")");
        idTypeCoding.setDisplayElement(displayElement);
        idType.getCoding().add(idTypeCoding);
        idType.setText(displayText);
        return(idType);
    }

    private static final String PEGACORN_GROUP_TYPE_CODE_SYSTEM = "/group-type";

    public String getPegacornGroupTypeCodeSystem() {
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_GROUP_TYPE_CODE_SYSTEM;
        return (codeSystem);
    }
}
