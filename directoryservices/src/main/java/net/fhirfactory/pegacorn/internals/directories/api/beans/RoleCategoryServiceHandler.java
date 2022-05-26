package net.fhirfactory.pegacorn.internals.directories.api.beans;

import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.core.model.internal.brokers.RoleCategoryESRBroker;
import net.fhirfactory.pegacorn.core.model.internal.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class RoleCategoryServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(RoleCategoryServiceHandler.class);

    @Inject
    private RoleCategoryESRBroker roleCategoryBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (roleCategoryBroker);
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }
}
