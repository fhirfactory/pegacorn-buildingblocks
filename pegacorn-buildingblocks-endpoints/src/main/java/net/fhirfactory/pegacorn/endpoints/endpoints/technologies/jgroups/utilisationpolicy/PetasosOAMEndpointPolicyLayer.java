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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.utilisationpolicy;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointChannelScopeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import org.apache.commons.lang3.StringUtils;

public abstract class PetasosOAMEndpointPolicyLayer extends JGroupsPetasosEndpointBase {


    //
    // Endpoint/Participant tests
    //

    protected boolean hasEndpointServiceName(PubSubParticipant participant){
        if(participant != null){
            if(participant.getInterSubsystemParticipant() != null){
                if(participant.getInterSubsystemParticipant().getEndpointID() != null){
                    if(participant.getInterSubsystemParticipant().getEndpointServiceName() != null){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    protected boolean hasEndpointServiceName(PetasosEndpoint petasosEndpoint){
        if(petasosEndpoint != null){
            if(!StringUtils.isEmpty(petasosEndpoint.getEndpointServiceName())){
                return(true);
            }
        }
        return(false);
    }

    protected boolean isEndpointServiceAvailable(String endpointServiceName){
        getLogger().debug(".isEndpointServiceAvailable(): Entry, endpointServiceName->{}", endpointServiceName);
        boolean participantIsAvailable = getAvailableEndpointInstanceForEndpointService(endpointServiceName) != null;
        getLogger().debug(".isEndpointServiceAvailable(): Exit, returning->{}", participantIsAvailable);
        return(participantIsAvailable);
    }

    // Get an EndpointInstance for a Given EndpointService (encpasulated in what-ever)

    protected String getAvailableEndpointInstanceForEndpointService(InterSubsystemPubSubParticipant participant){
        getLogger().debug(".getAvailableEndpointInstanceForEndpointService(): Entry, participant->{}", participant);
        if(participant == null){
            return(null);
        }
        String endpointInstanceName = getAvailableEndpointInstanceForEndpointService(participant.getEndpointServiceName());
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, serviceInstanceName->{}", endpointInstanceName);
        return(endpointInstanceName);
    }

    public String getAvailableEndpointInstanceForEndpointService(String endpointServiceName){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, endpointServiceName->{}", endpointServiceName);
        if(StringUtils.isEmpty(endpointServiceName)){
            return(null);
        }
        PetasosAdapterAddress targetAddress = getTargetMemberAdapterInstanceForService(endpointServiceName);
        String participantInstanceName = targetAddress.toString();
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participantInstanceName->{}", participantInstanceName);
        return(participantInstanceName);
    }

    //
    // Tests for Existence of Endpoints and/or EndpointServices
    //

    public boolean isEndpointInstanceReachable(String petasosEndpointKey){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, participantInstanceName->{}", petasosEndpointKey);
        String petasosEndpointName = removeFunctionNameSuffixFromEndpointName(petasosEndpointKey);
        String petasosEndpointPubSubKey = addFunctionNameSuffixToEndpointName(petasosEndpointName, PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT);
        getLogger().trace(".isParticipantInstanceAvailable(): Exit, petasosEndpointPubSubKey->{}", petasosEndpointPubSubKey);
        boolean participantInstanceNameStillActive = getTargetMemberAddress(petasosEndpointPubSubKey) != null;
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, participantInstanceNameStillActive->{}", participantInstanceNameStillActive);
        return(participantInstanceNameStillActive);
    }

    public String getServiceNameFromParticipantInstanceName(String participantInstanceName){
        if(StringUtils.isEmpty(participantInstanceName)){
            return(null);
        }
        String[] nameParts = StringUtils.split(participantInstanceName, "(");
        return(nameParts[0]);
    }

    protected String extractPublisherServiceName(String participantInstanceName){
        return(getServiceNameFromParticipantInstanceName(participantInstanceName));
    }

    //
    // Channel Traffic Utilisation Policy
    //

    protected boolean isRightChannel(PetasosEndpointIdentifier otherEndpointID) {
        boolean sameZone = otherEndpointID.getEndpointZone().equals(getPetasosEndpoint().getEndpointID().getEndpointZone());
        boolean sameSite = otherEndpointID.getEndpointSite().contentEquals(getPetasosEndpoint().getEndpointID().getEndpointSite());
        boolean isInScope = false;
        if (sameSite && sameZone) {
            if (getPetasosEndpoint().getEndpointChannelScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTRAZONE)) {
                isInScope = true;
            }
        }
        if (sameSite && !sameZone) {
            if (getPetasosEndpoint().getEndpointChannelScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERZONE)) {
                isInScope = true;
            }
        }
        if (!sameSite) {
            if (getPetasosEndpoint().getEndpointChannelScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERSITE)) {
                isInScope = true;
            }
        }
        return(isInScope);
    }

    protected boolean isRightChannelBasedOnChannelName(String otherEndpointChannelName) {
        boolean channelIsInterSite = otherEndpointChannelName.contains(getJgroupsParticipantInformationService().getInterSitePrefix());
        boolean channelIsInterZone = otherEndpointChannelName.contains(getJgroupsParticipantInformationService().getInterZonePrefix());
        boolean channelIsIntraZone = otherEndpointChannelName.contains(getJgroupsParticipantInformationService().getIntraZonePrefix());
        boolean withinScope = false;
        if (channelIsIntraZone) {
            if (getPetasosEndpoint().getEndpointChannelScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTRAZONE)) {
                withinScope = true;
            }
        }
        if (channelIsInterZone) {
            if (getPetasosEndpoint().getEndpointChannelScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERZONE)) {
                withinScope = true;
            }
        }
        if (channelIsInterSite) {
            if (getPetasosEndpoint().getEndpointChannelScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERSITE)) {
                withinScope = true;
            }
        }
        return(withinScope);
    }
}
