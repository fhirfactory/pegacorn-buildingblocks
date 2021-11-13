package net.fhirfactory.pegacorn.internals.directories.api.beans;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.model.ui.brokers.HealthcareServiceESRBroker;
import net.fhirfactory.pegacorn.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.model.ui.transactions.ESRMethodOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
