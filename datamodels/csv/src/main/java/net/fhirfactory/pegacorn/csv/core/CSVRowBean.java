package net.fhirfactory.pegacorn.csv.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.fhirfactory.pegacorn.csv.core.validator.DateValidator;
import net.fhirfactory.pegacorn.csv.core.validator.ExpectedValuesValidator;
import net.fhirfactory.pegacorn.csv.core.validator.FieldValidator;
import net.fhirfactory.pegacorn.csv.core.validator.LengthValidator;
import net.fhirfactory.pegacorn.csv.core.validator.MandatoryValidator;
import net.fhirfactory.pegacorn.csv.core.validator.MoneyValidator;
import net.fhirfactory.pegacorn.csv.core.validator.NumberValidator;
import net.fhirfactory.pegacorn.csv.core.validator.PropertiesValidator;
import net.fhirfactory.pegacorn.csv.core.validator.annotation.Date;
import net.fhirfactory.pegacorn.csv.core.validator.annotation.ExpectedValues;
import net.fhirfactory.pegacorn.csv.core.validator.annotation.Length;
import net.fhirfactory.pegacorn.csv.core.validator.annotation.Mandatory;
import net.fhirfactory.pegacorn.csv.core.validator.annotation.Money;
import net.fhirfactory.pegacorn.csv.core.validator.annotation.Number;
import net.fhirfactory.pegacorn.csv.core.validator.annotation.Properties;

/**
 * Base class for all CSV row beans. A row bean stores the data for each row
 * from the CSV file and enables the user of getters and setters to access the
 * data.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class CSVRowBean {
    protected String[] rowData;
    protected List<String> validationErrors = new ArrayList<>();
    protected List<FieldValidator> validators = new ArrayList<>();

    public CSVRowBean() {
        rowData = new String[getNumberOfColumns()];
    }

    public CSVRowBean(String[] rowData) throws CSVParsingException {
        this();

        // Makes sure the number of columns read from the file is not greater than the
        // number allowed for the row.
        if (rowData.length != getNumberOfColumns()) {
            throw new CSVParsingException(
                    "The number of columns read from the file (" + rowData.length + ") does not match the expected number (" + getNumberOfColumns() + ")");
        }

        for (int i = 0; i < rowData.length; i++) {
            this.rowData[i] = rowData[i].trim();
        }
    }

    /**
     * Validates the row.
     * 
     * @return
     * @throws CSVParsingException
     */
    public boolean validate() throws CSVParsingException {
        Class<? extends Annotation>[] supportedAnnotationClasses = new Class[] { Mandatory.class, Length.class, Date.class, ExpectedValues.class, Number.class,
                Money.class,Properties.class };

        try {
            List<Method> methods = Arrays.asList(this.getClass().getDeclaredMethods());
            Collections.sort(methods, new MethodComparator());

            for (Method method : methods) {

                method.setAccessible(true);

                for (Class<? extends Annotation> supportedAnnotationClass : supportedAnnotationClasses) {
                    if (method.isAnnotationPresent(supportedAnnotationClass)) {
                        Annotation annotation = method.getAnnotation(supportedAnnotationClass);

                        String fieldName = method.getName().substring(3); // This is the method name after the get part.
                        FieldValidator validator = null;

                        if (annotation instanceof Mandatory) {
                            validator = new MandatoryValidator(fieldName);
                        } else if (annotation instanceof Length) {
                            Length length = (Length) annotation;
                            validator = new LengthValidator(fieldName, length.min(), length.max());
                        } else if (annotation instanceof Date) {
                            validator = new DateValidator(fieldName, ((Date) annotation).value());
                        } else if (annotation instanceof ExpectedValues) {
                            validator = new ExpectedValuesValidator(fieldName, ((ExpectedValues) annotation).values());
                        } else if (annotation instanceof net.fhirfactory.pegacorn.csv.core.validator.annotation.Number) {
                            validator = new NumberValidator(fieldName, ((net.fhirfactory.pegacorn.csv.core.validator.annotation.Number) annotation).type());
                        } else if (annotation instanceof Money) {
                            validator = new MoneyValidator(fieldName, ((Money) annotation).type());
                        } else if (annotation instanceof Properties) {
                            validator = new PropertiesValidator(fieldName, ((Properties)annotation).type());
                        } else {
                            throw new CSVParsingException("Unknown annotation on field: " + fieldName);
                        }

                        if (validator != null) {
                            String value = (String) method.invoke(this);

                            if (!validator.isValid(value)) {
                                validationErrors.add(validator.getErrorMessage());
                                break; // break so we only get 1 error per field.
                            }
                        }
                    }
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new CSVParsingException("Error validating the CSV file", e);
        }

        return validationErrors.size() == 0;
    }

    public String[] getRowData() {
        return rowData;
    }

    /**
     * Returns the maximum number of columns allowed for this row bean.
     * 
     * @return
     */
    protected abstract int getNumberOfColumns();

    /**
     * Returns a list of validation errors or an empty list if none.
     * 
     * @return
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }

    private class MethodComparator implements Comparator<Method> {

        @Override
        public int compare(Method thisMethod, Method otherMethod) {
            return thisMethod.getName().compareTo(otherMethod.getName());
        }
    }

}