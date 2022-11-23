/*
 * Copyright (c) 2020 MAHun
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

package net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import net.fhirfactory.pegacorn.petasos.oam.metrics.collectors.EndpointMetricsAgent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.support.DefaultRegistry;
import org.apache.commons.dbcp.BasicDataSource;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantRoleSupportInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.sql.SQLClientTopologyEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPTemplate;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpointContainer;

public abstract class InteractSQLClientContentReceiverWUP extends GenericMessageBasedWUPTemplate {
	private static String DATABASE_USERNAME;
	private static String DATABASE_PASSWORD;

    private EndpointMetricsAgent endpointMetricsAgent;

    @Inject
    private ProcessingPlantRoleSupportInterface processingPlantCapabilityStatement;

    //
    // Constructor(s)
    //

    public InteractSQLClientContentReceiverWUP() {
        super();
        this.endpointMetricsAgent = null;
        
    	DATABASE_USERNAME = System.getenv("DATABASE_USERNAME");
    	DATABASE_PASSWORD = System.getenv("DATABASE_PASSWORD");
    	        
        getLogger().debug(".MessagingIngresGatewayWUP(): Entry, Default constructor");
    }

    //
    // Abstract Method Declarations
    //

    protected abstract String specifyIngresTopologyEndpointName();
    protected abstract String specifyIngresEndpointVersion();
    protected abstract String specifyEndpointParticipantName();

    //
    // Getters (and Setters)
    //

    protected EndpointMetricsAgent getEndpointMetricsAgent(){
        return(endpointMetricsAgent);
    }

    protected void setEndpointMetricsAgent(EndpointMetricsAgent agent){
        this.endpointMetricsAgent = agent;
    }

    //
    // Superclass Method Overrides/Implementations
    //

    @Override
    protected WUPArchetypeEnum specifyWUPArchetype(){
        return(WUPArchetypeEnum.WUP_NATURE_MESSAGE_EXTERNAL_INGRES_POINT);
    }

    @Override
    protected MessageBasedWUPEndpointContainer specifyEgressEndpoint(){
        getLogger().debug(".specifyEgressTopologyEndpoint(): Entry");
        MessageBasedWUPEndpointContainer egressEndpoint = new MessageBasedWUPEndpointContainer();
        egressEndpoint.setFrameworkEnabled(true);
        egressEndpoint.setEndpointSpecification(this.getNameSet().getEndPointWUPEgress());
        getLogger().debug(".specifyEgressTopologyEndpoint(): Exit");
        return(egressEndpoint);
    }

    @Override
    protected void establishEndpointMetricAgents(){
        getLogger().debug(".establishEndpointMetricAgents(): Entry");
        String connectedSystem = getSourceSystemName();
        String endpointDescription = getIngresEndpoint().getEndpointTopologyNode().getEndpointDescription();
        this.endpointMetricsAgent = getMetricAgentFactory().newEndpointMetricsAgent(
                processingPlantCapabilityStatement,
                getIngresEndpoint().getEndpointTopologyNode().getComponentID(),
                getIngresEndpoint().getEndpointTopologyNode().getParticipantName(),
                connectedSystem,
                endpointDescription);
        getLogger().debug(".establishEndpointMetricAgents(): Exit");
    }

    /**
     * The Ingres Message Gateway doesn't subscribe to ANY topics as it receives it's 
     * input from an external system.
     * 
     * @return An empty Set<TopicToken>
     */
    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        List<DataParcelManifest> subTopics = new ArrayList<>();
        return(subTopics);
    }

    /**
     * @param uri
     * @return the RouteBuilder.from(uri) with all exceptions logged but not handled
     */
    protected RouteDefinition fromInteractIngresService(String uri) {
        SourceSystemDetailInjector sourceSystemDetailInjector = new SourceSystemDetailInjector();
        PortDetailInjector portDetailInjector = new PortDetailInjector();
        RouteDefinition route = fromIncludingPetasosServices(uri);
        route
                .process(sourceSystemDetailInjector)
                .process(portDetailInjector)
        ;
        return route;
    }

    protected String getSourceSystemName(){
        getLogger().debug(".getSourceSystemName(): Entry, ingresEndpoint->{}", getIngresEndpoint());
        IPCTopologyEndpoint endpointTopologyNode = getIngresEndpoint().getEndpointTopologyNode();
        getLogger().trace(".getSourceSystemName(): endpointTopologyNode->{}", endpointTopologyNode);
        String sourceSystemName = endpointTopologyNode.getConnectedSystemName();
        getLogger().debug(".getSourceSystemName(): Exit, sourceSystemName->{}", sourceSystemName);
        return(sourceSystemName);
    }

    protected class SourceSystemDetailInjector implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            getLogger().debug("SourceSystemDetailInjector.process(): Entry");
            boolean alreadyInPlace = false;
            if(exchange.hasProperties()) {
                String sourceSystem = exchange.getProperty(PetasosPropertyConstants.WUP_INTERACT_INGRES_SOURCE_SYSTEM_NAME, String.class);
                if (sourceSystem != null) {
                    alreadyInPlace = true;
                }
            }
            if(!alreadyInPlace) {
                exchange.setProperty(PetasosPropertyConstants.WUP_INTERACT_INGRES_SOURCE_SYSTEM_NAME, getSourceSystemName());
            }
        }
    }

    //
    //
    // Route Helper Functions

    /**
     * @param uri
     * @return the RouteBuilder.from(uri) with all exceptions logged but not handled
     */
    @Override
	protected RouteDefinition fromIncludingPetasosServices(String uri) {
        NodeDetailInjector nodeDetailInjector = new NodeDetailInjector();
        AuditAgentInjector auditAgentInjector = new AuditAgentInjector();
        TaskReportAgentInjector taskReportAgentInjector = new TaskReportAgentInjector();
        RouteDefinition route = fromWithStandardExceptionHandling(uri);
        route
                .process(nodeDetailInjector)
                .process(auditAgentInjector)
                .process(taskReportAgentInjector)
        ;
        return route;
    }

    //
    // Detail Injectors for Routes
    //

    protected class PortDetailInjector implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            getLogger().debug("PortDetailInjector.process(): Entry");
            boolean alreadyInPlace = false;
            if(exchange.hasProperties()) {
                String ingresPort = exchange.getProperty(PetasosPropertyConstants.WUP_INTERACT_PORT_TYPE, String.class);
                if (ingresPort != null) {
                    alreadyInPlace = true;
                }
            }
            if (!alreadyInPlace) {
                switch(getIngresEndpoint().getEndpointTopologyNode().getEndpointType()) {
                    case SQL_CLIENT: {
                        SQLClientTopologyEndpoint sqlClientTopologyEndpoint = (SQLClientTopologyEndpoint) getIngresEndpoint().getEndpointTopologyNode();
                        exchange.setProperty(PetasosPropertyConstants.WUP_INTERACT_PORT_TYPE, sqlClientTopologyEndpoint.getEndpointType().getToken());
                        exchange.setProperty(PetasosPropertyConstants.ENDPOINT_PORT_VALUE, getIngresEndpoint().getEndpointSpecification());
                        exchange.setProperty(PetasosPropertyConstants.ENDPOINT_TOPOLOGY_NODE_EXCHANGE_PROPERTY, sqlClientTopologyEndpoint);
                        exchange.setProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, getMetricsAgent());
                        exchange.setProperty(PetasosPropertyConstants.ENDPOINT_METRICS_AGENT_EXCHANGE_PROPERTY, getEndpointMetricsAgent());
                        break;
                    }
                    default:{
                        exchange.setProperty(PetasosPropertyConstants.WUP_INTERACT_PORT_TYPE, "Undisclosed");
                        exchange.setProperty(PetasosPropertyConstants.WUP_INTERACT_PORT_TYPE, "Undisclosed");
                    }
                }

            }
        }
    }

    
    public void configure(CamelContext context) {
		SQLClientTopologyEndpoint ingresEndpoint = (SQLClientTopologyEndpoint) getIngresEndpoint().getEndpointTopologyNode();
				
		String connectionUrl = ingresEndpoint.getConnectionURL();
		String dataSourceName = ingresEndpoint.getDataSourceName();
		String driverClassName = ingresEndpoint.getDriverClassName();
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUsername(DATABASE_USERNAME);
		dataSource.setPassword(DATABASE_PASSWORD);
		dataSource.setUrl(connectionUrl);

		DefaultRegistry registry = (DefaultRegistry) context.getRegistry();
		registry.bind(dataSourceName, DataSource.class, dataSource);
		((org.apache.camel.impl.DefaultCamelContext) context).setRegistry(registry);
	}
}
