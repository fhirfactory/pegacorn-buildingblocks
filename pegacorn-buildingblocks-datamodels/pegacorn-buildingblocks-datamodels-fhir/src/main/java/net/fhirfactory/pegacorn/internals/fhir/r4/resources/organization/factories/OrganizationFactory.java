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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.organization.factories;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.OrganizationType;
import org.hl7.fhir.r4.model.codesystems.OrganizationTypeEnumFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrganizationFactory {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationFactory.class);

    @Inject
    private OrganizationResourceHelpers organizationResourceHelpers;

    public Organization buildOrganization(String organizationName, OrganizationType organizationType, Reference parentOrganization){
        LOG.debug(".buildOrganization(): Entry, organizationName --> {}, organizationType --> {}, parentOrganization --> {}", organizationName,organizationType,parentOrganization);
        Organization newOrganization = new Organization();
        // Populate the Organization Identifier (using standard Identifier type)
        Identifier organizationIdentifier = organizationResourceHelpers.buildOrganizationIdentifier(organizationName);
        newOrganization.addIdentifier(organizationIdentifier);
        OrganizationTypeEnumFactory typeFactory = new OrganizationTypeEnumFactory();
        CodeableConcept orgType = organizationResourceHelpers.buildOrganizationType(organizationType);
        newOrganization.addType(orgType);
        if(parentOrganization != null){
            newOrganization.setPartOf(parentOrganization);
        }
        newOrganization.setName(organizationName);
        LOG.debug(".buildOrganization(): Exit, create Organization --> {}", newOrganization);
        return(newOrganization);
    }

    public Organization buildOrganization(String organizationShortName, String organizationLongName, OrganizationType organizationType, Reference parentOrganization){
        LOG.debug(".buildOrganization(): Entry, organizationShortName --> {}, organizationLongName --> {}, organizationType --> {}, parentOrganization --> {}",
                organizationShortName, organizationLongName ,organizationType,parentOrganization);

        Organization newOrganization = new Organization();
        // Populate the Organization Identifier (using standard Identifier type)
        Identifier organizationIdentifier = organizationResourceHelpers.buildOrganizationIdentifier(organizationShortName, Identifier.IdentifierUse.USUAL);
        newOrganization.addIdentifier(organizationIdentifier);
        OrganizationTypeEnumFactory typeFactory = new OrganizationTypeEnumFactory();
        CodeableConcept orgType = organizationResourceHelpers.buildOrganizationType(organizationType);
        newOrganization.addType(orgType);
        if(parentOrganization != null){
            newOrganization.setPartOf(parentOrganization);
        }
        newOrganization.setName(organizationLongName);
        newOrganization.addAlias(organizationShortName);
        LOG.debug(".buildOrganization(): Exit, create Organization --> {}", newOrganization);
        return(newOrganization);
    }
}
