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
import org.hl7.fhir.r4.model.Coding;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EndpointConnectionTypeCodeFactory {
    private String PEGACORN_ENDPOINT_CODE_SYSTEM = "/endpoint/connection_type";

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    public Coding newPegacornEndpointJGroupsConnectionCodeSystem(String technologyType, String endpointType) {
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_ENDPOINT_CODE_SYSTEM;
        Coding coding = new Coding();
        coding.setCode(technologyType);
        coding.setSystem(codeSystem);
        String codeDisplay = technologyType + "(" + endpointType +" )";
        coding.setDisplay(codeDisplay);
        return (coding);
    }
}
