package jbst.foundation.utilities.time;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

@JbstDeletionScheduled(reason = "migrate -> JbstTime", version = "unknown future")
@UtilityClass
public class LocalDateUtility {

    public static LocalDate now(TimeZone timeZone) {
        return now(timeZone.toZoneId());
    }

    public static LocalDate now(ZoneId zoneId) {
        return LocalDate.now(zoneId);
    }

    public static LocalDate convertDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    public static LocalDate convertDate(Date date, ZoneId zoneId) {
        return LocalDate.ofInstant(Instant.ofEpochMilli(date.getTime()), zoneId);
    }

    public static LocalDate getFirstDayCurrentMonth(ZoneId zoneId) {
        return now(zoneId).withDayOfMonth(1);
    }

    public static LocalDate getFirstDayPreviousMonth(ZoneId zoneId) {
        return now(zoneId).minusMonths(1).withDayOfMonth(1);
    }

    public static LocalDate getFirstDayTwoMonthAgo(ZoneId zoneId) {
        return now(zoneId).minusMonths(2).withDayOfMonth(1);
    }

    public static LocalDate getFirstDayMonthsAgo(ZoneId zoneId, int months) {
        return now(zoneId).minusMonths(months).withDayOfMonth(1);
    }

    public static LocalDate getLastDayCurrentMonth(ZoneId zoneId) {
        var now = now(zoneId);
        return now.withDayOfMonth(now.lengthOfMonth());
    }

    public static LocalDate getLastDayPreviousMonth(ZoneId zoneId) {
        var past = now(zoneId).minusMonths(1);
        return past.withDayOfMonth(past.lengthOfMonth());
    }

    public static LocalDate getLastDayTwoMonthAgo(ZoneId zoneId) {
        var past = now(zoneId).minusMonths(2);
        return past.withDayOfMonth(past.lengthOfMonth());
    }

    public static LocalDate getLastDayMonthsAgo(ZoneId zoneId, int months) {
        var past = now(zoneId).minusMonths(months);
        return past.withDayOfMonth(past.lengthOfMonth());
    }

    public static boolean isFirstDayOfMonth(LocalDate localDate) {
        return localDate.getDayOfMonth() == 1;
    }

    public static boolean isLastDayOfMonth(LocalDate localDate) {
        return localDate.equals(YearMonth.from(localDate).atEndOfMonth());
    }

    public static int getCurrentDayOfMonth(ZoneId zoneId) {
        return now(zoneId).getDayOfMonth();
    }
}
