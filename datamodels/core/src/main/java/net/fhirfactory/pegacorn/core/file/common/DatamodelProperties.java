package net.fhirfactory.pegacorn.core.file.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import net.fhirfactory.pegacorn.util.PegacornProperties;

/**
 * Properties used in CSV files, mappings etc.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class DatamodelProperties extends PegacornProperties {
    private static Properties appProp = new Properties();

    public DatamodelProperties(String filename) {
       
        if (appProp.size() == 0) {

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    DatamodelProperties.class.getClassLoader().getResourceAsStream(filename), StandardCharsets.UTF_8))) {
                appProp.load(reader);
            } catch (Exception e) {
                throw new RuntimeException("Unable to load classification mapping file.  Filname: " + filename, e);
            }   
        }
    }
    
    public String get(String key) {
        return appProp.getProperty(key);
    }
    
    public boolean exists(String key) {
        return appProp.getProperty(key) != null;
    }
}
