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

import net.fhirfactory.pegacorn.internals.directories.api.beans.GroupServiceHandler;
import net.fhirfactory.pegacorn.internals.directories.api.common.ResourceDirectoryAPI;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryNotFoundException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GroupDirectoryAPI extends ResourceDirectoryAPI {
    private static final Logger LOG = LoggerFactory.getLogger(GroupDirectoryAPI.class);

    @Inject
    private GroupServiceHandler groupServiceHandler;

    @Override
    protected Logger getLogger(){return(LOG);}

    private String getGroupServiceEndpoint(){
        String endpointSpecification =
                getIngresEndpoint() + "/Group"+getPathSuffix();
        return(endpointSpecification);
    }

    @Override
    public void configure() throws Exception {

        //
        //
        //

        //
        // The Practitioner(Lite) Resource Handler
        //

        onException(DirectoryEntryNotFoundException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "DirectoryEntryNotFoundException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(constant(""));

        onException(DirectoryEntryUpdateException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "DirectoryEntryUpdateException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("${exception.message}\n"));

        onException(Exception.class)
                .handled(true)
                // use HTTP status 500 when we had a server side error
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setBody(simple("${exception.message}\n"));

        from(getGroupServiceEndpoint())
                .log(LoggingLevel.INFO, "Incoming Request --> ${body}")
                .choice()
                .when(header("CamelHttpMethod").isEqualTo("GET"))
                .to("direct:GroupGET")
                .when(header("CamelHttpMethod").isEqualTo("POST"))
                .to("direct:GroupPOST")
                .when(header("CamelHttpMethod").isEqualTo("PUT"))
                .to("direct:GroupPUT")
                .when(header("CamelHttpMethod").isEqualTo("DELETE"))
                .to("direct:GroupDELETE")
                .otherwise()
                .to("direct:Error");


        from("direct:GroupGET")
                .bean(groupServiceHandler, "get")
                .log(LoggingLevel.INFO, "GET Request --> ${body}");

        from("direct:GroupPOST")
                .log(LoggingLevel.INFO, "POST Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));

        from("direct:GroupPUT")
                .log(LoggingLevel.INFO, "PUT Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));

        from("direct:GroupDELETE")
                .log(LoggingLevel.INFO, "DELETE Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));



    }
}
