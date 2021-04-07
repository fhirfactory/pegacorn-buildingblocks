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
package net.fhirfactory.pegacorn.platform.oam.topologysync;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.components.model.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.names.common.IPCGroupNames;
import net.fhirfactory.pegacorn.deployment.properties.environmentlookupbased.PlatformEnvironmentLookup;
import net.fhirfactory.pegacorn.platform.ipc.frameworks.jgroups.JGroupsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TopologySynchronisationEndpoint extends JGroupsEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(TopologySynchronisationEndpoint.class);

    @Inject
    private IPCGroupNames ipcGroupNames;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PlatformEnvironmentLookup platformEnvironmentLookup;

    public TopologySynchronisationEndpoint(){
        super();
    }

    @Override
    protected String specifyChannelName() {
        TopologyNodeRDN processingPlantRDN = processingPlant.getProcessingPlantNode().getNodeRDN();
        String platformName = platformEnvironmentLookup.getPlatformName(processingPlant.getProcessingPlantNode().isKubernetesDeployed());
        String channelName = "OAM-TopologySynchronisation-"+platformName;
        return (channelName);
    }

    @Override
    protected String specifyGroupName() {
        return (ipcGroupNames.getTopologySynchronisationGroup());
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

}