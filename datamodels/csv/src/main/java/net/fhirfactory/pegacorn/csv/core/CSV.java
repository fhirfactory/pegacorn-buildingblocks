package net.fhirfactory.pegacorn.csv.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvValidationException;

import net.fhirfactory.pegacorn.core.file.common.PegacornFile;
import net.fhirfactory.pegacorn.csv.core.validator.FileValidator;


/**
 * Base class for all classes which need to read and write CSV. The CSV data can
 * either be read from a file or a String and can be written to a file or
 * returned as a String.
 * 
 * Validators can be configured in the {@link CSVRowBean} implementations.
 * 
 * headings are optional but if they are required then an implementing CSV class needs to implement {@link HasHeaderRow}
 * 
 * @author Brendan Douglas
 *
 */
public abstract class CSV extends PegacornFile {
    private static final char DEFAULT_DELIMITER = ',';
    
    protected List<CSVRowBean> rows = new ArrayList<>();
    private List<String> validationErrors = new ArrayList<>();
    protected String[] headings; // Optional headings.

    /**
     * Creates an empty CSV object
     */
    public CSV() {

    }

    /**
     * Constructs this CSV object from a file.
     * 
     * @param csvFile
     * @throws CSVParsingException
     * @throws IOException
     */
    public CSV(File csvFile) throws CSVParsingException, IOException {
        filename = csvFile.getAbsolutePath();
        
        CSVParser csvParser = getCSVParser();
        CSVReader csvReader = null;

        try {
            csvReader = new CSVReaderBuilder(new FileReader(csvFile))
                    .withSkipLines(0)
                    .withCSVParser(csvParser)
                    .build();
            
            
            processReadRow(csvReader);
        } catch (CsvValidationException e) {
            throw new CSVParsingException("Unable to generate CSV file from file: " + filename, e);
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
        }
    }

