package net.fhirfactory.pegacorn.csv.core.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;

/**
 * A CSV field validator to make sure the field value is a date in the correct
 * format.
 * 
 * @author Brendan Douglas
 */
public class DateValidator extends FieldValidator {
    private String dateFormat;

    public DateValidator(String field, String dateFormat) {
        super(field);

        this.dateFormat = dateFormat;
    }

    @Override
    public boolean isValid(String value) {

        // If no value provided then assume the field is optional. If mandatory please
        // add @Mandatory annotation.
        if (StringUtils.isBlank(value)) {
            return true;
        }

        return isValidDateFormat(value, dateFormat);
    }

    @Override
    public String getErrorMessage() {
        return field + " has an incorrect date format.  Required format is: " + dateFormat;
    }

    private boolean isValidDateFormat(String date, String format) {
        try {
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            builder.parseCaseInsensitive();
            builder.appendPattern(dateFormat);

            DateTimeFormatter formatter = builder.toFormatter();
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }
}
