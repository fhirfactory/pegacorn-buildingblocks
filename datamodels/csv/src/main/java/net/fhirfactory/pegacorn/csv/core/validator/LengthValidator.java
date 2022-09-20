package net.fhirfactory.pegacorn.csv.core.validator;

import org.apache.commons.lang3.StringUtils;

/**
 * A CSV field validator to make sure the length of the field is >= min and <=
 * max.
 * 
 * @author Brendan Douglas
 *
 */
public class LengthValidator extends FieldValidator {
    private int min;
    private int max;

    private boolean minLengthValueSet = false;

    public LengthValidator(String field, int min, int max) {
        super(field);

        minLengthValueSet = min != -1;

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(String value) {

        // If no value provided then assume the field is optional. If mandatory please
        // add @Mandatory annotation.
        if (StringUtils.isBlank(value)) {
            return true;
        }

        if (!minLengthValueSet) {
            return value.length() <= max;
        }

        return value.length() >= min && value.length() <= max;
    }

    @Override
    public String getErrorMessage() {
        if (!minLengthValueSet) {
            return field + " length invalid.  Length must be: Maximum " + max + " characters";
        }

        return field + " length invalid.  Length must be: " + min + " to " + max + " characters";
    }
}
