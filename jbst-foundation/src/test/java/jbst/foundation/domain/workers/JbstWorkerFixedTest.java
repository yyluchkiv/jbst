package jbst.foundation.domain.workers;

import jbst.foundation.domain.time.JbstSchedulerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static jbst.foundation.domain.concurrent.JbstSleep.sleep;

@Disabled
@Slf4j
class JbstWorkerFixedTest {

    public static class JbstWorkerFixedConsole extends JbstWorkerFixed {

        public JbstWorkerFixedConsole(Duration duration, JbstSchedulerConfiguration interval) {
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
                JbstSchedulerConfiguration.EVERY_5_SECONDS
        );

        // Act
        worker.start();
        sleep(35, TimeUnit.SECONDS);

        // Assert
        // no asserts
    }
}
