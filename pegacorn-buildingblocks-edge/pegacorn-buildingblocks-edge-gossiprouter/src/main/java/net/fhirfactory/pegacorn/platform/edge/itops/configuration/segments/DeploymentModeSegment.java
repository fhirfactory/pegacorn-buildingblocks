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
package net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments;

import org.apache.commons.lang3.StringUtils;

public class DeploymentModeSegment {
    private boolean kubernetes;
    private Integer processingPlantReplicationCount;
    private boolean concurrent;
    private boolean usingInternalEncryption;
    private String deploymentConfig;


    public DeploymentModeSegment(){
        kubernetes = false;
        processingPlantReplicationCount = 0;
        concurrent = false;
        usingInternalEncryption = false;
        deploymentConfig = null;
    }

    public void mergeOverrides(DeploymentModeSegment overrides){
        this.kubernetes = overrides.isKubernetes();
        this.concurrent = overrides.isConcurrent();
        this.usingInternalEncryption = overrides.isUsingInternalEncryption();
        if(overrides.getProcessingPlantReplicationCount() > 0){
            this.processingPlantReplicationCount = overrides.getProcessingPlantReplicationCount();
        }
        if(!StringUtils.isEmpty(deploymentConfig)){
            this.deploymentConfig = overrides.getDeploymentConfig();
        }
    }

    public String getDeploymentConfig() {
        return deploymentConfig;
    }

    public void setDeploymentConfig(String deploymentConfig) {
        this.deploymentConfig = deploymentConfig;
    }

    public boolean isKubernetes() {
        return kubernetes;
    }

    public void setKubernetes(boolean kubernetes) {
        this.kubernetes = kubernetes;
    }

    public Integer getProcessingPlantReplicationCount() {
        return processingPlantReplicationCount;
    }

    public void setProcessingPlantReplicationCount(Integer processingPlantReplicationCount) {
        this.processingPlantReplicationCount = processingPlantReplicationCount;
    }

    public boolean isConcurrent() {
        return concurrent;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public boolean isUsingInternalEncryption() {
        return usingInternalEncryption;
    }

    public void setUsingInternalEncryption(boolean usingInternalEncryption) {
        this.usingInternalEncryption = usingInternalEncryption;
    }

    @Override
    public String toString() {
        return "DeploymentModeSegment{" +
                "kubernetes=" + kubernetes +
                ", processingPlantReplicationCount=" + processingPlantReplicationCount +
                ", concurrent=" + concurrent +
                ", usingInternalEncryption=" + usingInternalEncryption +
                ", deploymentConfig='" + deploymentConfig + '\'' +
                '}';
    }
}
