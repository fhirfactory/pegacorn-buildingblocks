package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

import java.util.ArrayList;
import java.util.List;

public class PractitionerRoleCareTeamListESDT {
	List<ParticipantRoleCareTeam>careTeams;
	
	public PractitionerRoleCareTeamListESDT() {
		this.careTeams = new ArrayList<>();
	}

	public List<ParticipantRoleCareTeam> getCareTeams() {
		return careTeams;
	}

	public void setCareTeams(List<ParticipantRoleCareTeam> careTeams) {
		this.careTeams = careTeams;
	}
}
