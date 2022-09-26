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
package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.*;

public interface PegacornTopologyFactoryInterface {

    public WorkshopSoftwareComponent buildWorkshop(String name, String version, ProcessingPlantSoftwareComponent processingPlant, SoftwareComponentTypeEnum nodeType);

    public WorkUnitProcessorSoftwareComponent buildWUP(String name, String version, String participantName, WorkshopSoftwareComponent workshop, SoftwareComponentTypeEnum nodeType);

    public WorkUnitProcessorSubComponentSoftwareComponent buildWUPComponent(String name, SoftwareComponentTypeEnum topologyType, WorkUnitProcessorSoftwareComponent wup);

    public WorkUnitProcessorInterchangeSoftwareComponent buildWUPInterchange(String name, SoftwareComponentTypeEnum topologyNodeType, WorkUnitProcessorSoftwareComponent wup);

    public Boolean getSubsystemInternalTrafficEncrypt();

    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrenceMode();

    public void initialise();
}
