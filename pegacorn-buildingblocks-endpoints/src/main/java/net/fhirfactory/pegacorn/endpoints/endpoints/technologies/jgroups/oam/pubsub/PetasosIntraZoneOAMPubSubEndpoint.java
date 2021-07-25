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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.oam.pubsub;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosEndpoint;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class PetasosIntraZoneOAMPubSubEndpoint extends JGroupsPetasosEndpointBase{
    private static final Logger LOG = LoggerFactory.getLogger(PetasosIntraZoneOAMPubSubEndpoint.class);

    private static String OAM_ENDPOINT_SUFFIX = ".OAM.PubSub";
    private static String OAM_GROUP_NAME = "IntraZone.OAM";

    @Override
    protected Logger specifyLogger() {
        return null;
    }

    @Override
    protected PetasosEndpointIdentifier specifyEndpointID() {
        PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
        String endpointName = getProcessingPlantInterface().getIPCServiceName() + OAM_ENDPOINT_SUFFIX + "(" + UUID.randomUUID().toString() + ")";
        endpointID.setEndpointName(endpointName);
        endpointID.setEndpointZone(getProcessingPlantInterface().getNetworkZone().toString());
        endpointID.setEndpointSite(getProcessingPlantInterface().getDeploymentSite());
        endpointID.setEndpointGroup(OAM_GROUP_NAME);
        String endpointAddress = "JGroups:" + endpointName + ":" + OAM_GROUP_NAME;
        endpointID.setEndpointAddress(endpointAddress);
        return (endpointID);
    }

    @Override
    protected String specifyEndpointServiceName() {
        return (getProcessingPlantInterface().getIPCServiceName());
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getFunctionNameIntraZoneJGroupsOAM());
    }

    @Override
    protected TopologyEndpointTypeEnum specifyIPCType() {
        return (TopologyEndpointTypeEnum.JGROUPS_INTRAZONE_OAM_SERVICE);
    }

    @Override
    public PetasosEndpointStatusEnum checkInterfaceStatus(PetasosEndpointIdentifier interfaceAddress) {
        PetasosEndpoint petasosEndpoint = probeEndpoint(interfaceAddress, getPetasosEndpoint());
        return(petasosEndpoint.getStatus());
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlantInterface().getProcessingPlantNode().getIntraZoneOAMStackConfigFile());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.ENDPOINT_FUNCTION_PEGACORN_OAM);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENPOINT_PAYLOAD_PEGACORN_OAM);
    }
}
