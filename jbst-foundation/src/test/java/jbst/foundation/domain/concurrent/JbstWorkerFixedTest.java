package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.workers.JbstWorkerFixed;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static jbst.foundation.utilities.concurrent.SleepUtility.sleep;

@Slf4j
class JbstWorkerFixedTest {

    public static class JbstWorkerFixedConsole extends JbstWorkerFixed {

        public JbstWorkerFixedConsole(Duration duration, SchedulerConfiguration interval) {
            super(duration, interval);
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
                Duration.ofSeconds(30),
                SchedulerConfiguration.EVERY_5_SECONDS
        );

        // Act
        worker.start();
        sleep(35, TimeUnit.SECONDS);

        // Assert
        // no asserts
    }
}
