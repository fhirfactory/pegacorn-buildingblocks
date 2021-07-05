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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitionerrole.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems.DeploymentInstanceDetailInterface;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class PractitionerRoleResourceHelper {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleResourceHelper.class);

    @Inject
    private DeploymentInstanceDetailInterface deploymentInstanceDetailInterface;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    public Identifier buildIdentifierFromShortName(String shortName){
        LOG.debug(".buildIdentifierFromShortName(): Entry");
        Identifier identifier = new Identifier();
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        CodeableConcept idType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_PRACTITIONER_ROLE_SHORT_NAME);
        identifier.setType(idType);
        identifier.setSystem(deploymentInstanceDetailInterface.getDeploymentInstanceSystemEndpointSystem());
        identifier.setValue(shortName);
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        identifier.setPeriod(validPeriod);
        identifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceSystemOwnerOrganization());
        LOG.debug(".buildIdentifierFromShortName(): Exit, created Identifier --> {}", identifier);
        return(identifier);
    }

    public Identifier buildIdentifierFromLongName(String longName){
        LOG.debug(".buildIdentifierFromLongName(): Entry");
        Identifier identifier = new Identifier();
        identifier.setUse(Identifier.IdentifierUse.SECONDARY);
        CodeableConcept idType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_PRACTITIONER_ROLE_LONG_NAME);
        identifier.setType(idType);
        identifier.setSystem(deploymentInstanceDetailInterface.getDeploymentInstanceSystemEndpointSystem());
        identifier.setValue(longName);
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        identifier.setPeriod(validPeriod);
        identifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceSystemOwnerOrganization());
        LOG.debug(".buildIdentifierFromLongName(): Exit, created Identifier --> {}", identifier);
        return(identifier);
    }

    /**
     *
     * @param identifierValue
     * @return
     */
    public Reference buildPractitionerRoleReference(String identifierValue){
        LOG.debug(".buildPractitionerRoleReference(): Entry, Entry identifierValue --> {}", identifierValue);
        Identifier referenceIdentifier = buildIdentifierFromShortName(identifierValue);
        Reference practitionerRoleReference = new Reference();
        practitionerRoleReference.setIdentifier(referenceIdentifier);
        practitionerRoleReference.setDisplay("PractitionerRole --> " + identifierValue);
        practitionerRoleReference.setType(ResourceType.PractitionerRole.name());
        LOG.debug(".buildPractitionerRoleReference(): Exit, created Reference --> {}", practitionerRoleReference);
        return(practitionerRoleReference);
    }

}
