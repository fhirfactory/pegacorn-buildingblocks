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

import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.SoftwareComponentEndpointSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.WorkUnitProcessorSummary;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.petasos.oam.topology.factories.common.PetasosMonitoredComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class PetasosMonitoredWorkUnitProcessorFactory extends PetasosMonitoredComponentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosMonitoredWorkUnitProcessorFactory.class);

    @Inject
    private PetasosMonitoredEndpointFactory endpointFactory;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public WorkUnitProcessorSummary newWorkUnitProcessor(WorkUnitProcessorSoftwareComponent wupTopologyNode){
        getLogger().debug(".newWorkUnitProcessor(): wupTopologyNode->{}", wupTopologyNode);
        WorkUnitProcessorSummary wup = new WorkUnitProcessorSummary();
        wup = (WorkUnitProcessorSummary) newPetasosMonitoredComponent(wup, wupTopologyNode);
        if(wupTopologyNode.getEgressEndpoint() != null){
            IPCTopologyEndpoint egressEndpoint = wupTopologyNode.getEgressEndpoint();
            SoftwareComponentEndpointSummary egressMonitoredEndpoint = endpointFactory.newEndpoint(egressEndpoint);
            wup.addEndpoint(egressMonitoredEndpoint);

        }
        if(wupTopologyNode.getIngresEndpoint() != null){
            IPCTopologyEndpoint ingresEndpoint = wupTopologyNode.getIngresEndpoint();
            SoftwareComponentEndpointSummary ingresMonitoredEndpoint = endpointFactory.newEndpoint(ingresEndpoint);
            wup.addEndpoint(ingresMonitoredEndpoint);
        }
        getLogger().debug(".newWorkUnitProcessor(): wup->{}", wup);
        return(wup);
    }
}
