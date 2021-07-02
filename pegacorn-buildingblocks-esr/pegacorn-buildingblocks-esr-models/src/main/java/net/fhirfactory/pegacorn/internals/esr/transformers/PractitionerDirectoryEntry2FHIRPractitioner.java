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
package net.fhirfactory.pegacorn.internals.esr.transformers;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.GroupFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitioner.PractitionerFactory;

@ApplicationScoped
public class PractitionerDirectoryEntry2FHIRPractitioner {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerDirectoryEntry2FHIRPractitioner.class);

    @Inject
    private PractitionerFactory practitionerFactory;

    @Inject
    private GroupFactory groupFactory;

    @Inject
    private DataTypeTransformers dataTypeTransformers;


    public PractitionerDirectoryEntry2FHIRPractitioner() {
    }

    /**
     *
     * @param practitionerESR
     * @return
     */
    public org.hl7.fhir.r4.model.Practitioner convertToPractitioner(PractitionerESR practitionerESR){
        LOG.debug(".convertToPractitioner(): Entry");
        String practitionerName = practitionerESR.getOfficialName().getDisplayName();
        String practitionerEmail = practitionerESR.getIdentifierWithType(IdentifierType.EMAIL_ADDRESS).getValue();
        org.hl7.fhir.r4.model.Practitioner newPractitioner = practitionerFactory.buildPractitioner(practitionerName, practitionerEmail);
        List<ContactPoint> contactPoints = dataTypeTransformers.createContactPoints(practitionerESR);
        newPractitioner.getTelecom().addAll(contactPoints);
        return(newPractitioner);
    }

    /**
     *
     * @param practitionerESR
     * @return
     */
    public Group createPractitionerRoleGroup(PractitionerESR practitionerESR){
        String associatedPractitionerEmailAddress = practitionerESR.getIdentifierWithType(IdentifierType.EMAIL_ADDRESS).getValue();
        Group practitionerRoleGroup = groupFactory.buildPractitionerGroupForPractitionerRole(associatedPractitionerEmailAddress);
        return(practitionerRoleGroup);
    }
}
