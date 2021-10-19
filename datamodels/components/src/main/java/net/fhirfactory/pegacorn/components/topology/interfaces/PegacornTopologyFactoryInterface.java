/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.components.topology.interfaces;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.*;

public interface PegacornTopologyFactoryInterface {
    public TopologyNodeRDN createNodeRDN(String nodeName, String nodeVersion, ComponentTypeTypeEnum nodeType);

    public WorkshopTopologyNode createWorkshop(String name, String version, ProcessingPlantTopologyNode processingPlant, ComponentTypeTypeEnum nodeType);

    public WorkUnitProcessorTopologyNode createWorkUnitProcessor(String name, String version, WorkshopTopologyNode workshop, ComponentTypeTypeEnum nodeType);

    public WorkUnitProcessorComponentTopologyNode createWorkUnitProcessorComponent(String name, ComponentTypeTypeEnum topologyType, WorkUnitProcessorTopologyNode wup);

    public WorkUnitProcessorInterchangeComponentTopologyNode createWorkUnitProcessingInterchangeComponent(String name, ComponentTypeTypeEnum topologyNodeType, WorkUnitProcessorTopologyNode wup);

    public Boolean getSubsystemInternalTrafficEncrypt();

    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrenceMode();

    public void initialise();
}
