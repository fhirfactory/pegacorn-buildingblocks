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
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.TypeESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.valuesets.ExtremelySimplifiedResourceTypeEnum;

public class OrganizationESR extends ExtremelySimplifiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationESR.class);
    
    @Override
    protected Logger getLogger(){return(LOG);}

    private ArrayList<String> containedOrganizations;
    private String parentOrganization;
    private TypeESDT organizationType;
    private List<ContactPointESDT> contactPoints;

    public OrganizationESR(){
        super();
        this.containedOrganizations = new ArrayList<>();

        this.contactPoints = new ArrayList<>();
        this.setResourceESRType(ExtremelySimplifiedResourceTypeEnum.ESR_ORGANIZATION);
    }

    public ArrayList<String> getContainedOrganizations() {
        return containedOrganizations;
    }

    public void setContainedOrganizations(ArrayList<String> containedOrganizations) {
        this.containedOrganizations = containedOrganizations;
    }

    public String getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(String parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public TypeESDT getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(TypeESDT organizationType) {
        this.organizationType = organizationType;
    }

	public List<ContactPointESDT> getContactPoints() {
		return contactPoints;
	}

	public void setContactPoints(List<ContactPointESDT> contactPoints) {
		this.contactPoints = contactPoints;
	}
	
	public void addContactPoint(ContactPointESDT contactPoint) {
		this.contactPoints.add(contactPoint);
	}

    @Override
    protected ResourceType specifyResourceType() {
        return (ResourceType.Organization);
    }
}
