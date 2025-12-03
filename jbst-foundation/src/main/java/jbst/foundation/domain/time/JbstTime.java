package jbst.foundation.domain.time;

import jbst.foundation.utilities.time.LocalDateUtility;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.Date;

import static java.time.ZoneOffset.UTC;
import static jbst.foundation.utilities.time.LocalDateTimeUtility.getTimestamp;

@UtilityClass
public class JbstTime {

    public static long getStartOfMonth(long timestamp) {
        return getTimestamp(
                LocalDateUtility.convertDate(Date.from(Instant.ofEpochMilli(timestamp))).withDayOfMonth(1).atStartOfDay(),
                UTC
        );
    }
}
