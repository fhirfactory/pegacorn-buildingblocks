package net.fhirfactory.pegacorn.services.oam.agent.common;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public abstract class AgentWorkerBase extends RouteBuilder {

    abstract protected String getFriendlyName();

    @Override
    public void configure() throws Exception {
        String processingPlantName = getFriendlyName();

        from("timer://"+processingPlantName+"?delay=1000&repeatCount=1")
                .routeId("ProcessingPlant::"+processingPlantName)
                .log(LoggingLevel.DEBUG, "Starting....");
    }
}
