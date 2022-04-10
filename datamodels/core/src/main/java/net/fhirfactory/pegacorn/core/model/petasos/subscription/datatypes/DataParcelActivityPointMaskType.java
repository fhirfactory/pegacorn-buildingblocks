/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantName;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.time.Instant;

public class DataParcelActivityPointMaskType implements Serializable {
    private PetasosParticipantName processingPlantParticipantName;
    private PetasosParticipantName workUnitProcessorParticipantName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant activityInstant;

    //
    // Constructor(s)
    //

    public DataParcelActivityPointMaskType(){
        this.activityInstant = null;
        this.processingPlantParticipantName = null;
        this.workUnitProcessorParticipantName = null;
    }

    public DataParcelActivityPointMaskType(String processingPlant, String workUnitProcessor){
        this.processingPlantParticipantName = new PetasosParticipantName(processingPlant);
        this.workUnitProcessorParticipantName = new PetasosParticipantName(workUnitProcessor);
    }

    public DataParcelActivityPointMaskType(String processingPlant, String workUnitProcessor, Instant instant){
        this.processingPlantParticipantName = new PetasosParticipantName(processingPlant);
        this.workUnitProcessorParticipantName = new PetasosParticipantName(workUnitProcessor);
        this.activityInstant = SerializationUtils.clone(instant);
    }

    public DataParcelActivityPointMaskType(DataParcelActivityPointMaskType ori){
        if(ori.hasActivityInstant()){
            setActivityInstant(ori.getActivityInstant());
        } else {
            setActivityInstant(null);
        }
        if(ori.hasWorkUnitProcessorParticipantName()){
            setWorkUnitProcessorParticipantName(ori.getWorkUnitProcessorParticipantName());
        } else {
            setWorkUnitProcessorParticipantName(null);
        }
        if(ori.hasProcessingPlantParticipantName()){
            setProcessingPlantParticipantName(ori.getProcessingPlantParticipantName());
        }
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasProcessingPlantParticipantName(){
        boolean hasValue = this.processingPlantParticipantName != null;
        return(hasValue);
    }

    public PetasosParticipantName getProcessingPlantParticipantName() {
        return processingPlantParticipantName;
    }

    public void setProcessingPlantParticipantName(PetasosParticipantName processingPlantParticipantName) {
        this.processingPlantParticipantName = processingPlantParticipantName;
    }

    @JsonIgnore
    public boolean hasWorkUnitProcessorParticipantName(){
        boolean hasValue = this.workUnitProcessorParticipantName != null;
        return(hasValue);
    }

    public PetasosParticipantName getWorkUnitProcessorParticipantName() {
        return workUnitProcessorParticipantName;
    }

    public void setWorkUnitProcessorParticipantName(PetasosParticipantName workUnitProcessorParticipantName) {
        this.workUnitProcessorParticipantName = workUnitProcessorParticipantName;
    }

    @JsonIgnore
    public boolean hasActivityInstant(){
        boolean hasValue = this.activityInstant != null;
        return(hasValue);
    }

    public Instant getActivityInstant() {
        return activityInstant;
    }

    public void setActivityInstant(Instant activityInstant) {
        this.activityInstant = activityInstant;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        return "ParcelOfWorkActivityPoint{" +
                "processingPlantParticipantName=" + processingPlantParticipantName +
                ", workUnitProcessorParticipantName=" + workUnitProcessorParticipantName +
                ", activityInstant=" + activityInstant +
                '}';
    }
}
