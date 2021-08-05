package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.communicate.iris.im;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.SecurityCredentialSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact.HTTPClusteredServiceInteractServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.communicate.common.CommunicateSubsystemPropertyFile;

public class CommunicateIrisIMPropertyFile extends CommunicateSubsystemPropertyFile {


    private HTTPClusteredServiceInteractServerPortSegment interactMatrixApplicationServicesServer;
    private HTTPClusteredServiceInteractServerPortSegment interactPegacornVoIPServicesServer;
    private SecurityCredentialSegment wildflyUser;

    public CommunicateIrisIMPropertyFile(){
        super();
        wildflyUser = new SecurityCredentialSegment();
        interactMatrixApplicationServicesServer = new HTTPClusteredServiceInteractServerPortSegment();
        interactPegacornVoIPServicesServer = new HTTPClusteredServiceInteractServerPortSegment();
    }

    public HTTPClusteredServiceInteractServerPortSegment getInteractMatrixApplicationServicesServer() {
        return interactMatrixApplicationServicesServer;
    }

    public void setInteractMatrixApplicationServicesServer(HTTPClusteredServiceInteractServerPortSegment interactMatrixApplicationServicesServer) {
        this.interactMatrixApplicationServicesServer = interactMatrixApplicationServicesServer;
    }

    public HTTPClusteredServiceInteractServerPortSegment getInteractPegacornVoIPServicesServer() {
        return interactPegacornVoIPServicesServer;
    }

    public void setInteractPegacornVoIPServicesServer(HTTPClusteredServiceInteractServerPortSegment interactPegacornVoIPServicesServer) {
        this.interactPegacornVoIPServicesServer = interactPegacornVoIPServicesServer;
    }

    public SecurityCredentialSegment getWildflyUser() {
        return wildflyUser;
    }

    public void setWildflyUser(SecurityCredentialSegment wildflyUser) {
        this.wildflyUser = wildflyUser;
    }

    @Override
    public String toString() {
        return "CommunicateIrisIMPropertyFile{" +
                "interactMatrixApplicationServicesServer=" + interactMatrixApplicationServicesServer +
                ", interactPegacornVoIPServicesServer=" + interactPegacornVoIPServicesServer +
                ", wildflyUser=" + wildflyUser +
                "," + super.toString() +
                '}';
    }
}
