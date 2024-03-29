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
package net.fhirfactory.pegacorn.services.tasks.agent;

import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.services.tasks.distribution.PetasosTasksDistributionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PetasosTasksAgentHandler extends PetasosTasksDistributionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTasksAgentHandler.class);

    @Override
    public PetasosActionableTask registerActionableTask(PetasosActionableTask actionableTask, JGroupsIntegrationPointSummary requesterEndpointIdentifier) {
        getLogger().debug(".registerActionableTask(): Entry (Agent), actionableTask->{}, requesterEndpointIdentifier->{}", actionableTask, requesterEndpointIdentifier);
        return null;
    }

    @Override
    public PetasosActionableTask fulfillActionableTask(PetasosActionableTask actionableTask, JGroupsIntegrationPointSummary requesterEndpointIdentifier) {
        getLogger().debug(".fulfillActionableTask(): Entry (Agent), actionableTask->{}, requesterEndpointIdentifier->{}", actionableTask, requesterEndpointIdentifier);
        return null;
    }

    @Override
    public PetasosActionableTask updateActionableTask(PetasosActionableTask actionableTask, JGroupsIntegrationPointSummary requesterEndpointIdentifier) {
        getLogger().debug(".updateActionableTask(): Entry (Agent), actionableTask->{}, requesterEndpointIdentifier->{}", actionableTask, requesterEndpointIdentifier);
        return null;
    }

    @Override
    public List<PetasosActionableTask> retrievePendingActionableTasks(JGroupsIntegrationPointSummary requestorEndpointIdentifier) {
        getLogger().debug(".retrievePendingActionableTasks(): Entry (Agent), requesterEndpointIdentifier->{}", requestorEndpointIdentifier);
        return null;
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

}
