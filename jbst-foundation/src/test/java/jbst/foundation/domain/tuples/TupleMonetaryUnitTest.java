package jbst.foundation.domain.tuples;

import tools.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TupleMonetaryUnitTest extends AbstractTupleTest {
    private static final TupleMonetaryUnit TUPLE = new TupleMonetaryUnit(
            new BigDecimal("1.23"),
            new BigDecimal("4.56")
    );

    @Override
    protected String getFileName() {
        return "tuple-monetary-unit.json";
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
        var typeReference = new TypeReference<TupleMonetaryUnit>() {};

        // Act
        var tuple = OBJECT_MAPPER.readValue(json, typeReference);

        // Assert
        assertThat(tuple).isEqualTo(TUPLE);
    }
}
