package net.fhirfactory.pegacorn.platform.edge.itops.apiservices;

import net.fhirfactory.pegacorn.platform.edge.itops.apiservices.transactions.EndpointStatusHandler;
import net.fhirfactory.pegacorn.platform.edge.itops.apiservices.transactions.common.PetasosITOpsMethodOutcome;
import net.fhirfactory.pegacorn.platform.edge.itops.configuration.JGroupsGossipRouterNode;
import org.apache.camel.Exchange;
import org.hl7.fhir.r4.model.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;

public class APIServicesFacade extends APIServicesFacadeBase{
    private static final Logger LOG = LoggerFactory.getLogger(APIServicesFacade.class);

    private Date lastUpdateDate;

    @Override
    protected Logger getLogger(){return(LOG);}

    public APIServicesFacade(JGroupsGossipRouterNode node){
        super(node);
        LOG.debug(".APIServicesFacade(): Entry, node->{}", node);
        lastUpdateDate = Date.from(Instant.now());
    }

    public void updateDate(){
        lastUpdateDate = Date.from(Instant.now());
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
        // The Endpoint Resource Handler
        //

        rest("/")
                .get("/{id}").outType(PetasosITOpsMethodOutcome.class)
                .to("direct:EndpointStatusGET")
                .post().type(Endpoint.class)
                .to("direct:EndpointStatusPOST")
                .put().type(Endpoint.class)
                .to("direct:EndpointStatusPUT")
                .delete().type(Endpoint.class)
                .to("direct:EndpointStatusDELETE");

        from("direct:EndpointStatusGET")
                .bean(EndpointStatusHandler.class, "getResource");

        from("direct:EndpointStatusPOST")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));

        from("direct:EndpointStatusPUT")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));

        from("direct:EndpointStatusDELETE")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                .setBody(simple("Action not support for this Directory Entry"));
    }

    @Override
    public String toString() {
        return "APIServicesFacade{" +
                "lastUpdateDate=" + lastUpdateDate +
                ", topologyNode=" + topologyNode +
                ", serverPort='" + getServerPort() + '\'' +
                ", serverHost='" + getServerHost() + '\'' +
                ", webContext='" + getWebContext() + '\'' +
                ", order=" + getOrder() +
                ", restConfiguration=" + restConfiguration() +
                ", rest=" + rest() +
                ", transformer=" + transformer() +
                ", validator=" + validator() +
                ", intercept=" + intercept() +
                ", interceptFrom=" + interceptFrom() +
                ", onCompletion=" + onCompletion() +
                ", restCollection=" + getRestCollection() +
                ", restConfiguration=" + getRestConfiguration() +
                ", routeCollection=" + getRouteCollection() +
                ", routeTemplateCollection=" + getRouteTemplateCollection() +
                ", body=" + body() +
                ", exceptionMessage=" + exceptionMessage() +
                ", defaultErrorHandler=" + defaultErrorHandler() +
                ", noErrorHandler=" + noErrorHandler() +
                ", context=" + getContext() +
                ", errorHandlerBuilder=" + getErrorHandlerBuilder() +
                '}';
    }
}
