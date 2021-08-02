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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.discovery;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.*;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.discovery.base.PetasosOAMDiscoveryEndpoint;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PetasosInterZoneOAMDiscoveryEndpoint extends PetasosOAMDiscoveryEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosInterZoneOAMDiscoveryEndpoint.class);

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    public PetasosInterZoneOAMDiscoveryEndpoint(){
        super();
    }

    @Override
    protected PetasosEndpointIdentifier specifyEndpointID() {
        PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
        String endpointName = getJgroupsParticipantInformationService().getMyInterZoneOAMDiscoveryEndpointName();
        endpointID.setEndpointName(endpointName);
        String endpointKey = getJgroupsParticipantInformationService().getMyInterZoneOAMDiscoveryEndpointAddressName();
        endpointID.setEndpointAddressName(endpointKey);
        endpointID.setEndpointZone(getProcessingPlantInterface().getNetworkZone());
        endpointID.setEndpointSite(getProcessingPlantInterface().getDeploymentSite());
        endpointID.setEndpointGroup(getJgroupsParticipantInformationService().getInterZoneOAMGroupName());
        String endpointAddress = "JGroups:" + endpointName + ":" + getJgroupsParticipantInformationService().getInterZoneOAMGroupName();
        endpointID.setEndpointDetailedAddressName(endpointAddress);
        getLogger().info(".specifyEndpointID(): Exit, endpointID->{}", endpointID);
        return (endpointID);
    }

    @Override
    protected String specifyEndpointServiceName() {
        return (getProcessingPlantInterface().getIPCServiceName());
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getFunctionNameInterZoneJGroupsOAM());
    }

    @Override
    protected PetasosTopologyEndpointTypeEnum specifyIPCType() {
        return (PetasosTopologyEndpointTypeEnum.JGROUPS_INTERZONE_SERVICE);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlantInterface().getProcessingPlantNode().getInterZoneOAMStackConfigFile());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENPOINT_PAYLOAD_PEGACORN_OAM);
    }

    @Override
    protected void resolveTopologyEndpoint() {
        setTopologyNode(getJgroupsParticipantInformationService().getMyInterZoneTopologyEndpoint());
    }

    @Override
    public PetasosEndpointStatusEnum checkInterfaceStatus(PetasosEndpointIdentifier endpointID) {
        return null;
    }

    @Override
    protected PubSubParticipant specifyPubSubParticipant() {
        return (getJgroupsParticipantInformationService().getMyInterZoneParticipantRole());
    }

    @Override
    protected void registerWithCoreSubsystemPetasosEndpointsWatchdog() {
        getCoreSubsystemPetasosEndpointsWatchdog().setInterzoneOAMDiscovery(this.getPetasosEndpoint());
    }

    @Override
    protected PetasosEndpointChannelScopeEnum specifyPetasosEndpointScope() {
        return (PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERZONE);
    }
}