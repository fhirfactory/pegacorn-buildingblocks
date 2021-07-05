package net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common;

import net.fhirfactory.pegacorn.petasos.model.pubsub.DistributedPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.petasos.model.pubsub.DistributedPubSubPublisher;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubPublisher;
import org.jgroups.*;
import org.jgroups.util.MessageBatch;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class JGroupsEndpoint implements Receiver{

    private JChannel ipcChannel;
    private boolean initialised;

    public JGroupsEndpoint(){
        this.setIPCChannel(null);
        this.setInitialised(false);
    }

    @Override
    public void receive(Message message) {

    }

    @Override
    public void viewAccepted(View new_view) {
        Receiver.super.viewAccepted(new_view);
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
        Receiver.super.receive(batch);
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        Receiver.super.getState(output);
    }

    @Override
    public void setState(InputStream input) throws Exception {
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

    protected Address getTargetAddress(PubSubPublisher publisher){
        if(publisher == null){
            return(null);
        }
        Address address = getTargetAddress(publisher.getDistributedPublisher());
        return(address);
    }

    protected Address getTargetAddress(DistributedPubSubPublisher publisher){
        if(publisher == null){
            return(null);
        }
        Address address = getTargetAddress(publisher.getIdentifier());
        return(address);
    }

    protected Address getTargetAddress(DistributedPubSubParticipantIdentifier identifier){
        if(getIPCChannel() == null){
            return(null);
        }
        Address address = getTargetAddress(identifier.getSubsystemName());
        return(address);
    }

    protected Address getTargetAddress(String name){
        if(getIPCChannel() == null){
            return(null);
        }
        List<Address> addressList = getIPCChannel().getView().getMembers();
        for(Address currentAddress: addressList){
            if(currentAddress.toString().startsWith(name, name.length())){
                return(currentAddress);
            }
        }
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
