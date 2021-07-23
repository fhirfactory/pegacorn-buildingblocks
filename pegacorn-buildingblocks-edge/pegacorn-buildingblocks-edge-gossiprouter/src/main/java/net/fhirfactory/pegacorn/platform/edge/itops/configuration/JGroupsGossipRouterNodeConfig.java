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


import net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments.*;
import net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments.ports.base.StandardClusterServiceServerPortSegment;
import net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments.ports.standard.HTTPProcessingPlantServerPortSegment;

public class JGroupsGossipRouterNodeConfig {

    private SubsystemInstanceSegment subsystemInstant;
    private DeploymentModeSegment deploymentMode;
    private DeploymentSiteSegment deploymentSites;
    private DeploymentZoneSegment deploymentZone;
    private HTTPProcessingPlantServerPortSegment kubeReadinessProbe;
    private HTTPProcessingPlantServerPortSegment kubeLivelinessProbe;
    private HTTPProcessingPlantServerPortSegment prometheusPort;
    private HTTPProcessingPlantServerPortSegment jolokiaPort;
    private StandardClusterServiceServerPortSegment gossipRouterPort;
    private LoadBalancerSegment loadBalancer;
    private SubsystemImageSegment subsystemImageProperties;
    private SecurityCredentialSegment trustStorePassword;
    private SecurityCredentialSegment keyPassword;
    private JavaDeploymentSegment javaDeploymentParameters;
    private VolumeMountSegment volumeMounts;
    private SecurityCredentialSegment hapiAPIKey;

    public JGroupsGossipRouterNodeConfig() {
        subsystemInstant = new SubsystemInstanceSegment();
        deploymentMode = new DeploymentModeSegment();
        deploymentSites = new DeploymentSiteSegment();
        kubeLivelinessProbe = new HTTPProcessingPlantServerPortSegment();
        kubeReadinessProbe = new HTTPProcessingPlantServerPortSegment();
        subsystemImageProperties = new SubsystemImageSegment();
        trustStorePassword = new SecurityCredentialSegment();
        keyPassword = new SecurityCredentialSegment();
        jolokiaPort = new HTTPProcessingPlantServerPortSegment();
        prometheusPort = new HTTPProcessingPlantServerPortSegment();
        deploymentZone = new DeploymentZoneSegment();
        gossipRouterPort = new StandardClusterServiceServerPortSegment();
        loadBalancer = new LoadBalancerSegment();
        volumeMounts = new VolumeMountSegment();
        hapiAPIKey = new SecurityCredentialSegment();
    }

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

    public StandardClusterServiceServerPortSegment getGossipRouterPort() {
        return gossipRouterPort;
    }

    public void setGossipRouterPort(StandardClusterServiceServerPortSegment gossipRouterPort) {
        this.gossipRouterPort = gossipRouterPort;
    }

    public JavaDeploymentSegment getJavaDeploymentParameters() {
        return javaDeploymentParameters;
    }

    public void setJavaDeploymentParameters(JavaDeploymentSegment javaDeploymentParameters) {
        this.javaDeploymentParameters = javaDeploymentParameters;
    }

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
                ", gossipRouterPort=" + gossipRouterPort +
                ", subsystemImageProperties=" + subsystemImageProperties +
                ", trustStorePassword=" + trustStorePassword +
                ", keyPassword=" + keyPassword +
                ", javaDeploymentParameters=" + javaDeploymentParameters +
                '}';
    }
}
