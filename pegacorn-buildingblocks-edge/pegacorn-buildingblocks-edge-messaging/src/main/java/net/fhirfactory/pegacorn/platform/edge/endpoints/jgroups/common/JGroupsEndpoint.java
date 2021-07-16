package net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common;

import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
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

    protected Logger getLogger(){
        return(specifyLogger());
    }

    @Override
    public void receive(Message message) {
        getLogger().debug(".receive(): Entry, message->{}", message);

    }

    @Override
    public void viewAccepted(View newView) {
        getLogger().info(".viewAccepted(): Entry, newView->{}", newView);
        Receiver.super.viewAccepted(newView);
        getLogger().info(".viewAccepted(): Woo Hoo, we're here!!!!!!");
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

    protected Address getTargetAddress(PubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        Address address = getTargetAddress(publisher.getInterSubsystemParticipant());
        return(address);
    }

    protected Address getTargetAddress(InterSubsystemPubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        Address address = getTargetAddress(publisher.getIdentifier());
        return(address);
    }

    protected Address getTargetAddress(InterSubsystemPubSubParticipantIdentifier identifier){
        if(getIPCChannel() == null){
            return(null);
        }
        Address address = getTargetAddress(identifier.getServiceName());
        return(address);
    }

    protected Address getTargetAddress(String name){
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

    protected Address getMyAddress(){
        if(getIPCChannel() != null){
            Address myAddress = getIPCChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }


}
