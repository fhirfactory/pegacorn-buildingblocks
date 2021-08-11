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
package net.fhirfactory.pegacorn.core.model.topology.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;

import java.util.UUID;

public abstract class TopologyNode{
    abstract protected Logger getLogger();
    private TopologyNodeRDN nodeRDN;
    private TopologyNodeFDN nodeFDN;
    private String nodeKey;
    private TopologyNodeFunctionFDN nodeFunctionFDN;
    private TopologyNodeTypeEnum componentType;
    private TopologyNodeFDN containingNodeFDN;
    private ConcurrencyModeEnum concurrencyMode;
    private ResilienceModeEnum resilienceMode;
    private NetworkSecurityZoneEnum securityZone;

    public TopologyNode(){
        this.nodeRDN = null;
        this.nodeFDN = null;
        this.nodeFunctionFDN = null;
        this.concurrencyMode = null;
        this.resilienceMode = null;
        this.nodeKey = null;
    }

    public TopologyNodeRDN getNodeRDN() {
        return nodeRDN;
    }

    public void setNodeRDN(TopologyNodeRDN nodeRDN) {
        this.nodeRDN = nodeRDN;
        constructNodeKey();
    }

    public TopologyNodeFDN getNodeFDN() {
        return nodeFDN;
    }

    public void setNodeFDN(TopologyNodeFDN nodeFDN) {
        this.nodeFDN = nodeFDN;
        setNodeRDN(nodeFDN.getLeafRDN());
        constructNodeKey();
    }

    public TopologyNodeTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(TopologyNodeTypeEnum componentType) {
        this.componentType = componentType;
    }

    public TopologyNodeFDN getContainingNodeFDN() {
        return containingNodeFDN;
    }

    public void setContainingNodeFDN(TopologyNodeFDN containingNodeFDN) {
        this.containingNodeFDN = containingNodeFDN;
    }

    @JsonIgnore
    public void constructFDN(TopologyNodeFDN parentNodeFDN, TopologyNodeRDN nodeRDN){
        getLogger().debug(".constructFDN(): Entry, parentNodeFDN->{}, nodeRDN->{}", parentNodeFDN, nodeRDN);
        if(parentNodeFDN == null || nodeRDN.getNodeType().equals(TopologyNodeTypeEnum.SOLUTION)){
            getLogger().trace(".constructFDN(): Is a Solution Node");
            TopologyNodeFDN solutionFDN = new TopologyNodeFDN();
            solutionFDN.appendTopologyNodeRDN(nodeRDN);
            this.nodeFDN = solutionFDN;
        } else {
            getLogger().trace(".constructFDN(): Is not a Solution Node");
            TopologyNodeFDN newFDN = (TopologyNodeFDN)SerializationUtils.clone(parentNodeFDN);
            getLogger().trace(".constructFDN(): newFDN Created");
            newFDN.appendTopologyNodeRDN(nodeRDN);
            getLogger().trace(".constructFDN(): nodeRDN appended");
            this.nodeFDN = newFDN;
            getLogger().trace(".constructFDN(): this.nodeFDN assigned->{}", this.getNodeFDN());
        }
        setNodeRDN(nodeRDN);
        constructNodeKey();
        getLogger().debug(".constructFDN(): Exit, nodeFDN->{}", this.getNodeFDN());
    }

    public void constructNodeKey(){
        String fdnValue = getNodeRDN().getNodeName() + "-" + UUID.randomUUID().toString();
        setNodeKey(fdnValue);
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public TopologyNodeFunctionFDN getNodeFunctionFDN() {
        return nodeFunctionFDN;
    }

    public void setNodeFunctionFDN(TopologyNodeFunctionFDN nodeFunctionFDN) {
        this.nodeFunctionFDN = nodeFunctionFDN;
    }

    @JsonIgnore
    public void constructFunctionFDN(TopologyNodeFunctionFDN parentFunctionFDN, TopologyNodeRDN nodeRDN){
        getLogger().debug(".constructFunctionFDN(): Entry");
        switch(nodeRDN.getNodeType()){
            case SOLUTION: {
                TopologyNodeFunctionFDN solutionFDN = new TopologyNodeFunctionFDN();
                solutionFDN.appendTopologyNodeRDN(nodeRDN);
                this.nodeFunctionFDN = solutionFDN;
                break;
            }
            case SITE:
            case PLATFORM:{
                this.nodeFunctionFDN = parentFunctionFDN;
                break;
            }
            default:{
                TopologyNodeFunctionFDN newFunctionFDN = (TopologyNodeFunctionFDN)SerializationUtils.clone(parentFunctionFDN);
                newFunctionFDN.appendTopologyNodeRDN(nodeRDN);
                this.nodeFunctionFDN = newFunctionFDN;
            }
        }
        setNodeRDN(nodeRDN);
        constructNodeKey();
        getLogger().debug(".constructFunctionFDN(): Exit, nodeFunctionFDN->{}", this.getNodeFunctionFDN());
    }

    public ConcurrencyModeEnum getConcurrencyMode() {
        return concurrencyMode;
    }

    public void setConcurrencyMode(ConcurrencyModeEnum concurrencyMode) {
        this.concurrencyMode = concurrencyMode;
    }

    public ResilienceModeEnum getResilienceMode() {
        return resilienceMode;
    }

    public void setResilienceMode(ResilienceModeEnum resilienceMode) {
        this.resilienceMode = resilienceMode;
    }

    public NetworkSecurityZoneEnum getSecurityZone() {
        return securityZone;
    }

    public void setSecurityZone(NetworkSecurityZoneEnum securityZone) {
        this.securityZone = securityZone;
    }

    @JsonIgnore
    public boolean isKubernetesDeployed(){
        if(getResilienceMode() == null){
            return(false);
        }
        switch(getResilienceMode()){
            case RESILIENCE_MODE_KUBERNETES_CLUSTERED:
            case RESILIENCE_MODE_KUBERNETES_MULTISITE:
            case RESILIENCE_MODE_KUBERNETES_STANDALONE:
            case RESILIENCE_MODE_KUBERNETES_MULTISITE_CLUSTERED:
                return(true);
            case RESILIENCE_MODE_CLUSTERED:
            case RESILIENCE_MODE_MULTISITE:
            case RESILIENCE_MODE_STANDALONE:
            case RESILIENCE_MODE_MULTISITE_CLUSTERED:
            default:
                return(false);
        }
    }

    @Override
    public String toString() {
        return "TopologyNode{" +
                "nodeRDN=" + nodeRDN +
                ", nodeFDN=" + nodeFDN +
                ", nodeFunctionFDN=" + nodeFunctionFDN +
                ", componentType=" + componentType +
                ", containingComponent=" + containingNodeFDN +
                ", concurrencyMode=" + concurrencyMode +
                ", resilienceMode=" + resilienceMode +
                ", securityZone=" + securityZone +
                '}';
    }
}
