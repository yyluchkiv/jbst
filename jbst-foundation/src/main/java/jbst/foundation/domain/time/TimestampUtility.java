package jbst.foundation.domain.time;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
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
}
