package net.fhirfactory.pegacorn.csv.core.validator;

import org.apache.commons.lang3.StringUtils;

/**
 * A CSV field validator to make sure a number is the correct format.
 * 
 * @author Brendan Douglas
 *
 */
public class NumberValidator extends FieldValidator {
    private Class<? extends Number> type;
    private boolean incorrectType = false;

    public NumberValidator(String field, Class<? extends Number> type) {
        super(field);

        this.type = type;

    }

    @Override
    public boolean isValid(String value) {

        // If no value provided then assume the field is optional. If mandatory please
        // add @Mandatory annotation.
        if (StringUtils.isBlank(value)) {
            return true;
        }

        try {
            if (type.equals(Integer.class)) {
                Integer.parseInt(value);
            } else if (type.equals(Double.class)) {
                Double.parseDouble(value);
            } else if (type.equals(Float.class)) {
                Float.parseFloat(value);
            } else if (type.equals(Short.class)) {
                Short.parseShort(value);
            } else if (type.equals(Long.class)) {
                Long.parseLong(value);
            } else {
                incorrectType = true;
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        if (!incorrectType) {
            return field + " is an invalid number. The number should be: " + type.getSimpleName();
        } else {
            return field + " has a not supported number type: " + type.getSimpleName();
        }
    }
}
