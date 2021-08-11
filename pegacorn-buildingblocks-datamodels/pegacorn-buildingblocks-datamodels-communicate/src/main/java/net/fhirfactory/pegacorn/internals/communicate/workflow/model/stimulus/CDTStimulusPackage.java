/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus;

import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours.CDTBehaviourIdentifier;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.CDTIdentifier;

public class CDTStimulusPackage {
    private FDNToken stimulusUoWIdentifier;
    private CDTIdentifier targetTwin;
    private CDTBehaviourIdentifier targetBehaviour;
    private CDTStimulus stimulus;


    public CDTStimulusPackage(FDNToken stimulusUoW, CDTIdentifier targetTwin, CDTBehaviourIdentifier targetBehaviour, CDTStimulus stimulus) {
        this.stimulusUoWIdentifier = stimulusUoW;
        this.targetTwin = targetTwin;
        this.targetBehaviour = targetBehaviour;
        this.stimulus = stimulus;
    }

    public CDTStimulusPackage() {
        this.stimulusUoWIdentifier = null;
        this.targetTwin = null;
        this.targetBehaviour = null;
        this.stimulus = null;
    }


    public FDNToken getStimulusUoWIdentifier() {
        return stimulusUoWIdentifier;
    }

    public void setStimulusUoWIdentifier(FDNToken stimulusUoWIdentifier) {
        this.stimulusUoWIdentifier = stimulusUoWIdentifier;
    }

    public CDTIdentifier getTargetTwin() {
        return targetTwin;
    }

    public void setTargetTwin(CDTIdentifier targetTwin) {
        this.targetTwin = targetTwin;
    }

    public CDTBehaviourIdentifier getTargetBehaviour() {
        return targetBehaviour;
    }

    public void setTargetBehaviour(CDTBehaviourIdentifier targetBehaviour) {
        this.targetBehaviour = targetBehaviour;
    }

    public CDTStimulus getStimulus() {
        return stimulus;
    }

    public void setStimulus(CDTStimulus stimulus) {
        this.stimulus = stimulus;
    }
}
