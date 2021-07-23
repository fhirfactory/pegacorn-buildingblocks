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
package net.fhirfactory.pegacorn.platform.edge.itops.apiservices;

import net.fhirfactory.pegacorn.platform.edge.itops.apiservices.transactions.exceptions.ResourceNotFoundException;
import net.fhirfactory.pegacorn.platform.edge.itops.apiservices.transactions.exceptions.ResourceUpdateException;
import net.fhirfactory.pegacorn.platform.edge.itops.configuration.JGroupsGossipRouterNode;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.slf4j.Logger;

public abstract class APIServicesFacadeBase extends RouteBuilder {

    JGroupsGossipRouterNode topologyNode;

    public APIServicesFacadeBase(JGroupsGossipRouterNode node){
        super();
        topologyNode = node;
    }

    protected String getPathSuffix() {
        String suffix = "?matchOnUriPrefix=true&option.enableCORS=true&option.corsAllowedCredentials=true";
        return (suffix);
    }

    abstract protected Logger getLogger();

    public String getServerPort() {
        int portValue = topologyNode.getPropertyFile().getPrometheusPort().getPortValue();
        String portValueAsString = Integer.toString(portValue);
        return (portValueAsString);
    }

    public String getServerHost() {
        String hostValue = topologyNode.getPropertyFile().getPrometheusPort().getHostDNSEntry();
        return (hostValue);
    }

    public String getWebContext(){
        String webContext = topologyNode.getPropertyFile().getPrometheusPort().getWebServicePath();
        return(webContext);
    }

    protected RestConfigurationDefinition getRestConfigurationDefinition() {
        RestConfigurationDefinition restConf = restConfiguration()
                .component("netty-http")
                .scheme("http")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath(getWebContext());
        return (restConf);
    }

    protected OnExceptionDefinition getResourceNotFoundException() {
        OnExceptionDefinition exceptionDef = onException(ResourceNotFoundException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "ResourceNotFoundException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));

        return(exceptionDef);
    }

    protected OnExceptionDefinition getResourceUpdateException() {
        OnExceptionDefinition exceptionDef = onException(ResourceUpdateException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "ResourceUpdateException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400));

        return(exceptionDef);
    }

    protected OnExceptionDefinition getGeneralException() {
        OnExceptionDefinition exceptionDef = onException(Exception.class)
                .handled(true)
                // use HTTP status 500 when we had a server side error
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500));
        return (exceptionDef);
    }


}
