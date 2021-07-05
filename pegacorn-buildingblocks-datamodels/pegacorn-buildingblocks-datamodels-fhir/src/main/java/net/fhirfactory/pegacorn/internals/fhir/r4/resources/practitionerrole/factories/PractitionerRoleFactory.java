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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitionerrole.factories;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class PractitionerRoleFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleFactory.class);

    @Inject
    private RoleFactory roleFactory;

    @Inject
    private PractitionerRoleResourceHelper resourceHelper;

    public PractitionerRole buildPractitionerRole(String shortName, String longName, List<CodeableConcept> roleSet){
        PractitionerRole newPractitionerRole = new PractitionerRole();
        Identifier shortNameIdentifier = resourceHelper.buildIdentifierFromShortName(shortName);
        Identifier longNameIdentifier = resourceHelper.buildIdentifierFromLongName(longName);
        newPractitionerRole.getCode().addAll(roleSet);
        newPractitionerRole.addIdentifier(shortNameIdentifier);
        newPractitionerRole.addIdentifier(longNameIdentifier);
        Period notionalAllowedPeriod = new Period();
        notionalAllowedPeriod.setStart(Date.from(Instant.now()));
        newPractitionerRole.setPeriod(notionalAllowedPeriod);
        newPractitionerRole.setActive(true);
        return(newPractitionerRole);
    }
}
