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
package net.fhirfactory.pegacorn.platform.edge.forward.common;

import net.fhirfactory.pegacorn.petasos.wup.archetypes.EdgeEgressMessagingGatewayWUP;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;

import javax.inject.Inject;

public abstract class EdgeJGroupsIPCForwarderWUPTemplate extends EdgeEgressMessagingGatewayWUP {
    
    @Inject
    CamelContext camelCTX;
    
    @Override
    protected void executePostInitialisationActivities(){
        executePostInitialisationActivities(camelCTX);
    }

    public static void executePostInitialisationActivities(CamelContext camelCTX){

    }
    
    @Override
    public void configure() throws Exception {
        getLogger().info("EdgeIPCForwarderWUPTemplate :: WUPIngresPoint/ingresFeed --> {}", this.getIngresTopologyEndpoint().getEndpointSpecification());
        getLogger().info("EdgeIPCForwarderWUPTemplate :: WUPEgressPoint/egressFeed --> {}", this.getEgressTopologyEndpoint().getEndpointSpecification());

        fromWithStandardExceptionHandling(this.getIngresTopologyEndpoint().getEndpointSpecification())
                .routeId(getNameSet().getRouteCoreWUP())
                .log(LoggingLevel.DEBUG, "Raw Content to be Forwarded --> ${body}");
    }

    protected abstract String specifyTargetSubsystem();
    protected abstract String specifyTargetSubsystemVersion();
    protected abstract String specifyTargetEndpointName();
    protected abstract String specifyTargetEndpointVersion();
    protected abstract String specifyTargetService();
    protected abstract String specifyTargetProcessingPlant();


}
