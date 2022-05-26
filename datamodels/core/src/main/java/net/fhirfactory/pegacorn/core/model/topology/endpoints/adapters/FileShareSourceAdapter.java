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

public class FileShareSourceAdapter extends IPCAdapter {

    private String hostName;
    private String filePath;
    private String filePathAlias;
    private String filePathOnSuccessfulRead;
    private String filePathOnFailedRead;
    private boolean toBeMovedOnSuccessfulRead;
    private boolean toBeMovedOnFailedRead;

    //
    // Constructor(s)
    //

    public FileShareSourceAdapter(){
        super();
        this.filePath = null;
        this.filePathAlias = null;
        this.hostName = null;
        this.filePathOnFailedRead = null;
        this.filePathOnSuccessfulRead = null;
        this.toBeMovedOnFailedRead = false;
        this.toBeMovedOnSuccessfulRead = false;
    }

    //
    // Getters and Setters
    //

    public String getFilePathOnSuccessfulRead() {
        return filePathOnSuccessfulRead;
    }

    public void setFilePathOnSuccessfulRead(String filePathOnSuccessfulRead) {
        this.filePathOnSuccessfulRead = filePathOnSuccessfulRead;
    }

    public String getFilePathOnFailedRead() {
        return filePathOnFailedRead;
    }

    public void setFilePathOnFailedRead(String filePathOnFailedRead) {
        this.filePathOnFailedRead = filePathOnFailedRead;
    }

    public boolean isToBeMovedOnSuccessfulRead() {
        return toBeMovedOnSuccessfulRead;
    }

    public void setToBeMovedOnSuccessfulRead(boolean toBeMovedOnSuccessfulRead) {
        this.toBeMovedOnSuccessfulRead = toBeMovedOnSuccessfulRead;
    }

    public boolean isToBeMovedOnFailedRead() {
        return toBeMovedOnFailedRead;
    }

    public void setToBeMovedOnFailedRead(boolean toBeMovedOnFailedRead) {
        this.toBeMovedOnFailedRead = toBeMovedOnFailedRead;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePathAlias() {
        return filePathAlias;
    }

    public void setFilePathAlias(String filePathAlias) {
        this.filePathAlias = filePathAlias;
    }


    //
    // To String
    //

    @Override
    public String toString() {
        return "FileShareSourceAdapter{" +
                "hostName='" + hostName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", filePathAlias='" + filePathAlias + '\'' +
                ", filePathOnSuccessfulRead='" + filePathOnSuccessfulRead + '\'' +
                ", filePathOnFailedRead='" + filePathOnFailedRead + '\'' +
                ", toBeMovedOnSuccessfulRead=" + toBeMovedOnSuccessfulRead +
                ", toBeMovedOnFailedRead=" + toBeMovedOnFailedRead +
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
