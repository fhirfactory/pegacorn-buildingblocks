package net.fhirfactory.pegacorn.internals.esr.resources.search.filter.practitioner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;

/**
 * Filters a list of practitioners based on their currently fulfilled roles.
 * 
 * @author Brendan Douglas
 *
 */
public class RoleFilter extends BaseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RoleFilter.class);
    
    public RoleFilter(String param) throws ResourceInvalidSearchException {
		super(param);
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public boolean doFilter(ExtremelySimplifiedResource searchResult) {
		
		PractitionerESR practitionerResource = (PractitionerESR)searchResult;
		
		for (String value : values) {
			if (practitionerResource.hasCurrentRole(value)) {
				return true;
			}
		}
		
		return false;
	}
}
