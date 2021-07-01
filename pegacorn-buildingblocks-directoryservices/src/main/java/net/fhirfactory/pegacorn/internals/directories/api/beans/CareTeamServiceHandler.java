package net.fhirfactory.pegacorn.internals.directories.api.beans;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceUpdateException;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.buildingblocks.esr.resources.CareTeamESR;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.internals.esr.brokers.CareTeamESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;

@Dependent
public class CareTeamServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(CareTeamServiceHandler.class);

    @Inject
    private CareTeamESRBroker resourceBroker;

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
    
    
    //
    // Update
    //

    public void updateCareTeam(CareTeamESR entryToUpdate, Exchange camelExchange) throws ResourceUpdateException, ResourceInvalidSearchException {
        LOG.info(".update(): Entry, inputBody --> {}", entryToUpdate);
        CareTeamESR entry = entryToUpdate;
        LOG.info(".update(): Requesting update from the Directory Resource Broker");
       
        ESRMethodOutcome outcome = resourceBroker.updateCareTeam(entry);
        LOG.info(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL) ||outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            String result = convertToJSONString(outcome.getEntry());
            LOG.info(".update(): Exit, returning updated resource");
            return;
        }
        
        LOG.info(".update(): Exit, something has gone wrong.....");
    }
}
