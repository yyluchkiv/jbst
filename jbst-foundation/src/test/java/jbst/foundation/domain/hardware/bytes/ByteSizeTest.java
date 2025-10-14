package jbst.foundation.domain.hardware.bytes;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.io.TestsIOUtils;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ByteSizeTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> serializeDeserializeTest() {
        return Stream.of(
                Arguments.of(ByteSize.kilobyte(), "byte-size-kilobyte.json"),
                Arguments.of(ByteSize.megabyte(), "byte-size-megabyte.json"),
                Arguments.of(ByteSize.gigabyte(), "byte-size-gigabyte.json")
        );
    }

    private static Stream<Arguments> getByTest() {
        return Stream.of(
                Arguments.of(ByteUnit.KILOBYTE, 2, new BigDecimal("1536857.3"), new BigDecimal("1536857.25")),
                Arguments.of(ByteUnit.MEGABYTE, 2, new BigDecimal("1500.8"), new BigDecimal("1500.84")),
                Arguments.of(ByteUnit.GIGABYTE, 2, new BigDecimal("1.4657"), new BigDecimal("1.47"))
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void serializeTest(ByteSize byteSize, String fileName) {
        // Act
        var json = this.writeValueAsString(byteSize);

        // Assert
        assertThat(json).isEqualTo(TestsIOUtils.readFile(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeDeserializeTest")
    void deserializeTest(ByteSize byteSize, String fileName) {
        // Arrange
        var json = TestsIOUtils.readFile(this.getFolder(), fileName);
        var typeReference = new TypeReference<ByteSize>() {};

        // Act
        var actual = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(actual).isEqualTo(byteSize);
        assertThat(actual.getBytes()).isEqualTo(byteSize.getBytes());
    }

    @ParameterizedTest
    @MethodSource("getByTest")
    void getByTest(ByteUnit unit, int scale, BigDecimal expected1, BigDecimal expected2) {
        // Arrange
        var gigabyte15 = new ByteSize(1573741824L);

        // Act
        var actual1 = gigabyte15.getBy(unit);
        var actual2 = gigabyte15.getBy(unit, scale);

        // Assert
        assertThat(actual1).isEqualTo(expected1);
        assertThat(actual2).isEqualTo(expected2);
    }
}
