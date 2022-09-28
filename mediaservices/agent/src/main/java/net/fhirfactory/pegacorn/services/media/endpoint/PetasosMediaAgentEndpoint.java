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
package net.fhirfactory.pegacorn.services.media.endpoint;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hl7.fhir.r4.model.Media;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.core.interfaces.media.PetasosMediaServiceBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.topology.PetasosTopologyReportingServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.petasos.endpoints.services.media.PetasosMediaServicesEndpoint;

@ApplicationScoped
public class PetasosMediaAgentEndpoint extends PetasosMediaServicesEndpoint
        implements PetasosMediaServiceBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosMediaAgentEndpoint.class);

    @Inject
    private PetasosTopologyReportingServiceProviderNameInterface topologyReportingProvider;

    //
    // Constructor(s)
    //

    public PetasosMediaAgentEndpoint(){
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
    // Post Construct (invoked from Superclass)
    //

    @Override
    protected void executePostConstructInstanceActivities() {

    }

    //
    // Metrics (Client) RPC Method Support
    //

    @Override
    public Boolean saveMedia(String serviceProviderName, Media media){
        getLogger().info(".saveMedia(): Entry, serviceProviderName->{}, media->{}", serviceProviderName, media);
        JGroupsIntegrationPointSummary myJGroupsIP = createSummary(getJGroupsIntegrationPoint());
        Address targetAddress = getCandidateMediaServerTargetAddress(serviceProviderName);
        
        Object objectSet[] = new Object[2];
        Class classSet[] = new Class[2];
        objectSet[0] = media;
        classSet[0] = Media.class;
        objectSet[1] = myJGroupsIP;
        classSet[1] = JGroupsIntegrationPointSummary.class;
        RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
        String savedMediaId = null;
        try {
            synchronized (getIPCChannelLock()){
                savedMediaId = getRPCDispatcher().callRemoteMethod(targetAddress, "saveMediaHandler", objectSet, classSet, requestOptions);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".saveMedia(): Error ({}) ->{}", e.getClass().getCanonicalName(), e.getMessage());
            return(false);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".saveMedia(): Error while attempting saveMediaHandler remote call for serviceProvider", e);
            return(false);
        }
        
        if (savedMediaId != null) {
            media.setId(savedMediaId);
        }
        getMetricsAgent().incrementRemoteProcedureCallCount();
        getLogger().info(".saveMedia(): Exit, savedMediaId->{}", savedMediaId);
        return(savedMediaId != null);
    }

	@Override
	public Media retrieveMedia(String serviceProviderName, String mediaId) {
        getLogger().info(".retrieveMedia(): Entry, serviceProviderName->{}, mediaId->{}", serviceProviderName, mediaId);
        JGroupsIntegrationPointSummary myJGroupsIP = createSummary(getJGroupsIntegrationPoint());
        Address targetAddress = getCandidateMediaServerTargetAddress(serviceProviderName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = mediaId;
            classSet[0] = String.class;
            objectSet[1] = myJGroupsIP;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Media response = null;
            synchronized (getIPCChannelLock()){
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "retrieveMediaHandler", objectSet, classSet, requestOptions);
            }
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().info(".retrieveMedia(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".retrieveMedia(): serviceProvider->{}, mediaId->{}, targetAddress->{}: Error (NoSuchMethodException) ->{}", serviceProviderName, mediaId, targetAddress, e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".retrieveMedia(): serviceProvider->{}, mediaId->{}, targetAddress->{}: Error ({}) ->{}", serviceProviderName, mediaId, targetAddress, e.getClass().getCanonicalName(), e.getMessage());
            return(null);
        }
	}
}

