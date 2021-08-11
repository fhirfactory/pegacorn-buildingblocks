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

package net.fhirfactory.pegacorn.core.model.tasks.base;

import java.io.Serializable;
import java.util.Objects;

public class PetasosCapabilityCommonName implements Serializable {
    private String deliveredService;
    private String deliveredServiceVersion;
    private String deliveredFunction;
    private String deliveredFunctionVersion;

    public PetasosCapabilityCommonName() {
        deliveredFunctionVersion = null;
        deliveredFunction = null;
        deliveredServiceVersion = null;
        deliveredService = null;
    }

    public String getDeliveredService() {
        return deliveredService;
    }

    public void setDeliveredService(String deliveredService) {
        this.deliveredService = deliveredService;
    }

    public String getDeliveredServiceVersion() {
        return deliveredServiceVersion;
    }

    public void setDeliveredServiceVersion(String deliveredServiceVersion) {
        this.deliveredServiceVersion = deliveredServiceVersion;
    }

    public String getDeliveredFunction() {
        return deliveredFunction;
    }

    public void setDeliveredFunction(String deliveredFunction) {
        this.deliveredFunction = deliveredFunction;
    }

    public String getDeliveredFunctionVersion() {
        return deliveredFunctionVersion;
    }

    public void setDeliveredFunctionVersion(String deliveredFunctionVersion) {
        this.deliveredFunctionVersion = deliveredFunctionVersion;
    }

    @Override
    public String toString() {
        return "CapabilityCommonName{" +
                "deliveredService='" + deliveredService + '\'' +
                ", deliveredServiceVersion='" + deliveredServiceVersion + '\'' +
                ", deliveredFunction='" + deliveredFunction + '\'' +
                ", deliveredFunctionVersion='" + deliveredFunctionVersion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosCapabilityCommonName)) return false;
        PetasosCapabilityCommonName that = (PetasosCapabilityCommonName) o;
        return Objects.equals(getDeliveredService(), that.getDeliveredService()) && Objects.equals(getDeliveredServiceVersion(), that.getDeliveredServiceVersion()) && Objects.equals(getDeliveredFunction(), that.getDeliveredFunction()) && Objects.equals(getDeliveredFunctionVersion(), that.getDeliveredFunctionVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDeliveredService(), getDeliveredServiceVersion(), getDeliveredFunction(), getDeliveredFunctionVersion());
    }
}
