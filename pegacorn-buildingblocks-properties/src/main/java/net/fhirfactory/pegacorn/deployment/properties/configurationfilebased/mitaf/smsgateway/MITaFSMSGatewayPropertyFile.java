package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.mitaf.smsgateway;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.StandardServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact.StandardInteractServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.mitaf.MITaFSubsystemPropertyFile;

public class MITaFSMSGatewayPropertyFile extends MITaFSubsystemPropertyFile {

    private StandardInteractServerPortSegment smsGateway;

    private StandardServerPortSegment edgeReceiveCommunication;

    public StandardInteractServerPortSegment getSmsGateway() {
        return smsGateway;
    }

    public void setSmsGateway(StandardInteractServerPortSegment smsGateway) {
        this.smsGateway = smsGateway;
    }

    public StandardServerPortSegment getEdgeReceiveCommunication() {
        return edgeReceiveCommunication;
    }

    public void setEdgeReceiveCommunication(StandardServerPortSegment edgeReceiveCommunication) {
        this.edgeReceiveCommunication = edgeReceiveCommunication;
    }
}
