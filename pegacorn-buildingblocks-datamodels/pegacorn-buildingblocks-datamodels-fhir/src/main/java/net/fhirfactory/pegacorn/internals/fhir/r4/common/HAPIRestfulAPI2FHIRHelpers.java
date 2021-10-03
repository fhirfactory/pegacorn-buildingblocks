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
package net.fhirfactory.pegacorn.internals.fhir.r4.common;

import javax.enterprise.context.ApplicationScoped;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.TokenParamModifier;

@ApplicationScoped
public class HAPIRestfulAPI2FHIRHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(HAPIRestfulAPI2FHIRHelpers.class);

    /**
     * This method converts a received TokenParam (representing a FHIR::Identifier) and converts it to
     * an actual FHIR::Identifier object.
     *
     * Contrary to the understood structure of the TokenParam attribute within the HAPI FHIR documentation,
     * Pegacorn maps the System & Code to be associated to the FHIR::Identifier.type.system &  FHIR::Identifier.type.code
     * rather than the System attribute of the TokenParam mapping to the FHIR::Identifier.system.
     *
     * @param identifierParam The TokenParam attribute representing an FHIR::Identifier
     * @return A (minimally populated) FHIR::Identifier element.
     */
    public Identifier tokenParam2Identifier(TokenParam identifierParam){
        LOG.debug(".tokenParam2Identifier(): Entry, identifierParam (TokenParam) --> {}", identifierParam);
        if(identifierParam == null){
            LOG.warn(".tokenParam2Identifier(): Parameter identifierParam (TokenParam) is null");
            return(null);
        }
        String identifierTypeSystemValue = identifierParam.getSystem();
        String identifierTypeCodeValue = null;
        String identifierValue = null;
        boolean hasAppropriateModifier = false;
        if(identifierParam.getModifier() == null) {
            LOG.trace(".tokenParam2Identifier(): There is no modifier present");
            hasAppropriateModifier = false;
        } else {
            if(identifierParam.getModifier().getValue().equals(TokenParamModifier.OF_TYPE.getValue())){
                hasAppropriateModifier = true;
            }
        }
        // HAPI FHIR Server is not passing Modifiers
        // This code manually checks to see if a modifier
        // has been used.
        if(!hasAppropriateModifier) {
            if(identifierParam.getValue().contains("|")) {
                hasAppropriateModifier = true;
            }
        }
        if(hasAppropriateModifier){
            String paramValue = identifierParam.getValue();
            String[] values = paramValue.split("\\|");
            identifierTypeCodeValue = values[0];
            identifierValue = values[1];
        }
        else {
            identifierValue = identifierParam.getValue();
            identifierTypeCodeValue = "";
        }
        CodeableConcept identifierType = new CodeableConcept();
        Coding identifierTypeCode = new Coding();
        identifierTypeCode.setCode(identifierTypeCodeValue);
        identifierTypeCode.setSystem(identifierTypeSystemValue);
        identifierType.addCoding(identifierTypeCode);
        identifierType.setText(identifierTypeSystemValue + ":" + identifierTypeCodeValue);
        Identifier generatedIdentifier = new Identifier();
        generatedIdentifier.setType(identifierType);
        generatedIdentifier.setValue(identifierValue);
        LOG.debug(".tokenParam2Identifier(): Exit, created Identifier --> {}", generatedIdentifier);
        return(generatedIdentifier);
    }
}
