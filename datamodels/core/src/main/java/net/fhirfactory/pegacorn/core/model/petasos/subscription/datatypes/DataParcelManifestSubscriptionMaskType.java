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
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.common.SubscriptionMaskBase;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.valuesets.DataParcelDirectionSubscriptionMaskEnum;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.valuesets.DataParcelTypeSubscriptionMaskEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class DataParcelManifestSubscriptionMaskType extends SubscriptionMaskBase {
    private static final Logger LOG = LoggerFactory.getLogger(DataParcelManifestSubscriptionMaskType.class);

    public static String WILDCARD_CHARACTER = "*";

    private DataParcelTypeDescriptorSubscriptionMaskType contentDescriptorMask;
    private DataParcelTypeDescriptorSubscriptionMaskType containerDescriptorMask;
    private DataParcelQualitySubscriptionMaskType contentQualityMask;

    private DataParcelTypeSubscriptionMaskEnum dataParcelTypeMask;

    private DataParcelBoundaryPointSubscriptionMaskType originMask;
    private DataParcelBoundaryPointSubscriptionMaskType destinationMask;

    private DataParcelActivityLocationMaskType lastActivityLocationMask;

    private DataParcelDirectionSubscriptionMaskEnum dataParcelFlowDirectionMask;

    //
    // Constructor(s)
    //

    public DataParcelManifestSubscriptionMaskType(){
        super();
        this.contentDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.containerDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.contentQualityMask = new DataParcelQualitySubscriptionMaskType();
        this.dataParcelTypeMask = DataParcelTypeSubscriptionMaskEnum.PARCEL_TYPE_ANY;
        this.originMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.destinationMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.lastActivityLocationMask = new DataParcelActivityLocationMaskType();
        this.dataParcelFlowDirectionMask = DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_DIRECTION_ANY;
    }

    public DataParcelManifestSubscriptionMaskType(DataParcelManifestSubscriptionMaskType ori){
        super(ori);
        this.contentDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.containerDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.contentQualityMask = new DataParcelQualitySubscriptionMaskType();
        this.dataParcelTypeMask = DataParcelTypeSubscriptionMaskEnum.PARCEL_TYPE_ANY;
        this.originMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.destinationMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.lastActivityLocationMask = new DataParcelActivityLocationMaskType();
        this.dataParcelFlowDirectionMask = DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_DIRECTION_ANY;

        if(ori.hasDataParcelTypeMask()){
            setDataParcelTypeMask(ori.getDataParcelTypeMask());
        }

        if(ori.hasContentQualityMask()){
            setContentQualityMask(ori.getContentQualityMask());
        }

        if(ori.hasLastActivityLocationMask()){
            setLastActivityLocationMask(ori.getLastActivityLocationMask());
        }

        if(ori.hasOriginMask()){
            setOriginMask(ori.getOriginMask());
        }

        if(ori.hasDestinationMask()){
            setDestinationMask(ori.getDestinationMask());
        }

        if(ori.hasDataParcelFlowDirectionMask()){
            setDataParcelFlowDirectionMask(ori.getDataParcelFlowDirectionMask());
        }

        if(ori.hasContentDescriptorMask()){
            setContentDescriptorMask(ori.getContentDescriptorMask());
        }

        if(ori.hasContainerDescriptorMask()){
            setContainerDescriptorMask(ori.getContainerDescriptorMask());
        }
    }

    public DataParcelManifestSubscriptionMaskType(DataParcelManifest ori){
        super();
        this.contentDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.containerDescriptorMask = new DataParcelTypeDescriptorSubscriptionMaskType();
        this.contentQualityMask = new DataParcelQualitySubscriptionMaskType();
        this.dataParcelTypeMask = DataParcelTypeSubscriptionMaskEnum.PARCEL_TYPE_ANY;
        this.originMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.destinationMask = new DataParcelBoundaryPointSubscriptionMaskType();
        this.lastActivityLocationMask = new DataParcelActivityLocationMaskType();
        this.dataParcelFlowDirectionMask = DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_DIRECTION_ANY;

        if(ori.hasDataParcelType()){
            switch(ori.getDataParcelType()){
                case IPC_DATA_PARCEL_TYPE:
                    setDataParcelTypeMask(DataParcelTypeSubscriptionMaskEnum.IPC_DATA_PARCEL_TYPE);
                    break;
                case SEARCH_QUERY_DATA_PARCEL_TYPE:
                    setDataParcelTypeMask(DataParcelTypeSubscriptionMaskEnum.SEARCH_QUERY_DATA_PARCEL_TYPE);
                    break;
                case SEARCH_RESULT_DATA_PARCEL_TYPE:
                    setDataParcelTypeMask(DataParcelTypeSubscriptionMaskEnum.SEARCH_RESULT_DATA_PARCEL_TYPE);
                    break;
                case GENERAL_DATA_PARCEL_TYPE:
                    setDataParcelTypeMask(DataParcelTypeSubscriptionMaskEnum.GENERAL_DATA_PARCEL_TYPE);
                    break;
            }
        }

        if(ori.hasContentQuality()){
            setContentQualityMask(new DataParcelQualitySubscriptionMaskType(ori.getContentQuality()));
        }

        if(ori.hasLastActivityLocation()){
            setLastActivityLocationMask(new DataParcelActivityLocationMaskType(ori.getLastActivityLocation()));
        }

        if(ori.hasOrigin()){
            setOriginMask(new DataParcelBoundaryPointSubscriptionMaskType(ori.getOrigin()));
        }

        if(ori.hasDestination()){
            setDestinationMask(new DataParcelBoundaryPointSubscriptionMaskType(ori.getDestination()));
        }

        if(ori.hasDataParcelFlowDirection()){
            switch(ori.getDataParcelFlowDirection()){
                case INFORMATION_FLOW_INBOUND_DATA_PARCEL:
                    setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_INBOUND_DATA_PARCEL);
                    break;
                case INFORMATION_FLOW_OUTBOUND_DATA_PARCEL:
                    setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_OUTBOUND_DATA_PARCEL);
                    break;
                case INFORMATION_FLOW_API_ACTIVITY_REQUEST:
                    setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_API_ACTIVITY_REQUEST);
                    break;
                case INFORMATION_FLOW_API_ACTIVITY_RESPONSE:
                    setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_API_ACTIVITY_RESPONSE);
                    break;
                case INFORMATION_FLOW_WORKFLOW_OUTPUT:
                    setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_WORKFLOW_OUTPUT);
                    break;
                case INFORMATION_FLOW_WORKFLOW_INPUT:
                    setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_WORKFLOW_INPUT);
                    break;
                case INFORMATION_FLOW_WORKFLOW_TRANSIENT:
                    setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_WORKFLOW_TRANSIENT);
                    break;
                case INFORMATION_FLOW_SUBSYSTEM_IPC_DATA_PARCEL:
                    setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum.INFORMATION_FLOW_SUBSYSTEM_IPC_DATA_PARCEL);
                    break;
            }
        }

        if(ori.hasContentDescriptor()){
            setContentDescriptorMask(new DataParcelTypeDescriptorSubscriptionMaskType(ori.getContentDescriptor()));
        }

        if(ori.hasContainerDescriptor()){
            setContainerDescriptorMask(new DataParcelTypeDescriptorSubscriptionMaskType(ori.getContainerDescriptor()));
        }
    }

    //
    // ifExists (has)
    //

    @JsonIgnore
    public boolean hasDataParcelFlowDirectionMask(){
        boolean hasValue = this.dataParcelFlowDirectionMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasLastActivityLocationMask(){
        boolean hasValue = this.lastActivityLocationMask != null;
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
    public boolean hasContentQualityMask(){
        boolean hasValue = this.contentQualityMask != null;
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

    @JsonIgnore
    public static String getWildcardCharacter() {
        return WILDCARD_CHARACTER;
    }

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

    public DataParcelQualitySubscriptionMaskType getContentQualityMask() {
        return contentQualityMask;
    }

    public void setContentQualityMask(DataParcelQualitySubscriptionMaskType contentQualityMask) {
        this.contentQualityMask = contentQualityMask;
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

    public DataParcelActivityLocationMaskType getLastActivityLocationMask() {
        return lastActivityLocationMask;
    }

    public void setLastActivityLocationMask(DataParcelActivityLocationMaskType lastActivityLocationMask) {
        this.lastActivityLocationMask = lastActivityLocationMask;
    }

    public DataParcelDirectionSubscriptionMaskEnum getDataParcelFlowDirectionMask() {
        return dataParcelFlowDirectionMask;
    }

    public void setDataParcelFlowDirectionMask(DataParcelDirectionSubscriptionMaskEnum dataParcelFlowDirectionMask) {
        this.dataParcelFlowDirectionMask = dataParcelFlowDirectionMask;
    }

    // Logger

    @JsonIgnore
    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "DataParcelManifestSubscriptionMaskType{" +
                "contentDescriptorMask=" + contentDescriptorMask +
                ", containerDescriptorMask=" + containerDescriptorMask +
                ", payloadQualityMask=" + contentQualityMask +
                ", dataParcelTypeMask=" + dataParcelTypeMask +
                ", originMask=" + originMask +
                ", destinationMask=" + destinationMask +
                ", lastActivityPointMask=" + lastActivityLocationMask +
                ", dataParcelFlowDirection=" + dataParcelFlowDirectionMask +
                ", allowAll=" + getAllowAll() +
                '}';
    }

    //
    // Filter/Mask Implementation
    //

    public boolean applyMask(DataParcelManifest manifest){

    }
}
