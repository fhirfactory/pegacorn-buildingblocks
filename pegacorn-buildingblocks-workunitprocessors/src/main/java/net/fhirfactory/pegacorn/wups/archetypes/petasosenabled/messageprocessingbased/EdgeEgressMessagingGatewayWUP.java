/*
 * Copyright (c) 2020 MAHun
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

package net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased;

import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCClusteredServerTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPTemplate;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;

import javax.inject.Inject;

public abstract class EdgeEgressMessagingGatewayWUP extends GenericMessageBasedWUPTemplate {
    private IPCServerTopologyEndpoint associatedEgressTopologyEndpoint;

    public EdgeEgressMessagingGatewayWUP() {
        super();
    }

    protected abstract String specifyEgressInterfaceName();
    protected abstract IPCInterfaceDefinition specifyEgressInterfaceDefinition();

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    protected PegacornCommonInterfaceNames getInterfaceNames(){
        return(interfaceNames);
    }

    @Override
    protected WUPArchetypeEnum specifyWUPArchetype(){
        return(WUPArchetypeEnum.WUP_NATURE_MESSAGE_EXTERNAL_EGRESS_POINT);
    }

    @Override
    protected MessageBasedWUPEndpoint specifyIngresEndpoint(){
        getLogger().debug(".specifyIngresTopologyEndpoint(): Entry");
        MessageBasedWUPEndpoint ingresEndpoint = new MessageBasedWUPEndpoint();
        ingresEndpoint.setFrameworkEnabled(true);
        ingresEndpoint.setEndpointSpecification(this.getNameSet().getEndPointWUPIngres());
        getLogger().debug(".specifyIngresTopologyEndpoint(): Exit");
        return(ingresEndpoint);
    }

    @Override
    protected boolean getUsesWUPFrameworkGeneratedEgressEndpoint() {
        return(false);
    }

    /**
     * Derive the Ingres Topology Endpoint
     */
    protected void assignEgressTopologyEndpoint(){
        getLogger().debug(".assignIngresTopologyEndpoint(): Entry");
        IPCServerTopologyEndpoint endpoint = deriveAssociatedTopologyEndpoint(specifyEgressInterfaceName(), specifyEgressInterfaceDefinition());
        if(endpoint != null){
            this.associatedEgressTopologyEndpoint = endpoint;
        } else {
            throw(new RuntimeException("Cannot resolve appropriate Ingres Interface to bind to!"));
        }
        getLogger().debug(".assignIngresTopologyEndpoint(): Exit, endpoint->{}", endpoint);
    }

    public IPCServerTopologyEndpoint getAssociatedEgressTopologyEndpoint() {
        return associatedEgressTopologyEndpoint;
    }

    public void setAssociatedEgressTopologyEndpoint(IPCServerTopologyEndpoint associatedEgressTopologyEndpoint) {
        this.associatedEgressTopologyEndpoint = associatedEgressTopologyEndpoint;
    }

}
