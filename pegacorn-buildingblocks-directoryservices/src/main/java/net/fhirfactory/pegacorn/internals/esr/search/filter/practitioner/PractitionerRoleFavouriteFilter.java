package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerRoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;


/**
 * Filters a list of practitioner role search results  A search result is accepted if the practitioner has a role favourite matching the value.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class PractitionerRoleFavouriteFilter extends BasePractitionerFavouriteFilter {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleFavouriteFilter.class);

    @Override
	protected Logger getLogger() {
		return LOG;
	}

    
    @Override
	public List<String> getFavouriteList() {
		return practitioner.getPractitionerRoleFavourites().getFavourites();
	}


	@Override
	public boolean filter(ExtremelySimplifiedResource searchResult, List<String> favourites) {
		PractitionerRoleESR practitionerRoleResource = (PractitionerRoleESR)searchResult;
		
		if (favourites.contains(practitionerRoleResource.getSimplifiedID())) {
			return true;
		}
		
		return false;
	} 
}
