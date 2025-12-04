package jbst.foundation.domain.time;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.time.TimestampUtility.getCurrentTimestamp;

@UtilityClass
public class JbstTime {

    // =================================================================================================================
    // CONVERT(s)
    // =================================================================================================================
    public static LocalDateTime convert(long timestamp, ZoneId zoneId) {
        return LocalDateTime.ofInstant(ofEpochMilli(timestamp), zoneId);
    }

    public static LocalDateTime convert(Date date, ZoneId zoneId) {
        return convert(date.getTime(), zoneId);
    }

    public static Date convert(LocalDateTime localDateTime, ZoneId zoneId) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    public static LocalDate convert4(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    public static LocalDate convert5(Date date, ZoneId zoneId) {
        return LocalDate.ofInstant(ofEpochMilli(date.getTime()), zoneId);
    }

    // =================================================================================================================
    // BLOCK: "timestamp"(s)
    // =================================================================================================================
    public static long getTimestamp(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static long getStartOfMonth(long timestamp) {
        return getTimestamp(
                convert4(Date.from(ofEpochMilli(timestamp))).withDayOfMonth(1).atStartOfDay(),
                UTC
        );
    }

    public static boolean isCurrentTimestampNSecondsMore(long timestamp, long seconds) {
        return TimeUnit.MILLISECONDS.toSeconds(getCurrentTimestamp() - timestamp) > seconds;
    }

    // =================================================================================================================
    // BLOCK: LocalDateTime
    // =================================================================================================================
    public static boolean isParamsEqualsTruncatedBySeconds(LocalDateTime time1, LocalDateTime time2) {
        return isParamsEqualsTruncatedBy(time1, time2, ChronoUnit.SECONDS);
    }

    public static boolean isParamsEqualsTruncatedBy(LocalDateTime time1, LocalDateTime time2, ChronoUnit chronoUnit) {
        return time1.truncatedTo(chronoUnit).isEqual(time2.truncatedTo(chronoUnit));
    }

    public static boolean isFirstParamAfterTruncatedBySeconds(LocalDateTime time1, LocalDateTime time2) {
        return isFirstParamAfterTruncatedBy(time1, time2, ChronoUnit.SECONDS);
    }

    public static boolean isFirstParamAfterTruncatedBy(LocalDateTime time1, LocalDateTime time2, ChronoUnit chronoUnit) {
        return time1.truncatedTo(chronoUnit).isAfter(time2.truncatedTo(chronoUnit));
    }

    public static boolean isFirstParamAfterOrEqualTruncatedBySeconds(LocalDateTime time1, LocalDateTime time2) {
        return isFirstParamAfterOrEqualTruncatedBy(time1, time2, ChronoUnit.SECONDS);
    }

    public static boolean isFirstParamAfterOrEqualTruncatedBy(LocalDateTime time1, LocalDateTime time2, ChronoUnit chronoUnit) {
        return isParamsEqualsTruncatedBy(time1, time2, chronoUnit) ||
                isFirstParamAfterTruncatedBy(time1, time2, chronoUnit);
    }

    public static boolean isFirstParamBeforeTruncatedBySeconds(LocalDateTime time1, LocalDateTime time2) {
        return isFirstParamBeforeTruncatedBy(time1, time2, ChronoUnit.SECONDS);
    }

    public static boolean isFirstParamBeforeTruncatedBy(LocalDateTime time1, LocalDateTime time2, ChronoUnit chronoUnit) {
        var time1Truncated = time1.truncatedTo(chronoUnit);
        var time2Truncated = time2.truncatedTo(chronoUnit);
        return time1Truncated.isBefore(time2Truncated);
    }

    public static boolean isFirstParamBeforeOrEqualTruncatedBySeconds(LocalDateTime time1, LocalDateTime time2) {
        return isFirstParamBeforeOrEqualTruncatedBy(time1, time2, ChronoUnit.SECONDS);
    }

    public static boolean isFirstParamBeforeOrEqualTruncatedBy(LocalDateTime time1, LocalDateTime time2, ChronoUnit chronoUnit) {
        return isParamsEqualsTruncatedBy(time1, time2, chronoUnit) ||
                isFirstParamBeforeTruncatedBy(time1, time2, chronoUnit);
    }

    // =================================================================================================================
    // BLOCK: LocalDate
    // =================================================================================================================
    public static LocalDate getFirstDayCurrentMonth(ZoneId zoneId) {
        return LocalDate.now(zoneId).withDayOfMonth(1);
    }

    public static LocalDate getFirstDayPreviousMonth(ZoneId zoneId) {
        return LocalDate.now(zoneId).minusMonths(1).withDayOfMonth(1);
    }

    public static LocalDate getFirstDayTwoMonthAgo(ZoneId zoneId) {
        return LocalDate.now(zoneId).minusMonths(2).withDayOfMonth(1);
    }

    public static LocalDate getFirstDayMonthsAgo(ZoneId zoneId, int months) {
        return LocalDate.now(zoneId).minusMonths(months).withDayOfMonth(1);
    }

    public static LocalDate getLastDayCurrentMonth(ZoneId zoneId) {
        var now = LocalDate.now(zoneId);
        return now.withDayOfMonth(now.lengthOfMonth());
    }

    public static LocalDate getLastDayPreviousMonth(ZoneId zoneId) {
        var past = LocalDate.now(zoneId).minusMonths(1);
        return past.withDayOfMonth(past.lengthOfMonth());
    }

    public static LocalDate getLastDayTwoMonthAgo(ZoneId zoneId) {
        var past = LocalDate.now(zoneId).minusMonths(2);
        return past.withDayOfMonth(past.lengthOfMonth());
    }

    public static LocalDate getLastDayMonthsAgo(ZoneId zoneId, int months) {
        var past = LocalDate.now(zoneId).minusMonths(months);
        return past.withDayOfMonth(past.lengthOfMonth());
    }

    public static boolean isFirstDayOfMonth(LocalDate localDate) {
        return localDate.getDayOfMonth() == 1;
    }

    public static boolean isLastDayOfMonth(LocalDate localDate) {
        return localDate.equals(YearMonth.from(localDate).atEndOfMonth());
    }

    public static int getCurrentDayOfMonth(ZoneId zoneId) {
        return LocalDate.now(zoneId).getDayOfMonth();
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
