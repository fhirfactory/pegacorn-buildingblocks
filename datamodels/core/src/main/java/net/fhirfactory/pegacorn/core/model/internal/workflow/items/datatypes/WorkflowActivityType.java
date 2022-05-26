/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.internal.workflow.items.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkflowActivityType implements Serializable {
    private String activityDomain;
    private String activityCategory;
    private String activitySubCategory;
    private String activityGroup;
    private String activityAction;
    private List<String> activityParameters;

    //
    // Constructor(s)
    //

    public WorkflowActivityType(){
        this.activityAction = null;
        this.activityCategory = null;
        this.activitySubCategory = null;
        this.activityGroup = null;
        this.activityDomain = null;
        activityParameters = new ArrayList<>();
    }

    //
    // Getters and Setters
    //

    public String getActivityDomain() {
        return activityDomain;
    }

    public void setActivityDomain(String activityDomain) {
        this.activityDomain = activityDomain;
    }

    public String getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
    }

    public String getActivitySubCategory() {
        return activitySubCategory;
    }

    public void setActivitySubCategory(String activitySubCategory) {
        this.activitySubCategory = activitySubCategory;
    }

    public String getActivityGroup() {
        return activityGroup;
    }

    public void setActivityGroup(String activityGroup) {
        this.activityGroup = activityGroup;
    }

    public String getActivityAction() {
        return activityAction;
    }

    public void setActivityAction(String activityAction) {
        this.activityAction = activityAction;
    }

    public List<String> getActivityParameters() {
        return activityParameters;
    }

    public void setActivityParameters(List<String> activityParameters) {
        this.activityParameters = activityParameters;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "WorkflowActivityType{" +
                "activityDomain='" + activityDomain + '\'' +
                ", activityCategory='" + activityCategory + '\'' +
                ", activitySubCategory='" + activitySubCategory + '\'' +
                ", activityGroup='" + activityGroup + '\'' +
                ", activityAction='" + activityAction + '\'' +
                ", activityParameters=" + activityParameters +
                '}';
    }
}
