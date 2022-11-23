/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.archetypes;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.common.BasePetasosContainerRoute;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks.*;
import net.fhirfactory.pegacorn.petasos.oam.metrics.collectors.WorkUnitProcessorMetricsAgent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mark A. Hunter
 * @since 2020-07-1
 */

public class StandardWUPContainerRoute extends BasePetasosContainerRoute {
	private static final Logger LOG = LoggerFactory.getLogger(StandardWUPContainerRoute.class);
    protected Logger getLogger(){
        return(LOG);
    }

	private WorkUnitProcessorSoftwareComponent wupTopologyNode;
	private RouteElementNames nameSet;
	private WorkUnitProcessorMetricsAgent metricsAgent;

	//
	// Constructor(s)
	//

	public StandardWUPContainerRoute(CamelContext camelCTX, WorkUnitProcessorSoftwareComponent wupTopologyNode, PetasosFulfillmentTaskAuditServicesBroker auditTrailBroker, WorkUnitProcessorMetricsAgent metricsAgent) {
		super(camelCTX, auditTrailBroker);
		getLogger().debug(".StandardWUPContainerRoute(): Entry, context --> ###, wupNode --> {}", wupTopologyNode);
		this.wupTopologyNode = wupTopologyNode;
		this.nameSet = new RouteElementNames(wupTopologyNode.getNodeFunctionFDN().getFunctionToken());
		this.metricsAgent = metricsAgent;
	}

	public StandardWUPContainerRoute(CamelContext camelCTX, WorkUnitProcessorSoftwareComponent wupTopologyNode, PetasosFulfillmentTaskAuditServicesBroker auditTrailBroker, boolean requiresDirect, String sedaParameters, WorkUnitProcessorMetricsAgent metricsAgent) {
		super(camelCTX, auditTrailBroker);
		getLogger().debug(".StandardWUPContainerRoute(): Entry, context --> ###, wupNode --> {}", wupTopologyNode);
		this.wupTopologyNode = wupTopologyNode;
		this.nameSet = new RouteElementNames(wupTopologyNode.getNodeFunctionFDN().getFunctionToken(), requiresDirect, sedaParameters);
		this.metricsAgent = metricsAgent;
	}

	//
	// Business Methods (Routes)
	//

	@Override
	public void configure() {
		getLogger().debug(".configure(): Entry!, for wupNode --> {}", this.wupTopologyNode);
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPContainerIngresProcessorIngres --> {}", nameSet.getEndPointWUPContainerIngresProcessorIngres());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPContainerIngresProcessorEgress --> {}", nameSet.getEndPointWUPContainerIngresProcessorEgress());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPContainerIngresGatekeeperIngres --> {}", nameSet.getEndPointWUPContainerIngresGatekeeperIngres());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPIngresConduitIngres --> {}", nameSet.getEndPointWUPIngresConduitIngres());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPIngres --> {}", nameSet.getEndPointWUPIngres());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPEgress --> {}", nameSet.getEndPointWUPEgress());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPEgressConduitEgress --> {}", nameSet.getEndPointWUPEgressConduitEgress());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPContainerEgressProcessorIngres --> {}", nameSet.getEndPointWUPContainerEgressProcessorIngres());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPContainerEgressProcessorEgress --> {}", nameSet.getEndPointWUPContainerEgressProcessorEgress());
		getLogger().debug("StandardWUPContainerRoute :: EndPointWUPContainerEgressGatekeeperIngres --> {}", nameSet.getEndPointWUPContainerEgressGatekeeperIngres());

		specifyCamelExecutionExceptionHandler();

		NodeDetailInjector nodeDetailInjector = new NodeDetailInjector();

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerIngresProcessorIngres())
				.routeId(nameSet.getRouteWUPContainerIngressProcessor())
				.log(LoggingLevel.DEBUG, "Processing Task->${body}")
				.process(nodeDetailInjector)
				.bean(WUPContainerIngresProcessor.class, "ingresContentProcessor(*, Exchange)")
				.to(nameSet.getEndPointWUPContainerIngresProcessorEgress());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerIngresProcessorEgress())
				.routeId(nameSet.getRouteIngresProcessorEgress2IngresGatekeeperIngres())
				.to(nameSet.getEndPointWUPContainerIngresGatekeeperIngres());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerIngresGatekeeperIngres())
				.routeId(nameSet.getRouteWUPContainerIngresGateway())
				.process(nodeDetailInjector)
				.bean(WUPContainerIngresGatekeeper.class, "ingresGatekeeper(*, Exchange)");

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPIngresConduitIngres())
				.routeId(nameSet.getRouteIngresConduitIngres2WUPIngres())
				.process(nodeDetailInjector)
				.bean(WUPIngresConduit.class, "forwardIntoWUP(*, Exchange)")
				.to(nameSet.getEndPointWUPIngres());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPEgress())
				.routeId(nameSet.getRouteWUPEgress2WUPEgressConduitEgress())
				.process(nodeDetailInjector)
				.bean(WUPEgressConduit.class, "receiveFromWUP(*, Exchange)")
				.to( nameSet.getEndPointWUPEgressConduitEgress());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPEgressConduitEgress())
				.routeId(nameSet.getRouteWUPEgressConduitEgress2WUPEgressProcessorIngres())
				.to(nameSet.getEndPointWUPContainerEgressProcessorIngres());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerEgressProcessorIngres())
				.routeId(nameSet.getRouteWUPContainerEgressProcessor())
				.process(nodeDetailInjector)
				.bean(WUPContainerEgressProcessor.class, "egressContentProcessor(*, Exchange)")
				.to(nameSet.getEndPointWUPContainerEgressProcessorEgress());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerEgressProcessorEgress())
				.routeId(nameSet.getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres())
				.to(nameSet.getEndPointWUPContainerEgressGatekeeperIngres());

		fromWithStandardExceptionHandling(nameSet.getEndPointWUPContainerEgressGatekeeperIngres())
				.routeId(nameSet.getRouteWUPContainerEgressGateway())
				.process(nodeDetailInjector)
				.bean(WUPContainerEgressGatekeeper.class, "egressGatekeeper(*, Exchange)")
				.to(PetasosPropertyConstants.TASK_OUTCOME_COLLECTION_QUEUE);

	}

	//
	// Content Injectors
	//

	protected class NodeDetailInjector implements Processor {
		@Override
		public void process(Exchange exchange) throws Exception {
			getLogger().debug("NodeDetailInjector.process(): Entry");
			boolean alreadyInPlace = false;
			if(exchange.hasProperties()) {
				WorkUnitProcessorSoftwareComponent wupTN = exchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorSoftwareComponent.class);
				if (wupTN != null) {
					alreadyInPlace = true;
				}
			}
			if(!alreadyInPlace) {
				exchange.setProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, getWupTopologyNode());
			}
			exchange.setProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, getMetricsAgent());
		}
	}

	//
	// Getters (and Setters)
	//

	public WorkUnitProcessorSoftwareComponent getWupTopologyNode() {
		return wupTopologyNode;
	}

	protected WorkUnitProcessorMetricsAgent getMetricsAgent(){
		return(this.metricsAgent);
	}

	protected RouteElementNames getNameSet() {
		return nameSet;
	}
}
