package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

import java.util.ArrayList;
import java.util.List;

public class PractitionerRoleCareTeamListESDT {
	List<String>careTeams;
	
	public PractitionerRoleCareTeamListESDT() {
		this.careTeams = new ArrayList<>();
	}

	public List<String> getCareTeams() {
		return careTeams;
	}

	public void setCareTeams(List<String> careTeams) {
		this.careTeams = careTeams;
	}
}