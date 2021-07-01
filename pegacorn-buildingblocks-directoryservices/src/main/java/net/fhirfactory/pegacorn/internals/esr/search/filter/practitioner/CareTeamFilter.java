package net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.RoleHistoryDetail;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;
import net.fhirfactory.pegacorn.internals.esr.search.filter.BaseFilter;

/**
 * A practitioner care team filter.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class CareTeamFilter extends BaseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(CareTeamFilter.class);
    
	@Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;
    
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
			
			for (RoleHistoryDetail roleHistoryDetail : practitioner.getRoleHistory().getAllCurrentRoles()) {
				String role = roleHistoryDetail.getRole();
			
				// get the practitioner role.
				ESRMethodOutcome practitionerRoleOutcome =  practitionerRoleBroker.getResource(role.toLowerCase());
				PractitionerRoleESR practitionerRole = (PractitionerRoleESR)practitionerRoleOutcome.getEntry();
			
				if (practitionerRole == null) {
					throw new ESRFilteringException("practitioner role resource missing for role: " + role.toLowerCase());
				}
			
				// Compare the filter values against all the care teams the practitioner role is currently in.
				for (String careTeam : practitionerRole.getCareTeams()) {
					for (String value : values) {
						
						if (value.equalsIgnoreCase(careTeam)) {
							return true;
						} 
					}					
				}
			}
		} catch (Exception e) {
			throw new ESRFilteringException("Error filtering", e);
		}
		
		return false;
	}
}
