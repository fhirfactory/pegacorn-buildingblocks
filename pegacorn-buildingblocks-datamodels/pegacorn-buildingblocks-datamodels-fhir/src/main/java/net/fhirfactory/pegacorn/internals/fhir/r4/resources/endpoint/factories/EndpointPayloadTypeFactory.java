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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories;

import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EndpointPayloadTypeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(EndpointPayloadTypeFactory.class);
    private String PEGACORN_ENDPOINT_CODE_SYSTEM = "/endpoint/payload_type";

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    public CodeableConcept newPayloadType(EndpointPayloadTypeEnum codeValue){
        LOG.debug(".newPayloadType(): Entry, codeValue->{}", codeValue);
        CodeableConcept newCC = new CodeableConcept();
        Coding newCoding = new Coding();
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_ENDPOINT_CODE_SYSTEM;
        newCoding.setSystem(codeSystem);
        newCoding.setCode(codeValue.getPayloadType());
        newCoding.setDisplay(codeValue.getPayloadType());
        newCC.addCoding(newCoding);
        newCC.setText(codeValue.getPayloadType());
        LOG.debug(".newPayloadType(): Exit, newCC->{}", newCC);
        return(newCC);
    }
}
