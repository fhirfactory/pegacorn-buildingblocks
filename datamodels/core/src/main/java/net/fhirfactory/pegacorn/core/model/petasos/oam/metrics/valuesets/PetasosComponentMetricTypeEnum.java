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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.valuesets;

public enum PetasosComponentMetricTypeEnum {
    CACHE_SIZE("CacheSize", "petasos.component.metric.type.cache-size"),
    MESSAGES_RECEIVED("MessagesReceived", "petasos.component.metric.type.messages-sent"),
    MESSAGES_FORWARDED("MessagesForwarded", "petasos.component.metric.type.message-received"),
    TASK_PROCESSING_TIME("ActionableTaskProcessingTime", "petasos.component.metric.type.task-processing-time"),
    ACTIVITY_TIMESTAMP("ActivityTimestamp", "petasos.component.metric.type.activity-timestamp"),
    TASKS_FINISHED("TasksFinished", "petasos.component.metric.type.tasks-finished"),
    TASKS_STARTED("TasksStarted", "petasos.component.metric.type.tasks-started"),
    TASKS_WAITING("TasksWaiting", "petasos.component.metric.type.tasks-waiting"),
    TASKS_CANCELLED("TasksCancelled", "petasos.component.metric.type.tasks-cancelled"),
    TASKS_FINALISED("TasksFinalised", "petasos.component.metric.type.tasks-finalised");

    private String displayName;
    private String token;

    private PetasosComponentMetricTypeEnum(String name, String token){
        this.displayName = name;
        this.token = token;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getToken(){
        return(this.token);
    }
}
