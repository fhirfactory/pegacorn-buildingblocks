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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitioner.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems.DeploymentInstanceDetailInterface;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.datatypes.ContactPointFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.datatypes.HumanNameFactory;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class PractitionerResourceHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerResourceHelpers.class);

    @Inject
    private DeploymentInstanceDetailInterface deploymentInstanceDetailInterface;

    @Inject
    private ContactPointFactory contactPointFactory;

    @Inject
    private HumanNameFactory humanNameFactory;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    public Identifier buildPractitionerIdentifierFromEmail(String eMailAddress){
        LOG.debug(".constructIdentifierFromEmail(): Entry");
        Identifier emailBasedIdentifier = new Identifier();
        emailBasedIdentifier.setUse(Identifier.IdentifierUse.SECONDARY);
        CodeableConcept idType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_PRACTITIONER_EMAIL);
        emailBasedIdentifier.setType(idType);
        emailBasedIdentifier.setSystem(deploymentInstanceDetailInterface.getDeploymentInstanceSystemEndpointSystem());
        emailBasedIdentifier.setValue(eMailAddress);
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        emailBasedIdentifier.setPeriod(validPeriod);
        emailBasedIdentifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceSystemOwnerOrganization());
        LOG.debug(".constructIdentifierFromEmail(): Exit, created Identifier --> {}", emailBasedIdentifier);
        return(emailBasedIdentifier);
    }

    public HumanName constructHumanName(String firstName, String lastName, String middleName, String prefix, String suffix, HumanName.NameUse nameUse){
        LOG.debug(".constructHumanName(): Entry, firstName --> {}, middleName --> {}, lastName --> {}, prefix --> {}, suffix --> {}, nameUse --> {}", firstName, lastName, middleName, prefix, suffix, nameUse);
        HumanName name = humanNameFactory.constructHumanName(firstName,lastName,middleName,prefix,suffix,nameUse);
        LOG.debug(".constructHumanName(): Exit, HumanName --> {}", name);
        return(name);
    }

    public ContactPoint constructContactPoint(String value, ContactPoint.ContactPointUse use, ContactPoint.ContactPointSystem system, int rank){
        LOG.debug(".constructContactPoint(): Entry, value --> {}, use --> {}, system --> {}, rank --> {}", value, use, system, rank);
        ContactPoint contact = contactPointFactory.buildContactPoint(value, use, system, rank);
        LOG.debug(".constructContactPoint(): Exit");
        return(contact);
    }

    /**
     *
     * @param identifierValue
     * @return
     */
    public Reference buildPractitionerReferenceUsingEmail(String identifierValue){
        LOG.debug(".buildPractitionerReferenceUsingEmail(): Entry, Entry identifierValue --> {}", identifierValue);
        Identifier referenceIdentifier = buildPractitionerIdentifierFromEmail(identifierValue);
        Reference practitionerReference = new Reference();
        practitionerReference.setIdentifier(referenceIdentifier);
        practitionerReference.setDisplay("Practitioner --> " + identifierValue);
        practitionerReference.setType(ResourceType.Practitioner.name());
        LOG.debug(".buildPractitionerReferenceUsingEmail(): Exit, created Reference --> {}", practitionerReference);
        return(practitionerReference);
    }
}
