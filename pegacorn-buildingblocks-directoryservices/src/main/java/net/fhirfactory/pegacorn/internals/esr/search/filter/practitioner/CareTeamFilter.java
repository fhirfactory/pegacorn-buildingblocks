package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;

@ApplicationScoped
public class CareTeamFilter extends BaseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(CareTeamFilter.class);
    
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public boolean doFilter(ExtremelySimplifiedResource searchResult) throws ESRFilteringException {
		return true;
	}
}
