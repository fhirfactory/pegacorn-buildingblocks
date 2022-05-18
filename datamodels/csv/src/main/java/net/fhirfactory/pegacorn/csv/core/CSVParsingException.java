package net.fhirfactory.pegacorn.csv.core;

import net.fhirfactory.pegacorn.datamodel.BaseFileParsingException;

/**
 * An exception thrown when the CSV cannot be processed. This exception is not
 * thrown due to validation errors. {@link CSVValidationFailedException} is
 * thrown when validation fails.
 * 
 * @author Brendan Douglas
 *
 */
public class CSVParsingException extends BaseFileParsingException {

    private static final long serialVersionUID = 253784633892224585L;

    public CSVParsingException(String message) {
        super(message);
    }

    public CSVParsingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String toString() {       
        return "The CSV cannot be parsed: " + super.toString();
    }
}
