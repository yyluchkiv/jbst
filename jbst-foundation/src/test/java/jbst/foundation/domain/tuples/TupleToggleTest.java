package jbst.foundation.domain.tuples;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class TupleToggleTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of(TupleToggle.enabled("enabled"), "tuple-toggle-enabled.json"),
                Arguments.of(TupleToggle.disabled("DIS"), "tuple-toggle-disabled.json"),
                Arguments.of(TupleToggle.disabled(), "tuple-toggle-disabled-null.json")
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    void serialize(TupleToggle<String> tupleToggle, String fileName) {
        // Act
        var json = this.writeValueAsString(tupleToggle);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeTest")
    void deserializeTest(TupleToggle<String> tupleToggle, String fileName) {
        // Arrange
        var json = read(this.getFolder(), fileName);
        var typeReference = new TypeReference<TupleToggle<String>>() {};

        // Act
        var tuple = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(tuple).isEqualTo(tupleToggle);
    }
}
