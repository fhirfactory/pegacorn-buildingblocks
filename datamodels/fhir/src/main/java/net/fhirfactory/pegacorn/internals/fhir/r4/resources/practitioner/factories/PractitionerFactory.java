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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitioner.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.valuesets.InformationCompartmentSecurityCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.valuesets.InformationConfidentialitySecurityCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.SecurityLabelFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.datatypes.ContactPointFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.datatypes.HumanNameFactory;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PractitionerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerFactory.class);

    @Inject
    private PractitionerResourceHelpers practitionerResourceHelpers;

    @Inject
    private SecurityLabelFactory securityLabelFactory;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    @Inject
    private HumanNameFactory humanNameFactory;

    @Inject
    private ContactPointFactory contactPointFactory;

    public Practitioner buildPractitioner(HumanName humanName, Identifier practitionerIdentifier ){
        LOG.debug(".create(): Entry");
        Practitioner newPractitioner = new Practitioner();
        newPractitioner.addName(humanName);
        newPractitioner.addIdentifier(practitionerIdentifier);
        Coding confidentialitySecurityLabel = securityLabelFactory.constructConfidentialitySecurityLabel(InformationConfidentialitySecurityCodeEnum.L);
        Coding compartmentSecurityLabel = securityLabelFactory.constructSecurityLabelCompartment(InformationCompartmentSecurityCodeEnum.HRCOMPT);
        Meta metadata = new Meta();
        metadata.addSecurity(confidentialitySecurityLabel);
        metadata.addSecurity(compartmentSecurityLabel);
        newPractitioner.setMeta(metadata);
        newPractitioner.setActive(true);
        LOG.debug(".create(): Exit");
        return(newPractitioner);
    }

    public Practitioner buildPractitioner(String firstName, String lastName, String practitionerEmail){
        LOG.debug(".create(): Entry");
        Identifier practitionerIdentifier = practitionerResourceHelpers.buildPractitionerIdentifierFromEmail(practitionerEmail);
        HumanName humanName = humanNameFactory.constructHumanName(firstName, lastName, null, null, null, HumanName.NameUse.USUAL);
        Practitioner newPractitioner = buildPractitioner(humanName, practitionerIdentifier);
        LOG.debug(".create(): Entry");
        return(newPractitioner);
    }

    public Practitioner buildPractitioner(String nameText, String practitionerEmail){
        LOG.debug(".create(): Entry");
        Identifier practitionerIdentifier = practitionerResourceHelpers.buildPractitionerIdentifierFromEmail(practitionerEmail);
        HumanName humanName = humanNameFactory.constructHumanName(nameText, HumanName.NameUse.USUAL);
        Practitioner newPractitioner = buildPractitioner(humanName, practitionerIdentifier);
        LOG.debug(".create(): Entry");
        return(newPractitioner);
    }
}
