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

import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPServerAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.answer.StandardEdgeAnswerHTTPEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.answer.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.ask.StandardEdgeAskHTTPEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.datatypes.JGroupsAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.http.InteractHTTPClientTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.http.InteractHTTPServerTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.InteractMLLPClientEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.InteractMLLPServerEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.adapters.MLLPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.adapters.MLLPServerAdapter;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.codesystems.EndpointConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;

@ApplicationScoped
public class EndpointFactory {
    private static final Logger LOG = LoggerFactory.getLogger(EndpointFactory.class);

    @Inject
    private EndpointIdentifierFactory identifierFactory;

    @Inject
    private EndpointMetaTagFactory metaTagFactory;

    @Inject
    private EndpointConnectionTypeCodeFactory connectionTypeCodeFactory;

    //
    // Business Methods
    //

    protected Endpoint newEndpointFromIPCTopologyEndpoint(IPCTopologyEndpoint topologyEndpoint) {
        Endpoint endpoint = new Endpoint();

        //
        // Add the Identifier
        Identifier identifier = getIdentifierFactory().newEndpointIdentifier(topologyEndpoint.getComponentID().getId());
        endpoint.addIdentifier(identifier);

        //
        // Set the (default) Status
        endpoint.setStatus(Endpoint.EndpointStatus.ACTIVE);

        //
        // Set the name
        endpoint.setName(topologyEndpoint.getComponentID().getDisplayName());

        //
        // Set the period
        Period activePeriod = new Period();
        activePeriod.setStart(Date.from(topologyEndpoint.getComponentID().getIdValidityStartInstant()));
        endpoint.setPeriod(activePeriod);

        //
        // All Done!
        return (endpoint);
    }

    public Endpoint newEndpoint(InteractMLLPClientEndpoint mllpClientEndpoint, MLLPClientAdapter clientAdapter) {
        Endpoint endpoint = newEndpointFromIPCTopologyEndpoint(mllpClientEndpoint);
        //
        // Set URL
        String hostName = clientAdapter.getHostName();
        Integer portNumber = clientAdapter.getPortNumber();
        String url = "mllp://" + hostName + ":" + portNumber;
        endpoint.setAddress(url);
        //
        // Set Type
        Coding endpointConnectionType = new Coding();
        endpointConnectionType.setCode(EndpointConnectionType.HL7V2MLLP.toCode());
        endpointConnectionType.setDisplay(EndpointConnectionType.HL7V2MLLP.getDisplay());
        endpointConnectionType.setSystem(EndpointConnectionType.HL7V2MLLP.getSystem());
        endpoint.setConnectionType(endpointConnectionType);
        //
        // Set Endpoint Behaviour
        Coding endpointIsClient = getMetaTagFactory().newEndpointClientTag();
        endpoint.getMeta().addTag(endpointIsClient);
        //
        // All Done!
        return (endpoint);
    }

    public Endpoint newEndpoint(InteractMLLPServerEndpoint mllpServerEndpoint, MLLPServerAdapter serverAdapter) {
        Endpoint endpoint = newEndpointFromIPCTopologyEndpoint(mllpServerEndpoint);
        //
        // Set URL
        String hostName = null;
        Integer portNumber = null;
        if (serverAdapter.getServiceDNSName() != null) {
            hostName = serverAdapter.getServiceDNSName();
        } else {
            hostName = serverAdapter.getHostName();
        }
        if (serverAdapter.getServicePortValue() != null) {
            portNumber = serverAdapter.getServicePortValue();
        } else {
            portNumber = serverAdapter.getPortNumber();
        }
        String url = "mllp://" + hostName + ":" + portNumber;
        endpoint.setAddress(url);
        //
        // Set Type
        Coding endpointConnectionType = new Coding();
        endpointConnectionType.setCode(EndpointConnectionType.HL7V2MLLP.toCode());
        endpointConnectionType.setDisplay(EndpointConnectionType.HL7V2MLLP.getDisplay());
        endpointConnectionType.setSystem(EndpointConnectionType.HL7V2MLLP.getSystem());
        endpoint.setConnectionType(endpointConnectionType);
        //
        // Set Default PortNumber, Hostname (in case the above URL points to the Service Endpoint)
        Coding endpointHostnameTag = getMetaTagFactory().newEndpointDefaultHostName(serverAdapter.getHostName());
        Coding endpointPortNumberTag = getMetaTagFactory().newEndpointDefaultPortNumber(Integer.toString(serverAdapter.getPortNumber()));
        endpoint.getMeta().addTag(endpointHostnameTag);
        endpoint.getMeta().addTag(endpointPortNumberTag);
        //
        // Set Endpoint Behaviour
        Coding endpointIsServer = getMetaTagFactory().newEndpointServerTag();
        endpoint.getMeta().addTag(endpointIsServer);
        //
        // All Done!
        return (endpoint);
    }

    public Endpoint newEndpoint(StandardEdgeAskHTTPEndpoint askHTTPEndpoint, HTTPClientAdapter clientAdapter){
        Endpoint endpoint = newEndpointFromIPCTopologyEndpoint(askHTTPEndpoint);
        Endpoint enhancedEnpoint = newHTTPClientBasedEndpoint(endpoint, clientAdapter);
        return(enhancedEnpoint);
    }

    public Endpoint newEndpoint(InteractHTTPClientTopologyEndpoint httpEndpoint, HTTPClientAdapter clientAdapter){
        Endpoint endpoint = newEndpointFromIPCTopologyEndpoint(httpEndpoint);
        Endpoint enhancedEnpoint = newHTTPClientBasedEndpoint(endpoint, clientAdapter);
        return(enhancedEnpoint);
    }

