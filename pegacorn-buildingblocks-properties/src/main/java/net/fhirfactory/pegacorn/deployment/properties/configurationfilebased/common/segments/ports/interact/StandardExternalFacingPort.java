package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.connectedsystems.ConnectedSystemProperties;
import org.slf4j.Logger;

public abstract class StandardExternalFacingPort {

    protected abstract Logger specifyLogger();

    protected Logger getLogger(){
        return(specifyLogger());
    }

    private String portType;
    private ConnectedSystemProperties connectedSystem;
    private int startupDelay;
    private String name;
    private boolean server;

    public StandardExternalFacingPort(){
        super();
        this.connectedSystem = new ConnectedSystemProperties();
        this.name = null;
        this.portType = null;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public ConnectedSystemProperties getConnectedSystem() {
        return connectedSystem;
    }

    public void setConnectedSystem(ConnectedSystemProperties connectedSystem) {
        this.connectedSystem = connectedSystem;
    }

    public int getStartupDelay() {
        return startupDelay;
    }

    public void setStartupDelay(int startupDelay) {
        this.startupDelay = startupDelay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }
}
