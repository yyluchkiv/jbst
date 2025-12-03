package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.exceptions.base.JbstTooManyRequestsException;
import jbst.foundation.utilities.random.RandomUtility;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class JbstRateLimiterTest {

    private static Stream<Arguments> calculatePermitsPerSecondTest() {
        return Stream.of(
                Arguments.of(60, Duration.ofSeconds(1), 60),
                Arguments.of(60, Duration.ofSeconds(60), 1),
                Arguments.of(1, Duration.ofSeconds(1), 1),
                Arguments.of(1, Duration.ofSeconds(5), 0.2),
                Arguments.of(1, Duration.ofMinutes(1), 0.016666666666666666),
                Arguments.of(1, Duration.ofSeconds(60), 0.016666666666666666),
                Arguments.of(10, Duration.ofMinutes(1), 0.16666666666666666),
                Arguments.of(100, Duration.ofMinutes(1), 1.6666666666666667),
                Arguments.of(90, Duration.ofMinutes(1), 1.5),
                Arguments.of(30, Duration.ofMinutes(1), 0.5)
        );
    }

    private static Stream<Arguments> tryAcquireTest() {
        return Stream.of(
                Arguments.of(1, Duration.ofSeconds(1), 100, false),
                Arguments.of(1, Duration.ofSeconds(2), 200, false),
                Arguments.of(1, Duration.ofSeconds(1), 500, false),
                Arguments.of(1, Duration.ofSeconds(1), 600, false),
                Arguments.of(1, Duration.ofSeconds(2), 900, false),
                Arguments.of(1, Duration.ofSeconds(1), 1000, true),
                Arguments.of(1, Duration.ofSeconds(2), 1100, false)
        );
    }

    private static Stream<Arguments> acquireTest() {
        return Stream.of(
                Arguments.of(1, Duration.ofSeconds(1), 100, true),
                Arguments.of(1, Duration.ofSeconds(2), 200, true),
                Arguments.of(1, Duration.ofSeconds(1), 500, true),
                Arguments.of(1, Duration.ofSeconds(1), 600, true),
                Arguments.of(1, Duration.ofSeconds(2), 900, true),
                Arguments.of(1, Duration.ofSeconds(1), 1000, false),
                Arguments.of(1, Duration.ofSeconds(2), 1100, true)
        );
    }

    @ParameterizedTest
    @MethodSource("calculatePermitsPerSecondTest")
    void calculatePermitsPerSecondTest(int requests, Duration duration, double permitsPerSecond) {
        // Act
        var actual = JbstRateLimiter.calculatePermitsPerSecond(requests, duration);

        // Assert
        assertThat(actual).isEqualTo(permitsPerSecond);
    }

    @ParameterizedTest
    @MethodSource("tryAcquireTest")
    void tryAcquireTest(int requests, Duration duration, long timeout, boolean expected) {
        // Arrange
        var componentUnderTest = new JbstRateLimiter<String>(requests, duration, duration.multipliedBy(2));
        var key = RandomUtility.randomString();

        // Act
        componentUnderTest.tryAcquire(key);
        JbstSleep.sleepMilliseconds(timeout);
        var actual = componentUnderTest.tryAcquire(key);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("acquireTest")
    void acquireTest(int requests, Duration duration, long timeout, boolean exceptionally) {
        // Arrange
        var componentUnderTest = new JbstRateLimiter<String>(requests, duration, duration.multipliedBy(2));
        var key = RandomUtility.randomString();

        // Act
        componentUnderTest.tryAcquire(key);
        JbstSleep.sleepMilliseconds(timeout);
        var actual = catchThrowable(() -> componentUnderTest.acquire(key));

        // Assert
        if (exceptionally) {
            assertThat(actual).isInstanceOf(JbstTooManyRequestsException.class).isNotNull();
        } else {
            assertThat(actual).isNull();
        }
    }

}
