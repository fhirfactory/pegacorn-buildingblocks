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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes;

import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskWorkItemManifestSetType implements Serializable {
    private Set<TaskWorkItemManifestType> workItemManifestTypeSet;

    //
    // Constructor(s)
    //

    public TaskWorkItemManifestSetType(){
        this.workItemManifestTypeSet = new HashSet<>();
    }

    public TaskWorkItemManifestSetType(List<DataParcelManifest> parcelManifestSet){
        this.workItemManifestTypeSet = new HashSet<>();
        if(parcelManifestSet != null){
            if(!parcelManifestSet.isEmpty()){
                for(DataParcelManifest currentParcelManifest: parcelManifestSet){
                    TaskWorkItemManifestType currentWorkItemManifest = new TaskWorkItemManifestType(currentParcelManifest);
                    if(!workItemManifestTypeSet.contains(currentWorkItemManifest)){
                        workItemManifestTypeSet.add(currentWorkItemManifest);
                    }
                }
            }
        }
    }

    public TaskWorkItemManifestSetType(TaskWorkItemManifestSetType ori){
        this.workItemManifestTypeSet = new HashSet<>();
        if(ori != null){
            if(!ori.getWorkItemManifestTypeSet().isEmpty()){
                this.workItemManifestTypeSet.addAll(ori.getWorkItemManifestTypeSet());
            }
        }
    }

    //
    // Getters and Setters
    //

    public Set<TaskWorkItemManifestType> getWorkItemManifestTypeSet() {
        return workItemManifestTypeSet;
    }

    public void setWorkItemManifestTypeSet(Set<TaskWorkItemManifestType> workItemManifestTypeSet) {
        this.workItemManifestTypeSet = workItemManifestTypeSet;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskWorkItemManifestSetType{" +
                "workItemManifestTypeSet=" + workItemManifestTypeSet +
                '}';
    }
}
