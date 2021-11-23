package net.fhirfactory.pegacorn.internals.directories.api.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.api.beans.common.HandlerBase;
import net.fhirfactory.pegacorn.core.model.ui.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PractitionerESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.FavouriteListESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.PractitionerRoleListESDT;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.pegacorn.core.model.ui.transactions.exceptions.ResourceUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.constant;

@Dependent
public class PractitionerServiceHandler extends HandlerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerServiceHandler.class);

    @Inject
    private PractitionerESRBroker practitionerDirectoryResourceBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected ESRBroker specifyResourceBroker() {
        return (practitionerDirectoryResourceBroker);
    }

    //
    // Update
    //

    public String updatePractitioner(String inputBody,  Exchange camelExchange)
            throws ResourceUpdateException, ResourceInvalidSearchException {
        getLogger().debug(".update(): Entry, inputBody --> {}", inputBody);
        PractitionerESR entry = null;
        try{
            getLogger().debug(".update(): Attempting to parse Resource");
            JsonMapper jsonMapper = new JsonMapper();
            entry = jsonMapper.readValue(inputBody, PractitionerESR.class);
            getLogger().debug(".update(): Resource parsing successful");
        } catch (JsonMappingException mappingException) {
            throw(new ResourceUpdateException("Unable to parse (map) message, error --> " + mappingException.getMessage()));
        } catch (JsonProcessingException processingException) {
            throw(new ResourceUpdateException("Unable to process message, error --> " + processingException.getMessage()));
        }
        getLogger().debug(".update(): Requesting update from the Directory Resource Broker");
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitioner(entry);
        getLogger().debug(".update(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
            getLogger().debug(".update(): Exit, returning updated resource");
            return(result);
        }
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
            String result = convertToJSONString(outcome.getEntry());
            camelExchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));
            getLogger().debug(".update(): Exit, returning updated resource (after creating it)");
            return(result);
        }
        getLogger().debug(".update(): Exit, something has gone wrong.....");
        return("Hmmm... not good!");
    }

    public PractitionerRoleListESDT getPractitionerRoles(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerRoles(): Entry, pathValue --> {}", id);
        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        if (outcome.getEntry() != null) {
            PractitionerRoleListESDT output = new PractitionerRoleListESDT();
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            output.getPractitionerRoles().addAll(practitioner.getCurrentPractitionerRoles());
            getLogger().debug(".getPractitionerRoles(): Exit, PractitionerRoles found, returning them");
            return (output);
        } else {
            getLogger().debug(".getPractitionerRoles(): Exit, No PractitionerRoles found");
            return (new PractitionerRoleListESDT());
        }
    }

    public PractitionerESR updatePractitionerRoles(@Header("simplifiedID") String id, PractitionerRoleListESDT practitionerRoles) throws ResourceInvalidSearchException, ResourceUpdateException {
        getLogger().debug(".updatePractitionerRoles(): Entry, simplifiedID->{}, practitionerRoles->{}", id, practitionerRoles);
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updatePractitionerRoles(id, practitionerRoles);
        getLogger().trace(".updatePractitionerRoles(): Directory Resource Broker has finished update, outcome --> {}", outcome.getStatus());
        if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)){
            ESRMethodOutcome updatedOutcome = getResourceBroker().getResource(id.toLowerCase());
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            getLogger().debug(".updatePractitionerRoles(): Exit, returning updated resource");
            return(practitioner);
        } else {
            getLogger().error(".updatePractitionerRoles(): Could not update Resource");
            throw(new ResourceUpdateException(outcome.getStatusReason()) );
        }
    }

    public FavouriteListESDT getPractitionerRoleFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerRoleFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, "PractitionerRoleFavourites");
        getLogger().debug(".getPractitionerRoleFavourites(): Exit");
        return (output);
    }

    public FavouriteListESDT getPractitionerFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getPractitionerFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, "PractitionerFavourites");
        getLogger().debug(".getPractitionerFavourites(): Exit");
        return (output);
    }

    public FavouriteListESDT getServiceFavourites(@Header("simplifiedID") String id) throws ResourceInvalidSearchException {
        getLogger().debug(".getServiceFavourites(): Entry, pathValue --> {}", id);
        FavouriteListESDT output = getFavourites(id, "ServiceFavourites");
        getLogger().debug(".getServiceFavourites(): Exit");
        return (output);
    }

    private FavouriteListESDT getFavourites(String id, String favouriteType) throws ResourceInvalidSearchException {
        getLogger().debug(".getFavourites(): Entry, id->{}, favouriteType->{}", id, favouriteType);
        ESRMethodOutcome outcome = getResourceBroker().getResource(id.toLowerCase());
        if (outcome.getEntry() != null) {
            FavouriteListESDT output = new FavouriteListESDT();
            PractitionerESR practitioner = (PractitionerESR) outcome.getEntry();
            switch(favouriteType){
                case "PractitionerRoleFavourites":{
                    output.getFavourites().addAll(practitioner.getPractitionerRoleFavourites().getFavourites());
                    break;
                }
                case "PractitionerFavourites":{
                    output.getFavourites().addAll(practitioner.getPractitionerFavourites().getFavourites());
                    break;
                }
                case "ServiceFavourites":{
                    output.getFavourites().addAll(practitioner.getHealthcareServiceFavourites().getFavourites());
                    break;
                }
                default:{
                    // do nothing (and return an empty set)
                }
            }
            getLogger().debug(".getFavourites(): Exit, Favourites found, returning them");
            return (output);
        } else {
            getLogger().debug(".getFavourites(): Exit, No Favourites found");
            return (new FavouriteListESDT());
        }
    }

    public PractitionerESR updatePractitionerRoleFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerRoleFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, "PractitionerRoleFavourites", newFavourites);
        getLogger().debug(".updatePractitionerRoleFavourites(): Exit");
        return (output);
    }

    public PractitionerESR updatePractitionerFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updatePractitionerFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, "PractitionerFavourites", newFavourites);
        getLogger().debug(".updatePractitionerFavourites(): Exit");
        return (output);
    }

    public PractitionerESR updateServiceFavourites(@Header("simplifiedID") String id, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".updateServiceFavourites(): Entry, id->{}, newFavourites->{}", id, newFavourites);
        PractitionerESR output = updateFavourites(id, "ServiceFavourites", newFavourites);
        getLogger().debug(".updateServiceFavourites(): Exit");
        return (output);
    }

    private PractitionerESR updateFavourites(String id, String favouriteType, FavouriteListESDT newFavourites) throws ResourceInvalidSearchException {
        getLogger().debug(".getFavourites(): Entry, id->{}, favouriteType->{}", id, favouriteType);
        ESRMethodOutcome outcome = practitionerDirectoryResourceBroker.updateFavourites(id, favouriteType, newFavourites);
        if (outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL)) {
            getLogger().debug(".getFavourites(): Exit, Favourites found, returning them");
            PractitionerESR updatedPractitioner = (PractitionerESR) getResource(id).getEntry();
            return (updatedPractitioner);
        } else {
            getLogger().debug(".getFavourites(): Exit, No Favourites found");
            return (null);
        }
    }

    @Override
    protected void printOutcome(ESRMethodOutcome outcome) {

    }
}
