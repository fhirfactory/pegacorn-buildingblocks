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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TaskSequenceNumber {
    private Long majorSequenceNumber;
    private Long minorSequenceNumber;

    //
    // Constructor(s)
    //

    public TaskSequenceNumber(){
        this.majorSequenceNumber = null;
        this.minorSequenceNumber = null;
    }

    public TaskSequenceNumber(Long minorNumber){
        this.majorSequenceNumber = Instant.now().getEpochSecond();
        this.minorSequenceNumber = minorNumber;
    }

    public TaskSequenceNumber(Long majorNumber, Long minorNumber){
        this.majorSequenceNumber = majorNumber;
        this.minorSequenceNumber = minorNumber;
    }

    public TaskSequenceNumber(TaskSequenceNumber ori){
        if(ori.getMajorSequenceNumber() == null){
            this.majorSequenceNumber = Instant.now().getEpochSecond();
        } else {
            this.majorSequenceNumber = ori.getMajorSequenceNumber();
        }
        if(ori.getMinorSequenceNumber() == null){
            this.minorSequenceNumber = 0L;
        } else {
            this.minorSequenceNumber = ori.getMinorSequenceNumber();
        }
    }

    //
    // Getters and Setters
    //

    public Long getMajorSequenceNumber() {
        return majorSequenceNumber;
    }

    public void setMajorSequenceNumber(Long majorSequenceNumber) {
        this.majorSequenceNumber = majorSequenceNumber;
    }

    public Long getMinorSequenceNumber() {
        return minorSequenceNumber;
    }

    public void setMinorSequenceNumber(Long minorSequenceNumber) {
        this.minorSequenceNumber = minorSequenceNumber;
    }

    @JsonIgnore
    public String getCompleteSequenceNumberAsString(){
        String majorNumberString = "Undefined";
        String minorNumberString = "Undefined";
        if(getMajorSequenceNumber() != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of(PetasosPropertyConstants.DEFAULT_TIMEZONE));
            Instant instant = Instant.ofEpochSecond(getMajorSequenceNumber());
            majorNumberString = timeFormatter.format(instant);
        }
        if(getMinorSequenceNumber() != null){
            minorNumberString = Long.toString(getMinorSequenceNumber());
        }
        String value = majorNumberString + "." + minorNumberString;
        return(value);
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskSequenceNumber{");
        sb.append("majorSequenceNumber=").append(majorSequenceNumber);
        sb.append(", minorSequenceNumber=").append(minorSequenceNumber);
        sb.append(", singleString=").append(getCompleteSequenceNumberAsString());
        sb.append('}');
        return sb.toString();
    }
}
