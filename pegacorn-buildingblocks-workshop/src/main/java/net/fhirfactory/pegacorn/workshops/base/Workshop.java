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
package net.fhirfactory.pegacorn.workshops.base;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class Workshop extends RouteBuilder implements WorkshopInterface {

    private WorkshopTopologyNode workshopNode;
    private boolean isInitialised;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private ProcessingPlantInterface processingPlant;

    public Workshop() {
        super();
        this.isInitialised = false;
    }

    protected abstract Logger specifyLogger();
    protected Logger getLogger() {return(specifyLogger());}

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    abstract protected String specifyWorkshopName();
    abstract protected String specifyWorkshopVersion();
    abstract protected TopologyNodeTypeEnum specifyWorkshopType();
    abstract protected void invokePostConstructInitialisation();

    protected PegacornTopologyFactoryInterface getTopologyFactory(){
        return(processingPlant.getTopologyFactory());
    }

    @Override
    public WorkshopTopologyNode getWorkshopNode(){
        return(workshopNode);
    }

    @PostConstruct
    private void initialise() {
        if (!isInitialised) {
            getLogger().debug("StandardWorkshop::initialise(): Invoked!");
            processingPlant.initialisePlant();
            buildWorkshop();
            invokePostConstructInitialisation();
            getLogger().trace("StandardWorkshop::initialise(): Node --> {}", getWorkshopNode());
            isInitialised = true;
        }
    }

    @Override
    public void initialiseWorkshop(){
        initialise();
    }

    private void buildWorkshop() {
        getLogger().debug(".buildWorkshop(): Entry, adding Workshop --> {}, version --> {}", specifyWorkshopName(), specifyWorkshopVersion());
        WorkshopTopologyNode workshop = getTopologyFactory().createWorkshop(specifyWorkshopName(), specifyWorkshopVersion(), getProcessingPlant().getProcessingPlantNode(),specifyWorkshopType());
        topologyIM.addTopologyNode(getProcessingPlant().getProcessingPlantNode().getNodeFDN(), workshop);
        this.workshopNode = workshop;
        getLogger().debug(".buildWorkshop(): Exit");
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    public void setInitialised(boolean initialised) {
        isInitialised = initialised;
    }

    @Override
    public void configure() throws Exception {
        String fromString = "timer://" +getFriendlyName() + "-ingres" + "?repeatCount=1";

        from(fromString)
            .log(LoggingLevel.DEBUG, "InteractWorkshop --> ${body}");
    }

    private String getFriendlyName(){
        String nodeName = getWorkshopNode().getNodeRDN().getNodeName() + "(" + getWorkshopNode().getNodeRDN().getNodeVersion() + ")";
        return(nodeName);
    }

    @Override
    public WorkUnitProcessorTopologyNode getWUP(String wupName, String wupVersion) {
        getLogger().debug(".getWUP(): Entry, wupName --> {}, wupVersion --> {}", wupName, wupVersion);
        boolean found = false;
        WorkUnitProcessorTopologyNode foundWorkshop = null;
        for (TopologyNodeFDN containedWorkshopFDN : this.workshopNode.getWupSet()) {
            WorkUnitProcessorTopologyNode containedWorkshop = (WorkUnitProcessorTopologyNode)topologyIM.getNode(containedWorkshopFDN);
            TopologyNodeRDN testRDN = new TopologyNodeRDN(TopologyNodeTypeEnum.WORKSHOP, wupName, wupVersion);
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

    public WorkUnitProcessorTopologyNode getWUP(String workshopName){
        getLogger().debug(".getWorkshop(): Entry, workshopName --> {}", workshopName);
        String version = this.workshopNode.getNodeRDN().getNodeVersion();
        WorkUnitProcessorTopologyNode workshop = getWUP(workshopName, version);
        return(workshop);
    }
}
