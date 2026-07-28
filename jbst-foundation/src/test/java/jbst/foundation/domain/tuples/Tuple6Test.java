package jbst.foundation.domain.tuples;

import tools.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Tuple6Test extends AbstractTupleTest {
    private static final Tuple6<String, String, String, String, String, String> TUPLE = new Tuple6<>(
            "1st",
            "2nd",
            "3rd",
            "4th",
            "5th",
            "6th"
    );

    @Override
    protected String getFileName() {
        return "tuple6.json";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(TUPLE);

        // Assert
        assertThat(json).isEqualTo(this.readFile());
    }

    @SneakyThrows
    @Test
    void deserializeTest() {
        // Arrange
        var json = this.readFile();
        var typeReference = new TypeReference<Tuple6<String, String, String, String, String, String>>() {};

        // Act
        var tuple = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(tuple).isEqualTo(TUPLE);
    }
}
