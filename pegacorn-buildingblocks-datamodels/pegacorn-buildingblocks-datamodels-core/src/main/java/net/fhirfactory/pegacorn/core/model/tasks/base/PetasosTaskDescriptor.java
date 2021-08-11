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
package net.fhirfactory.pegacorn.core.model.tasks.base;

import java.io.Serializable;
import java.util.Objects;

public class PetasosTaskDescriptor implements Serializable {
    private PetasosCapabilityDefinition requiredCapability;
    private String taskInstanceID;
    private boolean requiresOrchestration;

    public PetasosTaskDescriptor() {
        this.requiredCapability = requiredCapability;
        this.taskInstanceID = null;
        this.requiresOrchestration = false;
    }

    public String getTaskInstanceID() {
        return taskInstanceID;
    }

    public void setTaskInstanceID(String taskInstanceID) {
        this.taskInstanceID = taskInstanceID;
    }

    public boolean isRequiresOrchestration() {
        return requiresOrchestration;
    }

    public void setRequiresOrchestration(boolean requiresOrchestration) {
        this.requiresOrchestration = requiresOrchestration;
    }

    public PetasosCapabilityDefinition getRequiredCapability() {
        return requiredCapability;
    }

    public void setRequiredCapability(PetasosCapabilityDefinition requiredCapability) {
        this.requiredCapability = requiredCapability;
    }

    @Override
    public String toString() {
        return "TaskDescriptor{" +
                "taskInstanceID='" + taskInstanceID + '\'' +
                ", requiresOrchestration=" + requiresOrchestration +
                ", requiredCapability=" + requiredCapability +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosTaskDescriptor)) return false;
        if (!super.equals(o)) return false;
        PetasosTaskDescriptor that = (PetasosTaskDescriptor) o;
        boolean sameRequiresOrchestration = isRequiresOrchestration() == that.isRequiresOrchestration();
        boolean sameTaskInstanceID = Objects.equals(getTaskInstanceID(), that.getTaskInstanceID());
        boolean sameRequiredCapability = Objects.equals(getRequiredCapability(), that.getRequiredCapability());
        return sameRequiresOrchestration && sameTaskInstanceID && sameRequiredCapability;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTaskInstanceID(), isRequiresOrchestration());
    }
}
