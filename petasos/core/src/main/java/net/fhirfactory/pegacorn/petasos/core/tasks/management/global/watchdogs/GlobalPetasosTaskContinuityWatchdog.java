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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.global.watchdogs;

import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedActionableTaskDM;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedTaskJobCardDM;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class GlobalPetasosTaskContinuityWatchdog {

    private Instant actionableTaskCheckInstant;
    private Instant taskJobCardCheckInstant;
    private boolean initialised;

    @Inject
    private SharedActionableTaskDM actionableTaskDM;

    @Inject
    private SharedTaskJobCardDM taskJobCardDM;

    //
    // Constructor(s)
    //

    public GlobalPetasosTaskContinuityWatchdog(){
        this.actionableTaskCheckInstant = Instant.EPOCH;
        this.taskJobCardCheckInstant = Instant.EPOCH;
        this.initialised = false;
    }

    //
    // Post Constrct
    //

    @PostConstruct
    public void initialise(){

    }

    //
    // Scheduling & Initialisation
    //



    //
    // Actionable Task Controller / Watchdog
    //

    protected void actionableTaskWatchdog(){

    }

    //
    // Task Job Card Controller / Watchdog
    //

    protected void taskJobCardWatchdog(){

    }

    //
    // Getters and Setters
    //

    public Instant getActionableTaskCheckInstant() {
        return actionableTaskCheckInstant;
    }

    public void setActionableTaskCheckInstant(Instant actionableTaskCheckInstant) {
        this.actionableTaskCheckInstant = actionableTaskCheckInstant;
    }

    public Instant getTaskJobCardCheckInstant() {
        return taskJobCardCheckInstant;
    }

    public void setTaskJobCardCheckInstant(Instant taskJobCardCheckInstant) {
        this.taskJobCardCheckInstant = taskJobCardCheckInstant;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public SharedActionableTaskDM getActionableTaskDM() {
        return actionableTaskDM;
    }

    public SharedTaskJobCardDM getTaskJobCardDM() {
        return taskJobCardDM;
    }
}
