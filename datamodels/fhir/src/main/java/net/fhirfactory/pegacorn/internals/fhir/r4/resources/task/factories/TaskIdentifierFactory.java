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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.DRICaTSIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.DRICaTSIdentifierFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.valuesets.TaskExtensionSystemEnum;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class TaskIdentifierFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TaskIdentifierFactory.class);

    private ObjectMapper jsonMapper;

    @Inject
    private DRICaTSIdentifierFactory generalIdentifierFactory;

    @Inject
    private TaskExtensionSystemFactory taskExtensionSystems;


    //
    // Constructor(s)
    //

    public TaskIdentifierFactory(){
        this.jsonMapper = new ObjectMapper();
    }

    //
    // Getters (and Setters)
    //

    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    //
    // Business Methods
    //

    public Identifier newTaskIdentifier(TaskTypeTypeEnum taskType, TaskIdType taskId, Instant startInstant, Instant endInstant){
        getLogger().debug(".newActionableTaskIdentifier(): Entry, taskId->{}", taskId);
        Identifier fhirIdentifier = new Identifier();
        Period identifierPeriod = new Period();
        if(startInstant != null){
            identifierPeriod.setStart(Date.from(startInstant));
        } else {
            identifierPeriod.setStart(Date.from(Instant.now()));
        }
        if(endInstant != null){
            identifierPeriod.setEnd(Date.from(endInstant));
        }
        DRICaTSIdentifierCodeEnum identifierCode = null;
        switch(taskType){
            case PETASOS_BASE_TASK_TYPE:
            case PETASOS_ACTIONABLE_TASK_TYPE:
                identifierCode = DRICaTSIdentifierCodeEnum.IDENTIFIER_CODE_ACTIONABLE_TASK;
                break;
            case PETASOS_FULFILLMENT_TASK_TYPE:
                identifierCode = DRICaTSIdentifierCodeEnum.IDENTIFIER_CODE_FULFILLMENT_TASK;
                break;
            case PETASOS_AGGREGATE_TASK_TYPE:
                identifierCode = DRICaTSIdentifierCodeEnum.IDENTIFIER_CODE_AGGREGATE_TASK;
                break;
        }
        fhirIdentifier = generalIdentifierFactory.newIdentifier(identifierCode, taskId.getLocalId(), identifierPeriod);

        try {
            if(taskId.getTaskSequenceNumber() != null) {
                String sequenceNumberJSON = jsonMapper.writeValueAsString(taskId.getTaskSequenceNumber());
                Extension taskSequenceNumberExtension = new Extension();
                taskSequenceNumberExtension.setUrl(taskExtensionSystems.getDricatsTaskExtensionSystemURL(TaskExtensionSystemEnum.TASK_SEQUENCE_NUMBER));
                taskSequenceNumberExtension.setValue(new StringType(sequenceNumberJSON));
                fhirIdentifier.addExtension(taskSequenceNumberExtension);
            }
        } catch(Exception ex){
            getLogger().warn(".newTaskIdentifier(): Could not transform to JSON the Task Sequence Number");
        }

        getLogger().debug(".newActionableTaskIdentifier(): Exit, fhirIdentifier->{}", fhirIdentifier);
        return(fhirIdentifier);
    }

    public Identifier newTaskIdentifier(TaskTypeTypeEnum taskType, TaskIdType taskId, Instant endInstant){
        getLogger().debug(".newActionableTaskIdentifier(): Entry, taskId->{}", taskId);
        Identifier fhirIdentifier = fhirIdentifier = newTaskIdentifier(taskType, taskId, null, endInstant);
        getLogger().debug(".newActionableTaskIdentifier(): Exit, fhirIdentifier->{}", fhirIdentifier);
        return(fhirIdentifier);
    }

    public Identifier newTaskIdentifier(TaskTypeTypeEnum taskType, TaskIdType taskId){
        getLogger().debug(".newActionableTaskIdentifier(): Entry, taskId->{}", taskId);
        Identifier fhirIdentifier = newTaskIdentifier(taskType, taskId, null, null);
        getLogger().debug(".newActionableTaskIdentifier(): Exit, fhirIdentifier->{}", fhirIdentifier);
        return(fhirIdentifier);
    }


    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
