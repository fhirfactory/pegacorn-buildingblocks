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
package net.fhirfactory.pegacorn.petasos.endpoints.services.metrics;

import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.JGroupsIntegrationPointBase;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

public abstract class PetasosOAMMetricsEndpointBase extends JGroupsIntegrationPointBase {

    @Produce
    private ProducerTemplate camelProducer;

    //
    // Constructor(s)
    //

    public PetasosOAMMetricsEndpointBase(){
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

    //
    // Getters (and Setters)
    //

    public ProducerTemplate getCamelProducer() {
        return camelProducer;
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getPetasosMetricsEndpointName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_INTEGRATION_POINT);
    }

    //
    // Endpoint Definition
    //

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlant().getMeAsASoftwareComponent().getPetasosMetricsStackConfigFile());
    }

    @Override
    protected String specifySubsystemParticipantName() {
        return (getProcessingPlant().getSubsystemParticipantName());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_METRICS_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_METRICS);
    }

    @Override
    protected String specifyJGroupsClusterName() {
        return (getComponentNameUtilities().getPetasosMetricsGroupName());
    }

    @Override
    protected void addIntegrationPointToJGroupsIntegrationPointSet() {
        getJgroupsIPSet().setPetasosMetricsServicesEndpoint(getJGroupsIntegrationPoint());
    }

    //
    // Processing Plant check triggered by JGroups Cluster membership change
    //

    @Override
    protected void doIntegrationPointBusinessFunctionCheck(JGroupsIntegrationPointSummary integrationPointSummary, boolean isRemoved, boolean isAdded) {

    }

//
    // Metrics Service Methods
    //

    /*
    public List<Address> getMetricsServerTargetAddressSet(String endpointServiceName){
        getLogger().info(".getMetricsServerTargetAddressSet(): Entry, endpointServiceName->{}", endpointServiceName);
        List<Address> endpointAddressSet = new ArrayList<>();
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getMetricsServerTargetAddressSet(): Exit, endpointServiceName is empty");
            return(endpointAddressSet);
        }
        List<PetasosAdapterAddress> memberAdapterSetForService = getTargetMemberAdapterSetForService(endpointServiceName);
        for(PetasosAdapterAddress currentMember: memberAdapterSetForService){
            Address currentMemberAddress = currentMember.getJGroupsAddress();
            if(currentMemberAddress != null){
                endpointAddressSet.add(currentMemberAddress);
            }
        }
        getLogger().debug(".getMetricsServerTargetAddressSet(): Exit, endpointAddressSet->{}", endpointAddressSet);
        return(endpointAddressSet);
    }

    public Address getCandidateMetricsServerTargetAddress(String endpointServiceName){
        getLogger().info(".getCandidateMetricsServerTargetAddress(): Entry, endpointServiceName->{}", endpointServiceName);
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getCandidateMetricsServerTargetAddress(): Exit, endpointServiceName is empty");
            return(null);
        }
        List<Address> endpointAddressSet = getMetricsServerTargetAddressSet(endpointServiceName);
        if(endpointAddressSet.isEmpty()){
            getLogger().debug(".getCandidateMetricsServerTargetAddress(): Exit, endpointAddressSet is empty");
            return(null);
        }
        Address endpointJGroupsAddress = endpointAddressSet.get(0);
        getLogger().debug(".getCandidateMetricsServerTargetAddress(): Exit, selected address->{}", endpointJGroupsAddress);
        return(endpointJGroupsAddress);
    }

     */




}
