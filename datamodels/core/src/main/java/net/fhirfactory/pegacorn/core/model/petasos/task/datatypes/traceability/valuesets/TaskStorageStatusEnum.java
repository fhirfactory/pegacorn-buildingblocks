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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.valuesets;

import org.thymeleaf.util.StringUtils;

public enum TaskStorageStatusEnum {
    TASK_SAVED("Saved", "petasos.task.traceability.storage_status.saved", "Task Saved"),
    TASK_HAS_UPDATES("HasUpdates", "petasos.task.traceability.storage_status.has_updates", "Task has Unsaved Updates"),
    TASK_UNSAVED("Unsaved", "petasos.task.traceability.storage_status.unsaved", "No Version of Task is Saved" );

    private String token;
    private String name;
    private String displayName;

    private TaskStorageStatusEnum(String name, String token, String displayName){
        this.name = name;
        this.token = token;
        this.displayName = displayName;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskStorageStatusEnum fromToken(String token){
        if(StringUtils.isEmpty(token)){
            return(null);
        }
        for(TaskStorageStatusEnum currentValue: TaskStorageStatusEnum.values()){
            if(currentValue.getToken().equalsIgnoreCase(token)){
                return(currentValue);
            }
        }
        return(null);
    }

    public static TaskStorageStatusEnum fromName(String name){
        if(StringUtils.isEmpty(name)){
            return(null);
        }
        for(TaskStorageStatusEnum currentValue: TaskStorageStatusEnum.values()){
            if(currentValue.getName().equalsIgnoreCase(name)){
                return(currentValue);
            }
        }
        return(null);
    }
}
