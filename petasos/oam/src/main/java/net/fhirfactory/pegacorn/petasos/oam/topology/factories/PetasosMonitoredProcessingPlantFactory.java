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
package net.fhirfactory.pegacorn.petasos.oam.topology.factories;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.PetasosMonitoredProcessingPlant;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.PetasosMonitoredWorkshop;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.oam.topology.factories.common.PetasosMonitoredComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosMonitoredProcessingPlantFactory extends PetasosMonitoredComponentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosMonitoredProcessingPlantFactory.class);

    @Inject
    private PetasosMonitoredWorkshopFactory workshopFactory;

    @Inject
    private TopologyIM topologyIM;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public PetasosMonitoredProcessingPlant newProcessingPlant(ProcessingPlantTopologyNode topologyNode){
        LOG.debug(".newProcessingPlant(): Entry, topologyNode->{}", topologyNode);
        PetasosMonitoredProcessingPlant processingPlant = new PetasosMonitoredProcessingPlant();
        processingPlant = (PetasosMonitoredProcessingPlant) newPetasosMonitoredComponent(processingPlant, topologyNode);
        processingPlant.setSecurityZone(topologyNode.getSecurityZone().getNetworkSecurityZone());
        processingPlant.setActualHostIP(topologyNode.getActualHostIP());
        processingPlant.setActualPodIP(topologyNode.getActualPodIP());
        TopologyNodeFDN nodeFDN = topologyNode.getComponentFDN();
        processingPlant.setTopologyNodeFDN(nodeFDN);
        String platformNodeName = "Unknown";
        for(TopologyNodeRDN currentRDN: nodeFDN.getHierarchicalNameSet()){
            if(currentRDN.getNodeType().equals(ComponentTypeTypeEnum.PLATFORM)){
                platformNodeName = currentRDN.getNodeName();
                break;
            }
        }
        String siteName = "Unknown";
        for(TopologyNodeRDN currentRDN: nodeFDN.getHierarchicalNameSet()){
            if(currentRDN.getNodeType().equals(ComponentTypeTypeEnum.SITE)){
                siteName = currentRDN.getNodeName();
                break;
            }
        }
        processingPlant.setSite(siteName);
        processingPlant.setPlatformID(platformNodeName);
        for(TopologyNodeFDN currentWorkshopFDN: topologyNode.getWorkshops()){
            WorkshopTopologyNode workshopTopologyNode = (WorkshopTopologyNode) topologyIM.getNode(currentWorkshopFDN);
            PetasosMonitoredWorkshop currentWorkshop = workshopFactory.newWorkshop(workshopTopologyNode);
            processingPlant.addWorkshop(currentWorkshop);
        }
        LOG.debug(".newProcessingPlant(): Exit, processingPlant->{}", processingPlant);
        return(processingPlant);
    }
}
