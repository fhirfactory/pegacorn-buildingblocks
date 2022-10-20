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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes;

import net.fhirfactory.pegacorn.core.model.capabilities.definition.Capability;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;

import java.io.Serializable;

public class TaskPerformerTypeType implements Serializable {
    private boolean capabilityBased;

    private PetasosParticipantId knownTaskPerformer;
    private Capability requiredPerformerCapability;
    //
    // Constructor(s)
    //

    public TaskPerformerTypeType(){
        super();
        this.knownTaskPerformer = null;
        this.requiredPerformerCapability = null;
        this.capabilityBased = false;
    }

    //
    // Getters and Setters
    //

    public boolean isCapabilityBased() {
        return capabilityBased;
    }

    public void setCapabilityBased(boolean capabilityBased) {
        this.capabilityBased = capabilityBased;
    }

    public PetasosParticipantId getKnownTaskPerformer() {
        return knownTaskPerformer;
    }

    public void setKnownTaskPerformer(PetasosParticipantId knownTaskPerformer) {
        this.knownTaskPerformer = knownTaskPerformer;
    }

    public Capability getRequiredPerformerCapability() {
        return requiredPerformerCapability;
    }

    public void setRequiredPerformerCapability(Capability requiredPerformerCapability) {
        this.requiredPerformerCapability = requiredPerformerCapability;
    }


    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskPerformerTypeType{");
        sb.append("knownTaskPerformer=").append(getKnownTaskPerformer());
        sb.append(", requiredPerformerCapability=").append(getRequiredPerformerCapability());
        sb.append(", capabilityBased").append(isCapabilityBased());
        sb.append('}');
        return sb.toString();
    }
}
