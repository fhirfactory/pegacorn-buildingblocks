package net.fhirfactory.pegacorn.csv.core;

/**
 * CSV classes need to implement this interface if the CSV has headings.
 * 
 * @author Brendan Douglas
 *
 */
public interface HasHeaderRow {
    String[] getRequiredHeadings();
}
