package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.HealthcareServiceESR;


/**
 * Filters a list of health care service search results.  A search result is accepted if the practitioners has a health care service favourite matching the value.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class PractitionerHealthCareServiceFavouriteFilter extends BasePractitionerFavouriteFilter {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerHealthCareServiceFavouriteFilter.class);

    @Override
	protected Logger getLogger() {
		return LOG;
	}

    
    @Override
	public List<String> getFavouriteList() {
		return practitioner.getHealthcareServiceFavourites().getFavourites();
	}
    
    
	@Override
	public boolean filter(ExtremelySimplifiedResource searchResult, List<String> favourites) {
		HealthcareServiceESR healthCareServiceResource = (HealthcareServiceESR)searchResult;
		
		if (favourites.contains(healthCareServiceResource.getSimplifiedID())) {
			return true;
		}
		
		return false;
	} 
}
