package net.fhirfactory.pegacorn.internals.esr.resources.datatypes;

/**
 * A care team participant
 * 
 * @author Brendan Douglas
 *
 */
public class ParticipantESDT {
	private String simplifiedID;
	
	public ParticipantESDT() {}
	
	public ParticipantESDT(String simplifiedId) {
		this.simplifiedID = simplifiedId;
	}

	public String getSimplifiedID() {
		return simplifiedID;
	}

	public void setSimplifiedID(String simplifiedID) {
		this.simplifiedID = simplifiedID;
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
		
		return true;
	}
}
