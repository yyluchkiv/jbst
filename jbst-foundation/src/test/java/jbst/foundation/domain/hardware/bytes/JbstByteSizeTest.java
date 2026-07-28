package jbst.foundation.domain.hardware.bytes;

import tools.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.JbstUnitTests;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static jbst.foundation.domain.tests.JbstUnitTests.IO.read;
import static org.assertj.core.api.Assertions.assertThat;

class JbstByteSizeTest extends JbstUnitTests.Runners.BaseFolder {

    private static Stream<Arguments> serializeDeserializeTest() {
        return Stream.of(
                Arguments.of(JbstByteSize.kilobyte(), "byte-size-kilobyte.json"),
                Arguments.of(JbstByteSize.megabyte(), "byte-size-megabyte.json"),
                Arguments.of(JbstByteSize.gigabyte(), "byte-size-gigabyte.json")
        );
    }

    private static Stream<Arguments> getByTest() {
        return Stream.of(
                Arguments.of(JbstByteUnit.KILOBYTE, 2, new BigDecimal("1536857.3"), new BigDecimal("1536857.25")),
                Arguments.of(JbstByteUnit.MEGABYTE, 2, new BigDecimal("1500.8"), new BigDecimal("1500.84")),
                Arguments.of(JbstByteUnit.GIGABYTE, 2, new BigDecimal("1.4657"), new BigDecimal("1.47"))
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void serializeTest(JbstByteSize byteSize, String fileName) {
        // Act
        var json = this.writeValueAsString(byteSize);

        // Assert
        assertThat(json).isEqualTo(read(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void deserializeTest(JbstByteSize byteSize, String fileName) {
        // Arrange
        var json = read(this.getFolder(), fileName);
        var typeReference = new TypeReference<JbstByteSize>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(byteSize);
        assertThat(actual.getBytes()).isEqualTo(byteSize.getBytes());
    }

    @ParameterizedTest
    @MethodSource("getByTest")
    void getByTest(JbstByteUnit unit, int scale, BigDecimal expected1, BigDecimal expected2) {
        // Arrange
        var gigabyte15 = new JbstByteSize(1573741824L);

        // Act
        var actual1 = gigabyte15.getBy(unit);
        var actual2 = gigabyte15.getBy(unit, scale);

        // Assert
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }
}
