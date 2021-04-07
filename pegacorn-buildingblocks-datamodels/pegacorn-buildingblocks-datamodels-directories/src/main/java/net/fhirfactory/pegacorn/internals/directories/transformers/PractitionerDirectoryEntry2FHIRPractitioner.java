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
package net.fhirfactory.pegacorn.internals.directories.transformers;

import net.fhirfactory.pegacorn.internals.directories.transformers.common.DataTypeTransformers;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.GroupFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitioner.PractitionerFactory;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerDirectoryEntry;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

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
     * @param practitionerDirectoryEntry
     * @return
     */
    public org.hl7.fhir.r4.model.Practitioner convertToPractitioner(PractitionerDirectoryEntry practitionerDirectoryEntry){
        LOG.debug(".convertToPractitioner(): Entry");
        String practitionerName = practitionerDirectoryEntry.getOfficialName().getDisplayName();
        String practitionerEmail = practitionerDirectoryEntry.getIdentifierWithType("EmailAddress").getValue();
        org.hl7.fhir.r4.model.Practitioner newPractitioner = practitionerFactory.buildPractitioner(practitionerName, practitionerEmail);
        List<ContactPoint> contactPoints = dataTypeTransformers.createContactPoints(practitionerDirectoryEntry);
        newPractitioner.getTelecom().addAll(contactPoints);
        return(newPractitioner);
    }

    /**
     *
     * @param practitionerDirectoryEntry
     * @return
     */
    public Group createPractitionerRoleGroup(PractitionerDirectoryEntry practitionerDirectoryEntry){
        String associatedPractitionerEmailAddress = practitionerDirectoryEntry.getIdentifierWithType("EmailAddress").getValue();
        Group practitionerRoleGroup = groupFactory.buildPractitionerGroupForPractitionerRole(associatedPractitionerEmailAddress);
        return(practitionerRoleGroup);
    }
}
