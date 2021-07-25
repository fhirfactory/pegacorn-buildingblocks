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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base;

import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubNetworkConnectionStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceAddressTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.base.EndpointChangeNotificationActionInterface;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class JGroupsInterfaceBase implements MembershipListener {

    private boolean initialised;
    private PubSubParticipant pubsubParticipant;
    private JChannel ipcChannel;
    private JChannel ipcChannelSend;
    private RpcDispatcher rpcDispatcher;

    private ArrayList<Address> previousScannedMembership;
    private ArrayList<Address> currentScannedMembership;
    private ArrayList<EndpointChangeNotificationActionInterface> membershipEventListeners;

    public JGroupsInterfaceBase(){
        this.ipcChannel = null;
        this.ipcChannelSend = null;
        this.previousScannedMembership = new ArrayList<>();
        this.currentScannedMembership = new ArrayList<>();
        this.membershipEventListeners = new ArrayList<>();
        this.pubsubParticipant = null;
        this.rpcDispatcher = null;
    }

    abstract protected Logger specifyLogger();

    //
    // JGroups Group/Cluster Membership Event Listener
    //

    @Override
    public void viewAccepted(View newView) {
        getLogger().debug(".viewAccepted(): Entry, JGroups View Changed!");
//        Receiver.super.viewAccepted(newView);
        List<Address> addressList = newView.getMembers();
        getLogger().trace(".viewAccepted(): Got the Address set via view, now iterate through and see if one is suitable");
        if(getIPCChannel() != null) {
            getLogger().warn("JGroupsCluster->{}", getIPCChannel().getClusterName());
        } else {
            getLogger().warn("JGroupsCluster still Forming");
        }
        this.previousScannedMembership.clear();
        this.previousScannedMembership.addAll(this.currentScannedMembership);
        this.currentScannedMembership.clear();
        for(Address currentAddress: addressList){
            this.currentScannedMembership.add(currentAddress);
            getLogger().warn("Visible Member->{}", currentAddress);
        }
        getLogger().trace(".viewAccepted(): Checking PubSub Participants");
        List<PetasosInterfaceAddress> removals = getMembershipRemovals(previousScannedMembership, currentScannedMembership);
        List<PetasosInterfaceAddress> additions = getMembershipAdditions(previousScannedMembership, currentScannedMembership);
        getLogger().info(".viewAccepted(): Changes(MembersAdded->{}, MembersRemoved->{}", additions.size(), removals.size());
        for(EndpointChangeNotificationActionInterface currentActionInterface: this.membershipEventListeners){
            getLogger().info(".viewAccepted(): Iterating through ActionInterfaces");
            currentActionInterface.notifyMembershipChange(additions, removals);
        }
        getLogger().trace(".viewAccepted(): PubSub Participants check completed");
        getLogger().debug(".viewAccepted(): Exit");
    }

    @Override
    public void suspect(Address suspected_mbr) {
        MembershipListener.super.suspect(suspected_mbr);
    }

    @Override
    public void block() {
        MembershipListener.super.block();
    }

    @Override
    public void unblock() {
        MembershipListener.super.unblock();
    }

    private List<PetasosInterfaceAddress> getMembershipAdditions(List<Address> oldList, List<Address> newList){
        List<PetasosInterfaceAddress> additions = new ArrayList<>();
        for(Address newListElement: newList){
            if(oldList.contains(newListElement)){
                // do nothing
            } else {
                PetasosInterfaceAddress currentPetasosInterfaceAddress = new PetasosInterfaceAddress();
                currentPetasosInterfaceAddress.setAddressName(newListElement.toString());
                currentPetasosInterfaceAddress.setJGroupsAddress(newListElement);
                currentPetasosInterfaceAddress.setAddressType(PetasosInterfaceAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                additions.add(currentPetasosInterfaceAddress);
            }
        }
        return(additions);
    }

    private List<PetasosInterfaceAddress> getMembershipRemovals(List<Address> oldList, List<Address> newList){
        List<PetasosInterfaceAddress> removals = new ArrayList<>();
        for(Address oldListElement: oldList){
            if(newList.contains(oldListElement)){
                // no nothing
            } else {
                PetasosInterfaceAddress currentPetasosInterfaceAddress = new PetasosInterfaceAddress();
                currentPetasosInterfaceAddress.setAddressName(oldListElement.toString());
                currentPetasosInterfaceAddress.setJGroupsAddress(oldListElement);
                currentPetasosInterfaceAddress.setAddressType(PetasosInterfaceAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                removals.add(currentPetasosInterfaceAddress);
            }
        }
        return(removals);
    }

    //
    // JChannel Initialisation
    //

    protected void establishJChannel(String fileName, String groupName, String channelName){
        getLogger().debug(".establishJChannel(): Entry, groupName->{}, channelName->{}", groupName, channelName);
        try {
            getLogger().trace(".establishJChannel(): Creating JChannel");
            getLogger().trace(".establishJChannel(): Getting the required ProtocolStack");
            JChannel newChannel = new JChannel(fileName);
            getLogger().trace(".establishJChannel(): JChannel initialised, now setting JChannel name");
            newChannel.name(channelName);
            getLogger().trace(".establishJChannel(): JChannel Name set, now set ensure we don't get our own messages");
            newChannel.setDiscardOwnMessages(true);
            getLogger().trace(".establishJChannel(): Now setting RPCDispatcher");
            RpcDispatcher newRPCDispatcher = new RpcDispatcher(newChannel, this);
            newRPCDispatcher.setMembershipListener(this);
            getLogger().trace(".establishJChannel(): RPCDispatcher assigned, now connect to JGroup");
            newChannel.connect(groupName);
            getLogger().trace(".establishJChannel(): Connected to JGroup complete, now assigning class attributes");
            this.setIPCChannel(newChannel);
            this.setRPCDispatcher(newRPCDispatcher);
            this.getPubsubParticipant().getInterSubsystemParticipant().setConnectionEstablishmentDate(Date.from(Instant.now()));
            this.getPubsubParticipant().getInterSubsystemParticipant().setConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_ESTABLISHED);
            getLogger().trace(".establishJChannel(): Exit, JChannel & RPCDispatcher created");
            return;
        } catch (Exception e) {
            getLogger().error(".establishJChannel(): Cannot establish JGroups Channel, error->", e);
            this.getPubsubParticipant().getInterSubsystemParticipant().setConnectionEstablishmentDate(Date.from(Instant.now()));
            this.getPubsubParticipant().getInterSubsystemParticipant().setConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_FAILED);
            return;
        }
    }

    //
    // Getters and Setters
    //

    public JChannel getIPCChannel() {
        return ipcChannel;
    }

    public void setIPCChannel(JChannel ipcChannel) {
        this.ipcChannel = ipcChannel;
    }

    public ArrayList<EndpointChangeNotificationActionInterface> getMembershipEventListeners() {
        return membershipEventListeners;
    }

    protected Logger getLogger(){
        return(specifyLogger());
    }

    public RpcDispatcher getRPCDispatcher() {
        return rpcDispatcher;
    }

    protected void setRPCDispatcher(RpcDispatcher rpcDispatcher) {
        this.rpcDispatcher = rpcDispatcher;
    }

    public PubSubParticipant getPubsubParticipant() {
        return pubsubParticipant;
    }

    public void setPubsubParticipant(PubSubParticipant pubsubParticipant) {
        this.pubsubParticipant = pubsubParticipant;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    //
    // JGroups Membership Methods
    //

    public Address getTargetMemberAddress(String name){
        getLogger().debug(".getTargetAddress(): Entry, name->{}", name);
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getTargetAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getIPCChannel().getView().getMembers();
        getLogger().trace(".getTargetAddress(): Got the Address set via view, now iterate through and see if one is suitable");
        for(Address currentAddress: addressList){
            getLogger().trace(".getTargetAddress(): Iterating through Address list, current element->{}", currentAddress);
            if(currentAddress.toString().contentEquals(name)){
                getLogger().debug(".getTargetAddress(): Exit, A match!, returning address->{}", currentAddress);
                return(currentAddress);
            }
        }
        getLogger().debug(".getTargetAddress(): Exit, no suitable Address found!");
        return(null);
    }


    public Address getTargetServiceAddress(String name){
        getLogger().debug(".getTargetAddress(): Entry, name->{}", name);
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getTargetAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getIPCChannel().getView().getMembers();
        getLogger().trace(".getTargetAddress(): Got the Address set via view, now iterate through and see if one is suitable");
        for(Address currentAddress: addressList){
            getLogger().trace(".getTargetAddress(): Iterating through Address list, current element->{}", currentAddress);
            if(currentAddress.toString().startsWith(name)){
                getLogger().debug(".getTargetAddress(): Exit, A match!, returning address->{}", currentAddress);
                return(currentAddress);
            }
        }
        getLogger().debug(".getTargetAddress(): Exit, no suitable Address found!");
        return(null);
    }



    protected boolean isTargetAddressActive(String addressName){
        getLogger().debug(".isTargetAddressActive(): Entry, addressName->{}", addressName);
        if(getIPCChannel() == null){
            getLogger().debug(".isTargetAddressActive(): IPCChannel is null, exit returning -false-");
            return(false);
        }
        if(StringUtils.isEmpty(addressName)){
            getLogger().debug(".isTargetAddressActive(): addressName is empty, exit returning -false-");
            return(false);
        }
        getLogger().trace(".isTargetAddressActive(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getIPCChannel().getView().getMembers();
        getLogger().trace(".isTargetAddressActive(): Got the Address set via view, now iterate through and see our address is there");
        for(Address currentAddress: addressList){
            getLogger().trace(".isTargetAddressActive(): Iterating through Address list, current element->{}", currentAddress);
            if(currentAddress.toString().contentEquals(addressName)){
                getLogger().info(".isTargetAddressActive(): Exit, A match!, returning -true-");
                return(true);
            }
        }
        getLogger().info(".isTargetAddressActive(): Exit, no matching Address found!");
        return(false);
    }

    public String getServiceNameFromAddressInstanceName(String participantInstanceName){
        if(StringUtils.isEmpty(participantInstanceName)){
            return(null);
        }
        String[] nameParts = StringUtils.split(participantInstanceName, "(");
        return(nameParts[0]);
    }

    public List<PetasosInterfaceAddress> getAllTargets(){
        List<Address> addressList = getIPCChannel().getView().getMembers();
        List<PetasosInterfaceAddress> petasosInterfaceAddresses = new ArrayList<>();
        for(Address currentAddress: addressList){
            String serviceName = getServiceNameFromAddressInstanceName(currentAddress.toString());
            if(serviceName.contentEquals(getPubsubParticipant().getInterSubsystemParticipant().getIdentifier().getServiceName())){
                // don't add, it's one of us!
            } else {
                getLogger().trace(".getAllTargets(): Iterating through Address list, current element->{}", currentAddress);
                PetasosInterfaceAddress currentPetasosInterfaceAddress = new PetasosInterfaceAddress();
                currentPetasosInterfaceAddress.setAddressType(PetasosInterfaceAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                currentPetasosInterfaceAddress.setJGroupsAddress(currentAddress);
                currentPetasosInterfaceAddress.setAddressName(currentAddress.toString());
                petasosInterfaceAddresses.add(currentPetasosInterfaceAddress);
            }

        }
        return(petasosInterfaceAddresses);
    }

    protected Address getMyAddress(){
        if(getIPCChannel() != null){
            Address myAddress = getIPCChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }

}
