package net.fhirfactory.pegacorn.core.file.common;

/**
 * Base class for all exceptions parsing {@link PegacornFile}
 * 
 * @author Brendan Douglas
 *
 */
public abstract class BaseFileParsingException extends Exception {
    
    private static final long serialVersionUID = 976126670117948492L;

    public BaseFileParsingException(String message) {
        super(message);
    }

    public BaseFileParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    
    @Override
    public String toString() {       
        return super.getMessage();
    }   
}
