package net.fhirfactory.pegacorn.internals.esr.resources.search.filter.practitioner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;

public class RoleCategoryFilter extends BaseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RoleCategoryFilter.class);
    
    public RoleCategoryFilter(String param) throws ResourceInvalidSearchException {
		super(param);
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public boolean doFilter(ExtremelySimplifiedResource searchResult) {
		return true;
	}
}
