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
package net.fhirfactory.pegacorn.core.model.dataparcel;

import net.fhirfactory.pegacorn.core.model.generalid.FDN;
import net.fhirfactory.pegacorn.core.model.generalid.RDN;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelTypeKeyEnum;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Objects;

public class DataParcelTypeDescriptor implements Serializable {
    private String dataParcelDefiner;
    private String dataParcelCategory;
    private String dataParcelSubCategory;
    private String dataParcelResource;
    private String dataParcelSegment;
    private String dataParcelAttribute;
    private String dataParcelDiscriminatorType;
    private String dataParcelDiscriminatorValue;
    private String version;

    public DataParcelTypeDescriptor(){
        this.dataParcelCategory = null;
        this.dataParcelDefiner = null;
        this.dataParcelSubCategory = null;
        this.dataParcelResource = null;
        this.dataParcelSegment = null;
        this.dataParcelAttribute = null;
        this.version = null;
        this.dataParcelDiscriminatorType = null;
        this.dataParcelDiscriminatorValue = null;
    }

    public DataParcelTypeDescriptor(DataParcelTypeDescriptor ori){
        if(ori.hasDataParcelDefiner()) {
            this.dataParcelDefiner = SerializationUtils.clone(ori.getDataParcelDefiner());
        }
        if(ori.hasDataParcelCategory()) {
            this.dataParcelCategory = SerializationUtils.clone(ori.getDataParcelCategory());
        }
        if(ori.hasDataParcelSubCategory()) {
            this.dataParcelSubCategory = SerializationUtils.clone(ori.getDataParcelSubCategory());
        }
        if(ori.hasDataParcelResource()) {
            this.dataParcelResource = SerializationUtils.clone(ori.getDataParcelResource());
        }
        if(ori.hasDataParcelSegment()) {
            this.dataParcelSegment = SerializationUtils.clone(ori.getDataParcelSegment());
        }
        if(ori.hasDataParcelAttribute()) {
            this.dataParcelAttribute = SerializationUtils.clone(ori.getDataParcelAttribute());
        }
        if(ori.hasVersion()) {
            this.version = SerializationUtils.clone(ori.getVersion());
        }
        if(ori.hasDataParcelDiscriminatorType()) {
            this.dataParcelDiscriminatorType = SerializationUtils.clone(ori.getDataParcelDiscriminatorType());
        }
        if(ori.hasDataParcelDiscriminatorValue()) {
            this.dataParcelDiscriminatorValue = SerializationUtils.clone(ori.getDataParcelDiscriminatorValue());
        }
    }

