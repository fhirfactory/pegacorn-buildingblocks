package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

import java.util.ArrayList;
import java.util.List;

public class PractitionerRoleCareTeamListESDT {
	List<PractitionerRoleCareTeam>careTeams;
	
	public PractitionerRoleCareTeamListESDT() {
		this.careTeams = new ArrayList<>();
	}

	public List<PractitionerRoleCareTeam> getCareTeams() {
		return careTeams;
	}

	public void setCareTeams(List<PractitionerRoleCareTeam> careTeams) {
		this.careTeams = careTeams;
	}
}
