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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.reason.datatypes.RetryTaskReasonType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.reason.valuesets.TaskReasonTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class TaskReasonFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TaskReasonFactory.class);

    private ObjectMapper jsonMapper;

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    @Inject
    private TaskIdentifierFactory taskIdentifierFactory;

    private static final String DRICATS_TASK_REASON_SYSTEM = "/task-reason";

    private static final String DRICATS_TASK_REASON_PREVIOUS_FAILED_TASK_EXTENSION_SYSTEM = "/extension/task-reason/previous-failed-task";
    private static final String DRICATS_TASK_REASON_PREVIOUS_FAILED_TASK_INSTANT_EXTENSION_SYSTEM = "/extension/task-reason/previous-failed-task-instant";
    private static final String DRICATS_TASK_REASON_ORIGINAL_FAILED_TASK_INSTANT_EXTENSION_SYSTEM = "/extension/task-reason/original-failed-task-instant";

    //
    // Constructor(s)
    //

    public TaskReasonFactory(){
        jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        JavaTimeModule module = new JavaTimeModule();
        this.jsonMapper.registerModule(module);
        this.jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public String getTaskReasonFailedOriginalTaskTimeSystemURL(){
        String system = systemWideProperties.getPegacornCodeSystemSite() + DRICATS_TASK_REASON_ORIGINAL_FAILED_TASK_INSTANT_EXTENSION_SYSTEM;
        return(system);
    }

    public String getTaskReasonFailedPreviousTaskTimeSystemURL(){
        String system = systemWideProperties.getPegacornCodeSystemSite() + DRICATS_TASK_REASON_ORIGINAL_FAILED_TASK_INSTANT_EXTENSION_SYSTEM;
        return(system);
    }

    public String getTaskReasonFailedPreviousTaskIdSystemURL(){
        String system = systemWideProperties.getPegacornCodeSystemSite() + DRICATS_TASK_REASON_PREVIOUS_FAILED_TASK_EXTENSION_SYSTEM;
        return(system);
    }

    public String getTaskReasonSystem(){
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + DRICATS_TASK_REASON_SYSTEM;
        return (codeSystem);
    }

    public CodeableConcept newTaskReason(TaskReasonTypeEnum taskReason ){
        CodeableConcept taskReasonCC = new CodeableConcept();
        Coding taskReasonCoding = new Coding();
        taskReasonCoding.setSystem(getTaskReasonSystem());
        taskReasonCoding.setCode(taskReason.getTaskReasonCode());
        taskReasonCoding.setDisplay(taskReason.getTaskReasonDisplayName());
        taskReasonCC.setText(taskReasonCoding.getDisplay());
        taskReasonCC.addCoding(taskReasonCoding);
        return(taskReasonCC);
    }

    public Reference newTaskReasonReference(RetryTaskReasonType retryTaskReason){
        if(retryTaskReason != null){
            if(retryTaskReason.hasOriginalTaskId()){
                Identifier originalTaskIdentifier = taskIdentifierFactory.newTaskIdentifier(TaskTypeTypeEnum.PETASOS_ACTIONABLE_TASK_TYPE, retryTaskReason.getOriginalTaskId());
                Reference reasonReference = new Reference();
                reasonReference.setIdentifier(originalTaskIdentifier);
                reasonReference.setType(ResourceType.Task.name());
                if(retryTaskReason.hasOriginalTaskExecutionInstant()){
                    String originalTaskInstant = instantToString(retryTaskReason.getOriginalTaskExecutionInstant());
                    if(StringUtils.isNotEmpty(originalTaskInstant)){
                        Extension originalTaskExecutionInstantExtension = new Extension();
                        originalTaskExecutionInstantExtension.setUrl(getTaskReasonFailedOriginalTaskTimeSystemURL());
                        originalTaskExecutionInstantExtension.setValue(new StringType(originalTaskInstant));
                        reasonReference.addExtension(originalTaskExecutionInstantExtension);
                    }
                }
                if(retryTaskReason.hasPreviousRetryTaskId()){
                    Identifier previousTaskIdentifier = taskIdentifierFactory.newTaskIdentifier(TaskTypeTypeEnum.PETASOS_ACTIONABLE_TASK_TYPE, retryTaskReason.getPreviousRetryTaskId());
                    Reference previousFailedTaskReasonReference = new Reference();
                    previousFailedTaskReasonReference.setIdentifier(previousTaskIdentifier);
                    previousFailedTaskReasonReference.setType(ResourceType.Task.name());
                    Extension previousTaskIdExtension = new Extension();
                    previousTaskIdExtension.setUrl(getTaskReasonFailedPreviousTaskIdSystemURL());
                    previousTaskIdExtension.setValue(previousFailedTaskReasonReference);
                    reasonReference.addExtension(previousTaskIdExtension);
                }
                if(retryTaskReason.hasPreviousTaskExecutionInstant()){
                    String previousTaskInstant = instantToString(retryTaskReason.getPreviousTaskExecutionInstant());
                    if(StringUtils.isNotEmpty(previousTaskInstant)){
                        Extension previousTaskExecutionInstantExtension = new Extension();
                        previousTaskExecutionInstantExtension.setUrl(getTaskReasonFailedPreviousTaskTimeSystemURL());
                        previousTaskExecutionInstantExtension.setValue(new StringType(previousTaskInstant));
                        reasonReference.addExtension(previousTaskExecutionInstantExtension);
                    }
                }
                return(reasonReference);
            }
        }
        return(null);
    }

    protected String instantToString(Instant instant){
        try {
            String s = jsonMapper.writeValueAsString(instant);
            return(s);
        } catch (JsonProcessingException e) {
            LOG.error(".instantToString(): Error -->{}", ExceptionUtils.getStackTrace(e));
            return(null);
        }
    }

    protected Instant instantFromString(String instantString){
        try {
            Instant instant = jsonMapper.readValue(instantString, Instant.class);
            return(instant);
        } catch (JsonProcessingException e) {
            LOG.error(".instantFromString(): Error -->{}", ExceptionUtils.getStackTrace(e));
            return(null);
        }
    }

    public RetryTaskReasonType extractRetryTaskReasonFromReference(Reference reasonReference){
        if(reasonReference == null){
            return(null);
        }
        if(reasonReference.getIdentifier() == null){
            return(null);
        }
        String taskIdValue = reasonReference.getIdentifier().getValue();
        TaskIdType originalFailedTaskId = new TaskIdType();
        originalFailedTaskId.setId(taskIdValue);
        RetryTaskReasonType retryTaskReason = new RetryTaskReasonType();
        retryTaskReason.setOriginalTaskId(originalFailedTaskId);
        if(reasonReference.hasExtension(getTaskReasonFailedOriginalTaskTimeSystemURL())){
            List<Extension> originalFailedTaskInstantExtensionList = reasonReference.getExtensionsByUrl(getTaskReasonFailedOriginalTaskTimeSystemURL());
            if(!originalFailedTaskInstantExtensionList.isEmpty()){
                Extension originalFailedTaskInstantExtension = originalFailedTaskInstantExtensionList.get(0);
                if(originalFailedTaskInstantExtension != null) {
                    StringType instantStringType = (StringType) originalFailedTaskInstantExtension.getValue();
                    Instant originalFailedTaskInstant = instantFromString(instantStringType.getValue());
                    if (originalFailedTaskInstant != null) {
                        retryTaskReason.setOriginalTaskExecutionInstant(originalFailedTaskInstant);
                    }
                }
            }
        }
        if(reasonReference.hasExtension(getTaskReasonFailedPreviousTaskIdSystemURL())){
            List<Extension> previousFailedTaskIdExtensionList = reasonReference.getExtensionsByUrl(getTaskReasonFailedPreviousTaskIdSystemURL());
            if(!previousFailedTaskIdExtensionList.isEmpty()) {
                Extension previousFailedTaskIdExtension = previousFailedTaskIdExtensionList.get(0);
                if (previousFailedTaskIdExtension != null) {
                    StringType previousTaskIdStringType = (StringType) previousFailedTaskIdExtension.getValue();
                    if(previousTaskIdStringType != null){
                        TaskIdType previousRetryTaskId = new TaskIdType();
                        previousRetryTaskId.setId(previousTaskIdStringType.getValue());
                        retryTaskReason.setPreviousRetryTaskId(previousRetryTaskId);
                    }
                }
            }
        }
        if(reasonReference.hasExtension(getTaskReasonFailedPreviousTaskTimeSystemURL())){
            List<Extension> previousFailedTaskInstantExtensionList = reasonReference.getExtensionsByUrl(getTaskReasonFailedPreviousTaskTimeSystemURL());
            if(!previousFailedTaskInstantExtensionList.isEmpty()){
                Extension previousFailedTaskInstantExtension = previousFailedTaskInstantExtensionList.get(0);
                if(previousFailedTaskInstantExtension != null) {
                    StringType instantStringType = (StringType) previousFailedTaskInstantExtension.getValue();
                    Instant previousFailedTaskInstant = instantFromString(instantStringType.getValue());
                    if (previousFailedTaskInstant != null) {
                        retryTaskReason.setPreviousTaskExecutionInstant(previousFailedTaskInstant);
                    }
                }
            }
        }
        return(retryTaskReason);
    }
}
