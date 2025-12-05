package jbst.foundation.domain.hardware.memories;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class JbstHeapMemoryTest extends AbstractMemoriesTest {

    private static Stream<Arguments> serializeDeserializeTest() {
        return Stream.of(
                Arguments.of(new JbstHeapMemory(
                        1073741824L,
                        573741824L,
                        1073741824L,
                        1073741824L
                ), "heap-memory-1.json"),
                Arguments.of(JbstHeapMemory.zeroUsage(), "heap-memory-2.json")
        );
    }

    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void serializeTest(JbstHeapMemory heapMemory, String fileName) {
        // Act
        var json = this.writeValueAsString(heapMemory);

        // Assert
        Assertions.assertThat(json).isEqualTo(read(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void deserializeTest(JbstHeapMemory heapMemory, String fileName) {
        // Arrange
        var json = read(this.getFolder(), fileName);
        var typeReference = new TypeReference<JbstHeapMemory>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(heapMemory);
    }
}
