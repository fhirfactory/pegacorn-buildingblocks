package net.fhirfactory.pegacorn.csv.core.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.fhirfactory.pegacorn.datamodel.DatamodelProperties;

/**
 * An annotation to use when a field in the CSV needs to contains one of the
 * specified values.
 * 
 * @author Brendan Douglas
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Properties {
    public Class<? extends DatamodelProperties> type();
}
