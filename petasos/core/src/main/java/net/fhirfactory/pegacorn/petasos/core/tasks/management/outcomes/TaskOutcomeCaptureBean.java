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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.outcomes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalTaskActivityManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalActionableTaskCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.queue.LocalTaskQueueCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.queue.LocalTaskQueueManager;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.agents.WorkUnitProcessorTaskReportAgent;
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
    private LocalTaskActivityManager actionableTaskActivityController;

    @Inject
    private LocalActionableTaskCache localActionableTaskCache;


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
        if(getLogger().isDebugEnabled()) {
            getLogger().debug(".captureAndRegisterOutcome(): Entry, fulfillmentTask->{}", convertToString(fulfillmentTask));
        }

        getLogger().trace(".captureAndRegisterOutcome(): [Update Task Status With Central (Ponos)] Start");
        TaskIdType actionableTaskId = fulfillmentTask.getActionableTaskId();
        TaskExecutionCommandEnum petasosTaskExecutionStatus = null;
        switch(fulfillmentTask.getTaskFulfillment().getStatus()){
            case FULFILLMENT_EXECUTION_STATUS_UNREGISTERED:
            case FULFILLMENT_EXECUTION_STATUS_REGISTERED:
            case FULFILLMENT_EXECUTION_STATUS_INITIATED:
            case FULFILLMENT_EXECUTION_STATUS_FAILED:
                getLogger().debug(".captureAndRegisterOutcome(): fulfillmentTask.getTaskFulfillment().getStatus() Implies failure...");
                petasosTaskExecutionStatus = actionableTaskActivityController.notifyTaskFailure(actionableTaskId, fulfillmentTask);
                break;
            case FULFILLMENT_EXECUTION_STATUS_CANCELLED:
            case FULFILLMENT_EXECUTION_STATUS_ACTIVE_ELSEWHERE:
            case FULFILLMENT_EXECUTION_STATUS_FINISHED_ELSEWHERE:
            case FULFILLMENT_EXECUTION_STATUS_FINALISED_ELSEWHERE:
                getLogger().debug(".captureAndRegisterOutcome(): fulfillmentTask.getTaskFulfillment().getStatus() Implies cancellation...");
                petasosTaskExecutionStatus = actionableTaskActivityController.notifyTaskCancellation(actionableTaskId, fulfillmentTask);
                break;
            case FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED:
            case FULFILLMENT_EXECUTION_STATUS_FINISHED:
                getLogger().debug(".captureAndRegisterOutcome(): fulfillmentTask.getTaskFulfillment().getStatus() Implies (good) finish...");
                petasosTaskExecutionStatus = actionableTaskActivityController.notifyTaskFinish(actionableTaskId, fulfillmentTask);
                break;
        }
        getLogger().trace(".captureAndRegisterOutcome(): [Update Task Status With Central (Ponos)] Finish");

        //
        // Get the updated the ActionableTaskSharedInstance so we can do some metrics
        getLogger().trace(".captureAndRegisterOutcome(): [Retrieve ActionableTask From Cache] Start");
        PetasosActionableTask actionableTask = localActionableTaskCache.getTask(actionableTaskId);
        getLogger().trace(".captureAndRegisterOutcome(): [Retrieve ActionableTask From Cache] Finish, actionableTask->{}", actionableTask);

        //
        // Get out metricsAgent for the WUP that sent the task & do add some metrics
        getLogger().trace(".captureAndRegisterOutcome(): [Update some Metrics] Start");
        WorkUnitProcessorTaskReportAgent taskReportAgent = camelExchange.getProperty(PetasosPropertyConstants.ENDPOINT_TASK_REPORT_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorTaskReportAgent.class);
        if(taskReportAgent != null){
            getLogger().trace(".captureAndRegisterOutcome(): [Update some Metrics] metrics agent found!");
            taskReportAgent.sendITOpsTaskReport(actionableTask);
        }
        getLogger().trace(".captureAndRegisterOutcome(): [Update some Metrics] Finish");

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

    protected LocalTaskActivityManager getActionableTaskActivityController(){
        return(this.actionableTaskActivityController);
    }


}
