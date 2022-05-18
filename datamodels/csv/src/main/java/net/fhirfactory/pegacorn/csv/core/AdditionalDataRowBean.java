package net.fhirfactory.pegacorn.csv.core;

/**
 * An additional data row bean. Additional data fields do not get written to the
 * final CSV.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class AdditionalDataRowBean extends CSVRowBean {

    public AdditionalDataRowBean() {
        rowData[0] = "A";
    }

    public String getRecordType() {
        return rowData[0];
    }
}
