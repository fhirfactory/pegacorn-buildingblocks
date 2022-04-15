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
import net.fhirfactory.pegacorn.core.model.generalid.FDN;
import net.fhirfactory.pegacorn.core.model.generalid.RDN;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelDescriptorKeyEnum;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.DataParcelManifestSubscriptionMaskType;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

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

    //
    // Constructor(s)
    //

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

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasVersion(){
        if(this.version == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDataParcelDefiner(){
        if(this.dataParcelDefiner == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDataParcelCategory(){
        if(this.dataParcelCategory == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDataParcelSubCategory(){
        if(this.dataParcelSubCategory == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDataParcelResource(){
        if(this.dataParcelResource == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDataParcelSegment(){
        if(this.dataParcelSegment == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDataParcelAttribute(){
        if(this.dataParcelAttribute == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDataParcelDiscriminatorType(){
        boolean hasDiscriminatorType = this.getDataParcelDiscriminatorType() != null;
        return(hasDiscriminatorType);
    }

    @JsonIgnore
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

    //
    // Helper Methods
    //

    public FDN toFDN() {
        FDN fdn = new FDN();
        if (hasDataParcelDefiner()) {
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_DEFINER.getTopicKey(), getDataParcelDefiner()));
        }
        if (hasDataParcelCategory()) {
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_CATEGORY.getTopicKey(), getDataParcelCategory()));
        }
        if (hasDataParcelSubCategory()) {
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_SUBCATEGORY.getTopicKey(), getDataParcelSubCategory()));
        }
        if (hasDataParcelResource()) {
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_RESOURCE.getTopicKey(), getDataParcelResource()));
        }
        if (hasDataParcelSegment()) {
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_SEGMENT.getTopicKey(), getDataParcelSegment()));
        }
        if (hasDataParcelAttribute()) {
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_ATTRIBUTE.getTopicKey(), getDataParcelAttribute()));
        }
        if (hasDataParcelDiscriminatorType()){
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_DISCRIMINATOR_TYPE.getTopicKey(), getDataParcelDiscriminatorType()));
        }
        if (hasDataParcelDiscriminatorValue()){
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_DISCRIMINATOR_VALUE.getTopicKey(), getDataParcelDiscriminatorValue()));
        }
        if (hasVersion()) {
            fdn.appendRDN(new RDN(DataParcelDescriptorKeyEnum.DATASET_VERSION.getTopicKey(), getVersion()));
        }
        return (fdn);
    }

    public void fromFDN(FDN fdn) {
        if (fdn == null) {
            return;
        }
        for (RDN rdn : fdn.getRDNSet()) {
            DataParcelDescriptorKeyEnum keyEnum = DataParcelDescriptorKeyEnum.fromTopicKeyString(rdn.getQualifier());
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

    //
    // To String
    //

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

    //
    // Equals and Hashcode
    //

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

    //
    // Equals with Wildcards (in testObject)
    //

    public boolean isEqualWithWildcardsInOther(DataParcelTypeDescriptor otherDescriptor){

        boolean parcelDefinerIsEqual = StringUtils.equals(this.getDataParcelDefiner(),otherDescriptor.getDataParcelDefiner());
        boolean parcelDefinedHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelDefiner(), DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelDefinerIsGoodEnoughMatch = parcelDefinerIsEqual || parcelDefinedHasWildcard;
        if (!parcelDefinerIsGoodEnoughMatch) {
            return (false);
        }
        boolean parcelCategoryIsEqual = StringUtils.equals(this.getDataParcelCategory(),otherDescriptor.getDataParcelCategory());
        boolean parcelCategoryHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelCategory(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelCategoryIsGoodEnoughMatch = parcelCategoryIsEqual || parcelCategoryHasWildcard;
        if(!parcelCategoryIsGoodEnoughMatch){
            return(false);
        }
        boolean parcelSubcategoryIsEqual = StringUtils.equals(this.getDataParcelSubCategory(),otherDescriptor.getDataParcelSubCategory());
        boolean parcelSubcategoryHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelSubCategory(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelSubcategoryIsGoodEnoughMatch = parcelSubcategoryIsEqual || parcelSubcategoryHasWildcard;
        if(!parcelSubcategoryIsGoodEnoughMatch){
            return(false);
        }
        boolean parcelResourceIsEqual = StringUtils.equals(this.getDataParcelResource(),otherDescriptor.getDataParcelResource());
        boolean parcelResourceHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelResource(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelResourceIsGoodEnoughMatch = parcelResourceIsEqual || parcelResourceHasWildcard;
        if (!parcelResourceIsGoodEnoughMatch) {
            return(false);
        }
        boolean parcelSegmentIsEqual = StringUtils.equals(this.getDataParcelSegment(),otherDescriptor.getDataParcelSegment());
        boolean parcelSegmentHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelSegment(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelSegmentIsGoodEnoughMatch = parcelSegmentIsEqual || parcelSegmentHasWildcard;
        if (!parcelSegmentIsGoodEnoughMatch) {
            return(false);
        }
        boolean parcelAttributeIsEqual = StringUtils.equals(this.getDataParcelAttribute(),otherDescriptor.getDataParcelAttribute());
        boolean parcelAttributeHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelAttribute(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelAttributeIsGoodEnoughMatch = parcelAttributeIsEqual || parcelAttributeHasWildcard;
        if(!parcelAttributeIsGoodEnoughMatch){
            return(false);
        }
        boolean parcelDiscriminatorTypeIsEqual = StringUtils.equals(this.getDataParcelDiscriminatorType(),otherDescriptor.getDataParcelDiscriminatorType());
        boolean parcelDiscriminatorTypeHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelDiscriminatorType(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelDiscriminatorTypeIsGoodEnoughMatch = parcelDiscriminatorTypeIsEqual || parcelDiscriminatorTypeHasWildcard;
        if(!parcelDiscriminatorTypeIsGoodEnoughMatch){
            return(false);
        }
        boolean parcelDiscriminatorValueIsEqual = StringUtils.equals(this.getDataParcelDiscriminatorValue(),otherDescriptor.getDataParcelDiscriminatorValue());
        boolean parcelDiscriminatorValueHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelDiscriminatorValue(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelDiscriminatorValueIsGoodEnoughMatch = parcelDiscriminatorValueIsEqual || parcelDiscriminatorValueHasWildcard;
        if(!parcelDiscriminatorValueIsGoodEnoughMatch){
            return(false);
        }
        return(true);
    }

    //
    // Nice and Simple toString variant
    //

    public String toDotString(){
        String definer = getDataParcelDefiner();
        String category = getDataParcelCategory();
        String subcategory = getDataParcelSubCategory();
        String resource = getDataParcelResource();
        String segment = getDataParcelSegment();
        String attribute = getDataParcelAttribute();
        String version = getVersion();
        String discriminatorType = getDataParcelDiscriminatorType();
        String discriminatorValue = getDataParcelDiscriminatorValue();

        boolean definerExists = StringUtils.isNotEmpty(definer);
        boolean categoryExists = StringUtils.isNotEmpty(category);
        boolean subcategoryExists = StringUtils.isNotEmpty(subcategory);
        boolean resourceExists = StringUtils.isNotEmpty(resource);
        boolean segmentExists = StringUtils.isNotEmpty(segment);
        boolean attributeExists = StringUtils.isNotEmpty(attribute);
        boolean versionExists = StringUtils.isNotEmpty(version);
        boolean discriminatorTypeExists = StringUtils.isNotEmpty(discriminatorType);
        boolean discriminatorValueExists = StringUtils.isNotEmpty(discriminatorValue);

        StringBuilder descriptionBuilder = new StringBuilder();

        if(definerExists){
            descriptionBuilder.append(definer);
            if(subElementExists(DataParcelDescriptorKeyEnum.DATASET_DEFINER)){
                descriptionBuilder.append(".");
            }
        }
        if(categoryExists){
            descriptionBuilder.append(category);
            if(subElementExists(DataParcelDescriptorKeyEnum.DATASET_CATEGORY)){
                descriptionBuilder.append(".");
            }
        }
        if(subcategoryExists){
            descriptionBuilder.append(subcategory);
            if(subElementExists(DataParcelDescriptorKeyEnum.DATASET_SUBCATEGORY)){
                descriptionBuilder.append(".");
            }
        }
        if(resourceExists){
            descriptionBuilder.append(resource);
            if(subElementExists(DataParcelDescriptorKeyEnum.DATASET_RESOURCE)){
                descriptionBuilder.append(".");
            }
        }
        if(segmentExists){
            descriptionBuilder.append(segment);
            if(subElementExists(DataParcelDescriptorKeyEnum.DATASET_SEGMENT)){
                descriptionBuilder.append(".");
            }
        }
        if (attributeExists) {
            descriptionBuilder.append(attribute);
            if(subElementExists(DataParcelDescriptorKeyEnum.DATASET_ATTRIBUTE)){
                descriptionBuilder.append(".");
            }
        }
        if(discriminatorTypeExists){
            descriptionBuilder.append(discriminatorType);
            if(subElementExists(DataParcelDescriptorKeyEnum.DATASET_DISCRIMINATOR_TYPE)){
                descriptionBuilder.append(".");
            }
        }
        if(discriminatorValueExists){
            descriptionBuilder.append(discriminatorValue);
            if(subElementExists(DataParcelDescriptorKeyEnum.DATASET_DISCRIMINATOR_VALUE)){
                descriptionBuilder.append(".");
            }
        }
        if(versionExists){
            descriptionBuilder.append(version);
        }
        String description = descriptionBuilder.toString();
        return(description);
    }

    private boolean subElementExists( DataParcelDescriptorKeyEnum key){
        String definer = getDataParcelDefiner();
        String category = getDataParcelCategory();
        String subcategory = getDataParcelSubCategory();
        String resource = getDataParcelResource();
        String segment = getDataParcelSegment();
        String attribute = getDataParcelAttribute();
        String version = getVersion();
        String discriminatorType = getDataParcelDiscriminatorType();
        String discriminatorValue = getDataParcelDiscriminatorValue();

        boolean definerExists = StringUtils.isNotEmpty(definer);
        boolean categoryExists = StringUtils.isNotEmpty(category);
        boolean subcategoryExists = StringUtils.isNotEmpty(subcategory);
        boolean resourceExists = StringUtils.isNotEmpty(resource);
        boolean segmentExists = StringUtils.isNotEmpty(segment);
        boolean attributeExists = StringUtils.isNotEmpty(attribute);
        boolean versionExists = StringUtils.isNotEmpty(version);
        boolean discriminatorTypeExists = StringUtils.isNotEmpty(discriminatorType);
        boolean discriminatorValueExists = StringUtils.isNotEmpty(discriminatorValue);

        switch(key){
            case DATASET_DEFINER:
                if(categoryExists || subcategoryExists || resourceExists || segmentExists || attributeExists || versionExists || discriminatorTypeExists || discriminatorValueExists){
                    return(true);
                }
                break;
            case DATASET_CATEGORY:
                if(subcategoryExists || resourceExists || segmentExists || attributeExists || versionExists || discriminatorTypeExists || discriminatorValueExists){
                    return(true);
                }
                break;
            case DATASET_SUBCATEGORY:
                if(resourceExists || segmentExists || attributeExists || versionExists || discriminatorTypeExists || discriminatorValueExists){
                    return(true);
                }
                break;
            case DATASET_RESOURCE:
                if(segmentExists || attributeExists || versionExists || discriminatorTypeExists || discriminatorValueExists){
                    return(true);
                }
                break;
            case DATASET_SEGMENT:
                if(attributeExists || versionExists || discriminatorTypeExists || discriminatorValueExists){
                    return(true);
                }
                break;
            case DATASET_ATTRIBUTE:
                if(discriminatorTypeExists || discriminatorValueExists){
                    return(true);
                }
                break;
            case DATASET_DISCRIMINATOR_TYPE:
                if(discriminatorValueExists){
                    return(true);
                }
                break;
        }
        if(versionExists){
            return(true);
        }
        return(false);
    }
}
