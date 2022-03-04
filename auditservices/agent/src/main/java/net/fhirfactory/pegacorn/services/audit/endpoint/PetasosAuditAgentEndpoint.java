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
package net.fhirfactory.pegacorn.services.audit.endpoint;

import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.topology.PetasosTopologyReportingServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.core.model.transaction.model.PegacornTransactionMethodOutcome;
import net.fhirfactory.pegacorn.petasos.endpoints.services.audit.PetasosAuditServicesEndpoint;
import org.hl7.fhir.r4.model.AuditEvent;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PetasosAuditAgentEndpoint extends PetasosAuditServicesEndpoint
        implements PetasosAuditEventServiceBrokerInterface{
    private static final Logger LOG = LoggerFactory.getLogger(PetasosAuditAgentEndpoint.class);

    @Inject
    private PetasosTopologyReportingServiceProviderNameInterface topologyReportingProvider;

    //
    // Constructor(s)
    //

    public PetasosAuditAgentEndpoint(){
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
    public Boolean logAuditEvent(String serviceProviderName, AuditEvent event){
        getLogger().info(".logAuditEvent(): Entry, serviceProviderName->{}, event->{}", serviceProviderName, event);
        JGroupsIntegrationPointSummary myJGroupsIP = createSummary(getJGroupsIntegrationPoint());
        Address targetAddress = getCandidateAuditServerTargetAddress(serviceProviderName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = event;
            classSet[0] = AuditEvent.class;
            objectSet[1] = myJGroupsIP;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PegacornTransactionMethodOutcome response = null;
            synchronized (getIPCChannelLock()){
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "logAuditEventHandler", objectSet, classSet, requestOptions);
            }
            getMetricsAgent().incrementRemoteProcedureCallCount();
            Boolean created = response.getCreated();
            getLogger().info(".logAuditEvent(): Exit, response->{}", response);
            return(created);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".logAuditEvent(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".logAuditEvent: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public Boolean logAuditEvent(String serviceProviderName, List<AuditEvent> eventList){
        getLogger().trace(".logAuditEvent(): Entry, serviceProviderName->{}, eventList->{}", serviceProviderName, eventList);
        JGroupsIntegrationPointSummary jgroupsIP = createSummary(getJGroupsIntegrationPoint());
        Address targetAddress = getCandidateAuditServerTargetAddress(serviceProviderName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = eventList;
            classSet[0] = List.class;
            objectSet[1] = jgroupsIP;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PegacornTransactionMethodOutcome response = null;
            synchronized (getIPCChannelLock()){
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "logMultipleAuditEventHandler", objectSet, classSet, requestOptions);
            }
            getMetricsAgent().incrementRemoteProcedureCallCount();
            Boolean created = response.getCreated();
            getLogger().debug(".logAuditEvent(): Exit, response->{}", response);
            return(created);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".logAuditEvent(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".logAuditEvent: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }
}

