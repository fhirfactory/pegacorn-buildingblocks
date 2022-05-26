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
package net.fhirfactory.pegacorn.petasos.endpoints.services.interception;

import net.fhirfactory.pegacorn.core.interfaces.interception.PetasosInterceptionBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.interception.PetasosInterceptionHandlerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.JGroupsIntegrationPointBase;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PetasosOAMInterceptionEndpoint extends JGroupsIntegrationPointBase implements PetasosInterceptionBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosOAMInterceptionEndpoint.class);

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    PetasosInterceptionHandlerInterface interceptionHandler;

    //
    // Constructor(s)
    //

    public PetasosOAMInterceptionEndpoint(){
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

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    protected ProducerTemplate getCamelProducer(){
        return(camelProducer);
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getPetasosInterceptionEndpointName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.JGROUPS_INTEGRATION_POINT);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlant().getMeAsASoftwareComponent().getPetasosInterceptionStackConfigFile());
    }

    @Override
    protected String specifySubsystemParticipantName() {
        return (getProcessingPlant().getSubsystemParticipantName());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_INTERCEPTION_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_INTERCEPTION);
    }

    @Override
    protected String specifyJGroupsClusterName() {
        return (getComponentNameUtilities().getPetasosInterceptionGroupName());
    }

    @Override
    protected void addIntegrationPointToJGroupsIntegrationPointSet() {
        getJgroupsIPSet().setPetasosInterceptionServicesEndpoint(getJGroupsIntegrationPoint());
    }

    //
    // Processing Plant check triggered by JGroups Cluster membership change
    //

    @Override
    protected void doIntegrationPointBusinessFunctionCheck(JGroupsIntegrationPointSummary integrationPointSummary, boolean isRemoved, boolean isAdded) {

    }

    //
    // Business Methods
    //

    public List<Address> getInterceptionCollectorTargetAddressSet(String endpointServiceName){
        getLogger().debug(".getInterceptionCollectorTargetAddressSet(): Entry, endpointServiceName->{}", endpointServiceName);
        List<Address> endpointAddressSet = new ArrayList<>();
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getInterceptionCollectorTargetAddressSet(): Exit, endpointServiceName is empty");
            return(endpointAddressSet);
        }
        List<PetasosAdapterAddress> memberAdapterSetForService = getTargetMemberAdapterSetForSubsystemName(endpointServiceName);
        for(PetasosAdapterAddress currentMember: memberAdapterSetForService){
            Address currentMemberAddress = currentMember.getJGroupsAddress();
            if(currentMemberAddress != null){
                endpointAddressSet.add(currentMemberAddress);
            }
        }
        getLogger().debug(".getInterceptionCollectorTargetAddressSet(): Exit, endpointAddressSet->{}", endpointAddressSet);
        return(endpointAddressSet);
    }

    public Address getInterceptionCollectorTargetAddress(String endpointServiceName){
        getLogger().debug(".getInterceptionCollectorTargetAddress(): Entry, endpointServiceName->{}", endpointServiceName);
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getInterceptionCollectorTargetAddress(): Exit, endpointServiceName is empty");
            return(null);
        }
        List<Address> endpointAddressSet = getInterceptionCollectorTargetAddressSet(endpointServiceName);
        if(endpointAddressSet.isEmpty()){
            getLogger().debug(".getInterceptionCollectorTargetAddress(): Exit, endpointAddressSet is empty");
            return(null);
        }
        Address endpointJGroupsAddress = endpointAddressSet.get(0);
        getLogger().debug(".getInterceptionCollectorTargetAddress(): Exit, selected address->{}", endpointJGroupsAddress);
        return(endpointJGroupsAddress);
    }


    //
    // Interception Methods
    //

    @Override
    public PetasosFulfillmentTask redirectFulfillmentTask(String collectorServiceName, PetasosFulfillmentTask task){
        getLogger().trace(".redirectFulfillmentTask(): Entry, collectorServiceName->{}, event->{}", collectorServiceName, task);
        JGroupsIntegrationPointSummary myIP = createSummary(getJGroupsIntegrationPoint());
        Address targetAddress = getInterceptionCollectorTargetAddress(collectorServiceName);
        if(targetAddress == null){
            getLogger().error(".redirectFulfillmentTask(): Cannot find collectServiceName candidate->{}", collectorServiceName);
            return(null);
        }
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = task;
            classSet[0] = PetasosFulfillmentTask.class;
            objectSet[1] = myIP;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosFulfillmentTask redirectedTaskOutcome = null;
            synchronized (getIPCChannelLock()) {
                redirectedTaskOutcome = getRPCDispatcher().callRemoteMethod(targetAddress, "redirectFulfillmentTaskHandler", objectSet, classSet, requestOptions);
            }
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".redirectFulfillmentTask(): Exit, redirectedTask->{}", redirectedTaskOutcome);
            return(redirectedTaskOutcome);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".redirectFulfillmentTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".redirectFulfillmentTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    public PetasosFulfillmentTask redirectFulfillmentTaskHandler(PetasosFulfillmentTask task, JGroupsIntegrationPointSummary endpointIdentifier){
        getLogger().trace(".redirectFulfillmentTaskHandler(): Entry, task->{}, endpointIdentifier->{}", task, endpointIdentifier);
        PetasosFulfillmentTask redirectedTaskOutcome = null;
        if((task != null) && (endpointIdentifier != null)) {
            redirectedTaskOutcome = interceptionHandler.redirectFulfillmentTask(task, endpointIdentifier);
        }
        getMetricsAgent().incrementRemoteProcedureCallHandledCount();
        getLogger().debug(".redirectFulfillmentTaskHandler(): Exit, redirectedTaskOutcome->{}", redirectedTaskOutcome);
        return(redirectedTaskOutcome);
    }
}
