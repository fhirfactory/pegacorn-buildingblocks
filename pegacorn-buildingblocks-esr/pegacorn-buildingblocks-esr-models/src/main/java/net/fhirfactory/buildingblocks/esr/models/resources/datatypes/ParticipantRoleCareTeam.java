package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

public class ParticipantRoleCareTeam {
	private String name;
	private ParticipantTypeEnum role;
	
	public String getName() {
		return name;
	}
	
	public ParticipantRoleCareTeam() {
		
	}
	
	public ParticipantRoleCareTeam(String name, ParticipantTypeEnum role) {
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		ParticipantRoleCareTeam other = (ParticipantRoleCareTeam)obj;
		
		if (!name.equals(other.getName())) {
			return false;
		}
		
		if (!role.name().equals(other.getRole().name())) {
			return false;
		}
		
		return true;
	}

}
