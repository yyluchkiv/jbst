package jbst.foundation.domain.dto.requests;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static org.assertj.core.api.Assertions.assertThat;

class RequestUserUpdate1Test extends AbstractSerializationDeserializationRunner {
    private static final RequestUserUpdate1 REQUEST = new RequestUserUpdate1(
            UKRAINE,
            Email.hardcoded(),
            "Hardcoded Tests"
    );

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Override
    protected String getFileName() {
        return "user-update-1.json";
    }

    // serialization is not required for request-based dtos

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<RequestUserUpdate1>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(REQUEST);
    }
}
