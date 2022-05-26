package net.fhirfactory.pegacorn.internals.directories.api.beans;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.core.model.ui.brokers.RoleESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class RoleServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(RoleServiceHandler.class);

    @Inject
    private RoleESRBroker roleBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (roleBroker);
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }
}
