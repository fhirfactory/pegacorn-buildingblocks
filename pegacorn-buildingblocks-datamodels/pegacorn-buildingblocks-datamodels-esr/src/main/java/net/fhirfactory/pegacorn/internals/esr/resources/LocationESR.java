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

import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.valuesets.ExtremelySimplifiedResourceTypeEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.TypeESDT;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LocationESR extends ExtremelySimplifiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(LocationESR.class);
    public static String CUMULATIVE_SHORT_NAME_IDENTIFIER_TYPE = "CumulativeShortName";
    public static String CUMULATIVE_LONG_NAME_IDENTIFIER_TYPE = "CumulativeLongName";
    public static String CUMULATIVE_NAME_SEPARATOR = "+";

    private ArrayList<String> containedLocationIDs;
    private String containingLocationID;
    private TypeESDT locationType;

    @Override
    protected Logger getLogger(){return(LOG);}

    public LocationESR(){
        super();
        this.containedLocationIDs = new ArrayList<>();
        this.setResourceESRType(ExtremelySimplifiedResourceTypeEnum.ESR_LOCATION);
    }

    public ArrayList<String> getContainedLocationIDs() {
        return containedLocationIDs;
    }

    public void setContainedLocationIDs(ArrayList<String> containedLocationIDs) {
        this.containedLocationIDs = containedLocationIDs;
    }

    public String getContainingLocationID() {
        return containingLocationID;
    }

    public void setContainingLocationID(String containingLocationID) {
        this.containingLocationID = containingLocationID;
    }

    public TypeESDT getLocationType() {
        return locationType;
    }

    public void setLocationType(TypeESDT locationType) {
        this.locationType = locationType;
    }

    public String populateCumulativeNameIdentifiers(List<IdentifierESDT> parentLocationIdentifierList, String shortName, String longName){
        getLogger().debug(".populateCumulativeNameIdentifiers(), Entry");
        IdentifierESDT cumulativeShortNameIdentifier = new IdentifierESDT();
        cumulativeShortNameIdentifier.setUse(IdentifierESDTUseEnum.OFFICIAL);
        cumulativeShortNameIdentifier.setType(this.CUMULATIVE_SHORT_NAME_IDENTIFIER_TYPE);
        String shortNameValue = generateLocationCumulativeName(parentLocationIdentifierList, shortName,this.CUMULATIVE_SHORT_NAME_IDENTIFIER_TYPE);
        cumulativeShortNameIdentifier.setValue(shortNameValue);
        this.getIdentifiers().add(cumulativeShortNameIdentifier);
        IdentifierESDT cumulativeLongNameIdentifier = new IdentifierESDT();
        cumulativeLongNameIdentifier.setUse(IdentifierESDTUseEnum.OFFICIAL);
        cumulativeLongNameIdentifier.setType(this.CUMULATIVE_LONG_NAME_IDENTIFIER_TYPE);
        String longNameValue = generateLocationCumulativeName(parentLocationIdentifierList, shortName,this.CUMULATIVE_LONG_NAME_IDENTIFIER_TYPE);
        cumulativeLongNameIdentifier.setValue(longNameValue);
        this.getIdentifiers().add(cumulativeLongNameIdentifier);
        getLogger().debug(".populateCumulativeNameIdentifiers(): Exit, returning long cumulative name --> {}", longNameValue);
        return(longNameValue);
    }

    public String generateLocationCumulativeName(List<IdentifierESDT> parentLocationIdentifierList, String thisName, String identifierType){
        getLogger().debug(".buildLocationCumulativeName(), Entry");
        String parentLocationCumulativeName = null;
        if( parentLocationIdentifierList != null ) {
            if( !parentLocationIdentifierList.isEmpty() ) {
                IdentifierESDT uniqueParentIdentifier = null;
                for(IdentifierESDT currentIdentifier: parentLocationIdentifierList){
                    if(currentIdentifier.getType().equalsIgnoreCase(identifierType)){
                        uniqueParentIdentifier = currentIdentifier;
                    }
                }
                if (uniqueParentIdentifier != null) {
                    parentLocationCumulativeName = uniqueParentIdentifier.getValue();
                }
            }
        }
        String cumulativeName = null;
        if(parentLocationCumulativeName == null){
            cumulativeName = thisName;
        } else {
            cumulativeName = parentLocationCumulativeName + this.CUMULATIVE_NAME_SEPARATOR + thisName;
        }
        getLogger().debug(".buildLocationCumulativeName(): Exit, built name --> {}", cumulativeName);
        return(cumulativeName);
    }

    @Override
    protected ResourceType specifyResourceType() {
        return (ResourceType.Location);
    }
}
