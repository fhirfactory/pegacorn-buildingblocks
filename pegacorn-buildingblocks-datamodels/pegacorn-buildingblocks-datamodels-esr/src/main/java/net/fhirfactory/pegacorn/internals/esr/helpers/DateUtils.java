package net.fhirfactory.pegacorn.internals.esr.helpers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for converting and formatting dates.
 * 
 * @author Brendan Douglas
 *
 */
public class DateUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    
    public static final String YYYY_MM_DD_T_HH_MM_SS_INPUT = "yyyy-MM-dd'T'HH:mm:ss";
       
    public static final String DEFAULT_TIME_ZONE = "Australia/Sydney";

    /**
     * Converts a {@link String} representation of a timestamp to a {@link Date}
     * using the supplied input format.
     * 
     * @param inputTimestamp
     * @param inputFormat
     * @return
     */
    public static Date getDate(String timestamp, DateTimeFormatter timestampFormatter) {
        Date date = null;
        if (StringUtils.isNotBlank(timestamp)) {
            LocalDateTime localDateTime = LocalDateTime.parse(timestamp, timestampFormatter);
            date = getDateFromLocalDateTime(localDateTime);
        }
        logger.debug("getDate({}, {})={}", timestamp, timestampFormatter, date);
        return date;
    }

    public static String getFormattedInstantWithTimeZone(Instant instant, DateTimeFormatter timestampFormatter) {
        String dataUntilTimestampStr = convertInstantToDefaultTimeZone(instant).format(timestampFormatter);
        int indexThirdLastCharacter = dataUntilTimestampStr.length() - 3;
        if (dataUntilTimestampStr.charAt(indexThirdLastCharacter) != ':') {
            dataUntilTimestampStr = dataUntilTimestampStr.substring(0, indexThirdLastCharacter + 1) + ":" + 
                    dataUntilTimestampStr.substring(indexThirdLastCharacter + 1);
        }
        return dataUntilTimestampStr;
    }
        
    public static Date getDateFrom(String timestamp, DateTimeFormatter timestampFormatter) {
        if (StringUtils.isBlank(timestamp)) {
            return new Date(2999 - 1900, 12 - 1, 31, 23, 59, 59);
        }

        Date date = getDateFromZonedDateTime(ZonedDateTime.parse(timestamp, timestampFormatter));
        logger.debug("getDateFrom({})={}", timestamp, date);
        return date;
    }

    public static Date getDateFromZonedDateTime(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date getDateFromLocalDateTime(LocalDateTime localDateTime) {
        return getDateFromZonedDateTime(localDateTime.atZone(getZoneId()));
    }

    private static ZoneId getZoneId() {
//      return ZoneId.systemDefault(); // Returned GMT
      return ZoneId.of(DEFAULT_TIME_ZONE);
    }
    
    private static ZonedDateTime convertInstantToDefaultTimeZone(Instant instant) {
        ZonedDateTime zdt = instant.atZone(getZoneId());
        logger.trace("convertInstantToDefaultTimeZone instant={}, zdt={}", instant, zdt.toString());
        return zdt;
    }

    private static ZonedDateTime convertDateToDefaultZonedDateTime(Date date) {
        return convertInstantToDefaultTimeZone(date.toInstant());
    }
        
    /**
     * Converts a {@link Date} to a Date in the {@link DEFAULT_TIME_ZONE}
     * 
     * @param date
     * @return
     */
    public static Date convertDateToDefaultTimeZone(Date date) {
        ZonedDateTime zdt = convertDateToDefaultZonedDateTime(date);
        Date result = getDateFromZonedDateTime(zdt);
        logger.debug("convertDateToDefaultTimeZone({})={}", date, result);
        return result;
    }

    /**
     * Converts a {@link Date} to a string using the supplied output format.
     * 
     * @param inputDate
     * @param outputFormat
     * @return
     */
    public static String format(Date inputDate, DateTimeFormatter outputFormat) {
        String result = null; 
        if (inputDate != null) {
            ZonedDateTime zdt = convertDateToDefaultZonedDateTime(inputDate);
            LocalDateTime ldt = zdt.toLocalDateTime();
            result = ldt.format(outputFormat);
        }
        logger.debug("format({}, {})={}", inputDate, outputFormat, result);
        return result;
    }

    /**
     * Formats a {@link String} representation of a timestamp from the input format
     * to the output format
     * 
     * @param inputTimestamp
     * @param inputFormat
     * @param outputFormat
     * @return
     */
    public static String format(String inputTimestamp, DateTimeFormatter inputFormat, DateTimeFormatter outputFormat) {
        Date date = getDate(inputTimestamp, inputFormat);
        return format(date, outputFormat);
    }

    /**
     * Validates the provided timestamp against the supplied formatter.
     * 
     * @param timestamp
     * @param formatter
     * @return
     */
    public static boolean isValidTimestamp(String timestamp, DateTimeFormatter formatter) {
        try {
            ZonedDateTime.parse(timestamp, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }
    
    /**
     * Returns true if the from timestamp is before the to timestamp.
     * 
     * @param from
     * @param to
     * @param formatter
     * @return
     */
    public static boolean isFromBeforeTo(String from, String to, DateTimeFormatter formatter) {
        ZonedDateTime fromTimestamp = ZonedDateTime.parse(from, formatter);
        ZonedDateTime toTimestamp = ZonedDateTime.parse(to, formatter);

        return (fromTimestamp.isBefore(toTimestamp));
    }
    
    
    public static ZonedDateTime getCurrentZoneDateTime() {
        return convertInstantToDefaultTimeZone(Instant.now());
    }
}