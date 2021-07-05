package net.fhirfactory.pegacorn.internals.esr.search;

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
	
	
    public static SortOrderEnum get(String searchValue) {
        for (SortOrderEnum type : values()) {
            if (type.getValue().equalsIgnoreCase(searchValue)) {
                return type;
            }
        }
        
        return ASCENDING;
    }
}
