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
package net.fhirfactory.pegacorn.core.model.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.*;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetricSet;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SoftwareComponent implements Serializable {
    abstract protected Logger getLogger();
    private TopologyNodeRDN componentRDN;
    private TopologyNodeFDN componentFDN;
    private ComponentIdType componentID;
    private TopologyNodeFunctionFDN nodeFunctionFDN;
    private ComponentTypeTypeEnum componentType;
    private TopologyNodeFDN containingNodeFDN;
    private ConcurrencyModeEnum concurrencyMode;
    private ResilienceModeEnum resilienceMode;
    private NetworkSecurityZoneEnum securityZone;
    private ConcurrentHashMap<String, String> otherConfigurationParameters;
    private String actualHostIP;
    private String actualPodIP;
    private PetasosComponentMetricSet metrics;

    //
    // Constructor
    //

    public SoftwareComponent(){
        this.componentRDN = null;
        this.componentFDN = null;
        this.nodeFunctionFDN = null;
        this.concurrencyMode = null;
        this.resilienceMode = null;
        this.componentID = null;
        this.otherConfigurationParameters = new ConcurrentHashMap<>();
        this.metrics = null;
    }

    //
    // Some Helper Functions
    //

    public TopologyNodeFDN getComponentFDN() {
        return componentFDN;
    }

    public void setComponentFDN(TopologyNodeFDN componentFDN) {
        this.componentFDN = componentFDN;
        setComponentRDN(componentFDN.getLeafRDN());
        constructComponentID();
    }

    @JsonIgnore
    public void constructFDN(TopologyNodeFDN parentNodeFDN, TopologyNodeRDN nodeRDN){
        getLogger().debug(".constructFDN(): Entry, parentNodeFDN->{}, nodeRDN->{}", parentNodeFDN, nodeRDN);
        if(parentNodeFDN == null || nodeRDN.getNodeType().equals(ComponentTypeTypeEnum.SOLUTION)){
            getLogger().trace(".constructFDN(): Is a Solution Node");
            TopologyNodeFDN solutionFDN = new TopologyNodeFDN();
            solutionFDN.appendTopologyNodeRDN(nodeRDN);
            this.componentFDN = solutionFDN;
        } else {
            getLogger().trace(".constructFDN(): Is not a Solution Node");
            TopologyNodeFDN newFDN = (TopologyNodeFDN)SerializationUtils.clone(parentNodeFDN);
            getLogger().trace(".constructFDN(): newFDN Created");
            newFDN.appendTopologyNodeRDN(nodeRDN);
            getLogger().trace(".constructFDN(): nodeRDN appended");
            this.componentFDN = newFDN;
            getLogger().trace(".constructFDN(): this.nodeFDN assigned->{}", this.getComponentFDN());
        }
        setComponentRDN(nodeRDN);
        constructComponentID();
        getLogger().debug(".constructFDN(): Exit, nodeFDN->{}", this.getComponentFDN());
    }

    @JsonIgnore
    public void constructComponentID(){
        String id = getComponentRDN().getNodeName() + "::" + Long.toHexString(UUID.randomUUID().getLeastSignificantBits());
        ComponentIdType newId = new ComponentIdType();
        newId.setId(id);
        newId.setDisplayName(id);
        setComponentID(newId);
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
        setComponentRDN(nodeRDN);
        constructComponentID();
        getLogger().debug(".constructFunctionFDN(): Exit, nodeFunctionFDN->{}", this.getNodeFunctionFDN());
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

    @JsonIgnore
    public void addOtherConfigurationParameter(String key, String value){
        if(this.otherConfigurationParameters.containsKey(key)){
            this.otherConfigurationParameters.remove(key);
        }
        this.otherConfigurationParameters.put(key,value);
    }

    public String getOtherConfigurationParameter(String key){
        if(this.otherConfigurationParameters.containsKey(key)){
            String value = this.otherConfigurationParameters.get(key);
            return(value);
        }
        return(null);
    }

    //
    // Getters (and Setters)
    //

    public ConcurrentHashMap<String, String> getOtherConfigurationParameters() {
        return otherConfigurationParameters;
    }

    public void setOtherConfigurationParameters(ConcurrentHashMap<String, String> otherConfigurationParameters) {
        this.otherConfigurationParameters = otherConfigurationParameters;
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
    public boolean hasComponentID(){
        boolean hasValue = this.componentID != null;
        return(hasValue);
    }

    public ComponentIdType getComponentID() {
        return componentID;
    }

    public void setComponentID(ComponentIdType componentID) {
        this.componentID = componentID;
    }

    @JsonIgnore
    public boolean hasNodeFunctionFDN(){
        boolean hasValue = this.nodeFunctionFDN != null;
        return(hasValue);
    }

    public TopologyNodeFunctionFDN getNodeFunctionFDN() {
        return nodeFunctionFDN;
    }

    public void setNodeFunctionFDN(TopologyNodeFunctionFDN nodeFunctionFDN) {
        this.nodeFunctionFDN = nodeFunctionFDN;
    }

    public ComponentTypeTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentTypeTypeEnum componentType) {
        this.componentType = componentType;
    }

    @JsonIgnore
    public boolean hasContainingNodeFDN(){
        boolean hasValue = this.containingNodeFDN != null;
        return(hasValue);
    }

    public TopologyNodeFDN getContainingNodeFDN() {
        return containingNodeFDN;
    }

    public void setContainingNodeFDN(TopologyNodeFDN containingNodeFDN) {
        this.containingNodeFDN = containingNodeFDN;
    }

    public String getActualHostIP() {
        return actualHostIP;
    }

    public void setActualHostIP(String actualHostIP) {
        this.actualHostIP = actualHostIP;
    }

    public String getActualPodIP() {
        return actualPodIP;
    }

    public void setActualPodIP(String actualPodIP) {
        this.actualPodIP = actualPodIP;
    }

    @JsonIgnore
    public boolean hasComponentRDN(){
        boolean hasValue = this.componentRDN != null;
        return(hasValue);
    }

    public void setComponentRDN(TopologyNodeRDN componentRDN) {
        this.componentRDN = componentRDN;
        constructComponentID();
    }

    public TopologyNodeRDN getComponentRDN() {
        return componentRDN;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode{" +
                "nodeRDN=" + componentRDN +
                ", nodeFDN=" + componentFDN +
                ", componentID=" + componentID +
                ", nodeFunctionFDN=" + nodeFunctionFDN +
                ", componentType=" + componentType +
                ", containingNodeFDN=" + containingNodeFDN +
                ", concurrencyMode=" + concurrencyMode +
                ", resilienceMode=" + resilienceMode +
                ", securityZone=" + securityZone +
                ", otherConfigurationParameters=" + otherConfigurationParameters +
                ", actualHostIP=" + actualHostIP +
                ", actualPodIP=" + actualPodIP +
                ", kubernetesDeployed=" + isKubernetesDeployed() +
                ", metrics=" + metrics +
                '}';
    }
}
