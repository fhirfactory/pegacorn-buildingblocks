/*
 * Copyright (c) 2021 Mark A. Hunter
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
 */package net.fhirfactory.pegacorn.service.oam.model.factories;

import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopSoftwareComponent;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import org.hl7.fhir.r4.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PetasosSoftwareComponentToFHIRDeviceTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosSoftwareComponentToFHIRDeviceTransformer.class);

    //
    // Business Methods
    //

    public Device newDevice(SoftwareComponent softwareComponent){

        Device fhirDevice = new Device();
        return(fhirDevice);
    }

    public Device newDevice(ProcessingPlantSoftwareComponent processingPlant){



        Device fhirDevice = new Device();
        return(fhirDevice);
    }

    public Device newDevice(WorkshopSoftwareComponent workshop){

        Device fhirDevice = new Device();
        return(fhirDevice);
    }

    public Device newDevice(WorkUnitProcessorSoftwareComponent wupNode){

        Device fhirDevice = new Device();
        return(fhirDevice);
    }

    public Device newDevice(IPCTopologyEndpoint topologyEndpoint){

        Device fhirDevice = new Device();
        return(fhirDevice);
    }

    public Device newDevice(IPCAdapter ipcAdapter){

        Device fhirDevice = new Device();
        return(fhirDevice);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
