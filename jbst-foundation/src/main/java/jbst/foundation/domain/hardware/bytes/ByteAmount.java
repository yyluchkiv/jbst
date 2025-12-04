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
public class ByteAmount {
    private final BigDecimal amount;
    private final ByteUnit unit;

    public ByteAmount(
            long bytes,
            ByteUnit unit
    ) {
        assertNonNullOrThrow(unit, invalidAttribute("ByteAmount.unit"));
        this.amount = new ByteSize(bytes).getBy(unit);
        this.unit = unit;
    }

    public static ByteAmount ofGb(
            long bytes
    ) {
        return new ByteAmount(
                bytes,
                ByteUnit.GIGABYTE
        );
    }

    public static ByteAmount ofMB(
            long bytes
    ) {
        return new ByteAmount(
                bytes,
                ByteUnit.MEGABYTE
        );
    }
}
