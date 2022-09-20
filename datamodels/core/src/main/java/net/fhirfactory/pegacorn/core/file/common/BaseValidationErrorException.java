package net.fhirfactory.pegacorn.core.file.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all exceptions which contains a list of validation errors.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class BaseValidationErrorException extends Exception {
    private static final long serialVersionUID = 6887470696834847855L;
    
    private List<String> validationErrors = new ArrayList<>();
    
    public BaseValidationErrorException(String message) {
        super(message);
    }
    
    public BaseValidationErrorException(String message, List<String> validationErrors) {
        this(message);
        this.validationErrors = validationErrors;
    }

    public void addValidationError(String message) {
        this.validationErrors.add(message);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (String errormessage : validationErrors) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            
            sb.append(errormessage);
        }
        
        return sb.toString();
    }   
}
