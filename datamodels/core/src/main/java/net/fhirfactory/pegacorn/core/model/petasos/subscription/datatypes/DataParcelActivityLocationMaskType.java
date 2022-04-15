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
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelActivityLocation;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantName;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.common.SubscriptionMaskBase;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class DataParcelActivityLocationMaskType extends SubscriptionMaskBase {
    private static final Logger LOG = LoggerFactory.getLogger(DataParcelActivityLocationMaskType.class);

    private PetasosParticipantName processingPlantParticipantNameMask;
    private PetasosParticipantName workUnitProcessorParticipantNameMask;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant activityInstant;

    //
    // Constructor(s)
    //

    public DataParcelActivityLocationMaskType(){
        super();
        this.activityInstant = null;
        this.processingPlantParticipantNameMask = null;
        this.workUnitProcessorParticipantNameMask = null;
    }

    public DataParcelActivityLocationMaskType(String processingPlant, String workUnitProcessor){
        super();
        this.processingPlantParticipantNameMask = new PetasosParticipantName(processingPlant);
        this.workUnitProcessorParticipantNameMask = new PetasosParticipantName(workUnitProcessor);
    }

    public DataParcelActivityLocationMaskType(String processingPlant, String workUnitProcessor, Instant instant){
        super();
        this.processingPlantParticipantNameMask = new PetasosParticipantName(processingPlant);
        this.workUnitProcessorParticipantNameMask = new PetasosParticipantName(workUnitProcessor);
        this.activityInstant = SerializationUtils.clone(instant);
    }

    public DataParcelActivityLocationMaskType(DataParcelActivityLocation ori){
        super();
        this.activityInstant = null;
        this.processingPlantParticipantNameMask = null;
        this.workUnitProcessorParticipantNameMask = null;

        if(ori.hasActivityInstant()){
            setActivityInstant(ori.getActivityInstant());
        }
        if(ori.hasProcessingPlantParticipantName()){
            setProcessingPlantParticipantNameMask(SerializationUtils.clone(ori.getProcessingPlantParticipantName()));
        }
        if(ori.hasWorkUnitProcessorParticipantName()){
            setWorkUnitProcessorParticipantNameMask(SerializationUtils.clone(ori.getWorkUnitProcessorParticipantName()));
        }
    }

    public DataParcelActivityLocationMaskType(DataParcelActivityLocationMaskType ori){
        super(ori);
        if(ori.hasActivityInstant()){
            setActivityInstant(ori.getActivityInstant());
        } else {
            setActivityInstant(null);
        }
        if(ori.hasWorkUnitProcessorParticipantNameMask()){
            setWorkUnitProcessorParticipantNameMask(ori.getWorkUnitProcessorParticipantNameMask());
        } else {
            setWorkUnitProcessorParticipantNameMask(null);
        }
        if(ori.hasProcessingPlantParticipantNameMask()){
            setProcessingPlantParticipantNameMask(ori.getProcessingPlantParticipantNameMask());
        } else {
            setProcessingPlantParticipantNameMask(null);
        }
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasProcessingPlantParticipantNameMask(){
        boolean hasValue = this.processingPlantParticipantNameMask != null;
        return(hasValue);
    }

    public PetasosParticipantName getProcessingPlantParticipantNameMask() {
        return processingPlantParticipantNameMask;
    }

    public void setProcessingPlantParticipantNameMask(PetasosParticipantName processingPlantParticipantNameMask) {
        this.processingPlantParticipantNameMask = processingPlantParticipantNameMask;
    }

    @JsonIgnore
    public boolean hasWorkUnitProcessorParticipantNameMask(){
        boolean hasValue = this.workUnitProcessorParticipantNameMask != null;
        return(hasValue);
    }

    public PetasosParticipantName getWorkUnitProcessorParticipantNameMask() {
        return workUnitProcessorParticipantNameMask;
    }

    public void setWorkUnitProcessorParticipantNameMask(PetasosParticipantName workUnitProcessorParticipantNameMask) {
        this.workUnitProcessorParticipantNameMask = workUnitProcessorParticipantNameMask;
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

    // Logger

    @JsonIgnore
    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    //
    // toString
    //

    @Override
    public String toString() {
        return "ParcelOfWorkActivityPoint{" +
                "processingPlantParticipantName=" + processingPlantParticipantNameMask +
                ", workUnitProcessorParticipantName=" + workUnitProcessorParticipantNameMask +
                ", activityInstant=" + activityInstant +
                '}';
    }

    //
    // Mask Tests
    //

    public boolean applyMask(DataParcelActivityLocation testLocation){
        getLogger().debug(".applyMask(): Entry, testLocation->{}", testLocation);

        if(hasAllowAll()){
            if(getAllowAll()){
                getLogger().debug(".applyMask(): Exit, allowAll is true, returning -true-");
                return(true);
            }
        }

        boolean processingPlantNamePasses = participantNamePasses(getProcessingPlantParticipantNameMask(), testLocation.getProcessingPlantParticipantName());
        boolean workingUnitProcessorNamePasses = participantNamePasses(getWorkUnitProcessorParticipantNameMask(), testLocation.getWorkUnitProcessorParticipantName());

        boolean passesMask = processingPlantNamePasses
                && workingUnitProcessorNamePasses;

        getLogger().debug(".applyMask(): Exit, passesMask->{}", passesMask);
        return(passesMask);
    }
}
