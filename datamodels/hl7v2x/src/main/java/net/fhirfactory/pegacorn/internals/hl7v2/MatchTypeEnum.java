package net.fhirfactory.pegacorn.internals.hl7v2;

/**
 * The type of match to perform.
 * 
 * @author Brendan Douglas
 *
 */
public enum MatchTypeEnum {
	EQUALS("equals"),
	CONTAINS("contains"),
	STARTS_WITH("starts-with"),
	ENDS_WITH("ends-with"),
	NOT_EQUALS("not-equals"),
	NOT_CONTAINS("not-contains"),
	NOT_STARTS_WITH("not-starts-with"),
	NOT_ENDS_WITH("not-ends-with");
	
	private String type;
	
	MatchTypeEnum(String type) {
		this.type = type;
	}
	
	
	public String getType() {
		return type;
	}
	
	
	public static MatchTypeEnum get(String type) {
		for (MatchTypeEnum matchType : values()) {
			if (matchType.type.equalsIgnoreCase(type)) {
				return matchType;
			}
		}
		
		return EQUALS; // The default is equals.
	}
}
