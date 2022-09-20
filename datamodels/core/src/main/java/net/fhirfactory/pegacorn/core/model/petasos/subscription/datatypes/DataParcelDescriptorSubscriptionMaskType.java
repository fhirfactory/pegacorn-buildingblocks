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
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelDescriptorKeyEnum;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.common.SubscriptionMaskBase;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class DataParcelDescriptorSubscriptionMaskType extends SubscriptionMaskBase {
    private static final Logger LOG = LoggerFactory.getLogger(DataParcelDescriptorSubscriptionMaskType.class);

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

    public DataParcelDescriptorSubscriptionMaskType(){
        super();
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

    public DataParcelDescriptorSubscriptionMaskType(DataParcelDescriptorSubscriptionMaskType ori){
        super(ori);
        this.dataParcelCategory = null;
        this.dataParcelDefiner = null;
        this.dataParcelSubCategory = null;
        this.dataParcelResource = null;
        this.dataParcelSegment = null;
        this.dataParcelAttribute = null;
        this.version = null;
        this.dataParcelDiscriminatorType = null;
        this.dataParcelDiscriminatorValue = null;
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

    public DataParcelDescriptorSubscriptionMaskType(DataParcelTypeDescriptor ori){
        super();
        this.dataParcelCategory = null;
        this.dataParcelDefiner = null;
        this.dataParcelSubCategory = null;
        this.dataParcelResource = null;
        this.dataParcelSegment = null;
        this.dataParcelAttribute = null;
        this.version = null;
        this.dataParcelDiscriminatorType = null;
        this.dataParcelDiscriminatorValue = null;
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

    // Logger

    @JsonIgnore
    @Override
    protected Logger getLogger(){
        return(LOG);
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
                ", allowAll=" + getAllowAll() +
                '}';
    }

    //
    // Equals and Hashcode
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataParcelDescriptorSubscriptionMaskType)) return false;
        DataParcelDescriptorSubscriptionMaskType that = (DataParcelDescriptorSubscriptionMaskType) o;
        return Objects.equals(getDataParcelDefiner(), that.getDataParcelDefiner()) && Objects.equals(getDataParcelCategory(), that.getDataParcelCategory()) && Objects.equals(getDataParcelSubCategory(), that.getDataParcelSubCategory()) && Objects.equals(getDataParcelResource(), that.getDataParcelResource()) && Objects.equals(getDataParcelSegment(), that.getDataParcelSegment()) && Objects.equals(getDataParcelAttribute(), that.getDataParcelAttribute()) && Objects.equals(getDataParcelDiscriminatorType(), that.getDataParcelDiscriminatorType()) && Objects.equals(getDataParcelDiscriminatorValue(), that.getDataParcelDiscriminatorValue()) && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataParcelDefiner(), getDataParcelCategory(), getDataParcelSubCategory(), getDataParcelResource(), getDataParcelSegment(), getDataParcelAttribute(), getDataParcelDiscriminatorType(), getDataParcelDiscriminatorValue(), getVersion());
    }

    //
    // Equals with Wildcards (in testObject)
    //

    public boolean applyMask(DataParcelTypeDescriptor otherDescriptor){
        getLogger().debug(".applyMask(): Entry, otherDescriptor->{}", otherDescriptor);

        if(hasAllowAll()){
            if(getAllowAll()){
                getLogger().debug(".applyMask(): Exit, allowAll is true, returning -true-");
                return(true);
            }
        }

        boolean parcelDefinerIsEqual = StringUtils.equals(this.getDataParcelDefiner(),otherDescriptor.getDataParcelDefiner());
        boolean parcelDefinedHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelDefiner(), DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelDefinerIsGoodEnoughMatch = parcelDefinerIsEqual || parcelDefinedHasWildcard;
        if (!parcelDefinerIsGoodEnoughMatch) {
            getLogger().debug(".applyMask(): Exit, parcelDefinerIsGoodEnoughMatch is false, returning -false-");
            return (false);
        }
        boolean parcelCategoryIsEqual = StringUtils.equals(this.getDataParcelCategory(),otherDescriptor.getDataParcelCategory());
        boolean parcelCategoryHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelCategory(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelCategoryIsGoodEnoughMatch = parcelCategoryIsEqual || parcelCategoryHasWildcard;
        if(!parcelCategoryIsGoodEnoughMatch){
            getLogger().debug(".applyMask(): Exit, parcelCategoryIsGoodEnoughMatch is false, returning -false-");
            return(false);
        }
        boolean parcelSubcategoryIsEqual = StringUtils.equals(this.getDataParcelSubCategory(),otherDescriptor.getDataParcelSubCategory());
        boolean parcelSubcategoryHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelSubCategory(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelSubcategoryIsGoodEnoughMatch = parcelSubcategoryIsEqual || parcelSubcategoryHasWildcard;
        if(!parcelSubcategoryIsGoodEnoughMatch){
            getLogger().debug(".applyMask(): Exit, parcelSubcategoryIsGoodEnoughMatch is false, returning -false-");
            return(false);
        }
        boolean parcelResourceIsEqual = StringUtils.equals(this.getDataParcelResource(),otherDescriptor.getDataParcelResource());
        boolean parcelResourceHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelResource(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelResourceIsGoodEnoughMatch = parcelResourceIsEqual || parcelResourceHasWildcard;
        if (!parcelResourceIsGoodEnoughMatch) {
            getLogger().debug(".applyMask(): Exit, parcelResourceIsGoodEnoughMatch is false, returning -false-");
            return(false);
        }
        boolean parcelSegmentIsEqual = StringUtils.equals(this.getDataParcelSegment(),otherDescriptor.getDataParcelSegment());
        boolean parcelSegmentHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelSegment(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelSegmentIsGoodEnoughMatch = parcelSegmentIsEqual || parcelSegmentHasWildcard;
        if (!parcelSegmentIsGoodEnoughMatch) {
            getLogger().debug(".applyMask(): Exit, parcelSegmentIsGoodEnoughMatch is false, returning -false-");
            return(false);
        }
        boolean parcelAttributeIsEqual = StringUtils.equals(this.getDataParcelAttribute(),otherDescriptor.getDataParcelAttribute());
        boolean parcelAttributeHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelAttribute(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelAttributeIsGoodEnoughMatch = parcelAttributeIsEqual || parcelAttributeHasWildcard;
        if(!parcelAttributeIsGoodEnoughMatch){
            getLogger().debug(".applyMask(): Exit, parcelAttributeIsGoodEnoughMatch is false, returning -false-");
            return(false);
        }
        boolean parcelDiscriminatorTypeIsEqual = StringUtils.equals(this.getDataParcelDiscriminatorType(),otherDescriptor.getDataParcelDiscriminatorType());
        boolean parcelDiscriminatorTypeHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelDiscriminatorType(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelDiscriminatorTypeIsGoodEnoughMatch = parcelDiscriminatorTypeIsEqual || parcelDiscriminatorTypeHasWildcard;
        if(!parcelDiscriminatorTypeIsGoodEnoughMatch){
            getLogger().debug(".applyMask(): Exit, parcelDiscriminatorTypeIsGoodEnoughMatch is false, returning -false-");
            return(false);
        }
        boolean parcelDiscriminatorValueIsEqual = StringUtils.equals(this.getDataParcelDiscriminatorValue(),otherDescriptor.getDataParcelDiscriminatorValue());
        boolean parcelDiscriminatorValueHasWildcard = StringUtils.equals(otherDescriptor.getDataParcelDiscriminatorValue(),DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER);
        boolean parcelDiscriminatorValueIsGoodEnoughMatch = parcelDiscriminatorValueIsEqual || parcelDiscriminatorValueHasWildcard;
        if(!parcelDiscriminatorValueIsGoodEnoughMatch){
            getLogger().debug(".applyMask(): Exit, parcelDiscriminatorValueIsGoodEnoughMatch is false, returning -false-");
            return(false);
        }
        getLogger().debug(".applyMask(): Exit, returning -true-");
        return(true);
    }
}
