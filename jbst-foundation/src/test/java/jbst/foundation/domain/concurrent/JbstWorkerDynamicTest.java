package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.time.TimeAmount;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static jbst.foundation.utilities.concurrent.SleepUtility.sleep;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JbstWorkerDynamicTest {

    public static class JbstWorkerDynamicConsole extends JbstWorkerDynamic {
        private long delay = 1;

        public JbstWorkerDynamicConsole(SchedulerConfiguration interval, TimeAmount duration) {
            super(interval, duration);
        }

        @Override
        protected long nextDelaySeconds() {
            long current = this.delay;
            this.delay = Math.min(this.delay * 2, 30); // exponential backoff up to 30s
            return current;
        }

        @Override
        public void onTick() {
            LOGGER.warn("JbstWorkerDynamicConsole onTick()");
        }

        @Override
        public void onComplete() {
            LOGGER.warn("JbstWorkerDynamicConsole onComplete()");
        }
    }

    @Test
    void console() {
        // Arrange
        var worker = new JbstWorkerDynamicConsole(
                SchedulerConfiguration.EVERY_1_SECOND,
                new TimeAmount(60, ChronoUnit.SECONDS)
        );

        // Act
        worker.start();
        sleep(65, TimeUnit.SECONDS);

        // Assert
        // no asserts
    }

}
