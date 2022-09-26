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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import net.fhirfactory.pegacorn.core.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.oam.topology.PetasosMonitoredTopologyReportingAgent;

public abstract class Workshop extends RouteBuilder implements WorkshopInterface {

    private WorkshopSoftwareComponent workshopNode;
    private boolean isInitialised;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosMonitoredTopologyReportingAgent itopsCollectionAgent;

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
    abstract protected SoftwareComponentTypeEnum specifyWorkshopType();
    abstract protected void invokePostConstructInitialisation();

    protected PegacornTopologyFactoryInterface getTopologyFactory(){
        return(processingPlant.getTopologyFactory());
    }

    @Override
    public WorkshopSoftwareComponent getWorkshopNode(){
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
        WorkshopSoftwareComponent workshop = getTopologyFactory().buildWorkshop(specifyWorkshopName(), specifyWorkshopVersion(), getProcessingPlant().getMeAsASoftwareComponent(),specifyWorkshopType());
        String workshopParticipantName = getProcessingPlant().getMeAsASoftwareComponent().getParticipantId().getSubsystemName() + "." + specifyWorkshopName();
        workshop.setParticipantName(workshopParticipantName);
        topologyIM.addTopologyNode(getProcessingPlant().getMeAsASoftwareComponent().getComponentID(), workshop);
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
        String nodeName = getWorkshopNode().getComponentID().getDisplayName();
        return(nodeName);
    }

    @Override
    public WorkUnitProcessorSoftwareComponent getWUP(String wupName, String wupVersion) {
        getLogger().debug(".getWUP(): Entry, wupName --> {}, wupVersion --> {}", wupName, wupVersion);
        if(StringUtils.isEmpty(wupName)){
            return(null);
        }
        boolean found = false;
        WorkUnitProcessorSoftwareComponent foundWUP = null;
        for (ComponentIdType containedWUPId: this.workshopNode.getWupSet()) {
            boolean wupNameEqual = false;
            boolean wupDisplayNameEqual = false;
            boolean versionEqual = true;
            if(containedWUPId.hasName()) {
                wupNameEqual = containedWUPId.getName().contentEquals(wupName);
            }
            if(containedWUPId.hasDisplayName()){
                wupDisplayNameEqual = containedWUPId.getDisplayName().contentEquals(wupName);
            }
            if (wupNameEqual || wupDisplayNameEqual) {
                foundWUP = (WorkUnitProcessorSoftwareComponent)topologyIM.getNode(containedWUPId);
                if(StringUtils.isNotEmpty(wupVersion)){
                    versionEqual = foundWUP.getVersion().contentEquals(wupVersion);
                }
                if(versionEqual){
                    break;
                } else {
                    foundWUP = null;
                }
            }
        }
        getLogger().debug(".getWUP(): Exit, foundWUP->{}", foundWUP);
        return (foundWUP);
    }

    @Override
	public WorkUnitProcessorSoftwareComponent getWUP(String workshopName){
        getLogger().debug(".getWorkshop(): Entry, workshopName --> {}", workshopName);
        String version = this.workshopNode.getVersion();
        WorkUnitProcessorSoftwareComponent workshop = getWUP(workshopName, version);
        return(workshop);
    }
}
