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

import net.fhirfactory.pegacorn.internals.directories.api.beans.PractitionerRoleServiceHandler;
import net.fhirfactory.pegacorn.internals.directories.api.common.ResourceDirectoryAPI;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerRoleDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryNotFoundException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PractitionerRoleDirectoryAPI extends ResourceDirectoryAPI {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleDirectoryAPI.class);

    @Inject
    private PractitionerRoleServiceHandler practitionerRoleServiceHandler;

    @Override
    protected Logger getLogger(){return(LOG);}


    private String getPractitionerRoleServiceEndpoint(){
        String endpointSpecification =
                getIngresEndpoint() + "/PractitionerRole"+getPathSuffix();
        return(endpointSpecification);
    }

    @Override
    public void configure() throws Exception {

        //
        // Camel REST
        //

        restConfiguration()
                .component("netty-http")
                .scheme("http")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath(getPegacornReferenceProperties().getPegacornResourceDirectoryR1Path()).host(getServerHost()).port(getServerPort())
                .enableCORS(true)
                .corsAllowCredentials(true)
                .corsHeaderProperty("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, login");


        //
        // The PractitionerRoleDirectory Resource Handler
        //

        onException(DirectoryEntryNotFoundException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "DirectoryEntryNotFoundException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("${exception.message}\n"));

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


        rest("/PractitionerRole")
            .get("{id}").outType(PractitionerRoleDirectoryEntry.class)
                .to("direct:PractitionerRoleGET")
            .get("?pageSize={pageSize}&page={page}&sortBy={sortBy}&sortOrder={sortOrder}")
                .param().name("pageSize").type(RestParamType.query).required(false).endParam()
                .param().name("page").type(RestParamType.query).required(false).endParam()
                .param().name("sortBy").type(RestParamType.query).required(false).endParam()
                .param().name("sortOrder").type(RestParamType.query).required(false).endParam()
                .to("direct:PractitionerRoleListGET")
            .get("/search?shortName={shortName}&longName={longName}&displayName={displayName}&primaryRoleCategory={primaryRoleCategory}&primaryRole{primaryRole}&primaryOrganization={primaryOrg}&primaryLocation={primaryLocation}&pageSize={pageSize}&page={page}&sortBy={sortBy}&sortOrder={sortOrder}")
                .param().name("shortName").type(RestParamType.query).required(false).endParam()
                .param().name("longName").type(RestParamType.query).required(false).endParam()
                .param().name("displayName").type(RestParamType.query).required(false).endParam()
                .param().name("primaryRoleCategory").type(RestParamType.query).required(false).endParam()
                .param().name("primaryRole").type(RestParamType.query).required(false).endParam()
                .param().name("primaryOrg").type(RestParamType.query).required(false).endParam()
                .param().name("primaryLocation").type(RestParamType.query).required(false).endParam()
                .param().name("pageSize").type(RestParamType.query).required(false).endParam()
                .param().name("page").type(RestParamType.query).required(false).endParam()
                .param().name("sortBy").type(RestParamType.query).required(false).endParam()
                .param().name("sortOrder").type(RestParamType.query).required(false).endParam()
                .to("direct:PractitionerRoleSearchGET")
            .post().type(PractitionerRoleDirectoryEntry.class)
                .to("direct:PractitionerRolePOST")
            .put().type(PractitionerRoleDirectoryEntry.class)
                .to("direct:PractitionerRolePUT");


        from("direct:PractitionerRoleGET")
                .bean(practitionerRoleServiceHandler, "getPractitionerRole")
                .log(LoggingLevel.INFO, "GET Request --> ${body}");

        from("direct:PractitionerRoleListGET")
                .bean(practitionerRoleServiceHandler, "getPractitionerRoleList")
                .log(LoggingLevel.INFO, "GET Request --> ${body}");

        from("direct:PractitionerRoleSearchGET")
                .bean(practitionerRoleServiceHandler, "getPractitionerRoleSearch")
                .log(LoggingLevel.INFO, "GET (Search) Request --> ${body}");

        from("direct:PractitionerRolePOST")
                .log(LoggingLevel.INFO, "POST Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));

        from("direct:PractitionerRolePUT")
                .bean(practitionerRoleServiceHandler, "updatePractitionerRole")
                .log(LoggingLevel.INFO, "PUT Request --> ${body}");

        from("direct:PractitionerRoleDELETE")
                .log(LoggingLevel.INFO, "DELETE Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));


        from("direct:Error")
                .log(LoggingLevel.INFO, "Error --> ${body}");
    }
}
