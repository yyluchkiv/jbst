package jbst.foundation.utilities.time;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import jbst.foundation.domain.base.Timestamp;
import jbst.foundation.domain.time.TimeAmount;
import jbst.foundation.domain.tuples.TupleRange;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static jbst.foundation.utilities.time.LocalDateTimeUtility.getTimestamp;

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
                LocalDateUtility.convertDate(Date.from(Instant.ofEpochMilli(timestamp))).withDayOfMonth(1).atStartOfDay(),
                zoneId
        );
    }

    public static long getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestampUTC() {
        return getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestamp(ZoneOffset.UTC);
    }

    public static long getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestamp(ZoneId zoneId) {
        return getTimestamp(
                LocalDateUtility.now(zoneId).withDayOfMonth(1).atStartOfDay(),
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
                LocalDateUtility.now(zoneId).minusMonths(monthAgo).withDayOfMonth(1).atStartOfDay(),
                zoneId
        );
    }

    public static Timestamp getPastTimestamp(Duration duration) {
        return new Timestamp(getCurrentTimestamp() - duration.toMillis());
    }

    public static TupleRange<Long> getPastRange(TimeAmount timeAmount) {
        return getPastRange(getCurrentTimestamp(), timeAmount);
    }

    public static TupleRange<Long> getPastRange(long timestamp, TimeAmount timeAmount) {
        var past = timestamp - timeAmount.toMillis();
        return new TupleRange<>(past, timestamp);
    }

    public static Timestamp getFutureTimestamp(Duration duration) {
        return new Timestamp(getCurrentTimestamp() + duration.toMillis());
    }

    public static TupleRange<Long> getFutureRange(TimeAmount timeAmount) {
        return getFutureRange(getCurrentTimestamp(), timeAmount);
    }

    public static TupleRange<Long> getFutureRange(long timestamp, TimeAmount timeAmount) {
        var future = timestamp + timeAmount.toMillis();
        return new TupleRange<>(timestamp, future);
    }

    public static boolean isBetween(long timestamp, long past, long future) {
        return timestamp > past && timestamp < future;
    }

    public static boolean isBetweenInclusive(long timestamp, long past, long future) {
        return timestamp >= past && timestamp <= future;
    }

    public static boolean isPast(long timestamp) {
        return getCurrentTimestamp() > timestamp;
    }

    public static boolean isFuture(long timestamp) {
        return getCurrentTimestamp() < timestamp;
    }

    public boolean isCurrentTimestampNSecondsMore(long timestamp, long seconds) {
        return TimeUnit.MILLISECONDS.toSeconds(getCurrentTimestamp() - timestamp) > seconds;
    }
}
