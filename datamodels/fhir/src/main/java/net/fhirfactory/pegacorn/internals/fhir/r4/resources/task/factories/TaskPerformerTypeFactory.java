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

import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TaskPerformerTypeFactory {

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    private static final String PEGACORN_TASK_PERFORMER_PARTICIPANT_TYPE = "/task-performer-participant-type";
    private static final String PEGACORN_TASK_PERFORMER_PARTICIPANT_SUBSYSTEM_TYPE = "/task-performer-participant-subsystem-type";

    //
    // Business Methods
    //

    public String getPegacornTaskPerformerParticipantType(){
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_TASK_PERFORMER_PARTICIPANT_TYPE;
        return (codeSystem);
    }

    public String getPegacornTaskPerformerParticipantSubsystemType(){
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_TASK_PERFORMER_PARTICIPANT_SUBSYSTEM_TYPE;
        return (codeSystem);
    }

    public CodeableConcept newTaskPerformerType(PetasosParticipantId participantId){
        CodeableConcept performerTypeCC = new CodeableConcept();
        Coding performerTypeCoding = new Coding();
        performerTypeCoding.setSystem(getPegacornTaskPerformerParticipantType());
        performerTypeCoding.setCode(participantId.getName());
        performerTypeCoding.setVersion(participantId.getVersion());
        performerTypeCoding.setDisplay(participantId.getDisplayName());
        performerTypeCC.addCoding(performerTypeCoding);
        Coding performerSubsystemTypeCoding = new Coding();
        performerSubsystemTypeCoding.setSystem(getPegacornTaskPerformerParticipantSubsystemType());
        performerSubsystemTypeCoding.setCode(participantId.getSubsystemName());
        performerSubsystemTypeCoding.setVersion(participantId.getVersion());
        performerSubsystemTypeCoding.setDisplay(participantId.getSubsystemName());
        performerTypeCC.addCoding(performerTypeCoding);
        return(performerTypeCC);
    }

    public TaskPerformerTypeType newTaskPerformTypeType(CodeableConcept codeableConcept){
        Coding taskPerformerTypeCode = extractCodeFromCodeableConceptForTaskPerformerParticipantType(codeableConcept);
        Coding taskPerformerSubsystemCode = extractCodeFromCodeableConceptForTaskPerformerParticipantSubsystemType(codeableConcept);
        if((taskPerformerTypeCode != null) && (taskPerformerSubsystemCode != null)) {
            TaskPerformerTypeType taskPerformerType = new TaskPerformerTypeType();
            PetasosParticipantId participantId = new PetasosParticipantId();
            participantId.setName(taskPerformerTypeCode.getCode());
            participantId.setFullName(codeableConcept.getText());
            participantId.setVersion(taskPerformerTypeCode.getVersion());
            participantId.setDisplayName(taskPerformerTypeCode.getDisplay());
            participantId.setSubsystemName(taskPerformerSubsystemCode.getCode());
            TaskPerformerTypeType taskPerformer = new TaskPerformerTypeType();
            taskPerformer.setCapabilityBased(false);
            taskPerformer.setKnownTaskPerformer(participantId);
            return(taskPerformerType);
        }
        return(null);
    }

    public Coding extractCodeFromCodeableConceptForTaskPerformerParticipantType(CodeableConcept performerType){
        if(performerType == null){
            return(null);
        }
        if (performerType.getCoding() == null) {
            return(null);
        }
        for(Coding code: performerType.getCoding()){
            if(code.getSystem().contentEquals(getPegacornTaskPerformerParticipantType())){
                return(code);
            }
        }
        return(null);
    }

    public Coding extractCodeFromCodeableConceptForTaskPerformerParticipantSubsystemType(CodeableConcept performerType){
        if(performerType == null){
            return(null);
        }
        if (performerType.getCoding() == null) {
            return(null);
        }
        for(Coding code: performerType.getCoding()){
            if(code.getSystem().contentEquals(getPegacornTaskPerformerParticipantSubsystemType())){
                return(code);
            }
        }
        return(null);
    }
}
