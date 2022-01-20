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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentExecutionControlEnum;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentStatusEnum;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentSystemRoleEnum;
import net.fhirfactory.pegacorn.core.model.componentid.*;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.PetasosComponentMetricSet;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SoftwareComponent implements Serializable {
    abstract protected Logger getLogger();

    private TopologyNodeRDN componentRDN;
    private TopologyNodeFDN componentFDN;
    private ComponentIdType componentID;
    private TopologyNodeFunctionFDN nodeFunctionFDN;
    private PegacornSystemComponentTypeTypeEnum componentType;
    private TopologyNodeFDN containingNodeFDN;
    private ConcurrencyModeEnum concurrencyMode;
    private ResilienceModeEnum resilienceMode;
    private NetworkSecurityZoneEnum securityZone;
    private String deploymentSite;
    private ConcurrentHashMap<String, String> otherConfigurationParameters;
    private PetasosComponentMetricSet metrics;
    private SoftwareComponentSystemRoleEnum componentSystemRole;
    private SoftwareComponentStatusEnum componentStatus;
    private SoftwareComponentExecutionControlEnum componentExecutionControl;
    private String subsystemParticipantName;
    private String participantName;
    private String participantDisplayName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastActivityInstant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastReportingInstant;

    //
    // Constructor(s)
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
        this.deploymentSite = null;
        this.componentSystemRole = SoftwareComponentSystemRoleEnum.COMPONENT_ROLE_SUBSYSTEM_INTERNAL;
        this.componentStatus = SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_STATUS_UNKNOWN;
        this.componentExecutionControl = SoftwareComponentExecutionControlEnum.SOFTWARE_COMPONENT_PAUSE_EXECUTION;
        this.subsystemParticipantName = null;
        this.lastActivityInstant = Instant.now();
        this.lastReportingInstant = null;
        this.participantName = null;
        this.participantDisplayName = null;
    }

    public SoftwareComponent(SoftwareComponent ori){
        this.componentRDN = null;
        this.componentFDN = null;
        this.nodeFunctionFDN = null;
        this.concurrencyMode = null;
        this.resilienceMode = null;
        this.componentID = null;
        this.participantDisplayName = null;
        this.otherConfigurationParameters = new ConcurrentHashMap<>();
        this.metrics = null;
        this.componentSystemRole = SoftwareComponentSystemRoleEnum.COMPONENT_ROLE_SUBSYSTEM_INTERNAL;
        this.componentStatus = SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_STATUS_UNKNOWN;
        this.componentExecutionControl = SoftwareComponentExecutionControlEnum.SOFTWARE_COMPONENT_PAUSE_EXECUTION;
        // Now update with passed value
        if(ori.hasComponentRDN()){
            setComponentRDN(ori.getComponentRDN());
        }
        if(ori.hasComponentFDN()){
            setComponentFDN(ori.getComponentFDN());
        }
        if (ori.hasNodeFunctionFDN()) {
            setNodeFunctionFDN(ori.getNodeFunctionFDN());
        }
        if(ori.hasContainingNodeFDN()){
            setContainingNodeFDN(ori.getContainingNodeFDN());
        }
        setComponentSystemRole(ori.getComponentSystemRole());
        setComponentStatus(ori.getComponentStatus());
        setComponentExecutionControl(ori.getComponentExecutionControl());
        setResilienceMode(ori.getResilienceMode());
        setConcurrencyMode(ori.getConcurrencyMode());
        setComponentType(ori.getComponentType());
        if(ori.getOtherConfigurationParameters() != null){
            if(!ori.getOtherConfigurationParameters().isEmpty()){
                Enumeration<String> keys = ori.getOtherConfigurationParameters().keys();
                while(keys.hasMoreElements()){
                    String parameterName = keys.nextElement();
                    getOtherConfigurationParameters().put(parameterName, ori.getOtherConfigurationParameter(parameterName));
                }
            }
        }
        if(ori.getMetrics() != null){
            setMetrics(ori.getMetrics());
        }
        setSecurityZone(ori.getSecurityZone());
        setComponentType(ori.getComponentType());
        if(ori.hasSubsystemParticipantName()){
            setSubsystemParticipantName(ori.getSubsystemParticipantName());
        }
        if(ori.hasLastActivityInstant()){
            setLastActivityInstant(ori.getLastActivityInstant());
        }
        if(ori.hasLastReportingInstant()){
            setLastReportingInstant(ori.getLastReportingInstant());
        }
        if(ori.hasParticipantName()){
            setParticipantName(ori.getParticipantName());
        }
        if(ori.hasSubsystemParticipantName()){
            setSubsystemParticipantName(ori.getSubsystemParticipantName());
        }
        if(ori.hasParticipantDisplayName()){
            setParticipantDisplayName(ori.getParticipantDisplayName());
        }
    }

    //
    // Some Helper Functions
    //

    @JsonIgnore
    public boolean hasParticipantDisplayName(){
        boolean hasValue = this.participantDisplayName != null;
        return(hasValue);
    }

    public String getParticipantDisplayName() {
        return participantDisplayName;
    }

    public void setParticipantDisplayName(String participantDisplayName) {
        this.participantDisplayName = participantDisplayName;
    }

    @JsonIgnore
    public boolean hasParticipantName(){
        boolean hasValue = this.participantName != null;
        return(hasValue);
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    @JsonIgnore
    public boolean hasDeploymentSite(){
        boolean hasValue = this.deploymentSite != null;
        return(hasValue);
    }

    public String getDeploymentSite() {
        return deploymentSite;
    }

    public void setDeploymentSite(String deploymentSite) {
        this.deploymentSite = deploymentSite;
    }

    @JsonIgnore
    public boolean hasLastActivityInstant(){
        boolean hasValue = this.lastActivityInstant != null;
        return(hasValue);
    }
    public Instant getLastActivityInstant() {
        return lastActivityInstant;
    }

    public void setLastActivityInstant(Instant lastActivityInstant) {
        this.lastActivityInstant = lastActivityInstant;
    }

    @JsonIgnore
    public boolean hasLastReportingInstant(){
        boolean hasValue = this.lastReportingInstant != null;
        return(hasValue);
    }

    public Instant getLastReportingInstant() {
        return lastReportingInstant;
    }

    public void setLastReportingInstant(Instant lastReportingInstant) {
        this.lastReportingInstant = lastReportingInstant;
    }

    @JsonIgnore
    public boolean hasSubsystemParticipantName(){
        boolean hasValue = this.subsystemParticipantName != null;
        return(hasValue);
    }

    public String getSubsystemParticipantName() {
        return subsystemParticipantName;
    }

    public void setSubsystemParticipantName(String subsystemParticipantName) {
        this.subsystemParticipantName = subsystemParticipantName;
    }

    public boolean hasComponentFDN(){
        boolean hasValue = this.componentFDN != null;
        return(hasValue);
    }

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
        if(parentNodeFDN == null || nodeRDN.getNodeType().equals(PegacornSystemComponentTypeTypeEnum.SOLUTION)){
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
        String id = getComponentRDN().getNodeName();
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

    @JsonIgnore
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

    public PegacornSystemComponentTypeTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(PegacornSystemComponentTypeTypeEnum componentType) {
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

    public PetasosComponentMetricSet getMetrics() {
        return metrics;
    }

    public void setMetrics(PetasosComponentMetricSet metrics) {
        this.metrics = metrics;
    }

    public SoftwareComponentSystemRoleEnum getComponentSystemRole() {
        return componentSystemRole;
    }

    public void setComponentSystemRole(SoftwareComponentSystemRoleEnum componentSystemRole) {
        this.componentSystemRole = componentSystemRole;
    }

    public SoftwareComponentStatusEnum getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(SoftwareComponentStatusEnum componentStatus) {
        this.componentStatus = componentStatus;
    }

    public SoftwareComponentExecutionControlEnum getComponentExecutionControl() {
        return componentExecutionControl;
    }

    public void setComponentExecutionControl(SoftwareComponentExecutionControlEnum componentExecutionControl) {
        this.componentExecutionControl = componentExecutionControl;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "SoftwareComponent{" +
                "componentRDN=" + componentRDN +
                ", componentFDN=" + componentFDN +
                ", componentID=" + componentID +
                ", nodeFunctionFDN=" + nodeFunctionFDN +
                ", componentType=" + componentType +
                ", containingNodeFDN=" + containingNodeFDN +
                ", concurrencyMode=" + concurrencyMode +
                ", resilienceMode=" + resilienceMode +
                ", securityZone=" + securityZone +
                ", deploymentSite='" + deploymentSite + '\'' +
                ", otherConfigurationParameters=" + otherConfigurationParameters +
                ", metrics=" + metrics +
                ", componentSystemRole=" + componentSystemRole +
                ", componentStatus=" + componentStatus +
                ", componentExecutionControl=" + componentExecutionControl +
                ", subsystemParticipantName='" + subsystemParticipantName + '\'' +
                ", participantName='" + participantName + '\'' +
                ", participantDisplayName='" + participantDisplayName + '\'' +
                ", lastActivityInstant=" + lastActivityInstant +
                ", lastReportingInstant=" + lastReportingInstant +
                '}';
    }


    //
    // Equals and Hashcode
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SoftwareComponent)) return false;
        SoftwareComponent that = (SoftwareComponent) o;
        return Objects.equals(getComponentRDN(), that.getComponentRDN()) && Objects.equals(getComponentFDN(), that.getComponentFDN()) && Objects.equals(getComponentID(), that.getComponentID()) && Objects.equals(getNodeFunctionFDN(), that.getNodeFunctionFDN()) && getComponentType() == that.getComponentType() && Objects.equals(getContainingNodeFDN(), that.getContainingNodeFDN()) && getConcurrencyMode() == that.getConcurrencyMode() && getResilienceMode() == that.getResilienceMode() && getSecurityZone() == that.getSecurityZone() && Objects.equals(getOtherConfigurationParameters(), that.getOtherConfigurationParameters()) && Objects.equals(getMetrics(), that.getMetrics()) && getComponentSystemRole() == that.getComponentSystemRole() && getComponentStatus() == that.getComponentStatus() && getComponentExecutionControl() == that.getComponentExecutionControl() && Objects.equals(getSubsystemParticipantName(), that.getSubsystemParticipantName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponentRDN(), getComponentFDN(), getComponentID(), getNodeFunctionFDN(), getComponentType(), getContainingNodeFDN(), getConcurrencyMode(), getResilienceMode(), getSecurityZone(), getOtherConfigurationParameters(), getMetrics(), getComponentSystemRole(), getComponentStatus(), getComponentExecutionControl(), getSubsystemParticipantName());
    }
}
