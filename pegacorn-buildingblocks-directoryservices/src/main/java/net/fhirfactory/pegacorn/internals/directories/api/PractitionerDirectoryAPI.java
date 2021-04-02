/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.internals.directories.api;

import net.fhirfactory.pegacorn.internals.directories.api.beans.PractitionerServiceHandler;
import net.fhirfactory.pegacorn.internals.directories.api.common.ResourceDirectoryAPI;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PractitionerDirectoryAPI extends ResourceDirectoryAPI {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerDirectoryAPI.class);

    @Inject
    private PractitionerServiceHandler practitionerServiceHandler;

    @Override
    protected Logger getLogger(){return(LOG);}

    private String getPractitionerServiceEndpoint(){
        String endpointSpecification =
                getIngresEndpoint() + "/Practitioner?matchOnUriPrefix=true";
        return(endpointSpecification);
    }

    @Override
    public void configure() throws Exception {

        //
        // The Practitioner(Lite) Resource Handler
        //

        onException(DirectoryEntryNotFoundException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "DirectoryEntryNotFoundException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(constant(""));

        from(getPractitionerServiceEndpoint())
                .log(LoggingLevel.INFO, "Incoming Request --> ${body}")
                .choice()
                .when(header("CamelHttpMethod").isEqualTo("GET"))
                .to("direct:PractitionerGET")
                .when(header("CamelHttpMethod").isEqualTo("POST"))
                .to("direct:PractitionerPOST")
                .when(header("CamelHttpMethod").isEqualTo("PUT"))
                .to("direct:PractitionerPUT")
                .when(header("CamelHttpMethod").isEqualTo("DELETE"))
                .to("direct:PractitionerDELETE")
                .otherwise()
                .to("direct:Error");


        from("direct:PractitionerGET")
                .bean(practitionerServiceHandler, "get")
                .log(LoggingLevel.INFO, "GET Request --> ${body}");

        from("direct:PractitionerPOST")
                .log(LoggingLevel.INFO, "POST Request --> ${body}");

        from("direct:PractitionerPUT")
                .bean(practitionerServiceHandler, "update")
                .log(LoggingLevel.INFO, "PUT Request --> ${body}");

        from("direct:PractitionerDELETE")
                .log(LoggingLevel.INFO, "DELETE Request --> ${body}");



    }
}
