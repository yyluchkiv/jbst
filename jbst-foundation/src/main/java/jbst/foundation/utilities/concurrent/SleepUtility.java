package jbst.foundation.utilities.concurrent;

import jbst.foundation.domain.annotations.DeletionScheduled;
import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class SleepUtility {

    public static void sleepMilliseconds(long timeout) {
        sleep(timeout, TimeUnit.MILLISECONDS);
    }

    @DeletionScheduled(version = "1.20")
    public static void sleep(TimeUnit timeUnit, long timeout) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException ex) {
            // ignore
        }
    }

    public static void sleep(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException ex) {
            // ignore
        }
    }
}
