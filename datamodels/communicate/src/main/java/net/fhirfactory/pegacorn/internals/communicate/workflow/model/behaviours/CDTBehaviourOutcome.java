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
package net.fhirfactory.pegacorn.internals.communicate.workflow.model.behaviours;

import net.fhirfactory.pegacorn.internals.communicate.workflow.model.CDTIdentifier;
import net.fhirfactory.pegacorn.internals.communicate.workflow.model.stimulus.CDTStimulusIdentifier;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;

import java.util.Date;

public class CDTBehaviourOutcome {
    private ExtremelySimplifiedResource outputResource;
    private Date creationDate;
    private CDTStimulusIdentifier sourceStimulus;
    private CDTBehaviourOutcomeIdentifier id;
    private CDTBehaviourIdentifier sourceBehaviour;
    private CDTIdentifier affectingTwin;
    private CDTBehaviourOutcomeStatusEnum status;
    private boolean echoedToFHIR;

    public ExtremelySimplifiedResource getOutputResource() {
        return outputResource;
    }

    public void setOutputResource(ExtremelySimplifiedResource outputResource) {
        this.outputResource = outputResource;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public CDTStimulusIdentifier getSourceStimulus() {
        return sourceStimulus;
    }

    public void setSourceStimulus(CDTStimulusIdentifier sourceStimulus) {
        this.sourceStimulus = sourceStimulus;
    }

    public CDTBehaviourOutcomeIdentifier getId() {
        return id;
    }

    public void setId(CDTBehaviourOutcomeIdentifier id) {
        this.id = id;
    }

    public CDTBehaviourIdentifier getSourceBehaviour() {
        return sourceBehaviour;
    }

    public void setSourceBehaviour(CDTBehaviourIdentifier sourceBehaviour) {
        this.sourceBehaviour = sourceBehaviour;
    }

    public CDTIdentifier getAffectingTwin() {
        return affectingTwin;
    }

    public void setAffectingTwin(CDTIdentifier affectingTwin) {
        this.affectingTwin = affectingTwin;
    }

    public CDTBehaviourOutcomeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CDTBehaviourOutcomeStatusEnum status) {
        this.status = status;
    }

    public boolean isEchoedToFHIR() {
        return echoedToFHIR;
    }

    public void setEchoedToFHIR(boolean echoedToFHIR) {
        this.echoedToFHIR = echoedToFHIR;
    }
}
