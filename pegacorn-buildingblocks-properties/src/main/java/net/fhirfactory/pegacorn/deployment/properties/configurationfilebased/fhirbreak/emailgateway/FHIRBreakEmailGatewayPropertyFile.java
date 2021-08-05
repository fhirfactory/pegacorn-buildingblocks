package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.fhirbreak.emailgateway;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact.ClusterServiceInteractServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.StandardServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.fhirbreak.common.FHIRBreakSubsystemPropertyFile;

public class FHIRBreakEmailGatewayPropertyFile extends FHIRBreakSubsystemPropertyFile {

    private ClusterServiceInteractServerPortSegment smtpGateway;
    private ClusterServiceInteractServerPortSegment imapGateway;

    private StandardServerPortSegment edgeReceiveCommunication;

    public ClusterServiceInteractServerPortSegment getSmtpGateway() {
        return smtpGateway;
    }

    public void setSmtpGateway(ClusterServiceInteractServerPortSegment smtpGateway) {
        this.smtpGateway = smtpGateway;
    }

    public ClusterServiceInteractServerPortSegment getImapGateway() {
        return imapGateway;
    }

    public void setImapGateway(ClusterServiceInteractServerPortSegment imapGateway) {
        this.imapGateway = imapGateway;
    }

    public StandardServerPortSegment getEdgeReceiveCommunication() {
        return edgeReceiveCommunication;
    }

    public void setEdgeReceiveCommunication(StandardServerPortSegment edgeReceiveCommunication) {
        this.edgeReceiveCommunication = edgeReceiveCommunication;
    }
}
