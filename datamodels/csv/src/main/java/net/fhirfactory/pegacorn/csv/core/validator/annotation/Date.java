package net.fhirfactory.pegacorn.csv.core.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.fhirfactory.pegacorn.datamodel.DateUtils;

/**
 * An annotation to use when CSV date validation is required.
 * 
 * @author Brendan Douglas
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Date {
    public String value() default DateUtils.DD_MMM_YY;
}
