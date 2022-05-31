package net.fhirfactory.pegacorn.core.file.common;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will return a unique timestamp for the file prefix.
 * 
 * @author Brendan Douglas
 */
public class UniqueFilenameTimestampGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(UniqueFilenameTimestampGenerator.class);
    
    /**
     * This map will contain the last timestamp generated for a file prefix.
     */
    private static ConcurrentHashMap<String, ZonedDateTime> filenameTimestampMap = new ConcurrentHashMap<>();

    /**
     * NOTE: this function currently assumes the file name only contains the timestamp for the second.
     * If a different time resolution was required this function would need to be updated.
     */
    public ZonedDateTime getUniqueTimestamp(String fileNamePrefix) {
               
        while (true) {   // Just continue looping until a unique timestamp is generated.         
            // As the file name is currently assumed to only contain the seconds, truncate to that ChronoUnit.
            // If other timestamp truncations are required in the file name, then this code would need to 
            // change and the filenameTimestampMap updated to consider the ChronoUnit
            ZonedDateTime currentZoneDateTime = DateUtils.getCurrentZoneDateTime().truncatedTo(ChronoUnit.SECONDS);
            
            ZonedDateTime previousTimestamp = filenameTimestampMap.put(fileNamePrefix, currentZoneDateTime);
                                    
            if (previousTimestamp == null || (! previousTimestamp.equals(currentZoneDateTime))) {
                LOG.info("Generated timestamp for fileNamePrefix {} is: {}", fileNamePrefix, currentZoneDateTime);
                
                deleteEntriesOlderThan(currentZoneDateTime);
                
                return currentZoneDateTime;
            } else {
                LOG.info("Duplicate filename timestamp detected.  fileNamePrefix: {}, timestamp: {}", fileNamePrefix, previousTimestamp);
                
                try {
                    Thread.sleep(1000);   // Wait for the ChronoUnit (e.g. 1 second) and then try again.
                } catch(Exception e) {
                    // Just ignore and try again without sleeping.
                }
            }
        }
    }
        
    private void deleteEntriesOlderThan(ZonedDateTime zoneDateTime) {      
        // Remove entries in the map which are before the specified zoneDateTime
        filenameTimestampMap.entrySet().removeIf(e -> e.getValue().isBefore(zoneDateTime));  
    }
}
