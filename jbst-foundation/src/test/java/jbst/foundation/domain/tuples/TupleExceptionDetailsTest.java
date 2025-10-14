package jbst.foundation.domain.tuples;

import com.fasterxml.jackson.core.type.TypeReference;
import jbst.foundation.domain.tests.io.TestsIOUtils;
import jbst.foundation.domain.tests.runners.AbstractFolderSerializationRunner;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TupleExceptionDetailsTest extends AbstractFolderSerializationRunner {

    private static Stream<Arguments> serializeTest() {
        return Stream.of(
                Arguments.of(TupleExceptionDetails.ok(), "tuple-exception-details-ok.json"),
                Arguments.of(TupleExceptionDetails.exception("jbst"), "tuple-exception-details-exception.json")
        );
    }

    @Override
    protected String getFolder() {
        return "jsons";
    }

    @ParameterizedTest
    @MethodSource("serializeTest")
    void serialize(TupleExceptionDetails tupleExceptionDetails, String fileName) {
        // Act
        var json = this.writeValueAsString(tupleExceptionDetails);

        // Assert
        assertThat(json).isEqualTo(TestsIOUtils.readFile(this.getFolder(), fileName));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("serializeTest")
    void deserializeTest(TupleExceptionDetails tupleExceptionDetails, String fileName) {
        // Arrange
        var json = TestsIOUtils.readFile(this.getFolder(), fileName);
        var typeReference = new TypeReference<TupleExceptionDetails>() {};

        // Act
        var tuple = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(tuple).isEqualTo(tupleExceptionDetails);
    }
}
