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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.answer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.InitialHostSpecification;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.datatypes.JGroupsAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StandardEdgeIPCEndpoint extends IPCServerTopologyEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(StandardEdgeIPCEndpoint.class);

    private List<InitialHostSpecification> initialHosts;
    private String nameSpace;
    private String configurationFileName;

    //
    // Constructor(s)
    //

    public StandardEdgeIPCEndpoint(){
        super();
        this.initialHosts = new ArrayList<>();
        this.nameSpace = null;
        this.configurationFileName = null;
    }

    public StandardEdgeIPCEndpoint(StandardEdgeIPCEndpoint ori){
        super(ori);
        this.initialHosts = new ArrayList<>();
        this.nameSpace = null;
        this.configurationFileName = null;
        if(!ori.getInitialHosts().isEmpty()){
            getInitialHosts().addAll(ori.getInitialHosts());
        }
        if(StringUtils.isNotEmpty(ori.getNameSpace())){
            setNameSpace(ori.getNameSpace());
        }
        if(StringUtils.isNotEmpty(ori.getConfigurationFileName())){
            setConfigurationFileName(ori.getConfigurationFileName());
        }
    }

    //
    // Getters and Setters
    //


    public String getConfigurationFileName() {
        return configurationFileName;
    }

    public void setConfigurationFileName(String configurationFileName) {
        this.configurationFileName = configurationFileName;
    }

    public List<InitialHostSpecification> getInitialHosts() {
        return initialHosts;
    }

    public void setInitialHosts(List<InitialHostSpecification> initialHosts) {
        this.initialHosts = initialHosts;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    @JsonIgnore
    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @JsonIgnore
    public JGroupsAdapter getJGroupsAdapter(){
        if(getAdapterList().isEmpty()){
            return(null);
        }
        JGroupsAdapter jgroupsAdapter = (JGroupsAdapter) getAdapterList().get(0);
        return(jgroupsAdapter);
    }

    @JsonIgnore
    public void setJGroupsAdapter(JGroupsAdapter jgroupsAdapter){
        if(jgroupsAdapter != null){
            getAdapterList().add(jgroupsAdapter);
        }
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StandardEdgeIPCEndpoint{");
        sb.append("initialHosts=").append(initialHosts);
        sb.append(", nameSpace='").append(nameSpace).append('\'');
        sb.append(", configurationFileName='").append(configurationFileName).append('\'');
        sb.append(", JGroupsAdapter=").append(getJGroupsAdapter());
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }
}
