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
public class JbstHeapMemory {
    private final JbstByteSize initial;
    private final JbstByteSize used;
    private final JbstByteSize max;
    private final JbstByteSize committed;

    @JsonCreator
    public JbstHeapMemory(
            @JsonProperty("initial") long initial,
            @JsonProperty("used") long used,
            @JsonProperty("max") long max,
            @JsonProperty("committed") long committed
    ) {
        this.initial = new JbstByteSize(initial);
        this.used = new JbstByteSize(used);
        this.max = new JbstByteSize(max);
        this.committed = new JbstByteSize(committed);
    }

    public static JbstHeapMemory zeroUsage() {
        var zero = 0L;
        return new JbstHeapMemory(
                zero,
                zero,
                zero,
                zero
        );
    }

    public static JbstHeapMemory hardcoded() {
        return new JbstHeapMemory(
                1073741824L,
                573741824L,
                1073741824L,
                1073741824L
        );
    }

    public static JbstHeapMemory random() {
        return new JbstHeapMemory(
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE
        );
    }
}
