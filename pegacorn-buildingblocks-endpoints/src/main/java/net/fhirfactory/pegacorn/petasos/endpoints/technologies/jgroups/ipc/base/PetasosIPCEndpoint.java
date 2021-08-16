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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.base;

import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.components.capabilities.hl7v2tasks.A19QueryCapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.components.capabilities.hl7v2tasks.A19QueryTask;
import net.fhirfactory.pegacorn.components.capabilities.hl7v2tasks.A19QueryTaskOutcome;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class PetasosIPCEndpoint extends JGroupsPetasosEndpointBase {

    @Produce
    private ProducerTemplate camelProducer;

    //
    // Constructor
    //

    public PetasosIPCEndpoint(){
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

    //
    // Receive Messages (via RPC invocations)
    //

    protected abstract InterProcessingPlantHandoverResponsePacket injectMessageIntoRoute(InterProcessingPlantHandoverPacket handoverPacket);

    public InterProcessingPlantHandoverResponsePacket receiveIPCMessage(InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".receiveIPCMessage(): Entry, handoverPacket->{}",handoverPacket);
        InterProcessingPlantHandoverResponsePacket response = injectMessageIntoRoute(handoverPacket);
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
        return(interProcessingPlantHandoverResponsePacket);
    }


    //
    // Message Senders
    //

    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(Address targetAddress, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".sendIPCMessage(): Entry, targetAddress->{}", targetAddress);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = handoverPacket;
            classSet[0] = InterProcessingPlantHandoverPacket.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            InterProcessingPlantHandoverResponsePacket response = getRPCDispatcher().callRemoteMethod(targetAddress, "receiveIPCMessage", objectSet, classSet, requestOptions);
            getLogger().debug(".sendIPCMessage(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".sendIPCMessage(): Error (NoSuchMethodException) ->{}", e.getMessage());
            InterProcessingPlantHandoverResponsePacket response = new InterProcessingPlantHandoverResponsePacket();
            response.setActivityID(handoverPacket.getActivityID());
            response.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_SEND_FAILURE);
            response.setStatusReason("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".sendIPCMessage: Error (GeneralException) ->{}", e.getMessage());
            InterProcessingPlantHandoverResponsePacket response = new InterProcessingPlantHandoverResponsePacket();
            response.setActivityID(handoverPacket.getActivityID());
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
        List<PetasosAdapterAddress> memberAdapterSetForService = getTargetMemberAdapterSetForService(endpointServiceName);
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

    //
    // ****Tactical****
    // A19 Query Task Execution / Capability Utilisation Services
    //

    public CapabilityUtilisationResponse executeTask(String capabilityProviderName, CapabilityUtilisationRequest a19QueryTask){
        getLogger().debug(".requestA19Query(): Entry, capabilityProviderName->{}, a19QueryTask->{}", capabilityProviderName, a19QueryTask);
        Address targetAddress = getCandidateIPCTargetAddress(capabilityProviderName);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = a19QueryTask;
            classSet[0] = CapabilityUtilisationRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            CapabilityUtilisationResponse response = getRPCDispatcher().callRemoteMethod(targetAddress, "request19QueryHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".requestA19Query(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".sendIPCMessage(): Error (NoSuchMethodException) ->{}", e.getMessage());
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setAssociatedRequestID(a19QueryTask.getRequestID());
            response.setSuccessful(false);
            return(response);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".sendIPCMessage: Error (GeneralException) ->{}", e.getMessage());
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setAssociatedRequestID(a19QueryTask.getRequestID());
            response.setSuccessful(false);
            return(response);
        }
    }

    public CapabilityUtilisationResponse request19QueryHandler(CapabilityUtilisationRequest a19QueryTask){
        getLogger().debug(".request19QueryHandler(): Entry, a19QueryTask->{}", a19QueryTask);
        CapabilityUtilisationResponse response = getProcessingPlantInterface().executeTask(a19QueryTask);
        getLogger().debug(".request19QueryHandler(): Exit, response->{}", response);
        return(response);
    }

    public boolean capabilityProviderIsInScope(String capabilityProviderServiceName){
        List<String> memberSetBasedOnService = getClusterMemberSetBasedOnService(capabilityProviderServiceName);
        if(memberSetBasedOnService.isEmpty()){
            return(false);
        }
        for(String currentName: memberSetBasedOnService){
            if(isWithinScopeBasedOnChannelName(currentName)){
                return(true);
            }
        }
        return(false);
    }

    //
    // Getters (and Setters)
    //


    public ProducerTemplate getCamelProducer() {
        return camelProducer;
    }
}
