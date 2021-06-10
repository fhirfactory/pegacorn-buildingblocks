package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.RoleCategoryESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.RoleHistoryDetail;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleCategoryESRBroker;
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

	@Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;
	
	@Inject
    private RoleCategoryESRBroker roleCategoryBroker;
	
    private static final Logger LOG = LoggerFactory.getLogger(RoleCategoryFilter.class);
    

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public boolean doFilter(ExtremelySimplifiedResource searchResult) throws ESRFilteringException  {
		
		try {
			PractitionerESR practitioner = (PractitionerESR)searchResult;
			
			if (practitioner.getCurrentPractitionerRoles().isEmpty()) {
				return false;
			}
			
			for (RoleHistoryDetail roleHistoryDetail : practitioner.getRoleHistory().getAllCurrentRoles()) {
				String role = roleHistoryDetail.getRole();
			
				// get the practitioner role.
				ESRMethodOutcome practitionerRoleOutcome =  practitionerRoleBroker.getResource(role.toLowerCase());
				PractitionerRoleESR practitionerRole = (PractitionerRoleESR)practitionerRoleOutcome.getEntry();
			
				if (practitionerRole == null) {
					throw new ESRFilteringException("practitioner role resource missing for role: " + role.toLowerCase());
				}
			
				// if we are here we have got the practitioner role resource so now get the role category.
				String roleCategoryId = practitionerRole.getPrimaryRoleCategoryID();
				ESRMethodOutcome roleCategoryOutcome = roleCategoryBroker.getResource(roleCategoryId.toLowerCase());
			
				if (roleCategoryOutcome == null) {
					throw new ESRFilteringException("role category resource missing for roleCategoryId: " + roleCategoryId);
				}
			
				RoleCategoryESR roleCategory = (RoleCategoryESR)roleCategoryOutcome.getEntry();
				
					
				for (String value : values) {
					
					if (value.equalsIgnoreCase(roleCategory.getDisplayName())) {
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
