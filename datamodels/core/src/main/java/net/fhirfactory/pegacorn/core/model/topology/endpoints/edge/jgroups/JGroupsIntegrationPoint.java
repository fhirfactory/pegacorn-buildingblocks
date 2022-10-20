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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.answer.StandardEdgeIPCEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class JGroupsIntegrationPoint extends StandardEdgeIPCEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsIntegrationPoint.class);

    private String channelName;

    //
    // Constructor(s)
    //

    public JGroupsIntegrationPoint(){
        super();
    }

    public JGroupsIntegrationPoint(JGroupsIntegrationPoint ori){
        super(ori);
    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @JsonIgnore
    public boolean hasChannelName(){
        boolean hasValue = this.channelName != null;
        return(hasValue);
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    //
    // Summary Creation
    //

    public JGroupsIntegrationPointSummary toSummary(){
        JGroupsIntegrationPointSummary summary = new JGroupsIntegrationPointSummary();
        summary.setFunction(getInterfaceFunction());
        summary.setComponentId(getComponentId());
        summary.setLastRefreshInstant(Instant.now());
        summary.setSite(getDeploymentSite());
        summary.setProcessingPlantInstanceId(getEnablingProcessingPlantId());
        summary.setZone(getSecurityZone());
        summary.setChannelName(getChannelName());
        return(summary);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JGroupsIntegrationPoint{");
        sb.append("channelName='").append(channelName).append('\'');
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }
}
