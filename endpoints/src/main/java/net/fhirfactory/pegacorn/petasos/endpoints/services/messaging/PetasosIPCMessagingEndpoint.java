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
package net.fhirfactory.pegacorn.petasos.endpoints.services.messaging;

import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.JGroupsIntegrationPointBase;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PetasosIPCMessagingEndpoint extends JGroupsIntegrationPointBase {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosIPCMessagingEndpoint.class);

    @Produce
    private ProducerTemplate camelProducer;

    //
    // Constructor(s)
    //

    public PetasosIPCMessagingEndpoint(){
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

    //
    // Getters and Setters
    //

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }


    protected ProducerTemplate getCamelProducer() {
        return camelProducer;
    }

    //
    // Endpoint Specifications
    //

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getPetasosIPCMessagingEndpointName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.JGROUPS_INTEGRATION_POINT);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlant().getMeAsASoftwareComponent().getPetasosIPCStackConfigFile());
    }

    @Override
    protected String specifyJGroupsClusterName() {
        return (getComponentNameUtilities().getPetasosIpcMessagingGroupName());
    }

    @Override
    protected void addIntegrationPointToJGroupsIntegrationPointSet() {
        getJgroupsIPSet().setPetasosMessagingServicesEndpoint(getJGroupsIntegrationPoint());
    }

    @Override
    protected String specifySubsystemParticipantName() {
        return (getProcessingPlant().getSubsystemParticipantName());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_MESSAGING_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_IPC);
    }

    //
    // Processing Plant check triggered by JGroups Cluster membership change
    //

    @Override
    protected void doIntegrationPointBusinessFunctionCheck(JGroupsIntegrationPointSummary integrationPointSummary, boolean isRemoved, boolean isAdded) {

    }

    //
    // Local Message Route Injection
    //

    protected InterProcessingPlantHandoverResponsePacket injectMessageIntoRoute(InterProcessingPlantHandoverPacket handoverPacket) {
        getLogger().debug(".injectMessageIntoRoute(): Entry, handoverPacket->{}", handoverPacket);
        InterProcessingPlantHandoverResponsePacket response =
                (InterProcessingPlantHandoverResponsePacket)getCamelProducer().sendBody(getIPCComponentNames().getInterZoneIPCReceiverRouteEndpointName(), ExchangePattern.InOut, handoverPacket);
        getLogger().debug(".injectMessageIntoRoute(): Exit, response->{}", response);
        return(response);
    }

    //
    // Receive Message
    //

    public InterProcessingPlantHandoverResponsePacket receiveIPCMessage(InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".receiveIPCMessage(): Entry, handoverPacket->{}",handoverPacket);
        InterProcessingPlantHandoverResponsePacket response = injectMessageIntoRoute(handoverPacket);
        getMetricsAgent().incrementInternalReceivedMessageCount();
        getLogger().debug(".receiveIPCMessage(): Exit, response->{}",response);
        return(response);
    }

    //
    // Send Messages (via RPC invocations)
    //
    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(String targetParticipantServiceName, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".sendIPCMessage(): Entry, targetParticipantServiceName->{}, handoverPacket->{}", targetParticipantServiceName, handoverPacket);
        Address targetServiceAddress = getCandidateIPCTargetAddress(targetParticipantServiceName);
        getLogger().debug(".sendIPCMessage(): Got an address, targetServiceAddress->{}", targetServiceAddress);
        InterProcessingPlantHandoverResponsePacket interProcessingPlantHandoverResponsePacket = sendIPCMessage(targetServiceAddress, handoverPacket);
        getMetricsAgent().incrementInternalMessageDistributionCount();
        getMetricsAgent().incrementInternalMessageDistributionCount(targetParticipantServiceName);
        getLogger().debug(".sendIPCMessage(): Exit, interProcessingPlantHandoverResponsePacket->{}", interProcessingPlantHandoverResponsePacket);
        return(interProcessingPlantHandoverResponsePacket);
    }


    //
    // Message Senders
    //

    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(Address targetAddress, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".sendIPCMessage(): Entry, targetAddress->{}", targetAddress);
        if(getLogger().isInfoEnabled()){
            getLogger().info(".sendIPCMessage(): Forwarding Task To->{}", targetAddress);
        }
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = handoverPacket;
            classSet[0] = InterProcessingPlantHandoverPacket.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            getLogger().trace(".sendIPCMessage(): Message.SEND: targetAddress->{}, handoverPacket->{}", targetAddress, handoverPacket);
            InterProcessingPlantHandoverResponsePacket response = null;
            synchronized(getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "receiveIPCMessage", objectSet, classSet, requestOptions);
            }
            getLogger().trace(".sendIPCMessage(): Message.SEND.RESPONSE: response->{}", response);
            if(getLogger().isInfoEnabled()){
                getLogger().info(".sendIPCMessage(): Forwarding of Task Complete");
            }
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".sendIPCMessage(): Error (NoSuchMethodException) ->{}", e.getMessage());
            InterProcessingPlantHandoverResponsePacket response = new InterProcessingPlantHandoverResponsePacket();
            response.setActionableTaskId(handoverPacket.getActionableTask().getTaskId());
            response.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_SEND_FAILURE);
            response.setStatusReason("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".sendIPCMessage: Error (GeneralException) ->{}", e.getMessage());
            InterProcessingPlantHandoverResponsePacket response = new InterProcessingPlantHandoverResponsePacket();
            response.setActionableTaskId(handoverPacket.getActionableTask().getTaskId());
            response.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_SEND_FAILURE);
            response.setStatusReason("Error (GeneralException)" + e.getMessage());
            return(response);
        }
    }

    public List<Address> getIPCTargetAddressSet(String endpointServiceName){
        getLogger().debug(".getIPCTargetAddressSet(): Entry, endpointServiceName->{}", endpointServiceName);
        List<Address> endpointAddressSet = new ArrayList<>();
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getIPCTargetAddressSet(): Exit, endpointServiceName is empty");
            return(endpointAddressSet);
        }
        List<PetasosAdapterAddress> memberAdapterSetForService = getTargetMemberAdapterSetForSubsystemName(endpointServiceName);
        for(PetasosAdapterAddress currentMember: memberAdapterSetForService){
            Address currentMemberAddress = currentMember.getJGroupsAddress();
            if(currentMemberAddress != null){
                endpointAddressSet.add(currentMemberAddress);
            }
        }
        getLogger().debug(".getIPCTargetAddressSet(): Exit, endpointAddressSet->{}", endpointAddressSet);
        return(endpointAddressSet);
    }

    public Address getCandidateIPCTargetAddress(String endpointServiceName){
        getLogger().debug(".getCandidateIPCTargetAddress(): Entry, endpointServiceName->{}", endpointServiceName);
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getCandidateIPCTargetAddress(): Exit, endpointServiceName is empty");
            return(null);
        }
        List<Address> endpointAddressSet = getIPCTargetAddressSet(endpointServiceName);
        if(endpointAddressSet.isEmpty()){
            getLogger().debug(".getCandidateIPCTargetAddress(): Exit, endpointAddressSet is empty");
            return(null);
        }
        Address endpointJGroupsAddress = endpointAddressSet.get(0);
        getLogger().debug(".getCandidateIPCTargetAddress(): Exit, selected address->{}", endpointJGroupsAddress);
        return(endpointJGroupsAddress);
    }
}
