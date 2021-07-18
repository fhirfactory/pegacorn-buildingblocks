package net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common;

import org.apache.commons.lang3.StringUtils;
import org.jgroups.*;
import org.jgroups.util.MessageBatch;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class JGroupsEndpoint implements Receiver{

    private JChannel ipcChannel;
    private boolean initialised;

    public JGroupsEndpoint(){
        this.setIPCChannel(null);
        this.setInitialised(false);
    }

    abstract protected Logger specifyLogger();
    abstract protected boolean performPubSubParticipantCheck();

    protected Logger getLogger(){
        return(specifyLogger());
    }

    @Override
    public void receive(Message message) {
        getLogger().debug(".receive(): Entry, message->{}", message);

    }

    @Override
    public void viewAccepted(View newView) {
        getLogger().debug(".viewAccepted(): Entry, JGroups View Changed!");
//        Receiver.super.viewAccepted(newView);
        List<Address> addressList = newView.getMembers();
        getLogger().trace(".viewAccepted(): Got the Address set via view, now iterate through and see if one is suitable");
        if(getIPCChannel() != null) {
            getLogger().warn("JGroupsCluster->{}", getIPCChannel().getClusterName());
        }
        for(Address currentAddress: addressList){
            getLogger().warn("Visible Member->{}", currentAddress);
        }
        getLogger().trace(".viewAccepted(): Checking PubSub Participants");
        performPubSubParticipantCheck();
        getLogger().trace(".viewAccepted(): PubSub Participants check completed");
        getLogger().debug(".viewAccepted(): Exit");
    }

    @Override
    public void suspect(Address suspected_mbr) {
        Receiver.super.suspect(suspected_mbr);
    }

    @Override
    public void block() {
        Receiver.super.block();
    }

    @Override
    public void unblock() {
        Receiver.super.unblock();
    }

    @Override
    public void receive(MessageBatch batch) {
        getLogger().debug(".receive(): Entry, batch->{}", batch);
        Receiver.super.receive(batch);
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        getLogger().debug(".getState(): Entry, output->{}", output);
        Receiver.super.getState(output);
    }

    @Override
    public void setState(InputStream input) throws Exception {
        getLogger().debug(".setState(): Entry, input->{}", input);
        Receiver.super.setState(input);
    }

    public JChannel getIPCChannel() {
        return ipcChannel;
    }

    public void setIPCChannel(JChannel ipcChannel) {
        this.ipcChannel = ipcChannel;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }


    protected Address getTargetServiceAddress(String name){
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
                getLogger().debug(".isTargetAddressActive(): Exit, A match!, returning -true-");
                return(true);
            }
        }
        getLogger().debug(".isTargetAddressActive(): Exit, no matching Address found!");
        return(false);
    }

    protected Address getMyAddress(){
        if(getIPCChannel() != null){
            Address myAddress = getIPCChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }


}
