package jbst.foundation.domain.base;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.runners.AbstractSerializationDeserializationRunner;
import jbst.foundation.domain.tests.constants.TestsJunitConstants;
import lombok.SneakyThrows;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectIdTest extends AbstractSerializationDeserializationRunner {
    private static final ObjectId OBJECT_ID = ObjectId.hardcoded();

    @Override
    protected String getFileName() {
        return "object-id-1.json";
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(OBJECT_ID);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<ObjectId>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(OBJECT_ID);
        assertThat(actual.value()).isEqualTo(OBJECT_ID.value());
        assertThat(actual.toString()).hasToString(OBJECT_ID.value());
    }

    @RepeatedTest(TestsJunitConstants.SMALL_ITERATIONS_COUNT)
    void randomTest() {
        // Act
        var actual = ObjectId.random();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.value()).isNotNull();
    }
}
