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
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.helpers.JGroupsBasedParticipantInformationService;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistration;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.InterSubSystemPubSubBrokerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class InterSubSystemPubSubBroker implements InterSubSystemPubSubBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(InterSubSystemPubSubBroker.class);

    @Inject
    private DistributedPubSubSubscriptionMapIM distributedPubSubSubscriptionMapIM;

    @Inject
    private JGroupsBasedParticipantInformationService participantInformationService;

    @PostConstruct
    public void initialise(){
        LOG.info(".initialise(): initialising......");;
        LOG.info(".initialise(): Done....");
    }

    @Override
    public InterSubsystemPubSubPublisherSubscriptionRegistration subscribe(List<DataParcelManifest> dataParcelManifestList, String sourceSubSystem) {
        LOG.info(".subscribe(): Entry, I am here in (InterSubSystemPubSubBroker), dataParcelManifestList->{}, sourceSubSystem->{}", dataParcelManifestList, sourceSubSystem);
        LOG.info(".subscribe(): Registration subscriptions");
        InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = distributedPubSubSubscriptionMapIM.addSubscriptionToPublisher(dataParcelManifestList, sourceSubSystem);
        LOG.info(".subscribe(): Exit, done->{}", subscriptionRegistration);
        return (subscriptionRegistration);
    }


}
