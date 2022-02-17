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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.valuesets;

public enum PetasosComponentMetricTypeEnum {
    CACHE_SIZE("CacheSize", "CacheSize", "petasos.component.metric.type.cache-size"),

    INGRES_MESSAGES("IngresMessages", "MessageCount", "petasos.component.metric.type.messages-received"),
    EGRESS_MESSAGES_FORWARD_ATTEMPTS("EgressMessages-Attempts", "MessageCount", "petasos.component.metric.type.message-forward-attempts"),
    EGRESS_MESSAGES_FORWARD_FAILURES("EgressMessages-Failures", "MessageCount", "petasos.component.metric.type.message-forward-failures"),
    EGRESS_MESSAGES_FORWARD_SUCCESSFUL("EgressMessages-Successful", "MessageCount", "petasos.component.metric.type.message-forward-success"),

    INTERNAL_MESSAGE_FORWARDING("Internal-Forwarded-Message", "MessageCount", "petasos.component.metric.type.internal_message_forward"),
    INTERNAL_MESSAGE_RECEPTION("Internal-Received-Message", "MessageCount", "petasos.component.metric.type.internal_message_receive"),

    TASK_PROCESSING_TIME("TaskProcessingTime", "TaskProcessingTime", "petasos.component.metric.type.task-processing-time"),
    ACTIVITY_TIMESTAMP("ActivityTimestamp", "ActivityTimestamp", "petasos.component.metric.type.activity-timestamp"),
    TASK_COUNT("TaskCount", "TaskCount", "petasos.component.metric.type.task-count"),
    AUDIT_EVENT_WRITES("AuditEventWrites", "AuditEventWrites", "petasos.component.metric.type.aduit-event-writes"),
    CONTEXTUAL("ContextualMetric", "ContextualMetric", "petasos.component.metric.type.contextual-metric"),
    COMPONENT_GENERAL_STATUS("ComponentStatus", "ComponentStatus", "petasos.component.metric.type.status-general"),

    INTER_SUBSYSTEM_RPC_REQUEST("RPC-Request", "Transaction", "petasos.component.metric.type.rpc-transaction-request"),
    INTER_SUBSYSTEM_RPC_RESPONSE("RPC-Response", "Transaction", "petasos.component.metric.type.rpc-transaction-response"),
    INTER_SUBSYSTEM_RPC_FAILURE("RPC-Failure", "Transaction", "petasos.component.metric.type.rpc-transaction-failure"),

    REGISTERED_TASK_COUNT("Registered", "TaskCount", "petasos.component.metric.type.finished-task-count"),
    FINISHED_TASK_COUNT("Finished", "TaskCount", "petasos.component.metric.type.finished-task-count"),
    FINALISED_TASK_COUNT("Finalised", "TaskCount", "petasos.component.metric.type.finalised-task-count"),
    STARTED_TASK_COUNT("Started", "TaskCount", "petasos.component.metric.type.started-task-count"),
    CANCELLED_TASK_COUNT("Cancelled", "TaskCount", "petasos.component.metric.type.cancelled-task-count"),
    FAILED_TASK_COUNT("Failed", "TaskCount", "petasos.component.metric.type.failed-task-count"),

    ROLLING_TASK_PROCESSING_TIME("Rolling-Average", "TaskProcessingTime", "petasos.component.metric.type.task-processing-time"),
    CUMULATIVE_TASK_PROCESSING_TIME("Cumulative-Average", "TaskProcessingTime", "petasos.component.metric.type.task-processing-time"),

    LAST_TASK_PROCESSING_TIME("Last-Task", "TaskProcessingTime", "petasos.component.metric.type.task-processing-time"),
    LAST_TASK_ID("Last-Task", "Task-ID", "petasos.component.metric.type.task-id"),
    LAST_TASK_INSTANT("Last-Task-Timestamp", "Date/Time", "petasos.component.metric.type.timestamp");

    private String displayName;
    private String typeName;
    private String token;

    private PetasosComponentMetricTypeEnum(String name, String type, String token){
        this.displayName = name;
        this.token = token;
        this.typeName = type;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getToken(){
        return(this.token);
    }

    public String getTypeName(){
        return(this.typeName);
    }
}
