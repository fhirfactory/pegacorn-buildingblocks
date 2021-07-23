package net.fhirfactory.pegacorn.platform.edge.itops.apiservices.transactions;

import net.fhirfactory.pegacorn.platform.edge.itops.apiservices.transactions.common.PetasosITOpsMethodOutcome;
import net.fhirfactory.pegacorn.platform.edge.itops.apiservices.transactions.common.PetasosITOpsMethodOutcomeEnum;
import net.fhirfactory.pegacorn.platform.edge.itops.apiservices.transactions.exceptions.ResourceInvalidSearchException;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;

public class EndpointStatusHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EndpointStatusHandler.class);


    protected Logger getLogger(){
        return(LOG);
    }
    //
    // Review
    //

    protected PetasosITOpsMethodOutcome getResource(@Header("id") String id) throws ResourceInvalidSearchException {
        getLogger().info(".getEntry(): Entry, pathValue --> {}", id);
        PetasosITOpsMethodOutcome outcome = new PetasosITOpsMethodOutcome();
        outcome.setEntry("OK");
        outcome.setStatus(PetasosITOpsMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
        outcome.setDate(Date.from(Instant.now()));
        getLogger().info(".getEntry(): Exit, outcome --> {}", outcome.getStatus());
        return(outcome);
    }
}
