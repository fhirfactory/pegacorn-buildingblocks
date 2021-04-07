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
package net.fhirfactory.pegacorn.internals.directories.entries.datatypes;

import java.util.Objects;

public class IdentifierDE {
    private String type;
    private IdentifierDEUseEnum use;
    private String value;

    public IdentifierDE(){
        this.type = null;
        this.use = null;
        this.value = null;
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

    public IdentifierDEUseEnum getUse() {
        return use;
    }

    public void setUse(IdentifierDEUseEnum use) {
        this.use = use;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return (true);
        }
        if (!(o instanceof IdentifierDE)){
            return (false);
        }
        IdentifierDE that = (IdentifierDE) o;
        boolean typeMatches = this.getType().contentEquals(that.getType());
        boolean useMatches = this.getUse().equals(that.getUse());
        boolean valueMatches = this.getValue().contentEquals(that.getValue());
        if(typeMatches && useMatches && valueMatches){
            return(true);
        }
        return(false);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, use, value);
    }

    @Override
    public String toString() {
        return "IdentifierDE{" +
                "type='" + type + '\'' +
                ", use=" + use +
                ", value='" + value + '\'' +
                '}';
    }
}
