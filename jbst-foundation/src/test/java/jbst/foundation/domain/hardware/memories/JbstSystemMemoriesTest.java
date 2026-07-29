package jbst.foundation.domain.hardware.memories;

import tools.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class JbstSystemMemoriesTest extends AbstractMemoriesTest {

    private static Stream<Arguments> serializeDeserializeTest() {
        return Stream.of(
                Arguments.of(new JbstSystemMemories(JbstGlobalMemory.zeroUsage(), JbstCpuMemory.zeroUsage()), "system-memory-1.json")
        );
    }

    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void serializeTest(JbstSystemMemories systemMemories, String fileName) {
        // Act
        var json = this.writeValueAsString(systemMemories);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void deserializeTest(JbstSystemMemories systemMemories, String fileName) {
        // Arrange
        var json = read(this.getFolder(), fileName);
        var typeReference = new TypeReference<JbstSystemMemories>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(systemMemories);
    }
}
