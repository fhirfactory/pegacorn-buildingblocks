package net.fhirfactory.pegacorn.core.model.petasos.sost;

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
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;

import java.util.ArrayList;
import java.util.List;

public class SystemOfSystemsTransaction {
    private SystemOfSystemsTransactionIdentifier transactionIdentifier;
    private List<SystemOfSystemsTriggerEvent> triggerSet;
    private List<SystemOfSystemsOutcomeEvent> outcomeSet;
    private List<PetasosActionableTask> constinuateTasks;

    //
    // Constructor(s)
    //

    public SystemOfSystemsTransaction(){
        this.transactionIdentifier = null;
        this.triggerSet = new ArrayList<>();
        this.outcomeSet = new ArrayList<>();
        this.constinuateTasks = new ArrayList<>();
    }

    //
    // Getters and Setters
    //

    public SystemOfSystemsTransactionIdentifier getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public void setTransactionIdentifier(SystemOfSystemsTransactionIdentifier transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    public List<SystemOfSystemsTriggerEvent> getTriggerSet() {
        return triggerSet;
    }

    public void setTriggerSet(List<SystemOfSystemsTriggerEvent> triggerSet) {
        this.triggerSet = triggerSet;
    }

    public List<SystemOfSystemsOutcomeEvent> getOutcomeSet() {
        return outcomeSet;
    }

    public void setOutcomeSet(List<SystemOfSystemsOutcomeEvent> outcomeSet) {
        this.outcomeSet = outcomeSet;
    }

    public List<PetasosActionableTask> getConstinuateTasks() {
        return constinuateTasks;
    }

    public void setConstinuateTasks(List<PetasosActionableTask> constinuateTasks) {
        this.constinuateTasks = constinuateTasks;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "SystemOfSystemsTransaction{" +
                "transactionIdentifier=" + transactionIdentifier +
                ", triggerSet=" + triggerSet +
                ", outcomeSet=" + outcomeSet +
                ", constinuateTasks=" + constinuateTasks +
                '}';
    }
}
