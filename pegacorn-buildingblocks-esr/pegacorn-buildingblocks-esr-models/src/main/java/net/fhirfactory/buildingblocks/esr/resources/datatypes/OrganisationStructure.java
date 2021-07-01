package net.fhirfactory.buildingblocks.esr.resources.datatypes;

public class OrganisationStructure {
	private int index;
	private String type;
	private String value;
	
	
	public int getIndex() {
		return index;
	}
	
	
	public void setIndex(int index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}
	
	
	public void setType(String type) {
		this.type = type;
	}

	
	public String getValue() {
		return value;
	}
	
	
	public void setValue(String value) {
		this.value = value;
	}
}
