package net.fhirfactory.pegacorn.core.interfaces.tasks;

import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.datatypes.TaskExecutionControl;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;

public interface PetasosTaskGridClientInterface {
    TaskIdType queueTask(PetasosActionableTask actionableTask);

    PetasosActionableTask registerExternallyTriggeredTask(String participantName, PetasosActionableTask actionableTask);

    PetasosActionableTask getNextPendingTask(String participantName);

    TaskExecutionControl notifyTaskStart(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail);

    TaskExecutionControl notifyTaskFinish(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason);

    TaskExecutionControl notifyTaskCancellation(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason);

    TaskExecutionControl notifyTaskFailure(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason);

    TaskExecutionControl notifyTaskFinalisation(String participantName, TaskIdType taskId, TaskCompletionSummaryType completionSummary);
}
