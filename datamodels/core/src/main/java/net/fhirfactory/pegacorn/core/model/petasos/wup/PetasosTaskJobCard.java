/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.wup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosTaskExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;


public class PetasosTaskJobCard implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskJobCard.class);
    protected Logger getLogger(){
        return(LOG);
    }

    private TaskIdType actionableTaskId;
    private ComponentIdType actionableTaskAffinityNode;

    private TaskIdType executingFulfillmentTaskId;
    private ComponentIdType executingProcessingPlant;
    private String processingPlantParticipantName;
    private ComponentIdType executingWorkUnitProcessor;
    private String workUnitProcessorParticipantName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant executingFulfillmentTaskIdAssignmentInstant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastActivityCheckInstant;

    private PetasosTaskExecutionStatusEnum currentStatus;
    private PetasosTaskExecutionStatusEnum lastRequestedStatus;
    private PetasosTaskExecutionStatusEnum grantedStatus;

    private ConcurrencyModeEnum clusterMode;
    private ResilienceModeEnum systemMode;

    //
    // Constructor(s)
    //

    public PetasosTaskJobCard(){
        this.executingFulfillmentTaskId = null;
        this.actionableTaskId = null;
        this.executingFulfillmentTaskIdAssignmentInstant = null;
        this.lastActivityCheckInstant = null;
        this.currentStatus = null;
        this.lastRequestedStatus = null;
        this.clusterMode = null;
        this.systemMode = null;
        this.grantedStatus = null;
        this.executingProcessingPlant = null;
        this.executingWorkUnitProcessor = null;
        this.workUnitProcessorParticipantName = null;
        this.actionableTaskAffinityNode = null;
    }

    public PetasosTaskJobCard(
            TaskIdType executingFulfillmentTaskId,
            TaskIdType actionableTaskId,
            PetasosTaskExecutionStatusEnum currentStatus,
            PetasosTaskExecutionStatusEnum lastRequestedStatus,
            ConcurrencyModeEnum clusterMode,
            ResilienceModeEnum systemMode,
            Instant executingFulfillmentTaskIdAssignmentInstant) {
        //
        // Clear the deck
        this.executingFulfillmentTaskId = null;
        this.actionableTaskId = null;
        this.executingFulfillmentTaskIdAssignmentInstant = null;
        this.lastActivityCheckInstant = null;
        this.currentStatus = null;
        this.lastRequestedStatus = null;
        this.clusterMode = null;
        this.systemMode = null;
        this.grantedStatus = null;
        this.executingProcessingPlant = null;
        this.executingWorkUnitProcessor = null;
        this.workUnitProcessorParticipantName = null;
        this.actionableTaskAffinityNode = null;

        //
        // Assign provided values
        if ((executingFulfillmentTaskId == null)) {
            throw (new IllegalArgumentException("fulfillmentTaskIdentifier is null in Constructor"));
        } else {
            setExecutingFulfillmentTaskId(executingFulfillmentTaskId);
        }

        if ((actionableTaskId == null)) {
            throw (new IllegalArgumentException("actionableTaskIdentifier is null Constructor"));
        } else {
            setActionableTaskId(actionableTaskId);
        }
        setExecutingFulfillmentTaskIdAssignmentInstant(executingFulfillmentTaskIdAssignmentInstant);
        setCurrentStatus(currentStatus);
        setClusterMode(clusterMode);
        setLastRequestedStatus(lastRequestedStatus);
        setSystemMode(systemMode);
    }

    public PetasosTaskJobCard(PetasosTaskJobCard ori) {
        //
        // Clear the deck
        this.executingFulfillmentTaskId = null;
        this.actionableTaskId = null;
        this.executingFulfillmentTaskIdAssignmentInstant = null;
        this.lastActivityCheckInstant = null;
        this.currentStatus = null;
        this.lastRequestedStatus = null;
        this.clusterMode = null;
        this.systemMode = null;
        this.grantedStatus = null;
        this.executingProcessingPlant = null;
        this.executingWorkUnitProcessor = null;
        this.workUnitProcessorParticipantName = null;
        //
        // Assign provided values
        if(ori.hasActionableTaskId()){
            setActionableTaskId(SerializationUtils.clone(ori.getActionableTaskId()));
        }
        if(ori.hasExecutingFulfillmentTaskId()){
            setExecutingFulfillmentTaskId(SerializationUtils.clone(ori.getExecutingFulfillmentTaskId()));
        }
        if(ori.hasClusterMode()){
            setClusterMode(ori.getClusterMode());
        }
        if(ori.hasCurrentStatus()){
            setCurrentStatus(ori.getCurrentStatus());
        }
        if(ori.hasGrantedStatus()){
            setGrantedStatus(ori.getGrantedStatus());
        }
        if(ori.hasLastRequestedStatus()){
            setLastRequestedStatus(ori.getLastRequestedStatus());
        }
        if(ori.hasSystemMode()){
            setSystemMode(ori.getSystemMode());
        }
        if(ori.hasExecutingWorkUnitProcessor()){
            setExecutingWorkUnitProcessor(SerializationUtils.clone(ori.getExecutingWorkUnitProcessor()));
        }
        if(ori.hasExecutingProcessingPlant()){
            setExecutingProcessingPlant(SerializationUtils.clone(ori.getExecutingProcessingPlant()));
        }
        if(ori.hasLastActivityCheckInstant()){
            setLastActivityCheckInstant(SerializationUtils.clone(ori.getLastActivityCheckInstant()));
        }
        if(ori.hasWorkUnitProcessorParticipantName()){
            setWorkUnitProcessorParticipantName(ori.getWorkUnitProcessorParticipantName());
        }
    }

    //
    // Update
    //

    public void update(PetasosTaskJobCard other){
        if(other == null){
            return;
        }
        if(other.hasActionableTaskAffinityNode()){
            setActionableTaskAffinityNode(other.getActionableTaskAffinityNode());
        }
        if(other.hasClusterMode()){
            setClusterMode(other.getClusterMode());
        }
        if(other.hasCurrentStatus()){
            setCurrentStatus(other.getCurrentStatus());
        }
        if(other.hasActionableTaskId()){
            setActionableTaskId(other.getActionableTaskId());
        }
        if(other.hasExecutingFulfillmentTaskIdAssignmentInstant()){
            setExecutingFulfillmentTaskIdAssignmentInstant(other.getExecutingFulfillmentTaskIdAssignmentInstant());
        }
        if(other.hasExecutingProcessingPlant()){
            setExecutingProcessingPlant(other.getExecutingProcessingPlant());
        }
        if(other.hasExecutingWorkUnitProcessor()){
            setExecutingWorkUnitProcessor(other.getExecutingWorkUnitProcessor());
        }
        if(other.hasGrantedStatus()){
            setGrantedStatus(other.getGrantedStatus());
        }
        if(other.hasLastRequestedStatus()){
            setLastRequestedStatus(other.getLastRequestedStatus());
        }
        if(other.hasProcessingPlantParticipantName()){
            setProcessingPlantParticipantName(other.getProcessingPlantParticipantName());
        }
        if(other.hasWorkUnitProcessorParticipantName()){
            setWorkUnitProcessorParticipantName(other.getWorkUnitProcessorParticipantName());
        }
        if(other.hasExecutingFulfillmentTaskId()){
            setExecutingFulfillmentTaskId(other.getExecutingFulfillmentTaskId());
        }
        if(other.hasSystemMode()){
            setSystemMode(other.getSystemMode());
        }
        if(other.hasLastActivityCheckInstant()){
            setLastActivityCheckInstant(other.getLastActivityCheckInstant());
        }
    }

    //
    // Getters (and Setters)
    //

    @JsonIgnore
    public boolean hasActionableTaskAffinityNode(){
        boolean hasValue = this.actionableTaskAffinityNode != null;
        return(hasValue);
    }

    public ComponentIdType getActionableTaskAffinityNode() {
        return actionableTaskAffinityNode;
    }

    public void setActionableTaskAffinityNode(ComponentIdType actionableTaskAffinityNode) {
        this.actionableTaskAffinityNode = actionableTaskAffinityNode;
    }

    @JsonIgnore
    public boolean hasProcessingPlantParticipantName(){
        boolean hasValue = this.processingPlantParticipantName != null;
        return(hasValue);
    }

    public String getProcessingPlantParticipantName() {
        return processingPlantParticipantName;
    }

    public void setProcessingPlantParticipantName(String processingPlantParticipantName) {
        this.processingPlantParticipantName = processingPlantParticipantName;
    }

    public boolean hasWorkUnitProcessorParticipantName(){
        boolean hasValue = this.workUnitProcessorParticipantName != null;
        return(hasValue);
    }

    public String getWorkUnitProcessorParticipantName() {
        return workUnitProcessorParticipantName;
    }

    public void setWorkUnitProcessorParticipantName(String workUnitProcessorParticipantName) {
        this.workUnitProcessorParticipantName = workUnitProcessorParticipantName;
    }

    @JsonIgnore
    public boolean hasExecutingFulfillmentTaskId(){
        boolean hasValue = this.executingFulfillmentTaskId != null;
        return(hasValue);
    }

    public TaskIdType getExecutingFulfillmentTaskId() {
        return executingFulfillmentTaskId;
    }

    public void setExecutingFulfillmentTaskId(TaskIdType executingFulfillmentTaskId) {
        this.executingFulfillmentTaskId = executingFulfillmentTaskId;
    }

    @JsonIgnore
    public boolean hasActionableTaskId(){
        boolean hasValue = this.actionableTaskId != null;
        return(hasValue);
    }

    public TaskIdType getActionableTaskId() {
        return actionableTaskId;
    }

    public void setActionableTaskId(TaskIdType actionableTaskId) {
        this.actionableTaskId = actionableTaskId;
    }

    @JsonIgnore
    public boolean hasExecutingFulfillmentTaskIdAssignmentInstant(){
        boolean hasValue = this.executingFulfillmentTaskIdAssignmentInstant != null;
        return(hasValue);
    }

    public Instant getExecutingFulfillmentTaskIdAssignmentInstant() {
        return (this.executingFulfillmentTaskIdAssignmentInstant);
    }

    public void setExecutingFulfillmentTaskIdAssignmentInstant(Instant executingFulfillmentTaskIdAssignmentInstant) {
        this.executingFulfillmentTaskIdAssignmentInstant = executingFulfillmentTaskIdAssignmentInstant;
    }

    @JsonIgnore
    public boolean hasLastActivityCheckInstant(){
        boolean hasValue = this.lastActivityCheckInstant != null;
        return(hasValue);
    }

    public Instant getLastActivityCheckInstant() {
        return(this.lastActivityCheckInstant);
    }

    public void setLastActivityCheckInstant(Instant lastActivityCheckInstant) {
        this.lastActivityCheckInstant = lastActivityCheckInstant;
    }

    @JsonIgnore
    public boolean hasCurrentStatus(){
        boolean hasValue = this.currentStatus != null;
        return(hasValue);
    }

    public PetasosTaskExecutionStatusEnum getCurrentStatus() {
        return (this.currentStatus);
    }

    public void setCurrentStatus(PetasosTaskExecutionStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    @JsonIgnore
    public boolean hasLastRequestedStatus(){
        boolean hasValue = this.lastRequestedStatus != null;
        return(hasValue);
    }

    public PetasosTaskExecutionStatusEnum getLastRequestedStatus() {
        return (this.lastRequestedStatus);
    }

    public void setLastRequestedStatus(PetasosTaskExecutionStatusEnum lastRequestedStatus) {
        this.lastRequestedStatus = lastRequestedStatus;
    }

    @JsonIgnore
    public boolean hasGrantedStatus(){
        boolean hasValue = this.grantedStatus != null;
        return(hasValue);
    }

    public PetasosTaskExecutionStatusEnum getGrantedStatus() {
        return (this.grantedStatus);
    }

    public void setGrantedStatus(PetasosTaskExecutionStatusEnum grantedStatus) {
        this.grantedStatus = grantedStatus;
    }

    @JsonIgnore
    public boolean hasClusterMode(){
        boolean hasValue = this.clusterMode != null;
        return(hasValue);
    }

    public ConcurrencyModeEnum getClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(ConcurrencyModeEnum clusterMode) {
        this.clusterMode = clusterMode;
    }

    @JsonIgnore
    public boolean hasSystemMode(){
        boolean hasValue = this.systemMode != null;
        return(hasValue);
    }

    public ResilienceModeEnum getSystemMode() {
        return systemMode;
    }

    public void setSystemMode(ResilienceModeEnum systemMode) {
        this.systemMode = systemMode;
    }

    @JsonIgnore
    public boolean hasExecutingProcessingPlant(){
        boolean hasValue = this.executingProcessingPlant != null;
        return(hasValue);
    }

    public ComponentIdType getExecutingProcessingPlant() {
        return executingProcessingPlant;
    }

    public void setExecutingProcessingPlant(ComponentIdType executingProcessingPlant) {
        this.executingProcessingPlant = executingProcessingPlant;
    }

    @JsonIgnore
    public boolean hasExecutingWorkUnitProcessor(){
        boolean hasValue = this.executingWorkUnitProcessor != null;
        return(hasValue);
    }

    public ComponentIdType getExecutingWorkUnitProcessor() {
        return executingWorkUnitProcessor;
    }

    public void setExecutingWorkUnitProcessor(ComponentIdType executingWorkUnitProcessor) {
        this.executingWorkUnitProcessor = executingWorkUnitProcessor;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosTaskJobCard{" +
                "actionableTaskId=" + actionableTaskId +
                ", actionableTaskAffinityNode=" + actionableTaskAffinityNode +
                ", executingFulfillmentTaskId=" + executingFulfillmentTaskId +
                ", executingProcessingPlant=" + executingProcessingPlant +
                ", processingPlantParticipantName='" + processingPlantParticipantName + '\'' +
                ", executingWorkUnitProcessor=" + executingWorkUnitProcessor +
                ", workUnitProcessorParticipantName='" + workUnitProcessorParticipantName + '\'' +
                ", executingFulfillmentTaskIdAssignmentInstant=" + executingFulfillmentTaskIdAssignmentInstant +
                ", lastActivityCheckInstant=" + lastActivityCheckInstant +
                ", currentStatus=" + currentStatus +
                ", lastRequestedStatus=" + lastRequestedStatus +
                ", grantedStatus=" + grantedStatus +
                ", clusterMode=" + clusterMode +
                ", systemMode=" + systemMode +
                '}';
    }
}
