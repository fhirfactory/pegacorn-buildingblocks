package net.fhirfactory.pegacorn.internals.esr.search.filter.practitionerrole;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;

/**
 * A practitioner role category filter.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class RoleCategoryFilter extends BaseFilter {
	
    private static final Logger LOG = LoggerFactory.getLogger(RoleCategoryFilter.class);
    

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public boolean doFilter(ExtremelySimplifiedResource searchResult) throws ESRFilteringException  {
		
		try {
			PractitionerRoleESR practitionerRole = (PractitionerRoleESR)searchResult;

			for (String value : values) {
				
				if (value.equalsIgnoreCase(practitionerRole.getPrimaryRoleCategoryID())) {
					return true;
				} 
			}
		} catch (Exception e) {
			throw new ESRFilteringException("Error filtering", e);
		}
		
		return false;
	}
}
