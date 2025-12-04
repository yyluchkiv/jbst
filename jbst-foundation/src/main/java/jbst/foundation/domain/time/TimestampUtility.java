package jbst.foundation.domain.time;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import static jbst.foundation.domain.time.JbstTime.*;

@JbstDeletionScheduled(reason = "migrate -> JbstTime", version = "unknown future")
@UtilityClass
public class TimestampUtility {
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static long toUnixTime(long timestamp) {
        return timestamp / 1000;
    }

    public static long getStartOfMonthTimestamp(long timestamp, ZoneId zoneId) {
        return getTimestamp(
                convert4(Date.from(Instant.ofEpochMilli(timestamp))).withDayOfMonth(1).atStartOfDay(),
                zoneId
        );
    }

    public static long getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestampUTC() {
        return getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestamp(ZoneOffset.UTC);
    }

    public static long getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestamp(ZoneId zoneId) {
        return getTimestamp(
                LocalDate.now(zoneId).withDayOfMonth(1).atStartOfDay(),
                zoneId
        );
    }

    public static long getPreviousMonthAtStartOfMonthAndAtStartOfDayTimestampUTC() {
        return getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestampUTC(1);
    }

    public static long getPreviousMonthAtStartOfMonthAndAtStartOfDayTimestamp(ZoneId zoneId) {
        return getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestamp(zoneId, 1);
    }
}
