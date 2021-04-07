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
package net.fhirfactory.pegacorn.processingplatform;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.model.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.ClusterServiceDeliverySubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.topology.builders.PegacornSolutionBuilderInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.map.common.archetypes.common.PetasosEnabledSubsystemTopologyFactory;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class ProcessingPlant extends RouteBuilder implements ProcessingPlantInterface {

    private ProcessingPlantTopologyNode processingPlantTopologyNode;
    private boolean isInitialised;

    @Inject
    private TopologyIM topologyIM;

//    @Inject
//    private PegacornSolutionBuilderInterface topologyBuilder;

//    abstract protected LadonPropertyNamesBase specifyBaseLadonNames();

    abstract protected ClusterServiceDeliverySubsystemPropertyFile specifyPropertyFile();
    abstract protected PetasosEnabledSubsystemTopologyFactory specifyTopologyFactory();
    abstract protected Logger specifyLogger();

    protected Logger getLogger(){return(specifyLogger());}
    protected ClusterServiceDeliverySubsystemPropertyFile getPropertyFile(){return(specifyPropertyFile());}
    @Override
    public PetasosEnabledSubsystemTopologyFactory getTopologyFactory(){return(specifyTopologyFactory());}

    public ProcessingPlant() {
        super();
        this.isInitialised = false;
    }

    @PostConstruct
    private void initialise() {
        if (!isInitialised) {
            getLogger().debug("StandardProcessingPlatform::initialise(): Invoked!");
//            topologyBuilder.initialiseSubsystemTopology();
            getLogger().trace("StandardProcessingPlatform::initialise(): Building Workshops");
            isInitialised = true;
        }
    }

    @Override
    public void initialisePlant() {
        initialise();
    }


    public TopologyIM getTopologyIM() {
        return (topologyIM);
    }

//    public PegacornSolutionBuilderInterface getTopologyBuilder() {
//        return (this.topologyBuilder);
//    }

//    public LadonPropertyNamesBase getBaseLadonNames() {
//        return (specifyBaseLadonNames());
//    }

    public TopologyNodeFunctionFDN getNodeToken() {
        return (this.processingPlantTopologyNode.getNodeFunctionFDN());
    }

    @Override
    public void configure() throws Exception {
        String processingPlantName = getFriendlyName();

        from("timer://"+processingPlantName+"?delay=1000&repeatCount=1")
            .routeId("ProcessingPlant::"+processingPlantName)
            .log(LoggingLevel.INFO, "Starting....");
    }

    private String getFriendlyName(){
        String nodeName = getProcessingPlantNode().getNodeRDN().getNodeName() + "(" + getProcessingPlantNode().getNodeRDN().getNodeVersion() + ")";
        return(nodeName);
    }

    @Override
    public ProcessingPlantTopologyNode getProcessingPlantNode() {
        return (this.processingPlantTopologyNode);
    }

    public TopologyNodeFDN getProcessingPlantNodeFDN() {
        return (this.processingPlantTopologyNode.getNodeFDN());
    }

    @Override
    public WorkshopTopologyNode getWorkshop(String workshopName, String version) {
        getLogger().info(".getWorkshop(): Entry, workshopName --> {}, version --> {}", workshopName, version);
        boolean found = false;
        WorkshopTopologyNode foundWorkshop = null;
        for (WorkshopTopologyNode containedWorkshop : this.processingPlantTopologyNode.getWorkshops().values()) {
            TopologyNodeRDN testRDN = new TopologyNodeRDN(TopologyNodeTypeEnum.WORKSHOP, workshopName, version);
            if (testRDN.equals(containedWorkshop.getNodeRDN())) {
                found = true;
                foundWorkshop = containedWorkshop;
                break;
            }
        }
        if (found) {
            return (foundWorkshop);
        }
        return (null);
    }

    public WorkshopTopologyNode getWorkshop(String workshopName){
        getLogger().info(".getWorkshop(): Entry, workshopName --> {}", workshopName);
        String version = this.processingPlantTopologyNode.getNodeRDN().getNodeVersion();
        WorkshopTopologyNode workshop = getWorkshop(workshopName, version);
        return(workshop);
    }

}
