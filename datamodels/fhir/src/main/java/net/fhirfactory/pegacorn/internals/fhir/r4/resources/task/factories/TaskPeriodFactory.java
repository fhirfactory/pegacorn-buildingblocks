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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories;

import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.valuesets.TaskTransformConstants;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class TaskPeriodFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TaskPeriodFactory.class);

    @Inject
    private TaskTransformConstants taskTransformConstants;

    //
    // Constructor(s)
    //

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    //
    // Business Methods
    //

    public Period buildExecutionPeriod(PetasosTask petasosTask) {
        getLogger().debug(".specifyExecutionPeriod(): Entry");

        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;

        boolean hasCreationInstant = petasosTask.hasCreationInstant();
        boolean hasStartInstant = false;
        boolean hasFinishInstant = false;
        boolean hasRegistrationInstant = false;
        boolean hasReadyInstant = false;
        boolean hasFinalisationInstant = false;
        if(actionableTask.hasTaskFulfillment()) {
            hasStartInstant = actionableTask.getTaskFulfillment().hasStartInstant();
            hasFinishInstant = actionableTask.getTaskFulfillment().hasFinishInstant();
            hasRegistrationInstant = actionableTask.getTaskFulfillment().hasRegistrationInstant();
            hasReadyInstant = actionableTask.getTaskFulfillment().hasReadyInstant();
            hasFinalisationInstant = actionableTask.getTaskFulfillment().hasFinalisationInstant();
        }

        Instant startInstant = null;

        if(hasStartInstant) {
            startInstant = actionableTask.getTaskFulfillment().getStartInstant();
        } else {
            if(hasCreationInstant){
                startInstant = actionableTask.getCreationInstant();
            } else if(hasRegistrationInstant){
                startInstant = actionableTask.getTaskFulfillment().getRegistrationInstant();
            } else if(hasReadyInstant){
                startInstant = actionableTask.getTaskFulfillment().getReadyInstant();
            }
        }

        Instant finishInstant = null;
        if(hasFinishInstant){
            finishInstant = actionableTask.getTaskFulfillment().getFinishInstant();
        } else {
            if(hasFinalisationInstant) {
                finishInstant = actionableTask.getTaskFulfillment().getFinalisationInstant();
            }
        }

        Period executionPeriod = null;
        if(hasStartInstant || hasFinishInstant || hasRegistrationInstant || hasReadyInstant || hasFinalisationInstant){
            executionPeriod = new Period();
            Date startDate = null;
            Date finishDate = null;

            if(startInstant != null){
                try{
                    startDate = Date.from(startInstant);
                } catch(Exception ex){
                    getLogger().warn(".specifyExecutionPeriod(): Could not transform StartInstant into StartDate, message->",ex);
                }
            }
            if(finishInstant != null){
                try{
                    finishDate = Date.from(finishInstant);
                } catch(Exception ex){
                    getLogger().warn(".specifyExecutionPeriod(): Could not transform FinishInstant into FinishDate, message->",ex);
                }
            }

            if(startDate == null && finishDate == null){
                return(null);
            }

            executionPeriod.setStart(startDate);
            executionPeriod.setEnd(finishDate);

            if(hasRegistrationInstant) {
                Date registrationDate = null;
                if(actionableTask.getTaskFulfillment().getRegistrationInstant() != null){
                    try{
                        registrationDate = Date.from(actionableTask.getTaskFulfillment().getRegistrationInstant());
                    } catch(Exception ex){
                        getLogger().warn(".specifyExecutionPeriod(): Could not transform RegistrationInstant into RegistrationDate, message->",ex);
                    }
                }
                if(registrationDate != null) {
                    Extension registrationInstantExtension = new Extension();
                    registrationInstantExtension.setUrl(taskTransformConstants.getTaskRegistratonInstantExtensionUrl());
                    registrationInstantExtension.setValue(new InstantType(registrationDate));
                    executionPeriod.addExtension(registrationInstantExtension);
                }
            }
            if(hasReadyInstant) {
                Date readyDate = null;
                if(actionableTask.getTaskFulfillment().getReadyInstant() != null){
                    try{
                        readyDate = Date.from(actionableTask.getTaskFulfillment().getReadyInstant());
                    } catch(Exception ex){
                        getLogger().warn(".specifyExecutionPeriod(): Could not transform ReadyInstant into ReadyDate, message->",ex);
                    }
                }
                if(readyDate != null) {
                    Extension readyInstantExtension = new Extension();
                    readyInstantExtension.setUrl(taskTransformConstants.getTaskReadyInstantExtensionUrl());
                    readyInstantExtension.setValue(new InstantType(readyDate));
                    executionPeriod.addExtension(readyInstantExtension);
                }
            }
            if(hasFinalisationInstant){
                Date finalisationDate = null;
                if(actionableTask.getTaskFulfillment().getFinalisationInstant() != null){
                    try{
                        finalisationDate = Date.from(actionableTask.getTaskFulfillment().getFinalisationInstant());
                    } catch(Exception ex){
                        getLogger().warn(".specifyExecutionPeriod(): Could not transform FinalisationInstant into FinalisationDate, message->",ex);
                    }
                }
                if(finalisationDate != null) {
                    Extension finalisationInstantExtension = new Extension();
                    finalisationInstantExtension.setUrl(taskTransformConstants.getTaskFinalisationInstantExtensionUrl());
                    finalisationInstantExtension.setValue(new InstantType(finalisationDate));
                    executionPeriod.addExtension(finalisationInstantExtension);
                }
            }
        }
        getLogger().debug(".specifyExecutionPeriod(): Exit, executionPeriod->{}", executionPeriod);
        return (executionPeriod);
    }

    public Instant extractTaskStartInstant(Task fhirTask){
        if(fhirTask.hasExecutionPeriod()){
            if(fhirTask.getExecutionPeriod().hasStart()){
                Instant startInstant = fhirTask.getExecutionPeriod().getStart().toInstant();
                return(startInstant);
            }
        }
        return(null);
    }

    public Instant extractTaskRegistrationInstant(Task fhirTask){
        if(fhirTask.hasExecutionPeriod()){
            if(fhirTask.getExecutionPeriod().hasExtension(taskTransformConstants.getTaskRegistratonInstantExtensionUrl())){
                Extension registrationExtensionByUrl = fhirTask.getExecutionPeriod().getExtensionByUrl(taskTransformConstants.getTaskRegistratonInstantExtensionUrl());
                if(registrationExtensionByUrl != null || !registrationExtensionByUrl.isEmpty()){
                    try {
                        InstantType registrationDateType = (InstantType) registrationExtensionByUrl.getValue();
                        Date registrationDate = registrationDateType.getValue();
                        Instant registrationInstant = registrationDate.toInstant();
                        return (registrationInstant);
                    } catch(Exception ex){
                        getLogger().warn(".specifyExecutionPeriod(): Could not extract RegistrationInstant, message->",ex);
                        return(null);
                    }
                }
            }
        }
        return(null);
    }

    public Instant extractTaskFinishInstant(Task fhirTask){
        if(fhirTask.hasExecutionPeriod()){
            if(fhirTask.getExecutionPeriod().hasEnd()){
                Instant finishInstant = fhirTask.getExecutionPeriod().getEnd().toInstant();
                return(finishInstant);
            }
        }
        return(null);
    }

    public Instant extractTaskFinalisationInstant(Task fhirTask){
        if(fhirTask.hasExecutionPeriod()){
            if(fhirTask.getExecutionPeriod().hasExtension(taskTransformConstants.getTaskFinalisationInstantExtensionUrl())){
                Extension finalisationExtensionByUrl = fhirTask.getExecutionPeriod().getExtensionByUrl(taskTransformConstants.getTaskFinalisationInstantExtensionUrl());
                if(finalisationExtensionByUrl != null || !finalisationExtensionByUrl.isEmpty()){
                    try {
                        InstantType finalisationDateType = (InstantType) finalisationExtensionByUrl.getValue();
                        Date finalisationDate = finalisationDateType.getValue();
                        Instant finalisationInstant = finalisationDate.toInstant();
                        return (finalisationInstant);
                    } catch(Exception ex){
                        getLogger().warn(".specifyExecutionPeriod(): Could not extract FinalisationInstant, message->",ex);
                        return(null);
                    }
                }
            }
        }
        return(null);
    }

    public Instant extractTaskReadyInstant(Task fhirTask){
        if(fhirTask.hasExecutionPeriod()){
            if(fhirTask.getExecutionPeriod().hasExtension(taskTransformConstants.getTaskReadyInstantExtensionUrl())){
                Extension readyExtensionByUrl = fhirTask.getExecutionPeriod().getExtensionByUrl(taskTransformConstants.getTaskReadyInstantExtensionUrl());
                if(readyExtensionByUrl != null || !readyExtensionByUrl.isEmpty()){
                    try {
                        InstantType readyDateType = (InstantType) readyExtensionByUrl.getValue();
                        Date readyDate = readyDateType.getValue();
                        Instant readyInstant = readyDate.toInstant();
                        return (readyInstant);
                    } catch(Exception ex){
                        getLogger().warn(".specifyExecutionPeriod(): Could not extract FinalisationInstant, message->",ex);
                        return(null);
                    }
                }
            }
        }
        return(null);
    }
}
