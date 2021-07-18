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
package net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common;

import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class JGroupsIPCPubSubPublisherService extends JGroupsIPCPubSubParticipant{

    private boolean publisherCheckIsScheduled;
    private Object publisherCheckLock;
    private Long SUBSCRIPTION_CHECK_DELAY = 10000L;

    public JGroupsIPCPubSubPublisherService(){
        super();
        this.publisherCheckIsScheduled = false;
        this.publisherCheckLock = new Object();
    }

    @Override
    protected void additionalInitialisation(){
        registerPublisherIntoSolution(getPubsubParticipant().getInterSubsystemParticipant());
    }

    public abstract EdgeForwarderService getEdgeForwarderService();
    protected abstract void scheduleSubscriptionCheck();


    public boolean isPublisherInstanceAvailable(String publisherInstanceName){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, publisherInstanceName->{}", publisherInstanceName);
        boolean publisherInstanceNameStillActive = isParticipantInstanceAvailable(publisherInstanceName);
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, publisherInstanceNameStillActive->{}", publisherInstanceNameStillActive);
        return(publisherInstanceNameStillActive);
    }


    /**
     *
     * @param publisher
     * @return
     */
    public boolean isPublisherServiceAvailable(PubSubParticipant publisher){
        String publisherServiceName = publisher.getInterSubsystemParticipant().getIdentifier().getServiceName();
        boolean publisherAvailable = isPublisherServiceAvailable(publisherServiceName);
        return(publisherAvailable);
    }

    /**
     *
     * @param publisherServiceName
     * @return
     */
    public boolean isPublisherServiceAvailable(String publisherServiceName){
        getLogger().debug(".isPublisherAvailable(): Entry, publisherServiceName->{}", publisherServiceName);
        boolean publisherAvailable = isParticipantServiceAvailable(publisherServiceName);
        getLogger().debug(".isPublisherAvailable(): Exit, returning->{}", publisherAvailable);
        return(publisherAvailable);
    }

    public String getAvailablePublisherInstanceName(PubSubParticipant publisher){
        getLogger().debug(".getAvailablePublisherInstanceName(): Entry, publisher->{}", publisher);
        String publisherInstanceName = getAvailableParticipantInstanceName(publisher);
        getLogger().debug(".getAvailablePublisherInstanceName(): Exit, publisherInstanceName->{}", publisherInstanceName);
        return(publisherInstanceName);
    }

    public String getAvailablePublisherInstanceName(String publisherServiceName){
        getLogger().debug(".getAvailablePublisherInstanceName(): Entry, publisherServiceName->{}", publisherServiceName);
        String publisherInstanceName = getAvailableParticipantInstanceName(publisherServiceName);
        getLogger().debug(".getAvailablePublisherInstanceName(): Exit, publisherInstanceName->{}", publisherInstanceName);
        return(publisherInstanceName);
    }

    //
    // Publisher Management
    //

    /**
     *
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration rpcRegisterPublisherHandler(InterSubsystemPubSubParticipant publisher){
        getLogger().info(".rpcRegisterPublisherHandler(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = null;
        if(getPublisherRegistrationMapIM().isPublisherRegistered(publisher)){
            getLogger().trace(".rpcRegisterPublisherHandler(): Publisher is already Registered");
            registration = getPublisherRegistrationMapIM().getPublisherInstanceRegistration(publisher);
        } else {
            getLogger().trace(".rpcRegisterPublisherHandler(): Publisher is not locally Registered, so add it");
            registration = getPublisherRegistrationMapIM().registerPublisherInstance(publisher);
            getLogger().trace(".rpcRegisterPublisherHandler(): Publisher Registered, registration->{}", registration);
            if(registration.getPublisherStatus().equals(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED)){
                getLogger().trace(".rpcRegisterPublisherHandler(): Scheduling complete subscription check");
                getLogger().trace(".rpcRegisterPublisherHandler(): Scheduling of complete subscription check completed");
            }
        }
        getLogger().info("rpcRegisterPublisherHandler(): Exit, registration->{}", registration);
        return(registration);
    }

    /**
     *
     * @param target
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration requestPublisherRegistration(Address target, InterSubsystemPubSubParticipant publisher){
        getLogger().info(".requestPublisherRegistration(): Entry, target->{}, publisher->{}", target, publisher);
        Object objectSet[] = new Object[1];
        Class classSet[] = new Class[1];
        objectSet[0] = publisher;
        classSet[0] = InterSubsystemPubSubParticipant.class;
        RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, RPC_UNICAST_TIMEOUT);
        InterSubsystemPubSubPublisherRegistration response = null;
        try {
            response = getRPCDispatcher().callRemoteMethod(target, "rpcRegisterPublisherHandler", objectSet, classSet, requestOptions);
        } catch (Exception e) {
            getLogger().error(".requestPublisherRegistration(): Error registering Publisher, message->{}", e.getMessage());
            return(null);
        }
        getLogger().info(".requestPublisherRegistration(): Exit, response->{}", response);
        return(response);
    }

    /**
     *
     * @param publisher
     */
    public void registerPublisherIntoSolution(InterSubsystemPubSubParticipant publisher){
        getLogger().debug(".publisherRegister(): Entry, publisher->{}", publisher);
        List<Address> addressList = getIPCChannel().getView().getMembers();
        for(Address currentAddress: addressList){
            if(currentAddress.toString().startsWith(this.getPubsubParticipant().getInterSubsystemParticipant().getIdentifier().getServiceName())){
                // don't do anything, as this address either refers to me or another instance of myself
            } else {
                getLogger().trace(".publisherRegister(): sending registration request to->{}", currentAddress);
                InterSubsystemPubSubPublisherRegistration subscriptionResponse = requestPublisherRegistration(currentAddress, publisher);
                getLogger().trace(".publisherRegister(): Registration request to->{}, subscriptionResponse->{}", currentAddress, subscriptionResponse);
            }
        }
        getLogger().debug(".publisherRegister(): Exit");
    }

    /**
     *
     */
    protected void schedulePublisherCheck(){
        synchronized(this.publisherCheckLock) {
            if (this.publisherCheckIsScheduled) {
                // do nothing, it is already scheduled
            } else {
                TimerTask publisherCheckTask = new TimerTask() {
                    public void run() {
                        boolean doAgain = performPubSubParticipantCheck();
                        if(!doAgain) {
                            publisherCheckIsScheduled = false;
                            cancel();
                        }
                    }
                };
                Timer timer = new Timer("PublisherCheck");
                timer.schedule(publisherCheckTask, this.SUBSCRIPTION_CHECK_DELAY);
            }
        }
    }

    /**
     *
     */
    @Override
    protected boolean performPubSubParticipantCheck(){
        getLogger().info(".performPublisherCheck(): Entry");
        boolean doAgain = false;

        List<String> allPublishers = getPublisherRegistrationMapIM().getAllPublishers();

        for(String publisherInstanceName: allPublishers){
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = null;
            if(isPublisherInstanceAvailable(publisherInstanceName)){
                // Do Nothing!
            } else {
                getLogger().info(".performPublisherCheck(): Publisher->{} is not available, removing!", publisherInstanceName);
                InterSubsystemPubSubPublisherRegistration publisherInstanceRegistration = getPublisherRegistrationMapIM().getPublisherInstanceRegistration(publisherInstanceName);
                subscriptionRegistration = getPublisherRegistrationMapIM().unregisterPublisherInstance(publisherInstanceRegistration.getPublisher());
            }
            if(subscriptionRegistration != null) {
                if (subscriptionRegistration.getRegistrationStatus().equals(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS)) {
                    getLogger().warn(".performPublisherCheck(): No publisher for service ({}) is available", subscriptionRegistration.getPublisherServiceName() );
                }
            }
        }
        synchronized(this.publisherCheckLock) {
            this.publisherCheckIsScheduled = false;
        }
        scheduleSubscriptionCheck();
        getLogger().info(".performPublisherCheck(): Exit, returning doAgain->{}", doAgain);
        return(doAgain);
    }
}
