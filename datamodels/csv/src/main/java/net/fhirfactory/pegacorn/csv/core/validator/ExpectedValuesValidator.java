package net.fhirfactory.pegacorn.csv.core.validator;

import org.apache.commons.lang3.StringUtils;

/**
 * A CSV field validator to make sure the field value is one of the expected
 * values.
 * 
 * @author Brendan Douglas
 *
 */
public class ExpectedValuesValidator extends FieldValidator {
    private String[] expectedValues;

    public ExpectedValuesValidator(String field, String... expectedValues) {
        super(field);

        this.expectedValues = expectedValues;
    }

    @Override
    public boolean isValid(String value) {

        // If no value provided then assume the field is optional. If mandatory please
        // add @Mandatory annotation.
        if (StringUtils.isBlank(value)) {
            return true;
        }

        for (String expectedValue : expectedValues) {
            if (value.equals(expectedValue)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getErrorMessage() {
        return field + " contains a not expected value";
    }

}
