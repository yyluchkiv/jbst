package jbst.foundation.domain.time;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

@JbstDeletionScheduled(reason = "migrate -> JbstTime", version = "unknown future")
@UtilityClass
public class LocalDateTimeUtility {

    public static LocalDateTime nowByTimezone(TimeZone timeZone) {
        return nowByZoneId(timeZone.toZoneId());
    }

    public static LocalDateTime nowByZoneId(ZoneId zoneId) {
        return LocalDateTime.now(zoneId);
    }

    public static String format(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    public static LocalDateTime parse(String localDateTime, DateTimeFormatter formatter) {
        return LocalDateTime.parse(localDateTime, formatter);
    }

    public static LocalDateTime convertTimestamp(long timestamp, ZoneId zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
    }

    public static LocalDateTime convertDate(Date date, ZoneId zoneId) {
        return convertTimestamp(date.getTime(), zoneId);
    }

    public static long getTimestamp(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static boolean isParamsEqualsTruncatedBySeconds(LocalDateTime time1, LocalDateTime time2) {
        return isParamsEqualsTruncatedBy(time1, time2, ChronoUnit.SECONDS);
    }

    public static boolean isParamsEqualsTruncatedBy(LocalDateTime time1, LocalDateTime time2, ChronoUnit chronoUnit) {
        var time1Truncated = time1.truncatedTo(chronoUnit);
        var time2Truncated = time2.truncatedTo(chronoUnit);
        return time1Truncated.isEqual(time2Truncated);
    }

    public static boolean isFirstParamAfterTruncatedBySeconds(LocalDateTime time1, LocalDateTime time2) {
        return isFirstParamAfterTruncatedBy(time1, time2, ChronoUnit.SECONDS);
    }

    public static boolean isFirstParamAfterTruncatedBy(LocalDateTime time1, LocalDateTime time2, ChronoUnit chronoUnit) {
        var time1Truncated = time1.truncatedTo(chronoUnit);
        var time2Truncated = time2.truncatedTo(chronoUnit);
        return time1Truncated.isAfter(time2Truncated);
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
}
