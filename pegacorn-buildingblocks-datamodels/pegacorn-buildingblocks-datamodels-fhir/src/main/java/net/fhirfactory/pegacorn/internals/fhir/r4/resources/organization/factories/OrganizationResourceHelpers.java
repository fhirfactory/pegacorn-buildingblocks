/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.organization.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems.DeploymentInstanceDetailInterface;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.OrganizationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class OrganizationResourceHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationResourceHelpers.class);

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    @Inject
    private DeploymentInstanceDetailInterface deploymentInstanceDetailInterface;

    /**
     *
     * @param identifierValue
     * @return
     */
    public Identifier buildOrganizationIdentifier(String identifierValue, Identifier.IdentifierUse identifierUse){
        LOG.debug(".buildOrganizationIdentifier(): Entry identifierValue --> {}, identifierUse --> {}", identifierValue, identifierUse);
        // Create an empty FHIR::Identifier element
        Identifier organizationIdentifier = new Identifier();
        // Set the FHIR::Identifier.Type to XX (General Organization Identifier)
        CodeableConcept identifierType = new CodeableConcept();
        Coding identifierTypeCoding = new Coding();
        identifierTypeCoding.setCode("XX");
        identifierTypeCoding.setSystem("http://terminology.hl7.org/ValueSet/v2-0203");
        identifierTypeCoding.setDisplay("Organization identifier");
        identifierType.getCoding().add(identifierTypeCoding);
        identifierType.setText("Organization (or Business Unit) Identifier");
        organizationIdentifier.setType(identifierType);
        // Set the FHIR::Identifier.System to FHIRFactory (it's our ID we're creating)
        organizationIdentifier.setSystem(deploymentInstanceDetailInterface.getDeploymentInstanceSystemEndpointSystem());
        // Set the FHIR::Identifier.Value
        organizationIdentifier.setValue(identifierValue);
        // Set the FHIR::Identifier.Period
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        organizationIdentifier.setPeriod(validPeriod);
        // Set the FHIR::Identifier.Assigner (to us, for this one)
        organizationIdentifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceSystemOwnerOrganization());
        LOG.debug("buildOrganizationIdentifier(): Exit, created Identifier --> {}", organizationIdentifier);
        return (organizationIdentifier);
    }

    /**
     *
     * @param identifierValue
     * @return
     */
    public Identifier buildOrganizationIdentifier(String identifierValue){
        LOG.debug(".buildOrganizationIdentifier(): Entry identifierValue --> {}", identifierValue);
        Identifier newIdentifier = buildOrganizationIdentifier(identifierValue, Identifier.IdentifierUse.USUAL);
        LOG.debug("buildOrganizationIdentifier(): Exit, created Identifier --> {}", newIdentifier);
        return(newIdentifier);
    }

    /**
     *
     * @param orgType
     * @return
     */
    public CodeableConcept buildOrganizationType(OrganizationType orgType){
        LOG.debug(".buildOrganizationType(): Entry, orgType --> {}", orgType);
        CodeableConcept orgTypeCC = new CodeableConcept();
        Coding orgTypeCoding = new Coding();
        orgTypeCoding.setDisplay(orgType.getDisplay());
        orgTypeCoding.setCode(orgType.toCode());
        orgTypeCoding.setSystem(orgType.getSystem());
        orgTypeCC.addCoding(orgTypeCoding);
        orgTypeCC.setText(orgType.getDefinition());
        LOG.debug(".buildOrganizationType(): Exit, created CodeableConcept --> {}", orgTypeCC);
        return(orgTypeCC);
    }

    /**
     *
     * @param identifierValue
     * @param identifierUse
     * @return
     */
    public Reference buildOrganizationReference(String identifierValue, Identifier.IdentifierUse identifierUse){
        LOG.debug(".buildOrganizationReference(): Entry, Entry identifierValue --> {}, identifierUse --> {}", identifierValue, identifierUse);
        Identifier referenceIdentifier = buildOrganizationIdentifier(identifierValue, identifierUse);
        Reference organizationReference = new Reference();
        organizationReference.setIdentifier(referenceIdentifier);
        organizationReference.setDisplay("Organization -->" + identifierValue);
        organizationReference.setType(ResourceType.Organization.name());
        LOG.debug(".buildOrganizationReference(): Exit, created Reference --> {}", organizationReference);
        return(organizationReference);
    }
}
