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
package net.fhirfactory.pegacorn.internals.esr.resources;


import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.ContactPointESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.OrganisationStructure;
import net.fhirfactory.pegacorn.internals.esr.resources.valuesets.ExtremelySimplifiedResourceTypeEnum;

public class HealthcareServiceESR extends ExtremelySimplifiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(HealthcareServiceESR.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private List<ContactPointESDT> contactPoints;
    private String primaryOrganizationID;
    private List<OrganisationStructure>organisationStructure;

    public HealthcareServiceESR(){
        super();        
        contactPoints = new ArrayList<>();
        this.organisationStructure = new ArrayList<OrganisationStructure>();
        this.setResourceESRType(ExtremelySimplifiedResourceTypeEnum.ESR_HEALTHCARE_SERVICE);
    }

    
    public List<ContactPointESDT> getContactPoints() {
		return contactPoints;
	}
    
    
	public void setContactPoints(List<ContactPointESDT> contactPoints) {
		this.contactPoints = contactPoints;
	}

	
	public List<OrganisationStructure> getOrganisationStructure() {
        return organisationStructure;
    }

	
	public void setOrganisationStructure(List<OrganisationStructure> organisationStructure) {
        this.organisationStructure = organisationStructure;
    }

    
    public String getPrimaryOrganizationID() {
        return primaryOrganizationID;
    }

    
    public void setPrimaryOrganizationID(String primaryOrganizationID) {
        this.primaryOrganizationID = primaryOrganizationID;
    }

    @Override
    protected ResourceType specifyResourceType() {
        return (ResourceType.HealthcareService);
    }
}
