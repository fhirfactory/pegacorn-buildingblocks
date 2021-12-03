/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.platform.edge.itops.configuration;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.DeploymentModeSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.DeploymentSiteSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.DeploymentZoneSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.JavaDeploymentSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.LoadBalancerSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.SecurityCredentialSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.SubsystemImageSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.SubsystemInstanceSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.VolumeMountSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.StandardClusterServiceServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact.ClusteredInteractServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc.JGroupsInterZoneRepeaterServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.standard.HTTPProcessingPlantServerPortSegment;


public class JGroupsGossipRouterNodeConfig {

    private SubsystemInstanceSegment subsystemInstant;
    private DeploymentModeSegment deploymentMode;
    private DeploymentSiteSegment deploymentSites;
    private DeploymentZoneSegment deploymentZone;
    private HTTPProcessingPlantServerPortSegment kubeReadinessProbe;
    private HTTPProcessingPlantServerPortSegment kubeLivelinessProbe;
    private HTTPProcessingPlantServerPortSegment prometheusPort;
    private HTTPProcessingPlantServerPortSegment jolokiaPort;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterIPC;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterTasking;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterTopology;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterSubscriptions;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterInterception;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterMetrics;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterInfinspan;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterDatagrid;
    private JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterAudit;
    private LoadBalancerSegment loadBalancer;
    private SubsystemImageSegment subsystemImageProperties;
    private SecurityCredentialSegment trustStorePassword;
    private SecurityCredentialSegment keyPassword;
    private JavaDeploymentSegment javaDeploymentParameters;
    private VolumeMountSegment volumeMounts;
    private SecurityCredentialSegment hapiAPIKey;

    //
    // Constructor(s)
    //

    public JGroupsGossipRouterNodeConfig() {
        this.subsystemInstant = new SubsystemInstanceSegment();
        this.deploymentMode = new DeploymentModeSegment();
        this.deploymentSites = new DeploymentSiteSegment();
        this.kubeLivelinessProbe = new HTTPProcessingPlantServerPortSegment();
        this.kubeReadinessProbe = new HTTPProcessingPlantServerPortSegment();
        this.subsystemImageProperties = new SubsystemImageSegment();
        this.trustStorePassword = new SecurityCredentialSegment();
        this.keyPassword = new SecurityCredentialSegment();
        this.jolokiaPort = new HTTPProcessingPlantServerPortSegment();
        this.prometheusPort = new HTTPProcessingPlantServerPortSegment();
        this.deploymentZone = new DeploymentZoneSegment();
        this.multizoneRepeaterAudit = new JGroupsInterZoneRepeaterServerPortSegment();
        this.multizoneRepeaterDatagrid = new JGroupsInterZoneRepeaterServerPortSegment();
        this.multizoneRepeaterInfinspan = new JGroupsInterZoneRepeaterServerPortSegment();
        this.multizoneRepeaterInterception = new JGroupsInterZoneRepeaterServerPortSegment();
        this.multizoneRepeaterIPC = new JGroupsInterZoneRepeaterServerPortSegment();
        this.multizoneRepeaterMetrics = new JGroupsInterZoneRepeaterServerPortSegment();
        this.multizoneRepeaterSubscriptions = new JGroupsInterZoneRepeaterServerPortSegment();
        this.multizoneRepeaterTasking = new JGroupsInterZoneRepeaterServerPortSegment();
        this.multizoneRepeaterTopology = new JGroupsInterZoneRepeaterServerPortSegment();
        this.loadBalancer = new LoadBalancerSegment();
        this.volumeMounts = new VolumeMountSegment();
        this.hapiAPIKey = new SecurityCredentialSegment();
    }

    //
    // Getters and Setters
    //

    public SecurityCredentialSegment getHapiAPIKey() {
        return hapiAPIKey;
    }

    public void setHapiAPIKey(SecurityCredentialSegment hapiAPIKey) {
        this.hapiAPIKey = hapiAPIKey;
    }

    public VolumeMountSegment getVolumeMounts() {
        return volumeMounts;
    }

    public void setVolumeMounts(VolumeMountSegment volumeMounts) {
        this.volumeMounts = volumeMounts;
    }

    public LoadBalancerSegment getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancerSegment loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public HTTPProcessingPlantServerPortSegment getKubeReadinessProbe() {
        return kubeReadinessProbe;
    }

    public void setKubeReadinessProbe(HTTPProcessingPlantServerPortSegment kubeReadinessProbe) {
        this.kubeReadinessProbe = kubeReadinessProbe;
    }

    public HTTPProcessingPlantServerPortSegment getKubeLivelinessProbe() {
        return kubeLivelinessProbe;
    }

    public void setKubeLivelinessProbe(HTTPProcessingPlantServerPortSegment kubeLivelinessProbe) {
        this.kubeLivelinessProbe = kubeLivelinessProbe;
    }

    public HTTPProcessingPlantServerPortSegment getPrometheusPort() {
        return prometheusPort;
    }

    public void setPrometheusPort(HTTPProcessingPlantServerPortSegment prometheusPort) {
        this.prometheusPort = prometheusPort;
    }

    public HTTPProcessingPlantServerPortSegment getJolokiaPort() {
        return jolokiaPort;
    }

    public void setJolokiaPort(HTTPProcessingPlantServerPortSegment jolokiaPort) {
        this.jolokiaPort = jolokiaPort;
    }

