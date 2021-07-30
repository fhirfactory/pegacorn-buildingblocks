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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.ipc;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointChannelScopeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.ipc.base.PetasosIPCEndpoint;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.IntraZoneIPCMessageSenderInterface;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.ExchangePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PetasosIntraZoneIPCEndpoint extends PetasosIPCEndpoint implements IntraZoneIPCMessageSenderInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosIntraZoneIPCEndpoint.class);

    public PetasosIntraZoneIPCEndpoint(){
        super();
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getFunctionNameIntraZoneJGroupsIPC());
    }

    @Override
    protected PetasosTopologyEndpointTypeEnum specifyIPCType() {
        return (PetasosTopologyEndpointTypeEnum.JGROUPS_INTRAZONE_SERVICE);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlantInterface().getProcessingPlantNode().getIntraZoneIPCStackConfigFile());
    }

    @Override
    protected PetasosEndpointIdentifier specifyEndpointID() {
        PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
        String endpointName = getJgroupsParticipantInformationService().getMyIntraZoneIPCEndpointName();
        endpointID.setEndpointName(endpointName);
        String endpointKey = getJgroupsParticipantInformationService().getMyIntraZoneIPCEndpointAddressName();
        endpointID.setEndpointChannelName(endpointKey);
        endpointID.setEndpointZone(getProcessingPlantInterface().getNetworkZone());
        endpointID.setEndpointSite(getProcessingPlantInterface().getDeploymentSite());
        endpointID.setEndpointGroup(getJgroupsParticipantInformationService().getIntraZoneIPCGroupName());
        String endpointAddress = "JGroups:" + endpointName + ":" + getJgroupsParticipantInformationService().getIntraZoneIPCGroupName();
        endpointID.setEndpointDetailedAddressName(endpointAddress);
        return (endpointID);
    }

    @Override
    protected String specifyEndpointServiceName() {
        return (getProcessingPlantInterface().getIPCServiceName());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENPOINT_PAYLOAD_PEGACORN_IPC);
    }

    @Override
    protected void resolveTopologyEndpoint() {
        setTopologyNode(getJgroupsParticipantInformationService().getMyIntraZoneTopologyEndpoint());
    }

    @Override
    protected PubSubParticipant specifyPubSubParticipant() {
        PubSubParticipant myIntraZoneParticipantRole = getJgroupsParticipantInformationService().getMyIntraZoneParticipantRole(getPetasosEndpoint());
        return (myIntraZoneParticipantRole);
    }

    @Override
    protected void registerWithCoreSubsystemPetasosEndpointsWatchdog() {
        getCoreSubsystemPetasosEndpointsWatchdog().setIntrazoneIPC(this.getPetasosEndpoint());
    }

    @Override
    protected InterProcessingPlantHandoverResponsePacket injectMessageIntoRoute(InterProcessingPlantHandoverPacket handoverPacket) {
        InterProcessingPlantHandoverResponsePacket response =
                (InterProcessingPlantHandoverResponsePacket)getCamelProducer().sendBody(getIPCComponentNames().getIntraZoneIPCReceiverRouteEndpointName(), ExchangePattern.InOut, handoverPacket);
        return(response);
    }

    @Override
    protected PetasosEndpointChannelScopeEnum specifyPetasosEndpointScope() {
        return (PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTRAZONE);
    }

    @Override
    public InterProcessingPlantHandoverResponsePacket sendIPCMessagePlease(String targetParticipantServiceName, InterProcessingPlantHandoverPacket handoverPacket) {
        InterProcessingPlantHandoverResponsePacket responsePacket = sendIPCMessage(targetParticipantServiceName, handoverPacket);
        return(responsePacket);
    }
}
