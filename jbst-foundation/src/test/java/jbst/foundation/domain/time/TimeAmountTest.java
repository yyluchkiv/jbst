package jbst.foundation.domain.time;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.assertThat;

class TimeAmountTest extends AbstractSerializationDeserializationRunner {
    private static final TimeAmount TIME_AMOUNT = TimeAmount.hardcoded();

    private static Stream<Arguments> toTest() {
        return Stream.of(
                Arguments.of(new TimeAmount(10L, SECONDS), 10L, 10000L),
                Arguments.of(new TimeAmount(10L, MINUTES), 600L, 600000L),
                Arguments.of(new TimeAmount(10L, HOURS), 36000L, 36000000L),
                Arguments.of(new TimeAmount(10L, DAYS), 864000L, 864000000L)
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Override
    protected String getFileName() {
        return "time-amount.json";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(TIME_AMOUNT);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<TimeAmount>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(TIME_AMOUNT);
        assertThat(actual.amount()).isEqualTo(TIME_AMOUNT.amount());
        assertThat(actual.unit()).isEqualTo(TIME_AMOUNT.unit());
    }

    @ParameterizedTest
    @MethodSource("toTest")
    void toTest(TimeAmount timeAmount, long expectedSeconds, long expectedMillis) {
        // Act
        var actualSeconds = timeAmount.toSeconds();
        var actualMillis = timeAmount.toMillis();

        // Assert
        assertThat(actualSeconds).isEqualTo(expectedSeconds);
        assertThat(actualMillis).isEqualTo(expectedMillis);
    }
}
