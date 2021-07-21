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
package net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments.connectedsystems;

public class ConnectedSystemProperties {
    private String owningOrganizationName;
    private String solutionName;
    private String subsystemName;
    private String subsystemVersion;
    private String externalisedServiceName;
    private String externalisedServiceEndpointName;
    private ConnectedSystemPort targetPort1;
    private ConnectedSystemPort targetPort2;
    private ConnectedSystemPort targetPort3;

    public ConnectedSystemPort getTargetPort1() {
        return targetPort1;
    }

    public void setTargetPort1(ConnectedSystemPort targetPort1) {
        this.targetPort1 = targetPort1;
    }

    public ConnectedSystemPort getTargetPort2() {
        return targetPort2;
    }

    public void setTargetPort2(ConnectedSystemPort targetPort2) {
        this.targetPort2 = targetPort2;
    }

    public ConnectedSystemPort getTargetPort3() {
        return targetPort3;
    }

    public void setTargetPort3(ConnectedSystemPort targetPort3) {
        this.targetPort3 = targetPort3;
    }

    public String getOwningOrganizationName() {
        return owningOrganizationName;
    }

    public void setOwningOrganizationName(String owningOrganizationName) {
        this.owningOrganizationName = owningOrganizationName;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public String getSubsystemVersion() {
        return subsystemVersion;
    }

    public void setSubsystemVersion(String subsystemVersion) {
        this.subsystemVersion = subsystemVersion;
    }

    public String getExternalisedServiceName() {
        return externalisedServiceName;
    }

    public void setExternalisedServiceName(String externalisedServiceName) {
        this.externalisedServiceName = externalisedServiceName;
    }

    public String getExternalisedServiceEndpointName() {
        return externalisedServiceEndpointName;
    }

    public void setExternalisedServiceEndpointName(String externalisedServiceEndpointName) {
        this.externalisedServiceEndpointName = externalisedServiceEndpointName;
    }

    @Override
    public String toString() {
        return "ConnectedSystemProperties{" +
                "owningOrganizationName='" + owningOrganizationName + '\'' +
                ", solutionName='" + solutionName + '\'' +
                ", subsystemName='" + subsystemName + '\'' +
                ", subsystemVersion='" + subsystemVersion + '\'' +
                ", externalisedServiceName='" + externalisedServiceName + '\'' +
                ", externalisedServiceEndpointName='" + externalisedServiceEndpointName + '\'' +
                ", targetPort1=" + targetPort1 +
                ", targetPort2=" + targetPort2 +
                ", targetPort3=" + targetPort3 +
                '}';
    }
}
