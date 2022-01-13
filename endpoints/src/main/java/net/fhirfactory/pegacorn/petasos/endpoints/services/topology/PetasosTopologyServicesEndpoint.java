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
package net.fhirfactory.pegacorn.petasos.endpoints.services.topology;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.JGroupsIntegrationPointBase;
import net.fhirfactory.pegacorn.petasos.endpoints.topology.SoftwareComponentSet;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosTopologyServicesEndpoint extends JGroupsIntegrationPointBase {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTopologyServicesEndpoint.class);

    private boolean topologySynchronisationDaemonInitialised;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosDistributedSoftwareComponentMapDM softwareComponentDM;

    //
    // Constructor
    //

    public PetasosTopologyServicesEndpoint(){
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

    //
    // Endpoint Definition
    //

    @Override
    protected String specifySubsystemParticipantName() {
        return (getProcessingPlant().getSubsystemParticipantName());
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getPetasosTopologyServicesEndpointName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_INTEGRATION_POINT);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlant().getMeAsASoftwareComponent().getPetasosTopologyStackConfigFile());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_TOPOLOGY);
    }

    @Override
    protected String specifyJGroupsClusterName() {
        return (getComponentNameUtilities().getPetasosTopologyServicesGroupName());
    }

    @Override
    protected void addIntegrationPointToJGroupsIntegrationPointSet() {
        getJgroupsIPSet().setPetasosTopologyServicesEndpoint(getJGroupsIntegrationPoint());
    }

    //
    // Asynchronous Update of Topology triggered by changes in the JGroups Cluster Membership
    //

    @Override
    protected void doIntegrationPointBusinessFunctionCheck(JGroupsIntegrationPointSummary integrationPoint, boolean isRemoved, boolean isAdded) {
        getLogger().debug(".doIntegrationPointBusinessFunctionCheck(): Entry, integrationPoint->{}, isRemoved->{}, isAdded->{}", integrationPoint, isRemoved, isAdded);
        if(integrationPoint == null){
            getLogger().debug(".doIntegrationPointBusinessFunctionCheck(): integrationPoint is null, exiting!");
            return;
        }
        if(isRemoved){
            getLogger().trace(".doIntegrationPointBusinessFunctionCheck(): Is a -remove- activity");
            softwareComponentDM.removeDiscoveredProcessingPlant(integrationPoint);
            getLogger().debug(".doIntegrationPointBusinessFunctionCheck(): Exit, finished removing associated software components from cache!");
            return;
        }
        if(isAdded){
            getLogger().trace(".doIntegrationPointBusinessFunctionCheck(): Is an -add- activity");
            SoftwareComponentSet softwareComponentSet = probeProcessingPlantTopologyDetail(integrationPoint);
            if(softwareComponentSet != null){
                if(!softwareComponentSet.getComponentSet().isEmpty()){
                    for(SoftwareComponent currentSoftwareComponent: softwareComponentSet.getComponentSet()){
                        softwareComponentDM.addTopologyNode(integrationPoint, currentSoftwareComponent);
                    }
                }
            }
            getLogger().debug(".doIntegrationPointBusinessFunctionCheck(): Exit, finished adding associated software components into cache!");
            return;
        }
        getLogger().debug(".doIntegrationPointBusinessFunctionCheck(): Exit, nothing to do!");
    }

/*

    //
    // Publisher Management
    //

    protected void addPetasosParticipantToLocalRegistry(PetasosEndpoint addedPetasosEndpoint) {
        getLogger().debug(".addPetasosParticipantToLocalRegistry(): Entry, addedPetasosEndpoint->{}", addedPetasosEndpoint);
        PetasosEndpoint publisherEndpoint = buildPetasosEndpoint(addedPetasosEndpoint);
        if(publisherEndpoint == null){
            getLogger().warn(".addPetasosParticipantToLocalRegistry(): Exit, could not resolve Subscription Endpoint for a given Topology endpoint");
            return;
        }
        participantCacheIM.synchroniseLocalWithCentralCache(publisherEndpoint.getEndpointID().getProcessingPlantComponentID());
        getLogger().debug(".addPetasosParticipantToLocalRegistry(): Exit");
    }

    protected void removePetasosParticipantFromLocalRegistry(PetasosEndpoint addedPetasosEndpoint) {
        getLogger().debug(".addPetasosParticipantToLocalRegistry(): Entry, addedPetasosEndpoint->{}", addedPetasosEndpoint);
        PetasosEndpoint publisherEndpoint = buildPetasosEndpoint(addedPetasosEndpoint);
        if(publisherEndpoint == null){
            getLogger().warn(".addPetasosParticipantToLocalRegistry(): Exit, could not resolve Subscription Endpoint for a given Topology endpoint");
            return;
        }
        participantCacheIM.synchroniseLocalWithCentralCache(publisherEndpoint.getEndpointID().getProcessingPlantComponentID());
        getLogger().debug(".addPetasosParticipantToLocalRegistry(): Exit");
    }
*/

    //
    // Topology (Detailed) Information Collection
    //

    protected SoftwareComponentSet probeProcessingPlantTopologyDetail(JGroupsIntegrationPointSummary targetJGroupsIntegrationPoint){
        getLogger().info(".probeEndpointTopologyDetail(): Entry, targetJGroupsIntegrationPoint->{}",targetJGroupsIntegrationPoint.getChannelName());
        getLogger().debug(".probeEndpointTopologyDetail(): Entry, targetJGroupsIntegrationPoint->{}", targetJGroupsIntegrationPoint);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = targetJGroupsIntegrationPoint;
            classSet[0] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Address endpointAddress = getTargetMemberAddress(targetJGroupsIntegrationPoint.getChannelName());
            SoftwareComponentSet nodeList = getRPCDispatcher().callRemoteMethod(endpointAddress, "probeProcessingPlantTopologyDetailHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".probeEndpointTopologyDetail(): Exit, response->{}", nodeList);
            return(nodeList);
        } catch (NoSuchMethodException e) {
            getLogger().error(".probeEndpointTopologyDetail(): Error (NoSuchMethodException) message->{}, stacktrace->{}", ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
            return(null);
        } catch (Exception e) {
            getLogger().error(".probeEndpointTopologyDetail: Error (GeneralException) message->{}, stacktrace->{}", ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
            return(null);
        }
    }

    public SoftwareComponentSet probeProcessingPlantTopologyDetailHandler(JGroupsIntegrationPointSummary sourceForRequest) {
        getLogger().debug(".probeEndpointTopologyDetailHandler(): Entry, sourceForRequest->{}", sourceForRequest);
        SoftwareComponentSet myReport = new SoftwareComponentSet();
        myReport.getComponentSet().addAll(getTopologyIM().getNodeElementSet());
        return(myReport);
    }

}
