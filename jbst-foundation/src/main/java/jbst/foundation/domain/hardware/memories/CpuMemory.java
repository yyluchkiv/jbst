package jbst.foundation.domain.hardware.memories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

import static jbst.foundation.domain.numbers.JbstNumbers.scale;
import static jbst.foundation.domain.random.JbstRandom.randomBigDecimalByBounds;

// Lombok
@SuppressWarnings("ALL")
@Getter
@EqualsAndHashCode
@ToString
public class CpuMemory {
    @JsonValue
    private final BigDecimal value;

    @JsonCreator
    public CpuMemory(BigDecimal value) {
        this.value = scale(value, 2);
    }

    public static CpuMemory zeroUsage() {
        return new CpuMemory(
                BigDecimal.ZERO
        );
    }

    public static CpuMemory hardcoded() {
        return new CpuMemory(
                new BigDecimal("1.234")
        );
    }

    public static CpuMemory random() {
        return new CpuMemory(
                randomBigDecimalByBounds(1, 50)
        );
    }
}
