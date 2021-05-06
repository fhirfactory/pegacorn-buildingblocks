package net.fhirfactory.pegacorn.internals.esr.resources.search.common;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.resources.search.filter.BaseFilter;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;

/**
 * The search criteria and any filters.  Filters are just additional search criteria.
 * 
 * @author Brendan Douglas
 *
 */
public class SearchCriteria {
    private static final Logger LOG = LoggerFactory.getLogger(SearchCriteria.class);

	
	private String paramName;
	private String value;
	
	private List<BaseFilter> filters = new ArrayList<>();
	
	public SearchCriteria() {
		// 
	}
	
	public SearchCriteria(String paramName, String value) {
		this.paramName = paramName;
		setValue(value);
	}
	
	public String getParamName() {
		return paramName;
	}
	
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public List<BaseFilter> getFilters() {
		return filters;
	}
	
	public void setFilters(List<BaseFilter> filters) {
		this.filters = filters;
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
	
	
	public void addFilter(BaseFilter filter) throws ResourceInvalidSearchException {
		this.filters.add(filter);	
	}
}
