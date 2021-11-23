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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories;

import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import org.hl7.fhir.r4.model.Coding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EndpointMetaTagFactory {
    private static final Logger LOG = LoggerFactory.getLogger(EndpointMetaTagFactory.class);


    @Inject
    private PegacornReferenceProperties systemWideProperties;

    private static final String PEGACORN_ENDPOINT_DEFAULT_HOSTNAME = "/endpoint-meta-tag-default_hostname";
    private static final String PEGACORN_ENDPOINT_DEFAULT_PORT = "/endpoint-meta-tag-default_port";
    private static final String PEGACORN_ENDPOINT_CONNECTION_BEHAVIOUR = "/endpoint-meta-tag-connection_behaviour";

    private static final String PEGACORN_ENDPOINT_IS_SERVER = "isServer";
    private static final String PEGACORN_ENDPOINT_IS_CLIENT = "isClient";
    //
    // Business Methods
    //

    public String getPegacornEndpointDefaultHostname() {
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_ENDPOINT_DEFAULT_HOSTNAME;
        return (codeSystem);
    }

    public String getPegacornEndpointDefaultPort() {
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_ENDPOINT_DEFAULT_PORT;
        return(codeSystem);
    }

    public String getPegacornEndpointConnectionBehaviour(){
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_ENDPOINT_CONNECTION_BEHAVIOUR;
        return(codeSystem);
    }

    public Coding newEndpointDefaultHostName(String hostName){
        Coding podIdCoding = new Coding();
        podIdCoding.setSystem(getPegacornEndpointDefaultHostname());
        podIdCoding.setCode(hostName);
        return(podIdCoding);
    }

    public Coding newEndpointDefaultPortNumber(String portNumber){
        Coding podIPAddressCoding = new Coding();
        podIPAddressCoding.setSystem(getPegacornEndpointDefaultPort());
        podIPAddressCoding.setCode(portNumber);
        return(podIPAddressCoding);
    }

    public Coding newEndpointServerTag(){
        Coding endpointBehaviour = new Coding();
        endpointBehaviour.setSystem(getPegacornEndpointConnectionBehaviour());
        endpointBehaviour.setCode(PEGACORN_ENDPOINT_IS_SERVER);
        endpointBehaviour.setDisplay("Endpoint Acts as a Server");
        return(endpointBehaviour);
    }

    public Coding newEndpointClientTag(){
        Coding endpointBehaviour = new Coding();
        endpointBehaviour.setSystem(getPegacornEndpointConnectionBehaviour());
        endpointBehaviour.setCode(PEGACORN_ENDPOINT_IS_CLIENT);
        endpointBehaviour.setDisplay("Endpoint Acts as a Client");
        return(endpointBehaviour);
    }
}
