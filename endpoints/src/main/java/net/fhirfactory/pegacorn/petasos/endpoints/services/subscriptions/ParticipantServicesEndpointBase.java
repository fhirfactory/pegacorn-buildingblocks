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
package net.fhirfactory.pegacorn.petasos.endpoints.services.subscriptions;

import net.fhirfactory.pegacorn.core.interfaces.pathway.TaskPathwayManagementServiceInterface;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.JGroupsIntegrationPointBase;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public abstract class ParticipantServicesEndpointBase extends JGroupsIntegrationPointBase
        implements TaskPathwayManagementServiceInterface {

    private boolean subscriptionCheckScheduled;
    private Object subscriptionCheckLock;

    private static Long SUBSCRIPTION_CHECK_INITIAL_DELAY=5000L;
    private static Long SUBSCRIPTION_CHECK_PERIOD = 5000L;

    private int subscriptionCheckCount;
    private static int CHANGE_DETECTION_SUBSCRIPTION_CHECK_COUNT = 10;


    //
    // Constructor(s)
    //

    public ParticipantServicesEndpointBase(){
        super();
        subscriptionCheckScheduled = false;
        subscriptionCheckLock = new Object();
        subscriptionCheckCount = 0;
    }

    //
    // Post Construct
    //

    @Override
    protected void executePostConstructActivities(){
        getLogger().info(".executePostConstructActivities(): Start");
        initialiseCacheSynchronisationDaemon();
        getLogger().info(".executePostConstructActivities(): Finish");
    }

    //
    // Further post construct activities
    //

    abstract protected void initialiseCacheSynchronisationDaemon();

    //
    // Getters (and Setters)
    //

    @Override
    protected String specifyJGroupsClusterName() {
        return (getComponentNameUtilities().getPetasosSubscriptionsServicesGroupName());
    }

    //
    // Add Integration Point to ProcessingPlant's IntegrationPointSet
    //

    @Override
    protected void addIntegrationPointToJGroupsIntegrationPointSet() {
        getJgroupsIPSet().setPetasosSubscriptionServicesEndpoint(getJGroupsIntegrationPoint());
    }

    //
    // Endpoint Definition
    //

    @Override
    protected String specifySubsystemParticipantName() {
        return (getProcessingPlant().getSubsystemParticipantName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.JGROUPS_INTEGRATION_POINT);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlant().getMeAsASoftwareComponent().getPetasosSubscriptionsStackConfigFile());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_SUBSCRIPTION);
    }

    //
    // Processing Plant check triggered by JGroups Cluster membership change
    //

    @Override
    protected void doIntegrationPointBusinessFunctionCheck(JGroupsIntegrationPointSummary integrationPointSummary, boolean isRemoved, boolean isAdded) {

    }

    //
    // Endpoint/Participant tests
    //
    protected boolean hasDifferentParticipantSubsystemName(PetasosParticipant participant){
        getLogger().debug(".hasDifferentParticipantSubsystemName(): Entry, participant->{}", participant);
        if(participant != null){
            if(participant.hasSubsystemParticipantName()){
                if(participant.getSubsystemParticipantName().contentEquals(getParticipantHolder().getMyProcessingPlantPetasosParticipant().getSubsystemParticipantName())){
                    getLogger().debug(".hasDifferentParticipantSubsystemName(): Exit, returning -true-");
                    return(true);
                }
            }
        }
        getLogger().debug(".hasDifferentParticipantSubsystemName(): Exit, returning -false-");
        return(false);
    }

    public String getAvailableParticipantInstanceName(String participantServiceName){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participantServiceName->{}", participantServiceName);
        PetasosAdapterAddress targetAddress = getTargetMemberAdapterInstanceForSubsystem(participantServiceName);
        String participantInstanceName = targetAddress.toString();
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participantInstanceName->{}", participantInstanceName);
        return(participantInstanceName);
    }

    public boolean isPetasosEndpointChannelAvailable(String petasosEndpointChannelName){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, participantInstanceName->{}", petasosEndpointChannelName);
        boolean participantInstanceNameStillActive = getTargetMemberAddress(petasosEndpointChannelName) != null;
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, participantInstanceNameStillActive->{}", participantInstanceNameStillActive);
        return(participantInstanceNameStillActive);
    }

    public String getServiceNameFromParticipantInstanceName(String participantInstanceName){
        getLogger().debug(".getServiceNameFromParticipantInstanceName(): Entry, participantInstanceName->{}", participantInstanceName);
        if(StringUtils.isEmpty(participantInstanceName)){
            getLogger().debug(".getServiceNameFromParticipantInstanceName(): Exit, participantInstanceName is empty!");
            return(null);
        }
        String[] nameParts = StringUtils.split(participantInstanceName, "(");
        String serviceName = nameParts[0];
        getLogger().debug(".getServiceNameFromParticipantInstanceName(): Exit, serviceName->{}", serviceName);
        return(serviceName);
    }

    protected String extractPublisherServiceName(String participantInstanceName){
        return(getServiceNameFromParticipantInstanceName(participantInstanceName));
    }

    //
    // Helpers
    //

    /**
     * This method returns a set of possible endpoints supporting the PUBSUB function for the given publisherServiceName.
     *
     * It first pulls ALL the petasosEndpointNames that are part of the generic publisherServiceName list (i.e. OAM.PubSub,
     * OAM.Discovery & IPC based endpoints) and then filters them down to only include the OAM.PubSub entries.
     *
     * @param publisherServiceName The "Publisher Service Name" to which candidate endpoints are to be found
     * @return The list of .OAM.PubSub endpoints supporting that service.
     */
    List<String> getPublisherServicePubSubCandidateSet(String publisherServiceName){
        List<String> candidateSet = new ArrayList<>();
        if(StringUtils.isEmpty(publisherServiceName)){
            return(candidateSet);
        }
        List<String> serviceNameMembership = getIntegrationPointMap().getParticipantFulfillers(publisherServiceName);
        if(serviceNameMembership.isEmpty()){
            return(candidateSet);
        }
        for(String currentMember: serviceNameMembership){
            if(currentMember.contains(PetasosEndpointFunctionTypeEnum.PETASOS_SUBSCRIPTIONS_ENDPOINT.getDisplayName())){
                candidateSet.add(currentMember);
            }
        }
        return(candidateSet);
    }


}
