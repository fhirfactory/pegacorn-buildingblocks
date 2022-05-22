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
package net.fhirfactory.pegacorn.internals.hl7v2.transfoms.tofhir.common;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;

public abstract class ResourceFromHL7v2xBase {

    private HapiContext hapiContext;
    private boolean initialised;
    private HL7ToFHIRConverter hl7ToFHIRConverter;

    //
    // Constructor(s)
    //

    public ResourceFromHL7v2xBase(){
        initialised = false;
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){
        if(!initialised){
            hapiContext = new DefaultHapiContext();
            hl7ToFHIRConverter = new HL7ToFHIRConverter();
            initialised = true;
        }
    }

    //
    // Abstract Methods
    //

    abstract protected Logger getLogger();

    //
    // Getters (and Setters)
    //

    protected HL7ToFHIRConverter getHL7ToFHIRConverter(){
        return(this.hl7ToFHIRConverter);
    }

    protected HapiContext getHapiContext(){
        return(this.hapiContext);
    }
}
