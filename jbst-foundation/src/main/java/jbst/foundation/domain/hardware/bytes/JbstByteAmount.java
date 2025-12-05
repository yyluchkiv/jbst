package jbst.foundation.domain.hardware.bytes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

import static jbst.foundation.domain.asserts.JbstAsserts.assertNonNullOrThrow;
import static jbst.foundation.domain.strings.JbstMessages.invalidAttribute;

// JSON
// NOT used in serialization/deserialization
// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstByteAmount {
    private final BigDecimal amount;
    private final JbstByteUnit unit;

    public JbstByteAmount(
            long bytes,
            JbstByteUnit unit
    ) {
        assertNonNullOrThrow(unit, invalidAttribute("ByteAmount.unit"));
        this.amount = new JbstByteSize(bytes).getBy(unit);
        this.unit = unit;
    }

    public static JbstByteAmount ofGb(
            long bytes
    ) {
        return new JbstByteAmount(
                bytes,
                JbstByteUnit.GIGABYTE
        );
    }

    public static JbstByteAmount ofMB(
            long bytes
    ) {
        return new JbstByteAmount(
                bytes,
                JbstByteUnit.MEGABYTE
        );
    }
}
