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
package net.fhirfactory.pegacorn.internals.hl7v2.transfoms.tofhir;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import net.fhirfactory.pegacorn.internals.hl7v2.transfoms.tofhir.common.ResourceFromHL7v2xBase;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class FHIRPatientFromHLv2x extends ResourceFromHL7v2xBase {
    private static Logger LOG = LoggerFactory.getLogger(FHIRPatientFromHLv2x.class);


    //
    // Constructor(s)
    //

    public FHIRPatientFromHLv2x(){
        super();
    }


    public String transformADTIntoFHIR(String hl7EventString){
        getLogger().debug(".transformADTIntoFHIR(): Entry, hl7EventString->{}", hl7EventString);
        Message msg = null;

        try {
            msg = getHapiContext().getGenericParser().parse(hl7EventString);
        } catch (HL7Exception e) {
            getLogger().error(".transformADTIntoFHIR(): Cannot parse HL7 Message, error->{}", ExceptionUtils.getStackTrace(e));
            getLogger().debug(".transformADTIntoFHIR(): Exit, returning null");
            return(null);
        }
        String output = getHL7ToFHIRConverter().convert(hl7EventString);
        return(output);
    }



    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }



}
