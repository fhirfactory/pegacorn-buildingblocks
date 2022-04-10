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
package net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.valuesets.DataParcelDirectionSubscriptionMaskEnum;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.valuesets.DataParcelTypeSubscriptionMaskEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class DataParcelManifestSubscriptionMaskType implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(DataParcelManifestSubscriptionMaskType.class);

    private DataParcelTypeDescriptorSubscriptionMaskType contentDescriptorMask;
    private DataParcelTypeDescriptorSubscriptionMaskType containerDescriptorMask;
    private DataParcelQualitySubscriptionMaskType payloadQualityMask;

    private DataParcelTypeSubscriptionMaskEnum dataParcelTypeMask;

    private DataParcelBoundaryPointSubscriptionMaskType originMask;
    private DataParcelBoundaryPointSubscriptionMaskType destinationMask;

    private DataParcelActivityPointMaskType lastActivityPointMask;

    private DataParcelDirectionSubscriptionMaskEnum dataParcelFlowDirection;

    //
    // Constructor(s)
    //

    public DataParcelManifestSubscriptionMaskType(){
        this.contentDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.containerDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.payloadQualityMask = new DataParcelQualitySubscriptionMaskType();
        this.dataParcelTypeMask = DataParcelTypeSubscriptionMaskEnum.PARCEL_TYPE_ANY;
        this.originMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.destinationMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.lastActivityPointMask = new DataParcelActivityPointMaskType();
        this.dataParcelFlowDirection = DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_DIRECTION_ANY;
    }

    public DataParcelManifestSubscriptionMaskType(DataParcelManifestSubscriptionMaskType ori){
        this.contentDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.containerDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.payloadQualityMask = new DataParcelQualitySubscriptionMaskType();
        this.dataParcelTypeMask = DataParcelTypeSubscriptionMaskEnum.PARCEL_TYPE_ANY;
        this.originMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.destinationMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.lastActivityPointMask = new DataParcelActivityPointMaskType();
        this.dataParcelFlowDirection = DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_DIRECTION_ANY;

        if(ori.hasDataParcelTypeMask()){
            setDataParcelTypeMask(ori.getDataParcelTypeMask());
        }

        if(ori.hasPayloadQualityMask()){
            setPayloadQualityMask(ori.getPayloadQualityMask());
        }

        if(ori.hasLastActivityPointMask()){
            setLastActivityPointMask(ori.getLastActivityPointMask());
        }

        if(ori.hasOriginMask()){
            setOriginMask(ori.getOriginMask());
        }

        if(ori.hasDestinationMask()){
            setDestinationMask(ori.getDestinationMask());
        }

        if(ori.hasDataParcelFlowDirection()){
            setDataParcelFlowDirection(ori.getDataParcelFlowDirection());
        }

        if(ori.hasContentDescriptorMask()){
            setContentDescriptorMask(ori.getContentDescriptorMask());
        }

        if(ori.hasContainerDescriptorMask()){
            setContainerDescriptorMask(ori.getContainerDescriptorMask());
        }
    }

    //
    // ifExists (has)
    //

    @JsonIgnore
    public boolean hasDataParcelFlowDirection(){
        boolean hasValue = this.dataParcelFlowDirection != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasLastActivityPointMask(){
        boolean hasValue = this.lastActivityPointMask != null;
        return(hasValue);
    }


    @JsonIgnore
    public boolean hasDestinationMask(){
        boolean hasValue = this.destinationMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasOriginMask(){
        boolean hasValue = this.originMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasDataParcelTypeMask(){
        boolean hasValue = this.dataParcelTypeMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasPayloadQualityMask(){
        boolean hasValue = this.payloadQualityMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentDescriptorMask(){
        boolean hasValue = this.contentDescriptorMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContainerDescriptorMask(){
        boolean hasValue = this.containerDescriptorMask != null;
        return(hasValue);
    }

    //
    // Getters and Setters
    //


    public DataParcelTypeDescriptorSubscriptionMaskType getContentDescriptorMask() {
        return contentDescriptorMask;
    }

    public void setContentDescriptorMask(DataParcelTypeDescriptorSubscriptionMaskType contentDescriptorMask) {
        this.contentDescriptorMask = contentDescriptorMask;
    }

    public DataParcelTypeDescriptorSubscriptionMaskType getContainerDescriptorMask() {
        return containerDescriptorMask;
    }

    public void setContainerDescriptorMask(DataParcelTypeDescriptorSubscriptionMaskType containerDescriptorMask) {
        this.containerDescriptorMask = containerDescriptorMask;
    }

    public DataParcelQualitySubscriptionMaskType getPayloadQualityMask() {
        return payloadQualityMask;
    }

    public void setPayloadQualityMask(DataParcelQualitySubscriptionMaskType payloadQualityMask) {
        this.payloadQualityMask = payloadQualityMask;
    }

    public DataParcelTypeSubscriptionMaskEnum getDataParcelTypeMask() {
        return dataParcelTypeMask;
    }

    public void setDataParcelTypeMask(DataParcelTypeSubscriptionMaskEnum dataParcelTypeMask) {
        this.dataParcelTypeMask = dataParcelTypeMask;
    }

    public DataParcelBoundaryPointSubscriptionMaskType getOriginMask() {
        return originMask;
    }

    public void setOriginMask(DataParcelBoundaryPointSubscriptionMaskType originMask) {
        this.originMask = originMask;
    }

    public DataParcelBoundaryPointSubscriptionMaskType getDestinationMask() {
        return destinationMask;
    }

    public void setDestinationMask(DataParcelBoundaryPointSubscriptionMaskType destinationMask) {
        this.destinationMask = destinationMask;
    }

    public DataParcelActivityPointMaskType getLastActivityPointMask() {
        return lastActivityPointMask;
    }

    public void setLastActivityPointMask(DataParcelActivityPointMaskType lastActivityPointMask) {
        this.lastActivityPointMask = lastActivityPointMask;
    }

    public DataParcelDirectionSubscriptionMaskEnum getDataParcelFlowDirection() {
        return dataParcelFlowDirection;
    }

    public void setDataParcelFlowDirection(DataParcelDirectionSubscriptionMaskEnum dataParcelFlowDirection) {
        this.dataParcelFlowDirection = dataParcelFlowDirection;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "DataParcelManifestSubscriptionMaskType{" +
                "contentDescriptorMask=" + contentDescriptorMask +
                ", containerDescriptorMask=" + containerDescriptorMask +
                ", payloadQualityMask=" + payloadQualityMask +
                ", dataParcelTypeMask=" + dataParcelTypeMask +
                ", originMask=" + originMask +
                ", destinationMask=" + destinationMask +
                ", lastActivityPointMask=" + lastActivityPointMask +
                ", dataParcelFlowDirection=" + dataParcelFlowDirection +
                '}';
    }
}
