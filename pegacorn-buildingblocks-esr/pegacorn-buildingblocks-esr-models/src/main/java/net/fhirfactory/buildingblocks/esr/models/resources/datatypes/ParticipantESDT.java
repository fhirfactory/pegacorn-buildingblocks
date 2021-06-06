package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

/**
 * A care team participant
 * 
 * @author Brendan Douglas
 *
 */
public class ParticipantESDT {
	private String simplifiedID;
	private ParticipantTypeEnum participantType;
	
	public ParticipantESDT() {}
	
	public ParticipantESDT(String simplifiedId,ParticipantTypeEnum participantType) {
		this.simplifiedID = simplifiedId;
		this.participantType = participantType;
	}

	public String getSimplifiedID() {
		return simplifiedID;
	}

	public void setSimplifiedID(String simplifiedID) {
		this.simplifiedID = simplifiedID;
	}

	public ParticipantTypeEnum getParticipantType() {
		return participantType;
	}

	public void setParticipantType(ParticipantTypeEnum participantType) {
		this.participantType = participantType;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		ParticipantESDT other = (ParticipantESDT)obj;
		
		if (!simplifiedID.equals(other.getSimplifiedID())) {
			return false;
		}
		
		if (!participantType.name().equals(other.getParticipantType().name())) {
			return false;
		}
		
		return true;
	}
}
