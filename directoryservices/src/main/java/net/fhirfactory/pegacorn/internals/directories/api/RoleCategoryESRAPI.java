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

import net.fhirfactory.pegacorn.internals.directories.api.beans.RoleCategoryServiceHandler;
import net.fhirfactory.pegacorn.internals.directories.api.common.ResourceDirectoryAPI;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.RoleCategoryESR;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.rest.RestParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RoleCategoryESRAPI extends ResourceDirectoryAPI {
    private static final Logger LOG = LoggerFactory.getLogger(RoleCategoryESRAPI.class);

    @Inject
    private RoleCategoryServiceHandler serviceHandler;

    @Override
    protected Logger getLogger(){return(LOG);}

    @Override
    protected String specifyESRName() {
        return ("RoleCategory");
    }

    @Override
    protected Class specifyESRClass() {
        return (RoleCategoryESR.class);
    }

    @Override
    public void configure() throws Exception {

        //
        // Camel REST Configuration
        //

        getRestConfigurationDefinition();

        //
        // The PractitionerRoleDirectory Resource Handler (Exceptions)
        //

        getResourceNotFoundException();
        getResourceUpdateException();
        getGeneralException();

        //
        // The PractitionerESR Resource Handler
        //

        getRestGetDefinition()
            .get("/search?shortName={shortName}&longName={longName}&displayName={displayName}"
                        + "&pageSize={pageSize}&page={page}&sortBy={sortBy}&sortOrder={sortOrder}")
                .param().name("shortName").type(RestParamType.query).required(false).endParam()
                .param().name("longName").type(RestParamType.query).required(false).endParam()
                .param().name("displayName").type(RestParamType.query).required(false).endParam()
                .param().name("leafValue").type(RestParamType.query).required(false).endParam()
                .param().name("pageSize").type(RestParamType.query).required(false).endParam()
                .param().name("page").type(RestParamType.query).required(false).endParam()
                .param().name("sortBy").type(RestParamType.query).required(false).endParam()
                .param().name("sortOrder").type(RestParamType.query).required(false).endParam()
                .to("direct:"+getESRName()+"SearchGET")
            .post().type(getESRClass())
                .to("direct:"+getESRName()+"POST")
            .put().type(getESRClass())
                .to("direct:"+getESRName()+"PUT");


        from("direct:"+getESRName()+"GET")
                .bean(serviceHandler, "getResource")
                .log(LoggingLevel.INFO, "GET Request --> ${body}");

        from("direct:"+getESRName()+"ListGET")
                .bean(serviceHandler, "defaultGetResourceList")
                .log(LoggingLevel.INFO, "GET Request --> ${body}");

        from("direct:"+getESRName()+"SearchGET")
                .bean(serviceHandler, "defaultSearch")
                .log(LoggingLevel.INFO, "GET (Search) Request --> ${body}");

        from("direct:"+getESRName()+"POST")
                .log(LoggingLevel.INFO, "POST Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));

        from("direct:"+getESRName()+"PUT")
                .log(LoggingLevel.INFO, "PUT Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));

        from("direct:"+getESRName()+"DELETE")
                .log(LoggingLevel.INFO, "DELETE Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));
    }
}
