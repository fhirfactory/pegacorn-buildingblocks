package net.fhirfactory.pegacorn.internals.directories.api.beans;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.core.model.internal.brokers.LocationESRBroker;
import net.fhirfactory.pegacorn.core.model.internal.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class LocationServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(LocationServiceHandler.class);

    @Inject
    private LocationESRBroker locationBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (locationBroker);
    }

    //
    // Review / Get
    //

    //
    // Search
    //


    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }
}
