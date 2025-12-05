package jbst.foundation.domain.hardware.memories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jbst.foundation.domain.hardware.bytes.JbstByteSize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static jbst.foundation.domain.constants.JbstConstants.MemoryUnits.BYTES_IN_MEGABYTE;
import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZeroByBounds;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class JbstGlobalMemory {
    private final JbstByteSize available;
    private final JbstByteSize total;
    private final JbstByteSize swapUsed;
    private final JbstByteSize swapTotal;
    private final JbstByteSize virtualUsed;
    private final JbstByteSize virtualTotal;

    @JsonCreator
    public JbstGlobalMemory(
            @JsonProperty("available") long available,
            @JsonProperty("total") long total,
            @JsonProperty("swapUsed") long swapUsed,
            @JsonProperty("swapTotal") long swapTotal,
            @JsonProperty("virtualUsed") long virtualUsed,
            @JsonProperty("virtualTotal") long virtualTotal
    ) {
        this.available = new JbstByteSize(available);
        this.total = new JbstByteSize(total);
        this.swapUsed = new JbstByteSize(swapUsed);
        this.swapTotal = new JbstByteSize(swapTotal);
        this.virtualUsed = new JbstByteSize(virtualUsed);
        this.virtualTotal = new JbstByteSize(virtualTotal);
    }

    public static JbstGlobalMemory zeroUsage() {
        var zero = 0L;
        return new JbstGlobalMemory(
                zero,
                zero,
                zero,
                zero,
                zero,
                zero
        );
    }

    public static JbstGlobalMemory hardcoded() {
        return new JbstGlobalMemory(
                1073741824L,
                1973741824L,
                1073741824L,
                1773741824L,
                1073741824L,
                1673741824L
        );
    }

    public static JbstGlobalMemory random() {
        return new JbstGlobalMemory(
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE
        );
    }
}
