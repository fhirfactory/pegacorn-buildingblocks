/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.components.dataparcel;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelTypeEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelTypeKeyEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelValidationStatusEnum;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Mark A. Hunter
 * @since 2020-08-01
 */
@Deprecated
public class DataParcelToken extends DataParcelTypeDescriptor implements Serializable {


    private DataParcelNormalisationStatusEnum normalisationStatus;
    private DataParcelValidationStatusEnum validationStatus;
    private DataParcelTypeEnum dataParcelType;

    public DataParcelToken() {
        super();
        this.normalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE;
        this.validationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE;
        this.dataParcelType = DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
    }

    public DataParcelToken(DataParcelToken originalToken){
        super(originalToken);
        this.normalisationStatus = originalToken.getNormalisationStatus();
        this.validationStatus = originalToken.getValidationStatus();
        this.dataParcelType = originalToken.getDataParcelType();
    }

    public DataParcelToken(FDN fdn){
        super();
        this.normalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE;
        this.validationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE;
        this.dataParcelType = DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
        fromFDN(fdn);
    }

    public DataParcelTypeEnum getDataParcelType() {
        return dataParcelType;
    }

    public void setDataParcelType(DataParcelTypeEnum dataParcelType) {
        this.dataParcelType = dataParcelType;
    }

    public DataParcelNormalisationStatusEnum getNormalisationStatus() {
        return normalisationStatus;
    }

    public void setNormalisationStatus(DataParcelNormalisationStatusEnum normalisationStatus) {
        this.normalisationStatus = normalisationStatus;
    }

    public DataParcelValidationStatusEnum getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(DataParcelValidationStatusEnum validationStatus) {
        this.validationStatus = validationStatus;
    }

    /**
     * This function removes the Discriminator from a TopicToken Identifier.
     */
    public void removeDiscriminator(){
        setDataParcelType(null);
    }

    @Override
    public String toString() {
        return "DataParcelToken{" +
                "dataParcelDefiner=" + getDataParcelDefiner() +
                ", dataParcelCategory=" + getDataParcelCategory() +
                ", dataParcelSubCategory=" + getDataParcelSubCategory() +
                ", dataParcelResource=" + getDataParcelResource() +
                ", dataParcelSegment=" + getDataParcelSegment() +
                ", dataParcelAttribute=" + getDataParcelAttribute() +
                ", version=" + getVersion() +
                ", normalisationStatus=" + normalisationStatus +
                ", validationStatus=" + validationStatus +
                ", dataParcelType=" + dataParcelType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataParcelToken)) return false;
        DataParcelToken that = (DataParcelToken) o;
        return getDataParcelDefiner().equals(that.getDataParcelDefiner()) && getDataParcelCategory().equals(that.getDataParcelCategory()) && getDataParcelSubCategory().equals(that.getDataParcelSubCategory()) && getDataParcelResource().equals(that.getDataParcelResource()) && Objects.equals(getDataParcelSegment(), that.getDataParcelSegment()) && Objects.equals(getDataParcelAttribute(), that.getDataParcelAttribute()) && getVersion().equals(that.getVersion()) && getNormalisationStatus() == that.getNormalisationStatus() && getValidationStatus() == that.getValidationStatus() && getDataParcelType() == that.getDataParcelType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataParcelDefiner(), getDataParcelCategory(), getDataParcelSubCategory(), getDataParcelResource(), getDataParcelSegment(), getDataParcelAttribute(), getVersion(), getNormalisationStatus(), getValidationStatus(), getDataParcelType());
    }

    @Override
    public FDN toFDN(){
        FDN fdn = new FDN();
        if(hasDataParcelDefiner()){
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DEFINER.getTopicKey(), getDataParcelDefiner()));
        }
        if(hasDataParcelCategory()){
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_CATEGORY.getTopicKey(), getDataParcelCategory()));
        }
        if(hasDataParcelSubCategory()){
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicKey(), getDataParcelSubCategory()));
        }
        if(hasDataParcelResource()){
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicKey(), getDataParcelResource()));
        }
        if(hasDataParcelSegment()){
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SEGMENT.getTopicKey(), getDataParcelSegment()));
        }
        if(hasDataParcelAttribute()){
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_ATTRIBUTE.getTopicKey(), getDataParcelAttribute()));
        }
        fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_TYPE.getTopicKey(), getDataParcelType().getDataParcelTypeValue()));
        fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_NORMALISATION_STATUS.getTopicKey(), getNormalisationStatus().getNormalisationStatusValue()));
        fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_VALIDATION_STATUS.getTopicKey(), getValidationStatus().getValidationStatusValue()));
        if(hasVersion()) {
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_VERSION.getTopicKey(), getVersion()));
        }
        return(fdn);
    }

    @Override
    public void fromFDN(FDN fdn){
        if(fdn == null){
            return;
        }
        for(RDN rdn: fdn.getRDNSet()){
            DataParcelTypeKeyEnum keyEnum = DataParcelTypeKeyEnum.fromTopicKeyString(rdn.getQualifier());
            switch(keyEnum) {
                case DATASET_DEFINER:
                    setDataParcelDefiner(rdn.getValue());
                    break;
                case DATASET_CATEGORY:
                    setDataParcelCategory(rdn.getValue());
                    break;
                case DATASET_SUBCATEGORY:
                    setDataParcelSubCategory(rdn.getValue());
                    break;
                case DATASET_RESOURCE:
                    setDataParcelResource(rdn.getValue());
                    break;
                case DATASET_SEGMENT:
                    setDataParcelSegment(rdn.getValue());
                    break;
                case DATASET_ATTRIBUTE:
                    setDataParcelAttribute(rdn.getValue());
                    break;
                case DATASET_TYPE:
                    setDataParcelType(DataParcelTypeEnum.fromTypeValue(rdn.getValue()));
                    break;
                case DATASET_NORMALISATION_STATUS:
                    setNormalisationStatus(DataParcelNormalisationStatusEnum.fromNormalisationStatusString(rdn.getValue()));
                    break;
                case DATASET_VALIDATION_STATUS:
                    setValidationStatus(DataParcelValidationStatusEnum.fromValidationStatusString(rdn.getValue()));
                    break;
                case DATASET_VERSION:
                    setVersion(rdn.getValue());
                    break;
            }
        }
    }
}
