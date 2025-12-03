package jbst.foundation.domain.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JbstThrowableTraceTest extends AbstractSerializationDeserializationRunner {
    private static final JbstThrowableTrace TRACE = new JbstThrowableTrace("java.lang.NullPointerException: jbst at jbst.domain.exceptions.ThrowableTraceTest.main(ThrowableTraceTest.java:20)");

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Override
    protected String getFileName() {
        return "exception-trace.json";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(TRACE);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<JbstThrowableTrace>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(TRACE);
        assertThat(actual.value()).isEqualTo(TRACE.value());
        assertThat(actual.toString()).hasToString(TRACE.value());
    }
}
