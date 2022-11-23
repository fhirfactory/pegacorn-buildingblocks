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
package net.fhirfactory.pegacorn.petasos.endpoints.services.datagrid.client.task;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskGridClientInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.datatypes.TaskExecutionControl;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;
import net.fhirfactory.pegacorn.deployment.names.subsystems.SubsystemNames;
import net.fhirfactory.pegacorn.petasos.endpoints.services.datagrid.client.task.common.PetasosTaskGridClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class PetasosTaskGridClientLBService implements PetasosTaskGridClientInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskGridClientLBService.class);

    private static final Long MAXIMUM_ACTIVITY_DURATION = 5000L;

    @Inject
    private PetasosTaskGridClientDuo taskGridClientDuo;

    @Inject
    private PetasosTaskGridClientUno taskGridClientUnu;

    @Inject
    private SubsystemNames subsystemNames;


    //
    // Constructor(s)
    //

    public PetasosTaskGridClientLBService(){
        super();
    }

    //
    // PostConstruct Activities
    //

    //
    // Getters (and Setters)
    //

    protected Logger getLogger() {
        return (LOG);
    }

    protected PetasosTaskGridClientBase getTaskGridClient(){
        if(taskGridClientDuo.isActivityActive()){
            if(taskGridClientUnu.isActivityActive()){
                Long alphaActivityDuration = Instant.now().getEpochSecond() - taskGridClientDuo.getActivityStartTime().getEpochSecond();
                if(alphaActivityDuration > MAXIMUM_ACTIVITY_DURATION){
                    return(taskGridClientDuo);
                } else {
                    return(taskGridClientUnu);
                }
            } else {
                return(taskGridClientUnu);
            }
        } else {
            return (taskGridClientDuo);
        }
    }

    //
    // Task DataGrid Services
    //

    @Override
    public TaskIdType queueTask(PetasosActionableTask actionableTask){
        getLogger().debug(".registerActionableTask(): Entry, task->{}", actionableTask);
        TaskIdType taskIdType = getTaskGridClient().queueTask(actionableTask);
        getLogger().debug(".registerActionableTask(): Exit, taskIdType->{}", taskIdType);
        return(taskIdType);
    }

    @Override
    public PetasosActionableTask registerExternallyTriggeredTask(String participantName, PetasosActionableTask actionableTask) {
        getLogger().debug(".registerExternallyTriggeredTask(): Entry, actionableTask->{}", actionableTask);
        PetasosActionableTask cachedTask = getTaskGridClient().registerExternallyTriggeredTask(participantName, actionableTask);
        getLogger().debug(".registerExternallyTriggeredTask(): Exit, cachedTask->{}", cachedTask);
        return(cachedTask);
    }

    //
    // Update a PetasosActionableTask

    @Override
    public PetasosActionableTask getNextPendingTask(String participantName) {
        getLogger().debug(".getNextPendingTask(): Entry, participantName->{}", participantName);
        PetasosActionableTask nextPendingTask = getTaskGridClient().getNextPendingTask(participantName);
        getLogger().debug(".getNextPendingTask(): Exit, nextPendingTask->{}", nextPendingTask);
        return(nextPendingTask);
    }

    @Override
    public TaskExecutionControl notifyTaskStart(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail) {
        getLogger().debug(".notifyTaskStart(): Entry, taskId->{}", taskId);
        TaskExecutionControl taskExecutionControl = getTaskGridClient().notifyTaskStart(participantName, taskId, taskFulfillmentDetail);
        getLogger().debug(".notifyTaskStart(): Exit, taskExecutionControl->{}", taskExecutionControl);
        return(taskExecutionControl);
    }

    @Override
    public TaskExecutionControl notifyTaskFinish(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        getLogger().debug(".notifyTaskFinish(): Entry, taskId->{}", taskId);
        TaskExecutionControl taskExecutionControl = getTaskGridClient().notifyTaskFinish(participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, taskStatusReason);
        getLogger().debug(".notifyTaskFinish(): Exit, taskExecutionControl->{}", taskExecutionControl);
        return(taskExecutionControl);
    }

    @Override
    public TaskExecutionControl notifyTaskCancellation(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        getLogger().debug(".notifyTaskCancellation(): Entry, taskId->{}", taskId);
        TaskExecutionControl taskExecutionControl = getTaskGridClient().notifyTaskCancellation(participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, taskStatusReason);
        getLogger().debug(".notifyTaskCancellation(): Exit, taskExecutionControl->{}", taskExecutionControl);
        return(taskExecutionControl);
    }

    @Override
    public TaskExecutionControl notifyTaskFailure(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        getLogger().debug(".notifyTaskFailure(): Entry, taskId->{}", taskId);
        TaskExecutionControl taskExecutionControl = getTaskGridClient().notifyTaskFailure(participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, taskStatusReason);
        getLogger().debug(".notifyTaskFailure(): Exit, taskExecutionControl->{}", taskExecutionControl);
        return(taskExecutionControl);
    }

    @Override
    public TaskExecutionControl notifyTaskFinalisation(String participantName, TaskIdType taskId, TaskCompletionSummaryType completionSummary) {
        getLogger().debug(".notifyTaskFinalisation(): Entry, taskId->{}", taskId);
        TaskExecutionControl taskExecutionControl = getTaskGridClient().notifyTaskFinalisation(participantName, taskId, completionSummary);
        getLogger().debug(".notifyTaskFinalisation(): Exit, taskExecutionControl->{}", taskExecutionControl);
        return(taskExecutionControl);

    }
}
