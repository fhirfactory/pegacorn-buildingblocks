package net.fhirfactory.pegacorn.internals.directories.api.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.LocationESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;

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
