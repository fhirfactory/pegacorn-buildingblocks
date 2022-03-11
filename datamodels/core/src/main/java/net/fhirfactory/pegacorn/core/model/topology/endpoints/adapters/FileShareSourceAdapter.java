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

    private String filePath;
    private String filePathAlias;

    //
    // Constructor(s)
    //

    public FileShareSourceAdapter(){
        super();
        this.filePath = null;
        this.filePathAlias = null;
    }

    //
    // Getters and Setters
    //

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
                "filePath='" + filePath + '\'' +
                "filePathAlias='" + filePathAlias + '\'' +
                ", supportedDeploymentModes=" + getSupportedDeploymentModes() +
                ", targetNameInstant='" + getTargetSystemName() + '\'' +
                ", enablingTopologyEndpoint=" + getEnablingTopologyEndpoint() +
                ", supportedInterfaceDefinitions=" + getSupportedInterfaceDefinitions() +
                ", supportInterfaceTags=" + getSupportInterfaceTags() +
                ", encrypted=" + isEncrypted() +
                ", groupName='" + getGroupName() + '\'' +
                ", active=" + isActive() +
                ", additionalParameters=" + getAdditionalParameters() +
                ", lastActivity=" + getLastActivity() + '\'' +
                '}';
    }
}
