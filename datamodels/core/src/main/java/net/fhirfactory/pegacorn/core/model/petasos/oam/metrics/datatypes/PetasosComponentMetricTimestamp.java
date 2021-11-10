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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;


import java.io.Serializable;
import java.time.Instant;

public class PetasosComponentMetricTimestamp implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant measurementCaptureInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant measurementReportingInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Long measurementDuration;

    //
    // Constructor(s)
    //

    public PetasosComponentMetricTimestamp(){
        this.measurementCaptureInstant = null;
        this.measurementReportingInstant = null;
        this.measurementDuration = null;
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasMeasurementCaptureInstant(){
        boolean hasValue = this.measurementCaptureInstant != null;
        return(hasValue);
    }

    public Instant getMeasurementCaptureInstant() {
        return measurementCaptureInstant;
    }

    public void setMeasurementCaptureInstant(Instant measurementCaptureInstant) {
        this.measurementCaptureInstant = measurementCaptureInstant;
    }

    @JsonIgnore
    public boolean hasMeasurementReportingInstant(){
        boolean hasValue = this.measurementReportingInstant != null;
        return(hasValue);
    }

    public Instant getMeasurementReportingInstant() {
        return measurementReportingInstant;
    }

    public void setMeasurementReportingInstant(Instant measurementReportingInstant) {
        this.measurementReportingInstant = measurementReportingInstant;
    }

    @JsonIgnore
    public boolean hasMeasurementDuration(){
        boolean hasValue = this.measurementDuration != null;
        return(hasValue);
    }

    public Long getMeasurementDuration() {
        return measurementDuration;
    }

    public void setMeasurementDuration(Long measurementDuration) {
        this.measurementDuration = measurementDuration;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosComponentMetricTimestamp{" +
                "measurementCaptureInstant=" + measurementCaptureInstant +
                ", measurementReportingInstant=" + measurementReportingInstant +
                ", measurementDuration=" + measurementDuration +
                '}';
    }
}
