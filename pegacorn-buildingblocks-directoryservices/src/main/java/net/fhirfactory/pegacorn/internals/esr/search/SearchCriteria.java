package net.fhirfactory.pegacorn.internals.esr.search;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The search criteria and any filters.  Filters are just additional search criteria.
 * 
 * @author Brendan Douglas
 *
 */
public class SearchCriteria {
    private static final Logger LOG = LoggerFactory.getLogger(SearchCriteria.class);

	
	private SearchParamTypes paramName;
	private String value;
	
	private boolean containsMatch;
	
	public SearchCriteria() {
		containsMatch = true; 
	}
	
	public SearchCriteria(SearchParamTypes paramName, String value) {
		this();
		
		this.paramName = paramName;
		setValue(value);
	}
	
	public SearchCriteria(SearchParamTypes paramName, String value, boolean containsMatch) {
		this(paramName, value);
		
		this.containsMatch = containsMatch;
	}
	
	public SearchParamTypes getParamName() {
		return paramName;
	}
	
	public void setParamName(SearchParamTypes paramName) {
		this.paramName = paramName;
	}
	
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		String searchAttributeValueURLDecoded = URLDecoder.decode(value, StandardCharsets.UTF_8);
		
		this.value = searchAttributeValueURLDecoded;
	}
	
	
	public boolean isParamNameNull() {
		return (paramName == null);
	}
	
	public boolean isValueNull() {
		return (value == null);
	}
	
	public boolean isValueEmpty() {
		return (value.isEmpty());
	}
	
	public boolean isContainsMatch() {
		return containsMatch;
	}

	public void setContainsMatch(boolean containsMatch) {
		this.containsMatch = containsMatch;
	}
}
