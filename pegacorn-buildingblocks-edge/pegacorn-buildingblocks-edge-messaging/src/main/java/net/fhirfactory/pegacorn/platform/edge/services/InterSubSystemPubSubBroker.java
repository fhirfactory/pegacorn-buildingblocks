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
package net.fhirfactory.pegacorn.platform.edge.services;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.JGroupsInterZoneIPCEndpoint;
import net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.JGroupsIntraZoneIPCEndpoint;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.InterSubSystemPubSubBrokerInterface;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class InterSubSystemPubSubBroker implements InterSubSystemPubSubBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(InterSubSystemPubSubBroker.class);

    @Inject
    private JGroupsInterZoneIPCEndpoint interZoneIPCEndpoint;

    @Inject
    private JGroupsIntraZoneIPCEndpoint intraZoneIPCEndpoint;

    public RemoteSubscriptionResponse subscribe(List<DataParcelManifest> dataParcelManifestList, String sourceSubSystem){
        LOG.info(".subscribe(): Entry, dataParcelManifestList->{}, sourceSubSystem->{}", dataParcelManifestList, sourceSubSystem);
        PubSubParticipant publisher = new PubSubParticipant();
        InterSubsystemPubSubParticipant distributedPublisher = new InterSubsystemPubSubParticipant();
        InterSubsystemPubSubParticipantIdentifier distributedPublisherIdentifier = new InterSubsystemPubSubParticipantIdentifier();
        distributedPublisherIdentifier.setServiceName(sourceSubSystem);
        distributedPublisher.setIdentifier(distributedPublisherIdentifier);
        distributedPublisher.getIdentifier().setServiceName(sourceSubSystem);
        publisher.setInterSubsystemParticipant(distributedPublisher);
//        if(interZoneIPCEndpoint.isPublisherAvailable(publisher)){
//            LOG.info(".subscribe(): Is InterZone based communications");
//            RemoteSubscriptionResponse interZoneResponse = interZoneIPCEndpoint.subscribeToRemotePublishers(dataParcelManifestList, publisher);
//            LOG.info(".subscribe(): Exit, interZoneResponse->{}", interZoneResponse);
//            return(interZoneResponse);
//        }
//
        LOG.info(".subscribe(): Is IntraZone based communications");
        RemoteSubscriptionResponse intraZoneResponse = intraZoneIPCEndpoint.subscribeToRemotePublishers(dataParcelManifestList, publisher);
        LOG.info(".subscribe(): Exit, intraZoneResponse->{}", intraZoneResponse);
        return(intraZoneResponse);
//        RemoteSubscriptionResponse noSourceSystemResponse = new RemoteSubscriptionResponse();
//        noSourceSystemResponse.setSubscriptionSuccessful(false);
//        noSourceSystemResponse.setSubscriptionCommentary("Could not find Source Subsystem");
//        LOG.debug(".subscribe(): Exit, noSourceSystemResponse->{}", noSourceSystemResponse);
//        return(noSourceSystemResponse);
    }

}
