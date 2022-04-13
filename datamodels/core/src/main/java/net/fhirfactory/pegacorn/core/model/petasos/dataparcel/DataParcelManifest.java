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
package net.fhirfactory.pegacorn.core.model.petasos.dataparcel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelTypeEnum;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class DataParcelManifest implements Serializable {

    public static String WILDCARD_CHARACTER = "*";

    private DataParcelTypeDescriptor contentDescriptor;
    private DataParcelTypeDescriptor containerDescriptor;
    private DataParcelQualityStatement contentQuality;
    private DataParcelTypeEnum dataParcelType;

    private DataParcelBoundaryPointType origin;
    private DataParcelBoundaryPointType destination;

    private DataParcelActivityPoint lastActivityLocation;

    private DataParcelDirectionEnum dataParcelFlowDirection;

    public DataParcelManifest(){
        this.contentDescriptor = null;
        this.containerDescriptor = null;
        this.dataParcelFlowDirection = null;
        this.contentQuality = null;
        this.origin = null;
        this.destination = null;
        this.lastActivityLocation = null;
        this.dataParcelType = DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
    }

    public DataParcelManifest(DataParcelTypeDescriptor contentDescriptor){
        this.containerDescriptor = null;
        this.contentDescriptor = (DataParcelTypeDescriptor) SerializationUtils.clone(contentDescriptor);
        this.containerDescriptor = null;
        this.dataParcelFlowDirection = null;
        this.contentQuality = null;
        this.origin = null;
        this.destination = null;
        this.lastActivityLocation = null;
        this.dataParcelType = DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
    }

    public DataParcelManifest(DataParcelManifest ori){

        if(ori.hasOrigin()){
            setOrigin(ori.getOrigin());
        } else {
            setOrigin(null);
        }

        if(ori.hasDestination()){
            setDestination(ori.getDestination());
        } else {
            setDestination(null);
        }

        if(ori.hasDataParcelType()){
            setDataParcelType(ori.getDataParcelType());
        } else {
            setDataParcelType(null);
        }

        if(ori.hasDataParcelFlowDirection()){
            setDataParcelFlowDirection(ori.getDataParcelFlowDirection());
        } else {
            setDataParcelFlowDirection(null);
        }

        if(ori.hasContentDescriptor()){
            setContentDescriptor(ori.getContentDescriptor());
        } else {
            setContentDescriptor(null);
        }

        if(ori.hasContainerDescriptor()){
            setContainerDescriptor(ori.getContainerDescriptor());
        } else {
            setContainerDescriptor(null);
        }

        if(ori.hasContentQuality()){
            setContentQuality(ori.getContentQuality());
        } else {
            setContentQuality(null);
        }

        if(ori.hasLastActivityLocation()){
            setLastActivityLocation(ori.getLastActivityLocation());
        } else {
            setLastActivityLocation(null);
        }
    }

    //
    // ifExists (has)
    //

    @JsonIgnore
    public boolean hasContentDescriptor(){
        boolean hasValue = this.contentDescriptor != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContainerDescriptor(){
        boolean hasValue = this.containerDescriptor != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentQuality(){
        boolean hasValue = this.contentQuality != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasDataParcelType(){
        boolean hasValue = this.dataParcelType != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasDataParcelFlowDirection(){
        boolean hasValue = this.dataParcelFlowDirection != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasOrigin(){
        boolean hasValue = this.origin != null;
        return(hasValue);
    }


    @JsonIgnore
    public boolean hasDestination(){
        boolean hasValue = this.destination != null;
        return(hasValue);
    }


    @JsonIgnore
    public boolean hasLastActivityLocation(){
        boolean hasValue = this.lastActivityLocation != null;
        return(hasValue);
    }

    //
    // Getters and Setters
    //

    public DataParcelTypeDescriptor getContentDescriptor() {
        return contentDescriptor;
    }

    public void setContentDescriptor(DataParcelTypeDescriptor contentDescriptor) {
        this.contentDescriptor = contentDescriptor;
    }

    public DataParcelTypeDescriptor getContainerDescriptor() {
        return containerDescriptor;
    }

    public void setContainerDescriptor(DataParcelTypeDescriptor containerDescriptor) {
        this.containerDescriptor = containerDescriptor;
    }

    public DataParcelQualityStatement getContentQuality() {
        return contentQuality;
    }

    public void setContentQuality(DataParcelQualityStatement contentQuality) {
        this.contentQuality = contentQuality;
    }

    public DataParcelTypeEnum getDataParcelType() {
        return dataParcelType;
    }

    public void setDataParcelType(DataParcelTypeEnum dataParcelType) {
        this.dataParcelType = dataParcelType;
    }

    public DataParcelBoundaryPointType getOrigin() {
        return origin;
    }

    public void setOrigin(DataParcelBoundaryPointType origin) {
        this.origin = origin;
    }

    public DataParcelBoundaryPointType getDestination() {
        return destination;
    }

    public void setDestination(DataParcelBoundaryPointType destination) {
        this.destination = destination;
    }

    public DataParcelActivityPoint getLastActivityLocation() {
        return lastActivityLocation;
    }

    public void setLastActivityLocation(DataParcelActivityPoint lastActivityLocation) {
        this.lastActivityLocation = lastActivityLocation;
    }

    public DataParcelDirectionEnum getDataParcelFlowDirection() {
        return dataParcelFlowDirection;
    }

    public void setDataParcelFlowDirection(DataParcelDirectionEnum dataParcelFlowDirection) {
        this.dataParcelFlowDirection = dataParcelFlowDirection;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        return "DataParcelManifest{" +
                "contentDescriptor=" + contentDescriptor +
                ", containerDescriptor=" + containerDescriptor +
                ", contentQuality=" + contentQuality +
                ", dataParcelType=" + dataParcelType +
                ", origin=" + origin +
                ", destination=" + destination +
                ", lastActivityLocation=" + lastActivityLocation +
                ", dataParcelFlowDirection=" + dataParcelFlowDirection +
                '}';
    }
}
