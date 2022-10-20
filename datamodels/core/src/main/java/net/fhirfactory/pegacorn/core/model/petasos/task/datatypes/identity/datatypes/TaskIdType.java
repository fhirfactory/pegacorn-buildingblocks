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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.keyring.PegacornResourceKeyring;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;

public class TaskIdType extends PegacornResourceKeyring {

    private TaskSequenceNumber taskSequenceNumber;

    public static final String RESOURCE_TYPE = "PetasosTask";

    //
    // Constructors
    //

    public TaskIdType(){
        super();
        setResourceType(RESOURCE_TYPE);
    }

    public TaskIdType(IdType id, String keyContext){
        super(id, keyContext);
        setResourceType(RESOURCE_TYPE);
    }

    public TaskIdType(Identifier identifier){
        super(identifier);
        setResourceType(RESOURCE_TYPE);
    }

    public TaskIdType(TaskIdType ori){
        super(ori);
    }

    public TaskSequenceNumber getTaskSequenceNumber() {
        return taskSequenceNumber;
    }

    public void setTaskSequenceNumber(TaskSequenceNumber taskSequenceNumber) {
        this.taskSequenceNumber = taskSequenceNumber;
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public String getId(){
        return(getLocalId());
    }

    @JsonIgnore
    public void setId(String id){
        setLocalId(id);
    }

    //
    // Hashcode && Equals
    //

    //
    // To String
    //


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskIdType{");
        sb.append("taskSequenceNumber=").append(taskSequenceNumber);
        sb.append(", id=").append(getId());
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }
}
