package net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups;

import org.apache.camel.CamelContext;
import org.jgroups.JChannel;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

public abstract class JGroupsEndpoint {

    private JChannel myChannel;
    private boolean initialised;

    @Inject
    CamelContext camelContext;

    public JGroupsEndpoint(){
        myChannel = null;
        initialised = false;
    }

    protected abstract String specifyChannelName();
    protected abstract String specifyGroupName();
    protected abstract Logger getLogger();

    @PostConstruct
    public void initialse(){
        if(!initialised) {
            this.myChannel = initialiseChannel(specifyChannelName(), specifyGroupName());
            this.initialised = true;
        }
    }

    private JChannel initialiseChannel(String channelName, String groupName){
        try {
            JChannel newChannel = new JChannel().name(channelName);
            newChannel.connect(groupName);
            this.myChannel = newChannel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(this.myChannel);
    }

    public String getCamelEndpoint(){
        String camelEndpoint = "";
        return(camelEndpoint);
    }

    protected String buildChannelName(String subsystemName, String subsystemVersion){
        String name = subsystemName+"-"+subsystemVersion+"-"+Long.toString(Date.from(Instant.now()).getTime());
        return(name);
    }

    public JChannel getMyChannel() {
        return myChannel;
    }

    public void setMyChannel(JChannel myChannel) {
        this.myChannel = myChannel;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public String getChannelName(){
        String channelName = this.getMyChannel().getName();
        return(channelName);
    }
}
