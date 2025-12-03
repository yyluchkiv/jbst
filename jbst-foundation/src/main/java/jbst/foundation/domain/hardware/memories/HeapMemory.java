package jbst.foundation.domain.hardware.memories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jbst.foundation.domain.hardware.bytes.ByteSize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static jbst.foundation.domain.constants.JbstConstants.MemoryUnits.BYTES_IN_MEGABYTE;
import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZeroByBounds;

// Lombok
@Getter
@EqualsAndHashCode
@ToString
public class HeapMemory {
    private final ByteSize initial;
    private final ByteSize used;
    private final ByteSize max;
    private final ByteSize committed;

    @JsonCreator
    public HeapMemory(
            @JsonProperty("initial") long initial,
            @JsonProperty("used") long used,
            @JsonProperty("max") long max,
            @JsonProperty("committed") long committed
    ) {
        this.initial = new ByteSize(initial);
        this.used = new ByteSize(used);
        this.max = new ByteSize(max);
        this.committed = new ByteSize(committed);
    }

    public static HeapMemory zeroUsage() {
        var zero = 0L;
        return new HeapMemory(
                zero,
                zero,
                zero,
                zero
        );
    }

    public static HeapMemory hardcoded() {
        return new HeapMemory(
                1073741824L,
                573741824L,
                1073741824L,
                1073741824L
        );
    }

    public static HeapMemory random() {
        return new HeapMemory(
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE,
                randomLongGreaterThanZeroByBounds(10, 500) * BYTES_IN_MEGABYTE
        );
    }
}
