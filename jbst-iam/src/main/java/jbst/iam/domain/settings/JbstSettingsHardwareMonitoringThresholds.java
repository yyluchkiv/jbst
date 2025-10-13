package jbst.iam.domain.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jbst.foundation.domain.hardware.monitoring.HardwareName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

// Database
@JsonIgnoreProperties(ignoreUnknown = true)
// Properties
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
public class JbstSettingsHardwareMonitoringThresholds {
    private final boolean enabled;
    private Map<HardwareName, BigDecimal> values;

    public static JbstSettingsHardwareMonitoringThresholds hardcoded() {
        return new JbstSettingsHardwareMonitoringThresholds(
                true,
                new EnumMap<>(
                        Map.of(
                                HardwareName.CPU, new BigDecimal("80"),
                                HardwareName.HEAP, new BigDecimal("85"),
                                HardwareName.SERVER, new BigDecimal("90"),
                                HardwareName.SWAP, new BigDecimal("95"),
                                HardwareName.VIRTUAL, new BigDecimal("98")
                        )
                )
        );
    }
}
