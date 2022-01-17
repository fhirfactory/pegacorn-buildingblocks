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
package net.fhirfactory.pegacorn.platform.edge.general;

import net.fhirfactory.pegacorn.core.interfaces.edge.PetasosServicesEndpointRegistrationService;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.PetasosEndpoint;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalPetasosParticipantCacheIM;
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalPetasosParticipantSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.endpoints.services.messaging.PetasosIPCMessagingEndpoint;
import net.fhirfactory.pegacorn.platform.edge.general.beans.PetasosEdgeDoNothingBean;
import net.fhirfactory.pegacorn.workshops.EdgeWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.MOAStandardWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JGroupsServiceManagementWUP extends MOAStandardWUP implements PetasosServicesEndpointRegistrationService{
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsServiceManagementWUP.class);

    private static String WUP_VERSION = "1.0.0";

    @Inject
    private PetasosIPCMessagingEndpoint petasosMessagingEndpoint;

    @Inject
    private EdgeWorkshop edgeWorkshop;

    @Inject
    private PetasosEdgeDoNothingBean doNothingBean;

    @Inject
    LocalPetasosParticipantSubscriptionMapIM topicServer;

    @Inject
    LocalPetasosParticipantCacheIM localPetasosParticipantCacheIM;

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (edgeWorkshop);
    }


    protected PetasosIPCMessagingEndpoint getPetasosMessagingEndpoint() {
        return (petasosMessagingEndpoint);
    }

    //
    // Getters and Setters
    //

    public LocalPetasosParticipantSubscriptionMapIM getTopicServer(){
        return(this.topicServer);
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    //
    // WUP Specification
    //

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        List<DataParcelManifest> subscriptionList = new ArrayList<>();
        return (subscriptionList);
    }

    @Override
    protected List<DataParcelManifest> declarePublishedTopics() {
        return (new ArrayList<>());
    }

    @Override
    protected String specifyWUPInstanceName() {
        return (getClass().getSimpleName());
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return (WUP_VERSION);
    }

    //
    // Route
    //

    @Override
    public void configure() throws Exception {

        getLogger().info("PetasosEdgeGeneralIPCWUP :: WUPIngresPoint/ingresFeed --> {}", ingresFeed());
        getLogger().info("PetasosEdgeGeneralIPCWUP :: WUPEgressPoint/egressFeed --> {}", egressFeed());

        fromIncludingPetasosServices(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .bean(doNothingBean, "doNothing")
                .to(egressFeed());
    }

    //
    // Endpoint Services
    //

    @Override
    public void registerEndpoint(PetasosEndpointFunctionTypeEnum functionType, PetasosEndpoint endpoint) {
        if(getMeAsATopologyComponent().getServiceEndpoints().containsKey(functionType)){
            getMeAsATopologyComponent().getServiceEndpoints().remove(functionType);
        }
        getMeAsATopologyComponent().getServiceEndpoints().put(functionType, endpoint);
    }
}
