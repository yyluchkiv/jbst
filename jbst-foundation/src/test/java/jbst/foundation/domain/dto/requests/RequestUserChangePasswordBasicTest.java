package jbst.foundation.domain.dto.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUserChangePasswordBasicTest extends AbstractSerializationDeserializationRunner {
    private static final RequestUserChangePasswordBasic REQUEST = new RequestUserChangePasswordBasic(
            Password.of("password123"),
            Password.of("password123")
    );

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Override
    protected String getFileName() {
        return "user-change-password-1.json";
    }

    // serialization is not required for request-based dtos

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<RequestUserChangePasswordBasic>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(REQUEST);
    }
}
