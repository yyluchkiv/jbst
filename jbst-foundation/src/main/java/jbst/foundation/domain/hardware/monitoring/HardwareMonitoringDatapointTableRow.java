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

import static java.util.function.Function.identity;
import static jbst.foundation.utilities.numbers.BigDecimalUtility.is;
import static jbst.foundation.utilities.random.RandomUtility.*;
import static org.springframework.util.CollectionUtils.isEmpty;

// Lombok
@Getter
@EqualsAndHashCode(exclude = {
        "timestamp"
})
@ToString
public class HardwareMonitoringDatapointTableRow {
    private final HardwareName hardwareName;
    private final long timestamp;
    private final BigDecimal usage;
    private final String value;

    @JsonIgnore
    private final boolean thresholdReached;

    public HardwareMonitoringDatapointTableRow(
            @NotNull HardwareName hardwareName,
            long timestamp,
            @NotNull BigDecimal usage,
            @NotNull String value,
            @NotNull Map<HardwareName, BigDecimal> thresholds
    ) {
        this.hardwareName = hardwareName;
        this.timestamp = timestamp;
        this.usage = usage;
        this.value = value;
        if (!isEmpty(thresholds) && thresholds.containsKey(hardwareName)) {
            this.thresholdReached = is(usage, ">", thresholds.get(hardwareName));
        } else {
            this.thresholdReached = false;
        }
    }

    public static HardwareMonitoringDatapointTableRow random() {
        return new HardwareMonitoringDatapointTableRow(
                randomEnum(HardwareName.class),
                randomLongGreaterThanZero(),
                randomBigDecimalGreaterThanZeroByBounds(10L, 20L),
                randomString(),
                Stream.of(HardwareName.values()).collect(Collectors.toMap(
                        identity(),
                        entry -> randomBigDecimal()
                ))
        );
    }
}
