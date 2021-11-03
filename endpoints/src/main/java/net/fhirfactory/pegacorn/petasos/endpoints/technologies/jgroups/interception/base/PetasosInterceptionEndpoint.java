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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.interception.base;

import net.fhirfactory.pegacorn.core.interfaces.interception.PetasosInterceptionHandlerInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.core.model.transaction.model.PegacornTransactionMethodOutcome;
import net.fhirfactory.pegacorn.core.model.transaction.valuesets.PegacornTransactionStatusEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PetasosInterceptionEndpoint extends JGroupsPetasosEndpointBase {

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    PetasosInterceptionHandlerInterface interceptionHandler;

    //
    // Constructor
    //

    public PetasosInterceptionEndpoint(){
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

    public List<Address> getInterceptionCollectorTargetAddressSet(String endpointServiceName){
        getLogger().debug(".getInterceptionCollectorTargetAddressSet(): Entry, endpointServiceName->{}", endpointServiceName);
        List<Address> endpointAddressSet = new ArrayList<>();
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getInterceptionCollectorTargetAddressSet(): Exit, endpointServiceName is empty");
            return(endpointAddressSet);
        }
        List<PetasosAdapterAddress> memberAdapterSetForService = getTargetMemberAdapterSetForService(endpointServiceName);
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


    public boolean interceptionCollectorIsInScope(String capabilityProviderServiceName){
        List<String> memberSetBasedOnService = getClusterMemberSetBasedOnService(capabilityProviderServiceName);
        if(memberSetBasedOnService.isEmpty()){
            return(false);
        }
        for(String currentName: memberSetBasedOnService){
            if(isWithinScopeBasedOnChannelName(currentName)){
                return(true);
            }
        }
        return(false);
    }

    //
    // Interception Methods
    //

    public PetasosFulfillmentTask redirectFulfillmentTask(String collectorServiceName, PetasosFulfillmentTask task){
        getLogger().trace(".redirectFulfillmentTask(): Entry, collectorServiceName->{}, event->{}", collectorServiceName, task);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getInterceptionCollectorTargetAddress(collectorServiceName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = task;
            classSet[0] = PetasosFulfillmentTask.class;
            objectSet[1] = endpointIdentifier;
            classSet[1] = PetasosEndpointIdentifier.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosFulfillmentTask redirectedTaskOutcome = getRPCDispatcher().callRemoteMethod(targetAddress, "redirectFulfillmentTaskHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".redirectFulfillmentTask(): Exit, redirectedTask->{}", redirectedTaskOutcome);
            return(redirectedTaskOutcome);
        } catch (NoSuchMethodException e) {
            getLogger().error(".redirectFulfillmentTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".redirectFulfillmentTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    public PetasosFulfillmentTask redirectFulfillmentTaskHandler(PetasosFulfillmentTask task, PetasosEndpointIdentifier endpointIdentifier){
        getLogger().trace(".redirectFulfillmentTaskHandler(): Entry, task->{}, endpointIdentifier->{}", task, endpointIdentifier);
        PetasosFulfillmentTask redirectedTaskOutcome = null;
        if((task != null) && (endpointIdentifier != null)) {
            redirectedTaskOutcome = interceptionHandler.redirectFulfillmentTask(task, endpointIdentifier);
        }
        getLogger().debug(".redirectFulfillmentTaskHandler(): Exit, redirectedTaskOutcome->{}", redirectedTaskOutcome);
        return(redirectedTaskOutcome);
    }

    //
    // Getters (and Setters)
    //


    public ProducerTemplate getCamelProducer() {
        return camelProducer;
    }
}
