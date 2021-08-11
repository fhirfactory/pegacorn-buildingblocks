/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.tasks.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.tasks.valuesets.PetasosCapabilityLayerEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.maven.shared.utils.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public abstract class PetasosCapabilityDefinition extends PetasosCapabilityCommonName implements Serializable {
    public String capabilityDomain;
    public PetasosCapabilityLayerEnum capabilityLayer;
    public String level1Grouping;
    public String level2Grouping;
    public String level3Grouping;

    public String description;

    public PetasosCapabilityDefinition(){
        this.capabilityLayer = null;
        this.level1Grouping = null;
        this.capabilityDomain = null;
        this.level2Grouping = null;
        this.level3Grouping = null;
        this.description = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosCapabilityDefinition)) return false;
        if (!super.equals(o)) return false;
        PetasosCapabilityDefinition that = (PetasosCapabilityDefinition) o;
        return Objects.equals(getCapabilityDomain(), that.getCapabilityDomain()) && getCapabilityLayer() == that.getCapabilityLayer() && Objects.equals(getLevel1Grouping(), that.getLevel1Grouping()) && Objects.equals(getLevel2Grouping(), that.getLevel2Grouping()) && Objects.equals(getLevel3Grouping(), that.getLevel3Grouping()) && Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCapabilityDomain(), getCapabilityLayer(), getLevel1Grouping(), getLevel2Grouping(), getLevel3Grouping(), getDescription());
    }

    @Override
    public String toString() {
        return "EnterpriseCapabilityStatement{" +
                "capabilityDomain='" + capabilityDomain + '\'' +
                ", capabilityLayer='" + capabilityLayer + '\'' +
                ", level1Grouping='" + level1Grouping + '\'' +
                ", level2Grouping='" + level2Grouping + '\'' +
                ", level3Grouping='" + level3Grouping + '\'' +
                ", deliveredService='" + getDeliveredService() + '\'' +
                ", deliveredServiceVersion='" + getDeliveredServiceVersion() + '\'' +
                ", supportedFunction='" + getDeliveredFunction() + '\'' +
                ", deliveredFunctionVersion='" + getDeliveredFunctionVersion() + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getCapabilityDomain() {
        return capabilityDomain;
    }

    public void setCapabilityDomain(String capabilityDomain) {
        this.capabilityDomain = capabilityDomain;
    }

    public String getLevel1Grouping() {
        return level1Grouping;
    }

    public void setLevel1Grouping(String level1Grouping) {
        this.level1Grouping = level1Grouping;
    }

    public String getLevel2Grouping() {
        return level2Grouping;
    }

    public void setLevel2Grouping(String level2Grouping) {
        this.level2Grouping = level2Grouping;
    }

    public String getLevel3Grouping() {
        return level3Grouping;
    }

    public void setLevel3Grouping(String level3Grouping) {
        this.level3Grouping = level3Grouping;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PetasosCapabilityLayerEnum getCapabilityLayer() {
        return capabilityLayer;
    }

    public void setCapabilityLayer(PetasosCapabilityLayerEnum capabilityLayer) {
        this.capabilityLayer = capabilityLayer;
    }

    @JsonIgnore
    public PetasosCapabilityCommonName getPetasosCapabilityCommonName(){
        if(StringUtils.isEmpty(getDeliveredFunction())
            && StringUtils.isEmpty(getDeliveredFunctionVersion())
            && StringUtils.isEmpty(getDeliveredService())
            && StringUtils.isEmpty(getDeliveredServiceVersion()) ){
            return(null);
        }
        PetasosCapabilityCommonName commonName = new PetasosCapabilityCommonName();
        if(getDeliveredFunction() != null) {
            commonName.setDeliveredFunction(SerializationUtils.clone(getDeliveredFunction()));
        }
        if(getDeliveredFunctionVersion() != null){
            commonName.setDeliveredFunctionVersion(SerializationUtils.clone(getDeliveredFunctionVersion()));
        }
        if(getDeliveredService() != null){
            commonName.setDeliveredService(SerializationUtils.clone(getDeliveredService()));
        }
        if(getDeliveredServiceVersion() != null){
            commonName.setDeliveredServiceVersion(SerializationUtils.clone(getDeliveredServiceVersion()));
        }
        return(commonName);
    }

}
