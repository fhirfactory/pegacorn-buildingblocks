package net.fhirfactory.pegacorn.csv.core.validator;

import org.apache.commons.lang3.StringUtils;

/**
 * A CSV validator to make sure a field value is supplied.
 * 
 * @author Brendan Douglas
 *
 */
public class MandatoryValidator extends FieldValidator {

    public MandatoryValidator(String field) {
        super(field);
    }

    @Override
    public boolean isValid(String value) {
        return !StringUtils.isBlank(value);
    }

    @Override
    public String getErrorMessage() {
        return field + " is mandatory";
    }
}
