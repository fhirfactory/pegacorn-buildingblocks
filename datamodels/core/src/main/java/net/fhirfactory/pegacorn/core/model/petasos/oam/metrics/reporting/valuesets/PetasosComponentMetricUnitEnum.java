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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.valuesets;

public enum PetasosComponentMetricUnitEnum {
    INTEGER_COUNT("IntegerCount", "petasos.component.metrics.unit.integer-count"),
    TIME_INSTANT("TimeInstant", "petasos.component.metrics.unit.time-instant" ),
    TIME_DURATION_MILLISECONDS("Milliseconds", "petasos.component.metrics.unit.time-milliseconds"),
    TIME_DURATION_SECONDS("Seconds", "petasos.component.metrics.unit.time-seconds"),
    TIME_DURATION_MINUTES("Minutes", "petasos.component.metrics.unit.time-minutes"),
    TIME_DURATION_HOURS("Hours", "petasos.component.metrics.unit.time-hours"),
    TIME_DURATION_DAYS("Days", "petasos.component.metrics.unit.time-days"),
    STRING_DESCRIPTION("DisplayValue", "petasos.component.metrics.unit.description");

    private String displayName;
    private String token;

    private PetasosComponentMetricUnitEnum(String displayName, String token){
        this.displayName = displayName;
        this.token = token;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getToken(){
        return(this.token);
    }
}
