package net.fhirfactory.buildingblocks.esr.resources.datatypes;

public enum OrganisationStructureElementType {
	DIVISION("Division"),
	BRANCH("Branch"),
	SECTION("Section"),
	SUB_SECTION("Sub-Section"),
	BUSINESS_UNIT("Business Unit");
	
	private String value;

	
	OrganisationStructureElementType(String value) {
		this.value = value;
	}

	
	public String getValue() {
		return value;
	}

	
	public String toString() {
		return getValue();
	}
}