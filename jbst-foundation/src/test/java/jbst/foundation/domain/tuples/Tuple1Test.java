package jbst.foundation.domain.tuples;

import tools.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Tuple1Test extends AbstractTupleTest {
    private static final Tuple1<String> TUPLE = new Tuple1<>("1st");

    @Override
    protected String getFileName() {
        return "tuple1.json";
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
        var typeReference = new TypeReference<Tuple1<String>>() {};

        // Act
        var tuple = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(tuple).isEqualTo(TUPLE);
    }
}