    /**
     * Constructs this CSV object from a csv string.
     * 
     * @param csvString
     * @throws CSVParsingException
     * @throws IOException
     */
    public CSV(String csvString) throws CSVParsingException, IOException {
        
        CSVParser csvParser = getCSVParser();
        CSVReader csvReader = null;
        
        try {
            csvReader = new CSVReaderBuilder(new StringReader(csvString))
                    .withSkipLines(0)
                    .withCSVParser(csvParser)
                    .build();
            
            processReadRow(csvReader);
        } catch (CsvValidationException e) {
            throw new CSVParsingException("Unable to generate CSV file from string", e);
        } finally {
            if (csvReader != null) {
                csvReader.close();
            }
        }
    }
    
    
    private CSVParser getCSVParser() {
        return new CSVParserBuilder()
                .withSeparator(getDelimiter())
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_QUOTES)
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreQuotations(false)
                .withStrictQuotes(false)
                .build();
    }
    
    
    private void processReadRow(CSVReader reader) throws CSVParsingException, IOException, CsvValidationException {
        String[] nextLine;
        
        boolean firstRow = true;
        
        while ((nextLine = reader.readNext()) != null) {     
            if (firstRow && this instanceof HasHeaderRow) {
                headings = nextLine;
                
                if (this instanceof HasHeaderRow) {
                    String[] expectedHeadings = ((HasHeaderRow)this).getRequiredHeadings();
                    
                    if (expectedHeadings.length != nextLine.length) {
                        throw new CSVParsingException("The number of header values read from the file (" + nextLine.length + ") does not match the expected number (" + expectedHeadings.length + ")"); 
                    }
                    
                    // Now we know the number of columns matches it is now time to compare the values.
                    for (int i = 0; i < nextLine.length; i++) {
                        if (!nextLine[i].equalsIgnoreCase(expectedHeadings[i])) {
                            throw new CSVParsingException("The header value read from the file (" + nextLine[i] + ") does not match the expected value (" + expectedHeadings[i] + ") at position (" + i + ")");
                        }
                    }
                }
            } else {
                this.rows.add(convertToRowBean(nextLine));
            }
            
            firstRow = false;
        }     
    }
    
    
    public CSV(String csvString, String filename) throws CSVParsingException, IOException {
        this(csvString);
        
        this.filename = filename;
    }

    /**
     * Validates this CSV object and throws a {@link CSVValidationFailedException}
     * if validation errors occur.
     * 
     * @throws CSVParsingException
     * @throws CSVValidationFailedException
     */
    public void validate() throws CSVParsingException, CSVValidationFailedException {
        if (rows.isEmpty()) {
            throw new CSVParsingException("The CSV must contain at least 1 row");
        }
        
        
        for (int i = 0; i < rows.size(); i++) {
            CSVRowBean row = rows.get(i);
            
            if (!row.validate()) {

                for (String error : row.getValidationErrors()) {
                    validationErrors.add("Row:" + (i + 1) + " - " + error);
                }
            }
        }

        // Throw an exception with the list of validation error messages. This is better
        // than returning true or false as it
        // requires the caller to either handle or rethrow the exception. If returning
        // true or false the caller could just ignore.
        if (!validationErrors.isEmpty()) {
            throw new CSVValidationFailedException("An error has occured validating the csv file", validationErrors);
        }

        // If we get here the fields in the file have been validated so now we can
        // perform additional validation on multiple rows.
        for (FileValidator validator : getFileValidators()) {
            if (!validator.isValid(rows)) {
                validationErrors.addAll(validator.getErrorMessages());
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new CSVValidationFailedException("An error has occured validating the csv file", validationErrors);
        }
    }
    
    
    

    /**
     * Writes this csv object to file.
     * 
     * @param baseDirectory
     * @throws IOException
     */
    @Override
    public void write(String baseDirectory) throws IOException {
        File file = new File(baseDirectory + File.separator + this.getFilename());

        try (PegacornCSVWriter writer = new PegacornCSVWriter(new FileWriter(file), getDelimiter(), quoteText())) {
            
            if (this instanceof HasHeaderRow) {
                writer.writeNext(headings);
            }

            for (CSVRowBean row : rows) {
                if (!(row instanceof AdditionalDataRowBean)) { // Ignore the additional data when writing the final CSV.
                    writer.writeNext(row.getRowData());
                }
            }
        }
    }

    /**
     * Returns this CSV object as a String.
     * 
     * @return
     * @throws IOException
     */
    public String writeToString() throws IOException {
        StringWriter st = new StringWriter();

        try (PegacornCSVWriter writer = new PegacornCSVWriter(st, getDelimiter(), quoteText())) {
            if (this instanceof HasHeaderRow) {
                writer.writeNext(headings);
            }
            
            for (CSVRowBean row : rows) {
                writer.writeNext(row.getRowData());
            }
        }

        return st.getBuffer().toString();
    }

    /**
     * Adds a new row to this CSV file.
     * 
     * @param row
     */
    public void addRow(CSVRowBean row) {
        this.rows.add(row);
    }

    /**
     * Returns a single row from this CSV object.
     * 
     * @param rowNumber
     * @return
     */
    public CSVRowBean getRow(int rowNumber) {
        return this.rows.get(rowNumber);
    }

    /**
     * Returns all the rows from this CSV object.
     * 
     * @return
     */
    public List<CSVRowBean> getRows() {
        return this.rows;
    }

    /**
     * Converts a String array into a {@link CSVRowBean}.
     * 
     * @param row
     * @return
     * @throws CSVParsingException
     */
    protected abstract CSVRowBean convertToRowBean(String[] row) throws CSVParsingException;

    
    /**
     * Returns all the validation error messages.
     * 
     * @return
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }
    
    
    /**
     * Gets the configured validators for this CSV.
     * 
     * @return
     */
    public List<FileValidator> getFileValidators() {
        return new ArrayList<>();
    }

    
    /**
     * Base 64 encodes the PDF bytes.
     * 
     * @return
     */
    @Override
    public String getAsBase64Encoded() throws Exception {
        return Base64.getEncoder().encodeToString(writeToString().getBytes());      
    }
    
    
    /**
     * The delimiter to use when reading and writing this type of CSV.
     * 
     * @return
     */
    protected char getDelimiter() {
        return DEFAULT_DELIMITER; 
    }

    
    protected boolean quoteText() {
        return true;
    }

    @Override
    public abstract String getFileDisplayName();

    @Override
    public String toString()  {
        try {
            return this.writeToString();
        } catch(IOException e) {
            return "Error generating CSV toString()";
        }
    }
    
    
    
}
