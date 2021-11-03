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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.datatype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCInterface;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class MLLPPort extends IPCInterface implements Serializable {
    private String hostName;
    private String port;
    private ConcurrentHashMap<String, String> configurationParameters;
    private boolean server;
    private boolean active;
    private int priority;
    private Instant lastActivityPIT;

    //
    // Constructor(s)
    //

    public MLLPPort(){
        super();
        this.hostName = null;
        this.port = null;
        this.configurationParameters = new ConcurrentHashMap<>();
        this.server = false;
        this.active = false;
        this.lastActivityPIT = null;
        this.priority = 0;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasHostName(){
        boolean hasValue = this.hostName != null;
        return(hasValue);
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @JsonIgnore
    public boolean hasPort(){
        boolean hasValue = this.port != null;
        return(hasValue);
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @JsonIgnore
    public boolean hasConfigurationParameters(){
        boolean hasValue = this.configurationParameters != null;
        return(hasValue);
    }

    public ConcurrentHashMap<String, String> getConfigurationParameters() {
        return configurationParameters;
    }

    public void setConfigurationParameters(ConcurrentHashMap<String, String> configurationParameters) {
        this.configurationParameters = configurationParameters;
    }

    @JsonIgnore
    public void addConfigurationParameter(String parameterName, String parameterValue){
        if(StringUtils.isEmpty(parameterName) || StringUtils.isEmpty(parameterValue)){
            return;
        }
        if(configurationParameters.containsKey(parameterName)){
            configurationParameters.remove(parameterName);
        }
        configurationParameters.put(parameterName, parameterValue);
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @JsonIgnore
    public boolean hasLastActivityPIT(){
        boolean hasValue = this.lastActivityPIT != null;
        return(hasValue);
    }

    public Instant getLastActivityPIT() {
        return lastActivityPIT;
    }

    public void setLastActivityPIT(Instant lastActivityPIT) {
        this.lastActivityPIT = lastActivityPIT;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "MLLPPort{" +
                "hostName='" + hostName + '\'' +
                ", port='" + port + '\'' +
                ", configurationParameters=" + configurationParameters +
                ", server=" + server +
                '}';
    }
}
