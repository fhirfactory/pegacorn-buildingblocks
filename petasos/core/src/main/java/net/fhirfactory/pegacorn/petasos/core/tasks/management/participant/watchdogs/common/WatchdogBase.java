package net.fhirfactory.pegacorn.petasos.core.tasks.management.participant.watchdogs.common;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public abstract class WatchdogBase extends RouteBuilder {

    //
    // Constructor(s)
    //

    public WatchdogBase(){
        super();
    }

    //
    // Class Kickstarter
    //

    @Override
    public void configure() throws Exception {
        String name = getClass().getSimpleName();

        from("timer://"+name+"?delay=1000&repeatCount=1")
                .routeId(getClass().getName())
                .log(LoggingLevel.DEBUG, "Starting....");
    }
}
