package jbst.foundation.domain.hardware.monitoring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static jbst.foundation.domain.numbers.JbstNumbers.is;
import static jbst.foundation.domain.random.JbstRandom.*;

// Lombok
@Getter
@EqualsAndHashCode(exclude = {
        "timestamp"
})
@ToString
public class JbstHardwareMonitoringDatapointTableRow {
    private final JbstHardwareName hardwareName;
    private final long timestamp;
    private final BigDecimal usage;
    private final String value;

    @JsonIgnore
    private final boolean thresholdReached;

    public JbstHardwareMonitoringDatapointTableRow(
            @NotNull JbstHardwareName hardwareName,
            long timestamp,
            @NotNull BigDecimal usage,
            @NotNull String value,
            @NotNull Map<JbstHardwareName, BigDecimal> thresholds
    ) {
        this.hardwareName = hardwareName;
        this.timestamp = timestamp;
        this.usage = usage;
        this.value = value;
        var threshold = thresholds.get(hardwareName);
        if (nonNull(threshold)) {
            this.thresholdReached = is(usage, ">", threshold);
        } else {
            this.thresholdReached = false;
        }
    }

    public static JbstHardwareMonitoringDatapointTableRow random() {
        return new JbstHardwareMonitoringDatapointTableRow(
                randomEnum(JbstHardwareName.class),
                randomLongGreaterThanZero(),
                randomBigDecimalGreaterThanZeroByBounds(10L, 20L),
                randomString(),
                Stream.of(JbstHardwareName.values()).collect(Collectors.toMap(
                        identity(),
                        entry -> randomBigDecimalGreaterThanZeroByBounds(50L, 100L)
                ))
        );
    }
}
