package jbst.foundation.domain.hardware.memories;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class JbstCpuMemoryTest extends AbstractMemoriesTest {

    private static Stream<Arguments> serializeDeserializeTest() {
        return Stream.of(
                Arguments.of(new JbstCpuMemory(new BigDecimal("1.234")), "cpu-memory-1.json"),
                Arguments.of(new JbstCpuMemory(new BigDecimal("1.23456")), "cpu-memory-1.json"),
                Arguments.of(new JbstCpuMemory(new BigDecimal("1.2")), "cpu-memory-2.json"),
                Arguments.of(JbstCpuMemory.zeroUsage(), "cpu-memory-3.json")
        );
    }

    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void serializeTest(JbstCpuMemory cpuMemory, String fileName) {
        // Act
        var json = this.writeValueAsString(cpuMemory);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void deserializeTest(JbstCpuMemory cpuMemory, String fileName) {
        // Arrange
        var json = read(this.getFolder(), fileName);
        var typeReference = new TypeReference<JbstCpuMemory>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(cpuMemory);
    }
}
