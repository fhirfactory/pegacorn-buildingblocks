/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.outcomes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosActionableTaskActivityController;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TaskOutcomeCaptureBean {
    private static final Logger LOG = LoggerFactory.getLogger(TaskOutcomeCaptureBean.class);

    private ObjectMapper jsonMapper;

    @Inject
    private LocalPetasosFulfilmentTaskActivityController fulfilmentTaskActivityController;

    @Inject
    private LocalPetasosActionableTaskActivityController actionableTaskActivityController;

    //
    // Constructor
    //

    public TaskOutcomeCaptureBean() {
        jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;
        JavaTimeModule module = new JavaTimeModule();
        jsonMapper.registerModule(module);
    }

    //
    // Post Construct
    //


    //
    // Business Logic
    //

    public PetasosActionableTask captureAndRegisterOutcome(PetasosFulfillmentTask fulfillmentTask, Exchange camelExchange){
        if(getLogger().isInfoEnabled()) {
            getLogger().info(".captureAndRegisterOutcome(): Entry, fulfillmentTask->{}", convertToString(fulfillmentTask));
        }
        TaskIdType actionableTaskId = fulfillmentTask.getActionableTaskId();
        PetasosActionableTask actionableTask = null;
        switch(fulfillmentTask.getTaskFulfillment().getStatus()){
            case FULFILLMENT_EXECUTION_STATUS_UNREGISTERED:
            case FULFILLMENT_EXECUTION_STATUS_REGISTERED:
            case FULFILLMENT_EXECUTION_STATUS_INITIATED:
            case FULFILLMENT_EXECUTION_STATUS_ACTIVE_ELSEWHERE:
            case FULFILLMENT_EXECUTION_STATUS_FINISHED_ELSEWHERE:
            case FULFILLMENT_EXECUTION_STATUS_FINALISED_ELSEWHERE:
            case FULFILLMENT_EXECUTION_STATUS_FAILED:
                getLogger().info(".captureAndRegisterOutcome(): fulfillmentTask.getTaskFulfillment().getStatus() Implies failure...");
                actionableTask = actionableTaskActivityController.notifyActionableTaskExecutionFailure(actionableTaskId, fulfillmentTask);
                break;
            case FULFILLMENT_EXECUTION_STATUS_CANCELLED:
                getLogger().info(".captureAndRegisterOutcome(): fulfillmentTask.getTaskFulfillment().getStatus() Implies cancellation...");
                actionableTask = actionableTaskActivityController.notifyActionableTaskExecutionCancellation(actionableTaskId, fulfillmentTask);
                break;
            case FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED:
            case FULFILLMENT_EXECUTION_STATUS_FINISHED:
                getLogger().info(".captureAndRegisterOutcome(): fulfillmentTask.getTaskFulfillment().getStatus() Implies (good) finish...");
                actionableTask = actionableTaskActivityController.notifyActionableTaskExecutionFinish(actionableTaskId, fulfillmentTask);
                break;
        }
        if(getLogger().isDebugEnabled()) {
            getLogger().debug(".captureAndRegisterOutcome(): Exit, actionableTask->{}", convertToString(actionableTask));
        }
        return(actionableTask);
    }

    protected String convertToString(PetasosTask petasosTask){
        try {
            String jsonString = jsonMapper.writeValueAsString(petasosTask);
            return(jsonString);
        } catch (JsonProcessingException e) {
            return(ExceptionUtils.getStackTrace(e));
        }
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected LocalPetasosFulfilmentTaskActivityController getFulfilmentTaskActivityController(){
        return(this.fulfilmentTaskActivityController);
    }

    protected LocalPetasosActionableTaskActivityController getActionableTaskActivityController(){
        return(this.actionableTaskActivityController);
    }
}
