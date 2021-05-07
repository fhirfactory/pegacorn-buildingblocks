package net.fhirfactory.pegacorn.internals.directories.api.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.HealthcareServiceESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;

@Dependent
public class HealthcareServiceServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(HealthcareServiceServiceHandler.class);

    @Inject
    private HealthcareServiceESRBroker resourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (resourceBroker);
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }
}