    protected Endpoint newHTTPClientBasedEndpoint(Endpoint endpoint, HTTPClientAdapter clientAdapter){

        //
        // Set URL
        String hostName = clientAdapter.getHostName();
        Integer portNumber = clientAdapter.getPortNumber();
        String httpType = null;
            if(clientAdapter.isEncrypted())

        {
            httpType = "https";
        } else

        {
            httpType = "http";
        }

        String url = httpType + "://" + hostName + ":" + portNumber;
            endpoint.setAddress(url);
        //
        // Set Type
        Coding endpointConnectionType = new Coding();
            endpointConnectionType.setCode(EndpointConnectionType.HL7FHIRREST.getSystem());
            endpointConnectionType.setDisplay(EndpointConnectionType.HL7FHIRREST.getDisplay());
            endpointConnectionType.setSystem(EndpointConnectionType.HL7FHIRREST.getSystem());
            endpoint.setConnectionType(endpointConnectionType);
        //
        // Set Endpoint Behaviour
        Coding endpointIsClient = getMetaTagFactory().newEndpointClientTag();
            endpoint.getMeta().

        addTag(endpointIsClient);
        //
        // All Done!
            return(endpoint);
    }

    public Endpoint newEndpoint(StandardEdgeAnswerHTTPEndpoint answerHTTPEndpoint, HTTPServerAdapter serverAdapter){
        Endpoint endpoint = newEndpointFromIPCTopologyEndpoint(answerHTTPEndpoint);
        Endpoint enhancedEndpoint = newHTTPServerBasedEndpoint(endpoint, serverAdapter);
        //
        // All Done!
        return(enhancedEndpoint);
    }

    public Endpoint newEndpoint(InteractHTTPServerTopologyEndpoint httpEndpoint, HTTPServerAdapter httpServerAdapter){
        Endpoint endpoint = newEndpointFromIPCTopologyEndpoint(httpEndpoint);
        Endpoint enhancedEndpoint = newHTTPServerBasedEndpoint(endpoint, httpServerAdapter);
        //
        // All Done!
        return(enhancedEndpoint);
    }

    protected Endpoint newHTTPServerBasedEndpoint(Endpoint endpoint, HTTPServerAdapter serverAdapter ){
        // Set URL
        String hostName = null;
        Integer portNumber = null;
        if(serverAdapter.getServiceDNSName() != null){
            hostName = serverAdapter.getServiceDNSName();
        } else {
            hostName = serverAdapter.getHostName();
        }
        if(serverAdapter.getServicePortValue() != null){
            portNumber = serverAdapter.getServicePortValue();
        } else {
            portNumber = serverAdapter.getPortNumber();
        }
        String httpType = null;
        if(serverAdapter.isEncrypted()){
            httpType = "https";
        } else {
            httpType = "http";
        }
        String url = httpType+"://"+hostName+":"+portNumber;
        endpoint.setAddress(url);
        //
        // Set Type
        Coding endpointConnectionType = new Coding();
        endpointConnectionType.setCode(EndpointConnectionType.HL7FHIRREST.toCode());
        endpointConnectionType.setDisplay(EndpointConnectionType.HL7FHIRREST.getDisplay());
        endpointConnectionType.setSystem(EndpointConnectionType.HL7FHIRREST.getSystem());
        endpoint.setConnectionType(endpointConnectionType);
        //
        // Set Default PortNumber, Hostname (in case the above URL points to the Service Endpoint)
        Coding endpointHostnameTag = getMetaTagFactory().newEndpointDefaultHostName(serverAdapter.getHostName());
        Coding endpointPortNumberTag = getMetaTagFactory().newEndpointDefaultPortNumber(Integer.toString(serverAdapter.getPortNumber()));
        endpoint.getMeta().addTag(endpointHostnameTag);
        endpoint.getMeta().addTag(endpointPortNumberTag);
        //
        // Set Endpoint Behaviour
        Coding endpointIsServer = getMetaTagFactory().newEndpointServerTag();
        endpoint.getMeta().addTag(endpointIsServer);
        //
        // All Done!
        return(endpoint);
    }

    public Endpoint newJGroupsEndpoint(StandardEdgeIPCEndpoint jgroupsEndpoint, JGroupsAdapter jGroupsAdapter){
        Endpoint endpoint = newEndpointFromIPCTopologyEndpoint(jgroupsEndpoint);
        //
        // Set URL
        String groupName = jGroupsAdapter.getGroupName();
        String fileName = jgroupsEndpoint.getConfigurationFileName();
        String url = "jgroups("+fileName+"):"+groupName;
        endpoint.setAddress(url);
        //
        // Set Type
        Coding endpointConnectionType = getConnectionTypeCodeFactory().newPegacornEndpointJGroupsConnectionCodeSystem(jgroupsEndpoint.getEndpointType());
        endpoint.setConnectionType(endpointConnectionType);
        //
        // Set Endpoint Behaviour
        Coding endpointIsClient = getMetaTagFactory().newEndpointClientTag();
        endpoint.getMeta().addTag(endpointIsClient);
        //
        // All Done!
        return(endpoint);
    }

    //
    // Getters (and Setters)
    //

    protected EndpointIdentifierFactory getIdentifierFactory(){
        return(this.identifierFactory);
    }

    protected EndpointMetaTagFactory getMetaTagFactory(){
        return(this.metaTagFactory);
    }

    protected EndpointConnectionTypeCodeFactory getConnectionTypeCodeFactory(){
        return(this.connectionTypeCodeFactory);
    }
}
