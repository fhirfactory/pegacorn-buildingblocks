package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

public class OrganisationStructure {
	private int index;
	private OrganisationStructureElementType type;
	private String value;
	
	
	public int getIndex() {
		return index;
	}
	
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	
	public OrganisationStructureElementType getType() {
		return type;
	}
	
	
	public void setType(OrganisationStructureElementType type) {
		this.type = type;
	}
	
	
	public String getValue() {
		return value;
	}
	
	
	public void setValue(String value) {
		this.value = value;
	}
}
