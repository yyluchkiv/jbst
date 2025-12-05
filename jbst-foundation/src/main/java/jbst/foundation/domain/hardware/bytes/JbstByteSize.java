package jbst.foundation.domain.hardware.bytes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static jbst.foundation.domain.constants.JbstConstants.MemoryUnits.*;
import static jbst.foundation.domain.hardware.bytes.JbstByteUnit.*;
import static jbst.foundation.domain.numbers.JbstNumbers.scale;
import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZeroByBounds;

// Lombok
@Getter
@EqualsAndHashCode(exclude = { "mapping1", "mapping2" })
@ToString(exclude = { "mapping1", "mapping2" })
public final class JbstByteSize {
    private final Map<JbstByteUnit, Supplier<BigDecimal>> mapping1 = Map.of(
            KILOBYTE, () -> this.getKilobytes(1),
            MEGABYTE, () -> this.getMegabytes(1),
            GIGABYTE, () -> this.getGigabytes(4)
    );

    private final Map<JbstByteUnit, Function<Integer, BigDecimal>> mapping2 = Map.of(
            KILOBYTE, this::getKilobytes,
            MEGABYTE, this::getMegabytes,
            GIGABYTE, this::getGigabytes
    );

    @JsonValue
    private final long bytes;

    @JsonCreator
    public JbstByteSize(long bytes) {
        this.bytes = bytes;
    }

    public static JbstByteSize kilobyte() {
        return new JbstByteSize(BYTES_IN_KILOBYTE);
    }

    public static JbstByteSize megabyte() {
        return new JbstByteSize(BYTES_IN_MEGABYTE);
    }

    public static JbstByteSize gigabyte() {
        return new JbstByteSize(BYTES_IN_GIGABYTE);
    }

    public static JbstByteSize random() {
        return new JbstByteSize(randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE);
    }

    public BigDecimal getBy(JbstByteUnit unit) {
        return mapping1.get(unit).get();
    }

    public BigDecimal getBy(JbstByteUnit unit, int scale) {
        return mapping2.get(unit).apply(scale);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private BigDecimal getKilobytes(int scale) {
        var kb = BigDecimal.valueOf((double) this.bytes / BYTES_IN_KILOBYTE);
        return scale(kb, scale);
    }

    private BigDecimal getMegabytes(int scale) {
        var mb = BigDecimal.valueOf((double) this.bytes / BYTES_IN_MEGABYTE);
        return scale(mb, scale);
    }

    private BigDecimal getGigabytes(int scale) {
        var gb = BigDecimal.valueOf((double) this.bytes / BYTES_IN_GIGABYTE);
        return scale(gb, scale);
    }
}
