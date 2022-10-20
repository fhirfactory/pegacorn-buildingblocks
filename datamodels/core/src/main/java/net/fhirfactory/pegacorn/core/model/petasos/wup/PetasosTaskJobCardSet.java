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
package net.fhirfactory.pegacorn.core.model.petasos.wup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.internals.SerializableObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PetasosTaskJobCardSet implements Serializable {

    private HashMap<TaskIdType, PetasosTaskJobCard> jobCardSet;
    private SerializableObject jobCardSetLock;

    //
    // Constructor(s)
    //

    public PetasosTaskJobCardSet(){
        this.jobCardSet = new HashMap<>();
        jobCardSetLock = new SerializableObject();
    }

    //
    // Map Methods
    //

    @JsonIgnore
    public void addPetasosTaskJobCard(PetasosTaskJobCard card){
        //
        // (Defensive Programming) Check the passed in parameter
        if(card == null){
            return;
        }
        if(!card.hasTaskId() ){
            return;
        }
        //
        // Now add
        TaskIdType taskId = card.getTaskId();
        synchronized (getJobCardSetLock()){
            if(jobCardSet.containsKey(taskId)){
                jobCardSet.remove(taskId);
            }
            jobCardSet.put(taskId, card);
        }
        //
        // All done!
    }

    @JsonIgnore
    public void removePetasosTaskJobCard(PetasosTaskJobCard card){
        //
        // (Defensive Programming) Check the passed in parameter
        if(card == null){
            return;
        }
        if(!card.hasTaskId()){
            return;
        }
        //
        // Now remove
        TaskIdType taskId = card.getTaskId();
        synchronized (getJobCardSetLock()){
            if(jobCardSet.containsKey(taskId)){
                jobCardSet.remove(taskId);
            }
        }
        //
        // All done!
    }

    @JsonIgnore
    public PetasosTaskJobCard getPetasosTaskJobCard(TaskIdType fulfillmentTaskIdentifier){
        //
        // (Defensive Programming) Check the passed in parameter
        if(fulfillmentTaskIdentifier == null){
            return(null);
        }
        //
        // Now remove
        PetasosTaskJobCard card = null;
        synchronized (getJobCardSetLock()){
            if(jobCardSet.containsKey(fulfillmentTaskIdentifier)){
                card = jobCardSet.get(fulfillmentTaskIdentifier);
            }
        }
        //
        // All done!
        return(card);
    }

    @JsonIgnore
    public List<PetasosTaskJobCard> getAllPetasosTaskJobCard(){
        //
        // Build List
        List<PetasosTaskJobCard> cardList = new ArrayList<>();
        synchronized (getJobCardSetLock()){
            cardList.addAll(getJobCardSet().values());
        }
        //
        // All done!
        return(cardList);
    }

    //
    // Getters and Setters
    //

    public HashMap<TaskIdType, PetasosTaskJobCard> getJobCardSet() {
        return jobCardSet;
    }

    public void setJobCardSet(HashMap<TaskIdType, PetasosTaskJobCard> jobCardSet) {
        this.jobCardSet = jobCardSet;
    }

    public SerializableObject getJobCardSetLock() {
        return jobCardSetLock;
    }

    public void setJobCardSetLock(SerializableObject jobCardSetLock) {
        this.jobCardSetLock = jobCardSetLock;
    }

//
    // To String
    //

    @Override
    public String toString() {
        return "PetasosTaskJobCardSet{" +
                "jobCardSet=" + jobCardSet +
                '}';
    }
}
