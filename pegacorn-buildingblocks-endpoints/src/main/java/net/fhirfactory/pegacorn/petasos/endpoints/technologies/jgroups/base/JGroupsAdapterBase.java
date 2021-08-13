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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base;


import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddressTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class JGroupsAdapterBase implements MembershipListener {

    private boolean initialised;
    private PetasosEndpointIdentifier endpointID;
    private JChannel ipcChannel;
    private RpcDispatcher rpcDispatcher;

    private ArrayList<Address> previousScannedMembership;
    private ArrayList<Address> currentScannedMembership;
    private ArrayList<PetasosAdapterDeltasInterface> membershipEventListeners;

    private static Long RPC_UNICAST_TIMEOUT = 5000L;


    public JGroupsAdapterBase(){
        this.ipcChannel = null;
        this.previousScannedMembership = new ArrayList<>();
        this.currentScannedMembership = new ArrayList<>();
        this.membershipEventListeners = new ArrayList<>();
        this.rpcDispatcher = null;
    }

    abstract protected Logger specifyLogger();
    abstract protected String specifyEndpointServiceName();
    abstract protected String specifyJGroupsClusterName();
    abstract protected String specifyJGroupsChannelName();
    abstract protected String specifyJGroupsStackFileName();
    abstract protected String deriveEndpointServiceName(String endpointName);

    //
    // JGroups Group/Cluster Membership Event Listener
    //

    @Override
    public void viewAccepted(View newView) {
        getLogger().debug(".viewAccepted(): Entry, JGroups View Changed!");
        List<Address> addressList = newView.getMembers();
        getLogger().trace(".viewAccepted(): Got the Address set via view, now iterate through and see if one is suitable");
        if(getIPCChannel() != null) {
            getLogger().info("JGroupsCluster->{}", getIPCChannel().getClusterName());
        } else {
            getLogger().info("JGroupsCluster still Forming");
        }
        this.previousScannedMembership.clear();
        this.previousScannedMembership.addAll(this.currentScannedMembership);
        this.currentScannedMembership.clear();
        for(Address currentAddress: addressList){
            this.currentScannedMembership.add(currentAddress);
            getLogger().info("Visible Member->{}", currentAddress);
        }
        getLogger().trace(".viewAccepted(): Checking PubSub Participants");
        List<PetasosAdapterAddress> removals = getMembershipRemovals(previousScannedMembership, currentScannedMembership);
        List<PetasosAdapterAddress> additions = getMembershipAdditions(previousScannedMembership, currentScannedMembership);
        getLogger().info(".viewAccepted(): Changes(MembersAdded->{}, MembersRemoved->{}", additions.size(), removals.size());
        for(PetasosAdapterDeltasInterface currentActionInterface: this.membershipEventListeners){
            getLogger().info(".viewAccepted(): Iterating through ActionInterfaces");
            for(PetasosAdapterAddress currentAddedElement: additions){
                currentActionInterface.interfaceAdded(currentAddedElement);
            }
            for(PetasosAdapterAddress currentRemovedElement: removals){
                currentActionInterface.interfaceRemoved(currentRemovedElement);
            }
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

    private List<PetasosAdapterAddress> getMembershipAdditions(List<Address> oldList, List<Address> newList){
        List<PetasosAdapterAddress> additions = new ArrayList<>();
        for(Address newListElement: newList){
            if(oldList.contains(newListElement)){
                // do nothing
            } else {
                PetasosAdapterAddress currentPetasosAdapterAddress = new PetasosAdapterAddress();
                currentPetasosAdapterAddress.setAddressName(newListElement.toString());
                currentPetasosAdapterAddress.setJGroupsAddress(newListElement);
                currentPetasosAdapterAddress.setAddressType(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                additions.add(currentPetasosAdapterAddress);
            }
        }
        return(additions);
    }

    private List<PetasosAdapterAddress> getMembershipRemovals(List<Address> oldList, List<Address> newList){
        List<PetasosAdapterAddress> removals = new ArrayList<>();
        for(Address oldListElement: oldList){
            if(newList.contains(oldListElement)){
                // no nothing
            } else {
                PetasosAdapterAddress currentPetasosAdapterAddress = new PetasosAdapterAddress();
                currentPetasosAdapterAddress.setAddressName(oldListElement.toString());
                currentPetasosAdapterAddress.setJGroupsAddress(oldListElement);
                currentPetasosAdapterAddress.setAddressType(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                removals.add(currentPetasosAdapterAddress);
            }
        }
        return(removals);
    }

    //
    // JChannel Initialisation
    //

    protected void establishJChannel(){
        getLogger().info(".establishJChannel(): Entry, fileName->{}, groupName->{}, channelName->{}",  specifyJGroupsStackFileName(), specifyJGroupsClusterName(), specifyJGroupsChannelName());
        try {
            getLogger().trace(".establishJChannel(): Creating JChannel");
            getLogger().trace(".establishJChannel(): Getting the required ProtocolStack");
            JChannel newChannel = new JChannel(specifyJGroupsStackFileName());
            getLogger().trace(".establishJChannel(): JChannel initialised, now setting JChannel name");
            newChannel.name(specifyJGroupsChannelName());
            getLogger().trace(".establishJChannel(): JChannel Name set, now set ensure we don't get our own messages");
            newChannel.setDiscardOwnMessages(true);
            getLogger().trace(".establishJChannel(): Now setting RPCDispatcher");
            RpcDispatcher newRPCDispatcher = new RpcDispatcher(newChannel, this);
            newRPCDispatcher.setMembershipListener(this);
            getLogger().trace(".establishJChannel(): RPCDispatcher assigned, now connect to JGroup");
            newChannel.connect( specifyJGroupsClusterName());
            getLogger().trace(".establishJChannel(): Connected to JGroup complete, now assigning class attributes");
            this.setIPCChannel(newChannel);
            this.setRPCDispatcher(newRPCDispatcher);
            getLogger().trace(".establishJChannel(): Exit, JChannel & RPCDispatcher created");
            return;
        } catch (Exception e) {
            getLogger().error(".establishJChannel(): Cannot establish JGroups Channel, error->", e);
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

    public ArrayList<PetasosAdapterDeltasInterface> getMembershipEventListeners() {
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


    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public PetasosEndpointIdentifier getEndpointID() {
        return endpointID;
    }

    public void setEndpointID(PetasosEndpointIdentifier endpointID) {
        this.endpointID = endpointID;
    }

    public Long getRPCUnicastTimeout(){
        return(RPC_UNICAST_TIMEOUT);
    }

    //
    // JGroups Membership Methods
    //

    public Address getTargetMemberAddress(String name){
        getLogger().info(".getTargetAddress(): Entry, name->{}", name);
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().info(".getTargetAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getIPCChannel().getView().getMembers();
        getLogger().info(".getTargetAddress(): Got the Address set via view, now iterate through and see if one is suitable");
        for(Address currentAddress: addressList){
            getLogger().info(".getTargetAddress(): Iterating through Address list, current element->{}", currentAddress);
            if(currentAddress.toString().contentEquals(name)){
                getLogger().info(".getTargetAddress(): Exit, A match!, returning address->{}", currentAddress);
                return(currentAddress);
            }
        }
        getLogger().info(".getTargetAddress(): Exit, no suitable Address found!");
        return(null);
    }


    public Address getCandidateTargetServiceAddress(String targetServiceName){
        getLogger().debug(".getTargetAddress(): Entry, targetServiceName->{}", targetServiceName);
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getTargetAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getIPCChannel().getView().getMembers();
        getLogger().trace(".getTargetAddress(): Got the Address set via view, now iterate through and see if one is suitable");
        for(Address currentAddress: addressList){
            getLogger().trace(".getTargetAddress(): Iterating through Address list, current element->{}", currentAddress);
            String currentService = deriveEndpointServiceName(currentAddress.toString());
            if(currentService.equals(targetServiceName)){
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

    public List<PetasosAdapterAddress> getAllClusterTargets(){
        List<Address> addressList = getIPCChannel().getView().getMembers();
        List<PetasosAdapterAddress> petasosAdapterAddresses = new ArrayList<>();
        for(Address currentAddress: addressList){
            getLogger().info(".getAllTargets(): Iterating through Address list, current element->{}", currentAddress);
            PetasosAdapterAddress currentPetasosAdapterAddress = new PetasosAdapterAddress();
            currentPetasosAdapterAddress.setAddressType(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
            currentPetasosAdapterAddress.setJGroupsAddress(currentAddress);
            currentPetasosAdapterAddress.setAddressName(currentAddress.toString());
            petasosAdapterAddresses.add(currentPetasosAdapterAddress);
        }
        return(petasosAdapterAddresses);
    }

    protected Address getMyAddress(){
        if(getIPCChannel() != null){
            Address myAddress = getIPCChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }

}
