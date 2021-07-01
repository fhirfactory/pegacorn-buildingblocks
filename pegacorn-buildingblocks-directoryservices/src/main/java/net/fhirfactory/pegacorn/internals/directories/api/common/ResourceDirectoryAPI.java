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
package net.fhirfactory.pegacorn.internals.directories.api.common;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty.http.NettyHttpComponent;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.slf4j.Logger;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceNotFoundException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceUpdateException;
import net.fhirfactory.buildingblocks.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.platform.edge.common.CamelSSLTLSHandler;

public abstract class ResourceDirectoryAPI extends RouteBuilder {

    @Inject
    private PegacornReferenceProperties pegacornReferenceProperties;

    @Inject
    private CamelSSLTLSHandler camelSSLTLSHandler;

    private static String SERVER_PORT = "12121";
    private static String SERVER_HOST = "0.0.0.0";

    @Override
    public void configure() throws Exception {
    }

    protected PegacornReferenceProperties getPegacornReferenceProperties() {
        return (pegacornReferenceProperties);
    }

    protected String getPathSuffix() {
        String suffix = "?matchOnUriPrefix=true&option.enableCORS=true&option.corsAllowedCredentials=true";
        return (suffix);
    }

    abstract protected Logger getLogger();
    abstract protected String specifyESRName();
    abstract protected Class specifyESRClass();

    public String getESRName(){
        return(specifyESRName());
    }

    public Class<ExtremelySimplifiedResource> getESRClass(){
        return(specifyESRClass());
    }

    public static String getServerPort() {
        return (SERVER_PORT);
    }

    public static String getServerHost() {
        return SERVER_HOST;
    }

    protected RestConfigurationDefinition getRestConfigurationDefinition() {
        final String NETTY_HTTP = "netty-http";
        String scheme = "http";
        
        if (camelSSLTLSHandler.isSSLTLSEnabled()) {
            scheme = "https";
            camelSSLTLSHandler.applySSLTLSConfigToCamelContext(getContext());
    		
    		NettyHttpComponent nettyHttpcomponent = getContext().getComponent(NETTY_HTTP, NettyHttpComponent.class);
    		nettyHttpcomponent.setUseGlobalSslContextParameters(true);
        }

        RestConfigurationDefinition restConf = restConfiguration()
                .component(NETTY_HTTP)
                .scheme(scheme)
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath(getPegacornReferenceProperties().getPegacornResourceDirectoryR1Path()).host(getServerHost()).port(getServerPort())
                .enableCORS(true)
                .corsAllowCredentials(true)
                .corsHeaderProperty("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, logint, X-Total-Count");
       
        return (restConf);
    }

    protected RestDefinition getRestGetDefinition() {
        RestDefinition restDef = rest("/" + getESRName())
            .get("/{simplifiedID}").outType(getESRClass())
                .to("direct:" + getESRName() + "GET");
        return (restDef);
    }

    protected OnExceptionDefinition getResourceNotFoundException() {
        OnExceptionDefinition exceptionDef = onException(ResourceNotFoundException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "ResourceNotFoundException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("${exception.message}\n"));

        return(exceptionDef);
    }

    protected OnExceptionDefinition getResourceUpdateException() {
        OnExceptionDefinition exceptionDef = onException(ResourceUpdateException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "ResourceUpdateException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("${exception.message}\n"));

        return(exceptionDef);
    }

    protected OnExceptionDefinition getGeneralException() {
        OnExceptionDefinition exceptionDef = onException(Exception.class)
                .handled(true)
                // use HTTP status 500 when we had a server side error
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setBody(simple("${exception.message}\n"));
        return (exceptionDef);
    }
}
