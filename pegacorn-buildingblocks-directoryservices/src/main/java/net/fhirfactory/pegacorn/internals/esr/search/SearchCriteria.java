package net.fhirfactory.pegacorn.internals.esr.search;

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

    private SearchParam searchParam;
	private boolean containsMatch;
	
	public SearchCriteria() {
		containsMatch = true; 
	}
	
	public SearchCriteria(SearchParam searchParam) {
		this();
		
		this.searchParam = searchParam;
	}

	public SearchCriteria(SearchParam searchParam, boolean containsMatch) {
		this(searchParam);
		
		this.containsMatch = containsMatch;
	}

	
	public SearchParam getSearchParam() {
        return searchParam;
    }

    public void setSearchParam(SearchParam searchParam) {
        this.searchParam = searchParam;
    }

    
    public boolean isParamNameNull() {
		return (searchParam == null) || searchParam.getName() == null;
	}
	
	public boolean isValueNull() {
		return (searchParam == null || searchParam.getValue() == null);
	}
	
	public boolean isValueEmpty() {
		return (searchParam.getValue().isEmpty());
	}
	
	public boolean isContainsMatch() {
		return containsMatch;
	}

	public void setContainsMatch(boolean containsMatch) {
		this.containsMatch = containsMatch;
	}
}
