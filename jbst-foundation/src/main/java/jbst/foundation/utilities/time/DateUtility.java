package jbst.foundation.utilities.time;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@JbstDeletionScheduled(reason = "migrate -> JbstTime", version = "unknown future")
@UtilityClass
public class DateUtility {

    public static Date convertLocalDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    public static long getAbsDifferenceByTimeUnit(Date date1, Date date2, TimeUnit timeUnit) {
        var diff = Math.abs(date2.getTime() - date1.getTime());
        return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
    }
}
