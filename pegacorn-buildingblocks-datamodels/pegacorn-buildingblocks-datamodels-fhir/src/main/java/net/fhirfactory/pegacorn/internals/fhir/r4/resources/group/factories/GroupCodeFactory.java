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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornGroupCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets.GroupCodeValueSet;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GroupCodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(GroupCodeFactory.class);

    @Inject
    private PegacornGroupCodeSystemFactory pegacornGroupCodeSystemFactory;

    @Inject
    private GroupCodeValueSet groupCodeValueSet;

    public CodeableConcept newGroupCode(String codeValue, String displayText){
        LOG.debug(".newGroupCode(): Entry, codeValue->{}, displayText->{}", codeValue, displayText);
        CodeableConcept newCC = new CodeableConcept();
        Coding newCoding = new Coding();
        newCoding.setSystem(groupCodeValueSet.getCommunicateRoomCodeSystem());
        newCoding.setCode(codeValue);
        newCoding.setDisplay(displayText);
        newCC.addCoding(newCoding);
        newCC.setText(displayText);
        LOG.debug(".newGroupCode(): Exit, newCC->{}", newCC);
        return(newCC);
    }

    public CodeableConcept newGroupCode(String system, String codeValue, String displayText){
        LOG.debug(".newGroupCode(): Entry, systme->{}, codeValue->{}, displayText->{}", system, codeValue, displayText);
        CodeableConcept newCC = new CodeableConcept();
        Coding newCoding = new Coding();
        newCoding.setSystem(system);
        newCoding.setCode(codeValue);
        newCoding.setDisplay(displayText);
        newCC.addCoding(newCoding);
        newCC.setText(displayText);
        LOG.debug(".newGroupCode(): Exit, newCC->{}", newCC);
        return(newCC);
    }
}
