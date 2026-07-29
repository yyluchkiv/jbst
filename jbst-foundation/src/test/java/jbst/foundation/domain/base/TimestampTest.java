package jbst.foundation.domain.base;

import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimestampTest extends JbstUnitTests.Runners.BaseFolderFile {
    private static final Timestamp TIMESTAMP = Timestamp.fixed();

    @Override
    protected String getFileName() {
        return "timestamp-1.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(TIMESTAMP);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<Timestamp>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(TIMESTAMP);
        assertThat(actual.value()).isEqualTo(TIMESTAMP.value());
    }

    @RepeatedTest(10)
    void randomTest() {
        // Act
        var actual = Timestamp.random();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.value()).isNotZero();
    }
}
