package net.fhirfactory.pegacorn.internals.directories.api.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;

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
