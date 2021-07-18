package net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common;

import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import org.codehaus.plexus.util.StringUtils;
import org.jgroups.Address;

public abstract class JGroupsIPCPubSubParticipant extends JGroupsIPCEndpoint{

    public JGroupsIPCPubSubParticipant(){
        super();
    }

    protected boolean isParticipantServiceAvailable(String participantServiceName){
        getLogger().debug(".isParticipantAvailable(): Entry, publisherServiceName->{}", participantServiceName);
        boolean participantIsAvailable = getTargetServiceAddress(participantServiceName) != null;
        getLogger().debug(".isParticipantAvailable(): Exit, returning->{}", participantIsAvailable);
        return(participantIsAvailable);
    }

    protected String getAvailableParticipantInstanceName(PubSubParticipant participant){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participant->{}", participant);
        if(participant == null){
            getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participant is null");
            return(null);
        }
        if(participant.getInterSubsystemParticipant() == null){
            getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participant.getInterSubsystemParticipant() is null");
            return(null);
        }
        if(participant.getInterSubsystemParticipant().getIdentifier() == null){
            getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participant.getInterSubsystemParticipant().getIdentifier() is null");
            return(null);
        }
        if(StringUtils.isEmpty(participant.getInterSubsystemParticipant().getIdentifier().getServiceName())){
            getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participant.getInterSubsystemParticipant().getIdentifier().getServiceName() is empty");
            return(null);
        }
        String publisherServiceName = participant.getInterSubsystemParticipant().getIdentifier().getServiceName();
        String serviceInstanceName = getAvailableParticipantInstanceName(publisherServiceName);
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, serviceInstanceName->{}", serviceInstanceName);
        return(serviceInstanceName);
    }

    public String getAvailableParticipantInstanceName(String participantServiceName){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participantServiceName->{}", participantServiceName);
        Address targetAddress = getTargetServiceAddress(participantServiceName);
        String participantInstanceName = targetAddress.toString();
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participantInstanceName->{}", participantInstanceName);
        return(participantInstanceName);
    }

    public boolean isParticipantInstanceAvailable(String participantInstanceName){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, participantInstanceName->{}", participantInstanceName);
        boolean participantInstanceNameStillActive = isTargetAddressActive(participantInstanceName);
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, participantInstanceNameStillActive->{}", participantInstanceNameStillActive);
        return(participantInstanceNameStillActive);
    }

    protected Address getAddressForParticipantService(PubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        Address address = getAddressForParticipantService(publisher.getInterSubsystemParticipant());
        return(address);
    }

    protected Address getAddressForParticipantService(InterSubsystemPubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        Address address = getAddressForParticipantService(publisher.getIdentifier());
        return(address);
    }

    protected Address getAddressForParticipantService(InterSubsystemPubSubParticipantIdentifier identifier){
        if(getIPCChannel() == null){
            return(null);
        }
        Address address = getTargetServiceAddress(identifier.getServiceName());
        return(address);
    }
}
