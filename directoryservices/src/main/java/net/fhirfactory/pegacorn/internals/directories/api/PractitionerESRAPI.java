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
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerRoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.PractitionerRoleListESDT;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.rest.RestParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PractitionerESRAPI extends ResourceDirectoryAPI {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerESRAPI.class);

    @Inject
    private PractitionerServiceHandler practitionerServiceHandler;

    @Override
    protected String specifyESRName() {
        return ("Practitioner");
    }

    @Override
    protected Class specifyESRClass() {
        return (PractitionerESR.class);
    }

    @Override
    protected Logger getLogger(){return(LOG);}

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
            .get("/search?emailAddress={emailAddress}&displayName={displayName}"
                        + "&pageSize={pageSize}&page={page}&sortBy={sortBy}&sortOrder={sortOrder}")
                .param().name("emailAddress").type(RestParamType.query).required(false).endParam()
                .param().name("displayName").type(RestParamType.query).required(false).endParam()
                .param().name("pageSize").type(RestParamType.query).required(false).endParam()
                .param().name("page").type(RestParamType.query).required(false).endParam()
                .param().name("sortBy").type(RestParamType.query).required(false).endParam()
                .param().name("sortOrder").type(RestParamType.query).required(false).endParam()
                .to("direct:"+getESRName()+"SearchGET")
            .get("/{simplifiedID}/PractitionerRoles").outType(PractitionerRoleListESDT.class)
                .to("direct:" + getESRName() + "PractitionerRolesGET")
            .get("/{simplifiedID}/PractitionerRoleFavourites").outType(FavouriteListESDT.class)
                .to("direct:" + getESRName() + "PractitionerRoleFavouritesGET")
            .get("/{simplifiedID}/ServiceFavourites").outType(FavouriteListESDT.class)
                .to("direct:" + getESRName() + "ServiceFavouritesGET")
            .get("/{simplifiedID}/PractitionerFavourites").outType(FavouriteListESDT.class)
                .to("direct:" + getESRName() + "PractitionerFavouritesGET")
            .post().type(PractitionerRoleESR.class)
                .to("direct:"+getESRName()+"POST")
            .put("/").type(PractitionerRoleESR.class)
                .to("direct:"+getESRName()+"PUT")
            .put("/{simplifiedID}/PractitionerRoles/").type(PractitionerRoleListESDT.class)
                .to("direct:"+getESRName()+"PractitionerRolesPUT")
            .put("/{simplifiedID}/PractitionerRoleFavourites/").type(FavouriteListESDT.class)
                .to("direct:"+getESRName()+"PractitionerRolesFavouritesPUT")
            .put("/{simplifiedID}/ServiceFavourites/").type(FavouriteListESDT.class)
                .to("direct:"+getESRName()+"ServiceFavouritesPUT")
            .put("/{simplifiedID}/PractitionerFavourites/").type(FavouriteListESDT.class)
                .to("direct:"+getESRName()+"PractitionerFavouritesPUT");


        from("direct:"+getESRName()+"GET")
                .bean(practitionerServiceHandler, "getResource")
                .log(LoggingLevel.DEBUG, "GET Request --> ${body}");

        from("direct:" + getESRName() + "PractitionerRolesGET")
                .bean(practitionerServiceHandler, "getPractitionerRoles")
                .log(LoggingLevel.DEBUG, "GET Request --> ${body}");

        from("direct:" + getESRName() + "PractitionerRolesPUT")
                .bean(practitionerServiceHandler, "updatePractitionerRoles")
                .log(LoggingLevel.DEBUG, "PUT Request --> ${body}");

        from("direct:" + getESRName() + "PractitionerRoleFavouritesGET")
                .bean(practitionerServiceHandler, "getPractitionerRoleFavourites")
                .log(LoggingLevel.DEBUG, "PUT Request --> ${body}");

        from("direct:" + getESRName() + "PractitionerRoleFavouritesPUT")
                .bean(practitionerServiceHandler, "updatePractitionerRoleFavourites")
                .log(LoggingLevel.DEBUG, "PUT Request --> ${body}");

        from("direct:" + getESRName() + "PractitionerFavouritesGET")
                .bean(practitionerServiceHandler, "getPractitionerFavourites")
                .log(LoggingLevel.DEBUG, "PUT Request --> ${body}");

        from("direct:" + getESRName() + "PractitionerFavouritesPUT")
                .bean(practitionerServiceHandler, "updatePractitionerFavourites")
                .log(LoggingLevel.DEBUG, "PUT Request --> ${body}");

        from("direct:" + getESRName() + "ServiceFavouritesGET")
                .bean(practitionerServiceHandler, "getServiceFavourites")
                .log(LoggingLevel.DEBUG, "PUT Request --> ${body}");

        from("direct:" + getESRName() + "ServiceFavouritesPUT")
                .bean(practitionerServiceHandler, "updateServiceFavourites")
                .log(LoggingLevel.DEBUG, "PUT Request --> ${body}");

        from("direct:"+getESRName()+"ListGET")
                .bean(practitionerServiceHandler, "defaultGetResourceList")
                .log(LoggingLevel.DEBUG, "GET Request --> ${body}");

        from("direct:"+getESRName()+"SearchGET")
                .bean(practitionerServiceHandler, "defaultSearch")
                .log(LoggingLevel.DEBUG, "GET (Search) Request --> ${body}");

        from("direct:"+getESRName()+"POST")
                .log(LoggingLevel.DEBUG, "POST Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));

        from("direct:"+getESRName()+"PUT")
                .bean(practitionerServiceHandler, "updatePractitioner")
                .log(LoggingLevel.DEBUG, "PUT Request --> ${body}");

        from("direct:"+getESRName()+"DELETE")
                .log(LoggingLevel.DEBUG, "DELETE Request --> ${body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));
    }
}
