package jbst.foundation.domain.time;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.time.ZoneOffset.UTC;

@UtilityClass
public class JbstTime {

    // =================================================================================================================
    // CONVERT(s)
    // =================================================================================================================
    public static LocalDateTime convert(long timestamp, ZoneId zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
    }

    public static LocalDateTime convert(Date date, ZoneId zoneId) {
        return convert(date.getTime(), zoneId);
    }

    public static Date convert(LocalDateTime localDateTime, ZoneId zoneId) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    // =================================================================================================================
    // BLOCK: "timestamp"(s)
    // =================================================================================================================
    public static long getTimestamp(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

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

    // =================================================================================================================
    // BLOCK: TimeUnit
    // =================================================================================================================
    @SuppressWarnings("unused")
    public static TimeUnit toTimeUnit(ChronoUnit chronoUnit) {
        if (chronoUnit == ChronoUnit.NANOS) {
            return TimeUnit.NANOSECONDS;
        } else if (chronoUnit == ChronoUnit.MICROS) {
            return TimeUnit.MICROSECONDS;
        } else if (chronoUnit == ChronoUnit.MILLIS) {
            return TimeUnit.MILLISECONDS;
        } else if (chronoUnit == ChronoUnit.SECONDS) {
            return TimeUnit.SECONDS;
        } else if (chronoUnit == ChronoUnit.MINUTES) {
            return TimeUnit.MINUTES;
        } else if (chronoUnit == ChronoUnit.HOURS) {
            return TimeUnit.HOURS;
        } else if (chronoUnit == ChronoUnit.DAYS) {
            return TimeUnit.DAYS;
        } else {
            throw new IllegalArgumentException("Unsupported ChronoUnit: %s".formatted(chronoUnit));
        }
    }
}
