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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier;

import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.TokenParamModifier;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchSupportHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SearchSupportHelper.class);
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
        String identifierValue = null;
        Identifier generatedIdentifier = new Identifier();
        if(hasAppropriateModifier){
            String identifierTypeSystemValue = identifierParam.getSystem();
            String identifierTypeCodeValue = null;
            String paramValue = identifierParam.getValue();
            String[] values = paramValue.split("\\|");
            identifierTypeCodeValue = values[0];
            identifierValue = values[1];
            CodeableConcept identifierType = new CodeableConcept();
            Coding identifierTypeCode = new Coding();
            identifierTypeCode.setCode(identifierTypeCodeValue);
            identifierTypeCode.setSystem(identifierTypeSystemValue);
            identifierType.addCoding(identifierTypeCode);
            identifierType.setText(identifierTypeSystemValue + ":" + identifierTypeCodeValue);
            generatedIdentifier.setType(identifierType);
        }
        else {
            generatedIdentifier.setSystem(identifierParam.getSystem());
            identifierValue = identifierParam.getValue();
        }
        generatedIdentifier.setValue(identifierValue);
        LOG.debug(".tokenParam2Identifier(): Exit, created Identifier --> {}", generatedIdentifier);
        return(generatedIdentifier);
    }

    public TokenParam identifier2TokenParam(Identifier identifier){
        LOG.debug(".identifier2TokenParam(): Entry");
        String identifierSystem = null;
        String identifierCodeValue = null;
        String identifierValue = identifier.getValue();
        if(identifier.hasType()){
            identifierCodeValue = identifier.getType().getCodingFirstRep().getCode();
            if(identifier.getType().getCodingFirstRep().getSystem().equals("RI")){
                identifierSystem = identifier.getSystem();
            } else {
                identifierSystem = identifier.getType().getCodingFirstRep().getSystem();
            }
        } else {
            identifierSystem = identifier.getSystem();
        }
        TokenParam theTokenParam = new TokenParam();
        theTokenParam.setModifier(TokenParamModifier.OF_TYPE);
        String tokenParamValue = null;
        if(identifierCodeValue == null){
            tokenParamValue = identifierValue;
        } else{
            tokenParamValue = identifierCodeValue + "|" + identifierValue;
        }
        theTokenParam.setValue(tokenParamValue);
        theTokenParam.setSystem(identifierSystem);
        return(theTokenParam);
    }
}
