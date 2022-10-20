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
import net.fhirfactory.pegacorn.core.model.capabilities.definition.Capability;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentExecutionControlEnum;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentStatusEnum;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.componentid.*;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.PetasosComponentMetricSet;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.Serializable;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SoftwareComponent implements Serializable {
    abstract protected Logger getLogger();
    private ComponentIdType componentID;
    private PetasosParticipant participant;
    private Set<Capability> capabilities;
    private String version;
    private SoftwareComponentTypeEnum componentType;
    private ConcurrencyModeEnum concurrencyMode;
    private ResilienceModeEnum resilienceMode;
    private NetworkSecurityZoneEnum securityZone;
    private String deploymentSite;
    private ConcurrentHashMap<String, String> otherConfigurationParameters;
    private PetasosComponentMetricSet metrics;
    private SoftwareComponentConnectivityContextEnum componentSystemRole;
    private SoftwareComponentStatusEnum componentStatus;
    private SoftwareComponentExecutionControlEnum componentExecutionStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastActivityInstant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastReportingInstant;

    private ComponentIdType parentComponent;

    //
    // Constructor(s)
    //

    public SoftwareComponent(){
        this.concurrencyMode = null;
        this.resilienceMode = null;
        this.componentID = null;
        this.otherConfigurationParameters = new ConcurrentHashMap<>();
        this.metrics = null;
        this.deploymentSite = null;
        this.componentSystemRole = SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_SUBSYSTEM_INTERNAL;
        this.componentStatus = SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_STATUS_UNKNOWN;
        this.componentExecutionStatus = SoftwareComponentExecutionControlEnum.SOFTWARE_COMPONENT_PAUSE_EXECUTION;
        this.participant = new PetasosParticipant();
        this.lastActivityInstant = Instant.now();
        this.lastReportingInstant = null;
        this.capabilities = new HashSet<>();
        this.version = null;
        this.parentComponent = null;
    }

    public SoftwareComponent(SoftwareComponent ori){
        this.concurrencyMode = null;
        this.resilienceMode = null;
        this.componentID = null;
        this.otherConfigurationParameters = new ConcurrentHashMap<>();
        this.metrics = null;
        this.componentSystemRole = SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_SUBSYSTEM_INTERNAL;
        this.componentStatus = SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_STATUS_UNKNOWN;
        this.componentExecutionStatus = SoftwareComponentExecutionControlEnum.SOFTWARE_COMPONENT_PAUSE_EXECUTION;
        this.version = null;
        this.parentComponent = null;
        // Now update with passed value
        setComponentSystemRole(ori.getComponentSystemRole());
        setComponentStatus(ori.getComponentStatus());
        setComponentExecutionStatus(ori.getComponentExecutionStatus());
        setResilienceMode(ori.getResilienceMode());
        setConcurrencyMode(ori.getConcurrencyMode());
        setComponentType(ori.getComponentType());
        if(ori.hasDeploymentSite()){
            setDeploymentSite(ori.getDeploymentSite());
        }
        if(ori.hasComponentID()){
            setComponentID(SerializationUtils.clone(ori.getComponentId()));
        }
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
            setMetrics(SerializationUtils.clone(ori.getMetrics()));
        }
        setSecurityZone(ori.getSecurityZone());
        setComponentType(ori.getComponentType());
        if(ori.hasParticipant()){
            setParticipant(SerializationUtils.clone(ori.getParticipant()));
        } else {
            setParticipant(new PetasosParticipant());
        }
        if(ori.hasLastActivityInstant()){
            setLastActivityInstant(ori.getLastActivityInstant());
        }
        if(ori.hasLastReportingInstant()){
            setLastReportingInstant(ori.getLastReportingInstant());
        }
        if(!ori.getCapabilities().isEmpty()){
            for(Capability currentCapability: ori.getCapabilities()){
                Capability clonedCapability = SerializationUtils.clone(currentCapability);
                getCapabilities().add(clonedCapability);
            }
        }
        if(StringUtils.isNotEmpty(ori.getVersion())){
            setVersion(ori.getVersion());
        }
        if(ori.hasParentComponent()){
            setParentComponent(SerializationUtils.clone(ori.getParentComponent()));
        }
    }

    //
    // Some Helper Functions
    //

    @JsonIgnore
    public boolean hasParentComponent(){
        boolean hasValue = this.parentComponent != null;
        return(hasValue);
    }

    public ComponentIdType getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(ComponentIdType parentComponent) {
        this.parentComponent = parentComponent;
    }

    @JsonIgnore
    public boolean hasVersion(){
        boolean hasValue = this.version != null;
        return(hasValue);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<Capability> capabilities) {
        this.capabilities = capabilities;
    }

    @JsonIgnore
    public boolean hasParticipant(){
        boolean hasValue = this.participant != null;
        return(hasValue);
    }

    public PetasosParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(PetasosParticipant participant) {
        this.participant = participant;
    }

    @JsonIgnore
    public PetasosParticipantId getParticipantId(){
        return(getParticipant().getParticipantId());
    }

    @JsonIgnore
    public boolean hasParticipantId(){
        return(getParticipant().hasParticipantId());
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

    public ComponentIdType getComponentId() {
        return componentID;
    }

    public void setComponentID(ComponentIdType componentID) {
        this.componentID = componentID;
    }

    public SoftwareComponentTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(SoftwareComponentTypeEnum componentType) {
        this.componentType = componentType;
    }

    public PetasosComponentMetricSet getMetrics() {
        return metrics;
    }

    public void setMetrics(PetasosComponentMetricSet metrics) {
        this.metrics = metrics;
    }

    public SoftwareComponentConnectivityContextEnum getComponentSystemRole() {
        return componentSystemRole;
    }

    public void setComponentSystemRole(SoftwareComponentConnectivityContextEnum componentSystemRole) {
        this.componentSystemRole = componentSystemRole;
    }

    public SoftwareComponentStatusEnum getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(SoftwareComponentStatusEnum componentStatus) {
        this.componentStatus = componentStatus;
    }

    public SoftwareComponentExecutionControlEnum getComponentExecutionStatus() {
        return componentExecutionStatus;
    }

    public void setComponentExecutionStatus(SoftwareComponentExecutionControlEnum componentExecutionStatus) {
        this.componentExecutionStatus = componentExecutionStatus;
    }

    @JsonIgnore
    public PetasosParticipantRegistration getParticipantRegistrationContent(){
        if(hasParticipant()){
            PetasosParticipantRegistration petasosParticipantRegistration = getParticipant().toRegistration();
            petasosParticipantRegistration.setLocalComponentId(getComponentId());
            petasosParticipantRegistration.setLocalComponentStatus(getComponentStatus());
            petasosParticipantRegistration.setComponentType(getComponentType());
            return(petasosParticipantRegistration);
        }
        return(null);
    }

    @JsonIgnore
    public void setParticipantRegistrationContent(PetasosParticipantRegistration participantRegistration){
        if(participantRegistration != null){
            if(!hasParticipant()){
                setParticipant(new PetasosParticipant());
            }
            getParticipant().updateFromRegistration(participantRegistration);
        }
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SoftwareComponent{");
        sb.append("componentID=").append(componentID);
        sb.append(", componentType=").append(componentType);
        sb.append(", concurrencyMode=").append(concurrencyMode);
        sb.append(", resilienceMode=").append(resilienceMode);
        sb.append(", securityZone=").append(securityZone);
        sb.append(", deploymentSite='").append(deploymentSite).append('\'');
        sb.append(", otherConfigurationParameters=").append(otherConfigurationParameters);
        sb.append(", metrics=").append(metrics);
        sb.append(", componentSystemRole=").append(componentSystemRole);
        sb.append(", componentStatus=").append(componentStatus);
        sb.append(", componentExecutionControl=").append(componentExecutionStatus);
        sb.append(", participantId=").append(participant);
        sb.append(", capabilities=").append(capabilities);
        sb.append(", lastActivityInstant=").append(lastActivityInstant);
        sb.append(", lastReportingInstant=").append(lastReportingInstant);
        sb.append(", version=").append(version);
        sb.append(", parentComponent=").append(parentComponent);
        sb.append('}');
        return sb.toString();
    }


    //
    // Equals and Hashcode
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SoftwareComponent)) return false;
        SoftwareComponent that = (SoftwareComponent) o;
        return Objects.equals(getComponentId(), that.getComponentId()) && getComponentType() == that.getComponentType() && getConcurrencyMode() == that.getConcurrencyMode() && getResilienceMode() == that.getResilienceMode() && getSecurityZone() == that.getSecurityZone() && Objects.equals(getDeploymentSite(), that.getDeploymentSite()) && Objects.equals(getOtherConfigurationParameters(), that.getOtherConfigurationParameters()) && Objects.equals(getMetrics(), that.getMetrics()) && getComponentSystemRole() == that.getComponentSystemRole() && getComponentStatus() == that.getComponentStatus() && getComponentExecutionStatus() == that.getComponentExecutionStatus() && Objects.equals(getParticipant(), that.getParticipant()) && Objects.equals(getLastActivityInstant(), that.getLastActivityInstant()) && Objects.equals(getLastReportingInstant(), that.getLastReportingInstant());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponentId(), getComponentType(), getConcurrencyMode(), getResilienceMode(), getSecurityZone(), getDeploymentSite(), getOtherConfigurationParameters(), getMetrics(), getComponentSystemRole(), getComponentStatus(), getComponentExecutionStatus(), getParticipant(), getLastActivityInstant(), getLastReportingInstant());
    }

}
