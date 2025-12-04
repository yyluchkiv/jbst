package jbst.foundation.domain.time;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@JbstDeletionScheduled(reason = "migrate -> JbstTime", version = "unknown future")
@UtilityClass
public class LocalDateUtility {

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
}
