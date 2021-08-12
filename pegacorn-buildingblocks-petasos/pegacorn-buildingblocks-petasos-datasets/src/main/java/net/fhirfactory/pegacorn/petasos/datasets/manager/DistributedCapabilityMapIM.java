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
package net.fhirfactory.pegacorn.petasos.datasets.manager;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNode;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNodeRegistration;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNodeSet;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityRoutingEndpoint;
import net.fhirfactory.pegacorn.core.model.tasks.base.PetasosCapabilityCommonName;
import net.fhirfactory.pegacorn.petasos.datasets.cache.DistributedCapabilityMapDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class DistributedCapabilityMapIM {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedCapabilityMapIM.class);

    @Inject
    private DistributedCapabilityMapDM capabilityMapDM;

    public void addLocalCapabilityNode(PetasosCapabilityDeliveryNode localNode){
        LOG.info(".addLocalCapabilityNode(): Entry, localNode->{}", localNode);
        capabilityMapDM.addLocalCapabilityNode(localNode);
        LOG.info(".addLocalCapabilityNode(): Exit");
    }

    public void removeLocalCapabilityNode(PetasosCapabilityDeliveryNode localNode){
        LOG.info(".removeLocalCapabilityNode(): Entry, localNode->{}", localNode);
        capabilityMapDM.removeLocalCapabilityNode(localNode);
        LOG.info(".removeLocalCapabilityNode(): Exit");
    }

    public void removeLocalCapabilityNode(TopologyNodeFDNToken localNodeID){
        LOG.info(".removeLocalCapabilityNode(): Entry, localNodeID->{}", localNodeID);
        capabilityMapDM.removeLocalCapabilityNode(localNodeID);
        LOG.info(".removeLocalCapabilityNode(): Exit");
    }

    public PetasosCapabilityDeliveryNodeSet getLocalCapabilityNodeSet(){
        LOG.info(".getLocalCapabilityNodeSet(): Entry");
        PetasosCapabilityDeliveryNodeSet nodeSet = capabilityMapDM.getLocalCapabilityNodeSet();
        LOG.info(".getLocalCapabilityNodeSet(): Exit");
        return(nodeSet);
    }

    public List<PetasosCapabilityDeliveryNodeRegistration> registerCapabilityDeliveryNodeSet(PetasosCapabilityDeliveryNodeSet nodeSet) {
        LOG.info(".registerCapabilityDeliveryNodeSet(): Entry, nodeSet->{}", nodeSet);
        List<PetasosCapabilityDeliveryNodeRegistration> registrationSet = capabilityMapDM.registerCapabilityDeliveryNodeSet(nodeSet);
        LOG.debug(".registerCapabilityDeliveryNodeSet(): Exit, registrationSet->{}", registrationSet);
        return (registrationSet);
    }

    public PetasosCapabilityDeliveryNodeRegistration registerCapabilityDeliveryNode(PetasosCapabilityDeliveryNode ptce) {
        LOG.info(".registerCapabilityDeliveryNode(): Entry, ptce->{}", ptce);
        PetasosCapabilityDeliveryNodeRegistration petasosCapabilityDeliveryNodeRegistration = capabilityMapDM.registerCapabilityDeliveryNode(ptce);
        LOG.debug(".registerCapabilityDeliveryNode(): Exit, registration->{}", petasosCapabilityDeliveryNodeRegistration);
        return (petasosCapabilityDeliveryNodeRegistration);
    }

    public void unregisterCapabilityDeliveryNode(String petasosRoutingEndpointName) {
        LOG.info(".unregisterCapabilityDeliveryNode(): Entry, petasosRoutingEndpointName->{}", petasosRoutingEndpointName);
        capabilityMapDM.unregisterCapabilityDeliveryNode(petasosRoutingEndpointName);
        LOG.debug(".unregisterCapabilityDeliveryNode(): Exit");
    }

    public PetasosCapabilityDeliveryNodeRegistration getCapabilityDeliveryNodeRegistration(String deliveryEndpointName) {
        LOG.debug(".getCapabilityDeliveryNodeRegistration(): Entry, deliveryEndpointName->{}", deliveryEndpointName);
        PetasosCapabilityDeliveryNodeRegistration registration = capabilityMapDM.getCapabilityDeliveryNodeRegistration(deliveryEndpointName);
        LOG.debug(".getCapabilityDeliveryNodeRegistration(): Exit, registration->{}", registration);
        return (registration);
    }

    public List<PetasosCapabilityDeliveryNodeRegistration> getDeliveryNodeRegistrationSet(PetasosCapabilityCommonName capabilityName) {
        LOG.info(".getDeliveryNodeRegistrationSet(): Entry, capabilityName->{}", capabilityName);
        List<PetasosCapabilityDeliveryNodeRegistration> engineRegistrations = capabilityMapDM.getDeliveryNodeRegistrationSet(capabilityName);
        LOG.debug(".getDeliveryNodeRegistrationSet(): Exit");
        return (engineRegistrations);
    }

    public boolean isCapabilityDeliveryNodeRegistered(String engineRoutingEndpointName) {
        LOG.debug(".isCapabilityDeliveryNodeRegistered(): Entry, engineRoutingEndpointName->{}", engineRoutingEndpointName);
        boolean taskExecutionEngineRegistered = capabilityMapDM.isCapabilityDeliveryNodeRegistered(engineRoutingEndpointName);
        LOG.debug(".isCapabilityDeliveryNodeRegistered(): Exit, returning->{}", taskExecutionEngineRegistered);
        return (taskExecutionEngineRegistered);
    }

    public boolean isCapabilityDeliveryNodeAvailableForCapability(PetasosCapabilityCommonName commonName) {
        LOG.debug(".isCapabilityDeliveryNodeAvailableForCapability(): Entry, commonName->{}", commonName);
        boolean engineAvailableToSupportCapability = capabilityMapDM.isCapabilityDeliveryNodeAvailableForCapability(commonName);
        LOG.debug(".isCapabilityDeliveryNodeAvailableForCapability(): Exit, returning->{}", engineAvailableToSupportCapability);
        return (engineAvailableToSupportCapability);
    }

    //
    // Routing Endpoint Map
    //

    public void addCapabilityRoutingEndpoint(PetasosCapabilityRoutingEndpoint routingEndpoint){
        LOG.debug(".addCapabilityRoutingEndpoint(): Entry, routingEndpoint->{}", routingEndpoint);
        capabilityMapDM.addCapabilityRoutingEndpoint(routingEndpoint);
        LOG.debug(".addCapabilityROutingEndpoint(): Exit");
    }

    public void removeCapabilityRoutingEndpoint(PetasosCapabilityRoutingEndpoint routingEndpoint){
        LOG.debug(".removeCapabilityRoutingEndpoint(): Entry, routingEndpoint->{}", routingEndpoint);
        capabilityMapDM.removeCapabilityRoutingEndpoint(routingEndpoint);
        LOG.debug(".removeCapabilityRoutingEndpoint(): Exit");
    }
}
