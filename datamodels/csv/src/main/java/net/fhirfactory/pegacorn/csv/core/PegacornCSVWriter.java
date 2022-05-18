package net.fhirfactory.pegacorn.csv.core;

import java.io.Writer;

import com.opencsv.CSVWriter;

/**
 * A custom CSV writer to enable the seperator character to be changed.
 * 
 * @author Brendan Douglas
 *
 */
public class PegacornCSVWriter extends CSVWriter {

    public PegacornCSVWriter(Writer writer, char seperator, boolean quoteText) {
        super(writer,seperator,quoteText ? DEFAULT_QUOTE_CHARACTER : NO_QUOTE_CHARACTER,DEFAULT_ESCAPE_CHARACTER,DEFAULT_LINE_END);
    }
}
