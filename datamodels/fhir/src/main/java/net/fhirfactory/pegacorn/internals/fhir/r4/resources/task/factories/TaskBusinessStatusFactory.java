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
import net.fhirfactory.pegacorn.core.constants.systemwide.DRICaTSReferenceProperties;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.valuesets.TaskExtensionSystemEnum;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TaskBusinessStatusFactory {

    private ObjectMapper jsonMapper;

    private static final String DRICATS_TASK_BUSINESS_STATUS_SYSTEM = "/task-business-status";
    @Inject
    private DRICaTSReferenceProperties systemWideProperties;

    @Inject
    private TaskExtensionSystemFactory taskExtensionSystems;

    //
    // Constructor(s)
    //

    public TaskBusinessStatusFactory(){
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

    public String getDRICaTSTaskStatusReasonSystem(){
        String codeSystem = systemWideProperties.getDRICaTSCodeSystemSite() + DRICATS_TASK_BUSINESS_STATUS_SYSTEM;
        return (codeSystem);
    }

    public CodeableConcept newBusinessStatusFromOutcomeStatus(TaskOutcomeStatusType taskOutcomeStatus ){
        Coding businessStatusCoding = new Coding();
        businessStatusCoding.setSystem(getDRICaTSTaskStatusReasonSystem());
        businessStatusCoding.setCode(taskOutcomeStatus.getOutcomeStatus().getToken());
        businessStatusCoding.setDisplay(taskOutcomeStatus.getOutcomeStatus().getDisplayName());
        CodeableConcept businessStatusCC = new CodeableConcept();
        businessStatusCC.setText(businessStatusCoding.getDisplay());
        businessStatusCC.addCoding(businessStatusCoding);
        businessStatusCC.setText(businessStatusCoding.getDisplay());

        try {
            if (taskOutcomeStatus.getEntryInstant() != null) {
                String sequenceNumberJSON = jsonMapper.writeValueAsString(taskOutcomeStatus.getEntryInstant());
                Extension taskSequenceNumberExtension = new Extension();
                taskSequenceNumberExtension.setUrl(taskExtensionSystems.getDricatsTaskExtensionSystemURL(TaskExtensionSystemEnum.TASK_OUTCOME_ENTRY_INSTANT_EXTENSION_URL));
                taskSequenceNumberExtension.setValue(new StringType(sequenceNumberJSON));
                businessStatusCC.addExtension(taskSequenceNumberExtension);
            }
        } catch(Exception ex){
            // do nothing :)
        }

        return(businessStatusCC);
    }
}
