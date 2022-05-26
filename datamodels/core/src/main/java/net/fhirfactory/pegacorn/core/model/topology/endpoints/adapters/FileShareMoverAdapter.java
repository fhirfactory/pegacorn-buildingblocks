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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;

public class FileShareMoverAdapter extends IPCAdapter {

    private String hostName;
    private String sourceFilePath;
    private String targetFilePathForSuccessfulActivity;
    private String targetFilePathForFailedActivity;
    private boolean toBeDeletedIfSuccessfulActivity;
    private boolean toBeDeletedIfFailedActivity;

    //
    // Constructor(s)
    //

    public FileShareMoverAdapter(){
        super();
        this.sourceFilePath = null;
        this.hostName = null;
        this.targetFilePathForSuccessfulActivity = null;
        this.targetFilePathForFailedActivity = null;
        this.toBeDeletedIfSuccessfulActivity = false;
        this.toBeDeletedIfFailedActivity = false;
    }

    //
    // Getters and Setters
    //

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getTargetFilePathForSuccessfulActivity() {
        return targetFilePathForSuccessfulActivity;
    }

    public void setTargetFilePathForSuccessfulActivity(String targetFilePathForSuccessfulActivity) {
        this.targetFilePathForSuccessfulActivity = targetFilePathForSuccessfulActivity;
    }

    public String getTargetFilePathForFailedActivity() {
        return targetFilePathForFailedActivity;
    }

    public void setTargetFilePathForFailedActivity(String targetFilePathForFailedActivity) {
        this.targetFilePathForFailedActivity = targetFilePathForFailedActivity;
    }

    public boolean isToBeDeletedIfSuccessfulActivity() {
        return toBeDeletedIfSuccessfulActivity;
    }

    public void setToBeDeletedIfSuccessfulActivity(boolean toBeDeletedIfSuccessfulActivity) {
        this.toBeDeletedIfSuccessfulActivity = toBeDeletedIfSuccessfulActivity;
    }

    public boolean isToBeDeletedIfFailedActivity() {
        return toBeDeletedIfFailedActivity;
    }

    public void setToBeDeletedIfFailedActivity(boolean toBeDeletedIfFailedActivity) {
        this.toBeDeletedIfFailedActivity = toBeDeletedIfFailedActivity;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "FileShareMoverAdapter{" +
                "hostName='" + hostName + '\'' +
                ", sourceFilePath='" + sourceFilePath + '\'' +
                ", targetFilePathForSuccessfulActivity='" + targetFilePathForSuccessfulActivity + '\'' +
                ", targetFilePathForFailedActivity='" + targetFilePathForFailedActivity + '\'' +
                ", toBeDeletedIfSuccessfulActivity=" + toBeDeletedIfSuccessfulActivity +
                ", toBeDeletedIfFailedActivity=" + toBeDeletedIfFailedActivity +
                ", supportedDeploymentModes=" + getSupportedDeploymentModes() +
                ", targetSystemName='" + getTargetSystemName() + '\'' +
                ", enablingTopologyEndpoint=" + getEnablingTopologyEndpoint() +
                ", supportedInterfaceDefinitions=" + getSupportedInterfaceDefinitions() +
                ", supportInterfaceTags=" + getSupportInterfaceTags() +
                ", encrypted=" + isEncrypted() +
                ", groupName='" + getGroupName() + '\'' +
                ", active=" + isActive() +
                ", additionalParameters=" + getAdditionalParameters() +
                ", lastActivity=" + getLastActivity() +
                '}';
    }
}
