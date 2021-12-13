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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam;

import net.fhirfactory.pegacorn.core.interfaces.oam.metrics.PetasosMetricsBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.metrics.PetasosMetricsHandlerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetric;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetricSet;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PetasosOAMMetricsEndpoint extends JGroupsPetasosEndpointBase implements PetasosMetricsBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosOAMMetricsEndpoint.class);

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private PetasosMetricsHandlerInterface metricsHandler;

    //
    // Constructor(s)
    //

    public PetasosOAMMetricsEndpoint(){
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
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getPetasosMetricsEndpointName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_MESSAGING_SERVICE);
    }

    //
    // Endpoint Definition
    //

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlantInterface().getProcessingPlantNode().getPetasosMetricsStackConfigFile());
    }

    @Override
    protected PetasosEndpointIdentifier specifyEndpointID() {
        PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
        // Get Core Values
        String endpointServiceName = specifyEndpointServiceName();
        String endpointFunctionName = specifyPetasosEndpointFunctionType().getDisplayName();
        String endpointUUID = getEndpointNameUtilities().getCurrentUUID();
        String endpointSite = getProcessingPlantInterface().getDeploymentSite();
        String endpointZone = getProcessingPlantInterface().getNetworkZone().getDisplayName();
        // Build EndpointName
        String endpointName = getEndpointNameUtilities().buildEndpointName(endpointServiceName, endpointUUID);
        // Build EndpointChannelName
        String endpointChannelName = getEndpointNameUtilities().buildChannelName(endpointSite, endpointZone, endpointServiceName, endpointFunctionName, endpointUUID);
        // Build EndpointID
        endpointID.setEndpointChannelName(endpointChannelName);
        endpointID.setEndpointName(endpointName);
        endpointID.setEndpointZone(getProcessingPlantInterface().getNetworkZone());
        endpointID.setEndpointSite(getProcessingPlantInterface().getDeploymentSite());
        endpointID.setEndpointGroup(getJgroupsParticipantInformationService().getPetasosMetricsGroupName());
        endpointID.setEndpointComponentID(getTopologyNode().getComponentID());
        endpointID.setProcessingPlantComponentID(getProcessingPlantInterface().getProcessingPlantNode().getComponentID());
        String endpointAddress = "JGroups:" + endpointChannelName + ":" + getJgroupsParticipantInformationService().getPetasosMetricsGroupName();
        endpointID.setEndpointDetailedAddressName(endpointAddress);
        return (endpointID);
    }

    @Override
    protected String specifyEndpointServiceName() {
        return (getProcessingPlantInterface().getIPCServiceName());
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
    protected void resolveTopologyEndpoint() {
        setTopologyNode(getJgroupsParticipantInformationService().getMyPetasosTopologyEndpoint());
    }

    @Override
    protected PubSubParticipant specifyPubSubParticipant() {
        PubSubParticipant myInterZoneParticipantRole = getJgroupsParticipantInformationService().getMyPetasosParticipantRole();
        if(myInterZoneParticipantRole == null){
            myInterZoneParticipantRole = getJgroupsParticipantInformationService().buildMyPetasosParticipantRole(getPetasosEndpoint());
        }
        return (myInterZoneParticipantRole);
    }

    @Override
    protected void registerWithCoreSubsystemPetasosEndpointsWatchdog() {
        getCoreSubsystemPetasosEndpointsWatchdog().setPetasosMetricsEndpoint(this.getPetasosEndpoint());
    }

    //
    // Metrics Service Methods
    //

    public List<Address> getMetricsServerTargetAddressSet(String endpointServiceName){
        getLogger().debug(".getMetricsServerTargetAddressSet(): Entry, endpointServiceName->{}", endpointServiceName);
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
        getLogger().debug(".getCandidateMetricsServerTargetAddress(): Entry, endpointServiceName->{}", endpointServiceName);
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

    //
    // AuditEvent RPC Method Support
    //

    public Instant updateMetrics(String serviceProviderName, PetasosComponentMetricSet metricSet){
        getLogger().trace(".updateMetrics(): Entry, serviceProviderName->{}, metricSet->{}", serviceProviderName, metricSet);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getCandidateMetricsServerTargetAddress(serviceProviderName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = metricSet;
            classSet[0] = PetasosComponentMetricSet.class;
            objectSet[1] = endpointIdentifier;
            classSet[1] = PetasosEndpointIdentifier.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Instant response = getRPCDispatcher().callRemoteMethod(targetAddress, "updateMetricsHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".updateMetrics(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".updateMetrics(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".updateMetrics(): Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }
    public Instant updateMetricsHandler(PetasosComponentMetricSet metricSet, PetasosEndpointIdentifier endpointIdentifier){
        getLogger().trace(".updateMetricsHandler(): Entry, metricSet->{}, endpointIdentifier->{}", metricSet, endpointIdentifier);
        Instant outcome = null;
        if((metricSet != null) && (endpointIdentifier != null)) {
            outcome = metricsHandler.captureMetrics(metricSet, endpointIdentifier);
        }
        getLogger().debug(".updateMetricsHandler(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    public Instant updateMetric(String serviceProviderName, PetasosComponentMetric metric){
        getLogger().trace(".updateMetric(): Entry, serviceProviderName->{}, metric->{}", serviceProviderName, metric);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getCandidateMetricsServerTargetAddress(serviceProviderName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = metric;
            classSet[0] = PetasosComponentMetric.class;
            objectSet[1] = endpointIdentifier;
            classSet[1] = PetasosEndpointIdentifier.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Instant responseInstant = getRPCDispatcher().callRemoteMethod(targetAddress, "updateMetricHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".updateMetric(): Exit, responseInstant->{}", responseInstant);
            return(responseInstant);
        } catch (NoSuchMethodException e) {
            getLogger().error(".updateMetric(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".updateMetric: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    public Instant updateMetricHandler(PetasosComponentMetric metric, PetasosEndpointIdentifier endpointIdentifier){
        getLogger().trace(".logAuditEventHandler(): Entry, metric->{}, endpointIdentifier->{}", metric, endpointIdentifier);
        Instant outcomeInstant = null;
        if((metric != null) && (endpointIdentifier != null)) {
            outcomeInstant = metricsHandler.captureMetric(metric, endpointIdentifier);
        }
        getLogger().debug(".logAuditEventHandler(): Exit, outcomeInstant->{}", outcomeInstant);
        return(outcomeInstant);
    }

    //
    // Metrics Services
    //

    @Override
    public Instant captureMetric(String serviceProviderName, PetasosComponentMetric metric) {
        getLogger().debug(".captureMetric(): Entry, serviceProviderName->{}, metric->{}", serviceProviderName, metric);

        Instant captureInstant = updateMetric(serviceProviderName, metric);

        getLogger().debug(".captureMetric(): Exit, captureInstant->{}", captureInstant);
        return(captureInstant);
    }

    @Override
    public Instant captureMetrics(String serviceProviderName, PetasosComponentMetricSet metricSet) {
        getLogger().debug(".captureMetrics(): Entry, serviceProviderName->{}, metricSet->{}", serviceProviderName, metricSet);

        Instant captureInstant = updateMetrics(serviceProviderName, metricSet);

        getLogger().debug(".captureMetrics(): Exit, captureInstant->{}", captureInstant);
        return(captureInstant);
    }
}
