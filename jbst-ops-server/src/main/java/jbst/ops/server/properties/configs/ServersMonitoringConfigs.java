package jbst.ops.server.properties.configs;

import jbst.foundation.domain.properties.annotations.MandatoryProperty;
import jbst.foundation.domain.properties.base.AbstractPropertyConfigs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.math.BigDecimal;
import java.time.ZoneId;

// Lombok (property-based)
@AllArgsConstructor(onConstructor = @__({@ConstructorBinding}))
@Data
@EqualsAndHashCode(callSuper = true)
public class ServersMonitoringConfigs extends AbstractPropertyConfigs {
    @MandatoryProperty
    private final ZoneId zoneId;
    @MandatoryProperty
    private final Boolean hideIP;
    @MandatoryProperty
    private final BigDecimal fileSystemFilter;
    @MandatoryProperty
    private final BigDecimal fileSystemThreshold;

    public boolean isHideIP() {
        return this.hideIP;
    }
}
