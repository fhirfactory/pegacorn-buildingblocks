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

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;

public class CDTStimulusReason {
    private TopologyNodeFDN pegacornEntryPoint;
    private ExtremelySimplifiedResource why;
    private CDTStimulusReasonTypeEnum reasonType;
    private String originalSource;
    private String originalDestination;

    public CDTStimulusReason(CDTStimulusReasonTypeEnum reasonType, ExtremelySimplifiedResource why) {
        this.why = why;
        this.reasonType = reasonType;
        this.pegacornEntryPoint = null;
        this.originalDestination = null;
        this.originalDestination = null;
    }

    public CDTStimulusReason(CDTStimulusReason ori) {
        this.pegacornEntryPoint = ori.getPegacornEntryPoint();
        this.why = ori.getWhy();
        this.reasonType = ori.getReasonType();
        this.originalDestination = ori.getOriginalDestination();
        this.originalSource = ori.getOriginalSource();
    }

    public TopologyNodeFDN getPegacornEntryPoint() {
        return pegacornEntryPoint;
    }

    public void setPegacornEntryPoint(TopologyNodeFDN pegacornEntryPoint) {
        this.pegacornEntryPoint = pegacornEntryPoint;
    }

    public ExtremelySimplifiedResource getWhy() {
        return why;
    }

    public void setWhy(ExtremelySimplifiedResource why) {
        this.why = why;
    }

    public CDTStimulusReasonTypeEnum getReasonType() {
        return reasonType;
    }

    public void setReasonType(CDTStimulusReasonTypeEnum reasonType) {
        this.reasonType = reasonType;
    }

    public String getOriginalSource() {
        return originalSource;
    }

    public void setOriginalSource(String originalSource) {
        this.originalSource = originalSource;
    }

    public String getOriginalDestination() {
        return originalDestination;
    }

    public void setOriginalDestination(String originalDestination) {
        this.originalDestination = originalDestination;
    }
}
