package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc.HTTPIPCServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc.JGroupsIPCServerPortSegment;

public class PetasosEnabledSubsystemPropertyFile extends ClusterServiceDeliverySubsystemPropertyFile{
    private JGroupsIPCServerPortSegment interZoneIPC;
    private JGroupsIPCServerPortSegment intraZoneIPC;
    private JGroupsIPCServerPortSegment interZoneOAM;
    private JGroupsIPCServerPortSegment intraZoneOAM;
    private HTTPIPCServerPortSegment edgeAnswer;

    public PetasosEnabledSubsystemPropertyFile(){
        super();
        edgeAnswer = new HTTPIPCServerPortSegment();
        interZoneIPC = new JGroupsIPCServerPortSegment();
        intraZoneIPC = new JGroupsIPCServerPortSegment();
        interZoneOAM = new JGroupsIPCServerPortSegment();
        intraZoneOAM = new JGroupsIPCServerPortSegment();
    }

    public JGroupsIPCServerPortSegment getInterZoneIPC() {
        return interZoneIPC;
    }

    public void setInterZoneIPC(JGroupsIPCServerPortSegment interZoneIPC) {
        this.interZoneIPC = interZoneIPC;
    }

    public HTTPIPCServerPortSegment getEdgeAnswer() {
        return edgeAnswer;
    }

    public void setEdgeAnswer(HTTPIPCServerPortSegment edgeAnswer) {
        this.edgeAnswer = edgeAnswer;
    }

    public JGroupsIPCServerPortSegment getIntraZoneIPC() {
        return intraZoneIPC;
    }

    public void setIntraZoneIPC(JGroupsIPCServerPortSegment intraZoneIPC) {
        this.intraZoneIPC = intraZoneIPC;
    }

    public JGroupsIPCServerPortSegment getInterZoneOAM() {
        return interZoneOAM;
    }

    public void setInterZoneOAM(JGroupsIPCServerPortSegment interZoneOAM) {
        this.interZoneOAM = interZoneOAM;
    }

    public JGroupsIPCServerPortSegment getIntraZoneOAM() {
        return intraZoneOAM;
    }

    public void setIntraZoneOAM(JGroupsIPCServerPortSegment intraZoneOAM) {
        this.intraZoneOAM = intraZoneOAM;
    }

    @Override
    public String toString() {
        return "PetasosEnabledSubsystemPropertyFile{" +
                "defaultServicePortLowerBound=" + getDefaultServicePortLowerBound() +
                ", loadBalancer=" + getLoadBalancer() +
                ", volumeMounts=" + getVolumeMounts() +
                ", debugProperties=" + getDebugProperties() +
                ", hapiAPIKey=" + getHapiAPIKey() +
                ", javaDeploymentParameters=" + getJavaDeploymentParameters() +
                ", subsystemInstant=" + getSubsystemInstant() +
                ", deploymentMode=" + getDeploymentMode() +
                ", deploymentSites=" + getDeploymentSites() +
                ", kubeReadinessProbe=" + getKubeReadinessProbe() +
                ", kubeLivelinessProbe=" + getKubeLivelinessProbe() +
                ", prometheusPort=" + getPrometheusPort() +
                ", jolokiaPort=" + getJolokiaPort() +
                ", subsystemImageProperties=" + getSubsystemImageProperties() +
                ", trustStorePassword=" + getTrustStorePassword() +
                ", keyPassword=" + getKeyPassword() +
                ", interZoneIPC=" + getInterZoneIPC() +
                ", intraZoneIPC=" + getIntraZoneIPC() +
                ", interZoneOAM=" + getInterZoneOAM() +
                ", intraZoneOAM=" + getIntraZoneOAM() +
                ", edgeAnswer=" + getEdgeAnswer() +
                '}';
    }
}
