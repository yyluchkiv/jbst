package jbst.foundation.domain.hardware.monitoring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

import static jbst.foundation.domain.asserts.Asserts.assertNonNullOrThrow;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;
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
            HardwareName hardwareName,
            long timestamp,
            BigDecimal usage,
            String value,
            HardwareMonitoringThresholds thresholds
    ) {
        assertNonNullOrThrow(hardwareName, invalidAttribute("HardwareMonitoringDatapointTableRow.hardwareName"));
        assertNonNullOrThrow(usage, invalidAttribute("HardwareMonitoringDatapointTableRow.usage"));
        assertNonNullOrThrow(value, invalidAttribute("HardwareMonitoringDatapointTableRow.value"));
        assertNonNullOrThrow(thresholds, invalidAttribute("HardwareMonitoringDatapointTableRow.thresholds"));
        this.hardwareName = hardwareName;
        this.timestamp = timestamp;
        this.usage = usage;
        this.value = value;
        if (!isEmpty(thresholds.getThresholds()) && thresholds.getThresholds().containsKey(hardwareName)) {
            this.thresholdReached = is(usage, ">", thresholds.getThresholds().get(hardwareName).value());
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
                HardwareMonitoringThresholds.random()
        );
    }
}
