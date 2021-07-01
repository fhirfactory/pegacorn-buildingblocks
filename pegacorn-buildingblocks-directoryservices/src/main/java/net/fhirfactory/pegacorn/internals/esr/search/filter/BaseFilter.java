package net.fhirfactory.pegacorn.internals.esr.search.filter;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.search.exception.ESRFilteringException;



/**
 * Base class for all filters.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class BaseFilter {
	protected String[] values;
	
	protected abstract Logger getLogger();
    
    public abstract boolean doFilter(ExtremelySimplifiedResource searchResult) throws ESRFilteringException;
    
        
    
    public void setValue(String param) throws ResourceInvalidSearchException {
		if (param.isEmpty()) {
			throw new ResourceInvalidSearchException("Filter value is null");
		}
		
		param = StringUtils.replace(param, "[", "");
		param = StringUtils.replace(param, "]", "");
		param = StringUtils.replace(param, " ", "");
		
		String searchAttributeValueURLDecoded = URLDecoder.decode(param, StandardCharsets.UTF_8);
		
		values = searchAttributeValueURLDecoded.split(",");	    	
    }
    
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	
    	for (String value: values) {
    		if (sb.length() > 0) {
    			sb.append(",");
    		}
    		
    		sb.append(value);
    	}
    	
    	return this.getClass().getName() + " Filter values: " + sb.toString();
    }
}
