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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.ipc.base;

import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

public abstract class PetasosIPCEndpoint extends JGroupsPetasosEndpointBase {


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
        getLogger().info(".sendIPCMessage(): Got an address, targetServiceAddress->{}", targetServiceAddress);
        InterProcessingPlantHandoverResponsePacket interProcessingPlantHandoverResponsePacket = sendIPCMessage(targetServiceAddress, handoverPacket);
        return(interProcessingPlantHandoverResponsePacket);
    }


    //
    // Message Senders
    //

    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(Address targetAddress, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().info(".sendIPCMessage(): Entry, targetAddress->{}", targetAddress);
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


    //
    // Getters (and Setters)
    //



}
