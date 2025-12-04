package jbst.foundation.domain.factories.unique;

import jbst.foundation.domain.concurrent.JbstSleep;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;
import static org.assertj.core.api.Assertions.assertThat;

class CurrentTimeUniqueValueFactoryTest {

    private static Stream<Arguments> createValueCheckUniqueArgs() {
        return Stream.of(
                Arguments.of(100),
                Arguments.of(1000),
                Arguments.of(10000),
                Arguments.of(100000),
                Arguments.of(1000000)
        );
    }

    @RepeatedTest(100)
    void createValueBetween() {
        // Arrange
        var window = 1;
        var randomFactory = new CurrentTimeUniqueValueFactory();

        // Act
        var value = randomFactory.createValue();

        // Assert
        assertThat(value)
                .isNotNull()
                .isBetween(
                        (int) (getCurrentTimestamp() / 1000) - window,
                        (int) (getCurrentTimestamp() / 1000) + window
                );
    }

    @ParameterizedTest
    @MethodSource("createValueCheckUniqueArgs")
    void createValueCheckUnique(int iterations) {
        // Arrange
        var randomFactory = new CurrentTimeUniqueValueFactory();

        // Act
        var values1 = IntStream.range(0, iterations).mapToObj(i -> randomFactory.createValue()).collect(Collectors.toSet());
        JbstSleep.sleep(2, TimeUnit.SECONDS);
        var values2 = IntStream.range(0, iterations).mapToObj(i -> randomFactory.createValue()).collect(Collectors.toSet());

        // Assert
        assertThat(values1).hasSize(iterations);
        assertThat(values2).hasSize(iterations);
    }
}
