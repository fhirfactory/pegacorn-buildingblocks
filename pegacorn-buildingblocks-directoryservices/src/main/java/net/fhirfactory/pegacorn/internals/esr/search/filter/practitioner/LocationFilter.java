package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.LocationESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.RoleCategoryESR;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.brokers.LocationESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;


/**
 * A practitioner location filter.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class LocationFilter extends BaseFilter {	
    private static final Logger LOG = LoggerFactory.getLogger(LocationFilter.class);
    
    @Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;
    
    @Inject
    private LocationESRBroker locationBroker;
    
    

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public boolean doFilter(ExtremelySimplifiedResource searchResult) throws ESRFilteringException {
		
		try {
			PractitionerESR practitioner = (PractitionerESR)searchResult;
			
			if (practitioner.getCurrentPractitionerRoles().isEmpty()) {
				return false;
			}
			
			for (String role : practitioner.getCurrentPractitionerRoles()) {
			
				// get the practitioner role.
				ESRMethodOutcome practitionerRoleOutcome =  practitionerRoleBroker.getResource(role.toLowerCase());
				PractitionerRoleESR practitionerRole = (PractitionerRoleESR)practitionerRoleOutcome.getEntry();
			
				if (practitionerRole == null) {
					throw new ESRFilteringException("practitioner role resource missing for role: " + role.toLowerCase());
				}
			
				// if we are here we have got the practitioner role resource so now get the location.
				String locationId = practitionerRole.getPrimaryLocationID();
				ESRMethodOutcome locationOutcome = locationBroker.getResource(locationId.toLowerCase());
			
				if (locationOutcome == null) {
					throw new ESRFilteringException("location resource missing for locationId: " + locationId);
				}
			
				LocationESR location = (LocationESR)locationOutcome.getEntry();
				
				for (String value : values) {
					
					if (value.equalsIgnoreCase(location.getDisplayName())) {
						return true;
					} 
				}
			}
		} catch (Exception e) {
			throw new ESRFilteringException("Error filtering", e);
		}
		
		return false;
	}
}
