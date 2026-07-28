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
public class JbstCpuMemory {
    @JsonValue
    private final BigDecimal value;

    @JsonCreator
    public JbstCpuMemory(BigDecimal value) {
        this.value = scale(value, 2);
    }

    public static JbstCpuMemory zeroUsage() {
        return new JbstCpuMemory(
                BigDecimal.ZERO
        );
    }

    public static JbstCpuMemory fixed() {
        return new JbstCpuMemory(
                new BigDecimal("1.234")
        );
    }

    public static JbstCpuMemory random() {
        return new JbstCpuMemory(
                randomBigDecimalByBounds(1, 50)
        );
    }
}
