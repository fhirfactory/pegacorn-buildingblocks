package net.fhirfactory.pegacorn.core.file.common;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

import org.hl7.fhir.r4.model.Period;

/**
 * Class containing methods to create a document reference period.
 * 
 * @author Brendan Douglas
 *
 */
public class PeriodFactory {

    /**
     * Returns a default period.
     * 
     * @return
     */
    public static Period getDefaultPeriod() {
        Date defaultStart = Date.from(Instant.now());
        Period period = getPeriod(defaultStart);

        return period;

    }

    /**
     * Returns a period with a custom date range.
     * 
     * @param start
     * @param end
     * @return
     */
    public static Period getPeriod(Date start, Date end) {
        Period period = new Period();

        period.setStart(start);
        period.setEnd(end);

        return period;
    }

    public static Period getPeriod(Date start) {
        Date defaultEnd = Date.from(ZonedDateTime.now().plusYears(100).toInstant());

        Period period = getPeriod(start, defaultEnd);
        return period;
    }
}
