package jbst.foundation.domain.base;

import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTest extends JbstUnitTests.Runners.BaseFolderFile {
    private static final Email EMAIL = Email.fixed();

    private static Stream<Arguments> getUsernameTest() {
        return Stream.of(
                Arguments.of(Email.fixed(), new Username("tests")),
                Arguments.of(Email.unknown(), new Username("Unknown"))
        );
    }

    @Override
    protected String getFileName() {
        return "email-1.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(EMAIL);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<Email>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(EMAIL);
        assertThat(actual.value()).isEqualTo(EMAIL.value());
        assertThat(actual.toString()).hasToString(EMAIL.value());
    }

    @Test
    void randomTest() {
        // Arrange
        var randomLength = 32;
        var domainLength = 14;
        var expected = randomLength + domainLength;

        // Act
        var actual = Email.random();

        // Assert
        assertThat(actual.value()).hasSize(expected);
        assertThat(actual.value().substring(randomLength)).isEqualTo("@" + JbstConstants.Domains.FIXED);
    }

    @ParameterizedTest
    @MethodSource("getUsernameTest")
    void getUsernameTest(Email email, Username expected) {
        // Act + Assert
        assertThat(email.getUsername()).isEqualTo(expected);
    }
}
