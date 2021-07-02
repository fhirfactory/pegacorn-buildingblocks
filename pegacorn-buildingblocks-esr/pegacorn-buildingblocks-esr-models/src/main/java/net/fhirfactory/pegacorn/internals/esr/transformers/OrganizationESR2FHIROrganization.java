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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hl7.fhir.r4.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.resources.OrganizationESR;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.organization.factories.OrganizationFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.organization.factories.OrganizationResourceHelpers;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.SecurityLabelFactory;

@ApplicationScoped
public class OrganizationESR2FHIROrganization {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationESR2FHIROrganization.class);

    @Inject
    private OrganizationFactory organizationFactory;

    @Inject
    private OrganizationResourceHelpers organizationResourceHelpers;

    @Inject
    private SecurityLabelFactory securityLabelFactory;

    public Organization convertToOrganization(OrganizationESR organizationESR){
        LOG.debug(".convertToOrganization(): Entry, organizationESR --> {}", organizationESR);
        Organization newOrganization = new Organization();
        LOG.debug(".convertToOrganization(): Exit, created Organization --> {}", newOrganization);
        return(newOrganization);
    }
}
