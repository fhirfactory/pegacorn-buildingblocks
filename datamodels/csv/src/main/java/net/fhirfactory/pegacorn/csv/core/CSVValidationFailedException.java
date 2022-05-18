package net.fhirfactory.pegacorn.csv.core;

import java.util.List;

import net.fhirfactory.pegacorn.datamodel.BaseValidationErrorException;

/**
 * An exception thrown when the CSV file fails validation.
 * 
 * @author Brendan Douglas
 *
 */
public class CSVValidationFailedException extends BaseValidationErrorException {
    private static final long serialVersionUID = 5241336302320491870L;

    public CSVValidationFailedException(String message, List<String> validationErrors) {
        super(message, validationErrors);
    }   

    
    @Override
    public String toString() {       
        return "The following validation errors were found in the CSV: " + super.toString();
    }
}