    public boolean hasVersion(){
        if(this.version == null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasDataParcelDefiner(){
        if(this.dataParcelDefiner == null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasDataParcelCategory(){
        if(this.dataParcelCategory == null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasDataParcelSubCategory(){
        if(this.dataParcelSubCategory == null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasDataParcelResource(){
        if(this.dataParcelResource == null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasDataParcelSegment(){
        if(this.dataParcelSegment == null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasDataParcelAttribute(){
        if(this.dataParcelAttribute == null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasDataParcelDiscriminatorType(){
        boolean hasDiscriminatorType = this.getDataParcelDiscriminatorType() != null;
        return(hasDiscriminatorType);
    }

    public boolean hasDataParcelDiscriminatorValue(){
        boolean hasDiscriminatorValue = this.getDataParcelDiscriminatorValue() != null;
        return(hasDiscriminatorValue);
    }

    public String getDataParcelDefiner() {
        return dataParcelDefiner;
    }

    public void setDataParcelDefiner(String dataParcelDefiner) {
        this.dataParcelDefiner = dataParcelDefiner;
    }

    public String getDataParcelCategory() {
        return dataParcelCategory;
    }

    public void setDataParcelCategory(String dataParcelCategory) {
        this.dataParcelCategory = dataParcelCategory;
    }

    public String getDataParcelSubCategory() {
        return dataParcelSubCategory;
    }

    public void setDataParcelSubCategory(String dataParcelSubCategory) {
        this.dataParcelSubCategory = dataParcelSubCategory;
    }

    public String getDataParcelResource() {
        return dataParcelResource;
    }

    public void setDataParcelResource(String dataParcelResource) {
        this.dataParcelResource = dataParcelResource;
    }

    public String getDataParcelSegment() {
        return dataParcelSegment;
    }

    public void setDataParcelSegment(String dataParcelSegment) {
        this.dataParcelSegment = dataParcelSegment;
    }

    public String getDataParcelAttribute() {
        return dataParcelAttribute;
    }

    public void setDataParcelAttribute(String dataParcelAttribute) {
        this.dataParcelAttribute = dataParcelAttribute;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDataParcelDiscriminatorType() {
        return dataParcelDiscriminatorType;
    }

    public void setDataParcelDiscriminatorType(String dataParcelDiscriminatorType) {
        this.dataParcelDiscriminatorType = dataParcelDiscriminatorType;
    }

    public String getDataParcelDiscriminatorValue() {
        return dataParcelDiscriminatorValue;
    }

    public void setDataParcelDiscriminatorValue(String dataParcelDiscriminatorValue) {
        this.dataParcelDiscriminatorValue = dataParcelDiscriminatorValue;
    }

    public FDN toFDN() {
        FDN fdn = new FDN();
        if (hasDataParcelDefiner()) {
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DEFINER.getTopicKey(), getDataParcelDefiner()));
        }
        if (hasDataParcelCategory()) {
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_CATEGORY.getTopicKey(), getDataParcelCategory()));
        }
        if (hasDataParcelSubCategory()) {
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SUBCATEGORY.getTopicKey(), getDataParcelSubCategory()));
        }
        if (hasDataParcelResource()) {
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_RESOURCE.getTopicKey(), getDataParcelResource()));
        }
        if (hasDataParcelSegment()) {
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_SEGMENT.getTopicKey(), getDataParcelSegment()));
        }
        if (hasDataParcelAttribute()) {
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_ATTRIBUTE.getTopicKey(), getDataParcelAttribute()));
        }
        if (hasDataParcelDiscriminatorType()){
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_TYPE.getTopicKey(), getDataParcelDiscriminatorType()));
        }
        if (hasDataParcelDiscriminatorValue()){
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_DISCRIMINATOR_VALUE.getTopicKey(), getDataParcelDiscriminatorValue()));
        }
        if (hasVersion()) {
            fdn.appendRDN(new RDN(DataParcelTypeKeyEnum.DATASET_VERSION.getTopicKey(), getVersion()));
        }
        return (fdn);
    }

    public void fromFDN(FDN fdn) {
        if (fdn == null) {
            return;
        }
        for (RDN rdn : fdn.getRDNSet()) {
            DataParcelTypeKeyEnum keyEnum = DataParcelTypeKeyEnum.fromTopicKeyString(rdn.getQualifier());
            switch (keyEnum) {
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
                case DATASET_DISCRIMINATOR_TYPE:
                    setDataParcelDiscriminatorType(rdn.getValue());
                    break;
                case DATASET_DISCRIMINATOR_VALUE:
                    setDataParcelDiscriminatorValue(rdn.getValue());
                    break;
                case DATASET_VERSION:
                    setVersion(rdn.getValue());
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "DataParcelReference{" +
                "dataParcelDefiner=" + dataParcelDefiner +
                ", dataParcelCategory=" + dataParcelCategory +
                ", dataParcelSubCategory=" + dataParcelSubCategory +
                ", dataParcelResource=" + dataParcelResource +
                ", dataParcelSegment=" + dataParcelSegment +
                ", dataParcelAttribute=" + dataParcelAttribute +
                ", dataParcelDiscriminatorType=" + dataParcelDiscriminatorType +
                ", dataParcelDiscriminatorValue=" + dataParcelDiscriminatorValue +
                ", version=" + version +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataParcelTypeDescriptor)) return false;
        DataParcelTypeDescriptor that = (DataParcelTypeDescriptor) o;
        return Objects.equals(getDataParcelDefiner(), that.getDataParcelDefiner()) && Objects.equals(getDataParcelCategory(), that.getDataParcelCategory()) && Objects.equals(getDataParcelSubCategory(), that.getDataParcelSubCategory()) && Objects.equals(getDataParcelResource(), that.getDataParcelResource()) && Objects.equals(getDataParcelSegment(), that.getDataParcelSegment()) && Objects.equals(getDataParcelAttribute(), that.getDataParcelAttribute()) && Objects.equals(getDataParcelDiscriminatorType(), that.getDataParcelDiscriminatorType()) && Objects.equals(getDataParcelDiscriminatorValue(), that.getDataParcelDiscriminatorValue()) && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataParcelDefiner(), getDataParcelCategory(), getDataParcelSubCategory(), getDataParcelResource(), getDataParcelSegment(), getDataParcelAttribute(), getDataParcelDiscriminatorType(), getDataParcelDiscriminatorValue(), getVersion());
    }
}
