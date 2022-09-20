package net.fhirfactory.pegacorn.csv.core.validator;

import org.apache.commons.lang3.StringUtils;

/**
 * A CSV field validator to make sure a field storing a money amount is the
 * correct format.
 * 
 * @author Brendan Douglas
 *
 */
public class MoneyValidator extends FieldValidator {
    private MoneyTypeEnum type;
    private boolean invalidType;

    public MoneyValidator(String field, MoneyTypeEnum type) {
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
            Double moneyAmount = Double.parseDouble(value);
            if (type == MoneyTypeEnum.ANY) {
                return true;
            }

            if (moneyAmount > 0 && type == MoneyTypeEnum.NEGATIVE) { // Allow 0
                return false;
            }

            if (moneyAmount < 0 && type == MoneyTypeEnum.POSITIVE) { // Allow 0
                return false;
            }

        } catch (NumberFormatException e) {
            invalidType = true;
            return false;
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        if (invalidType) {
            return field + " is an invalid money amount";
        } else {
            return field + " must be: " + type;
        }
    }
}
