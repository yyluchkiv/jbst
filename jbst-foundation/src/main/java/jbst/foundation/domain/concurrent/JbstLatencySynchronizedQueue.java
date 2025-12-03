package jbst.foundation.domain.concurrent;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.collections4.queue.SynchronizedQueue;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZeroByBounds;
import static org.apache.commons.collections4.queue.SynchronizedQueue.synchronizedQueue;

@EqualsAndHashCode
@ToString
public class JbstLatencySynchronizedQueue {
    private final SynchronizedQueue<Long> nanos;

    @Getter
    @EqualsAndHashCode
    @ToString
    public static class JbstLatencyJSON {
        private final String latencyMs;
        private final String latenciesMs;

        public JbstLatencyJSON(SynchronizedQueue<Long> nanos) {
            var latencies = nanos.stream().map(TimeUnit.NANOSECONDS::toMillis).toList();
            var latency = (long) latencies.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0d);
            this.latencyMs = latency + " ms";
            this.latenciesMs = "[" + latencies.stream().map(element -> element + " ms").collect(Collectors.joining(", ")) + "]";
        }

        public static JbstLatencyJSON hardcoded() {
            return JbstLatencySynchronizedQueue.hardcoded().getJSON();
        }

        public static JbstLatencyJSON random() {
            return JbstLatencySynchronizedQueue.random().getJSON();
        }
    }

    public JbstLatencySynchronizedQueue(int size) {
        this.nanos = synchronizedQueue(new CircularFifoQueue<>(size));
    }

    public static JbstLatencySynchronizedQueue hardcoded() {
        var queue = new JbstLatencySynchronizedQueue(10);
        queue.add(200_000_000L);
        queue.add(210_000_000L);
        queue.add(210_000_000L);
        queue.add(215_000_000L);
        queue.add(225_000_000L);
        queue.add(232_000_000L);
        queue.add(251_000_000L);
        queue.add(274_000_000L);
        queue.add(278_000_000L);
        queue.add(279_000_000L);
        return queue;
    }

    public static JbstLatencySynchronizedQueue random() {
        var queue = new JbstLatencySynchronizedQueue(10);
        IntStream.range(0, 10).forEach(i -> {
            var latency = randomLongGreaterThanZeroByBounds(200_000_000, 350_000_000);
            queue.add(latency);
        });
        return queue;
    }

    public void add(Long latency) {
        this.nanos.add(latency);
    }

    @JsonValue
    public JbstLatencyJSON getJSON() {
        return new JbstLatencyJSON(this.nanos);
    }
}
