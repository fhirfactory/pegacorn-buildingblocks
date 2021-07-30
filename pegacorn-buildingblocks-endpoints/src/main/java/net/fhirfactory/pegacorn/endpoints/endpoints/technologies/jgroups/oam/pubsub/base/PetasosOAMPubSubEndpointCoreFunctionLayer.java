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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.oam.pubsub.base;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddressTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.utilisationpolicy.PetasosOAMEndpointPolicyLayer;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionRequest;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

public abstract class PetasosOAMPubSubEndpointCoreFunctionLayer extends PetasosOAMEndpointPolicyLayer {


    //
    // Abstract Methods
    //

    abstract protected EdgeForwarderService specifyEdgeForwarderService();

    //
    // Getters (and Setters)
    //

    protected EdgeForwarderService getEdgeForwarderService(){
        return(specifyEdgeForwarderService());
    }

    //
    // Callback Procedures for Subscribing
    //

    /**
     *
     * @param subscriptionRequest
     * @return
     */
    public RemoteSubscriptionResponse rpcRequestSubscriptionHandler(RemoteSubscriptionRequest subscriptionRequest){
        getLogger().info(".rpcRequestSubscriptionHandler(): Entry, subscriptionRequest.getSubscriber()->{}", subscriptionRequest.getSubscriber());

        PubSubParticipant subscriber = subscriptionRequest.getSubscriber();
        List<DataParcelManifest> subscriptionList = subscriptionRequest.getSubscriptionList();

        boolean rightChannel = isRightChannel(subscriber.getInterSubsystemParticipant());
        getLogger().trace(".rpcRequestSubscriptionHandler(): withinScope->{}", rightChannel);
        boolean doSubscription;
        PetasosEndpointStatusEnum aggregatePetasosEndpointStatus = getCoreSubsystemPetasosEndpointsWatchdog().getAggregatePetasosEndpointStatus();
        getLogger().trace(".rpcRequestSubscriptionHandler(): aggregateOperationalEndpointStatus->{}", aggregatePetasosEndpointStatus);
        boolean operationalStatusIsGood = aggregatePetasosEndpointStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
        if(operationalStatusIsGood && rightChannel){
            doSubscription = true;
        } else {
            doSubscription = false;
        }

        getLogger().trace(".rpcRequestSubscriptionHandler(): doSubscription->{}", doSubscription);
        RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
        if (doSubscription) {
            RemoteSubscriptionStatus subscriptionStatus = getEdgeForwarderService().subscribeOnBehalfOfRemoteSubscriber(subscriptionList, subscriber);
            getLogger().info(".rpcRequestSubscriptionHandler(): subscription result->{}", subscriptionStatus);
            response.setPublisher(getParticipant());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(subscriptionStatus.isSubscriptionSuccessful());
            response.setSubscriptionCommentary(subscriptionStatus.getSubscriptionCommentary());
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
        } else {
            getLogger().trace(".rpcRequestSubscriptionHandler(): subscription not supported");
            response.setPublisher(getParticipant());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("This IPC Endpoint can not presently support subscription");
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_FAILED);
        }
        getLogger().debug(".rpcRequestSubscriptionHandler(): Exit, response->{}", response);
        return(response);
    }

    /**
     *
     * @param publisherAddress
     * @param subscriptionRequest
     * @return
     */

    public RemoteSubscriptionResponse rpcRequestSubscription(PetasosAdapterAddress publisherAddress, RemoteSubscriptionRequest subscriptionRequest){
        getLogger().info(".rpcRequestSubscription(): Entry, publisher->{}, subscriptionRequest", publisherAddress, subscriptionRequest);
        if(publisherAddress == null || subscriptionRequest == null){
            getLogger().error(".rpcRequestSubscription: publisherAddress or subscriptionRequest are null");
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (publisherAddress or subscriptionRequest are null)");
            return(response);
        }
        if(!publisherAddress.getAddressType().equals(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS)){
            getLogger().error(".rpcRequestSubscription: publisherAddress or subscriptionRequest are null");
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (Wrong endpoint technology (should be JGroups))");
            return(response);
        }
        Address jgroupsAddress = publisherAddress.getJGroupsAddress();
        getLogger().trace(".rpcRequestSubscription(): Extract JGroups Address->{}", jgroupsAddress);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = subscriptionRequest;
            classSet[0] = RemoteSubscriptionRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteSubscriptionResponse response = getRPCDispatcher().callRemoteMethod(jgroupsAddress, "rpcRequestSubscriptionHandler", objectSet, classSet, requestOptions);
            getLogger().info(".rpcRequestSubscription(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".rpcRequestSubscription(): Error (NoSuchMethodException)->", e);
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            getLogger().error(".rpcRequestSubscription: Error (GeneralException) ->",e);
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (GeneralException)" + e.getMessage());
            return(response);
        }
    }
}
