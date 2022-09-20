package net.fhirfactory.pegacorn.core.file.common;

import java.io.File;
import java.io.IOException;

/**
 * Base class for all file implementations used in Aether.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class PegacornFile {
    
    // This will prevent duplicate filename timestamps being generated.
    protected static UniqueFilenameTimestampGenerator fileNameTimestampGenerator = new UniqueFilenameTimestampGenerator();
    
    protected String filename;
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Returns the filename without the folder.
     * 
     * @return
     */
    public String getFilenameEndPath() {
        int indexOfLastSlash = getFilename().lastIndexOf(File.separatorChar);

        if (indexOfLastSlash == -1) {
            return getFilename();
        }

        return getFilename().substring(indexOfLastSlash + 1);
    }

    /**
     * Returns the file path without the file name.
     * 
     * @return
     */
    public String getFilePath() {
        int indexOfLastSlash = getFilename().lastIndexOf(File.separatorChar);

        if (indexOfLastSlash == -1) {
            return getFilename();
        }

        return getFilename().substring(0, indexOfLastSlash);
    }
    
    
    /**
     * Returns this file object as base 64 encoded.
     * 
     * @return
     * @throws Exception
     */
    public abstract String getAsBase64Encoded() throws Exception;
    
    
    /**
     * Writes the content of this object to file.
     * 
     * @param baseDirectory
     * @throws IOException
     */
    public abstract void write(String baseDirectory) throws IOException;
    
    
    /**
     * A readable name for this file.
     * 
     * @return
     */
    public abstract String getFileDisplayName();
    
    
}
