package jbst.foundation.domain.tuples;

import jbst.foundation.domain.constants.JbstConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TupleSmartTimestampTest extends AbstractTupleTest {
    private static final TupleSmartTimestamp TUPLE = TupleSmartTimestamp.of(
            1668419401637L,
            JbstConstants.ZoneIds.UKRAINE,
            "dd.MM.yyyy HH:mm:ss"
    );

    @Override
    protected String getFileName() {
        return "tuple-smart-timestamp.json";
    }

    @Test
    void serializeTest() {
        // Act
        var json = this.writeValueAsString(TUPLE);

        // Assert
        Assertions.assertThat(json).isEqualTo(this.readFile());
    }

    // deserialization ignored deliberately
}
