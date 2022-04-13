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
package net.fhirfactory.pegacorn.core.model.petasos.dataparcel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantName;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class DataParcelBoundaryPointType implements Serializable {
    private PetasosParticipantName boundaryPointProcessingPlantParticipantName;
    private PetasosParticipantName boundaryPointWorkUnitProcessorParticipantName;
    private PetasosParticipantName boundaryPointEndpointParticipantName;
    private String boundaryPointInterfaceName;

    private String boundaryPointExternalEndpointInterfaceName;
    private String boundaryPointExternalSubsystemName;
    private String boundaryPointExternalType;

    //
    // Constructor(s)
    //

    public DataParcelBoundaryPointType(){
        this.boundaryPointEndpointParticipantName = null;
        this.boundaryPointExternalType = null;
        this.boundaryPointExternalSubsystemName = null;
        this.boundaryPointInterfaceName = null;
        this.boundaryPointExternalEndpointInterfaceName = null;
        this.boundaryPointProcessingPlantParticipantName = null;
        this.boundaryPointWorkUnitProcessorParticipantName = null;
    }

    public DataParcelBoundaryPointType(DataParcelBoundaryPointType ori){
        this.boundaryPointEndpointParticipantName = null;
        this.boundaryPointExternalType = null;
        this.boundaryPointExternalSubsystemName = null;
        this.boundaryPointInterfaceName = null;
        this.boundaryPointExternalEndpointInterfaceName = null;
        this.boundaryPointProcessingPlantParticipantName = null;
        this.boundaryPointWorkUnitProcessorParticipantName = null;

        if(ori.hasBoundaryPointExternalSubsystemName()){
            setBoundaryPointExternalSubsystemName(SerializationUtils.clone(ori.getBoundaryPointExternalSubsystemName()));
        }

        if(ori.hasBoundaryPointProcessingPlantParticipantName()){
            setBoundaryPointProcessingPlantParticipantName(SerializationUtils.clone(ori.getBoundaryPointProcessingPlantParticipantName()));
        }

        if(ori.hasBoundaryPointExternalType()){
            setBoundaryPointExternalType(SerializationUtils.clone(ori.getBoundaryPointExternalType()));
        }

        if(ori.hasBoundaryPointInterfaceName()){
            setBoundaryPointInterfaceName(SerializationUtils.clone(ori.getBoundaryPointInterfaceName()));
        }

        if(ori.hasBoundaryPointWorkUnitProcessorParticipantName()){
            setBoundaryPointWorkUnitProcessorParticipantName(ori.getBoundaryPointWorkUnitProcessorParticipantName());
        }

        if(ori.hasBoundaryPointEndpointParticipantName()){
            setBoundaryPointEndpointParticipantName(SerializationUtils.clone(ori.getBoundaryPointEndpointParticipantName()));
        }

        if(ori.hasBoundaryPointExternalEndpointInterfaceName()){
            setBoundaryPointExternalEndpointInterfaceName(SerializationUtils.clone(ori.getBoundaryPointExternalEndpointInterfaceName()));
        }
    }


    //
    // ifExists (has)
    //

    @JsonIgnore
    public boolean hasBoundaryPointProcessingPlantParticipantName(){
        boolean hasValue = this.boundaryPointProcessingPlantParticipantName != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointWorkUnitProcessorParticipantName(){
        boolean hasValue = this.boundaryPointWorkUnitProcessorParticipantName != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointEndpointParticipantName(){
        boolean hasValue = this.boundaryPointEndpointParticipantName != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointInterfaceName(){
        boolean hasValue = this.boundaryPointInterfaceName != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointExternalEndpointInterfaceName(){
        boolean hasValue = this.boundaryPointExternalEndpointInterfaceName != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointExternalSubsystemName(){
        boolean hasValue = this.boundaryPointExternalSubsystemName != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasBoundaryPointExternalType(){
        boolean hasValue = this.boundaryPointExternalType != null;
        return(hasValue);
    }

    //
    // Getters and Setters
    //

    public PetasosParticipantName getBoundaryPointProcessingPlantParticipantName() {
        return boundaryPointProcessingPlantParticipantName;
    }

    public void setBoundaryPointProcessingPlantParticipantName(PetasosParticipantName boundaryPointProcessingPlantParticipantName) {
        this.boundaryPointProcessingPlantParticipantName = boundaryPointProcessingPlantParticipantName;
    }

    public PetasosParticipantName getBoundaryPointWorkUnitProcessorParticipantName() {
        return boundaryPointWorkUnitProcessorParticipantName;
    }

    public void setBoundaryPointWorkUnitProcessorParticipantName(PetasosParticipantName boundaryPointWorkUnitProcessorParticipantName) {
        this.boundaryPointWorkUnitProcessorParticipantName = boundaryPointWorkUnitProcessorParticipantName;
    }

    public PetasosParticipantName getBoundaryPointEndpointParticipantName() {
        return boundaryPointEndpointParticipantName;
    }

    public void setBoundaryPointEndpointParticipantName(PetasosParticipantName boundaryPointEndpointParticipantName) {
        this.boundaryPointEndpointParticipantName = boundaryPointEndpointParticipantName;
    }

    public String getBoundaryPointInterfaceName() {
        return boundaryPointInterfaceName;
    }

    public void setBoundaryPointInterfaceName(String boundaryPointInterfaceName) {
        this.boundaryPointInterfaceName = boundaryPointInterfaceName;
    }

    public String getBoundaryPointExternalEndpointInterfaceName() {
        return boundaryPointExternalEndpointInterfaceName;
    }

    public void setBoundaryPointExternalEndpointInterfaceName(String boundaryPointExternalEndpointInterfaceName) {
        this.boundaryPointExternalEndpointInterfaceName = boundaryPointExternalEndpointInterfaceName;
    }

    public String getBoundaryPointExternalSubsystemName() {
        return boundaryPointExternalSubsystemName;
    }

    public void setBoundaryPointExternalSubsystemName(String boundaryPointExternalSubsystemName) {
        this.boundaryPointExternalSubsystemName = boundaryPointExternalSubsystemName;
    }

    public String getBoundaryPointExternalType() {
        return boundaryPointExternalType;
    }

    public void setBoundaryPointExternalType(String boundaryPointExternalType) {
        this.boundaryPointExternalType = boundaryPointExternalType;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "DataParcelBoundaryPointType{" +
                "boundaryPointProcessingPlantParticipantName=" + boundaryPointProcessingPlantParticipantName +
                ", boundaryPointWorkUnitProcessorParticipantName=" + boundaryPointWorkUnitProcessorParticipantName +
                ", boundaryPointEndpointParticipantName=" + boundaryPointEndpointParticipantName +
                ", boundaryPointInterfaceName='" + boundaryPointInterfaceName + '\'' +
                ", boundaryPointExternalEndpointInterfaceName='" + boundaryPointExternalEndpointInterfaceName + '\'' +
                ", boundaryPointExternalSubsystemName='" + boundaryPointExternalSubsystemName + '\'' +
                ", boundaryPointExternalType='" + boundaryPointExternalType + '\'' +
                '}';
    }
}
