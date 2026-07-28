package jbst.foundation.domain.base;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsernameTest extends JbstUnitTests.Runners.BaseFolderFile {
    private static final Username USERNAME = Username.fixed();

    @Override
    protected String getFileName() {
        return "username-1.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(USERNAME);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<Username>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(USERNAME);
        assertThat(actual.value()).isEqualTo(USERNAME.value());
        assertThat(actual.toString()).hasToString(USERNAME.value());
    }

    @RepeatedTest(10)
    void randomTest() {
        // Act
        var actual = Username.random();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.value()).isNotNull();
    }
}
