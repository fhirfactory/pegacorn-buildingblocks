package net.fhirfactory.pegacorn.core.file.common;

/**
 * Supported file types.
 * 
 * @author Brendan Douglas
 *
 */
public enum FileType {
    CSV("CSV"),
    XML("XML"),
    PDF("PDF"),
    TEXT("TXT");
    
    private String displayName;
    
    FileType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
