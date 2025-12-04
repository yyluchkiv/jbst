package jbst.foundation.domain.time;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import jbst.foundation.domain.base.Timestamp;
import jbst.foundation.domain.tuples.TupleRange;
import lombok.experimental.UtilityClass;

import java.time.*;
import java.util.Date;

import static jbst.foundation.domain.time.JbstTime.convert4;
import static jbst.foundation.domain.time.JbstTime.getTimestamp;

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

    public static long getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestampUTC(int monthAgo) {
        return getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestamp(ZoneOffset.UTC, monthAgo);
    }

    public static long getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestamp(ZoneId zoneId, int monthAgo) {
        return getTimestamp(
                LocalDate.now(zoneId).minusMonths(monthAgo).withDayOfMonth(1).atStartOfDay(),
                zoneId
        );
    }

    public static Timestamp getPastTimestamp(Duration duration) {
        return new Timestamp(getCurrentTimestamp() - duration.toMillis());
    }

    @SuppressWarnings("unused")
    public static TupleRange<Long> getPastRange(JbstTimeAmount timeAmount) {
        return getPastRange(getCurrentTimestamp(), timeAmount);
    }

    public static TupleRange<Long> getPastRange(long timestamp, JbstTimeAmount timeAmount) {
        var past = timestamp - timeAmount.toMillis();
        return new TupleRange<>(past, timestamp);
    }

    public static Timestamp getFutureTimestamp(Duration duration) {
        return new Timestamp(getCurrentTimestamp() + duration.toMillis());
    }

    @SuppressWarnings("unused")
    public static TupleRange<Long> getFutureRange(JbstTimeAmount timeAmount) {
        return getFutureRange(getCurrentTimestamp(), timeAmount);
    }

    public static TupleRange<Long> getFutureRange(long timestamp, JbstTimeAmount timeAmount) {
        var future = timestamp + timeAmount.toMillis();
        return new TupleRange<>(timestamp, future);
    }
}
