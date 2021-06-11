package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;


/**
 * Filters a list of practitioner search results.  A search result is accepted if the practitioner has a practitioner favourite matching the value.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class PractitionerOtherPractitionerFavouriteFilter extends BasePractitionerFavouriteFilter {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerOtherPractitionerFavouriteFilter.class);

    @Override
	protected Logger getLogger() {
		return LOG;
	}

    
    @Override
	public List<String> getFavouriteList() {
		return practitioner.getPractitionerFavourites().getFavourites();
	}
    
    
	@Override
	public boolean filter(ExtremelySimplifiedResource searchResult, List<String> favourites) {
		PractitionerESR practitionerResource = (PractitionerESR)searchResult;
		
		if (favourites.contains(practitionerResource.getSimplifiedID())) {
			return true;
		}
		
		return false;
	} 
}
