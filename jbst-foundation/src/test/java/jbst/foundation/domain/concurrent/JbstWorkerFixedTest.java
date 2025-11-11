package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.time.TimeAmount;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static jbst.foundation.utilities.concurrent.SleepUtility.sleep;

@Slf4j
class JbstWorkerFixedTest {

    public static class JbstWorkerFixedConsole extends JbstWorkerFixed {

        public JbstWorkerFixedConsole(SchedulerConfiguration interval, TimeAmount duration) {
            super(interval, duration);
        }

        @Override
        public void onTick() {
            LOGGER.warn("JbstWorkerFixedConsole onTick()");
        }

        @Override
        public void onComplete() {
            LOGGER.warn("JbstWorkerFixedConsole onComplete()");
        }
    }

    @Test
    void console() {
        // Arrange
        var worker = new JbstWorkerFixedConsole(
                SchedulerConfiguration.EVERY_5_SECONDS,
                new TimeAmount(30, ChronoUnit.SECONDS)
        );

        // Act
        worker.start();
        sleep(35, TimeUnit.SECONDS);

        // Assert
        // no asserts
    }
}
