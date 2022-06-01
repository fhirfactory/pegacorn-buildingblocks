package net.fhirfactory.pegacorn.csv.core.validator;

/**
 * Base class for all csv validators with validate a single field.
 * 
 * 
 * @author Brendan Douglas
 *
 */
public abstract class FieldValidator {
    protected String field;

    public FieldValidator(String field) {
        this.field = field;
    }

    /**
     * Is the supplied value valid for this validator.
     * 
     * @param value
     * @return
     */
    public abstract boolean isValid(String value);

    public String getField() {
        return field;
    }

    /**
     * Get the error message if the validation failed.
     * 
     * @return
     */
    public abstract String getErrorMessage();
}
