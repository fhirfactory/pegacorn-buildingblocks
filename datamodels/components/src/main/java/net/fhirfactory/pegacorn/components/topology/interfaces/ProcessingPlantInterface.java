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
package net.fhirfactory.pegacorn.components.topology.interfaces;

import net.fhirfactory.pegacorn.components.auditing.AuditEventCaptureLevelEnum;
import net.fhirfactory.pegacorn.components.capabilities.CapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.components.capabilities.CapabilityFulfillmentManagementInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;

public interface ProcessingPlantInterface extends CapabilityFulfillmentManagementInterface, CapabilityFulfillmentInterface {
    public void initialisePlant();
    public SolutionTopologyNode getSolutionNode();
    public String getSimpleFunctionName();
    public String getSimpleInstanceName();
    public NetworkSecurityZoneEnum getNetworkZone();
    public String getDeploymentPlatformName();
    public String getDeploymentSiteName();
    public PegacornTopologyFactoryInterface getTopologyFactory();
    public ProcessingPlantTopologyNode getProcessingPlantNode();
    public WorkshopTopologyNode getWorkshop(String workshopName, String version);
    public WorkshopTopologyNode getWorkshop(String workshopName);
    public String getIPCServiceRoutingName();
    public AuditEventCaptureLevelEnum getAuditingLevel();
    public boolean isITOpsNode();
}
