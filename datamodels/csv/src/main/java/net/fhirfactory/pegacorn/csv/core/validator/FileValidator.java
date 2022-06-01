package net.fhirfactory.pegacorn.csv.core.validator;

import java.util.List;

import net.fhirfactory.pegacorn.csv.core.CSVRowBean;

/**
 * Base class for all csv validators with validate mutliple (or all) rows within
 * a file. field.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class FileValidator {

    /**
     * Is the supplied value valid for this validator.
     * 
     * @param rows
     * @return
     */
    public abstract boolean isValid(List<CSVRowBean> rows);

    /**
     * Get the error message if the validation failed.
     * 
     * @return
     */
    public abstract List<String> getErrorMessages();
}
