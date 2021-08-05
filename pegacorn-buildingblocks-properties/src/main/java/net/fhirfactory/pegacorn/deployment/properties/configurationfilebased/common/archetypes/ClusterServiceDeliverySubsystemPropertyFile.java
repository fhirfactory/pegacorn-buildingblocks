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
package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.*;

public abstract class ClusterServiceDeliverySubsystemPropertyFile extends BaseSubsystemPropertyFile{

    private Integer defaultServicePortLowerBound;
    private LoadBalancerSegment loadBalancer;
    private VolumeMountSegment volumeMounts;
    private ApplicationDebugSegment debugProperties;
    private SecurityCredentialSegment hapiAPIKey;
    private JavaDeploymentSegment javaDeploymentParameters;

    public ClusterServiceDeliverySubsystemPropertyFile(){
        super();
        loadBalancer = new LoadBalancerSegment();
        volumeMounts = new VolumeMountSegment();
        debugProperties = new ApplicationDebugSegment();
        hapiAPIKey = new SecurityCredentialSegment();
        javaDeploymentParameters = new JavaDeploymentSegment();
        defaultServicePortLowerBound = null;
    }

    boolean hasDefaultServicePortLowerBound(){
        boolean has = defaultServicePortLowerBound != null;
        return(has);
    }

    public Integer getDefaultServicePortLowerBound() {
        return defaultServicePortLowerBound;
    }

    public void setDefaultServicePortLowerBound(Integer defaultServicePortLowerBound) {
        this.defaultServicePortLowerBound = defaultServicePortLowerBound;
    }

    public LoadBalancerSegment getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancerSegment loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public VolumeMountSegment getVolumeMounts() {
        return volumeMounts;
    }

    public void setVolumeMounts(VolumeMountSegment volumeMounts) {
        this.volumeMounts = volumeMounts;
    }

    public ApplicationDebugSegment getDebugProperties() {
        return debugProperties;
    }

    public void setDebugProperties(ApplicationDebugSegment debugProperties) {
        this.debugProperties = debugProperties;
    }

    public SecurityCredentialSegment getHapiAPIKey() {
        return hapiAPIKey;
    }

    public void setHapiAPIKey(SecurityCredentialSegment hapiAPIKey) {
        this.hapiAPIKey = hapiAPIKey;
    }

    public JavaDeploymentSegment getJavaDeploymentParameters() {
        return javaDeploymentParameters;
    }

    public void setJavaDeploymentParameters(JavaDeploymentSegment javaDeploymentParameters) {
        this.javaDeploymentParameters = javaDeploymentParameters;
    }

    @Override
    public String toString() {
        return "ClusterServiceDeliverySubsystemPropertyFile{" +
                "defaultServicePortLowerBound=" + defaultServicePortLowerBound +
                ", loadBalancer=" + loadBalancer +
                ", volumeMounts=" + volumeMounts +
                ", debugProperties=" + debugProperties +
                ", hapiAPIKey=" + hapiAPIKey +
                ", javaDeploymentParameters=" + javaDeploymentParameters +
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
                '}';
    }
}
