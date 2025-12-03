package jbst.foundation.domain.base;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyIdTest extends AbstractSerializationDeserializationRunner {
    private static final PropertyId PROPERTY_ID = PropertyId.hardcoded();

    @Override
    protected String getFileName() {
        return "property-id-1.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(PROPERTY_ID);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<PropertyId>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(PROPERTY_ID);
        assertThat(actual.value()).isEqualTo(PROPERTY_ID.value());
        assertThat(actual.toString()).hasToString(PROPERTY_ID.value());
    }

    @RepeatedTest(10)
    void randomTest() {
        // Act
        var actual = PropertyId.random();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.value()).isNotNull();
    }
}
