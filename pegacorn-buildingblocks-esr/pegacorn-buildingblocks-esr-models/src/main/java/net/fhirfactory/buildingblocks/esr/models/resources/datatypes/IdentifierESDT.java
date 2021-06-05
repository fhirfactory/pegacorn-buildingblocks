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
package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

import java.util.Objects;

public class IdentifierESDT {
    private static String CONTEXTUALISED_VALUE_SEPARATOR = "+";

    private String type;
    private IdentifierESDTUseEnum use;
    private String value;
    private String leafValue;

    public IdentifierESDT(){
        this.type = null;
        this.use = null;
        this.value = null;
        this.leafValue = null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public IdentifierESDTUseEnum getUse() {
        return use;
    }

    public void setUse(IdentifierESDTUseEnum use) {
        this.use = use;
    }

    public String getLeafValue() {
        return leafValue;
    }

    public void setLeafValue(String leafValue) {
        this.leafValue = leafValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return (true);
        }
        if (!(o instanceof IdentifierESDT)){
            return (false);
        }
        IdentifierESDT that = (IdentifierESDT) o;
        boolean typeMatches = this.getType().contentEquals(that.getType());
        boolean useMatches = this.getUse().equals(that.getUse());
        boolean valueMatches = this.getValue().contentEquals(that.getValue());
        boolean displayValueMatches = true;
        if(this.getLeafValue() != null && that.getLeafValue() != null){
            displayValueMatches = this.getLeafValue().contentEquals(that.getLeafValue());
        }
        if(typeMatches && useMatches && valueMatches && displayValueMatches){
            return(true);
        }
        return(false);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, use.getUseCode(), value);
    }

    @Override
    public String toString() {
        return "IdentifierESDT{" +
                "type='" + type + '\'' +
                ", use=" + use +
                ", value='" + value + '\'' +
                ", valueWithoutContext='" + leafValue + '\'' +
                '}';
    }

    public static String getContextualisedValueSeparator() {
        return CONTEXTUALISED_VALUE_SEPARATOR;
    }
}
