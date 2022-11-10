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
package net.fhirfactory.pegacorn.core.model.petasos.task.collections;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.util.HashSet;

public class PetasosTaskIdSet implements Serializable {
    private HashSet<TaskIdType> taskIdSet;

    //
    // Constructor(s)
    //

    public PetasosTaskIdSet(){
        this.taskIdSet = new HashSet<>();
    }

    //
    // Getters and Setters
    //

    public HashSet<TaskIdType> getTaskIdSet() {
        return taskIdSet;
    }

    public void setTaskIdSet(HashSet<TaskIdType> taskIdSet) {
        this.taskIdSet = taskIdSet;
    }

    public void addTaskId(TaskIdType taskId){
        if(taskId != null){
            if(getTaskIdSet().contains(taskId)){
                // do nothing
            } else {
                getTaskIdSet().add(taskId);
            }
        }
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosTaskIdSet{");
        sb.append("taskIdSet=").append(taskIdSet);
        sb.append('}');
        return sb.toString();
    }
}
