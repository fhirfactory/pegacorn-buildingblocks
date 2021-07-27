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
package net.fhirfactory.pegacorn.endpoints.endpoints.roles;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.base.PubSubParticipantEndpointServiceInterface;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.base.PubSubParticipantRoleBase;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.common.*;
import net.fhirfactory.pegacorn.platform.edge.endpoints.technologies.activitycache.datatypes.IPCEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import org.jgroups.JChannel;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PubSubSubscriberRole extends PubSubParticipantRoleBase {
    private static final Logger LOG = LoggerFactory.getLogger(PubSubSubscriberRole.class);

    private PetasosEndpointCheckSchedule subscriptionCheckSchedule;

    public PubSubSubscriberRole(
            ProcessingPlantInterface processingPlant,
            PubSubParticipantEndpointServiceInterface endpointServiceInterface,
            PubSubParticipant me,
            DistributedPubSubSubscriptionMapIM publisherMapIM,
            JChannel channel,
            RpcDispatcher rpcDispatcher,
            EdgeForwarderService forwarderService){
        super(processingPlant, endpointServiceInterface, me, publisherMapIM, channel, rpcDispatcher, forwarderService);
        subscriptionCheckSchedule = new PetasosEndpointCheckSchedule();

        TimerTask subscriptionCheckTask = new TimerTask() {
            public void run() {
                getLogger().info(".subscriptionCheckTask(): Entry");
                performSubscriptionCheck();
                getLogger().info(".subscriptionCheckTask(): Exit");
            }
        };
        Timer timer = new Timer("GeneralSubscriptionCheck");
        timer.schedule(subscriptionCheckTask, 300000, 300000);
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }



    @Override
    protected void performPublisherEventUpdateCheck(List<PetasosAdapterAddress> publishersRemoved, List<PetasosAdapterAddress> publishersAdded) {
        for(PetasosAdapterAddress currentAddress: publishersAdded){
            subscriptionCheckSchedule.scheduleEndpointCheck(currentAddress, false, true);
        }
    }

    @Override
    protected void performSubscriberEventUpdateCheck(List<PetasosAdapterAddress> subscribersRemoved, List<PetasosAdapterAddress> subscribersAdded) {

    }










}
