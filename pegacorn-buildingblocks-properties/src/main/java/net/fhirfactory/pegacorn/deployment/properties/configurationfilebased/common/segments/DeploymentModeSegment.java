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
package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class DeploymentModeSegment {
    private boolean kubernetes;
    private Integer processingPlantReplicationCount;
    private boolean concurrent;
    private boolean usingInternalEncryption;
    private String deploymentConfig;
    private String intraNetworkIPCStackConfigFile;
    private String intraNetworkOAMStackConfigFile;
    private String interNetworkIPCStackConfigFile;
    private String interNetworkOAMStackConfigFile;

    public DeploymentModeSegment(){
        kubernetes = false;
        processingPlantReplicationCount = 0;
        concurrent = false;
        usingInternalEncryption = false;
        deploymentConfig = null;
        this.intraNetworkIPCStackConfigFile = null;
        this.intraNetworkOAMStackConfigFile = null;
        this.interNetworkIPCStackConfigFile = null;
        this.interNetworkOAMStackConfigFile = null;
    }

    public void mergeOverrides(DeploymentModeSegment overrides){
        this.kubernetes = overrides.isKubernetes();
        this.concurrent = overrides.isConcurrent();
        this.usingInternalEncryption = overrides.isUsingInternalEncryption();
        if(overrides.getProcessingPlantReplicationCount() > 0){
            this.processingPlantReplicationCount = overrides.getProcessingPlantReplicationCount();
        }
        if(!StringUtils.isEmpty(overrides.getDeploymentConfig())){
            this.deploymentConfig = overrides.getDeploymentConfig();
        }
        if(!StringUtils.isEmpty(overrides.getInterNetworkIPCStackConfigFile())){
            this.setInterNetworkIPCStackConfigFile(overrides.getInterNetworkIPCStackConfigFile());
        }
        if(!StringUtils.isEmpty(overrides.getInterNetworkOAMStackConfigFile())){
            this.setInterNetworkOAMStackConfigFile(overrides.getInterNetworkOAMStackConfigFile());
        }
        if(!StringUtils.isEmpty(overrides.getIntraNetworkIPCStackConfigFile())){
            this.setIntraNetworkIPCStackConfigFile(overrides.getIntraNetworkIPCStackConfigFile());
        }
        if(!StringUtils.isEmpty(overrides.getIntraNetworkOAMStackConfigFile())){
            this.setIntraNetworkOAMStackConfigFile(overrides.getIntraNetworkOAMStackConfigFile());
        }
    }

    public String getIntraNetworkIPCStackConfigFile() {
        return intraNetworkIPCStackConfigFile;
    }

    public void setIntraNetworkIPCStackConfigFile(String intraNetworkIPCStackConfigFile) {
        this.intraNetworkIPCStackConfigFile = intraNetworkIPCStackConfigFile;
    }

    public String getIntraNetworkOAMStackConfigFile() {
        return intraNetworkOAMStackConfigFile;
    }

    public void setIntraNetworkOAMStackConfigFile(String intraNetworkOAMStackConfigFile) {
        this.intraNetworkOAMStackConfigFile = intraNetworkOAMStackConfigFile;
    }

    public String getInterNetworkIPCStackConfigFile() {
        return interNetworkIPCStackConfigFile;
    }

    public void setInterNetworkIPCStackConfigFile(String interNetworkIPCStackConfigFile) {
        this.interNetworkIPCStackConfigFile = interNetworkIPCStackConfigFile;
    }

    public String getInterNetworkOAMStackConfigFile() {
        return interNetworkOAMStackConfigFile;
    }

    public void setInterNetworkOAMStackConfigFile(String interNetworkOAMStackConfigFile) {
        this.interNetworkOAMStackConfigFile = interNetworkOAMStackConfigFile;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeploymentModeSegment)) return false;
        DeploymentModeSegment that = (DeploymentModeSegment) o;
        return isKubernetes() == that.isKubernetes() && isConcurrent() == that.isConcurrent() && isUsingInternalEncryption() == that.isUsingInternalEncryption() && Objects.equals(getProcessingPlantReplicationCount(), that.getProcessingPlantReplicationCount()) && Objects.equals(getDeploymentConfig(), that.getDeploymentConfig()) && Objects.equals(getIntraNetworkIPCStackConfigFile(), that.getIntraNetworkIPCStackConfigFile()) && Objects.equals(getIntraNetworkOAMStackConfigFile(), that.getIntraNetworkOAMStackConfigFile()) && Objects.equals(getInterNetworkIPCStackConfigFile(), that.getInterNetworkIPCStackConfigFile()) && Objects.equals(getInterNetworkOAMStackConfigFile(), that.getInterNetworkOAMStackConfigFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isKubernetes(), getProcessingPlantReplicationCount(), isConcurrent(), isUsingInternalEncryption(), getDeploymentConfig(), getIntraNetworkIPCStackConfigFile(), getIntraNetworkOAMStackConfigFile(), getInterNetworkIPCStackConfigFile(), getInterNetworkOAMStackConfigFile());
    }

    @Override
    public String toString() {
        return "DeploymentModeSegment{" +
                "kubernetes=" + kubernetes +
                ", processingPlantReplicationCount=" + processingPlantReplicationCount +
                ", concurrent=" + concurrent +
                ", usingInternalEncryption=" + usingInternalEncryption +
                ", deploymentConfig='" + deploymentConfig + '\'' +
                ", intraNetworkIPCStackConfigFile='" + intraNetworkIPCStackConfigFile + '\'' +
                ", intraNetworkOAMStackConfigFile='" + intraNetworkOAMStackConfigFile + '\'' +
                ", interNetworkIPCStackConfigFile='" + interNetworkIPCStackConfigFile + '\'' +
                ", interNetworkOAMStackConfigFile='" + interNetworkOAMStackConfigFile + '\'' +
                '}';
    }
}
