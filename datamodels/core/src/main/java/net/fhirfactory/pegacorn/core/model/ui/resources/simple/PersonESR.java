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
package net.fhirfactory.pegacorn.core.model.ui.resources.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.ContactPointESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.HumanNameESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.ExtremelySimplifiedResourceTypeEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.HumanNameESDTUseEnum;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

public class PersonESR extends ExtremelySimplifiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(PersonESR.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private HumanNameESDT officialName;
    private ArrayList<HumanNameESDT> otherNames;
    private ArrayList<ContactPointESDT> contactPoints;
    private Date dateOfBirth;

    public PersonESR(){
        super();
        this.contactPoints = new ArrayList<>();
        this.otherNames = new ArrayList<>();
        this.setResourceESRType(ExtremelySimplifiedResourceTypeEnum.ESR_PERSON);
        this.dateOfBirth = null;
    }

    public HumanNameESDT getOfficialName() {
        return officialName;
    }

    public void setOfficialName(HumanNameESDT officialName) {
        this.officialName = officialName;
    }

    public ArrayList<HumanNameESDT> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(ArrayList<HumanNameESDT> otherNames) {
        this.otherNames = otherNames;
    }

    public ArrayList<ContactPointESDT> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(ArrayList<ContactPointESDT> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public HumanNameESDT getHumanNameWithUse(HumanNameESDTUseEnum nameUse){
        for(HumanNameESDT currentName: getOtherNames() ){
            if(currentName.getNameUse().equals(nameUse)){
                return(currentName);
            }
        }
        return(null);
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    protected ResourceType specifyResourceType() {
        return (ResourceType.Person);
    }

    @JsonIgnore
    public boolean hasDateOfBirth(){
        if(this.dateOfBirth == null){
            return(false);
        } else {
            return(true);
        }
    }
}
