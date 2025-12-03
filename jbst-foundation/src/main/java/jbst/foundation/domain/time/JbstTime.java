package jbst.foundation.domain.time;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.time.LocalDateTimeUtility.getTimestamp;

@UtilityClass
public class JbstTime {

    // =================================================================================================================
    // CONVERT(s)
    // =================================================================================================================
    public static Date convert(LocalDateTime localDateTime, ZoneId zoneId) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    // =================================================================================================================
    // BLOCK: "timestamp"(s)
    // =================================================================================================================
    public static long getStartOfMonth(long timestamp) {
        return getTimestamp(
                LocalDateUtility.convertDate(Date.from(Instant.ofEpochMilli(timestamp))).withDayOfMonth(1).atStartOfDay(),
                UTC
        );
    }

    // =================================================================================================================
    // BLOCK: java.util.Date
    // =================================================================================================================
    public static long getAbsDifference(Date date1, Date date2, TimeUnit timeUnit) {
        var diff = Math.abs(date2.getTime() - date1.getTime());
        return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
    }
}
