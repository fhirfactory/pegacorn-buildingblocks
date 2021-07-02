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
package net.fhirfactory.pegacorn.oamwup.base;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.workshops.base.OAMWorkshop;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class OAMWorkUnitProcessor extends RouteBuilder{

    private WorkUnitProcessorTopologyNode wupTopologyNode;
    private boolean isInitialised;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private ProcessingPlantInterface processingPlant;

    public OAMWorkUnitProcessor() {
        super();
        this.isInitialised = false;
    }

    public WorkUnitProcessorTopologyNode getWUPTopologyNode(){
        return(this.wupTopologyNode);
    }

    protected abstract Logger specifyLogger();
    protected Logger getLogger() {return(specifyLogger());}

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    abstract protected String specifyOAMWUPName();
    abstract protected String specifyOAMWUPVersion();
    abstract protected OAMWorkshop specifyOAMWorkshop();

    public PegacornTopologyFactoryInterface getTopologyFactory(){
        return(processingPlant.getTopologyFactory());
    }

    public OAMWorkshop getWorkshop(){
        return(specifyOAMWorkshop());
    }

    abstract protected void invokePostConstructInitialisation();

    @PostConstruct
    private void initialise() {
        if (!isInitialised) {
            getLogger().debug("StandardWorkshop::initialise(): Invoked!");
            processingPlant.initialisePlant();
            buildOAMWorkUnitProcessor();
            invokePostConstructInitialisation();
            getLogger().trace("StandardWorkshop::initialise(): Node --> {}", getWUPTopologyNode());
            isInitialised = true;
        }
    }

    public void initialiseOAMWorkUnitProcessor(){
        initialise();
    }

    private void buildOAMWorkUnitProcessor() {
        getLogger().debug(".buildOAMWorkUnitProcessor(): Entry, adding Workshop --> {}, version --> {}", specifyOAMWUPName(), specifyOAMWUPVersion());
        WorkUnitProcessorTopologyNode wup = getTopologyFactory().createWorkUnitProcessor(specifyOAMWUPName(), specifyOAMWUPVersion(), specifyOAMWorkshop().getWorkshopNode(), TopologyNodeTypeEnum.OAM_WORK_UNIT_PROCESSOR);
        topologyIM.addTopologyNode(specifyOAMWorkshop().getWorkshopNode().getNodeFDN(), wup);
        this.wupTopologyNode = wup;
        getLogger().debug(".buildOAMWorkUnitProcessor(): Exit");
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    public void setInitialised(boolean initialised) {
        isInitialised = initialised;
    }

    private String getFriendlyName(){
        String nodeName = getWUPTopologyNode().getNodeRDN().getNodeName() + "(" + getWUPTopologyNode().getNodeRDN().getNodeVersion() + ")";
        return(nodeName);
    }
}
