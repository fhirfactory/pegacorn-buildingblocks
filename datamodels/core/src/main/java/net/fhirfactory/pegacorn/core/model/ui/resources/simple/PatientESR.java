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

import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.FunctionESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.ReferenceESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.ExtremelySimplifiedResourceTypeEnum;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class PatientESR extends PersonESR {
    private static final Logger LOG = LoggerFactory.getLogger(PatientESR.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private String primaryLocation;
    private String primaryHealthcareService;
    private String currentLocation;
    private HashMap<FunctionESDT, ReferenceESDT> careProviders;

    public PatientESR(){
        super();
        careProviders = new HashMap<>();
        this.setResourceESRType(ExtremelySimplifiedResourceTypeEnum.ESR_PATIENT);
    }

    public String getPrimaryLocation() {
        return primaryLocation;
    }

    public void setPrimaryLocation(String primaryLocation) {
        this.primaryLocation = primaryLocation;
    }

    public String getPrimaryHealthcareService() {
        return primaryHealthcareService;
    }

    public void setPrimaryHealthcareService(String primaryHealthcareService) {
        this.primaryHealthcareService = primaryHealthcareService;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public HashMap<FunctionESDT, ReferenceESDT> getCareProviders() {
        return careProviders;
    }

    public void setCareProviders(HashMap<FunctionESDT, ReferenceESDT> careProviders) {
        this.careProviders = careProviders;
    }

    @Override
    protected ResourceType specifyResourceType() {
        return (ResourceType.Patient);
    }
}
