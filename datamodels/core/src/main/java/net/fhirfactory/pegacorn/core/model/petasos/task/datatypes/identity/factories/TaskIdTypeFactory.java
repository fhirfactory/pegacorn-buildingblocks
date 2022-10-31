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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.factories;

import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskSequenceNumber;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.reason.valuesets.TaskReasonTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class TaskIdTypeFactory {

    public TaskIdType newTaskId(TaskReasonTypeEnum taskReason, String participantName){
        StringBuilder idBuilder = new StringBuilder();
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        if(StringUtils.isNotEmpty(participantName)){
            idBuilder.append(participantName);
            idBuilder.append("-");
        }
        idBuilder.append(taskReason.getTaskReasonDisplayName());
        idBuilder.append("-");
        idBuilder.append(uuidString);
        TaskIdType id = new TaskIdType();
        id.setLocalId(idBuilder.toString());
        TaskSequenceNumber sequenceNumber = new TaskSequenceNumber(uuid.getLeastSignificantBits());
        id.setTaskSequenceNumber(sequenceNumber);
        Identifier identifier = new Identifier();
        return(id);
    }

     public TaskIdType newTaskId(TaskReasonTypeEnum taskReason){
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(taskReason.getTaskReasonDisplayName());
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        idBuilder.append("-");
        idBuilder.append(uuidString);
        TaskIdType id = new TaskIdType();
        id.setLocalId(idBuilder.toString());
        TaskSequenceNumber sequenceNumber = new TaskSequenceNumber(uuid.getLeastSignificantBits());
        id.setTaskSequenceNumber(sequenceNumber);
        Identifier identifier = new Identifier();
        return(id);
    }

    public TaskIdType newTaskId(){
        TaskIdType taskId = newTaskId();
        TaskSequenceNumber sequenceNumber = new TaskSequenceNumber(UUID.randomUUID().getLeastSignificantBits());
        taskId.setTaskSequenceNumber(sequenceNumber);
        return(taskId);
    }

    public TaskIdType newTaskId(IdType id){
        TaskIdType taskId = new TaskIdType();
        taskId.setResourceId(id);
        UUID uuid = UUID.randomUUID();
        String localId = uuid.toString();
        taskId.setLocalId(localId);
        TaskSequenceNumber sequenceNumber = new TaskSequenceNumber(uuid.getLeastSignificantBits());
        taskId.setTaskSequenceNumber(sequenceNumber);
        return(taskId);
    }

    public TaskIdType newTaskId(Identifier identifier){
        TaskIdType taskId = new TaskIdType();
        taskId.setPrimaryBusinessIdentifier(identifier);
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        taskId.setLocalId(id);
        TaskSequenceNumber sequenceNumber = new TaskSequenceNumber(uuid.getLeastSignificantBits());
        taskId.setTaskSequenceNumber(sequenceNumber);
        return(taskId);
    }
}
