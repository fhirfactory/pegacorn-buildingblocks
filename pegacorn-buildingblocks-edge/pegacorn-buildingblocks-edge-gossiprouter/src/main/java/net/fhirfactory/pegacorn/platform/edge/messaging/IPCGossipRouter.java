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

    GossipRouter ipcGossipRouter;
    GossipRouter oamGossipRouter;
    GossipRouter taskingGossipRouter;
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

        String ipcHostAddress = gossipRouterNode.getPropertyFile().getIpcGossipRouterPort().getHostDNSEntry();
        int ipcPortNumber = gossipRouterNode.getPropertyFile().getIpcGossipRouterPort().getPortValue();
        ipcGossipRouter = new GossipRouter(ipcHostAddress, ipcPortNumber);
        String oamHostAddress = gossipRouterNode.getPropertyFile().getOamGossipRouterPort().getHostDNSEntry();
        int oamPortNumber = gossipRouterNode.getPropertyFile().getOamGossipRouterPort().getPortValue();
        oamGossipRouter = new GossipRouter(oamHostAddress, oamPortNumber);
        String taskingHostAddress = gossipRouterNode.getPropertyFile().getIpcGossipRouterPort().getHostDNSEntry();
        int taskingPortNumber = gossipRouterNode.getPropertyFile().getIpcGossipRouterPort().getPortValue();
        taskingGossipRouter = new GossipRouter(taskingHostAddress, taskingPortNumber);
        try {
            ipcGossipRouter.start();
            oamGossipRouter.start();
            taskingGossipRouter.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getLogger().info("initialiseGossipRouter(): ipcGossipRoute = Address->{}, Port->{}", ipcGossipRouter.bindAddress(), ipcGossipRouter.port());
        getLogger().info("initialiseGossipRouter(): oamGossipRoute = Address->{}, Port->{}", oamGossipRouter.bindAddress(), oamGossipRouter.port());
        getLogger().info("initialiseGossipRouter(): taskingGossipRoute = Address->{}, Port->{}", taskingGossipRouter.bindAddress(), taskingGossipRouter.port());
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
        getLogger().info(".printSomeStatistics(): -----------------------------------------------------------");
        getLogger().info(".printSomeStatistics(): Print Details(" + Date.from(Instant.now()).toString() +")");
        getLogger().info(".printSomeStatistics(): ----------- IPC Gossip Router -----------------------------");
        String addresssMappings = ipcGossipRouter.dumpAddresssMappings();
        String routingTable = ipcGossipRouter.dumpRoutingTable();
        getLogger().info(".printSomeStatistics(): Addressing Mappings ->{}", addresssMappings);
        getLogger().info(".printSomeStatistics(): Routing Table ->{}", routingTable);
        getLogger().info(".printSomeStatistics(): ----------- OAM Gossip Router -----------------------------");
        String oamAddresssMappings = oamGossipRouter.dumpAddresssMappings();
        String oamRoutingTable = oamGossipRouter.dumpRoutingTable();
        getLogger().info(".printSomeStatistics(): Addressing Mappings ->{}", oamAddresssMappings);
        getLogger().info(".printSomeStatistics(): Routing Table ->{}", oamRoutingTable);
        getLogger().info(".printSomeStatistics(): ----------- Tasking Gossip Router ------------------------");
        String taskingAddresssMappings = taskingGossipRouter.dumpAddresssMappings();
        String taskingRoutingTable = taskingGossipRouter.dumpRoutingTable();
        getLogger().info(".printSomeStatistics(): Addressing Mappings ->{}", taskingAddresssMappings);
        getLogger().info(".printSomeStatistics(): Routing Table ->{}", taskingRoutingTable);
    }
}