    public SubsystemInstanceSegment getSubsystemInstant() {
        return subsystemInstant;
    }

    public void setSubsystemInstant(SubsystemInstanceSegment subsystemInstant) {
        this.subsystemInstant = subsystemInstant;
    }

    public DeploymentModeSegment getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(DeploymentModeSegment deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    public DeploymentSiteSegment getDeploymentSites() {
        return deploymentSites;
    }

    public void setDeploymentSites(DeploymentSiteSegment deploymentSites) {
        this.deploymentSites = deploymentSites;
    }


    public SubsystemImageSegment getSubsystemImageProperties() {
        return subsystemImageProperties;
    }

    public void setSubsystemImageProperties(SubsystemImageSegment subsystemImageProperties) {
        this.subsystemImageProperties = subsystemImageProperties;
    }

    public SecurityCredentialSegment getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(SecurityCredentialSegment trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public SecurityCredentialSegment getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(SecurityCredentialSegment keyPassword) {
        this.keyPassword = keyPassword;
    }

    public DeploymentZoneSegment getDeploymentZone() {
        return deploymentZone;
    }

    public void setDeploymentZone(DeploymentZoneSegment deploymentZone) {
        this.deploymentZone = deploymentZone;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterIPC() {
        return multizoneRepeaterIPC;
    }

    public void setMultizoneRepeaterIPC(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterIPC) {
        this.multizoneRepeaterIPC = multizoneRepeaterIPC;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterTasking() {
        return multizoneRepeaterTasking;
    }

    public void setMultizoneRepeaterTasking(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterTasking) {
        this.multizoneRepeaterTasking = multizoneRepeaterTasking;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterTopology() {
        return multizoneRepeaterTopology;
    }

    public void setMultizoneRepeaterTopology(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterTopology) {
        this.multizoneRepeaterTopology = multizoneRepeaterTopology;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterSubscriptions() {
        return multizoneRepeaterSubscriptions;
    }

    public void setMultizoneRepeaterSubscriptions(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterSubscriptions) {
        this.multizoneRepeaterSubscriptions = multizoneRepeaterSubscriptions;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterInterception() {
        return multizoneRepeaterInterception;
    }

    public void setMultizoneRepeaterInterception(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterInterception) {
        this.multizoneRepeaterInterception = multizoneRepeaterInterception;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterMetrics() {
        return multizoneRepeaterMetrics;
    }

    public void setMultizoneRepeaterMetrics(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterMetrics) {
        this.multizoneRepeaterMetrics = multizoneRepeaterMetrics;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterInfinspan() {
        return multizoneRepeaterInfinspan;
    }

    public void setMultizoneRepeaterInfinspan(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterInfinspan) {
        this.multizoneRepeaterInfinspan = multizoneRepeaterInfinspan;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterDatagrid() {
        return multizoneRepeaterDatagrid;
    }

    public void setMultizoneRepeaterDatagrid(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterDatagrid) {
        this.multizoneRepeaterDatagrid = multizoneRepeaterDatagrid;
    }

    public JGroupsInterZoneRepeaterServerPortSegment getMultizoneRepeaterAudit() {
        return multizoneRepeaterAudit;
    }

    public void setMultizoneRepeaterAudit(JGroupsInterZoneRepeaterServerPortSegment multizoneRepeaterAudit) {
        this.multizoneRepeaterAudit = multizoneRepeaterAudit;
    }

    public JavaDeploymentSegment getJavaDeploymentParameters() {
        return javaDeploymentParameters;
    }

    public void setJavaDeploymentParameters(JavaDeploymentSegment javaDeploymentParameters) {
        this.javaDeploymentParameters = javaDeploymentParameters;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "JGroupsGossipRouterNodeConfig{" +
                "subsystemInstant=" + subsystemInstant +
                ", deploymentMode=" + deploymentMode +
                ", deploymentSites=" + deploymentSites +
                ", deploymentZone=" + deploymentZone +
                ", kubeReadinessProbe=" + kubeReadinessProbe +
                ", kubeLivelinessProbe=" + kubeLivelinessProbe +
                ", prometheusPort=" + prometheusPort +
                ", jolokiaPort=" + jolokiaPort +
                ", multizoneRepeaterIPC=" + multizoneRepeaterIPC +
                ", multizoneRepeaterTasking=" + multizoneRepeaterTasking +
                ", multizoneRepeaterTopology=" + multizoneRepeaterTopology +
                ", multizoneRepeaterSubscriptions=" + multizoneRepeaterSubscriptions +
                ", multizoneRepeaterInterception=" + multizoneRepeaterInterception +
                ", multizoneRepeaterMetrics=" + multizoneRepeaterMetrics +
                ", multizoneRepeaterInfinspan=" + multizoneRepeaterInfinspan +
                ", multizoneRepeaterDatagrid=" + multizoneRepeaterDatagrid +
                ", multizoneRepeaterAudit=" + multizoneRepeaterAudit +
                ", loadBalancer=" + loadBalancer +
                ", subsystemImageProperties=" + subsystemImageProperties +
                ", trustStorePassword=" + trustStorePassword +
                ", keyPassword=" + keyPassword +
                ", javaDeploymentParameters=" + javaDeploymentParameters +
                ", volumeMounts=" + volumeMounts +
                ", hapiAPIKey=" + hapiAPIKey +
                '}';
    }
}
