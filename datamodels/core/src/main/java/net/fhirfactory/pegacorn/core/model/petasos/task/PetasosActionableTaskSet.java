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
package net.fhirfactory.pegacorn.core.model.petasos.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashMap;

public class PetasosActionableTaskSet {
    private HashMap<Integer, PetasosActionableTask> actionableTaskSet;

    //
    // Constructor(s)
    //

    public PetasosActionableTaskSet(){
        actionableTaskSet = new HashMap<>();
    }

    public PetasosActionableTaskSet(PetasosActionableTaskSet ori){
        actionableTaskSet = new HashMap<>();
        if(ori != null){
            for(Integer currentItem: ori.getActionableTaskSet().keySet()){
                PetasosActionableTask clonedTask = SerializationUtils.clone(ori.getActionableTaskSet().get(currentItem));
                actionableTaskSet.put(currentItem, clonedTask);
            }
        }
    }

    //
    // Getters and Setters
    //

    public HashMap<Integer, PetasosActionableTask> getActionableTaskSet() {
        return actionableTaskSet;
    }

    public void setActionableTaskSet(HashMap<Integer, PetasosActionableTask> actionableTaskSet) {
        this.actionableTaskSet = actionableTaskSet;
    }

    @JsonIgnore
    public PetasosActionableTask getActionableTask(Integer itemNumber){
        if(actionableTaskSet.isEmpty()){
            return(null);
        }
        if(itemNumber == null){
            return(null);
        }
        if(itemNumber < 0){
            return(null);
        }
        PetasosActionableTask task = actionableTaskSet.get(itemNumber);
        return(task);
    }

    @JsonIgnore
    public void addActionableTask(PetasosActionableTask task){
        if(task == null){
            return;
        }
        Integer currentEndNumber = 0;
        for(Integer currentNumber: actionableTaskSet.keySet()){
            if(currentNumber > currentEndNumber){
                currentEndNumber = currentNumber;
            }
        }
        actionableTaskSet.put(currentEndNumber+1, task);
    }

    @JsonIgnore void removeActionableTask(Integer itemNumber){
        if(itemNumber == null){
            return;
        }
        if(itemNumber < 0){
            return;
        }
        if(actionableTaskSet.containsKey(itemNumber)){
            actionableTaskSet.remove(itemNumber);
        }
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosActionableTaskSet{");
        sb.append("actionableTaskSet=").append(actionableTaskSet);
        sb.append('}');
        return sb.toString();
    }
}
