package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

/**
 * A care team participant
 * 
 * @author Brendan Douglas
 *
 */
public class ParticipantESDT {
	private String simplifiedId;
	private ParticipantTypeEnum participantType;
	
	public ParticipantESDT() {}
	
	public ParticipantESDT(String simplifiedId,ParticipantTypeEnum participantType) {
		this.simplifiedId = simplifiedId;
		this.participantType = participantType;
	}

	public String getSimplifiedId() {
		return simplifiedId;
	}

	public void setSimplifiedId(String simplifiedId) {
		this.simplifiedId = simplifiedId;
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
		
		if (!simplifiedId.equals(other.getSimplifiedId())) {
			return false;
		}
		
		if (!participantType.name().equals(other.getParticipantType().name())) {
			return false;
		}
		
		return true;
	}
}
