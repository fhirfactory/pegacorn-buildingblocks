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

import java.util.HashSet;
import java.util.Set;

import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelToken;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPTemplate;

public abstract class InteractIngresMessagingGatewayWUP extends GenericMessageBasedWUPTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(InteractIngresMessagingGatewayWUP.class);

    private GenericMessageBasedWUPEndpoint ingresEndpoint;

    public InteractIngresMessagingGatewayWUP() {
        super();
//        LOG.debug(".MessagingIngresGatewayWUP(): Entry, Default constructor");
    }

    protected abstract String specifyIngresTopologyEndpointName();
    protected abstract String specifyIngresEndpointVersion();

    @Override
    protected WUPArchetypeEnum specifyWUPArchetype(){
        return(WUPArchetypeEnum.WUP_NATURE_MESSAGE_EXTERNAL_INGRES_POINT);
    }

    @Override
    protected GenericMessageBasedWUPEndpoint specifyEgressTopologyEndpoint(){
        getLogger().debug(".specifyEgressTopologyEndpoint(): Entry");
        GenericMessageBasedWUPEndpoint egressEndpoint = new GenericMessageBasedWUPEndpoint();
        egressEndpoint.setFrameworkEnabled(true);
        egressEndpoint.setEndpointSpecification(this.getNameSet().getEndPointWUPEgress());
        getLogger().debug(".specifyEgressTopologyEndpoint(): Exit");
        return(egressEndpoint);
    }

    /**
     * The Ingres Message Gateway doesn't subscribe to ANY topics as it receives it's 
     * input from an external system.
     * 
     * @return An empty Set<TopicToken>
     */
    @Override
    protected Set<DataParcelToken> specifySubscriptionTopics() {
        HashSet<DataParcelToken> subTopics = new HashSet<DataParcelToken>();
        return(subTopics);
    }

    public GenericMessageBasedWUPEndpoint getIngresEndpoint() {
        return ingresEndpoint;
    }

    public void setIngresEndpoint(GenericMessageBasedWUPEndpoint ingresEndpoint) {
        this.ingresEndpoint = ingresEndpoint;
    }
}
