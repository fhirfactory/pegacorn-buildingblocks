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

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.PetasosMonitoredTopologyGraph;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.ProcessingPlantSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PetasosMonitoredTopologyGraphFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosMonitoredTopologyGraphFactory.class);

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosProcessingPlantSummaryFactory processingPlantFactory;

    public PetasosMonitoredTopologyGraph newTopologyGraph(List<SoftwareComponent> nodeList){
        LOG.debug(".newTopologyGraph(): Entry");
        PetasosMonitoredTopologyGraph graph = new PetasosMonitoredTopologyGraph();
        String solutionName = "Unknown";
        LOG.debug(".newTopologyGraph(): Looking for SolutionNode (processingPlant->{})", processingPlant);
        if(processingPlant.getSolutionNode() != null) {
            LOG.debug(".newTopologyGraph(): Looking for ComponentID");
            if(processingPlant.getSolutionNode().getComponentType() != null) {
                LOG.debug(".newTopologyGraph(): Retrieving the ComponentID");
                solutionName = processingPlant.getSolutionNode().getComponentRDN().getNodeName();
            }
        }
        LOG.debug(".newTopologyGraph(): Setting Solution Name");
        graph.setDeploymentName(solutionName);
        LOG.debug(".newTopologyGraph(): Iterating Through nodeList");
        for(SoftwareComponent currentNode: nodeList){
            if(currentNode.getComponentType().equals(ComponentTypeTypeEnum.PROCESSING_PLANT)){
                ProcessingPlantSoftwareComponent currentProcessingPlantSoftwareComponent = (ProcessingPlantSoftwareComponent)currentNode;
                ProcessingPlantSummary processingPlant = processingPlantFactory.newProcessingPlant(currentProcessingPlantSoftwareComponent);
                graph.addProcessingPlant(processingPlant);
            }
        }
        LOG.debug(".newTopologyGraph(): Exit");
        return(graph);
    }
}
