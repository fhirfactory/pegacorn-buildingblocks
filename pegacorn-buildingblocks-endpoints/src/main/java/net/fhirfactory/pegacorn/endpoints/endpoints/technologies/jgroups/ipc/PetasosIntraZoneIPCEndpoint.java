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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.ipc;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroupsOLD.base.JGroupsIPCPetasosInterface;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.IntraZoneEdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.ExchangePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosIntraZoneIPCEndpoint extends JGroupsPetasosEndpointBase {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosIntraZoneIPCEndpoint.class);

    private static String STACK_INITIAL_HOSTS = "hosts";

    @Inject
    private IntraZoneEdgeForwarderService forwarderService;

    public PetasosIntraZoneIPCEndpoint(){
        super();
    }

    @Override
    protected String specifyForwarderWUPName() {
        return ("EdgeIntraZoneMessageForwardWUP");
    }

    @Override
    protected String specifyForwarderWUPVersion() {
        return ("1.0.0");
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyGroupName() {
        return (getIPCComponentNames().getIntraZoneIPCGroupName());
    }

    @Override
    protected String specifyFileName() {
        return ("privnet.xml");
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getFunctionNameIntraZoneJGroupsIPC());
    }

    @Override
    protected InterProcessingPlantHandoverResponsePacket injectMessageIntoRoute(InterProcessingPlantHandoverPacket handoverPacket) {
        InterProcessingPlantHandoverResponsePacket response = (InterProcessingPlantHandoverResponsePacket)getCamelProducer().sendBody(getIPCComponentNames().getIntraZoneIPCReceiverRouteEndpointName(), ExchangePattern.InOut, handoverPacket);
        return(response);
    }

    @Override
    protected TopologyEndpointTypeEnum specifyIPCType() {
        return (TopologyEndpointTypeEnum.JGROUPS_INTRAZONE_IPC_SERVICE);
    }

    @Override
    protected EdgeForwarderService specifyForwarderService() {
        return (forwarderService);
    }
}
