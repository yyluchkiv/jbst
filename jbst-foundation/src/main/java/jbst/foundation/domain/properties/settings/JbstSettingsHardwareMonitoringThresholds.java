package jbst.foundation.domain.properties.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jbst.foundation.domain.hardware.monitoring.HardwareName;
import jbst.foundation.domain.properties.annotations.MandatoryMapProperty;
import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.annotations.MandatoryToggleProperty;
import jbst.foundation.domain.properties.configs.AbstractTogglePropertiesConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

// Database
@JsonIgnoreProperties(ignoreUnknown = true)
// Properties
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class JbstSettingsHardwareMonitoringThresholds extends AbstractTogglePropertiesConfigs {
    @MandatoryProperty
    private final boolean enabled;
    @MandatoryToggleProperty
    @MandatoryMapProperty(propertyName = "values", keySetClass = HardwareName.class)
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

    @JsonIgnore
    @Override
    public boolean isParentPropertiesNode() {
        return true;
    }
}
