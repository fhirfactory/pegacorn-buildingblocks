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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelBoundaryPointType;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantName;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class DataParcelBoundaryPointSubscriptionMaskType implements Serializable {
    private PetasosParticipantName boundaryPointProcessingPlantParticipantNameMask;
    private PetasosParticipantName boundaryPointWorkUnitProcessorParticipantNameMask;
    private PetasosParticipantName boundaryPointEndpointParticipantNameMask;
    private String boundaryPointInterfaceNameMask;

    private String boundaryPointExternalEndpointInterfaceNameMask;
    private String boundaryPointExternalSubsystemNameMask;
    private String boundaryPointExternalTypeMask;

    //
    // Constructor(s)
    //

    public DataParcelBoundaryPointSubscriptionMaskType(){
        this.boundaryPointEndpointParticipantNameMask = new PetasosParticipantName(DataParcelManifest.WILDCARD_CHARACTER);
        this.boundaryPointExternalTypeMask = DataParcelManifest.WILDCARD_CHARACTER;
        this.boundaryPointExternalSubsystemNameMask = DataParcelManifest.WILDCARD_CHARACTER;
        this.boundaryPointInterfaceNameMask = DataParcelManifest.WILDCARD_CHARACTER;
        this.boundaryPointExternalEndpointInterfaceNameMask = DataParcelManifest.WILDCARD_CHARACTER;
        this.boundaryPointProcessingPlantParticipantNameMask = new PetasosParticipantName(DataParcelManifest.WILDCARD_CHARACTER);
        this.boundaryPointWorkUnitProcessorParticipantNameMask = new PetasosParticipantName(DataParcelManifest.WILDCARD_CHARACTER);
    }

    public DataParcelBoundaryPointSubscriptionMaskType(DataParcelBoundaryPointType ori){
        this.boundaryPointEndpointParticipantNameMask = new PetasosParticipantName(DataParcelManifest.WILDCARD_CHARACTER);
        this.boundaryPointExternalTypeMask = DataParcelManifest.WILDCARD_CHARACTER;
        this.boundaryPointExternalSubsystemNameMask = DataParcelManifest.WILDCARD_CHARACTER;
        this.boundaryPointInterfaceNameMask = DataParcelManifest.WILDCARD_CHARACTER;
        this.boundaryPointExternalEndpointInterfaceNameMask = DataParcelManifest.WILDCARD_CHARACTER;
        this.boundaryPointProcessingPlantParticipantNameMask = new PetasosParticipantName(DataParcelManifest.WILDCARD_CHARACTER);
        this.boundaryPointWorkUnitProcessorParticipantNameMask = new PetasosParticipantName(DataParcelManifest.WILDCARD_CHARACTER);

        if(ori.hasBoundaryPointExternalSubsystemName()){
            setBoundaryPointExternalSubsystemNameMask(ori.getBoundaryPointExternalSubsystemName());
        }

        if(ori.hasBoundaryPointProcessingPlantParticipantName()){
            setBoundaryPointProcessingPlantParticipantNameMask(SerializationUtils.clone(ori.getBoundaryPointProcessingPlantParticipantName()));
        }

        if(ori.hasBoundaryPointExternalType()){
            setBoundaryPointExternalTypeMask(SerializationUtils.clone(ori.getBoundaryPointExternalType()));
        }

        if(ori.hasBoundaryPointInterfaceName()){
            setBoundaryPointInterfaceNameMask(SerializationUtils.clone(ori.getBoundaryPointInterfaceName()));
        }

        if(ori.hasBoundaryPointWorkUnitProcessorParticipantName()){
            setBoundaryPointWorkUnitProcessorParticipantNameMask(SerializationUtils.clone(ori.getBoundaryPointWorkUnitProcessorParticipantName()));
        }

        if(ori.hasBoundaryPointEndpointParticipantName()){
            setBoundaryPointEndpointParticipantNameMask(SerializationUtils.clone(ori.getBoundaryPointEndpointParticipantName()));
        }

        if(ori.hasBoundaryPointExternalEndpointInterfaceName()){
            setBoundaryPointExternalEndpointInterfaceNameMask(SerializationUtils.clone(ori.getBoundaryPointExternalEndpointInterfaceName()));
        }
    }


    //
    // ifExists (has)
    //

    @JsonIgnore
    public boolean hasBoundaryPointProcessingPlantParticipantNameMask(){
        boolean hasValue = this.boundaryPointProcessingPlantParticipantNameMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointWorkUnitProcessorParticipantNameMask(){
        boolean hasValue = this.boundaryPointWorkUnitProcessorParticipantNameMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointEndpointParticipantNameMask(){
        boolean hasValue = this.boundaryPointEndpointParticipantNameMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointInterfaceNameMask(){
        boolean hasValue = this.boundaryPointInterfaceNameMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointExternalEndpointInterfaceNameMask(){
        boolean hasValue = this.boundaryPointExternalEndpointInterfaceNameMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointExternalSubsystemNameMask(){
        boolean hasValue = this.boundaryPointExternalSubsystemNameMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointExternalTypeMask(){
        boolean hasValue = this.boundaryPointExternalTypeMask != null;
        return(hasValue);
    }

    //
    // Getters and Setters
    //

    public PetasosParticipantName getBoundaryPointProcessingPlantParticipantNameMask() {
        return boundaryPointProcessingPlantParticipantNameMask;
    }

    public void setBoundaryPointProcessingPlantParticipantNameMask(PetasosParticipantName boundaryPointProcessingPlantParticipantNameMask) {
        this.boundaryPointProcessingPlantParticipantNameMask = boundaryPointProcessingPlantParticipantNameMask;
    }

    public PetasosParticipantName getBoundaryPointWorkUnitProcessorParticipantNameMask() {
        return boundaryPointWorkUnitProcessorParticipantNameMask;
    }

    public void setBoundaryPointWorkUnitProcessorParticipantNameMask(PetasosParticipantName boundaryPointWorkUnitProcessorParticipantNameMask) {
        this.boundaryPointWorkUnitProcessorParticipantNameMask = boundaryPointWorkUnitProcessorParticipantNameMask;
    }

    public PetasosParticipantName getBoundaryPointEndpointParticipantNameMask() {
        return boundaryPointEndpointParticipantNameMask;
    }

    public void setBoundaryPointEndpointParticipantNameMask(PetasosParticipantName boundaryPointEndpointParticipantNameMask) {
        this.boundaryPointEndpointParticipantNameMask = boundaryPointEndpointParticipantNameMask;
    }

    public String getBoundaryPointInterfaceNameMask() {
        return boundaryPointInterfaceNameMask;
    }

    public void setBoundaryPointInterfaceNameMask(String boundaryPointInterfaceNameMask) {
        this.boundaryPointInterfaceNameMask = boundaryPointInterfaceNameMask;
    }

    public String getBoundaryPointExternalEndpointInterfaceNameMask() {
        return boundaryPointExternalEndpointInterfaceNameMask;
    }

    public void setBoundaryPointExternalEndpointInterfaceNameMask(String boundaryPointExternalEndpointInterfaceNameMask) {
        this.boundaryPointExternalEndpointInterfaceNameMask = boundaryPointExternalEndpointInterfaceNameMask;
    }

    public String getBoundaryPointExternalSubsystemNameMask() {
        return boundaryPointExternalSubsystemNameMask;
    }

    public void setBoundaryPointExternalSubsystemNameMask(String boundaryPointExternalSubsystemNameMask) {
        this.boundaryPointExternalSubsystemNameMask = boundaryPointExternalSubsystemNameMask;
    }

    public String getBoundaryPointExternalTypeMask() {
        return boundaryPointExternalTypeMask;
    }

    public void setBoundaryPointExternalTypeMask(String boundaryPointExternalTypeMask) {
        this.boundaryPointExternalTypeMask = boundaryPointExternalTypeMask;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "DataParcelBoundaryPointType{" +
                "boundaryPointProcessingPlantParticipantName=" + boundaryPointProcessingPlantParticipantNameMask +
                ", boundaryPointWorkUnitProcessorParticipantName=" + boundaryPointWorkUnitProcessorParticipantNameMask +
                ", boundaryPointEndpointParticipantName=" + boundaryPointEndpointParticipantNameMask +
                ", boundaryPointInterfaceName='" + boundaryPointInterfaceNameMask + '\'' +
                ", boundaryPointExternalEndpointInterfaceName='" + boundaryPointExternalEndpointInterfaceNameMask + '\'' +
                ", boundaryPointExternalSubsystemName='" + boundaryPointExternalSubsystemNameMask + '\'' +
                ", boundaryPointExternalType='" + boundaryPointExternalTypeMask + '\'' +
                '}';
    }

}
