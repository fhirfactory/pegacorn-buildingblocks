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
package net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments.ports.base;


import net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments.connectedsystems.ConnectedSystemProperties;
import org.slf4j.Logger;

public abstract class StandardClientPortSegment {

    protected abstract Logger specifyLogger();

    protected Logger getLogger(){
        return(specifyLogger());
    }

    private String portType;
    private String portParameters;
    private int defaultRetryCount;
    private int defaultRetryWait;
    private int defaultTimeout;
    private ConnectedSystemProperties connectedSystem;
    private String name;

    public StandardClientPortSegment(){
        super();
        this.connectedSystem = new ConnectedSystemProperties();
        this.name = null;
        this.portType = null;
        this.portParameters = null;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public String getPortParameters() {
        return portParameters;
    }

    public void setPortParameters(String portParameters) {
        this.portParameters = portParameters;
    }

    public ConnectedSystemProperties getConnectedSystem() {
        return connectedSystem;
    }

    public void setConnectedSystem(ConnectedSystemProperties connectedSystem) {
        this.connectedSystem = connectedSystem;
    }

    public int getDefaultRetryCount() {
        return defaultRetryCount;
    }

    public void setDefaultRetryCount(int defaultRetryCount) {
        this.defaultRetryCount = defaultRetryCount;
    }

    public int getDefaultRetryWait() {
        return defaultRetryWait;
    }

    public void setDefaultRetryWait(int defaultRetryWait) {
        this.defaultRetryWait = defaultRetryWait;
    }

    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StandardClientPortSegment{" +
                "portType='" + portType + '\'' +
                ", portParameters='" + portParameters + '\'' +
                ", defaultRetryCount='" + defaultRetryCount + '\'' +
                ", defaultRetryWait='" + defaultRetryWait + '\'' +
                ", defaultTimeout='" + defaultTimeout + '\'' +
                ", connectedSystem=" + connectedSystem +
                ", name='" + name + '\'' +
                '}';
    }
}
