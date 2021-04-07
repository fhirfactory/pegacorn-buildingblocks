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

import java.util.ArrayList;
import java.util.Objects;

public class HumanNameDE {
    private HumanNameDEUseEnum nameUse;
    private String displayName;
    private String familyName;
    private ArrayList<String> givenNames;
    private String preferredGivenName;
    private ArrayList<String> prefixes;
    private ArrayList<String> suffixes;
    private EffectivePeriod period;

    public HumanNameDE(){
        this.givenNames = new ArrayList<>();
        this.prefixes = new ArrayList<>();
        this.suffixes = new ArrayList<>();
    }

    public HumanNameDEUseEnum getNameUse() {
        return nameUse;
    }

    public void setNameUse(HumanNameDEUseEnum nameUse) {
        this.nameUse = nameUse;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public ArrayList<String> getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(ArrayList<String> givenNames) {
        this.givenNames = givenNames;
    }

    public String getPreferredGivenName() {
        return preferredGivenName;
    }

    public void setPreferredGivenName(String preferredGivenName) {
        this.preferredGivenName = preferredGivenName;
    }

    public ArrayList<String> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(ArrayList<String> prefixes) {
        this.prefixes = prefixes;
    }

    public ArrayList<String> getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(ArrayList<String> suffixes) {
        this.suffixes = suffixes;
    }

    public EffectivePeriod getPeriod() {
        return period;
    }

    public void setPeriod(EffectivePeriod period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HumanNameDE)) return false;
        HumanNameDE that = (HumanNameDE) o;
        return getNameUse() == that.getNameUse() && getDisplayName().equals(that.getDisplayName()) && getFamilyName().equals(that.getFamilyName()) && getGivenNames().equals(that.getGivenNames()) && getPeriod().equals(that.getPeriod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNameUse(), getDisplayName(), getFamilyName(), getGivenNames(), getPeriod());
    }
}
