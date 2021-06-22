package net.fhirfactory.pegacorn.internals.esr.search;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class SearchParam {
    private SearchParamNames name;
    private String value;
    
    public SearchParam(SearchParamNames name, String value) {
        this.name = name;
        this.value = value;
    }
    
    
    public SearchParamNames getName() {
        return name;
    }
    
    
    public void setName(SearchParamNames name) {
        this.name = name;
    }
    
    
    public String getValue() {
        return value;
    }
    
    
    public void setValue(String value) {
        String searchAttributeValueURLDecoded = URLDecoder.decode(value, StandardCharsets.UTF_8);
        this.value = searchAttributeValueURLDecoded;
    }
}
