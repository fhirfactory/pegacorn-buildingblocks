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
 */
package net.fhirfactory.pegacorn.petasos.endpoints;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.tasks.PetasosInterZoneTaskEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.tasks.PetasosIntraZoneTaskEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InterProcessingPlantTaskServicesBroker implements PetasosTaskBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantTaskServicesBroker.class);

    @Inject
    private PetasosIntraZoneTaskEndpoint intraZoneTaskingEndpoint;

    @Inject
    private PetasosInterZoneTaskEndpoint interZoneTaskingEndpoint;

    @Override
    public PetasosActionableTask registerActionableTask(String taskFulfiller, PetasosActionableTask task) {
        LOG.info(".registerActionableTask(): Entry, taskFulfiller->{}, task->{}", taskFulfiller, task);

        PetasosActionableTask registeredTask = null;
        LOG.info(".registerActionableTask(): Check for interzone capability match");
        if(interZoneTaskingEndpoint.taskFulfillerIsInScope(taskFulfiller)){
            LOG.info(".registerActionableTask(): Using inter-zone communication framework");
            registeredTask = interZoneTaskingEndpoint.registerActionableTask(taskFulfiller, task);
        }
        LOG.info(".registerActionableTask(): Check for intrazone capability match");
        if(intraZoneTaskingEndpoint.taskFulfillerIsInScope(taskFulfiller)){
            LOG.info(".registerActionableTask(): Using intra-zone communication framework");
            registeredTask = intraZoneTaskingEndpoint.registerActionableTask(taskFulfiller, task);
        }
        if(registeredTask == null) {
            LOG.info(".executeTask(): Can't find suitable capability provider");
        }
        LOG.info(".executeTask(): Exit, registeredTask->{}", registeredTask);
        return(registeredTask);
    }

    @Override
    public PetasosActionableTask fulfillActionableTask(String serviceProviderName, PetasosActionableTask actionableTask) {
        LOG.debug(".fulfillActionableTask(): Entry, taskFulfiller->{}, actionableTask->{}", serviceProviderName, actionableTask);

        PetasosActionableTask registeredTask = null;
        if(interZoneTaskingEndpoint.taskFulfillerIsInScope(serviceProviderName)){
            LOG.trace(".fulfillActionableTask(): Using inter-zone communication framework");
            registeredTask = interZoneTaskingEndpoint.fulfillActionableTask(serviceProviderName, actionableTask);
        }
        if(intraZoneTaskingEndpoint.taskFulfillerIsInScope(serviceProviderName)){
            LOG.trace(".fulfillActionableTask(): Using intra-zone communication framework");
            registeredTask = intraZoneTaskingEndpoint.fulfillActionableTask(serviceProviderName, actionableTask);
        }
        if(registeredTask == null) {
            LOG.trace(".fulfillActionableTask(): Can't find suitable capability provider");
        }
        LOG.debug(".fulfillActionableTask(): Exit, registeredTask->{}", registeredTask);
        return(registeredTask);
    }

    @Override
    public PetasosActionableTask updateActionableTask(String serviceProviderName, PetasosActionableTask actionableTask) {
        LOG.info(".updateActionableTask(): Entry, taskFulfiller->{}, actionableTask->{}", serviceProviderName, actionableTask);

        PetasosActionableTask updatedTask = null;
        if(interZoneTaskingEndpoint.taskFulfillerIsInScope(serviceProviderName)){
            LOG.info(".updateActionableTask(): Using inter-zone communication framework");
            updatedTask = interZoneTaskingEndpoint.updateActionableTask(serviceProviderName, actionableTask);
        }
        if(intraZoneTaskingEndpoint.taskFulfillerIsInScope(serviceProviderName)){
            LOG.info(".updateActionableTask(): Using intra-zone communication framework");
            updatedTask = intraZoneTaskingEndpoint.updateActionableTask(serviceProviderName, actionableTask);
        }
        if(updatedTask == null) {
            LOG.info(".updateActionableTask(): Can't find suitable capability provider");
        }
        LOG.info(".updateActionableTask(): Exit, updatedTask->{}", updatedTask);
        return(updatedTask);
    }
}
