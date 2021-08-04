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
package net.fhirfactory.pegacorn.platform.edge.messaging;

import net.fhirfactory.pegacorn.platform.edge.itops.PetasosITOpsService;
import net.fhirfactory.pegacorn.platform.edge.itops.configuration.JGroupsGossipRouterNode;
import org.jgroups.stack.GossipRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.time.Instant;

public class IPCGossipRouter{
    private static final Logger LOG = LoggerFactory.getLogger(IPCGossipRouter.class);

    GossipRouter gossipRouter;
    PetasosITOpsService petasosServices;
    JGroupsGossipRouterNode gossipRouterNode;

    protected Logger getLogger(){
        return(LOG);
    }

    public void run(){
        getLogger().debug("IPCGossipRouter::main(): Gossip Router");
        gossipRouterNode = new JGroupsGossipRouterNode();
        petasosServices = new PetasosITOpsService(gossipRouterNode);
        petasosServices.start();
        initialiseGossipRouter();
        eventLoop();
    }

    private void initialiseGossipRouter(){

        String ipAddress = gossipRouterNode.getPropertyFile().getGossipRouterPort().getHostDNSEntry();
        int portNumber = gossipRouterNode.getPropertyFile().getGossipRouterPort().getPortValue();
        gossipRouter = new GossipRouter(ipAddress, portNumber);
        try {
            gossipRouter.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getLogger().info("initialiseGossipRouter(): Bound To = Address->{}, Port->{}",gossipRouter.bindAddress(), gossipRouter.port());
    }

    private void eventLoop(){
        while(true) {
            try {
                Thread.sleep(10000);
                petasosServices.updateDate();
                printSomeStatistics();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printSomeStatistics(){
        getLogger().info(".printSomeStatistics(): Print Details(" + Date.from(Instant.now()).toString() +")");
        String addresssMappings = gossipRouter.dumpAddresssMappings();
        String routingTable = gossipRouter.dumpRoutingTable();
        getLogger().info(".printSomeStatistics(): Addressing Mappings ->{}", addresssMappings);
        getLogger().info(".printSomeStatistics(): Routing Table ->{}", routingTable);
    }
}
