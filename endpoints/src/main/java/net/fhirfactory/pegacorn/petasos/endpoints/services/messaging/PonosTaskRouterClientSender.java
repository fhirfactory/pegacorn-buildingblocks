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

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskRepositoryServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.services.messaging.common.PonosTaskRouterClientCommon;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import net.fhirfactory.pegacorn.platform.edge.model.router.TaskRouterResponsePacket;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class PonosTaskRouterClientSender extends PonosTaskRouterClientCommon {
    private static final Logger LOG = LoggerFactory.getLogger(PonosTaskRouterClientSender.class);

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private PetasosTaskRepositoryServiceProviderNameInterface taskServiceProviderName;

    //
    // Constructor(s)
    //

    public PonosTaskRouterClientSender(){
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
        getJgroupsIPSet().setPetasosTaskRoutingReceiverEndpoint(getJGroupsIntegrationPoint());
    }

    @Override
    protected String specifySubsystemParticipantName() {
        return (getProcessingPlant().getSubsystemParticipantName());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_TASK_ROUTING_FORWARDER_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_TASK_ROUTING_FORWARDER);
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
    // Message Senders
    //

    public TaskRouterResponsePacket forwardTaskToRouter( PetasosActionableTask task){
        getLogger().debug(".forwardTaskToRouter(): Entry, task->{}", task);
        Address targetAddress = resolveTargetAddressForTaskHub();
        if(targetAddress == null){
            getLogger().error(".forwardTaskToRouter(): Cannot find candidate service address for {}: task->{}",taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName(),  task);
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate " + taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate " + taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            TaskRouterResponsePacket response = new TaskRouterResponsePacket();
            response.setRoutedTaskId(task.getTaskId());
            response.setSuccessorTaskId(null);
            response.setRoutingActivityInstant(Instant.now());
            response.setParticipantStatus(PetasosParticipantControlStatusEnum.PARTICIPANT_IS_IN_ERROR);
            response.setResponseCommentary("Cannot Find Ponos!!!!");
            return(response);
        }
        try {
            getLogger().trace(".forwardTaskToRouter(): [Construct RMI Method Arguments] Start");
            String sourceName = getProcessingPlant().getMeAsASoftwareComponent().getParticipantName();
            Object objectSet[] = new Object[]{sourceName, task};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            getLogger().trace(".forwardTaskToRouter(): [Construct RMI Method Arguments] Finish");
            getLogger().trace(".forwardTaskToRouter(): [Invoke RMI Method] Start");
            TaskRouterResponsePacket response = null;
            synchronized(getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "receiveTask", objectSet, classSet, requestOptions);
            }
            getLogger().trace(".forwardTaskToRouter(): [Invoke RMI Method] Finish, response->{}", response);
            getLogger().trace(".forwardTaskToRouter(): Exit, task->{}", task);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".forwardTaskToRouter(): Error (NoSuchMethodException) -> ", e);
            TaskRouterResponsePacket response = new TaskRouterResponsePacket();
            response.setRoutedTaskId(task.getTaskId());
            response.setSuccessorTaskId(null);
            response.setRoutingActivityInstant(Instant.now());
            response.setParticipantStatus(PetasosParticipantControlStatusEnum.PARTICIPANT_IS_IN_ERROR);
            response.setResponseCommentary("Error (NoSuchMethodException) -> " + e.getMessage());
            return(response);
        } catch (Exception e) {
            getLogger().error(".forwardTaskToRouter: Error (GeneralException) -> ",e);
            TaskRouterResponsePacket response = new TaskRouterResponsePacket();
            response.setRoutedTaskId(task.getTaskId());
            response.setSuccessorTaskId(null);
            response.setRoutingActivityInstant(Instant.now());
            response.setParticipantStatus(PetasosParticipantControlStatusEnum.PARTICIPANT_IS_IN_ERROR);
            response.setResponseCommentary("Error (GeneralException) ->" + e.getMessage());
            return(response);
        }
    }

    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(String targetParticipantServiceName, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".sendIPCMessage(): Entry, targetParticipantServiceName->{}, handoverPacket->{}", targetParticipantServiceName, handoverPacket);

        PetasosActionableTask actionableTask = handoverPacket.getActionableTask();
        TaskRouterResponsePacket taskRouterResponsePacket = forwardTaskToRouter(actionableTask);

        InterProcessingPlantHandoverResponsePacket interProcessingPlantHandoverResponsePacket = new InterProcessingPlantHandoverResponsePacket();
        if(!taskRouterResponsePacket.getParticipantStatus().equals(PetasosParticipantControlStatusEnum.PARTICIPANT_IS_IN_ERROR)) {
            interProcessingPlantHandoverResponsePacket.setActionableTaskId(actionableTask.getTaskId());
            interProcessingPlantHandoverResponsePacket.setMessageIdentifier(taskRouterResponsePacket.getRoutedTaskId().getId());
            interProcessingPlantHandoverResponsePacket.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_RECEIVED_AND_DECODED);
            interProcessingPlantHandoverResponsePacket.setDownstreamActionableTaskId(taskRouterResponsePacket.getSuccessorTaskId());
            interProcessingPlantHandoverResponsePacket.setMessageSendFinishInstant(taskRouterResponsePacket.getRoutingActivityInstant());
        } else {
            interProcessingPlantHandoverResponsePacket.setActionableTaskId(handoverPacket.getActionableTask().getTaskId());
            interProcessingPlantHandoverResponsePacket.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_SEND_FAILURE);
            interProcessingPlantHandoverResponsePacket.setStatusReason(taskRouterResponsePacket.getResponseCommentary());
        }

        getMetricsAgent().incrementInternalMessageDistributionCount();
        getMetricsAgent().incrementInternalMessageDistributionCount(targetParticipantServiceName);
        getLogger().debug(".sendIPCMessage(): Exit, interProcessingPlantHandoverResponsePacket->{}", interProcessingPlantHandoverResponsePacket);
        return(interProcessingPlantHandoverResponsePacket);
    }
}
