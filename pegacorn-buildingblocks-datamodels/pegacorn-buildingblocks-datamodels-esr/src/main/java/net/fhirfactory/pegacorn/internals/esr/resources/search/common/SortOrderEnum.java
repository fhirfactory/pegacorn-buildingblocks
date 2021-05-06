package net.fhirfactory.pegacorn.internals.esr.resources.search.common;

/**
 * Sort ordering.
 * 
 * @author Brendan Douglas
 *
 */
public enum SortOrderEnum {
	ASCENDING("ascending"),
	DESCENDING("descending");
	
	private String value;
	
	private SortOrderEnum(String value) {
		this.value = value;
	}
	
	String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return getValue();
	}
}
