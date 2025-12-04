package jbst.foundation.domain.concurrent;

import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;

class JbstLatencySynchronizedQueueTest {

    @RepeatedTest(10)
    void hardcodedTest() {
        // Act
        var queue = JbstLatencySynchronizedQueue.hardcoded();
        var json = JbstLatencySynchronizedQueue.JbstLatencyJSON.hardcoded();

        // Assert
        assertThat(queue.avgMs()).isEqualTo(237L);
        assertThat(queue.maxMs()).isEqualTo(279L);
        assertThat(json.getLatencyMs()).isEqualTo("237 ms");
        assertThat(json.getLatenciesMs()).isEqualTo("[200 ms, 210 ms, 210 ms, 215 ms, 225 ms, 232 ms, 251 ms, 274 ms, 278 ms, 279 ms]");
    }
}
