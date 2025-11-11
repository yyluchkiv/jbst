package jbst.foundation.domain.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static jbst.foundation.utilities.concurrent.SleepUtility.sleep;

@Slf4j
class JbstWorkerDynamicTest {

    public static class JbstWorkerDynamicConsole extends JbstWorkerDynamic {
        private long delay = 1;

        public JbstWorkerDynamicConsole(Duration duration) {
            super(duration);
        }

        @Override
        public Duration getDelay() {
            var current = this.delay;
            this.delay = Math.min(this.delay * 2, 30); // exponential backoff up to 30s
            return Duration.ofSeconds(current);
        }

        @Override
        public void onError(Exception ex) {
            LOGGER.warn("JbstWorkerDynamicConsole onError()", ex);
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
                Duration.ofSeconds(60)
        );

        // Act
        worker.start();
        sleep(65, TimeUnit.SECONDS);

        // Assert
        // no asserts
    }

}
