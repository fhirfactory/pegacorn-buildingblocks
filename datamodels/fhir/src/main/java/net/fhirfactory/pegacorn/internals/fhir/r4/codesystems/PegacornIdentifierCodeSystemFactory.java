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
package net.fhirfactory.pegacorn.internals.fhir.r4.codesystems;

import net.fhirfactory.pegacorn.core.constants.systemwide.DRICaTSReferenceProperties;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PegacornIdentifierCodeSystemFactory {

    @Inject
    private DRICaTSReferenceProperties systemWideProperties;

    private static final String PEGACORN_IDENTIFIER_CODE_SYSTEM = "/identifier-type";

    public String getPegacornIdentifierCodeSystem() {
        String codeSystem = systemWideProperties.getDRICaTSCodeSystemSite() + PEGACORN_IDENTIFIER_CODE_SYSTEM;
        return (codeSystem);
    }

    public CodeableConcept buildIdentifierType(PegacornIdentifierCodeEnum identifierCode){
        CodeableConcept idType = new CodeableConcept();
        Coding idTypeCoding = new Coding();
        idTypeCoding.setCode(identifierCode.getToken());
        idTypeCoding.setSystem(getPegacornIdentifierCodeSystem());
        idTypeCoding.setDisplay(identifierCode.getDisplayName());
        idType.getCoding().add(idTypeCoding);
        idType.setText(identifierCode.getDisplayText());
        return(idType);
    }
}
