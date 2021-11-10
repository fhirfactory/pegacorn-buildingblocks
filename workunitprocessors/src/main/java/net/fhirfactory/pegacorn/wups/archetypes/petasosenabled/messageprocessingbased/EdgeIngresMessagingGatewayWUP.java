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

import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.ipc.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapterDefinition;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPTemplate;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class EdgeIngresMessagingGatewayWUP extends GenericMessageBasedWUPTemplate {

    private IPCServerTopologyEndpoint associatedIngresTopologyEndpoint;

    public EdgeIngresMessagingGatewayWUP() {
        super();
    }

    protected abstract String specifyIngresInterfaceName();
    protected abstract IPCAdapterDefinition specifyIngresInterfaceDefinition();

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    protected PegacornCommonInterfaceNames getInterfaceNames(){
        return(interfaceNames);
    }

    @Override
    protected WUPArchetypeEnum specifyWUPArchetype(){
        return(WUPArchetypeEnum.WUP_NATURE_MESSAGE_EXTERNAL_INGRES_POINT);
    }

    @Override
    protected MessageBasedWUPEndpoint specifyEgressEndpoint(){
        getLogger().debug(".specifyEgressTopologyEndpoint(): Entry");
        MessageBasedWUPEndpoint egressEndpoint = new MessageBasedWUPEndpoint();
        egressEndpoint.setFrameworkEnabled(true);
        egressEndpoint.setEndpointSpecification(this.getNameSet().getEndPointWUPEgress());
        getLogger().debug(".specifyEgressTopologyEndpoint(): Exit");
        return(egressEndpoint);
    }

    @Override
    protected boolean getUsesWUPFrameworkGeneratedIngresEndpoint() {
        return(false);
    }


    /**
     * The Ingres Message Gateway doesn't subscribe to ANY topics as it receives it's 
     * input from an external system.
     * 
     * @return An empty Set<TopicToken>
     */
    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        List<DataParcelManifest> subTopics = new ArrayList<>();
        return(subTopics);
    }

    /**
     * Derive the Ingres Topology Endpoint
     */
    protected void assignIngresTopologyEndpoint(){
        getLogger().debug(".assignIngresTopologyEndpoint(): Entry");
        IPCServerTopologyEndpoint endpoint = deriveAssociatedTopologyEndpoint(specifyIngresInterfaceName(), specifyIngresInterfaceDefinition());
        if(endpoint != null){
            this.associatedIngresTopologyEndpoint = endpoint;
        } else {
            throw(new RuntimeException("Cannot resolve appropriate Ingres Interface to bind to!"));
        }
        getLogger().debug(".assignIngresTopologyEndpoint(): Exit, endpoint->{}", endpoint);
    }

    public IPCServerTopologyEndpoint getAssociatedIngresTopologyEndpoint() {
        return associatedIngresTopologyEndpoint;
    }

    public void setAssociatedIngresTopologyEndpoint(IPCServerTopologyEndpoint associatedIngresTopologyEndpoint) {
        this.associatedIngresTopologyEndpoint = associatedIngresTopologyEndpoint;
    }
}
