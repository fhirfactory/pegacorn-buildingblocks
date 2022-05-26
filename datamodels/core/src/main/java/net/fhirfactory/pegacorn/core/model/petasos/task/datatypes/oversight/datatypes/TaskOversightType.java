/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.oversight.datatypes;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import org.apache.commons.lang3.SerializationUtils;

public class TaskOversightType extends TaskFulfillmentType {
    private TaskIdType fulfillmentTaskIdentifier;

    //
    // Constructor(s)
    //

    public TaskOversightType(){
        super();
        this.fulfillmentTaskIdentifier = null;
    }

    public TaskOversightType(TaskOversightType ori){
        super(ori);
        this.fulfillmentTaskIdentifier = null;
        if(ori.hasFulfillmentTaskIdentifier()){
            setFulfillmentTaskIdentifier(SerializationUtils.clone(ori.getFulfillmentTaskIdentifier()));
        }
    }

    //
    // Getters and Setters (Bean Methods)
    //

    public boolean hasFulfillmentTaskIdentifier(){
        boolean hasValue = this.fulfillmentTaskIdentifier != null;
        return(hasValue);
    }

    public TaskIdType getFulfillmentTaskIdentifier() {
        return fulfillmentTaskIdentifier;
    }

    public void setFulfillmentTaskIdentifier(TaskIdType fulfillmentTaskIdentifier) {
        this.fulfillmentTaskIdentifier = fulfillmentTaskIdentifier;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "OversightSegment{" +
                "registrationInstant=" + getRegistrationInstant() +
                ", readyInstant=" + getReadyInstant() +
                ", startInstant=" + getStartInstant() +
                ", finishInstant=" + getFinishInstant() +
                ", finalisationInstant=" + getFinalisationInstant() +
                ", lastCheckedInstant=" + getLastCheckedInstant() +
                ", trackingID=" + getTrackingID() +
                ", status=" + getStatus() +
                ", resilientActivity=" + isResilientActivity() +
                ", fulfillmentTaskIdentifier=" + fulfillmentTaskIdentifier +
                '}';
    }
}
