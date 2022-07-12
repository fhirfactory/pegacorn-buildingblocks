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
package net.fhirfactory.pegacorn.internals.hl7v2.segments;

import java.util.HashMap;
import java.util.Map;

public class ZDESegmentSet {
    private Map<Integer, ZDESegment> errorNotes;

    //
    // Constructor(s)
    //

    public ZDESegmentSet(){
        this.errorNotes = new HashMap<>();
    }

    public ZDESegmentSet(String zatString){
        this.errorNotes = new HashMap<>();
    }

    //
    // Getters and Setters
    //

    public Map<Integer, ZDESegment> getErrorNotes() {
        return errorNotes;
    }

    public void setErrorNotes(Map<Integer, ZDESegment> errorNotes) {
        this.errorNotes.clear();
        if(errorNotes != null) {
            for(Integer counter: errorNotes.keySet()){
                this.errorNotes.put(counter, errorNotes.get(counter));
            }
        }
    }

    public void compactErrorNotes(){
        if(this.errorNotes.isEmpty()){
            return;
        }
        Integer newCounter = 0;
        Map<Integer, ZDESegment> temporaryNoteSet = new HashMap<>();
        for(Integer counter: this.errorNotes.keySet()){
            temporaryNoteSet.put(newCounter, this.errorNotes.get(counter));
            newCounter += 1;
        }
        this.errorNotes.clear();
        for(Integer reloadCounter=0; reloadCounter < newCounter; reloadCounter += 1){
            this.errorNotes.put(reloadCounter, temporaryNoteSet.get(reloadCounter));
        }
    }

    public void addErrorNote(ZDESegment note){
        if(note != null) {
            compactErrorNotes();
            Integer size = this.errorNotes.size();
            note.setSetId(size);
            this.errorNotes.put(size, note);
        }
    }

    //
    // toString
    //

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ZATSegment{");
        sb.append("errorNotes=").append(errorNotes);
        sb.append("}");
        return(sb.toString());
    }
}
