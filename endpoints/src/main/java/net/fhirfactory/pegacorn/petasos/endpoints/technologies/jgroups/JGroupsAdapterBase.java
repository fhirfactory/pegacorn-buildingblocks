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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups;


import net.fhirfactory.pegacorn.core.model.petasos.endpoint.JGroupsIntegrationPointIdentifier;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddressTypeEnum;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class JGroupsAdapterBase extends RouteBuilder implements MembershipListener {

    private boolean initialised;
    private JGroupsIntegrationPointIdentifier endpointID;
    private JChannel ipcChannel;
    private RpcDispatcher rpcDispatcher;

    private ArrayList<Address> previousScannedMembership;
    private ArrayList<Address> currentScannedMembership;
    private Object currentScannedMembershipLock;

    private static Long RPC_UNICAST_TIMEOUT = 5000L;

    //
    // Constructor(s)
    //

    public JGroupsAdapterBase(){
        this.ipcChannel = null;
        this.previousScannedMembership = new ArrayList<>();
        this.currentScannedMembership = new ArrayList<>();
        this.rpcDispatcher = null;
        this.currentScannedMembershipLock = new Object();
    }

    //
    // Abstract Methods
    //

    abstract protected Logger specifyLogger();
    abstract protected String specifySubsystemParticipantName();
    abstract protected String specifyJGroupsClusterName();
    abstract protected String specifyJGroupsChannelName();
    abstract protected String specifyJGroupsStackFileName();
    abstract protected String deriveIntegrationPointSubsystemName(String endpointName);

    abstract public void processInterfaceAddition(PetasosAdapterAddress addedInterface);
    abstract public void processInterfaceRemoval(PetasosAdapterAddress removedInterface);
    abstract public void processInterfaceSuspect(PetasosAdapterAddress suspectInterface);

    //
    // JGroups Group/Cluster Membership Event Listener
    //

    @Override
    public void viewAccepted(View newView) {
        getLogger().debug(".viewAccepted(): Entry, JGroups View Changed!");
        List<Address> addressList = newView.getMembers();
        getLogger().trace(".viewAccepted(): Got the Address set via view, now iterate through and see if one is suitable");
        if(getIPCChannel() != null) {
            getLogger().debug("JGroupsCluster->{}", getIPCChannel().getClusterName());
        } else {
            getLogger().debug("JGroupsCluster still Forming");
        }
        synchronized (this.currentScannedMembershipLock) {
            this.previousScannedMembership.clear();
            this.previousScannedMembership.addAll(this.currentScannedMembership);
            this.currentScannedMembership.clear();
            this.currentScannedMembership.addAll(addressList);
        }
        if(getLogger().isInfoEnabled()) {
            for (Address currentAddress : addressList) {
                getLogger().debug("Visible Member->{}", currentAddress);
            }
        }
        //
        // A Report
        //
        if((getIPCChannel() != null) && getLogger().isDebugEnabled()) {
            getLogger().debug(".viewAccepted(): -------- Starting Channel Report -------");
            String channelProperties = getIPCChannel().getProperties();
            getLogger().debug(".viewAccepted(): Properties->{}", channelProperties);
            String jchannelState = getIPCChannel().getState();
            getLogger().debug(".viewAccepted(): State->{}", jchannelState);
            getLogger().debug(".viewAccepted(): -------- End Channel Report -------");
        }
        //
        // Handle View Change
        getLogger().debug(".viewAccepted(): Checking PubSub Participants");
        List<PetasosAdapterAddress> removals = getMembershipRemovals(previousScannedMembership, currentScannedMembership);
        List<PetasosAdapterAddress> additions = getMembershipAdditions(previousScannedMembership, currentScannedMembership);
        getLogger().debug(".viewAccepted(): Changes(MembersAdded->{}, MembersRemoved->{}", additions.size(), removals.size());
        getLogger().debug(".viewAccepted(): Iterating through ActionInterfaces");
        for(PetasosAdapterAddress currentAddedElement: additions){
            processInterfaceAddition(currentAddedElement);
        }
        for(PetasosAdapterAddress currentRemovedElement: removals){
            processInterfaceRemoval(currentRemovedElement);
        }
        getLogger().debug(".viewAccepted(): PubSub Participants check completed");
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

    public void setEndpointID(JGroupsIntegrationPointIdentifier endpointID) {
        this.endpointID = endpointID;
    }

    public Long getRPCUnicastTimeout(){
        return(RPC_UNICAST_TIMEOUT);
    }

    public ArrayList<Address> getCurrentScannedMembership() {
        ArrayList<Address> clonedList = new ArrayList<>();
        return currentScannedMembership;
    }

    public void setCurrentScannedMembership(ArrayList<Address> currentScannedMembership) {
        this.currentScannedMembership = currentScannedMembership;
    }

    public Object getCurrentScannedMembershipLock() {
        return currentScannedMembershipLock;
    }

    //
    // JGroups Membership Methods
    //

    public List<Address> getAllViewMembers() {
        if (getIPCChannel() == null) {
            return (new ArrayList<>());
        }
        if (getIPCChannel().getView() == null) {
            return (new ArrayList<>());
        }
        try {
            List<Address> members = getIPCChannel().getView().getMembers();
            return (members);
        } catch (Exception ex) {
            getLogger().warn(".getAllMembers(): Failed to get View Members, Error: Message->{}, StackTrace->{}", ExceptionUtils.getMessage(ex), ExceptionUtils.getStackTrace(ex));
        }
        return (new ArrayList<>());
    }


    public Address getTargetMemberAddress(String name){
        getLogger().debug(".getTargetMemberAddress(): Entry, name->{}", name);
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetMemberAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getTargetMemberAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getAllViewMembers();
        Address foundAddress = null;
        synchronized (this.currentScannedMembershipLock) {
            getLogger().trace(".getTargetMemberAddress(): Got the Address set via view, now iterate through and see if one is suitable");
            for (Address currentAddress : addressList) {
                getLogger().trace(".getTargetMemberAddress(): Iterating through Address list, current element->{}", currentAddress);
                if (currentAddress.toString().contentEquals(name)) {
                    getLogger().trace(".getTargetMemberAddress(): Exit, A match!");
                    foundAddress = currentAddress;
                    break;
                }
            }
        }
        getLogger().debug(".getTargetMemberAddress(): Exit, address->{}", foundAddress);
        return(foundAddress);
    }


    public Address getCandidateTargetServiceAddress(String targetServiceName){
        getLogger().debug(".getCandidateTargetServiceAddress(): Entry, targetServiceName->{}", targetServiceName);
        if(getIPCChannel() == null){
            getLogger().debug(".getCandidateTargetServiceAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getCandidateTargetServiceAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getAllViewMembers();
        Address foundAddress = null;
        synchronized (this.currentScannedMembershipLock) {
            getLogger().debug(".getCandidateTargetServiceAddress(): Got the Address set via view, now iterate through and see if one is suitable");
            for (Address currentAddress : addressList) {
                getLogger().debug(".getCandidateTargetServiceAddress(): Iterating through Address list, current element->{}", currentAddress);
                String currentService = deriveIntegrationPointSubsystemName(currentAddress.toString());
                if (currentService.equals(targetServiceName)) {
                    getLogger().debug(".getCandidateTargetServiceAddress(): Exit, A match!");
                    foundAddress = currentAddress;
                    break;
                }
            }
        }
        getLogger().debug(".getCandidateTargetServiceAddress(): Exit, foundAddress->{}",foundAddress );
        return(foundAddress);
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
        List<Address> addressList = getAllViewMembers();
        boolean addressIsActive = false;
        synchronized (this.currentScannedMembershipLock) {
            getLogger().trace(".isTargetAddressActive(): Got the Address set via view, now iterate through and see our address is there");
            for (Address currentAddress : addressList) {
                getLogger().trace(".isTargetAddressActive(): Iterating through Address list, current element->{}", currentAddress);
                if (currentAddress.toString().contentEquals(addressName)) {
                    getLogger().trace(".isTargetAddressActive(): Exit, A match");
                    addressIsActive = true;
                    break;
                }
            }
        }
        getLogger().debug(".isTargetAddressActive(): Exit, addressIsActive->{}",addressIsActive);
        return(addressIsActive);
    }

    public List<PetasosAdapterAddress> getAllClusterTargets(){
        getLogger().debug(".getAllClusterTargets(): Entry");
        List<Address> addressList = getAllViewMembers();
        List<PetasosAdapterAddress> petasosAdapterAddresses = new ArrayList<>();
        synchronized (this.currentScannedMembershipLock) {
            for (Address currentAddress : addressList) {
                getLogger().debug(".getAllTargets(): Iterating through Address list, current element->{}", currentAddress);
                PetasosAdapterAddress currentPetasosAdapterAddress = new PetasosAdapterAddress();
                currentPetasosAdapterAddress.setAddressType(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                currentPetasosAdapterAddress.setJGroupsAddress(currentAddress);
                currentPetasosAdapterAddress.setAddressName(currentAddress.toString());
                petasosAdapterAddresses.add(currentPetasosAdapterAddress);
            }
        }
        getLogger().debug(".getAllClusterTargets(): Exit, petasosAdapterAddresses->{}", petasosAdapterAddresses);
        return(petasosAdapterAddresses);
    }

    protected Address getMyAddress(){
        if(getIPCChannel() != null){
            Address myAddress = getIPCChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }

    //
    // Route
    //

    @Override
    public void configure() throws Exception {
        String endpointName = specifySubsystemParticipantName();

        from("timer://"+endpointName+"?delay=1000&repeatCount=1")
                .routeId("ProcessingPlant::"+endpointName)
                .log(LoggingLevel.DEBUG, "Starting....");
    }

}
