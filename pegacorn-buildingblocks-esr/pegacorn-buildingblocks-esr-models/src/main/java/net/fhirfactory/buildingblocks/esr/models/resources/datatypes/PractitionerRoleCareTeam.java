package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

public class PractitionerRoleCareTeam {
	private String name;
	private ParticipantTypeEnum role;
	
	public String getName() {
		return name;
	}
	
	public PractitionerRoleCareTeam() {
		
	}
	
	public PractitionerRoleCareTeam(String name, ParticipantTypeEnum role) {
		this.name = name;
		this.role = role;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ParticipantTypeEnum getRole() {
		return role;
	}
	
	public void setRole(ParticipantTypeEnum role) {
		this.role = role;
	}
}
