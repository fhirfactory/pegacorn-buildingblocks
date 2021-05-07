package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;


/**
 * Filters a list of practitioners based on their currently fulfilled roles.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class RoleFilter extends BaseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RoleFilter.class);
    

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public boolean doFilter(ExtremelySimplifiedResource searchResult) throws ESRFilteringException {
		
		PractitionerESR practitionerResource = (PractitionerESR)searchResult;
		
		for (String value : values) {
			if (practitionerResource.hasCurrentRole(value)) {
				return true;
			}
		}
		
		return false;
	}
}
