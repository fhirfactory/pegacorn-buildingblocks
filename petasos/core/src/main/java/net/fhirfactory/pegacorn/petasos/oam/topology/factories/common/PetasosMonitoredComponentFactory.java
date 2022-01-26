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
package net.fhirfactory.pegacorn.petasos.oam.topology.factories.common;

import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.ProcessingPlantPetasosParticipantNameHolder;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.SoftwareComponentSummary;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;

public abstract class PetasosMonitoredComponentFactory {

    abstract protected Logger getLogger();

    @Inject
    private ProcessingPlantPetasosParticipantNameHolder participantNameHolder;

    protected SoftwareComponentSummary newPetasosMonitoredComponent(SoftwareComponentSummary monitoredNode, SoftwareComponent topologyNode){
        getLogger().debug(".newITOpsMonitoredNode(): Entry, monitoredNode->{}, topologyNode->{}", monitoredNode, topologyNode);
        monitoredNode.setComponentID(topologyNode.getComponentID());
        monitoredNode.setTopologyNodeFunctionFDN(topologyNode.getNodeFunctionFDN());
        monitoredNode.setTopologyNodeFDN(topologyNode.getComponentFDN());
        monitoredNode.setSubsystemParticipantName(participantNameHolder.getSubsystemParticipantName());
        if(StringUtils.isEmpty(topologyNode.getParticipantName())){
            monitoredNode.setParticipantName(topologyNode.getComponentID().getDisplayName());
        } else {
            monitoredNode.setParticipantName(topologyNode.getParticipantName());
        }
        if(StringUtils.isEmpty(topologyNode.getParticipantDisplayName())){
            monitoredNode.setParticipantDisplayName(monitoredNode.getParticipantName());
        } else {
            monitoredNode.setParticipantDisplayName(topologyNode.getParticipantDisplayName());
        }
        PetasosMonitoredComponentTypeEnum nodeTypeEnum = PetasosMonitoredComponentTypeEnum.nodeTypeFromTopologyNodeType(topologyNode.getComponentType());
        monitoredNode.setNodeType(nodeTypeEnum);
        monitoredNode.setNodeVersion(topologyNode.getComponentRDN().getNodeVersion());
        if(topologyNode.getConcurrencyMode() != null) {
            monitoredNode.setConcurrencyMode(topologyNode.getConcurrencyMode().getDisplayName());
        }
        if(topologyNode.getResilienceMode() != null) {
            monitoredNode.setResilienceMode(topologyNode.getResilienceMode().getDisplayName());
        }
        getLogger().debug(".newITOpsMonitoredNode(): Exit, monitoredNode->{}", monitoredNode);
        return(monitoredNode);
    }
}
