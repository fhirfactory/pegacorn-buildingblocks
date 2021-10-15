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
package net.fhirfactory.pegacorn.platform.edge.messaging.receive;

import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.platform.edge.messaging.receive.common.EdgeMessageReceiveWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EdgeInterZoneMessageReceiveWUP extends EdgeMessageReceiveWUP {
    private static final Logger LOG = LoggerFactory.getLogger(EdgeInterZoneMessageReceiveWUP.class);

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWUPInstanceName() {
        return ("EdgeInterZoneIPCReceiveWUP");
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return ("1.0.0");
    }

    @Override
    protected String specifyIPCZoneType() {
        return (getIPCComponentNames().getInterZoneIPCGroupName());
    }

    @Override
    protected MessageBasedWUPEndpoint specifyIngresEndpoint() {
        MessageBasedWUPEndpoint ingresEndpoint = new MessageBasedWUPEndpoint();
        assignIngresTopologyEndpoint();
        ingresEndpoint.setEndpointTopologyNode(getAssociatedIngresTopologyEndpoint());
        ingresEndpoint.setEndpointSpecification(getIPCComponentNames().getInterZoneIPCReceiverRouteEndpointName());
        ingresEndpoint.setFrameworkEnabled(false);
        return(ingresEndpoint);
    }

    @Override
    protected String specifyIngresInterfaceName() {
        return (getInterfaceNames().getFunctionNameInterZoneJGroupsIPC());
    }

    @Override
    protected IPCInterfaceDefinition specifyIngresInterfaceDefinition() {
        IPCInterfaceDefinition interfaceDefinition = new IPCInterfaceDefinition();
        interfaceDefinition.setInterfaceFormalName("JGroups-Gossip");
        interfaceDefinition.setInterfaceFormalVersion("1.0.0");
        return (interfaceDefinition);
    }
}
