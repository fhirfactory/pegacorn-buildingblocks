package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;


/**
 * Base class for all practitioner favourite filters.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public abstract class BasePractitionerFavouriteFilter extends BaseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(BasePractitionerFavouriteFilter.class);
    
    @Inject
    protected PractitionerESRBroker practitionerBroker;
    
    protected PractitionerESR practitioner;
    
	
	@Override
	public boolean doFilter(ExtremelySimplifiedResource searchResult) throws ESRFilteringException {
	
		try {
			if (practitioner == null) {
				ESRMethodOutcome outcome = practitionerBroker.getResource(values[0].toLowerCase());
				
				if (outcome.getStatus().equals(ESRMethodOutcomeEnum.REVIEW_ENTRY_NOT_FOUND)) {
					return false;
				}
				
				practitioner = (PractitionerESR)outcome.getEntry();
			}
			
			return filter(searchResult, getFavouriteList());
		} catch(Exception e) {
			throw new ESRFilteringException("Unable to filter", e);
		}
	}
	
	
	public abstract List<String>getFavouriteList();
	
	
	public abstract boolean filter(ExtremelySimplifiedResource searchResult, List<String>favourites);
}
