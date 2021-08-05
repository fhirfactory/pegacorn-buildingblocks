/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.hestia.dam.im;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.ClusterServiceDeliverySubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc.HTTPIPCServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.StandardServerPortSegment;

public class HestiaDAMIMPropertyFile extends ClusterServiceDeliverySubsystemPropertyFile {
    //
    // Edge :: Answer (FHIR API Server)
    //
    private HTTPIPCServerPortSegment edgeAnswer;
    //
    // Edge :: Receive (FHIR::Media Receivers)
    //
    private StandardServerPortSegment edgeReceiveMediaLadonNexusIM;
    private StandardServerPortSegment edgeReceiveMediaCommunicateIrisIM;
    private StandardServerPortSegment edgeReceiveMediaFHIRBreakLDAPScanner;
    private StandardServerPortSegment edgeReceiveMediaFHIRBreakEmailGateway;
    private StandardServerPortSegment edgeReceiveMediaMITaFSMSGateway;

    public HTTPIPCServerPortSegment getEdgeAnswer() {
        return edgeAnswer;
    }

    public void setEdgeAnswer(HTTPIPCServerPortSegment edgeAnswer) {
        this.edgeAnswer = edgeAnswer;
    }

    public StandardServerPortSegment getEdgeReceiveMediaLadonNexusIM() {
        return edgeReceiveMediaLadonNexusIM;
    }

    public void setEdgeReceiveMediaLadonNexusIM(StandardServerPortSegment edgeReceiveMediaLadonNexusIM) {
        this.edgeReceiveMediaLadonNexusIM = edgeReceiveMediaLadonNexusIM;
    }

    public StandardServerPortSegment getEdgeReceiveMediaCommunicateIrisIM() {
        return edgeReceiveMediaCommunicateIrisIM;
    }

    public void setEdgeReceiveMediaCommunicateIrisIM(StandardServerPortSegment edgeReceiveMediaCommunicateIrisIM) {
        this.edgeReceiveMediaCommunicateIrisIM = edgeReceiveMediaCommunicateIrisIM;
    }

    public StandardServerPortSegment getEdgeReceiveMediaFHIRBreakLDAPScanner() {
        return edgeReceiveMediaFHIRBreakLDAPScanner;
    }

    public void setEdgeReceiveMediaFHIRBreakLDAPScanner(StandardServerPortSegment edgeReceiveMediaFHIRBreakLDAPScanner) {
        this.edgeReceiveMediaFHIRBreakLDAPScanner = edgeReceiveMediaFHIRBreakLDAPScanner;
    }

    public StandardServerPortSegment getEdgeReceiveMediaFHIRBreakEmailGateway() {
        return edgeReceiveMediaFHIRBreakEmailGateway;
    }

    public void setEdgeReceiveMediaFHIRBreakEmailGateway(StandardServerPortSegment edgeReceiveMediaFHIRBreakEmailGateway) {
        this.edgeReceiveMediaFHIRBreakEmailGateway = edgeReceiveMediaFHIRBreakEmailGateway;
    }

    public StandardServerPortSegment getEdgeReceiveMediaMITaFSMSGateway() {
        return edgeReceiveMediaMITaFSMSGateway;
    }

    public void setEdgeReceiveMediaMITaFSMSGateway(StandardServerPortSegment edgeReceiveMediaMITaFSMSGateway) {
        this.edgeReceiveMediaMITaFSMSGateway = edgeReceiveMediaMITaFSMSGateway;
    }
}
